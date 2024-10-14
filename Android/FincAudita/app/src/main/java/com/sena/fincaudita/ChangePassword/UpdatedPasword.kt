package com.sena.fincaudita.ChangePassword

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.sena.fincaudita.MainActivity
import com.sena.fincaudita.R

class UpdatedPasword : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_updated_pasword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnSiguiente: Button = view.findViewById(R.id.btnSiguiente)

        btnSiguiente.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            UpdatedPasword().apply {
                arguments = Bundle().apply {
                }
            }
    }
}