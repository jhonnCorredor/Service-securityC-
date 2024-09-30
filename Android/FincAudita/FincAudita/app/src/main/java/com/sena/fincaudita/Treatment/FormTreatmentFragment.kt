package com.sena.fincaudita.Treatment

import GenericAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Farm
import com.sena.fincaudita.Entity.Lot
import com.sena.fincaudita.Entity.LotTreatment
import com.sena.fincaudita.Entity.SupplieTreatment
import com.sena.fincaudita.Entity.Treatment
import com.sena.fincaudita.R
import com.sena.fincaudita.components.DatePickerFragment
import org.json.JSONArray
import org.json.JSONObject
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormTreatmentFragment : Fragment() {
    private var listSupplie = mutableListOf<String>()
    private var supplieIdMap = HashMap<String, Int>()
    private var lotsMap = HashMap<Farm, List<Lot>>()
    private var listLotsId = mutableListOf<Int>()
    private var supplieId: Int? = null
    private val inputsList = mutableListOf<Pair<String, String>>()
    private lateinit var inputsAdapter: GenericAdapter<Pair<String, String>>
    private var treatment: Treatment? = null
    private var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            treatment = it.getParcelable("treatment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_treatment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val btnVolver = view.findViewById<ImageButton>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val txtTipo = view.findViewById<Spinner>(R.id.txtTipo)
        val txtMezcla = view.findViewById<EditText>(R.id.txtMezcla)
        val txtFarm = view.findViewById<AutoCompleteTextView>(R.id.txtFarm)
        val txtLot = view.findViewById<MultiAutoCompleteTextView>(R.id.txtlot)
        val txtInput = view.findViewById<AutoCompleteTextView>(R.id.txtInput)
        val txtDose = view.findViewById<EditText>(R.id.txtDose)
        val btnAddInput = view.findViewById<ImageButton>(R.id.btnAddInput)
        val btnCrear = view.findViewById<Button>(R.id.btnCrear)
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
        val recyclerViewInputs = view.findViewById<RecyclerView>(R.id.recyclerViewInputs)
        val txtFecha = view.findViewById<EditText>(R.id.txtFecha)

        recyclerViewInputs.layoutManager = LinearLayoutManager(context)
        inputsAdapter = GenericAdapter(
            items = inputsList,
            layoutResId = R.layout.item_supplie,
            bindView = { itemView, item ->
                val txtInputName = itemView.findViewById<TextView>(R.id.insumo)
                val txtInputDose = itemView.findViewById<TextView>(R.id.dose)
                val btnEliminar = itemView.findViewById<ImageButton>(R.id.eliminar)
                txtInputName.text = item.first
                txtInputDose.text = "Dosis: ${item.second} (L/ha)"
                btnEliminar.setOnClickListener {
                    inputsList.remove(item)
                    updateListView()
                }
            }
        )
        recyclerViewInputs.adapter = inputsAdapter

        if (treatment != null) {
            btnCrear.visibility = View.GONE
            btnActualizar.visibility = View.VISIBLE
            btnEliminar.visibility = View.VISIBLE

            treatment?.lotList?.forEach { lotTreatment ->
                val (beforeColon, afterColon) = lotTreatment.lot.split(":", limit = 2)
                txtFarm.setText(beforeColon.trim())
                txtLot.setText(txtLot.text.toString() + afterColon.trim() + ", ")
            }

            txtTipo.setSelection(getSpinnerIndex(txtTipo, treatment!!.typeTreatment))
            txtMezcla.setText(treatment!!.quantityMix)
            txtFecha.setText(treatment!!.dateTreatment)
            txtTipo.isEnabled = false
            txtMezcla.isEnabled = false
            txtFarm.isEnabled = false
            txtLot.isEnabled = false
            txtFecha.isEnabled = false
            txtInput.isEnabled = false
            txtDose.isEnabled = false
            loadExistingData()
        } else {
            btnCrear.visibility = View.VISIBLE
            btnActualizar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        }

        btnCrear.setOnClickListener {
            if (validarCamposGuardarActualizar(txtTipo, txtMezcla, txtFarm, txtLot, txtFecha, inputsList)) {
                createTreatment(txtTipo, txtMezcla, txtFecha)
            }
        }

        btnActualizar.setOnClickListener {
            if (treatment == null) {
                Toast.makeText(requireContext(), "No hay registro para actualizar", Toast.LENGTH_SHORT).show()
            } else if (!edit) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmar Edición")
                    .setMessage("¿Desea editar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        txtTipo.isEnabled = true
                        txtMezcla.isEnabled = true
                        txtFarm.isEnabled = true
                        txtLot.isEnabled = true
                        txtFecha.isEnabled = true
                        txtInput.isEnabled = true
                        txtDose.isEnabled = true
                        edit = true
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            } else {
                if (!validarCamposGuardarActualizar(txtTipo, txtMezcla, txtFarm, txtLot, txtFecha, inputsList)) {
                    return@setOnClickListener
                }

                val confirmUpdateDialog = AlertDialog.Builder(requireContext())
                confirmUpdateDialog.setTitle("Confirmar Actualización")
                    .setMessage("¿Está seguro de que desea actualizar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        treatment?.let { updateTreatment(it) }
                        txtTipo.isEnabled = false
                        txtMezcla.isEnabled = false
                        txtFarm.isEnabled = false
                        txtLot.isEnabled = false
                        txtFecha.isEnabled = false
                        txtInput.isEnabled = false
                        txtDose.isEnabled = false
                        edit = false
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        btnEliminar.setOnClickListener {
            if (treatment == null) {
                Toast.makeText(requireContext(), "No hay registro para eliminar", Toast.LENGTH_SHORT).show()
            } else {
                val confirmDeleteDialog = AlertDialog.Builder(requireContext())
                confirmDeleteDialog.setTitle("Confirmar Eliminación")
                    .setMessage("¿Está seguro de que desea eliminar este registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        deleteTreatment(treatment!!)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        btnAddInput.setOnClickListener {
            val inputName = txtInput.text.toString()
            val dose = txtDose.text.toString()

            if(txtInput.text.isEmpty()){
                txtInput.error = "Ingrese un insumo."
            }

            if(txtDose.text.isEmpty()){
                txtDose.error = "Ingrese dosis."
            }

            if (inputName.isNotBlank() && dose.isNotBlank()) {
                inputsList.add(Pair(inputName, dose))
                updateListView()
                txtInput.text.clear()
                txtDose.text.clear()
            } else {
                txtInput.error = "Ingrese un insumo."
                txtDose.error = "Ingrese dosis."
            }
        }

        txtFecha.setOnClickListener {
            showDatePickerDialog(txtFecha)
        }

        cargar_supplies { supplieList, cityMap ->
            val supplieAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                supplieList
            )
            txtInput.setAdapter(supplieAdapter)

            txtInput.setOnItemClickListener { parent, _, position, _ ->
                val supplieSelected = parent.getItemAtPosition(position) as String
                supplieId = cityMap[supplieSelected]
            }
        }

        cargar_farms { farmMap ->
            val farmAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                farmMap.keys.map { it.Name }
            )
            txtFarm.setAdapter(farmAdapter)

            txtFarm.setOnItemClickListener { parent, _, position, _ ->
                val selectedFarmName = parent.getItemAtPosition(position) as String
                val selectedFarm = farmMap.keys.find { it.Name == selectedFarmName }

                selectedFarm?.let {
                    val associatedLots = farmMap[it] ?: emptyList()
                    val crops = associatedLots.map { lot -> lot.Cultivo }

                    val cropAdapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        crops
                    )
                    txtLot.setAdapter(cropAdapter)
                    txtLot.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                    listLotsId = associatedLots.map { it.Id }.toMutableList()

                    val selectedCrops = associatedLots.map { it.Cultivo }.joinToString(", ")
                    txtLot.setText(selectedCrops)

                    txtLot.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val currentSelectedCrops = s.toString().split(",").map { it.trim() }
                            val iterator = listLotsId.iterator()

                            while (iterator.hasNext()) {
                                val lotId = iterator.next()
                                val cropName = associatedLots.find { it.Id == lotId }?.Cultivo
                                if (cropName != null && !currentSelectedCrops.contains(cropName)) {
                                    iterator.remove()
                                }
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
            }
        }
    }

    private fun loadExistingData() {
        treatment?.supplieList?.forEach { supply ->
            inputsList.add(Pair(supply.supplie, supply.dose))
        }
        updateListView()

        treatment?.lotList?.forEach { lotTreatment ->
            listLotsId.add(lotTreatment.lotId)
        }
    }
    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0
    }

    private fun createTreatment(txtTipo: Spinner, txtMezcla: EditText, txtFecha: EditText) {
        val tipo = txtTipo.selectedItem.toString()
        val mezcla = txtMezcla.text.toString()
        val date = txtFecha.text.toString()
        val lots = mutableListOf<LotTreatment>()
        val supplies = mutableListOf<SupplieTreatment>()

        if (tipo.isBlank() || mezcla.isBlank() || date.isBlank() || inputsList.isEmpty() || listLotsId.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val treatment = Treatment(0, date, tipo, mezcla, lots, supplies)
        saveTreatment(treatment)
    }

    private fun updateListView() {
        inputsAdapter.updateData(inputsList)
        inputsAdapter.notifyDataSetChanged()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(day, month, year, editText)
        }
        datePicker.show(parentFragmentManager, "datapicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, editText: EditText) {
        val formattedDay = if (day < 10) "0$day" else day.toString()
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
        editText.setText("$year-$formattedMonth-$formattedDay")
    }

    private fun cargar_supplies(onComplete: (List<String>, HashMap<String, Int>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlSupplies,
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val nombre = item.getString("name")
                        listSupplie.add(nombre)
                        supplieIdMap[nombre] = id
                    }
                    onComplete(listSupplie, supplieIdMap)
                },
                { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(context, "Error al cargar insumos: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargar_farms(onComplete: (HashMap<Farm, List<Lot>>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlFarm,
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val lotArray = item.getJSONArray("lots")
                        val listLot = mutableListOf<Lot>()
                        for (j in 0 until lotArray.length()) {
                            val lotItem = lotArray.getJSONObject(j)
                            val lot = Lot(
                                lotItem.getInt("id"),
                                lotItem.getInt("cropId"),
                                lotItem.getInt("num_hectareas"),
                                lotItem.getString("cultivo")
                            )
                            listLot.add(lot)
                        }
                        val farm = Farm(
                            item.getInt("id"),
                            item.getString("name"),
                            item.getInt("cityId"),
                            item.getInt("userId"),
                            item.getString("addres"),
                            item.getInt("dimension")
                        )
                        lotsMap[farm] = listLot
                    }
                    onComplete(lotsMap)
                },
                { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(context, "Error al cargar insumos: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTreatment(treatment: Treatment) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando tratamiento...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val lots = JSONArray()
            for (i in 0 until listLotsId.size) {
                val lotParams = JSONObject().apply {
                    put("lotId", listLotsId[i])
                }
                lots.put(lotParams)
            }

            val supplies = JSONArray()
            for (i in 0 until inputsList.size) {
                val supplyParams = JSONObject().apply {
                    val supplieName = inputsList[i].first
                    val supplieId = supplieIdMap[supplieName]
                    put("suppliesId", supplieId)
                    put("dose", "${inputsList[i].second} ")
                }
                supplies.put(supplyParams)
            }

            val params = JSONObject().apply {
                put("dateTreatment", treatment.dateTreatment)
                put("typeTreatment", treatment.typeTreatment)
                put("quantityMix", "${treatment.quantityMix} ")
                put("supplieList", supplies)
                put("lotList", lots)
            }

            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlTreatment,
                params,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Tratamiento guardado exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                },
                { error ->
                    progressDialog.dismiss()

                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                        .setMessage("Error: ${error.message}")
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
                .setMessage("Error al guardar: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun updateTreatment(treatment: Treatment) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val lots = JSONArray()
            for (i in 0 until listLotsId.size) {
                val lotParams = JSONObject().apply {
                    put("lotId", listLotsId[i])
                }
                lots.put(lotParams)
            }

            val supplies = JSONArray()
            for (i in 0 until inputsList.size) {
                val supplyParams = JSONObject().apply {
                    val supplieName = inputsList[i].first
                    val supplieId = supplieIdMap[supplieName]
                    put("suppliesId", supplieId)
                    put("dose", "${inputsList[i].second} ")
                }
                supplies.put(supplyParams)
            }

            val params = JSONObject().apply {
                put("id", treatment.id)
                put("dateTreatment", treatment.dateTreatment)
                put("typeTreatment", treatment.typeTreatment)
                put("quantityMix", "${treatment.quantityMix}")
                put("supplieList", supplies)
                put("lotList", lots)
            }

            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlTreatment,
                params,
                { response ->
                    progressDialog.dismiss()

                    edit = false
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro actualizado exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
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
                            .setMessage("Error al actualizar el tratamiento: ${error.message}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()

                        edit = false
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        builder.setTitle(successTitle)
                            .setMessage("Registro actualizado Exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
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
                .setMessage("Error al actualizar: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun deleteTreatment(treatment: Treatment) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Eliminando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.DELETE,
                "${urls.urlTreatment}/${treatment.id}",
                null,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro eliminado Exitosamente")
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
                            .setMessage("Error al eliminar el tratamiento: ${error.message}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()

                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        builder.setTitle(successTitle)
                            .setMessage("Registro eliminado Exitosamente")
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
                .setMessage("Error al eliminar: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun validarCamposGuardarActualizar(
        txtTipo: Spinner,
        txtMezcla: EditText,
        txtFarm: AutoCompleteTextView,
        txtLot: MultiAutoCompleteTextView,
        txtFecha: EditText,
        inputsList: List<Pair<String, String>>
    ): Boolean {
        var isValid = true

        if (txtTipo.selectedItem == null) {
            Toast.makeText(context, "Selecciona un tipo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (txtMezcla.text.isEmpty()) {
            txtMezcla.error = "La mezcla es obligatoria"
            isValid = false
        }

        if (txtFarm.text.isEmpty()) {
            txtFarm.error = "Selecciona una granja"
            isValid = false
        }

        if (txtLot.text.isEmpty()) {
            txtLot.error = "Selecciona al menos un lote"
            isValid = false
        }else{
            txtLot.error = null
        }

        if (txtFecha.text.isEmpty()) {
            txtFecha.error = "Ingresa una fecha válida"
            isValid = false
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(txtFecha.text.toString())
                if (date != null) {
                    val currentDate = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val sixMonthsLater = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        add(Calendar.MONTH, 6)
                    }

                    if (date.before(currentDate.time)) {
                        txtFecha.error = "La fecha debe ser hoy o futura"
                        isValid = false
                    } else if (date.after(sixMonthsLater.time)) {
                        txtFecha.error = "La fecha no puede ser más de 6 meses en el futuro"
                        isValid = false
                    } else {
                        txtFecha.error = null
                    }
                } else {
                    txtFecha.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                    isValid = false
                }
            } catch (e: ParseException) {
                txtFecha.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                isValid = false
            }
        }

        if (inputsList.isEmpty()) {
            Toast.makeText(context, "Debes agregar al menos un insumo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance(treatment: Treatment?) =
            FormTreatmentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("treatment", treatment)
                }
            }
    }
}