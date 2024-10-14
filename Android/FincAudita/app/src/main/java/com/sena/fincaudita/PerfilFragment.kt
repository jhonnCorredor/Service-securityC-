package com.sena.fincaudita

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Person
import com.sena.fincaudita.Entity.User
import com.sena.fincaudita.components.DatePickerFragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PerfilFragment : Fragment() {
    private lateinit var etxUser: TextView
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtApellido: EditText
    private lateinit var txtTipoDocumento: Spinner
    private lateinit var txtNumeroDocumento: EditText
    private lateinit var txtFechaNacimiento: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtCiudad: AutoCompleteTextView
    private var listCitys = mutableListOf<String>()
    private var cityIdMap = HashMap<String, Int>()
    private var cityId: Int? = null
    private lateinit var userLogged: User
    private lateinit var person: Person
    private val PICK_FILE_REQUEST = 1
    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var imageView: ImageView
    private var selectedImageView: ImageView? = null
    private var evidenceImage: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        var image =  sharedPreferences?.getString("foto", null )
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val accordionCard1 = view.findViewById<CardView>(R.id.accordionCard1)
        val content1 = view.findViewById<LinearLayout>(R.id.accordionContent1)
        val arrowIcon1 = view.findViewById<ImageView>(R.id.arrowIcon1)
        val accordionCard2 = view.findViewById<CardView>(R.id.accordionCard2)
        val content2 = view.findViewById<LinearLayout>(R.id.accordionContent2)
        val arrowIcon2 = view.findViewById<ImageView>(R.id.arrowIcon2)
        txtNombre = view.findViewById(R.id.txtfirstNombre)
        txtApellido = view.findViewById(R.id.txtApellido)
        txtTipoDocumento = view.findViewById(R.id.txtTipoDocumento)
        txtNumeroDocumento = view.findViewById(R.id.txtNumeroDocumento)
        txtFechaNacimiento = view.findViewById(R.id.txtFechaNacimiento)
        txtTelefono = view.findViewById(R.id.txtTelefono)
        txtCorreo = view.findViewById(R.id.txtCorreo)
        txtDireccion = view.findViewById(R.id.txtDireccion)
        txtCiudad = view.findViewById(R.id.txtCiudad)

        accordionCard1.setOnClickListener {
            if (content1.visibility == View.GONE) {
                content1.visibility = View.VISIBLE
                arrowIcon1.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            } else {
                content1.visibility = View.GONE
                arrowIcon1.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            }
        }

        accordionCard2.setOnClickListener {
            if (content2.visibility == View.GONE) {
                content2.visibility = View.VISIBLE
                arrowIcon2.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            } else {
                content2.visibility = View.GONE
                arrowIcon2.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            }
        }
        etxUser = view.findViewById(R.id.textView14)
        txtUsername = view.findViewById(R.id.txtNombre)
        txtPassword = view.findViewById(R.id.txtPassword)
        imageView = view.findViewById(R.id.imageView5)
        val buttonCamara = view.findViewById<ImageButton>(R.id.buttonCamera)
        val imgTogglePassword = view.findViewById<ImageView>(R.id.imgTogglePassword)
        var isPasswordVisible = false
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)
        val btnActualizarPerson: Button = view.findViewById(R.id.btnActualizarPerson)

        buttonCamara.setOnClickListener {
            checkPermissionsAndOpenFileChooser(imageView)
        }

        if (image != null && image.isNotEmpty()) {
            val decodedString = Base64.decode(image, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            imageView.setImageBitmap(decodedByte)
        }
        imgTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                txtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgTogglePassword.setImageResource(R.drawable.eye_svgrepo_com)
            } else {
                txtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imgTogglePassword.setImageResource(R.drawable.eye_cancelled)
            }
            isPasswordVisible = !isPasswordVisible
            txtPassword.setSelection(txtPassword.text.length)
        }

        txtFechaNacimiento.setOnClickListener {
            showDatePickerDialog(txtFechaNacimiento)
        }

        cargar_citys { cityList, cityMap ->
            val userAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cityList
            )
            txtCiudad.setAdapter(userAdapter)

            txtCiudad.setOnItemClickListener { parent, _, position, _ ->
                val citySelected = parent.getItemAtPosition(position) as String
                cityId = cityMap[citySelected]
            }
        }

        loadUser()

        btnActualizar.setOnClickListener {
            var isValid = true
            val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (txtPassword.text.isEmpty() || !passwordPattern.matches(txtPassword.text.toString())) {
                txtPassword.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
                isValid = false
            }

            val usernamePattern = Regex("^(?=.*[a-zñ])(?=.*[A-ZÑ])[A-Za-zñÑ ]{4,10}$")
            if (txtUsername.text.isEmpty() || !usernamePattern.matches(txtUsername.text.toString())) {
                txtUsername.error = "El nombre de usuario debe tener entre 4 y 10 caracteres, con mayúsculas y minúsculas, sin números ni caracteres especiales"
                isValid = false
            }

            if (isValid) {
                val username = txtUsername.text.toString()
                val password = txtPassword.text.toString()
                userLogged.Username = username
                userLogged.Password = password
                updateUser(userLogged)
            }
        }

        btnActualizarPerson.setOnClickListener {
            if (validarCampos(txtNombre, txtApellido, txtNumeroDocumento, txtFechaNacimiento, txtTelefono, txtCorreo, txtDireccion, txtCiudad,)) {
                val nombre = txtNombre.text.toString()
                val apellido = txtApellido.text.toString()
                val tipoDocumento = txtTipoDocumento.selectedItem.toString()
                val numeroDocumento = txtNumeroDocumento.text.toString()
                val fechaNacimiento = txtFechaNacimiento.text.toString()
                val telefono = txtTelefono.text.toString()
                val correo = txtCorreo.text.toString()
                val direccion = txtDireccion.text.toString()

                val newPerson = Person(
                    Id = person.Id,
                    First_name = nombre,
                    Last_name = apellido,
                    Type_document = tipoDocumento,
                    Document = numeroDocumento,
                    Birth_of_date = fechaNacimiento,
                    Phone = telefono,
                    Email = correo,
                    Addres = direccion,
                    CityId = cityId ?: 0
                )

                updatePerson(newPerson)
            } else {
                Toast.makeText(context, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnVolver: ImageButton = view.findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        }

    }


    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        val minYear = calendar.get(Calendar.YEAR) - 70
        val minDate = Calendar.getInstance().apply {
            set(minYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis

        val maxYear = calendar.get(Calendar.YEAR) - 18
        val maxDate = Calendar.getInstance().apply {
            set(maxYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis

        val datePicker = DatePickerFragment({ day, month, year ->
            onDateSelected(day, month, year, editText)
        }, minDate, maxDate)

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, editText: EditText) {
        val formattedDay = if (day < 10) "0$day" else day.toString()
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
        editText.setText("$year-$formattedMonth-$formattedDay")
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
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(context, "Error al cargar citys: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUser(){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando perfil...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userID = sharedPreferences?.getInt("user_id", -1)
        if (userID != -1) {
            try {
            val request = JsonObjectRequest(
                Request.Method.GET,
                "${urls.urlUser}/$userID",
                null,
                { response ->
                    val username = response.getString("username")
                    etxUser.text = username
                    txtUsername.setText(username)
                    txtPassword.setText(response.getString("password"))
                    userLogged = User(
                        response.getInt("id"),
                        response.getString("username"),
                        response.getString("password"),
                        response.getInt("personId"))
                    loadPerson()
                    progressDialog.dismiss()
                },{error ->
                    progressDialog.dismiss()
                    val view: View = requireView()
                    Snackbar.make(view, "Error al cargar el usuario: ${error.message}", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        .show()
                }
            )
                val queue = Volley.newRequestQueue(context)
                queue.add(request)
            }catch (error: Exception){
                progressDialog.dismiss()
                val view: View = requireView()
                Snackbar.make(view, "Error al cargar el usuario: ${error.message}", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            }
        }
    }

    private fun loadPerson(){
            try {
                val request = JsonObjectRequest(
                    Request.Method.GET,
                    "${urls.urlPerson}/${userLogged.PersonId}",
                    null,
                    { response ->
                        cityId = response.getInt("cityId")
                        person = Person(
                            response.getInt("id")
                            ,response.getString("first_name")
                            ,response.getString("last_name")
                            ,response.getString("email")
                            ,response.getString("addres")
                            ,response.getString("phone")
                            ,response.getString("type_document")
                            ,response.getString("document")
                            ,response.getInt("cityId")
                            ,response.getString("birth_of_date")
                        )
                        val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        val date = dateFormatInput.parse(person.Birth_of_date)
                        txtFechaNacimiento.setText(dateFormatOutput.format(date))
                        txtNombre.setText(person.First_name)
                        txtApellido.setText(person.Last_name)
                        when(person.Type_document){
                            "Cédula de Ciudadanía" -> txtTipoDocumento.setSelection(0)
                            "Cédula de Extranjería" -> txtTipoDocumento.setSelection(1)
                            "Pasaporte" -> txtTipoDocumento.setSelection(2)
                        }
                        txtNumeroDocumento.setText(person.Document)
                        txtTelefono.setText(person.Phone)
                        txtCorreo.setText(person.Email)
                        txtDireccion.setText(person.Addres)

                        cityId?.let  {
                            val cityName = cityIdMap.filterValues { it == person.CityId }.keys.firstOrNull()
                            txtCiudad.setText(cityName)
                        }

                    },{error ->
                        val view: View = requireView()
                        Snackbar.make(view, "Error al cargar datos personales: ${error.message}", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            .show()
                    }
                )
                val queue = Volley.newRequestQueue(context)
                queue.add(request)
            }catch (error: Exception){
                val view: View = requireView()
                Snackbar.make(view, "Error al cargar datos personales: ${error.message}", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            }
    }

    private fun updateUser(user: User) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando perfil...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("foto", evidenceImage)
        editor?.apply()
        val roleID = sharedPreferences?.getInt("role_id", -1)
        try {
            val params = JSONObject().apply {
                put("username", user.Username)
                put("password", user.Password)
                put("personId", user.PersonId)
                put("id", user.Id)
                val role = JSONObject().apply {
                    put("id", roleID)
                }
                val rolesArray = JSONArray().apply {
                    put(role)
                }
                put("roles", rolesArray)
            }

            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlUser,
                params,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                    builder.setMessage("Datos actualizados exitosamente.")
                    builder.setCancelable(false)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                },
                { error ->
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()

                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(errorTitle)
                        builder.setMessage("Error al actualizar los datos: ${error.message}")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
                    } else {
                        progressDialog.dismiss()

                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(successTitle)
                        builder.setMessage("Datos actualizados exitosamente.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
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
            builder.setMessage("Error al actualizar el usuario: ${error.message}")
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    private fun updatePerson(person: Person) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando datos personales...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject().apply {
                put("id", person.Id)
                put("first_name", person.First_name)
                put("last_name", person.Last_name)
                put("email", person.Email)
                put("addres", person.Addres)
                put("phone", person.Phone)
                put("type_document", person.Type_document)
                put("document", person.Document)
                put("cityId", person.CityId)
                put("birth_of_date", person.Birth_of_date)
            }

            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlPerson,
                params,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                    builder.setMessage("Datos actualizados exitosamente.")
                    builder.setCancelable(false)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                },
                { error ->
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()

                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(errorTitle)
                        builder.setMessage("Error al actualizar los datos: ${error.message}")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
                    } else {
                        progressDialog.dismiss()

                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(successTitle)
                        builder.setMessage("Datos actualizados exitosamente.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
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
            builder.setMessage("Error al actualizar los datos personales: ${error.message}")
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    private fun checkPermissionsAndOpenFileChooser(imageView: ImageView) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            openFileChooser(imageView)
        }
    }

    private fun openFileChooser(imageView: ImageView) {
        selectedImageView = imageView
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileName(uri)
                val base64 = uriToBase64(uri)
                if (base64!=null){
                            evidenceImage = base64
                }

                selectedImageView?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(it)
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser(selectedImageView!!)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            val fileSize = getFileSize(uri)
            if (fileSize > 1 * 1024 * 1024) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
                val byteArrayOutputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream?.use { stream ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (stream.read(buffer).also { len = it } != -1) {
                        byteArrayOutputStream.write(buffer, 0, len)
                    }
                }
                val bytes = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return try {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            var size = 0L
            cursor?.use {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    size = cursor.getLong(sizeIndex)
                }
            }
            size
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }


    private fun validarCampos(
        txtNombre: EditText,
        txtApellido: EditText,
        txtNumeroDocumento: EditText,
        txtFechaNacimiento: EditText,
        txtTelefono: EditText,
        txtCorreo: EditText,
        txtDireccion: EditText,
        txtCiudad: AutoCompleteTextView,
    ): Boolean {
        var isValid = true

        if(txtFechaNacimiento.text.isEmpty()){
            txtFechaNacimiento.error = "Ingresa una fecha válida"
            isValid = false
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val birthDate = sdf.parse(txtFechaNacimiento.text.toString())
                if (birthDate != null) {
                    val currentDate = Calendar.getInstance()
                    val birthCalendar = Calendar.getInstance().apply {
                        time = birthDate
                    }
                    var age = currentDate.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                    if (currentDate.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    if (age < 18) {
                        txtFechaNacimiento.error = "Debes ser mayor de 18 años"
                        isValid = false
                    }else{
                        txtFechaNacimiento.error = null
                    }
                } else {
                    txtFechaNacimiento.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                    isValid = false
                }
            } catch (e: ParseException) {
                txtFechaNacimiento.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                isValid = false
            }
        }


        if (txtNombre.text.isEmpty() || txtNombre.text.length > 25 || txtNombre.text.length < 3) {
            txtNombre.error = "El nombre es obligatorio, debe tener mínimo 3 caracteres y máximo 25 caracteres"
            isValid = false
        }

        if (txtApellido.text.isEmpty() || txtApellido.text.length > 25 || txtApellido.text.length < 3) {
            txtApellido.error = "El apellido es obligatorio, debe tener mínimo 3 caracteres y máximo 25 caracteres"
            isValid = false
        }

        if (txtNumeroDocumento.text.isEmpty() || txtNumeroDocumento.text.length > 10 || txtNumeroDocumento.text.length < 8) {
            txtNumeroDocumento.error = "El número de documento debe tener entre 8 y 10 dígitos"
            isValid = false
        }

        if (txtTelefono.text.isEmpty() || txtTelefono.text.length > 10 || txtTelefono.text.length < 9) {
            txtTelefono.error = "El número de teléfono debe tener entre 9 y 10 dígitos"
            isValid = false
        }

        val direccionPattern = Regex("^(Calle|Carrera|Transversal)\\s[\\w#-]+.*$", RegexOption.IGNORE_CASE)
        if (txtDireccion.text.isEmpty() || !direccionPattern.matches(txtDireccion.text.toString())) {
            txtDireccion.error = "La dirección debe comenzar con Calle, Carrera o Transversal y seguir el formato adecuado"
            isValid = false
        }

        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@(gmail|hotmail)\\.com$")
        if (txtCorreo.text.isEmpty() || !emailPattern.matches(txtCorreo.text.toString())) {
            txtCorreo.error = "Ingresa un correo válido (@gmail.com o @hotmail.com)"
            isValid = false
        }

        if (txtCiudad.text.isEmpty()) {
            txtCiudad.error = "La ciudad es obligatoria"
            isValid = false
        }

        if(cityId == null){
            txtCiudad.error = "Seleccione una ciudad"
            isValid = false
        }

        return isValid
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}