package com.sena.fincaudita

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.ChangePassword.changePasswordActivity
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.SignUp.SignUpActivity
import org.json.JSONObject

class loginFragment : Fragment() {

    private lateinit var checkBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtUser: EditText = view.findViewById(R.id.txtUser)
        val txtPassword: EditText = view.findViewById(R.id.txtPassword)
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val imgTogglePassword = view.findViewById<ImageView>(R.id.imgTogglePassword)
        var isPasswordVisible = false

        val txtCambiarContraseña: TextView = view.findViewById(R.id.text_forgot_password)
        val txtCrearCuenta: TextView = view.findViewById(R.id.text_create_account)
        checkBox = view.findViewById(R.id.checkbox_remember)

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

        txtCambiarContraseña.setOnClickListener{
            val intent = Intent(activity, changePasswordActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        txtCrearCuenta.setOnClickListener{
            val intent = Intent(activity, SignUpActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        btnLogin.setOnClickListener {
            val user = txtUser.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
            if (user.isEmpty() && password.isEmpty()){
                txtUser.error = "El campo usuario es obligatorio"
                txtPassword.error = "El campo contraseña es obligatorio"
            }else{
                if (user.isEmpty()) {
                    txtUser.error = "El campo usuario es obligatorio"
                    txtUser.requestFocus()
                } else if (password.isEmpty() || !passwordPattern.matches(password)) {
                    txtPassword.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
                    txtPassword.requestFocus()
                } else {
                    login(user, password)
                }
            }
        }
    }

    fun login(user: String, password: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Iniciando sesión...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject()
            params.put("username", user)
            params.put("password", password)

            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlLogin,
                params,
                { response ->

                    val menu = response.getJSONArray("menu")
                    if (menu.length() > 0) {
                        val userObject = menu.getJSONObject(0)
                        val userID = userObject.getInt("userID")
                        val roleID = userObject.getInt("roleID")

                        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.putInt("role_id", roleID)
                        editor?.putInt("user_id", userID)
                        editor?.putBoolean("remember_me", checkBox.isChecked)
                        editor?.apply()

                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(successTitle)
                        builder.setMessage("Inicio de sesión exitoso.")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            val intent = Intent(activity, MenuActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }

                        progressDialog.dismiss()
                        builder.create().show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                    builder.setMessage("Error al iniciar sesión. \nErro: ${error}")
                    builder.setCancelable(false)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                }
            )

            val queue = Volley.newRequestQueue(this.context)
            queue.add(request)

        } catch (error: Exception) {
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
            builder.setMessage("Error al iniciar sesión: ${error}")
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            loginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}