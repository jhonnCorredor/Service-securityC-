package com.sena.fincaudita.review

import GenericAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.sena.fincaudita.Config.urls
import com.sena.fincaudita.Entity.Assesment
import com.sena.fincaudita.Entity.Farm
import com.sena.fincaudita.Entity.Lot
import com.sena.fincaudita.Entity.Qualification
import com.sena.fincaudita.Entity.Review
import com.sena.fincaudita.R
import com.sena.fincaudita.components.DatePickerFragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormReviewFragment : Fragment() {
    private var qualificationMap = HashMap<Int, Qualification>()
    private var userIdMap = HashMap<String, Int>()
    private var userId: Int? = null
    private var listUsers = mutableListOf<String>()
    private var lotsMap = HashMap<Farm, List<Lot>>()
    private var lotId: Int? = null
    private lateinit var btnSiguiente: Button
    private lateinit var btnCrear: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnAtras: Button
    private lateinit var divider1: View
    private lateinit var divider2: View
    private lateinit var paso2: TextView
    private lateinit var paso3: TextView
    private lateinit var adapter: GenericAdapter<Assesment>
    private var assesments = mutableListOf<Assesment>()
    private val PICK_FILE_REQUEST = 1
    private val PERMISSION_REQUEST_CODE = 123
    private var selectedImageView: ImageView? = null
    private var evidenceImage: String? = null
    private var firmTecnicoImage: String? = null
    private var firmProductorImage: String? = null
    private var adapterEditText = false
    private var reviewUpdate: Review? = null
    private var edit = false
    private var roleId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reviewUpdate = it.getParcelable(ARG_REVIEW)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_review, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        roleId = sharedPreferences?.getInt("role_id", -1)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = imeInsets.bottom - 180
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }

        val btnArchivo = view.findViewById<Button>(R.id.btnElegirArchivo)
        val evidence = view.findViewById<ImageView>(R.id.imagenCultivo)
        val firmTecnico = view.findViewById<ImageView>(R.id.firmaTecnico)
        val firmProductor = view.findViewById<ImageView>(R.id.firmaProductor)
        val txtFarm = view.findViewById<AutoCompleteTextView>(R.id.txtFarm)
        val txtLot = view.findViewById<AutoCompleteTextView>(R.id.txtlot)
        val txtCode = view.findViewById<EditText>(R.id.txtCode)
        val txtObservation = view.findViewById<EditText>(R.id.txtName)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        divider1 = view.findViewById<View>(R.id.divider1)
        divider2 = view.findViewById<View>(R.id.divider2)
        paso2 = view.findViewById<TextView>(R.id.paso2)
        paso3 = view.findViewById<TextView>(R.id.paso3)
        btnAtras = view.findViewById<Button>(R.id.btnAtras)
        btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente)
        btnCrear= view.findViewById<Button>(R.id.btnCrear)
        btnActualizar = view.findViewById(R.id.btnActualizar)
        btnEliminar = view.findViewById(R.id.btnEliminar)
        val cardUser = view.findViewById<CardView>(R.id.cardUserId)
        val txtFecha = view.findViewById<EditText>(R.id.txtFecha)
        val txtUserId = view.findViewById<AutoCompleteTextView>(R.id.txtUserId)


        val horizontalScrollView = view.findViewById<HorizontalScrollView>(R.id.scrollView)
        horizontalScrollView.setOnTouchListener { _, _ ->
            true
        }
        val seccion1 = view.findViewById<ScrollView>(R.id.seccion1)
        val seccion2 = view.findViewById<ScrollView>(R.id.seccion2)
        val seccion3 = view.findViewById<ScrollView>(R.id.seccion3)
        var currentSection = 1
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        val layoutParams1 = seccion1.layoutParams
        layoutParams1.width = screenWidth
        seccion1.layoutParams = layoutParams1
        val layoutParams2 = seccion2.layoutParams
        layoutParams2.width = screenWidth
        seccion2.layoutParams = layoutParams2
        val layoutParams3 = seccion3.layoutParams
        layoutParams3.width = screenWidth
        seccion3.layoutParams = layoutParams3

        listOf(seccion1, seccion2, seccion3).forEach { section ->
            val layoutParams = section.layoutParams
            layoutParams.width = screenWidth
            section.layoutParams = layoutParams
        }

        if(reviewUpdate != null){
            var i = 1
            txtCode.setText(reviewUpdate!!.code)
            txtObservation.setText(reviewUpdate!!.observation)
            txtFecha.setText(reviewUpdate!!.date)
            txtUserId.setText(reviewUpdate!!.tecnico)
            userId = reviewUpdate!!.tecnicoId
            val (beforeColon, afterColon) = reviewUpdate!!.lot.split(":", limit = 2)
            txtFarm.setText(beforeColon)
            txtLot.setText(afterColon)
            lotId = reviewUpdate!!.lotId

            txtCode.isEnabled = false
            txtObservation.isEnabled = false
            txtFecha.isEnabled = false
            txtUserId.isEnabled = false
            txtFarm.isEnabled = false
            txtLot.isEnabled = false

            reviewUpdate?.evidenceList?.forEach { item ->
                val base64String = item.document.replace("data:image/.*;base64,".toRegex(), "")
                val decodedString = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                when (i) {
                    1 -> {
                        evidenceImage = item.document
                        evidence.let {
                            Glide.with(this)
                                .load(bitmap)
                                .into(it)
                        }
                    }
                    2 -> {
                        firmTecnicoImage = item.document
                        firmTecnico.let {
                            Glide.with(this)
                                .load(bitmap)
                                .into(it)
                        }
                    }
                    3 -> {
                        firmProductorImage = item.document
                        firmProductor.let {
                            Glide.with(this)
                                .load(bitmap)
                                .into(it)
                        }
                    }
                }
                i += 1
            }

            val qualifications = reviewUpdate!!.checklist!!.qualifications
            qualifications.forEach { qualification ->
                qualificationMap[qualification.assessmentCriteriaId] = qualification
            }
        }else{
            edit = true
            if(roleId == 2){
                cardUser.visibility = View.GONE
            }
        }

        btnArchivo.setOnClickListener {
            if(edit) {
                checkPermissionsAndOpenFileChooser(evidence)
            }
        }

        firmTecnico.setOnClickListener {
            if(edit) {
                checkPermissionsAndOpenFileChooser(firmTecnico)
            }
        }

        firmProductor.setOnClickListener {
            if(edit) {
                checkPermissionsAndOpenFileChooser(firmProductor)
            }
        }

        btnAtras.setOnClickListener {
            if (currentSection > 1) {
                currentSection--
                updateSections(currentSection, seccion1, seccion2, seccion3)
            }
        }

        btnSiguiente.setOnClickListener {
            if (currentSection < 3) {
                currentSection++
                updateSections(currentSection, seccion1, seccion2, seccion3)
            }
        }

        updateSections(currentSection, seccion1, seccion2, seccion3)

        val btnVolver = view.findViewById<ImageButton>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            requireActivity().onBackPressed()
        }

        txtFecha.setOnClickListener {
            showDatePickerDialog(txtFecha)
        }


        cargar_users { userList, userMap ->
            val userAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                userList
            )
            txtUserId.setAdapter(userAdapter)
            txtUserId.setOnItemClickListener { parent, _, position, _ ->
                val userSelected = parent.getItemAtPosition(position) as String
                userId = userMap[userSelected]
            }
        }

        cargar_farms { farmMap ->
            val farmAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                farmMap.keys.map { it.Name }
            )
            txtFarm.setAdapter(farmAdapter)

            txtFarm.setOnItemClickListener { parent, _, position, _ ->
                val selectedFarmName = parent.getItemAtPosition(position) as String
                val selectedFarm = farmMap.keys.find { it.Name == selectedFarmName }
                selectedFarm?.let {
                    val associatedLots = farmMap[it] ?: emptyList()
                    val lotAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        associatedLots.map { lot -> lot.Cultivo }
                    )
                    txtLot.setAdapter(lotAdapter)

                    txtLot.setOnItemClickListener { parentLot, _, lotPosition, _ ->
                        val selectedLotName = parentLot.getItemAtPosition(lotPosition) as String
                        val selectedLot = associatedLots.find { lot -> lot.Cultivo == selectedLotName }

                        selectedLot?.let {
                            lotId = it.Id
                        }
                    }
                }
            }
        }

        adapter = GenericAdapter(
            items = assesments,
            layoutResId = R.layout.item_checklist,
            bindView = { view, assesment ->
                val txtId = view.findViewById<TextView>(R.id.id)
                val txtAssesment = view.findViewById<TextView>(R.id.textView)
                val txtCalificacion = view.findViewById<EditText>(R.id.txtcalificacion)
                val txtObservation = view.findViewById<EditText>(R.id.txtObservation)

                txtId.text = assesment.Id.toString()
                txtAssesment.text = assesment.Name
                txtCalificacion.hint = "0 - ${assesment.rating}"

                if(roleId == 3){
                    txtCalificacion.isEnabled = false
                    txtObservation.isEnabled = false
                }

                val existingQualification = qualificationMap[assesment.Id]
                txtCalificacion.setText(existingQualification?.qualificationCriteria?.toString() ?: "")
                txtObservation.setText(existingQualification?.observation ?: "")

                txtCalificacion.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        if (txtCalificacion.text.isNullOrEmpty()) {
                            txtCalificacion.error = "Campo obligatorio"
                        }
                        checkAllFieldsFilled()
                    }
                }

                txtObservation.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        if (txtObservation.text.isNullOrEmpty()) {
                            txtObservation.error = "Campo obligatorio"
                        }
                        checkAllFieldsFilled()
                    }
                }

                txtCalificacion.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    val input = StringBuilder(dest).replace(dstart, dend, source.toString()).toString()
                    if (input.isEmpty() || (input.toInt() in 0..assesment.rating)) {
                        null
                    } else {
                        ""
                    }
                })

                txtCalificacion.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val input = s.toString()
                        val qualificationValue = input.toIntOrNull() ?: 0
                        if (qualificationValue > assesment.rating) {
                            txtCalificacion.setText(assesment.rating.toString())
                            txtCalificacion.setSelection(txtCalificacion.text.length)
                        }
                        qualificationMap[assesment.Id] = Qualification(
                            id = assesment.Id,
                            observation = txtObservation.text.toString(),
                            qualificationCriteria = qualificationValue,
                            assessmentCriteriaId = assesment.Id
                        )
                        checkAllFieldsFilled()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                txtObservation.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        qualificationMap[assesment.Id]?.observation = s.toString()
                        checkAllFieldsFilled()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            }
        )
        recyclerView.adapter = adapter
        cargar_assestCriteria()

        btnCrear.setOnClickListener {
            val txtUserId = view.findViewById<AutoCompleteTextView>(R.id.txtUserId)
            if(roleId == 2){
                userId = sharedPreferences?.getInt("user_id", -1)
                txtUserId.setText(userId.toString())
            }
            if (validarCamposGuardarActualizar(
                    txtCode,
                    txtObservation,
                    txtFarm,
                    txtLot,
                    txtFecha,
                    evidence,
                    firmTecnico,
                    firmProductor,
                    txtUserId
                )) {
                val date = txtFecha.text.toString()
                val code = txtCode.text.toString()
                val observation = txtObservation.text.toString()
                val review = Review(0, date, code, observation, lotId!!, userId!!, "", "", 0, listOf(), null)
                save_review(review)
            }else{
                Toast.makeText(context, "Complete todos los campos del formulario", Toast.LENGTH_SHORT).show();
            }
        }

        btnActualizar.setOnClickListener {
            if (reviewUpdate == null) {
                Toast.makeText(requireContext(), "No hay registro para actualizar", Toast.LENGTH_SHORT).show()
            } else if (!edit) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmar Edición")
                    .setMessage("¿Desea editar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        if(roleId != 2){
                            txtUserId.isEnabled = true
                        }
                        txtCode.isEnabled = true
                        txtObservation.isEnabled = true
                        txtFecha.isEnabled = true
                        txtFarm.isEnabled = true
                        txtLot.isEnabled = true
                        edit = true
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            } else {
                if (!validarCamposGuardarActualizar(
                        txtCode,
                        txtObservation,
                        txtFarm,
                        txtLot,
                        txtFecha,
                        evidence,
                        firmTecnico,
                        firmProductor,
                        txtUserId
                    )) {
                    Toast.makeText(context, "Complete todos los campos del formulario", Toast.LENGTH_SHORT).show();
                    return@setOnClickListener
                }

                val confirmUpdateDialog = AlertDialog.Builder(requireContext())
                confirmUpdateDialog.setTitle("Confirmar Actualización")
                    .setMessage("¿Está seguro de que desea actualizar el registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        val date = txtFecha.text.toString()
                        val code = txtCode.text.toString()
                        val observation = txtObservation.text.toString()
                        val review = Review(reviewUpdate!!.id, date, code, observation, lotId!!, userId!!, "", "", 0, listOf(), null)
                        update_review(review)
                        txtCode.isEnabled = false
                        txtObservation.isEnabled = false
                        txtFecha.isEnabled = false
                        txtUserId.isEnabled = false
                        txtFarm.isEnabled = false
                        txtLot.isEnabled = false
                        edit = false
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        btnEliminar.setOnClickListener {
            if (reviewUpdate == null) {
                Toast.makeText(requireContext(), "No hay registro para eliminar", Toast.LENGTH_SHORT).show()
            } else {
                val confirmDeleteDialog = AlertDialog.Builder(requireContext())
                confirmDeleteDialog.setTitle("Confirmar Eliminación")
                    .setMessage("¿Está seguro de que desea eliminar este registro?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        delete_review(reviewUpdate!!)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }

    private fun checkAllFieldsFilled() {
        adapterEditText = assesments.all { assest ->
            val qualification = qualificationMap[assest.Id]
            qualification != null &&
                    qualification.qualificationCriteria != null &&
                    qualification.observation != null &&
                    qualification.qualificationCriteria > 0 &&
                    qualification.observation.isNotEmpty()
        }
    }

    private fun checkPermissionsAndOpenFileChooser(imageView: ImageView) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            openFileChooser(imageView)
        }
    }

    private fun openFileChooser(imageView: ImageView) {
        selectedImageView = imageView
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // Puedes especificar un tipo de archivo, como "image/*" para imágenes
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileName(uri)
                val base64 = uriToBase64(uri)
                if (base64!=null){
                    when( selectedImageView?.id){
                        R.id.imagenCultivo -> {
                            evidenceImage = base64
                        }
                        R.id.firmaTecnico -> {
                            firmTecnicoImage = base64
                        }
                        R.id.firmaProductor -> {
                            firmProductorImage = base64
                        }
                    }
                }

                selectedImageView?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(it)
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser(selectedImageView!!)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            val fileSize = getFileSize(uri)
            if (fileSize > 1 * 1024 * 1024) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
                val byteArrayOutputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream?.use { stream ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (stream.read(buffer).also { len = it } != -1) {
                        byteArrayOutputStream.write(buffer, 0, len)
                    }
                }
                val bytes = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return try {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            var size = 0L
            cursor?.use {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    size = cursor.getLong(sizeIndex)
                }
            }
            size
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun updateSections(currentSection: Int, seccion1: View, seccion2: View, seccion3: View) {
        seccion1.visibility = if (currentSection == 1) View.VISIBLE else View.GONE
        seccion2.visibility = if (currentSection == 2) View.VISIBLE else View.GONE
        seccion3.visibility = if (currentSection == 3) View.VISIBLE else View.GONE

        val blackColor = ContextCompat.getColor(requireContext(), R.color.black)
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

        when (currentSection) {
            1 -> {
                paso2.setBackgroundResource(R.drawable.circle_white)
                paso2.setTextColor(blackColor)
                paso3.setBackgroundResource(R.drawable.circle_white)
                paso3.setTextColor(blackColor)
                divider1.setBackgroundResource(R.color.white)
                divider2.setBackgroundResource(R.color.white)
                btnSiguiente.visibility = View.VISIBLE
                btnCrear.visibility= View.GONE
                btnActualizar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            }
            2 -> {
                paso2.setBackgroundResource(R.drawable.circle_green)
                paso2.setTextColor(whiteColor)
                paso3.setBackgroundResource(R.drawable.circle_white)
                paso3.setTextColor(blackColor)
                divider1.setBackgroundResource(R.color.green)
                divider2.setBackgroundResource(R.color.white)
                btnSiguiente.visibility = View.VISIBLE
                btnCrear.visibility= View.GONE
                btnActualizar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            }
            3 -> {
                paso2.setBackgroundResource(R.drawable.circle_green)
                paso2.setTextColor(whiteColor)
                paso3.setBackgroundResource(R.drawable.circle_green)
                paso3.setTextColor(whiteColor)
                divider1.setBackgroundResource(R.color.green)
                divider2.setBackgroundResource(R.color.green)
                btnSiguiente.visibility = View.GONE
                if(roleId == 3){
                    btnCrear.visibility= View.GONE
                    btnActualizar.visibility = View.GONE
                    btnEliminar.visibility = View.GONE
                }else if(reviewUpdate != null){
                    btnActualizar.visibility = View.VISIBLE
                    btnEliminar.visibility = View.VISIBLE
                }else{
                    btnCrear.visibility= View.VISIBLE
                }
            }
        }

        btnAtras.visibility = if (currentSection > 1) View.VISIBLE else View.GONE
    }

    private fun showDatePickerDialog(editText: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(day, month, year, editText)
        }
        datePicker.show(parentFragmentManager, "datapicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, editText: EditText) {
        val formattedDay = if (day < 10) "0$day" else day.toString()
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
        editText.setText("$year-$formattedMonth-$formattedDay")
    }

    private fun cargar_users(onComplete: (List<String>, HashMap<String, Int>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                "${urls.urlUser}/byRole/2",
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val nombre = item.getString("username")
                        listUsers.add(nombre)
                        userIdMap[nombre] = id
                    }
                    onComplete(listUsers, userIdMap)
                },
                { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(context, "Error al cargar usuarios: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargar_farms(onComplete: (HashMap<Farm, List<Lot>>) -> Unit) {
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlFarm,
                null,
                { response ->
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val lotArray = item.getJSONArray("lots")
                        val listLot = mutableListOf<Lot>()
                        for (j in 0 until lotArray.length()) {
                            val lotItem = lotArray.getJSONObject(j)
                            val lot = Lot(
                                lotItem.getInt("id"),
                                lotItem.getInt("cropId"),
                                lotItem.getInt("num_hectareas"),
                                lotItem.getString("cultivo")
                            )
                            listLot.add(lot)
                        }
                        val farm = Farm(
                            item.getInt("id"),
                            item.getString("name"),
                            item.getInt("cityId"),
                            item.getInt("userId"),
                            item.getString("addres"),
                            item.getInt("dimension")
                        )
                        lotsMap[farm] = listLot
                    }
                    onComplete(lotsMap)
                },
                { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(context, "Error al cargar insumos: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargar_assestCriteria(){
        try {
            val request = JsonArrayRequest(
                Request.Method.GET,
                urls.urlAssesment,
                null,
                { response ->
                    assesments.clear()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val assesment = Assesment(
                            Id = item.getInt("id"),
                            Name = item.getString("name"),
                            rating = item.getInt("rating_range"),
                            type_criterian = item.getString("type_criterian"),
                        )
                        assesments.add(assesment)
                    }
                    adapter.updateData(assesments)
                    adapter.notifyDataSetChanged()
                },
                { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        } catch (error: Exception) {
            Toast.makeText(
                context,
                "Error al cargar data: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validarCamposGuardarActualizar(
        txtCode: EditText,
        txtObservation: EditText,
        txtFarm: AutoCompleteTextView,
        txtLot: AutoCompleteTextView,
        txtFecha: EditText,
        imageView1: ImageView,
        imageView2: ImageView,
        imageView3: ImageView,
        txtTecnico: AutoCompleteTextView,
    ): Boolean {
        var isValid = true
        val minParagraphLength = 100

        if (txtTecnico.text.isEmpty()) {
            txtTecnico.error = "Ingrese el nombre del técnico"
            isValid = false
        }

        if (txtCode.text.isEmpty()) {
            txtCode.error = "Ingresa un código."
            isValid = false
        }

        if (txtObservation.text.isEmpty() || txtObservation.text.length < minParagraphLength) {
            txtObservation.error = "Ingresa una observación de al menos 100 caracteres."
            isValid = false
        }

        if (txtFarm.text.isEmpty()) {
            txtFarm.error = "Selecciona una granja"
            isValid = false
        }

        if (txtLot.text.isEmpty()) {
            txtLot.error = "Selecciona al menos un lote"
            isValid = false
        } else {
            txtLot.error = null
        }

        if (txtFecha.text.isEmpty()) {
            txtFecha.error = "Ingresa una fecha válida"
            isValid = false
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(txtFecha.text.toString())
                if (date != null) {
                    val currentDate = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val sixMonthsLater = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        add(Calendar.MONTH, 6)
                    }

                    if (date.before(currentDate.time)) {
                        txtFecha.error = "La fecha debe ser hoy o futura"
                        isValid = false
                    } else if (date.after(sixMonthsLater.time)) {
                        txtFecha.error = "La fecha no puede ser más de 6 meses en el futuro"
                        isValid = false
                    } else {
                        txtFecha.error = null
                    }
                } else {
                    txtFecha.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                    isValid = false
                }
            } catch (e: ParseException) {
                txtFecha.error = "Formato de fecha inválido. Usa el formato yyyy-MM-dd"
                isValid = false
            }
        }

        val defaultDrawable1 = context?.let { ContextCompat.getDrawable(it, android.R.drawable.ic_menu_gallery) }
        val defaultDrawable2 = context?.let { ContextCompat.getDrawable(it, android.R.drawable.ic_menu_upload) }
        val currentDrawable1 = imageView1.drawable
        val currentDrawable2 = imageView2.drawable
        val currentDrawable3 = imageView3.drawable

        if (currentDrawable1 == null || currentDrawable1.constantState == defaultDrawable1?.constantState) {
            Toast.makeText(context, "Selecciona una imagen en la evidencía del cultivo.", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (currentDrawable2 == null || currentDrawable2.constantState == defaultDrawable2?.constantState) {
            Toast.makeText(context, "Selecciona la imagen de la firma del técnico.", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (currentDrawable3 == null || currentDrawable2.constantState == defaultDrawable2?.constantState) {
            Toast.makeText(context, "Selecciona la imagen de la firma del productor", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if(!adapterEditText){
            Toast.makeText(context, "Realice la lista de chequeo.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }



    private fun save_review(review: Review){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando revisión...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            var qualificationTotal: Int = 0
            val qualifications = JSONArray()
            for (qualification in qualificationMap){
                val json = JSONObject()
                json.put("observation", qualification.value.observation)
                json.put("qualification_criteria", qualification.value.qualificationCriteria)
                json.put("assessmentCriteriaId", qualification.value.assessmentCriteriaId)
                qualificationTotal = qualificationTotal + qualification.value.qualificationCriteria
                qualifications.put(json)
            }
            val checklist = JSONObject()
            checklist.put("code", review.code)
            checklist.put("calification_total", qualificationTotal)
            checklist.put("qualifications", qualifications)
            val image = JSONObject()
            image.put("code", "image")
            image.put("document", evidenceImage)
            val firmT = JSONObject()
            firmT.put("code", "firmTecnico")
            firmT.put("document", firmTecnicoImage)
            val firmP = JSONObject()
            firmP.put("code", "firmProductor")
            firmP.put("document", firmProductorImage)
            val evidences = JSONArray()
            evidences.put(image)
            evidences.put(firmT)
            evidences.put(firmP)
            val params = JSONObject()
            params.put("date_review", review.date)
            params.put("code", review.code)
            params.put("observation", review.observation)
            params.put("lotId", lotId)
            params.put("tecnicoId", userId)
            params.put("evidences", evidences)
            params.put("checklists", checklist)
            val request = JsonObjectRequest(
                Request.Method.POST,
                urls.urlReview,
                params,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Revisión guardada exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },{ error ->
                    progressDialog.dismiss()
                    val errorTitle = SpannableString("Error")
                    errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(errorTitle)
                        .setMessage("Error: ${error}")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        }catch (error: Exception){
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
                .setMessage("Error al guardar la revisión: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun update_review(review: Review){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Actualizando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            var qualificationTotal: Int = 0
            val qualifications = JSONArray()
            for (qualification in qualificationMap){
                val json = JSONObject()
                json.put("observation", qualification.value.observation)
                json.put("qualification_criteria", qualification.value.qualificationCriteria)
                json.put("assessmentCriteriaId", qualification.value.assessmentCriteriaId)
                qualificationTotal = qualificationTotal + qualification.value.qualificationCriteria
                qualifications.put(json)
            }
            val checklist = JSONObject()
            checklist.put("id", reviewUpdate!!.checklist!!.id)
            checklist.put("code", review.code)
            checklist.put("calification_total", qualificationTotal)
            checklist.put("qualifications", qualifications)
            val image = JSONObject()
            image.put("code", "image")
            image.put("document", evidenceImage)
            val firmT = JSONObject()
            firmT.put("code", "firmTecnico")
            firmT.put("document", firmTecnicoImage)
            val firmP = JSONObject()
            firmP.put("code", "firmProductor")
            firmP.put("document", firmProductorImage)
            val evidences = JSONArray()
            evidences.put(image)
            evidences.put(firmT)
            evidences.put(firmP)
            val params = JSONObject()
            params.put("id",review.id)
            params.put("date_review", review.date)
            params.put("code", review.code)
            params.put("observation", review.observation)
            params.put("lotId", lotId)
            params.put("tecnicoId", userId)
            params.put("evidences", evidences)
            params.put("checklists", checklist)
            val request = JsonObjectRequest(
                Request.Method.PUT,
                urls.urlReview,
                params,
                { response ->
                    edit = false
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro actualizado exitosamente")
                        .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                        .create()
                        .show()
                },{ error ->
                    val builder = AlertDialog.Builder(requireContext())
                    if (error.networkResponse != null && error.networkResponse.statusCode in 400..599) {
                        progressDialog.dismiss()
                        val errorTitle = SpannableString("Error")
                        errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)
                        builder.setTitle(errorTitle)
                            .setMessage("Error al actualizar el registro: ${error}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        edit = false
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        builder.setTitle(successTitle)
                            .setMessage("Registro actualizado Exitosamente")
                            .setPositiveButton("OK") { dialog, _ -> requireActivity().supportFragmentManager.popBackStack() }
                            .create()
                            .show()
                    }
                }
            )
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
        }catch (error: Exception){
            progressDialog.dismiss()
            val errorTitle = SpannableString("Error")
            errorTitle.setSpan(ForegroundColorSpan(Color.RED), 0, errorTitle.length, 0)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(errorTitle)
                .setMessage("Error al actualizar la revisión: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun delete_review(review: Review){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Eliminando registro...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            val request = JsonObjectRequest(
                Request.Method.DELETE,
                "${urls.urlReview}/${review.id}",
                null,
                { response ->
                    progressDialog.dismiss()
                    val successTitle = SpannableString("Éxito")
                    successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(successTitle)
                        .setMessage("Registro eliminado Exitosamente")
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
                            .setMessage("Error al eliminar el registro: ${error}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        progressDialog.dismiss()
                        val successTitle = SpannableString("Éxito")
                        successTitle.setSpan(ForegroundColorSpan(Color.GREEN), 0, successTitle.length, 0)
                        builder.setTitle(successTitle)
                            .setMessage("Registro eliminado Exitosamente")
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
                .setMessage("Error al eliminar: ${error}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    companion object {
        const val ARG_REVIEW = "review"
        @JvmStatic
        fun newInstance(review: Review?) =
            FormReviewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_REVIEW, review)
                }
            }
    }
}