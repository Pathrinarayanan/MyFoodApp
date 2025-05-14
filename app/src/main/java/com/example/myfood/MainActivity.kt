package com.example.myfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfood.ui.theme.MyFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFoodTheme {

            }
        }
    }
}

@Preview
@Composable
fun HomeScreen(){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .background(colorResource(R.color.yellow_base))
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 16.dp)
        ){
            Row(
                modifier = Modifier.padding( vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ){
                for(i in 1..5){
                    CategoryItem()
                }
            }
            Text("Best Sellers", Modifier, fontSize = 20.sp, color = colorResource(R.color.dark_font), fontWeight = FontWeight.SemiBold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(10){
                    BestSellersItem()
                }
            }

        }
    }
}

@Composable
fun TopBar(){
    Column(
        modifier = Modifier.background(colorResource(R.color.yellow_base))
            .padding(top = 30.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
              ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBox()
            Spacer(modifier = Modifier.weight(1f))
            val icons = listOf(
                R.drawable.cart_white_icon,
                R.drawable.notify_icon,
                R.drawable.profile_icon
            )
            Row(
                Modifier,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icons.forEach {
                    TopIconItem(it)
                }
            }
        }
        Text(
            "Good Morning",
            Modifier,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.color_font)
        )
        Text(
            "Rise and shine! It's breakfast time",
            Modifier,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.orange_base)
        )
    }
}


@Composable
fun BottomBar(){
    Row(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
            .padding(horizontal = 30.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        val icons = listOf(
            R.drawable.bottom1,
            R.drawable.bottom2,
            R.drawable.bottom3,
            R.drawable.bottom4,
            R.drawable.bottom5,
        )
        icons.forEach {image->
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier =Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SearchBox(){
    Row(
        modifier = Modifier.width(200.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("Search")
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.size(20.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.orange_base)),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.filter_icon),
                contentDescription = null,
            )
        }
    }
}


@Composable
fun TopIconItem(i: Int) {
    Box(
        modifier = Modifier.size(26.dp).background(Color(0xffF5F5F5), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(i),
            contentDescription = null
        )
    }
}

@Composable
fun CategoryItem(){
    Column {
        Box(
            modifier = Modifier.height(75.dp).width(50.dp)
                .background(colorResource(R.color.yellow_light), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.snacks_icon),
                contentDescription = null
            )
        }
        Text("Snacks", Modifier, color = colorResource(R.color.dark_font) , fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun BestSellersItem(){
    Box(
        modifier = Modifier.width(160.dp).height(140.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.burger),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Row (
            Modifier,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier.wrapContentSize()
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Text("5.0")
            }
            Box(
                modifier = Modifier.wrapContentSize()
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.filled_heart),
                    null,

                    )
            }
        }
    }
}
