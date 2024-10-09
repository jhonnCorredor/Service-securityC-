package com.sena.fincaudita.Supplie

import GenericAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Supplie
import com.sena.fincaudita.R

class SupplieFragment : Fragment() {

    private var supplies = mutableListOf<Supplie>()
    private lateinit var adapter: GenericAdapter<Supplie>
    private var roleId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supplie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        roleId = sharedPreferences?.getInt("role_id", -1)

        adapter = GenericAdapter(
            items = supplies,
            layoutResId = R.layout.item_recycler,
            bindView = { view, supplie ->
                val name: TextView = view.findViewById(R.id.title1)
                val price: TextView = view.findViewById(R.id.title2)
                val description: TextView = view.findViewById(R.id.subtitle1)
                val code: TextView = view.findViewById(R.id.code)
                val icon: ImageView = view.findViewById(R.id.icon)
                val Edit: ImageView = view.findViewById(R.id.detail_icon)

                name.text = supplie.Name
                price.text = supplie.Price.toString() + "$"
                description.text = supplie.Description
                code.text = supplie.Code
                icon.setImageResource(R.drawable.supplies)

                Edit.setOnClickListener{
                    cargar_supplieId(supplie.Id)
                }
            }
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.listSupplies)
        val noResultsTextView: TextView = view.findViewById(R.id.txtNoResults)


        cargar_supplies {
            if (supplies.isEmpty()) {
                recyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noResultsTextView.visibility = View.GONE

            }
        }

        val btnCambiarFragment: ImageButton = view.findViewById(R.id.btnCambiarFragmento)

        if(roleId == 1){
            btnCambiarFragment.visibility = View.VISIBLE
        }else{
            btnCambiarFragment.visibility = View.GONE
        }

        btnCambiarFragment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FormSupplieFragment.newInstance(null))
                .addToBackStack(null)
                .commit()
        }
        val btnVolver: ImageButton = view.findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = adapter
    }

    private fun cargar_supplies(onComplete: () -> Unit) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlSupplies,
                null,
                { response ->
                    supplies.clear()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val supplie = Supplie(
                            Id = item.getInt("id"),
                            Name = item.getString("name"),
                            Description = item.getString("description"),
                            Code = item.getString("code"),
                            Price = item.getDouble("price")
                        )
                        supplies.add(supplie)
                    }
                    progressDialog.dismiss()
                    adapter.updateData(supplies)
                    adapter.notifyDataSetChanged()
                    onComplete()
                },
                { error ->
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                    builder.setTitle(errorTitle)
                        .setMessage("Error al cargar los datos. \nError: ${error}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                    onComplete()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            progressDialog.dismiss()
            Toast.makeText(
                context,
                "Error al cargar data: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
            onComplete()
        }
    }

    private fun cargar_supplieId(id: Int) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.GET,
                "${urls.urlSupplies}/${id}",
                null,
                { response ->
                    val supplie = Supplie(
                        Id = response.getInt("id"),
                        Name = response.getString("name"),
                        Description = response.getString("description"),
                        Code = response.getString("code"),
                        Price = response.getDouble("price")
                    )
                    progressDialog.dismiss()

                    val formSupplieFragment = FormSupplieFragment.newInstance(supplie)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, formSupplieFragment)
                        .addToBackStack(null)
                        .commit()
                },
                { error ->
                    progressDialog.dismiss()
                    progressDialog.dismiss()
                    val builder = AlertDialog.Builder(requireContext())
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                    builder.setTitle(errorTitle)
                        .setMessage("Error al cargar los datos. \nError: ${error}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            progressDialog.dismiss()
            Toast.makeText(
                context,
                "Error al cargar data: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SupplieFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}