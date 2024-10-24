package com.sena.fincaudita.SignUp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Person
import com.sena.fincaudita.Entity.User
import com.sena.fincaudita.MainActivity
import com.sena.fincaudita.R
import com.sena.fincaudita.components.DatePickerFragment
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Signup1Fragment : Fragment() {
    private var listCitys = mutableListOf<String>()
    private var cityIdMap = HashMap<String, Int>()
    private var cityId: Int? = null
    private lateinit var checbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAtras: Button = view.findViewById(R.id.btnAtras)
        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)
        val txtSingIn: TextView = view.findViewById(R.id.txtSignIn)
        val txtNombre: EditText = view.findViewById(R.id.txtNombre)
        val txtApellido: EditText = view.findViewById(R.id.txtApellido)
        val txtTipoDocumento: Spinner = view.findViewById(R.id.txtTipoDocumento)
        val txtNumeroDocumento: EditText = view.findViewById(R.id.txtNumeroDocumento)
        val txtFechaNacimiento: EditText = view.findViewById(R.id.txtFechaNacimiento)
        val txtTelefono: EditText = view.findViewById(R.id.txtTelefono)
        val txtCorreo: EditText = view.findViewById(R.id.txtCorreo)
        val txtDireccion: EditText = view.findViewById(R.id.txtDireccion)
        val txtCiudad: AutoCompleteTextView = view.findViewById(R.id.txtCiudad)
        val txtContrasena: EditText = view.findViewById(R.id.txtPassword)
        checbox = view.findViewById(R.id.checkbox_remember)
        val imgTogglePassword = view.findViewById<ImageView>(R.id.imgTogglePassword)
        var isPasswordVisible = false

        checbox.setOnCheckedChangeListener(null)

        imgTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                txtContrasena.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgTogglePassword.setImageResource(R.drawable.eye_svgrepo_com)
            } else {
                txtContrasena.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imgTogglePassword.setImageResource(R.drawable.eye_cancelled)
            }
            isPasswordVisible = !isPasswordVisible
            txtContrasena.setSelection(txtContrasena.text.length)
        }

        checbox.setOnClickListener {
            if (checbox.isChecked) {
                checbox.isChecked = false
                showTermsDialog ()
            } else {
                checbox.isChecked = false
            }
        }

        btnAtras.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        txtFechaNacimiento.setOnClickListener {
            showDatePickerDialog(txtFechaNacimiento)
        }

        btnSiguiente.setOnClickListener {
            if (validarCampos(txtNombre, txtApellido, txtNumeroDocumento, txtFechaNacimiento, txtTelefono, txtCorreo, txtDireccion, txtCiudad, txtContrasena, checbox)) {
                val typeDocument = txtTipoDocumento.selectedItemId.toInt()
                var type = ""
                when (typeDocument) {
                    0 -> type="CC"
                    1 -> type="TI"
                    2 -> type="CE"
                    3 -> type="PS"
                    else -> null
                }
                val person = Person( 0,
                txtNombre.text.toString(),
                txtApellido.text.toString(),
                txtCorreo.text.toString(),
                txtDireccion.text.toString(),
                txtTelefono.text.toString(),
                type,
                txtNumeroDocumento.text.toString(),
                cityId!!,
                txtFechaNacimiento.text.toString())
                val user = User(0, txtNombre.text.toString() + txtApellido.text.toString(), txtContrasena.text.toString(), 0)
                guardarPerson(person, user)
            } else {
                val view: View = requireView()
                Snackbar.make(view, "Por favor completa todos los campos.", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
            }
        }

        txtSingIn.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
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

    private fun guardarPerson(person: Person, user: User){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Creando cuenta...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = originalFormat.parse(person.Birth_of_date)
            val formattedDate = targetFormat.format(date)

            val params = JSONObject().apply {
                put("first_name", person.First_name)
                put("last_name", person.Last_name)
                put("email", person.Email)
                put("addres", person.Addres)
                put("phone", person.Phone)
                put("type_document", person.Type_document)
                put("document", person.Document)
                put("cityId", person.CityId)
                put("birth_of_date", formattedDate)
            }
            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlPerson,
                params,
                { response ->
                    val paramsUser = JSONObject().apply {
                        put("username", user.Username)
                        put("password", user.Password)
                        put("personId", response.getInt("id"))
                        val role = JSONObject().apply {
                            put("id", 3)
                        }
                        val rolesArray = JSONArray().apply {
                            put(role)
                        }
                        put("roles", rolesArray)
                    }
                    val requestUser = JsonObjectRequest(
                        Request.Method.POST,
                        urls.urlUser,
                        paramsUser,
                        {response ->
                            progressDialog.dismiss()
                            val view: View = requireView()
                            Snackbar.make(view, "Registro Guardado Exitosamente", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                .show()
                            val nuevoFragmento = Signup2Fragment.newInstance()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView2, nuevoFragmento)
                                .addToBackStack(null)
                                .commit()
                        }, { error ->
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    val queue = Volley.newRequestQueue(context)
                    queue.add(requestUser)
                },
                { error ->
                    progressDialog.dismiss()
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                    builder.setCancelable(false)
                    builder.setMessage("Error al crear el usuario. \nError: ${error}")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        }catch (error: Exception){
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
            builder.setCancelable(false)
            builder.setMessage("Error al crear el usuario. \nError: ${error}")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
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
        txtContrasena: EditText,
        checbox: CheckBox
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
            txtCorreo.error = "Ingresa un correo válido (gmail.com o hotmail.com)"
            isValid = false
        }

        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (txtContrasena.text.isEmpty() || !passwordPattern.matches(txtContrasena.text.toString())) {
            txtContrasena.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
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

        if (!checbox.isChecked) {
            val view: View = requireView()
            Snackbar.make(view, "Debes aceptar los términos y condiciones.", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
            isValid = false
        }

        return isValid
    }

    private fun showTermsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Términos y Condiciones")
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.finca_audita_terms))
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            checbox.isChecked = true
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            checbox.isChecked = false
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            Signup1Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}