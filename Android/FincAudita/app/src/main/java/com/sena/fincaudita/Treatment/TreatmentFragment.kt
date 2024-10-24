package com.sena.fincaudita.Treatment

import GenericAdapter
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.LotTreatment
import com.sena.fincaudita.Entity.SupplieTreatment
import com.sena.fincaudita.Entity.Treatment
import com.sena.fincaudita.R
import java.text.SimpleDateFormat
import java.util.Locale

class TreatmentFragment : Fragment() {

    private lateinit var adapter: GenericAdapter<Treatment>
    private var treatments = mutableListOf<Treatment>()
    private var roleId: Int? = null
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_treatment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        roleId = sharedPreferences?.getInt("role_id", -1)
        userId = sharedPreferences?.getInt("user_id", -1)

        adapter = GenericAdapter(
            items = treatments,
            layoutResId = R.layout.item_recycler,
            bindView = { view, treatment ->
                val fecha: TextView = view.findViewById(R.id.title1)
                val type: TextView = view.findViewById(R.id.subtitle1)
                val supplie: TextView = view.findViewById(R.id.additional_info)
                val icon: ImageView = view.findViewById(R.id.icon)
                val Edit: ImageView = view.findViewById(R.id.detail_icon)
                val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val date = dateFormatInput.parse(treatment.dateTreatment)
                fecha.text = dateFormatOutput.format(date)
                type.text = treatment.typeTreatment
                val supplieNames = treatment.supplieList.joinToString { it.supplie }
                supplie.text = supplieNames

                icon.setImageResource(R.drawable.fertilizer)


                Edit.setOnClickListener {
                    cargar_treatmentId(treatment.id)
                }
            }
        )



        val recyclerView: RecyclerView = view.findViewById(R.id.listTreatment)
        val noResultsTextView: TextView = view.findViewById(R.id.txtNoResults)

        cargar_treatments{
            if (treatments.isEmpty()) {
                recyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noResultsTextView.visibility = View.GONE
            }
        }

        val btnVolver: ImageButton = view.findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val btnSiguiente: ImageButton = view.findViewById(R.id.btnCambiarFragmento)

        if(roleId == 3){
            btnSiguiente.visibility = View.GONE
        }else{
            btnSiguiente.visibility = View.VISIBLE
        }
        btnSiguiente.setOnClickListener {
            val formTreatmentFragment = FormTreatmentFragment.newInstance(null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, formTreatmentFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = adapter
    }

    private fun cargar_treatments(onComplete: () -> Unit){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var url = urls.urlTreatment
        if(roleId == 3){
            url = "${urls.urlTreatment}/user/${userId}"
        }
        try{
            val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    treatments.clear()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val lotArray = item.optJSONArray("lotList")
                        val lots = mutableListOf<LotTreatment>()
                        if(lotArray != null){
                            for (j in 0 until lotArray.length()) {
                                val lotItem = lotArray.getJSONObject(j)
                                lots.add(
                                    LotTreatment(
                                        lotItem.getInt("id"),
                                        lotItem.getInt("lotId"),
                                        lotItem.getString("lot")
                                    )
                                )
                            }
                        }


                        val suppliArray = item.optJSONArray("supplieList")
                        val supplies = mutableListOf<SupplieTreatment>()
                        if (suppliArray != null){
                            for (j in 0 until suppliArray.length()) {
                                val suppliItem = suppliArray.getJSONObject(j)
                                supplies.add(
                                    SupplieTreatment(
                                        suppliItem.getInt("id"),
                                        suppliItem.getString("dose"),
                                        suppliItem.getInt("suppliesId"),
                                        suppliItem.getString("supplie"),
                                    )
                                )
                            }
                        }

                        treatments.add(
                            Treatment(
                                item.getInt("id"),
                                item.getString("dateTreatment"),
                                item.getString("typeTreatment"),
                                item.getString("quantityMix"),
                                lots,
                                supplies
                            )
                        )
                    }
                    progressDialog.dismiss()
                    adapter.updateData(treatments)
                    adapter.notifyDataSetChanged()
                    onComplete()
                },{ error ->
                    onComplete()
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
        }catch (error: Exception){
            progressDialog.dismiss()
            onComplete()
            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargar_treatmentId(id: Int){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try{
            val request = JsonObjectRequest(
                Request.Method.GET,
                "${urls.urlTreatment}/${id}",
                null,
                { response ->
                    val lotArray = response.optJSONArray("lotList")
                    val lots = mutableListOf<LotTreatment>()
                    if(lotArray != null){
                        for (j in 0 until lotArray.length()) {
                            val lotItem = lotArray.getJSONObject(j)
                            lots.add(
                                LotTreatment(
                                    lotItem.getInt("id"),
                                    lotItem.getInt("lotId"),
                                    lotItem.getString("lot")
                                )
                            )
                        }
                    }

                    val suppliArray = response.optJSONArray("supplieList")
                    val supplies = mutableListOf<SupplieTreatment>()
                    if (suppliArray != null){
                        for (j in 0 until suppliArray.length()) {
                            val suppliItem = suppliArray.getJSONObject(j)
                            supplies.add(
                                SupplieTreatment(
                                    suppliItem.getInt("id"),
                                    suppliItem.getString("dose"),
                                    suppliItem.getInt("suppliesId"),
                                    suppliItem.getString("supplie"),
                                )
                            )
                        }
                    }

                    val treatment = Treatment(
                        response.getInt("id"),
                        response.getString("dateTreatment"),
                        response.getString("typeTreatment"),
                        response.getString("quantityMix"),
                        lots,
                        supplies
                    )
                    progressDialog.dismiss()
                    val formTreatmentFragment = FormTreatmentFragment.newInstance(treatment)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, formTreatmentFragment)
                        .addToBackStack(null)
                        .commit()
                }
                ,{ error ->
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
        }catch (error: Exception){
            progressDialog.dismiss()
            val view: View = requireView()
            Snackbar.make(view, "Error: ${error.message}", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            TreatmentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}