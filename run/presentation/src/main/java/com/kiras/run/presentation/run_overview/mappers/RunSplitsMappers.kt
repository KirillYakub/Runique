package com.kiras.run.presentation.run_overview.mappers

import com.kiras.core.domain.run.RecordedSplit
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.run.presentation.run_overview.model.RunSplitRowUi
import com.kiras.run.presentation.run_overview.model.RunSplitsUi
import com.kiras.run.presentation.util.toBarFraction
import com.kiras.run.presentation.util.toPartialSplitLabel
import com.kiras.run.presentation.util.toSplitValueText
import com.kiras.run.presentation.util.toSplitsLabelRes
import com.kiras.run.presentation.util.toUnitLabelRes

fun List<RecordedSplit>.toRunSplitsUi(
    unit: DistanceUnit,
    paceFormat: PaceFormat,
    averageText: String
): RunSplitsUi? {
    val splits = filter { it.unit == unit }
    if(splits.isEmpty()) {
        return null
    }
    val fastestSplit = splits
        .filter { it.distanceMeters >= unit.metersPerUnit }
        .minByOrNull { it.duration }

    return RunSplitsUi(
        rows = splits.map { split ->
            val isPartial = split.distanceMeters < unit.metersPerUnit
            RunSplitRowUi(
                label = if(isPartial) {
                    split.distanceMeters.toPartialSplitLabel(unit)
                } else {
                    split.number.toString()
                },
                valueText = split.duration.toSplitValueText(split.distanceMeters, unit, paceFormat),
                barFraction = if(fastestSplit == null) {
                    1f
                } else {
                    (split.duration * (unit.metersPerUnit / split.distanceMeters))
                        .toBarFraction(fastestSplit.duration)
                },
                isFastest = split.number == fastestSplit?.number,
                isPartial = isPartial
            )
        },
        labelRes = unit.toSplitsLabelRes(paceFormat),
        unitLabelRes = unit.toUnitLabelRes(),
        paceFormat = paceFormat,
        averageText = averageText,
        fastestNumber = fastestSplit?.number,
        fastestValueText = fastestSplit?.let {
            it.duration.toSplitValueText(it.distanceMeters, unit, paceFormat)
        }
    )
}
