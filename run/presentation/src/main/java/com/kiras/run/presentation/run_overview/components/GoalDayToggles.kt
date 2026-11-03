package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiras.run.presentation.util.toFullName
import com.kiras.run.presentation.util.toNarrowName
import java.time.DayOfWeek

@Composable
fun GoalDayToggles(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DayOfWeek.entries.forEach { day ->
            val isSelected = day in selectedDays
            val fullName = day.toFullName()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if(isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .toggleable(
                        value = isSelected,
                        role = Role.Checkbox,
                        onValueChange = { onDayToggle(day) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toNarrowName(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if(isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clearAndSetSemantics { contentDescription = fullName }
                )
            }
        }
    }
}
