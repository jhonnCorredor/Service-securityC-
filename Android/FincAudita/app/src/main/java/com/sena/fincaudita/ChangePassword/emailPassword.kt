package com.sena.fincaudita.ChangePassword

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.MainActivity
import com.sena.fincaudita.R
import org.json.JSONObject

class emailPassword : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_password, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)
        val txtEmail = view.findViewById<EditText>(R.id.txtEmail)
        val btnAtras: Button = view.findViewById(R.id.btnAtras)

        btnAtras.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        btnSiguiente.setOnClickListener {
            val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@(gmail|hotmail)\\.com$")
            if (txtEmail.text.isEmpty() || !emailPattern.matches(txtEmail.text.toString())) {
                txtEmail.error = "Ingresa un correo válido (@gmail.com o @hotmail.com)"
            }else{
                val email = txtEmail.text.toString()
                recoveryEmail(email)
            }
        }

    }

    private fun recoveryEmail(email: String){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Verificando correo...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject()
            params.put("email", email)

            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlRecovery,
                params,
                { response ->
                    val r = response
                    val item = response.getJSONObject("data").getJSONObject("result")
                    val userId = item.getInt("id")
                    val code = item.getString("code")
                    val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences?.edit()
                    editor?.putInt("user_id", userId)
                    editor?.putString("code",code)
                    editor?.apply()
                    val view: View = requireView()
                    Snackbar.make(view, "Correo Enviado", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        .show()
                    progressDialog.dismiss()
                    val nuevoFragmento = CodePassword.newInstance()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, nuevoFragmento)
                        .addToBackStack(null)
                        .commit()
                },{ error ->
                    progressDialog.dismiss()
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                    builder.setCancelable(false)
                    builder.setMessage("Error al consultar el usuario. \nError: ${error}")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                }
            )
            request.retryPolicy = DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
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
            builder.setMessage("Error al consultar el usuario. \nError: ${error}")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            emailPassword().apply {
                arguments = Bundle().apply {
                }
            }
    }
}