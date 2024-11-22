package com.example.wwbinspectionapp.auth

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wwbinspectionapp.grantList.GrantAcceptRejectResponse
import com.example.wwbinspectionapp.grantList.GrantListResponse
import com.example.wwbinspectionapp.model.LoginModel.LoginResponce
import com.example.wwbinspectionapp.model.UserRequest
import com.example.wwbinspectionapp.model.factoryList.FactoryListResponce
import com.example.wwbinspectionapp.repository.UserRepository
import com.example.wwbinspectionapp.utils.Helper
import com.example.wwbinspectionapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val userResponseLiveData: LiveData<NetworkResult<LoginResponce>>
        get() = userRepository.userRepoLiveData

    val factoryListLiveData: LiveData<NetworkResult<FactoryListResponce>>
        get() = userRepository.factoryListLiveData


    val grantListResponseLiveData: LiveData<NetworkResult<GrantListResponse>>
        get() = userRepository.grantListLiveData

    val approveGrantRequestListener: LiveData<NetworkResult<GrantAcceptRejectResponse>>
        get() = userRepository.grantGrantAcceptLiveData


//    fun registerUser(userRequest: UserRequest) {
//        viewModelScope.launch {
//            userRepository.registerUser(userRequest)
//        }
//    }

    fun loginUser(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }

    fun getUserFactoryList(token: String) {
        viewModelScope.launch {
            userRepository.getFactoryList(token)
        }
    }


    fun validateCredentials(
        emailAddress: String, userName: String, password: String,
        isLogin: Boolean
    ): Pair<Boolean, String> {

        var result = Pair(true, "")
        if (TextUtils.isEmpty(emailAddress) || (!isLogin && TextUtils.isEmpty(userName)) || TextUtils.isEmpty(
                password
            )
        ) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        } else if (!TextUtils.isEmpty(password) && password.length <= 5) {
            result = Pair(false, "Password length should be greater than 5")
        }
        return result
    }


    fun getGrantList(authToken: String, grantType: String, factoryId: Int) {
        viewModelScope.launch {
            userRepository.getGrantList(authToken, grantType, factoryId)
        }
    }

    fun approveGrant(
        authToken: String,
        grantType: String,
        appStatus: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        viewModelScope.launch {
            userRepository.approveGrant(authToken, grantType, appStatus, grantId, factoryWorkerId)
        }
    }
}
