package com.sena.fincaudita.review

import GenericAdapter
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
import com.android.volley.Request
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Checklist
import com.sena.fincaudita.Entity.Evidence
import com.sena.fincaudita.Entity.Qualification
import com.sena.fincaudita.Entity.Review
import com.sena.fincaudita.R

class RevisionFragment : Fragment() {

    private lateinit var adapter: GenericAdapter<Review>
    private var reviews = mutableListOf<Review>()
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
        return inflater.inflate(R.layout.fragment_revision, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.listRevision)

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        roleId = sharedPreferences?.getInt("role_id", -1)
        userId = sharedPreferences?.getInt("user_id", -1)

        adapter = GenericAdapter(
            items = reviews,
            layoutResId = R.layout.item_recycler,
            bindView = { view, review ->
                val fecha: TextView = view.findViewById(R.id.title1)
                val code: TextView = view.findViewById(R.id.code)
                val tecnico: TextView = view.findViewById(R.id.subtitle1)
                val lot: TextView = view.findViewById(R.id.additional_info)
                val icon: ImageView = view.findViewById(R.id.icon)
                val Edit: ImageView = view.findViewById(R.id.detail_icon)
                fecha.text = review.date
                code.text = review.code
                tecnico.text = "Técnico: ${review.tecnico}"
                lot.text = review.lot
                icon.setImageResource(R.drawable.document_svgrepo_com)
                Edit.setOnClickListener {
                    cargar_reviewId(review.id)
                }
            }
        )

        val noResultsTextView: TextView = view.findViewById(R.id.txtNoResults)

        cargar_review{
        if (reviews.isEmpty()) {
            recyclerView.visibility = View.GONE
            noResultsTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noResultsTextView.visibility = View.GONE
        }
        }

        val btnSiguiente: ImageButton = view.findViewById(R.id.btnCambiarFragmento)
        if(roleId == 3){
            btnSiguiente.visibility = View.GONE
        }else{
            btnSiguiente.visibility = View.VISIBLE
        }
        btnSiguiente.setOnClickListener {
            val formReviewFragment = FormReviewFragment.newInstance(null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, formReviewFragment)
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

    private fun cargar_review(onComplete: () -> Unit){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var url = urls.urlReview
        if(roleId == 2){
            url = "${urls.urlReview}/user/${userId}"
        }
        if(roleId == 3){
            url = "${urls.urlReview}/productor/${userId}"
        }
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    reviews.clear()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        reviews.add(
                            Review(
                                item.getInt("id"),
                                item.getString("date_review"),
                                item.getString("code"),
                                item.getString("observation"),
                                item.getInt("lotId"),
                                item.getInt("tecnicoId"),
                                item.getString("lot"),
                                item.getString("tecnico"),
                                item.getInt("checklistId"),
                                emptyList(),
                                null,
                            )
                        )
                    }
                    progressDialog.dismiss()

                    adapter.updateData(reviews)
                    adapter.notifyDataSetChanged()
                    onComplete()
                },{error ->
                    progressDialog.dismiss()
                    onComplete()
                    val builder = AlertDialog.Builder(requireContext())
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                        builder.setTitle(errorTitle)
                            .setMessage("Error al cargar los datos. \n Error: ${error}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        }catch (error: Exception){
            progressDialog.dismiss()

            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargar_reviewId(id: Int){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando información...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.GET,
                "${urls.urlReview}/${id}",
                null,
                { response ->
                    val evidenceArray = response.optJSONArray("evidences")
                    val evidences = mutableListOf<Evidence>()
                    if (evidenceArray != null) {
                        for (j in 0 until evidenceArray.length()) {
                            val evidenceItem = evidenceArray.getJSONObject(j)
                            evidences.add(
                                Evidence(
                                evidenceItem.getInt("id"),
                                evidenceItem.getString("code"),
                                evidenceItem.getString("document"),
                                )
                            )
                        }
                    }

                    var checklist: Checklist? = null
                    val checklistItem = response.optJSONObject("checklists")
                    if (checklistItem != null) {
                        val qualificationsArray = checklistItem.optJSONArray("qualifications")
                        val qualifications = mutableListOf<Qualification>()
                        if (qualificationsArray != null) {
                            for (j in 0 until qualificationsArray.length()) {
                                val qualificationItem = qualificationsArray.getJSONObject(j)
                                qualifications.add(
                                    Qualification(
                                        qualificationItem.getInt("id"),
                                        qualificationItem.getString("observation"),
                                        qualificationItem.getInt("qualification_criteria"),
                                        qualificationItem.getInt("assessmentCriteriaId"),
                                    )
                                )
                            }
                        }
                        checklist = Checklist(
                            checklistItem.getInt("id"),
                            checklistItem.getString("code"),
                            checklistItem.getInt("calification_total"),
                            qualifications,
                        )
                    }

                    val review = Review(
                        response.getInt("id"),
                        response.getString("date_review"),
                        response.getString("code"),
                        response.getString("observation"),
                        response.getInt("lotId"),
                        response.getInt("tecnicoId"),
                        response.getString("lot"),
                        response.getString("tecnico"),
                        response.getInt("checklistId"),
                        evidences,
                        checklist,
                    )
                    progressDialog.dismiss()
                    val formReviewFragment = FormReviewFragment.newInstance(review)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, formReviewFragment)
                        .addToBackStack(null)
                        .commit()
                }, { error ->
                    progressDialog.dismiss()

                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                })
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        }catch (error: Exception){
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
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RevisionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}