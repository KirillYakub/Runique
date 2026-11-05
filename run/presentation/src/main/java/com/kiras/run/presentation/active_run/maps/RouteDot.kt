package com.kiras.run.presentation.active_run.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.SplitlineTheme

@Composable
fun RouteDot(
    color: Color,
    ringWidth: Dp,
    showHalo: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(if(showHalo) 32.dp else 16.dp)
            .clip(CircleShape)
            .background(if(showHalo) color.copy(alpha = 0.25f) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
                .border(ringWidth, MaterialTheme.colorScheme.background, CircleShape)
        )
    }
}

@Preview
@Composable
private fun RouteDotPreview() {
    SplitlineTheme {
        RouteDot(
            color = MaterialTheme.colorScheme.primary,
            ringWidth = 3.dp,
            showHalo = true
        )
    }
}
