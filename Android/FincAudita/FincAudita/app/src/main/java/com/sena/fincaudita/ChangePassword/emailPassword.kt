package com.sena.fincaudita.ChangePassword

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.MainActivity
import com.sena.fincaudita.R
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [emailPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class emailPassword : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_password, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)
        val txtEmail = view.findViewById<EditText>(R.id.txtEmail)
        val btnAtras: Button = view.findViewById(R.id.btnAtras)

        btnAtras.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        btnSiguiente.setOnClickListener {
            val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@gmail.com$")
            if (txtEmail.text.isEmpty() || !emailPattern.matches(txtEmail.text.toString())) {
                txtEmail.error = "Ingresa un correo de Gmail válido"
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
                    Toast.makeText(context, "Correo Enviado", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    val nuevoFragmento = CodePassword.newInstance()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, nuevoFragmento)
                        .addToBackStack(null)
                        .commit()
                },{ error ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Correo no registrado",
                        Toast.LENGTH_SHORT
                    ).show()
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
            Toast.makeText(
                context,
                "Error al realizar la consulta: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
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