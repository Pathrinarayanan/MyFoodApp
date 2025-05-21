package com.example.myfood

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun LoginTopBar(text:String){
    Row(
        Modifier.fillMaxWidth().height(150.dp)
            .background(colorResource(R.color.yellow_base))
            .padding(top = 10.dp, start = 20.dp)
        ,
        horizontalArrangement = Arrangement.spacedBy(120.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            Icons.Default.KeyboardArrowLeft,
            null,
            Modifier,
            tint = colorResource(R.color.orange_base)
        )
        Text(
            text,
            Modifier,
            color = Color.White,
            fontWeight = FontWeight.W600,
            fontSize = 32.sp

        )
    }
}

@Composable
fun SignUpScreen(viewModel: MainViewModel, controller: NavHostController){
    Scaffold(
        Modifier.fillMaxSize().background(Color.White),
        topBar = {
            LoginTopBar("Sign Up")
        }
    ) {innerPadding->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .background(colorResource(R.color.yellow_base))
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 36.dp)
                .padding(top =30.dp)
        ) {

            Text(
                "Welcome",
                Modifier,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )

            Text(
                "Name",
                Modifier.padding(top = 40.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )
            TextField(
                "Name",
                onValueChange = {

                },
                Modifier.padding(top = 10.dp).clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.yellow_light),
                    unfocusedContainerColor =  colorResource(R.color.yellow_light)
                )
            )

             Text(
                "Email or Phone Number",
                Modifier.padding(top = 40.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )
            TextField(
                viewModel.emailSignUp,
                onValueChange = {
                    viewModel.emailSignUp =it
                },
                Modifier.padding(top = 10.dp).clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.yellow_light),
                    unfocusedContainerColor =  colorResource(R.color.yellow_light)
                )
            )


            Text(
                "Password",
                Modifier.padding(top = 30.dp),
                fontSize =20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )

            TextField(
                viewModel.passwordSignUp,
                onValueChange = {
                    viewModel.passwordSignUp = it
                },
                Modifier.padding(top = 10.dp).clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.yellow_light),
                    unfocusedContainerColor =  colorResource(R.color.yellow_light)
                ),
            )

            Box(
                Modifier
                    .padding(top = 40.dp)
                    .wrapContentHeight().fillMaxWidth()
                    .clickable(){
                        viewModel.signUp(controller)
                    },
                contentAlignment = Alignment.Center

            ){

                Text(
                    "Sign Up",
                    Modifier
                        .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.W600,
                    fontSize = 24.sp
                )
            }
            Row(
                Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Already have an account? ",
                    Modifier.padding(top = 40.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.dark_font)
                )
                Text(
                    "Log In",
                    Modifier.padding(top = 40.dp).clickable{
                        controller.navigate("login")
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.orange_base)
                )
            }

        }
    }
}
@Composable
fun LoginScreen(viewModel: MainViewModel, controller: NavHostController){
    Scaffold(
        Modifier.fillMaxSize().background(Color.White),
        topBar = {
            LoginTopBar("Log In")
        }
    ) {innerPadding->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .background(colorResource(R.color.yellow_base))
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 36.dp)
                .padding(top =30.dp)
        ) {

            Text(
                "Welcome",
                Modifier,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )

            Text(
                "Email or Phone Number",
                Modifier.padding(top = 40.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )
            TextField(
                viewModel.emailLogin,
                onValueChange = {
                        viewModel.emailLogin = it
                },
                Modifier.padding(top = 10.dp).clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.yellow_light),
                    unfocusedContainerColor =  colorResource(R.color.yellow_light)
                )
            )


            Text(
                "Password",
                Modifier.padding(top = 30.dp),
                fontSize =20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.dark_font)
            )

            TextField(
                viewModel.passwordLogin,
                onValueChange = {
                        viewModel.passwordLogin = it
                },
                Modifier.padding(top = 10.dp).clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.yellow_light),
                    unfocusedContainerColor =  colorResource(R.color.yellow_light)
                ),
            )


            Text(
                "Forget Password",
                Modifier.padding(top = 20.dp).fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.orange_base),
                textAlign = TextAlign.End
            )
            Box(
                Modifier
                    .padding(top = 40.dp)
                    .wrapContentHeight().fillMaxWidth()
                    .clickable(){
                        viewModel.login(controller)
                    }
                ,
                contentAlignment = Alignment.Center

            ){

                Text(
                    "Log In",
                    Modifier
                        .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.W600,
                    fontSize = 24.sp
                )
            }
            Row(
                Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Don’t have an account? ",
                    Modifier.padding(top = 40.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.dark_font)
                )
                Text(
                    "Sign Up",
                    Modifier.padding(top = 40.dp).clickable{
                        controller.navigate("signup")
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.orange_base)
                )
            }

        }
        }
    }

