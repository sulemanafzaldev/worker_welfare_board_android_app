package com.example.wwbinspectionapp.model.LoginModel

data class LoginResponce(
    val about: Any,
    val department: Department,
    val is_active: Boolean,
    val message: String,
    val mobile_no: Any,
    val name: String,
    val profile_image: String,
    val sub_zone: SubZone,
    val success: Boolean,
    val token: String,
    val user_id: Int,
    val username: String,
    val uuid: String,
    val zone: Zone
)