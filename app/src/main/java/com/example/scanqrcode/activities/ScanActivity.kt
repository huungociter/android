package com.example.scanqrcode.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.scanqrcode.models.InsertRequest
import com.example.scanqrcode.R
import com.example.scanqrcode.network.RetrofitClient
import com.example.scanqrcode.utils.LanguageUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanActivity : AppCompatActivity() {
    // Get the flag button
    private lateinit var buttonFlag: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Get data from intent
        val employeeId = intent.getStringExtra("employeeId") ?: ""
        val dateScan = intent.getStringExtra("dateScan") ?: ""
        var tableData = mutableListOf<Array<String>>()
        val logoutButton: MaterialButton = findViewById(R.id.btn_logout)
        val scanEditText: TextInputEditText = findViewById(R.id.scan_edit_text)
        val saveButton: MaterialButton = findViewById(R.id.btn_save)
        val tableLayout: TableLayout = findViewById(R.id.tableLayout)
        val addDeviceButton: MaterialButton = findViewById(R.id.btn_addDevice)
        var nameDeviceScan : String
        var currentIndex = 1

        buttonFlag = findViewById(R.id.btn_flag_scan_activity)

        LanguageUtils.loadLanguage(this, buttonFlag)

        // Add device button click listener
        addDeviceButton.setOnClickListener {
            val scanValue = scanEditText.text.toString()
            if (scanValue.isEmpty()) {
                Toast.makeText(this@ScanActivity, getString(R.string.please_enter_serial_number), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the entered serial number already exists in the tableData list
            val isSerialExists = tableData.any { it[1] == scanValue } // The [1] index refers to the serial number in tableData
            if (isSerialExists) {
                // If the serial number already exists, show a message indicating the serial number is already taken
                Toast.makeText(this@ScanActivity, getString(R.string.serial_number_duplicate_in_table), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop further execution if serial number exists
            }

            // Check serial number via API
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitClient.apiService.checkSerialNumber(serialNumber = scanValue)
                    if (response.isSuccessful) {
                        // Add data to the table
                        val serialNumberCheckResponse = response.body()
                        val status = serialNumberCheckResponse?.status ?: ""
                        val comment = serialNumberCheckResponse?.message ?: ""
                        val dataToAdd = arrayOf(currentIndex.toString(), scanValue, status, comment)
                        tableData.add(dataToAdd)

                        // Create a new row in the TableLayout
                        val row = TableRow(this@ScanActivity)
                        for (cellData in dataToAdd) {
                            val textView = TextView(this@ScanActivity)
                            textView.text = cellData
                            textView.setPadding(8, 8, 8, 8)
                            textView.gravity = Gravity.CENTER
                            textView.background = resources.getDrawable(R.drawable.border, null)
                            row.addView(textView)
                        }

                        tableLayout.addView(row)
                        currentIndex += 1
                        scanEditText.text?.clear()
                    } else {
                        Toast.makeText(this@ScanActivity, getString(R.string.api_error), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ScanActivity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

//        // Logout button click listener
//        logoutButton.setOnClickListener {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle(getString(R.string.logout_confirmation_title))
//            builder.setMessage(getString(R.string.logout_confirmation_message))
//
//            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
//                val intent = Intent(this@ScanActivity, LoginActivity::class.java)
//                startActivity(intent)
//                dialog.dismiss()
//            }
//            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
//                dialog.dismiss()
//            }
//
//            val alertDialog = builder.create()
//            alertDialog.show()
//        }

        // Save button click listener
        saveButton.setOnClickListener {
            if (tableData.isEmpty()) {
                Toast.makeText(this, getString(R.string.no_data_to_save), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare data and send to API
            val dataToSend = tableData.map { it.toList() }
            val request = InsertRequest(employeeId, dateScan, dataToSend)

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.apiService.insertData(request)
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ScanActivity, getString(R.string.data_saved_successfully), Toast.LENGTH_SHORT).show()
                            val childCount = tableLayout.childCount
                            if (childCount > 1) {
                                tableLayout.removeViews(1, childCount - 1)
                            }
                            currentIndex = 1
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ScanActivity, getString(R.string.api_error) + ": ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ScanActivity, getString(R.string.connection_error) + ": ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Populate initial table data if needed
        for (rowData in tableData) {
            val row = TableRow(this)
            for (cellData in rowData) {
                val textView = TextView(this)
                textView.text = cellData
                textView.setPadding(8, 8, 8, 8)
                textView.gravity = Gravity.CENTER
                textView.background = resources.getDrawable(R.drawable.border, null)
                row.addView(textView)
            }
            tableLayout.addView(row)
        }

        // TextWatcher to format input text
        scanEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val inputText = it.toString()
                    val trimmedText = inputText.substringBefore("$")
                    if (inputText != trimmedText) {
                        scanEditText.removeTextChangedListener(this)
                        scanEditText.setText(trimmedText)
                        scanEditText.setSelection(trimmedText.length)
                        scanEditText.addTextChangedListener(this)
                    }
                }
            }
        })

        // Set up the language popup menu for the flag button
        buttonFlag.setOnClickListener {
            val popupMenu = PopupMenu(this, buttonFlag)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.menu_flags, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_vietnamese -> {
                        LanguageUtils.changeLanguage(this, "vi");
                        updateUI();
                        buttonFlag.setImageResource(R.drawable.flag_vietnam)

                        true
                    }
                    R.id.menu_japanese -> {
                        LanguageUtils.changeLanguage(this, "ja");
                        updateUI();
                        buttonFlag.setImageResource(R.drawable.flag_japan)
                        true
                    }
                    R.id.menu_english -> {
                        LanguageUtils.changeLanguage(this, "en");
                        updateUI();
                        buttonFlag.setImageResource(R.drawable.flag_uk)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun updateUI() {
        // Retrieve UI elements from layout
        val qrScannerText = findViewById<TextView>(R.id.qrScannerText)
        val scanInputLayout = findViewById<TextInputLayout>(R.id.scan_input_layout)
        val btnAddDevice = findViewById<MaterialButton>(R.id.btn_addDevice)
        val btnLogout = findViewById<MaterialButton>(R.id.btn_logout)
        val btnSave = findViewById<MaterialButton>(R.id.btn_save)
        val sttTextView = findViewById<TextView>(R.id.stt)
        val serialNumberTextView = findViewById<TextView>(R.id.serial_number)
        val statusTextView = findViewById<TextView>(R.id.status)
        val commentTextView = findViewById<TextView>(R.id.comment)

        // Update text for the views
        qrScannerText.text = getString(R.string.qr_scanner)
        scanInputLayout.hint = getString(R.string.scan_or_enter_serial_number)
        btnAddDevice.text = getString(R.string.add_device)
        btnLogout.text = getString(R.string.logout)
        btnSave.text = getString(R.string.save)
        sttTextView.text = getString(R.string.stt)
        serialNumberTextView.text = getString(R.string.serial_number)
        statusTextView.text = getString(R.string.status)
        commentTextView.text = getString(R.string.comment)
    }
}