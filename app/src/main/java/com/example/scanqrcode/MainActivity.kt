package com.example.scanqrcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import android.content.Intent
import com.google.android.material.button.MaterialButton
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy đối tượng TextInputEditText
        val EmployeeEditText: TextInputEditText = findViewById(R.id.EmployeeId_edit_text)

        // Lấy đối tượng TextInputEditText
        val dateEditText: TextInputEditText = findViewById(R.id.date_edit_text)

        // Lấy đối tượng button
        val loginButton: MaterialButton = findViewById(R.id.btn_Login)

        // Lấy ngày hiện tại
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Đặt giá trị ngày hiện tại vào TextInputEditText
        dateEditText.setText(currentDate)

        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Hiển thị DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Đặt ngày chọn vào TextInputEditText
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    dateEditText.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        loginButton.setOnClickListener {
            // Tạo một Intent để chuyển đến SecondActivity
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
    }
}