package com.rodrigocardenas.rickmortyapp.ui.activities.splash

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.graphicsLayer
import com.rodrigocardenas.rickmortyapp.R

@Composable
fun SplashScreen() {
    val logoOffsetY = remember { Animatable(150f) }
    val logoAlpha = remember { Animatable(0f) }

    val textOffsetY = remember { Animatable(150f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = TweenSpec(durationMillis = 1000)
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = TweenSpec(durationMillis = 1000)
        )
    }

    LaunchedEffect(Unit) {
        textOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = TweenSpec(durationMillis = 1000)
        )
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = TweenSpec(durationMillis = 1000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF262E47))
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .graphicsLayer(
                    translationY = logoOffsetY.value,
                    alpha = logoAlpha.value
                )
        )

        Text(
            text = "Designed by - Rodrigo Cárdenas",
            color = Color(0xFFFAFAFA),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp)
                .graphicsLayer(
                    translationY = textOffsetY.value,
                    alpha = textAlpha.value
                )
        )
    }
}
