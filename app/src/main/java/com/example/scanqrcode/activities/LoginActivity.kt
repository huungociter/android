package com.example.scanqrcode.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import com.example.scanqrcode.R
import com.example.scanqrcode.network.RetrofitClient
import com.example.scanqrcode.utils.LanguageUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var buttonFlag: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Get the TextInputEditText for Employee ID
        val employeeEditText: TextInputEditText = findViewById(R.id.EmployeeId_edit_text)

        // Get the TextInputEditText for Date
        val dateEditText: TextInputEditText = findViewById(R.id.date_edit_text)

        // Get the MaterialButton for Login
        val loginButton: MaterialButton = findViewById(R.id.btn_Login)

        // Get the flag button
        buttonFlag = findViewById(R.id.btn_flag)

        LanguageUtils.loadLanguage(this, buttonFlag)

        // Set the current date in the TextInputEditText
        val currentDate = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        dateEditText.setText(currentDate)



        // Set up a click listener for the Date TextInputEditText to open a DatePickerDialog
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            // Sử dụng DatePickerDialog chỉ để chọn năm và tháng
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, _ ->
                    val selectedDate = "$selectedYear-${selectedMonth + 1}" // Chỉ hiển thị năm và tháng
                    dateEditText.setText(selectedDate)
                },
                year, month, 1 // Đặt ngày là 1
            )

            // Ẩn ngày bằng cách thiết lập chế độ chỉ chọn năm và tháng
            try {
                val datePicker = datePickerDialog.datePicker
                val daySpinnerId = resources.getIdentifier("day", "id", "android")
                val daySpinner = datePicker.findViewById<View>(daySpinnerId)
                if (daySpinner != null) {
                    daySpinner.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            datePickerDialog.show()
        }

        // Set up a click listener for the Login button
        loginButton.setOnClickListener {
            val employeeId = employeeEditText.text.toString()
            val dateScan = dateEditText.text.toString()

            if (employeeId.isEmpty()) {
                Toast.makeText(this@LoginActivity, getString(R.string.error_employee_id_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitClient.apiService.checkEmployeeExist(employeeId)
                    if (response.isSuccessful) {
                        val employeeResponse = response.body()
                        if (employeeResponse?.status == "ok") {
                            val intent = Intent(this@LoginActivity, ScanActivity::class.java)
                            intent.putExtra("employeeId", employeeId)
                            intent.putExtra("dateScan", dateScan)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.error_employee_not_exist), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Error during API call", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        val employeeIdInputLayout = findViewById<TextInputLayout>(R.id.EmployeeId_input_layout)
        val dateInputLayout = findViewById<TextInputLayout>(R.id.date_input_layout)
        val loginButton = findViewById<MaterialButton>(R.id.btn_Login)

        // Cập nhật văn bản cho các view
        employeeIdInputLayout.hint = getString(R.string.employee_id)
        dateInputLayout.hint = getString(R.string.inventory_month)
        loginButton.text = getString(R.string.login)
    }
}
