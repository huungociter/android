package com.example.scanqrcode
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ScanActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        // Lấy đối tượng button
        val logoutButton: MaterialButton = findViewById(R.id.btn_logout)

        logoutButton.setOnClickListener {
            // Tạo một AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận")
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?")

            // Nút "Yes"
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Chuyển đến MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                dialog.dismiss() // Đóng dialog
            }

            // Nút "No"
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Đóng dialog mà không làm gì cả
            }

            // Hiển thị AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }
        val data = mutableListOf<Array<String>>()

        for (rowData in data) {
            val row = TableRow(this)

            for (cellData in rowData) {
                val textView = TextView(this)
                textView.text = cellData
                textView.setPadding(8, 8, 8, 8)
                textView.gravity = Gravity.CENTER
                textView.background = resources.getDrawable(R.drawable.border, null)
                row.addView(textView)
            }

            val tableLayout: TableLayout = findViewById(R.id.tableLayout)
            tableLayout.addView(row)
        }

        // Lấy TableLayout và Button từ layout
        val tableLayout: TableLayout = findViewById(R.id.tableLayout)
        val addDeviceButton: MaterialButton = findViewById(R.id.btn_addDevice)

// Thiết lập sự kiện OnClickListener cho nút
        addDeviceButton.setOnClickListener {
            // Dữ liệu cần thêm vào khi nhấn nút
            val dataToAdd = arrayOf("4", "SN12348", "Active", "No issues")

            // Tạo một TableRow mới
            val row = TableRow(this)

            // Duyệt qua dữ liệu và tạo TextView cho mỗi phần tử
            for (cellData in dataToAdd) {
                val textView = TextView(this)
                textView.text = cellData
                textView.setPadding(8, 8, 8, 8)
                textView.gravity = Gravity.CENTER
                textView.background = resources.getDrawable(R.drawable.border, null)
                row.addView(textView)
            }

            // Thêm TableRow vào TableLayout
            tableLayout.addView(row)
        }
    }
}