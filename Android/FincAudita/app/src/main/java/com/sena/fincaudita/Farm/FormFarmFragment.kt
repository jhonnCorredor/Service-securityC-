package com.sena.fincaudita.Farm

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Farm
import com.sena.fincaudita.Entity.Lot
import com.sena.fincaudita.R
import org.json.JSONArray
import org.json.JSONObject

class FormFarmFragment : Fragment() {
    private var listCitys = mutableListOf<String>()
    private var cityIdMap = HashMap<String, Int>()
    private var cityId: Int? = null
    private var listUsers = mutableListOf<String>()
    private var userIdMap = HashMap<String, Int>()
    private var userId: Int? = null
    private var listCrops = mutableListOf<String>()
    private var cropIdMap = HashMap<String, Int>()
    private var selectedCropIds = mutableListOf<Int>()
    private var farm: Farm? = null
    private lateinit var txtHectareas: EditText
    private var lots: MutableList<Lot>? = null
    private var edit = false
    private var roleId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            farm = it.getParcelable(ARG_FARM)
            lots = it.getParcelableArrayList(ARG_LOTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_farm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        roleId = sharedPreferences?.getInt("role_id", -1)
        //userId = sharedPreferences?.getInt("user_id", -1)

        val txtCityId = view.findViewById<AutoCompleteTextView>(R.id.txtCityId)
        val txtUserId = view.findViewById<AutoCompleteTextView>(R.id.txtUserId)
        val txtCropId = view.findViewById<MultiAutoCompleteTextView>(R.id.txtCropId)
        val txtFarmName = view.findViewById<EditText>(R.id.txtName)
        val txtDimension = view.findViewById<EditText>(R.id.txtDimension)
        val txtAddress = view.findViewById<EditText>(R.id.txtAddress)
        val cardUser = view.findViewById<CardView>(R.id.cardUserId)
        txtHectareas = view.findViewById(R.id.txtHectarea)
        val btnGuardar = view.findViewById<Button>(R.id.btnCrear)
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
        val btnVolver = view.findViewById<ImageButton>(R.id.btnVolver)
        val txtRegistrarFinca = view.findViewById<TextView>(R.id.txtRegistrarFinca)

        if (lots != null && lots!!.isNotEmpty()) {
            txtHectareas.setText(lots!![0].numHectareas.toString())
        }

        cargar_citys { cityList, cityMap ->
            val cityAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cityList
            )
            txtCityId.setAdapter(cityAdapter)
            if(farm != null){
                val city = cityIdMap.filterValues { it == farm!!.CityId }.keys.firstOrNull()
                txtCityId.setText(city)
            }

            txtCityId.setOnItemClickListener { parent, _, position, _ ->
                val citySelected = parent.getItemAtPosition(position) as String
                cityId = cityMap[citySelected]
            }
        }

            cargar_users { userList, userMap ->
                val userAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    userList
                )
                txtUserId.setAdapter(userAdapter)
                if (farm != null) {
                    val user = userIdMap.filterValues { it == farm!!.UserId }.keys.firstOrNull()
                    txtUserId.setText(user)
                }
                txtUserId.setOnItemClickListener { parent, _, position, _ ->
                    val userSelected = parent.getItemAtPosition(position) as String
                    userId = userMap[userSelected]
                }
            }


        cargar_crops { cropList, cropMap ->
            val cropAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cropList
            )
            txtCropId.setAdapter(cropAdapter)
            txtCropId.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            txtCropId.setOnItemClickListener { parent, _, position, _ ->
                val cropSelected = parent.getItemAtPosition(position) as String
                val cropId = cropMap[cropSelected]

                if (cropId != null && !selectedCropIds.contains(cropId)) {
                    selectedCropIds.add(cropId)
                }
            }

            lots?.let { lotList ->
                selectedCropIds = lotList.map { it.CropId }.toMutableList()
                val selectedCrops = selectedCropIds.map { cropIdMap.filterValues { id -> id == it }.keys.firstOrNull() }.joinToString(", ")
                txtCropId.setText(selectedCrops)
            }

            txtCropId.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val currentSelectedCrops = s.toString().split(",").map { it.trim() }

                    val iterator = selectedCropIds.iterator()
                    while (iterator.hasNext()) {
                        val cropId = iterator.next()
                        val cropName = cropMap.entries.find { it.value == cropId }?.key
                        if (cropName != null && !currentSelectedCrops.contains(cropName)) {
                            iterator.remove()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        if (farm != null) {
            txtRegistrarFinca.setText("Detalle Finca")
            txtFarmName.setText(farm!!.Name)
            txtDimension.setText(farm!!.Dimension.toString())
            txtAddress.setText(farm!!.Addres)
            cityId = farm!!.CityId
            userId = farm!!.UserId
            val selectedCrops = selectedCropIds.map { cropIdMap.filterValues { id -> id == it }.keys.firstOrNull() }.joinToString(", ")
            txtCropId.setText(selectedCrops)
            txtCityId.isEnabled = false
            txtUserId.isEnabled = false
            txtCropId.isEnabled = false
            txtFarmName.isEnabled = false
            txtDimension.isEnabled = false
            txtHectareas.isEnabled = false
            txtAddress.isEnabled = false
            if(roleId == 1) {
                btnGuardar.visibility = View.GONE
                btnActualizar.visibility = View.VISIBLE
                btnEliminar.visibility = View.VISIBLE
            }else{
                btnGuardar.visibility = View.GONE
                btnActualizar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            }
        }else{
            if(roleId == 1){
                cardUser.visibility = View.VISIBLE
            }else{
                cardUser.visibility = View.GONE
            }
            btnGuardar.visibility = View.VISIBLE
            btnActualizar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        }

        btnGuardar.setOnClickListener {
            if (validarCampos(txtCityId, txtUserId, txtCropId, txtFarmName, txtDimension, txtHectareas, txtAddress)) {
                val newFarm = Farm(
                    farm?.Id ?: 0,
                    txtFarmName.text.toString(),
                    cityId!!,
                    userId!!,
                    txtAddress.text.toString(),
                    txtDimension.text.toString().toInt()
                )
                saveFarm(newFarm, selectedCropIds)
            } else {
                val view: View = requireView()
                Snackbar.make(view, "Por favor, complete todos los campos obligatorios", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            }
        }

        btnActualizar.setOnClickListener {
            if (farm == null) {
                val view: View = requireView()
                Snackbar.make(view, "No hay registro para actualizar", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            } else if (!edit) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmar Edición")
                    .setCancelable(false)
                    .setMessage("¿Desea editar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        txtCityId.isEnabled = true
                        txtUserId.isEnabled = true
                        txtCropId.isEnabled = true
                        txtFarmName.isEnabled = true
                        txtDimension.isEnabled = true
                        txtHectareas.isEnabled = true
                        txtAddress.isEnabled = true
                        edit = true
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            } else {
                if (!validarCampos(txtCityId, txtUserId, txtCropId, txtFarmName, txtDimension, txtHectareas, txtAddress)) {
                    return@setOnClickListener
                }

                val updatedFarm = Farm(
                    farm!!.Id,
                    txtFarmName.text.toString(),
                    cityId!!,
                    userId!!,
                    txtAddress.text.toString(),
                    txtDimension.text.toString().toInt()
                )

                val confirmUpdateDialog = AlertDialog.Builder(requireContext())
                confirmUpdateDialog.setTitle("Confirmar Actualización")
                    .setCancelable(false)
                    .setMessage("¿Está seguro de que desea actualizar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        updateFarm(updatedFarm, selectedCropIds)
                        txtCityId.isEnabled = false
                        txtUserId.isEnabled = false
                        txtCropId.isEnabled = false
                        txtFarmName.isEnabled = false
                        txtDimension.isEnabled = false
                        txtHectareas.isEnabled = false
                        txtAddress.isEnabled = false
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        btnEliminar.setOnClickListener {
            if (farm == null) {
                val view: View = requireView()
                Snackbar.make(view, "No hay registro para eliminar", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            } else {
                val confirmDeleteDialog = AlertDialog.Builder(requireContext())
                confirmDeleteDialog.setTitle("Confirmar Eliminación")
                    .setCancelable(false)
                    .setMessage("¿Está seguro de que desea eliminar este registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        deleteFarm(farm!!)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun saveFarm(farm: Farm, crops: MutableList<Int>) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando finca...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val lots = JSONArray()
            for (i in 0 until crops.size) {
                val lotParams = JSONObject().apply {
                    put("num_hectareas", txtHectareas.text.toString().toInt())
                    put("cropId", crops[i])
                }
                lots.put(lotParams)
            }

            val params = JSONObject().apply {
                put("name", farm.Name)
                put("cityId", farm.CityId)
                put("userId", farm.UserId)
                put("addres", farm.Addres)
                put("dimension", farm.Dimension)
                put("lots", lots)
            }

            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlFarm,
                params,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setCancelable(false)
                        .setMessage("Granja guardada exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },
                { error ->
                    progressDialog.dismiss()
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                        .setCancelable(false)
                        .setMessage("Error: ${error}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(request)

        } catch (error: Exception) {
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
                .setCancelable(false)
                .setMessage("Error al guardar la granja: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }


    private fun updateFarm(farm: Farm, crops: MutableList<Int>) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val lots = JSONArray()
            for (i in 0 until crops.size) {
                val lotParams = JSONObject().apply {
                    put("num_hectareas", txtHectareas.text.toString().toInt())
                    put("cropId", crops[i])
                }
                lots.put(lotParams)
            }

            val params = JSONObject().apply {
                put("id", farm.Id)
                put("name", farm.Name)
                put("cityId", farm.CityId)
                put("userId", farm.UserId)
                put("addres", farm.Addres)
                put("dimension", farm.Dimension)
                put("lots", lots)
            }

            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlFarm,
                params,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    edit = false

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setCancelable(false)
                        .setMessage("Granja actualizada exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },
                { error ->
                    val builder = AlertDialog.Builder(requireContext())
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        builder.setTitle(errorTitle)
                            .setCancelable(false)
                            .setMessage("Error al actualizar la finca: ${error}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        edit = false

                        builder.setTitle(successTitle)
                            .setCancelable(false)
                            .setMessage("Registro actualizado exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                            .create()
                            .show()
                    }
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
                .setCancelable(false)
                .setMessage("Error al actualizar la granja: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun deleteFarm(farm: Farm) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Eliminando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.DELETE,
                "${urls.urlFarm}/${farm.Id}",
                null,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setCancelable(false)
                        .setMessage("Registro eliminado exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },
                { error ->
                    val builder = AlertDialog.Builder(requireContext())
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        builder.setTitle(errorTitle)
                            .setCancelable(false)
                            .setMessage("Error al eliminar la finca: ${error}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                        builder.setTitle(successTitle)
                            .setCancelable(false)
                            .setMessage("Registro eliminado exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                            .create()
                            .show()
                    }
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(request)

        } catch (error: Exception) {
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
                .setCancelable(false)
                .setMessage("Error al eliminar la finca: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun cargar_citys(onComplete: (List<String>, HashMap<String, Int>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlCity,
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val nombre = item.getString("name")
                        listCitys.add(nombre)
                        cityIdMap[nombre] = id
                    }
                    onComplete(listCitys, cityIdMap)
                },
                { error ->
                    val view: View = requireView()
                    Snackbar.make(view, "Error: ${error.message}", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            val view: View = requireView()
            Snackbar.make(view, "Error al cargar ciudades: ${error.message}", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }

    private fun cargar_users(onComplete: (List<String>, HashMap<String, Int>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                "${urls.urlUser}/byRole/3",
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val nombre = item.getString("username")
                        listUsers.add(nombre)
                        userIdMap[nombre] = id
                    }
                    onComplete(listUsers, userIdMap)
                },
                { error ->
                    val view: View = requireView()
                    Snackbar.make(view, "Error: ${error.message}", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            val view: View = requireView()
            Snackbar.make(view, "Error al cargar usuarios: ${error.message}", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }

    private fun cargar_crops(onComplete: (List<String>, HashMap<String, Int>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlCrop,
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val nombre = item.getString("name")
                        listCrops.add(nombre)
                        cropIdMap[nombre] = id
                    }
                    onComplete(listCrops, cropIdMap)
                },
                { error ->
                    val view: View = requireView()
                    Snackbar.make(view, "Error: ${error.message}", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            val view: View = requireView()
            Snackbar.make(view, "Error al cargar cultivos: ${error.message}", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }

    private fun validarCampos(
        txtCityId: AutoCompleteTextView,
        txtUserId: AutoCompleteTextView,
        txtCropId: MultiAutoCompleteTextView,
        txtFarmName: EditText,
        txtDimension: EditText,
        txtHectareas: EditText,
        txtAddress: EditText
    ): Boolean {
        var isValid = true

        if (txtFarmName.text.isEmpty()) {
            txtFarmName.error = "El nombre de la finca es obligatorio"
            isValid = false
        } else if (txtFarmName.text.length < 3) {
            txtFarmName.error = "El nombre debe tener al menos 3 caracteres"
            isValid = false
        } else if (txtFarmName.text.length > 15) {
            txtFarmName.error = "El nombre no debe exceder los 15 caracteres"
            isValid = false
        } else {
            txtFarmName.error = null
        }

        if (txtAddress.text.isEmpty()) {
            txtAddress.error = "La dirección es obligatoria"
            isValid = false
        } else if (txtAddress.text.length < 3) {
            txtAddress.error = "La dirección debe tener al menos 3 caracteres"
            isValid = false
        } else if (txtAddress.text.length > 15) {
            txtAddress.error = "La dirección no debe exceder los 15 caracteres"
            isValid = false
        } else {
            txtAddress.error = null
        }


        if (txtDimension.text.isEmpty()) {
            txtDimension.error = "La dimensión es obligatoria"
            isValid = false
        } else {
            try {
                val dimension = txtDimension.text.toString().toInt()
                if (dimension <= 0) {
                    txtDimension.error = "La dimensión debe ser mayor a cero"
                    isValid = false
                } else if (dimension > 200) {
                    txtDimension.error = "La dimensión no debe exceder los 200 hectáreas"
                    isValid = false
                } else {
                    txtDimension.error = null
                }
            } catch (e: NumberFormatException) {
                txtDimension.error = "Ingresa una dimensión válida"
                isValid = false
            }
        }


        if (cityId == null) {
            txtCityId.error = "La ciudad es obligatoria"
            isValid = false
        } else {
            txtCityId.error = null
        }

        if (userId == null) {
            txtUserId.error = "El usuario es obligatorio"
            isValid = false
        } else {
            txtUserId.error = null
        }

        if (selectedCropIds.isEmpty()) {
            txtCropId.error = "Se debe seleccionar al menos un cultivo"
            isValid = false
        } else {
            txtCropId.error = null
        }

        if (txtHectareas.text.isEmpty()) {
            txtHectareas.error = "El tamaño de los lotes es obligatorio"
            isValid = false
        } else {
            try {
                val hectareas = txtHectareas.text.toString().toDouble()
                val dimension = txtDimension.text.toString().toDouble()
                if (hectareas > dimension) {
                    txtHectareas.error = "El tamaño de los lotes no puede ser mayor que la dimensión"
                    isValid = false
                } else {
                    txtHectareas.error = null
                }
            } catch (e: NumberFormatException) {
                txtHectareas.error = "Ingresa un tamaño válido"
                isValid = false
            }
        }

        return isValid
    }

    companion object {
        const val ARG_FARM = "farm"
        const val ARG_LOTS = "lots"

        @JvmStatic
        fun newInstance(farm: Farm?, lots: MutableList<Lot>? = null) =
            FormFarmFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_FARM, farm)
                    putParcelableArrayList(ARG_LOTS, lots as ArrayList<Lot>?)
                }
            }
    }
}
