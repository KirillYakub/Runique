package com.kiras.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.HomeIcon
import com.kiras.core.presentation.designsystem.R
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.StatisticsIcon
import com.kiras.core.presentation.designsystem.components.util.BottomBarTab

@Composable
fun SplitlineBottomBar(
    selectedTab: BottomBarTab,
    onTabClick: (BottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            BottomBarItem(
                icon = HomeIcon,
                label = stringResource(R.string.home),
                isSelected = selectedTab == BottomBarTab.HOME,
                onClick = { onTabClick(BottomBarTab.HOME) },
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                icon = StatisticsIcon,
                label = stringResource(R.string.statistics),
                isSelected = selectedTab == BottomBarTab.STATISTICS,
                onClick = { onTabClick(BottomBarTab.STATISTICS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor =
        if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                role = Role.Tab,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if(isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if(isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor
        )
    }
}

@Preview
@Composable
private fun SplitlineBottomBarPreview() {
    SplitlineTheme {
        SplitlineBottomBar(
            selectedTab = BottomBarTab.HOME,
            onTabClick = {}
        )
    }
}
