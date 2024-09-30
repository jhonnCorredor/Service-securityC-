package com.sena.fincaudita.Supplie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Supplie
import com.sena.fincaudita.R
import org.json.JSONObject
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color


private const val ARG_SUPPLIE = "supplie"

class FormSupplieFragment : Fragment() {

    private var supplie: Supplie? = null
    private var edit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            supplie = it.getParcelable(ARG_SUPPLIE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_supplie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val txtCode: TextView = view.findViewById(R.id.txtCode)
        val txtNombreProducto: TextView = view.findViewById(R.id.txtNombreProducto)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcion)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecio)
        val btnCrear: Button = view.findViewById(R.id.btnCrear)
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
        val btnVolver: ImageButton = view.findViewById(R.id.btnVolver)

        if (supplie != null) {
            txtCode.text = supplie!!.Code
            txtNombreProducto.text = supplie!!.Name
            txtDescripcion.text = supplie!!.Description
            txtPrecio.text = supplie!!.Price.toString()
            txtCode.isEnabled = false
            txtNombreProducto.isEnabled = false
            txtDescripcion.isEnabled = false
            txtPrecio.isEnabled = false
            btnCrear.visibility = View.GONE
            btnActualizar.visibility = View.VISIBLE
            btnEliminar.visibility = View.VISIBLE
        } else {
            btnCrear.visibility = View.VISIBLE
            btnActualizar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        }

        btnCrear.setOnClickListener {
            if (!validarCampos(txtCode, txtNombreProducto, txtDescripcion, txtPrecio)) {
                return@setOnClickListener
            }

            val code = txtCode.text.toString()
            val nombreProducto = txtNombreProducto.text.toString()
            val descripcion = txtDescripcion.text.toString()
            val precio = txtPrecio.text.toString().toDouble()

            val newSupplie = Supplie(
                0,
                nombreProducto,
                descripcion,
                code,
                precio
            )
            saveSupplie(newSupplie)
        }

        btnActualizar.setOnClickListener {
            if (!edit) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmar Edición")
                    .setMessage("¿Desea editar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        txtCode.isEnabled = true
                        txtNombreProducto.isEnabled = true
                        txtDescripcion.isEnabled = true
                        txtPrecio.isEnabled = true

                        edit = true
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            } else {
                if (!validarCampos(txtCode, txtNombreProducto, txtDescripcion, txtPrecio)) {
                    return@setOnClickListener
                }

                val code = txtCode.text.toString()
                val nombreProducto = txtNombreProducto.text.toString()
                val descripcion = txtDescripcion.text.toString()
                val precio = txtPrecio.text.toString().toDouble()

                val updatedSupplie = supplie?.copy(
                    Name = nombreProducto,
                    Description = descripcion,
                    Price = precio,
                    Code = code
                )

                if (updatedSupplie != null) {
                    val confirmUpdateDialog = AlertDialog.Builder(requireContext())
                    confirmUpdateDialog.setTitle("Confirmar Actualización")
                        .setMessage("¿Está seguro de que desea actualizar el registro?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            updateSupplie(updatedSupplie)
                            txtCode.isEnabled = false
                            txtNombreProducto.isEnabled = false
                            txtDescripcion.isEnabled = false
                            txtPrecio.isEnabled = false
                        }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }

        btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro de que desea eliminar este registro?")
                .setPositiveButton("Sí") { dialog, _ ->
                    supplie?.let {
                        deleteSupplie(it)
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun saveSupplie(supplie: Supplie) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando insumo...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject()
            params.put("name", supplie.Name)
            params.put("description", supplie.Description)
            params.put("price", supplie.Price)
            params.put("code", supplie.Code)

            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlSupplies,
                params,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro Guardado Exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                },
                { error ->
                    progressDialog.dismiss()

                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                        .setMessage("Error: ${error.message}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
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
                .setMessage("Error al guardar el supplie: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun updateSupplie(supplie: Supplie) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val params = JSONObject()
            params.put("id", supplie.Id)
            params.put("name", supplie.Name)
            params.put("description", supplie.Description)
            params.put("price", supplie.Price)
            params.put("code", supplie.Code)

            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlSupplies,
                params,
                { response ->
                    progressDialog.dismiss()

                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    edit = false
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro Actualizado Exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                },
                { error ->
                    val builder = AlertDialog.Builder(requireContext())
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()

                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        builder.setTitle(errorTitle)
                            .setMessage("Error al actualizar el supplie: ${error.message}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        edit = false
                        builder.setTitle(successTitle)
                            .setMessage("Registro Actualizado Exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
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
                .setMessage("Error al actualizar el supplie: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun deleteSupplie(supplie: Supplie) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Eliminando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.DELETE,
                "${urls.urlSupplies}/${supplie.Id}",
                null,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro Eliminado Exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },
                { error ->
                    val builder = AlertDialog.Builder(requireContext())
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                        builder.setTitle(errorTitle)
                            .setMessage("Error al eliminar el supplie: ${error.message}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                        builder.setTitle(successTitle)
                            .setMessage("Registro Eliminado Exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                            .create()
                            .show()
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
                .setMessage("Error al eliminar el supplie: ${error.message}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }


    private fun validarCampos(
        txtCode: TextView,
        txtNombre: TextView,
        txtDescripcion: TextView,
        txtPrecio: TextView
    ): Boolean {
        var isValid = true

        if (txtCode.text.isEmpty()) {
            txtCode.error = "El código es obligatorio"
            isValid = false
        }

        if (txtNombre.text.isEmpty()) {
            txtNombre.error = "El nombre es obligatorio"
            isValid = false
        }

        if (txtDescripcion.text.isEmpty()) {
            txtDescripcion.error = "La descripción es obligatoria"
            isValid = false
        }

        if (txtPrecio.text.isEmpty()) {
            txtPrecio.error = "El precio es obligatorio"
            isValid = false
        } else {
            try {
                val precio = txtPrecio.text.toString().toDouble()
                if (precio <= 0) {
                    txtPrecio.error = "El precio debe ser mayor a cero"
                    isValid = false
                } else {
                    txtPrecio.error = null
                }
            } catch (e: NumberFormatException) {
                txtPrecio.error = "Ingresa un precio válido"
                isValid = false
            }
        }
        return isValid
    }

    companion object {
        private const val ARG_SUPPLIE = "supplie"

        @JvmStatic
        fun newInstance(supplie: Supplie?) =
            FormSupplieFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUPPLIE, supplie)
                }
            }
    }
}