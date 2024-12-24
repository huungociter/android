package com.example.scanqrcode.network

import com.example.scanqrcode.models.EmployeeResponse
import com.example.scanqrcode.models.InsertRequest
import com.example.scanqrcode.models.SerialNumberCheckResponse
import com.example.scanqrcode.models.TableSaveResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("Common/RegisterMeetingEmployee")
    suspend fun checkEmployeeExist(
        @Query("employeeId") employeeId: String
    ): Response<EmployeeResponse>

    @GET("Common/CheckExistSerialNumber")
    suspend fun checkSerialNumber(
        @Query("serialNumber") serialNumber: String
    ): Response<SerialNumberCheckResponse>

    @POST("Common/InsertData")
    suspend fun insertData(@Body request: InsertRequest): Response<TableSaveResponse>
}