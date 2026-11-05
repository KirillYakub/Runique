package com.kiras.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.StartIcon
import com.kiras.core.presentation.designsystem.StopIcon
import com.kiras.run.presentation.R

@Composable
fun RunControlButton(
    isRunning: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(isRunning) {
        Box(
            modifier = modifier
                .size(62.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(role = Role.Button, onClick = onStopClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = StopIcon,
                contentDescription = stringResource(R.string.stop_run),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(62.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(role = Role.Button, onClick = onStartClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = StartIcon,
                contentDescription = stringResource(R.string.start_run),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RunControlButtonPreview() {
    SplitlineTheme {
        RunControlButton(
            isRunning = true,
            onStartClick = {},
            onStopClick = {}
        )
    }
}
