package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiras.core.presentation.designsystem.AnalyticsIcon
import com.kiras.core.presentation.designsystem.LogoutIcon
import com.kiras.core.presentation.designsystem.RuniqueTheme
import com.kiras.run.presentation.R

@Composable
fun HomeHeader(
    date: String,
    greeting: String,
    username: String,
    isAccountMenuOpen: Boolean,
    onAccountClick: () -> Unit,
    onAccountMenuDismiss: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onAccountClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            DropdownMenu(
                expanded = isAccountMenuOpen,
                onDismissRequest = onAccountMenuDismiss
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.analytics)) },
                    leadingIcon = {
                        Icon(
                            imageVector = AnalyticsIcon,
                            contentDescription = null
                        )
                    },
                    onClick = onAnalyticsClick
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.logout)) },
                    leadingIcon = {
                        Icon(
                            imageVector = LogoutIcon,
                            contentDescription = null
                        )
                    },
                    onClick = onLogoutClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeHeaderPreview() {
    RuniqueTheme {
        HomeHeader(
            date = "Tuesday, 21 Oct",
            greeting = "Good morning, Kyrylo",
            username = "Kyrylo",
            isAccountMenuOpen = false,
            onAccountClick = {},
            onAccountMenuDismiss = {},
            onAnalyticsClick = {},
            onLogoutClick = {}
        )
    }
}
