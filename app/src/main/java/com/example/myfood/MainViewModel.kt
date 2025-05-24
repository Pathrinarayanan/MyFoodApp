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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MainViewModel : ViewModel() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val service = RetrofitHelper.getInstance().create(ApiService::class.java)
    val data : MutableState<HomeData?>? = mutableStateOf<HomeData?>(null)
    var emailLogin by mutableStateOf("")
    var passwordLogin by mutableStateOf("")
    var emailSignUp by mutableStateOf("")
    var passwordSignUp by mutableStateOf("")
    var favoriteListIds : MutableList<Int> = mutableListOf<Int>()
     var _sharedFlow : MutableSharedFlow<FlowData> = MutableSharedFlow<FlowData>(replay = 0)
     var sharedFlow = _sharedFlow.asSharedFlow()
    var menuMap  by mutableStateOf<Map<Int, HomeMenuItem>>(emptyMap())
    fun fetchHomeData(){
        viewModelScope.launch {
            data?.value = service.getHomeData()
            menuItemsMap()
        }
    }
    fun menuItemsMap(){
        val menuList = (data?.value?.data?.restaurant?.menu?.best_sellers ?:emptyList()) +(data?.value?.data?.restaurant?.menu?.recommended ?:emptyList())
         menuMap = menuList.associateBy { it.id ?:0 }
    }
    fun getMenuItems(id : List<Int>) :List<HomeMenuItem>{
        return id.mapNotNull { menuMap[it] }
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
        if (!isValidEmail) {
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the valid email")
                )
            }
        } else if (passwordSignUp.length < 8) {
            viewModelScope.launch {
                _sharedFlow.emit(
                    FlowData.Toast("enter the password with minimum length 8")
                )
            }
        } else {
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
        fun fetchFavorites(){
            val uid = firebaseAuth.currentUser?.uid ?:""
            val docRef = db.collection("favorites").document(uid)
            docRef.get().addOnSuccessListener {
                if(it.exists()){
                    val favorites = it.get("favoriteIds") as? List<Int> ?:emptyList()
                    favoriteListIds = favorites.map { it.toInt() }.toMutableList()
                }
                else{
                    favoriteListIds = mutableListOf()
                }
            }
                .addOnFailureListener {
                    favoriteListIds = mutableListOf()
                }
        }

        fun removeFavorites(id : Int){
            val uid = firebaseAuth.currentUser?.uid ?:""
            val docRef = db.collection("favorites").document(uid)
            docRef.get().addOnSuccessListener {
                if(it.exists()){
                    docRef.update("favoriteIds", FieldValue.arrayRemove(id))
                        .addOnSuccessListener {
                            fetchFavorites()
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast("Removed to favorites")
                                )
                            }
                        }
                        .addOnFailureListener {exception->
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast(" Failure ${exception?.message}")
                                )
                            }
                        }
                }
            }
                .addOnFailureListener {exception->
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast(" Failure ${exception?.message}")
                        )
                    }
                }
        }

         fun addFavorites(id : Int){
            val uid = firebaseAuth.currentUser?.uid ?:""
            val docRef = db.collection("favorites").document(uid)

            docRef.get().addOnSuccessListener {
                if(it.exists()){
                    docRef.update("favoriteIds", FieldValue.arrayUnion(id))
                        .addOnSuccessListener {
                            fetchFavorites()
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast("Added to favorites")
                                )
                            }
                        }
                        .addOnFailureListener {exception->
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast(" Failure ${exception?.message}")
                                )
                            }
                        }
                }
                else{
                    val favoritesData = hashMapOf("favoriteIds" to listOf(id))
                    docRef.set(favoritesData, SetOptions.merge())
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast("Added to favorites")
                                )
                            }
                        }
                        .addOnFailureListener {exception->
                            viewModelScope.launch {
                                _sharedFlow.emit(
                                    FlowData.Toast(" Failure ${exception?.message}")
                                )
                            }
                        }
                }
            }
                .addOnFailureListener {exception->
                    viewModelScope.launch {
                        _sharedFlow.emit(
                            FlowData.Toast(" Failure ${exception?.message}")
                        )
                    }
                }
        }
    }
