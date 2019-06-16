package com.sagrishin

import com.google.gson.annotations.SerializedName

data class TestPatient(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)

data class TestAppointment(
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String,
    val dateTime: String = "$date $time"
)
