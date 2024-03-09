package com.example.memeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memeapp.models.Meme
import com.example.memeapp.ui.DetailScreen
import com.example.memeapp.ui.MainScreen
import com.example.memeapp.ui.theme.MemeAppTheme
import com.example.memeapp.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeAppTheme {
                val navController = rememberNavController()
                var memesList by remember {
                    mutableStateOf(listOf<Meme>())
                }
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = true){
                    scope.launch(Dispatchers.IO) {
                        val response = try {
                            RetrofitInstance.api.getMemesList()
                        } catch (e: IOException) {
                            Toast.makeText(
                                applicationContext,
                                "IO error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        } catch (e: HttpException) {
                            Toast.makeText(
                                applicationContext,
                                "http error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                        if (response.isSuccessful && response !=null){
                            withContext(Dispatchers.Main){
                                memesList = response.body()!!.data.memes
                            }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "MainScreen"){

                    composable(route = "MainScreen"){
                        MainScreen(memesList = memesList, navController = navController)
                    }
                    composable(route = "DetailScreen?name={name}&url={url}",
                    arguments = listOf(
                        navArgument(name = "name"){
                            type= NavType.StringType
                        },
                        navArgument(name = "url"){
                            type= NavType.StringType
                        },

                    )
                    ){
                        DetailScreen(
                            name = it.arguments?.getString("name"),
                            url = it.arguments?.getString("url")
                        )
                    }

                }
            }
        }
    }
}