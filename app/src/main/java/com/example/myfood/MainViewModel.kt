package com.example.myfood

import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.myfood.service.ApiService
import com.example.myfood.service.RetrofitHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MainViewModel : ViewModel() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val service = RetrofitHelper.getInstance().create(ApiService::class.java)
    val data : MutableState<HomeData?>? = mutableStateOf<HomeData?>(null)
    var emailLogin by mutableStateOf("")
    var passwordLogin by mutableStateOf("")
    var emailSignUp by mutableStateOf("")
    var passwordSignUp by mutableStateOf("")

     var _sharedFlow : MutableSharedFlow<FlowData> = MutableSharedFlow<FlowData>(replay = 0)
     var sharedFlow = _sharedFlow.asSharedFlow()

    fun fetchHomeData(){
        viewModelScope.launch {
            data?.value = service.getHomeData()
        }
    }
    fun login(controller: NavHostController) {
        val p: Pattern = Patterns.EMAIL_ADDRESS
        val isValidEmail = p.matcher(emailLogin).matches()
        if(!isValidEmail) {
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the valid email")
                )
            }
        }
        else if(passwordLogin.length<8){
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the password with minimum length 8")
                )
            }
        }
       else {
            firebaseAuth.signInWithEmailAndPassword(
                emailLogin,
                passwordLogin
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast("Login Success")
                        )
                    }
                    emailLogin = ""
                    passwordLogin = ""
                    controller.navigate("main")
                } else {
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast("Login Failure ${it.exception?.message}")
                        )
                    }
                }
            }
        }
    }
    fun signUp(controller: NavHostController) {
        val p: Pattern = Patterns.EMAIL_ADDRESS
        val isValidEmail = p.matcher(emailSignUp).matches()
        if(!isValidEmail){
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the valid email")
                )
            }
        }
        else if(passwordSignUp.length<8){
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the password with minimum length 8")
                )
            }
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(
                emailSignUp,
                passwordSignUp
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast("User Account Created")
                        )
                    }
                    controller.navigate("main")

                    emailSignUp = ""
                    passwordSignUp = ""
                } else {
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast("Login Failure ${it.exception?.message}")
                        )
                    }
                }
            }
        }
    }
}