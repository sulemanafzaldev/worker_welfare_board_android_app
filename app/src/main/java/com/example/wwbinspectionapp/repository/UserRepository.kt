package com.example.wwbinspectionapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wwbinspectionapp.api.UserApi
import com.example.wwbinspectionapp.grantList.GrantAcceptRejectResponse
import com.example.wwbinspectionapp.grantList.GrantListResponse
import com.example.wwbinspectionapp.model.LoginModel.LoginResponce
import com.example.wwbinspectionapp.model.UserRequest
import com.example.wwbinspectionapp.model.factoryList.FactoryListResponce
import com.example.wwbinspectionapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi) {

    private val _userRepoLiveData = MutableLiveData<NetworkResult<LoginResponce>>()
    val userRepoLiveData: LiveData<NetworkResult<LoginResponce>>
        get() = _userRepoLiveData

    private val _factoryListLiveData = MutableLiveData<NetworkResult<FactoryListResponce>>()
    val factoryListLiveData: LiveData<NetworkResult<FactoryListResponce>>
        get() = _factoryListLiveData

    private val _grantListLiveData = MutableLiveData<NetworkResult<GrantListResponse>>()
    val grantListLiveData: LiveData<NetworkResult<GrantListResponse>>
        get() = _grantListLiveData
    private val _grantAcceptLiveData = MutableLiveData<NetworkResult<GrantAcceptRejectResponse>>()
    val grantGrantAcceptLiveData: LiveData<NetworkResult<GrantAcceptRejectResponse>>
        get() = _grantAcceptLiveData

    suspend fun loginUser(userRequist: UserRequest) {
        _userRepoLiveData.postValue(NetworkResult.Loading())
        val response = userApi.signin(userRequist)
        handleResponse(response)
    }

    private fun handleResponse(response: Response<LoginResponce>) {

        if (response.isSuccessful && response.body() != null) {
            _userRepoLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userRepoLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userRepoLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun getFactoryList(token: String) {
        try {
            _factoryListLiveData.postValue(NetworkResult.Loading())
            val response = userApi.getFactoryList(token)
            if (response.isSuccessful && response.body() != null) {
                _factoryListLiveData.postValue(NetworkResult.Success(response.body()))
            } else {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _factoryListLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            }
        } catch (e: Exception) {
            _factoryListLiveData.postValue(NetworkResult.Error(e.message.toString()))

        }
    }

    suspend fun getGrantList(authToken: String, grantType: String, factoryId: Int) {
        try {
            _grantListLiveData.postValue(NetworkResult.Loading())
            val response = userApi.getGrantList(authToken, grantType, factoryId)
            if (response.isSuccessful && response.body() != null) {
                _grantListLiveData.postValue(NetworkResult.Success(response.body()))
            } else {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _grantListLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            }
        } catch (e: Exception) {
            _grantListLiveData.postValue(NetworkResult.Error(e.message.toString()))

        }
    }

    suspend fun approveGrant(
        authToken: String,
        grantType: String,
        appStatus: String,
        grantId: Int,
        factoryWorkerId: Int
    ) {
        try {
            _grantAcceptLiveData.postValue(NetworkResult.Loading())
            val response =
                userApi.approveGrant(authToken, grantType, appStatus, grantId, factoryWorkerId)
            if (response.isSuccessful && response.body() != null) {
                _grantAcceptLiveData.postValue(NetworkResult.Success(response.body()))
            } else {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _grantAcceptLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            }
        } catch (e: Exception) {
            _grantAcceptLiveData.postValue(NetworkResult.Error(e.message.toString()))

        }
    }
}