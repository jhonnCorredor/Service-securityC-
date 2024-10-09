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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sena.fincaudita.R

class CodePassword : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_code_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val btnAtras: Button = view.findViewById(R.id.btnAtras)
        val txtCodigo: EditText = view.findViewById(R.id.txtCodigo)
        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)

        btnAtras.setOnClickListener {
            val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.remove("user_id")
            editor?.remove("code")
            editor?.apply()

            val nuevoFragmento = emailPassword.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, nuevoFragmento)
                .addToBackStack(null)
                .commit()
        }

        btnSiguiente.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("Verificando código...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val Codigo = txtCodigo.text.toString()

            if(Codigo.isEmpty()){
                progressDialog.dismiss()
                txtCodigo.error = "Ingrese un código."
                return@setOnClickListener
            }
            val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val codigoAlmacenado = sharedPreferences?.getString("code", null)
            if (Codigo == codigoAlmacenado) {
                progressDialog.dismiss()
                val nuevoFragmento = changePassword.newInstance()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView2, nuevoFragmento)
                    .addToBackStack(null)
                    .commit()
            } else {
                progressDialog.dismiss()
                AlertDialog.Builder(requireContext())
                    .setTitle("Código Incorrecto")
                    .setMessage("El código ingresado es incorrecto. Por favor, inténtelo de nuevo.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            CodePassword().apply {
                arguments = Bundle().apply {
                }
            }
    }
}