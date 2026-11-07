package com.kiras.run.presentation.active_run.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.designsystem.KeyboardArrowDownIcon
import com.kiras.core.presentation.designsystem.KeyboardArrowUpIcon
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.run.presentation.R
import com.kiras.run.presentation.active_run.model.RunSplitUi
import com.kiras.run.presentation.components.SplitRow
import com.kiras.run.presentation.util.toSplitsLabelRes
import com.kiras.run.presentation.util.toUnitLabelRes

@Composable
fun SplitsCard(
    splits: List<RunSplitUi>,
    isExpanded: Boolean,
    distanceUnit: DistanceUnit,
    paceFormat: PaceFormat,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val listState = rememberLazyListState()

    LaunchedEffect(splits.size, isExpanded) {
        if(isExpanded && splits.isNotEmpty()) {
            listState.animateScrollToItem(splits.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest, shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(role = Role.Button, onClick = onToggle)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.splits),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(distanceUnit.toSplitsLabelRes(paceFormat)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = if(isExpanded) KeyboardArrowUpIcon else KeyboardArrowDownIcon,
                contentDescription = stringResource(
                    if(isExpanded) R.string.hide_splits else R.string.show_splits
                ),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(clip = true)
            },
            contentAlignment = Alignment.TopCenter,
            label = "splits_content",
            modifier = Modifier.fillMaxWidth()
        ) { expanded ->
            if(expanded) {
                if(splits.isEmpty()) {
                    Text(
                        text = stringResource(
                            R.string.splits_empty,
                            stringResource(distanceUnit.toUnitLabelRes())
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .animateContentSize()
                            .heightIn(max = 140.dp)
                    ) {
                        items(
                            items = splits,
                            key = { it.number }
                        ) { split ->
                            SplitRow(
                                label = split.number.toString(),
                                barFraction = split.barFraction,
                                valueText = split.valueText,
                                isFastest = split.isFastest,
                                isPartial = false,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                paceFormat = paceFormat
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview
@Composable
private fun SplitsCardCollapsedPreview() {
    SplitlineTheme {
        SplitsCard(
            splits = emptyList(),
            isExpanded = false,
            distanceUnit = DistanceUnit.KILOMETERS,
            paceFormat = PaceFormat.PACE,
            onToggle = {}
        )
    }
}

@Preview
@Composable
private fun SplitsCardEmptyPreview() {
    SplitlineTheme {
        SplitsCard(
            splits = emptyList(),
            isExpanded = true,
            distanceUnit = DistanceUnit.KILOMETERS,
            paceFormat = PaceFormat.PACE,
            onToggle = {}
        )
    }
}

@Preview
@Composable
private fun SplitsCardExpandedPreview() {
    SplitlineTheme {
        SplitsCard(
            splits = listOf(
                RunSplitUi(1, "5:34", 0.79f, false, Location(50.1, 14.4)),
                RunSplitUi(2, "5:26", 0.85f, false, Location(50.11, 14.41)),
                RunSplitUi(3, "5:08", 1f, true, Location(50.12, 14.42))
            ),
            isExpanded = true,
            distanceUnit = DistanceUnit.KILOMETERS,
            paceFormat = PaceFormat.PACE,
            onToggle = {}
        )
    }
}
