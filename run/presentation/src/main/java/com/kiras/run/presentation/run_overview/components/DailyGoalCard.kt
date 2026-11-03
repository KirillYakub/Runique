package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.EditIcon
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.DailyGoalProgress
import com.kiras.run.domain.goal.DayGoalStatus
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.mappers.toDailyGoalUi
import com.kiras.run.presentation.run_overview.model.DailyGoalUi
import java.time.LocalDate

@Composable
fun DailyGoalCard(
    dailyGoal: DailyGoalUi?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if(dailyGoal == null) {
                    Modifier.clickable(
                        onClickLabel = stringResource(R.string.edit_daily_goal),
                        role = Role.Button,
                        onClick = onEditClick
                    )
                } else Modifier
            )
            .padding(16.dp)
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DailyGoalHeader(onEditClick = onEditClick)
        if(dailyGoal == null) {
            Text(
                text = stringResource(R.string.set_daily_goal_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            DailyGoalProgressRow(dailyGoal = dailyGoal)
            if(dailyGoal.streakDays > 0) {
                GoalChip(
                    text = pluralStringResource(
                        R.plurals.day_streak,
                        dailyGoal.streakDays,
                        dailyGoal.streakDays
                    ),
                    isSelected = false,
                    unselectedTextColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun DailyGoalHeader(onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.todays_goal).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { heading() }
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(
                    onClickLabel = stringResource(R.string.edit_daily_goal),
                    role = Role.Button,
                    onClick = onEditClick
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = EditIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.edit),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun DailyGoalProgressRow(dailyGoal: DailyGoalUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GoalRing(
            progress = dailyGoal.progress,
            percent = dailyGoal.percent,
            modifier = Modifier.size(112.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = dailyGoal.todayValue,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = stringResource(
                        R.string.goal_of_target,
                        dailyGoal.target,
                        dailyGoal.unit.asString()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alignByBaseline()
                )
            }
            Text(
                text = dailyGoal.remaining.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            GoalWeekStrip(
                week = dailyGoal.week,
                todayIndex = dailyGoal.todayIndex
            )
        }
    }
}

@Preview
@Composable
private fun DailyGoalCardPreview() {
    SplitlineTheme {
        DailyGoalCard(
            dailyGoal = DailyGoalProgress(
                todayValue = 3_200L,
                target = 5_000L,
                week = listOf(
                    DayGoalStatus.MET,
                    DayGoalStatus.TODAY,
                    DayGoalStatus.UPCOMING,
                    DayGoalStatus.UPCOMING,
                    DayGoalStatus.UPCOMING,
                    DayGoalStatus.REST,
                    DayGoalStatus.REST
                ),
                streakDays = 12
            ).toDailyGoalUi(GoalMetric.DISTANCE, DistanceUnit.KILOMETERS, LocalDate.of(2026, 10, 27)),
            onEditClick = {}
        )
    }
}

@Preview
@Composable
private fun DailyGoalCardEmptyPreview() {
    SplitlineTheme {
        DailyGoalCard(
            dailyGoal = null,
            onEditClick = {}
        )
    }
}
