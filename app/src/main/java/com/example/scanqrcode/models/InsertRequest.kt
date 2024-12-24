package com.example.scanqrcode.models

data class InsertRequest(
    val employeeID: String,
    val dateScan: String,
    val tableData: List<List<String>>
)
