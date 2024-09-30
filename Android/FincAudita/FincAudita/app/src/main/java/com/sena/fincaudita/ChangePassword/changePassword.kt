package com.sena.fincaudita.ChangePassword

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.User
import com.sena.fincaudita.R
import org.json.JSONArray
import org.json.JSONObject

class changePassword : Fragment() {

    private lateinit var userLogged: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        loadUser()

        val btnAtras: Button = view.findViewById(R.id.btnAtras)
        val txtPassword: EditText = view.findViewById(R.id.txtPassword)
        val txtNewPassword: EditText = view.findViewById(R.id.txtNewPassword)
        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)

        btnAtras.setOnClickListener {
            val nuevoFragmento = CodePassword.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, nuevoFragmento)
                .addToBackStack(null)
                .commit()
        }

        btnSiguiente.setOnClickListener {
            var isValid = true

            val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (txtPassword.text.isEmpty() || !passwordPattern.matches(txtPassword.text.toString())) {
                txtPassword.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
                isValid = false
            }
            if(txtNewPassword.text.isEmpty()){
                txtNewPassword.error = "Ingrese la nueva contraseña."
                isValid = false
            }
            if(isValid) {
                val password = txtPassword.text.toString()
                val newPassword = txtNewPassword.text.toString()
                if (password == newPassword) {
                    updateUser(newPassword)
                } else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Error")
                        .setMessage("Las contraseñas no coinciden")
                        .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
    }

    private fun loadUser(){
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userID = sharedPreferences?.getInt("user_id", -1)
        if (userID != -1) {
            try {
                val request = JsonObjectRequest(
                    Request.Method.GET,
                    "${urls.urlUser}/$userID",
                    null,
                    { response ->
                        userLogged = User(
                            response.getInt("id"),
                            response.getString("username"),
                            response.getString("password"),
                            response.getInt("personId"))
                    },{error ->
                        Toast.makeText(context, "Error al cargar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
                val queue = Volley.newRequestQueue(context)
                queue.add(request)
            }catch (error: Exception){
                Toast.makeText(context, "Error al cargar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser(password: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cambiando contraseña...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject().apply {
                put("username", userLogged.Username)
                put("password", password)
                put("personId", userLogged.PersonId)
                put("id", userLogged.Id)
                val role = JSONObject().apply {
                    put("id", 1)
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
                    if (response == null || response.length() == 0) {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                        val nuevoFragmento = UpdatedPasword.newInstance()
                        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.remove("user_id")
                        editor?.remove("code")
                        editor?.apply()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView2, nuevoFragmento)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.remove("user_id")
                        editor?.remove("code")
                        editor?.apply()
                        val nuevoFragmento = UpdatedPasword.newInstance()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView2, nuevoFragmento)
                            .addToBackStack(null)
                            .commit()
                    }
                },
                { error ->
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Error al actualizar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.remove("user_id")
                        editor?.remove("code")
                        editor?.apply()
                        val nuevoFragmento = UpdatedPasword.newInstance()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView2, nuevoFragmento)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            progressDialog.dismiss()
            error.printStackTrace()
            Toast.makeText(context, "Error al cargar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            changePassword().apply {
                arguments = Bundle().apply {
                }
            }
    }
}