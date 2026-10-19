package com.example.streamdemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.streamdemo.ui.player.PlayerScreen
import com.example.streamdemo.ui.videoList.VideoListScreen

object Routes{
    const val LIST = "list"
    const val PLAYER = "player/{videoId}"
    const val VIDEO_ID = "videoId"
    fun player(videoId : String) = "player/$videoId"
}

@Composable
fun StreamDemoNavHost(modifier: Modifier = Modifier){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier
    ){
        composable(Routes.LIST) {
            VideoListScreen(
                onVideoClick = { id -> navController.navigate(Routes.player(id)) }
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(navArgument(Routes.VIDEO_ID){
                type = NavType.StringType
            })

        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?. getString(Routes.VIDEO_ID)

            //Temp placeholder : the real PlayerScreen comes in

            PlayerScreen()
        }
    }
}