package com.sena.fincaudita.ChangePassword

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.User
import com.sena.fincaudita.R
import org.json.JSONArray
import org.json.JSONObject

class changePassword : Fragment() {

    private lateinit var userLogged: User
    private var roleId: Int = 0

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

        loadUser()

        val btnAtras: Button = view.findViewById(R.id.btnAtras)
        val txtPassword: EditText = view.findViewById(R.id.txtPassword)
        val txtNewPassword: EditText = view.findViewById(R.id.txtNewPassword)
        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)
        var isPasswordVisible = false
        var isNewPasswordVisible = false
        val imgTogglePassword = view.findViewById<ImageView>(R.id.imgTogglePassword)
        val imgToggleNewPassword = view.findViewById<ImageView>(R.id.imgNewTogglePassword)

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

        imgToggleNewPassword.setOnClickListener {
            if (isNewPasswordVisible) {
                txtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgToggleNewPassword.setImageResource(R.drawable.eye_svgrepo_com)
            } else {
                txtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imgToggleNewPassword.setImageResource(R.drawable.eye_cancelled)
            }
            isNewPasswordVisible = !isNewPasswordVisible
            txtNewPassword.setSelection(txtNewPassword.text.length)
        }

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
                        .setCancelable(false)
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
                        var roles = response.getJSONArray("roles")
                        if (roles.length() > 0) {
                            val firstRole = roles.getJSONObject(0)
                            roleId = firstRole.getInt("id")

                        }
                        userLogged = User(
                            response.getInt("id"),
                            response.getString("username"),
                            response.getString("password"),
                            response.getInt("personId"))
                    },{error ->
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
                val view: View = requireView()
                Snackbar.make(view, "Error al cargar el usuario: ${error.message}", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .show()
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
                    put("id", roleId)
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
                        val view: View = requireView()
                        Snackbar.make(view, "Datos actualizados exitosamente", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            .show()
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
                        val view: View = requireView()
                        Snackbar.make(view, "Datos actualizados exitosamente", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            .show()
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
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                        val builder = android.app.AlertDialog.Builder(requireContext())
                        builder.setTitle(errorTitle)
                        builder.setCancelable(false)
                        builder.setMessage("Error al actualizar contraseña. \nError: ${error}")
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
                    } else {
                        progressDialog.dismiss()
                        val view: View = requireView()
                        Snackbar.make(view, "Datos actualizados exitosamente", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            .show()
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
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
            builder.setCancelable(false)
            builder.setMessage("Error al actualizar contraseña. \nError: ${error}")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
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