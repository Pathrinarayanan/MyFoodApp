package com.example.myfood

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.myfood.ui.theme.MyFoodTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel : MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class)
        mainViewModel.fetchHomeData()
        mainViewModel.fetchFavorites()
        mainViewModel.fetchCart()
        initObservers()
        setContent {
            val controller = rememberNavController()
            MyFoodTheme {
                NavHost(modifier =Modifier.fillMaxSize(), navController =  controller, startDestination = if(mainViewModel.firebaseAuth.currentUser!=null) "main/0" else "login"){
                    composable("main/{index}", arguments = listOf(
                        navArgument("index"){
                            type = NavType.IntType
                        }
                    )) {
                        val index = it.arguments?.getInt("index") ?:0
                        MainScreen(mainViewModel,controller, index)
                    }
                    composable("login") {
                        LoginScreen(mainViewModel, controller)
                    }
                    composable("signup") {
                        SignUpScreen(mainViewModel, controller)
                    }
                    composable ("pd/{MenuItem}",
                        arguments = listOf(navArgument("MenuItem"){
                            type= NavType.StringType
                        })){ currentBackStackEntry->
                        val arguements = currentBackStackEntry.arguments?.getString("MenuItem")
                        val decodeString = URLDecoder.decode(arguements, StandardCharsets.UTF_8.toString())
                        val productData = Json.decodeFromString<HomeMenuItem>(decodeString)
                        PDpage(productData, viewModel = mainViewModel)
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
fun CartItem(item: HomeMenuItem, baseUrl: String, count: Int,addCount:()->Unit,
             minusCount:()->Unit) {
    Row(
        Modifier.fillMaxWidth().wrapContentHeight()
            .background(colorResource(R.color.orange_base)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(Modifier
            .padding(16.dp)
            .size(80.dp).clip(RoundedCornerShape(20.dp))) {
            Image(
                rememberAsyncImagePainter(baseUrl +item?.image_url),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
        Column(Modifier
            .weight(0.8f)
        ) {

            Text(
                item?.name ?:"",
                Modifier,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 24.sp
                )
            Text(
                "${count.toInt() * (item?.price?:0).toInt()}",
                Modifier,
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Row(
            Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Box(
                Modifier
                    .size(20.dp)
                    .background(Color.White,CircleShape).clickable{
                        minusCount()
                    },
                contentAlignment = Alignment.Center
            ){
                Image(painterResource(R.drawable.minus_icon),
                    contentDescription = null)
            }
            Text(count.toString(),
                Modifier,
                color = Color.White,
                fontSize = 20.sp)
            Box(
                Modifier
                    .size(20.dp)
                    .background(Color.White,CircleShape)
                    .clickable{
                        addCount()
                    },
                contentAlignment = Alignment.Center
            ){
                Image(painterResource(R.drawable.plus_icon),
                    contentDescription = null)
            }
        }
    }
}

@Composable
fun CartDrawer(viewModel: MainViewModel) {
    var isEmptyCart = viewModel.cartIds.isEmpty()
    var cartItems = viewModel.getMenuItems(viewModel.cartIds)
    var baseUrl = viewModel.data?.value?.base_image_url ?: ""
    Column(
        Modifier.fillMaxSize()
            .clip(RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            .background(colorResource(R.color.orange_base))
            .padding(16.dp)
            .padding(end = 60.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.cart_white_icon),
                contentDescription = null,
                Modifier.padding(end = 16.dp)
            )
            Text(
                "Cart", Modifier, color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp
            )
        }
        Spacer(
            Modifier.padding(top = 20.dp).height(1.dp).fillMaxWidth()
                .background(colorResource(R.color.yellow_light))
        )
        Text(
            if (isEmptyCart)
                "Your Cart is empty"
            else "You have ${viewModel.cartIds.size ?:0 } items in cart",
            Modifier.fillMaxWidth().padding(top = 16.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        if (isEmptyCart) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(R.drawable.add_to_cart_icon),
                        contentDescription = null
                    )
                    Text(
                        "Want To Add Something?", Modifier.fillMaxWidth(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            cartItems?.forEach {
                CartItem(it, baseUrl, viewModel.cartMap[it?.id  ?:0] ?:1,
                    addCount = {
                        viewModel.cartMap[it?.id ?:0] = (viewModel.cartMap[it?.id ]?:0) +1
                        viewModel.total  = viewModel.total + (it.price?:0).toInt()
                    },
                    minusCount = {
                        if(  viewModel.cartMap[it?.id ?:0] == 1){
                            viewModel.removeCart(it?.id ?:0)
                            return@CartItem
                        }
                        viewModel.cartMap[it?.id ?:0] = (viewModel.cartMap[it?.id ]?:0) -1
                        viewModel.total -= (it.price ?:0).toInt()
                    })
            }


            Row(Modifier.padding(top = 20.dp)) {
                Text(
                    "SubTotal",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "$${viewModel.total}",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
            Row(Modifier.padding(top = 20.dp)) {
                Text(
                    "Tax And Fee",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "$${viewModel.total * 0.18}",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
            Row(Modifier.padding(top = 20.dp)) {
                Text(
                    "Delivery",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "$40.00",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
            Spacer(
                Modifier
                    .padding(top = 20.dp)
                    .height(1.dp).fillMaxWidth().background(colorResource(R.color.yellow_light))
            )
            Row(Modifier.padding(top = 20.dp)) {
                Text(
                    "Total",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "$ ${(viewModel.total) + (viewModel.total*0.18) + 40}",
                    Modifier,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
            Box(Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .padding(top = 20.dp)
                        .wrapContentHeight()
                        .background(colorResource(R.color.yellow_base), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        "Checkout",
                        Modifier,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.orange_base)
                    )
                }
            }
        }
    }
}

@Composable
fun PDpage(productData: HomeMenuItem,viewModel: MainViewModel) {
    var isFavorite = remember {  mutableStateOf( viewModel.favoriteListIds.contains(productData.id))}
    Scaffold (
        Modifier,
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(colorResource(R.color.yellow_base))
                    .padding(top = 10.dp, start = 20.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ){
                Row(Modifier.weight(0.8f)) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        null,
                        Modifier,
                        tint = colorResource(R.color.orange_base)
                    )
                    Text(
                        productData.name ?:"",
                        Modifier,
                        color = colorResource(R.color.dark_font),
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp

                    )
                }
                Image(
                    painterResource( if(isFavorite.value) R.drawable.filled_heart else R.drawable.closed_heart ),
                    contentDescription = null,
                    Modifier
                        .padding(end = 36.dp)
                        .size(24.dp)
                        .clickable{
                            if(isFavorite.value){
                                viewModel.removeFavorites(productData.id ?:0)
                            }
                            else{
                                viewModel.addFavorites(productData.id ?:0)
                            }
                            isFavorite.value = !isFavorite.value
                        }
                )
            }
        }
    ){innerPadding->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .background(colorResource(R.color.yellow_base))
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 36.dp)
                .padding(top = 30.dp)
        ) {
            Box(
                Modifier
                    .padding(bottom = 36.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                val baseUrl = viewModel.data?.value?.base_image_url
                Image(
                    rememberAsyncImagePainter(baseUrl+productData?.image_url),
                    contentDescription = null,
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.FillBounds
                )
            }
            Text(
                "$ ${productData?.price}",
                Modifier.padding(bottom = 20.dp), fontWeight = FontWeight.Bold,
                color = colorResource(R.color.orange_base),
                fontSize = 40.sp
            )

            Text(
                productData?.name ?:"",
                Modifier.padding(bottom = 20.dp), fontWeight = FontWeight.Bold,
                color = colorResource(R.color.dark_font),
                fontSize = 25.sp
            )

            Text(
                productData?.description?:"",
                Modifier, fontWeight = FontWeight.Medium,
                color = colorResource(R.color.dark_font),
                fontSize = 18.sp
            )
            Box (
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ){
                Row(
                    Modifier
                        .padding(top = 20.dp)
                        .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                        .clickable{
                            if(!viewModel.cartIds.contains(productData?.id)) {
                                viewModel.addCart(productData?.id ?: 0)
                            }
                        }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painterResource(R.drawable.cart_white_icon),
                        contentDescription = null
                    )
                    Text(
                        "Add To Cart",
                        Modifier,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun BestSellerItem(baseUrl1: String?, item: HomeMenuItem?, onProductClick:()->Unit) {
    Box(
        Modifier
            .height(108.dp)
            .width(width = 72.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                onProductClick()
            },
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
                .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
                .padding(5.dp)

        ){
            Text("$ ${item?.price}", Modifier, color = Color.White, fontSize = 10.sp)
        }
    }
}

@Composable
fun FavoritesItem(data: HomeMenuItem, baseUrl1: String?, onProductClick:()->Unit) {
    Column(Modifier.fillMaxWidth().wrapContentHeight()) {
        Box(
            Modifier
                .height(108.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    onProductClick()
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                rememberAsyncImagePainter(baseUrl1 + data?.image_url),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Box(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
                    .padding(5.dp)

            ) {
                Text("$ ${data?.price}", Modifier, color = Color.White, fontSize = 10.sp)
            }
        }
        Text(data?.name ?:"", Modifier.padding(start = 8.dp, top = 8.dp), color = colorResource(R.color.dark_font), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

    }
}

@Composable
fun MainScreen(viewModel: MainViewModel,controller: NavController, index : Int) {
    var currentIndex = remember { mutableStateOf(index) }
    val sheetwidth = LocalConfiguration.current.screenWidthDp.dp / 3
    val offsetX = remember { Animatable(sheetwidth.value) }

    Box {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(viewModel)
            },
            bottomBar = {
                BottomBar() {
                    controller.navigate("main/${it}")
                }
            }
        ) { innerPadding ->
                      Column(Modifier.fillMaxSize().padding(innerPadding)) {
                when (currentIndex.value) {
                    0 -> HomeScreen(viewModel, controller)
                    2 -> FavoritesScreen(viewModel, controller)
                }

            }
        }

        AnimatedVisibility(
            visible = viewModel.cartVisible,
            modifier = Modifier.fillMaxSize(),
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Box(
                Modifier.fillMaxHeight()
                    .width(sheetwidth)
                    .offset {
                        IntOffset(offsetX.value.toInt(), y = 0)
                    }
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX.value > sheetwidth.value * 0.5f) {
                                    viewModel.cartVisible = false
                                } else {
                                    viewModel.viewModelScope.launch {
                                        offsetX.animateTo(0f)
                                    }
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                viewModel.viewModelScope.launch {
                                    offsetX.snapTo(
                                        (offsetX.value + dragAmount).coerceIn(
                                            0f,
                                            sheetwidth.value
                                        )
                                    )
                                }

                            }
                        )
                    }
            ) {
                CartDrawer(viewModel)
            }
        }
    }
}
@Composable
fun HomeScreen(viewModel: MainViewModel, controller: NavController){
    Column (
        modifier = Modifier
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
            Modifier
                .height(1.dp)
                .fillMaxWidth()
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
            modifier = Modifier
                .padding(top = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            val baseUrl = viewModel.data?.value?.base_image_url
            val bestSellersData = viewModel.data?.value?.data?.restaurant?.menu?.best_sellers

            bestSellersData?.forEach  {
                BestSellerItem(baseUrl, it){
                    val gson = Gson()
                    val dataString = gson.toJson(it)
                    val encodeString = URLEncoder.encode(dataString, StandardCharsets.UTF_8.toString())
                    controller.navigate("pd/${encodeString}")
                }
            }
        }
        val baseUrl = viewModel.data?.value?.base_image_url
        val offersData = viewModel.data?.value?.data?.restaurant?.menu?.advertise
        val pagerstate =rememberPagerState(initialPage = 0, pageCount = {offersData?.size ?:0})
        HorizontalPager(
            state = pagerstate,
            Modifier
                .height(130.dp)
                .fillMaxWidth()
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
                    RecommendedItem(baseUrl?:"",recommendedItem){
                        val gson = Gson()
                        val dataString = gson.toJson(recommendedItem)
                        val encodeString = URLEncoder.encode(dataString, StandardCharsets.UTF_8.toString())
                        controller.navigate("pd/${encodeString}")
                    }
                }
            }
        }

    }
}

@Composable
fun TopBar(viewModel: MainViewModel){
    Column(
        modifier = Modifier
            .background(colorResource(R.color.yellow_base))
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
                icons.forEachIndexed { index, it ->
                    TopIconItem(it){
                        if(index == 0) {//which  is cart
                            viewModel.cartVisible = true
                        }
                    }
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
fun BottomBar(selectedIndex :(Int)->Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
        icons.forEachIndexed {index,image->
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier =Modifier.size(24.dp)
                    .clickable{
                        selectedIndex(index)
                    }
            )
        }
    }
}

@Composable
fun SearchBox(){
    Row(
        modifier = Modifier
            .width(200.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("Search")
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(20.dp)
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
fun TopIconItem(i: Int,onClick:()->Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(Color(0xffF5F5F5), RoundedCornerShape(12.dp))
            .clickable{
                onClick()
            },
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
            modifier = Modifier
                .height(75.dp)
                .width(50.dp)
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
            .padding(top = 20.dp)
            .height(130.dp)
            .background(colorResource(R.color.orange_base), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),

    ){
        Column(
            Modifier
                .weight(0.5f)
                .padding(start = 16.dp, top = 16.dp)
        ){
            Text(item.tagline?:"", Modifier, fontSize = 16.sp,color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(item?.offer?:"", Modifier, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold,overflow = TextOverflow.Ellipsis)
        }
        Image(
            painter = rememberAsyncImagePainter(baseUrl + item.image_url),
            contentDescription = null,
            Modifier
                .weight(0.5f)
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun RecommendedItem(url : String,item: HomeMenuItem, onProductclick:()->Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onProductclick()
            }
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
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Text("5.0")
            }
            Box(
                modifier = Modifier
                    .wrapContentSize()
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


@Composable
fun FavoritesScreen(viewModel: MainViewModel, controller: NavController) {
    val favoritesData = viewModel.getMenuItems( viewModel.favoriteListIds)
    val baseUrl = viewModel.data?.value?.base_image_url
        Column (
            modifier = Modifier
                .background(colorResource(R.color.yellow_base))
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 36.dp)
                .padding(top =30.dp)
        ) {
            Text(
                "Your Favorites Items",
                Modifier,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                color = colorResource(R.color.orange_base)
                )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(favoritesData){ index, data->
                    FavoritesItem(data,baseUrl){
                        val gson = Gson()
                        val dataString = gson.toJson(data)
                        val encodeString = URLEncoder.encode(dataString, StandardCharsets.UTF_8.toString())
                        controller.navigate("pd/${encodeString}")
                    }
                }


        }
        }
    }













