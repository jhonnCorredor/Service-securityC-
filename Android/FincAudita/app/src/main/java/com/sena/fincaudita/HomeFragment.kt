package com.sena.fincaudita

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
import android.widget.CalendarView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Alert
import com.sena.fincaudita.Farm.FarmFragment
import com.sena.fincaudita.Supplie.SupplieFragment
import com.sena.fincaudita.Treatment.TreatmentFragment
import com.sena.fincaudita.review.RevisionFragment
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {
    private var alerts = mutableListOf<Alert>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val calendarView  = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnTouchListener { _, _ -> true }
        val ctnRevision = view.findViewById<ConstraintLayout>(R.id.cntRevision)
        val ctnFarm = view.findViewById<ConstraintLayout>(R.id.cntFarm)
        val ctnSupplie = view.findViewById<ConstraintLayout>(R.id.cntSupplie)
        val ctnFertilization = view.findViewById<ConstraintLayout>(R.id.cntFertilization)

        ctnRevision.setOnClickListener {
            changeFragment(RevisionFragment.newInstance())
        }
        ctnFarm.setOnClickListener {
            changeFragment(FarmFragment.newInstance())
        }
        ctnSupplie.setOnClickListener {
            changeFragment(SupplieFragment.newInstance())
        }
        ctnFertilization.setOnClickListener {
            changeFragment(TreatmentFragment.newInstance())
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    private fun cargar_alerts(onComplete: () -> Unit) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando datos...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userID = sharedPreferences?.getInt("user_id", -1)
        if (userID != -1) {
            try {
                val params = JSONObject().apply {
                    put("Id", userID)
                }

                val jsonArrayRequest = object : JsonRequest<JSONArray>(
                    Request.Method.POST,
                    urls.urlNotification,
                    params.toString(),
                    Response.Listener { response ->
                        try {
                            alerts.clear()
                            for (i in 0 until response.length()) {
                                val item = response.getJSONObject(i)
                                val alert = Alert(
                                    Id = item.getInt("id"),
                                    Title = item.getString("title"),
                                    Theme = item.getString("theme"),
                                    Date = item.getString("date"),
                                    UserId = item.getInt("userId")
                                )
                                alerts.add(alert)
                            }
                            progressDialog.dismiss()
                            onComplete()
                        } catch (e: JSONException) {
                            progressDialog.dismiss()
                            onComplete()
                            Toast.makeText(context, "Error al procesar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
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
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        return headers
                    }

                    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONArray> {
                        return try {
                            val jsonString = String(response?.data ?: ByteArray(0), Charsets.UTF_8)
                            Response.success(JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response))
                        } catch (e: JSONException) {
                            Response.error(ParseError(e))
                        }
                    }
                }

                val queue = Volley.newRequestQueue(context)
                queue.add(jsonArrayRequest)

            } catch (error: Exception) {
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
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
}