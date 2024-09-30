package com.sena.fincaudita

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.android.volley.toolbox.JsonObjectRequest
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.User
import org.json.JSONArray
import org.json.JSONObject
import android.text.style.ForegroundColorSpan
import android.text.SpannableString



class PerfilFragment : Fragment() {
    private lateinit var etxUser: TextView
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
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
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        etxUser = view.findViewById(R.id.textView14)
        txtUsername = view.findViewById(R.id.txtNombre)
        txtPassword = view.findViewById(R.id.txtPassword)
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)

        loadUser()

        btnActualizar.setOnClickListener {
            var isValid = true
            val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (txtPassword.text.isEmpty() || !passwordPattern.matches(txtPassword.text.toString())) {
                txtPassword.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
                isValid = false
            }

            if (txtUsername.text.isEmpty()) {
                txtUsername.error = "El nombre de usuario es obligatorio"
                isValid = false
            }

            if(isValid) {
                val username = txtUsername.text.toString()
                val password = txtPassword.text.toString()
                userLogged.Username = username;
                userLogged.Password = password;
                updateUser(userLogged)
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
                    progressDialog.dismiss()
                },{error ->
                    progressDialog.dismiss()
                    Toast.makeText(context, "Error al cargar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
                val queue = Volley.newRequestQueue(context)
                queue.add(request)
            }catch (error: Exception){
                progressDialog.dismiss()
                Toast.makeText(context, "Error al cargar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser(user: User) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando perfil...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject().apply {
                put("username", user.Username)
                put("password", user.Password)
                put("personId", user.PersonId)
                put("id", user.Id)
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
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                    builder.setMessage("Datos actualizados exitosamente.")
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
            builder.setMessage("Error al cargar el usuario: ${error.message}")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
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