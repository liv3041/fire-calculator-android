package com.toonandtools.firecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toonandtools.firecalculator.ui.theme.FireCalculatorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FireCalculatorTheme {
                val data = listOf(30f, 80f, 60f, 100f) // Sample data
                AnimatedBarGraph(data = data, modifier = Modifier.fillMaxHeight())
            }
        }
    }
}

@Composable
fun AnimatedBarGraph(
    data: List<Float>, // Values for the graph
    modifier: Modifier = Modifier
) {
    val maxDataValue = data.maxOrNull() ?: 0f
    val animatedValues = remember { data.map { Animatable(0f) } }
    val isAlternating = remember { mutableStateOf(false) }
    LaunchedEffect(data) {
        animatedValues.forEachIndexed { index, animatable ->
            launch {
                animatable.animateTo(
                    data[index],
                    animationSpec = tween(durationMillis = if (index % 2 == 0) 1000 else 2000)
                )
                while (true){
                    delay(1000)
//                    isAlternating.value = !isAlternating.value
                    animatedValues.forEachIndexed { index, animatable ->
                        launch {
                            val newTarget = if (index % 2 == 0) {
                                data[index] * 0.5f
                            }else {
                                data[index] * 1.5f
                            }
                            animatable.animateTo(
                                targetValue = newTarget.coerceIn(0f, maxDataValue), // Stay within bounds
                                animationSpec = tween(durationMillis = 1000)
                            )
                        }
                    }
                    delay(1000)
                    animatedValues.forEachIndexed { index, animatable ->
                        launch {
                            val newTarget = if (index % 2 == 0) {
                                data[index] * 1.5f // Even indices go 50% higher
                            } else {
                                data[index] * 0.5f // Odd indices go halfway down
                            }
                            animatable.animateTo(
                                targetValue = newTarget.coerceIn(0f, maxDataValue),
                                animationSpec = tween(durationMillis = 1000)
                            )
                        }
                    }
                    delay(1000) // Pause before restarting the loop
                }


            }
        }
    }
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val list = listOf(colorResource(R.color.HoneyFlower),colorResource(R.color.Amaranth), colorResource(R.color.BrightSun), colorResource(R.color.Amazon))

        Canvas(modifier = Modifier.size(300.dp)) {
            val barWidth = size.width / data.size
            val graphHeight = size.height / 2


            data.forEachIndexed { index, _ ->
                val xOffset = barWidth * index
                val barHeight = (animatedValues[index].value / maxDataValue) * graphHeight
                val color = list[index % list.size]
                drawRect(
                    color =color,
                    topLeft = Offset(xOffset, graphHeight - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAnimatedBarGraph() {
    val data = listOf(30f, 80f, 60f, 100f, 50f) // Sample data
    AnimatedBarGraph(data = data, modifier = Modifier.size(300.dp))
}


