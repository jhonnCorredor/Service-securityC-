package com.sena.fincaudita

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
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Alert
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NotificationFragment : Fragment() {
    private var alerts = mutableListOf<Alert>()
    private lateinit var adapter: GenericAdapter<Alert>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.listAlerts)

        val btnVolver: ImageButton = view.findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        val noResultsTextView: TextView = view.findViewById(R.id.txtNoResults)

        adapter = GenericAdapter(
            items = alerts,
            layoutResId = R.layout.item_notification,
            bindView = { view, alert ->
                val title: TextView = view.findViewById(R.id.notification_title)
                val date: TextView = view.findViewById(R.id.notification_date)
                val theme: View = view.findViewById(R.id.notification_color)

                title.text = alert.Title
                date.text = alert.Date
                val colorRes = when (alert.Theme) {
                    "rojo" -> R.color.red
                    "amarillo" -> R.color.yellow
                    "verde" -> R.color.green
                    "azul" -> R.color.blue
                    "morado" -> R.color.purple
                    else -> R.color.black
                }

                theme.setBackgroundColor(ContextCompat.getColor(view.context, colorRes))
            }
        )
        recyclerView.adapter = adapter
        cargar_alerts{
            if (alerts.isEmpty()) {
                recyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noResultsTextView.visibility = View.GONE
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
                            adapter.updateData(alerts)
                            adapter.notifyDataSetChanged()
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



    companion object {
        @JvmStatic
        fun newInstance() =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}