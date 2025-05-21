package com.example.myfood

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myfood.ui.theme.MyFoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel : MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class)
        mainViewModel.fetchHomeData()
        initObservers()
        setContent {
            val controller = rememberNavController()
            MyFoodTheme {
                NavHost(modifier =Modifier.fillMaxSize(), navController =  controller, startDestination = if(mainViewModel.firebaseAuth.currentUser!=null) "main" else "login"){
                    composable("main") {
                        HomeScreen(mainViewModel)
                    }
                    composable("login") {
                        LoginScreen(mainViewModel, controller)
                    }
                    composable("signup") {
                        SignUpScreen(mainViewModel, controller)
                    }
                }
            }
        }
    }
    fun initObservers(){
        mainViewModel.viewModelScope.launch {
            mainViewModel.sharedFlow.collect {
                when(it){
                    is FlowData.Toast ->{
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}


@Composable
fun BestSellerItem(baseUrl1: String?, item: HomeMenuItem) {
    Box(
        Modifier.height(108.dp).width( width  = 72.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.BottomEnd
    ){
        Image(
            rememberAsyncImagePainter(baseUrl1 + item?.image_url),
            contentDescription = null,
            modifier =Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds

        )

        Box(
            modifier = Modifier
                .padding(bottom = 15.dp)
                .background(colorResource(R.color.orange_base),RoundedCornerShape(20.dp))
                .padding(5.dp)

        ){
            Text("$ ${item.price}", Modifier, color = Color.White, fontSize = 10.sp)
        }
    }
}


@Composable
fun HomeScreen(viewModel: MainViewModel){
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
                val items = listOf(
                    Item(R.drawable.snacks_icon, "Snacks"),
                    Item(R.drawable.meals_icon, "Meals"),
                    Item(R.drawable.vegan_icon, "Vegan"),
                    Item(R.drawable.desserts_icon, "Desserts"),
                    Item(R.drawable.drinks_icon, "Drinks"),
                )
                items.forEach{
                    CategoryItem(it)
                }
            }
            Spacer(
                Modifier.height(1.dp).fillMaxWidth()
                    .background(Color(0xffFFD8C7))
            )
            Row (
                Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "Best Sellers",
                    Modifier,
                    fontSize = 20.sp,
                    color = colorResource(R.color.dark_font),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "View All",
                    Modifier,
                    fontSize = 12.sp,
                    color = colorResource(R.color.orange_base),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier.padding(top =10.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
              val baseUrl = viewModel.data?.value?.base_image_url
              val bestSellersData = viewModel.data?.value?.data?.restaurant?.menu?.best_sellers

              bestSellersData?.forEach  {
                    BestSellerItem(baseUrl, it)
                }
            }
            val baseUrl = viewModel.data?.value?.base_image_url
            val offersData = viewModel.data?.value?.data?.restaurant?.menu?.advertise
            val pagerstate =rememberPagerState(initialPage = 0, pageCount = {offersData?.size ?:0})
            HorizontalPager(
                state = pagerstate,
                Modifier.height( 130.dp).fillMaxWidth()
            ) {
                LaunchedEffect(
                    offersData
                ) {
                    while(true){
                        val nextpage =( pagerstate.currentPage +1) % (offersData?.size?:1)
                        pagerstate.animateScrollToPage(nextpage)
                        delay(1500)
                    }
                }
                Box {
                    offersData?.forEach {
                        OfferWidget(baseUrl, it)
                    }
                }
            }
            Text("Recommened", Modifier.padding(top = 10.dp), fontSize = 20.sp, color = colorResource(R.color.dark_font), fontWeight = FontWeight.SemiBold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val baseUrl = viewModel.data?.value?.base_image_url
                val recommended = viewModel.data?.value?.data?.restaurant?.menu?.recommended

               recommended?.forEach { recommendedItem->
                   item {
                       RecommendedItem(baseUrl?:"",recommendedItem)
                   }
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
fun CategoryItem(item: Item) {
    Column {
        Box(
            modifier = Modifier.height(75.dp).width(50.dp)
                .background(colorResource(R.color.yellow_light), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(item.img),
                contentDescription = null
            )
        }
        Text(item.title, Modifier.padding(start = 5.dp,top =5.dp), color = colorResource(R.color.dark_font) , fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun OfferWidget(baseUrl: String?, item: AdvertiseItem) {
    Row(
        Modifier
            .padding(top =20.dp)
            .height(130.dp).background(colorResource(R.color.orange_base),RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),

    ){
        Column(
            Modifier.weight(0.5f)
                .padding(start = 16.dp,top =16.dp)
        ){
            Text(item.tagline?:"", Modifier, fontSize = 16.sp,color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(item?.offer?:"", Modifier, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold,overflow = TextOverflow.Ellipsis)
        }
        Image(
            painter = rememberAsyncImagePainter(baseUrl + item.image_url),
            contentDescription = null,
            Modifier
                .weight(0.5f).fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun RecommendedItem(url : String,item: HomeMenuItem) {
    Box(
        modifier = Modifier.width(160.dp).height(140.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            rememberAsyncImagePainter(url + item.image_url),
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

data class Item(
    val img :  Int,
    val title : String
)