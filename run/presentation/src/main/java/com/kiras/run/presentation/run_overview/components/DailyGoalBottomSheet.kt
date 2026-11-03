package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.model.GoalDraftUi
import com.kiras.run.presentation.util.toGoalPresetText
import com.kiras.run.presentation.util.toGoalValueText
import com.kiras.run.presentation.util.toStepperConfig
import com.kiras.run.presentation.util.toUnitText
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalBottomSheet(
    draft: GoalDraftUi,
    distanceUnit: DistanceUnit,
    onDismiss: () -> Unit,
    onMetricChange: (GoalMetric) -> Unit,
    onDecrementClick: () -> Unit,
    onIncrementClick: () -> Unit,
    onPresetClick: (Long) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val hideSheetThen: (() -> Unit) -> Unit = { action ->
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }
    val config = draft.metric.toStepperConfig(distanceUnit)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.daily_goal),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = stringResource(R.string.daily_goal_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            GoalMetricSegmentedControl(
                selectedMetric = draft.metric,
                onMetricChange = onMetricChange,
                modifier = Modifier.fillMaxWidth()
            )
            GoalStepper(
                value = draft.target.toGoalValueText(draft.metric, distanceUnit),
                unit = draft.metric.toUnitText(distanceUnit).asString(),
                canDecrement = draft.target > config.minTarget,
                canIncrement = draft.target < config.maxTarget,
                onDecrementClick = onDecrementClick,
                onIncrementClick = onIncrementClick,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                config.presetTargets.forEach { preset ->
                    GoalChip(
                        text = preset.toGoalPresetText(draft.metric, distanceUnit).asString(),
                        isSelected = preset == draft.target,
                        onClick = { onPresetClick(preset) }
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.goal_days),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        text = stringResource(R.string.rest_days_keep_streak),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
                GoalDayToggles(
                    selectedDays = draft.goalDays,
                    onDayToggle = onDayToggle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GoalSheetButton(
                    text = stringResource(R.string.cancel),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    isEnabled = true,
                    onClick = { hideSheetThen(onDismiss) },
                    modifier = Modifier.weight(1f)
                )
                GoalSheetButton(
                    text = stringResource(R.string.save_goal),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    isEnabled = draft.goalDays.isNotEmpty(),
                    onClick = { hideSheetThen(onSaveClick) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GoalSheetButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        modifier = modifier.heightIn(min = 52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
