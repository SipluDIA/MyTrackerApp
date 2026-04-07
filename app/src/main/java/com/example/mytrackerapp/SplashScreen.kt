package com.example.mytrackerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mytrackerapp.ui.theme.Black
import com.example.mytrackerapp.ui.theme.BtnGreen
import com.example.mytrackerapp.ui.theme.GradientEnd
import com.example.mytrackerapp.ui.theme.GradientStart
import com.example.mytrackerapp.ui.theme.Grey1
import com.example.mytrackerapp.ui.theme.poppinsFamily

@Composable
fun SplashScreen(navController: NavHostController) {
    Box {


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img3),
                contentDescription = "Login Image",
                modifier = Modifier
                    .size(345.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 36.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        color = BtnGreen
                    ),
                ) {
                    append("FIT")
                }
                withStyle(
                    style = SpanStyle(
                        color = Black, fontSize = 34.sp,
                        fontFamily = poppinsFamily, fontWeight = FontWeight.Bold
                    )
                ) {
                    append("TRACK")
                }
            })

            Text(
                "Everybody Can Train",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = Grey1
            )


        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 55.dp, end = 55.dp, bottom = 65.dp)

                .background(
                    color = BtnGreen,
                    shape = ButtonDefaults.shape
                )
                .height(55.dp),
            onClick = {navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text(
                "Get Started", color = Color.White, fontSize = 16.sp,
                fontFamily = poppinsFamily, fontWeight = FontWeight.Bold
            )
        }
    }
}