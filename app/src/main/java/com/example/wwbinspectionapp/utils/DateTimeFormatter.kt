package com.example.wwbinspectionapp.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object DateTimeFormatter {
    @SuppressLint("SimpleDateFormat")
    fun format(timeStamp: String): String {
        if (timeStamp.isEmpty())
            return ""

        try {
            // Define the input format (UTC)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Parse the input as UTC

            // Parse the timestamp into a Date object
            val date: Date = inputFormat.parse(timeStamp)!!

            // Define the output formats for date and time in local time zone
            val dateFormatter = SimpleDateFormat("MMM dd, yyyy")
            val timeFormatter = SimpleDateFormat("hh:mm a") // 12-hour format with AM/PM

            // Set the local time zone for the output format
            dateFormatter.timeZone = TimeZone.getDefault() // Get the local time zone dynamically
            timeFormatter.timeZone = TimeZone.getDefault()

            // Format the date and time in local time zone
            val formattedDate = dateFormatter.format(date)
            val formattedTime = timeFormatter.format(date)

            return formattedDate
        } catch (e: ParseException) {
            println("Invalid format: ${e.message}")
            return "Invalid format: ${e.message}"
        }
    }
}
