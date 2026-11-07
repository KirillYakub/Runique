package com.kiras.run.presentation.active_run.mappers

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.run.domain.RunSplit
import com.kiras.run.presentation.active_run.model.RunSplitUi
import com.kiras.run.presentation.util.toBarFraction
import com.kiras.run.presentation.util.toSplitValueText

fun List<RunSplit>.toRunSplitUis(
    unit: DistanceUnit,
    paceFormat: PaceFormat
): List<RunSplitUi> {
    val fastestSplit = minByOrNull { it.duration } ?: return emptyList()
    return map { split ->
        RunSplitUi(
            number = split.number,
            valueText = split.duration.toSplitValueText(unit.metersPerUnit, unit, paceFormat),
            barFraction = split.duration.toBarFraction(fastestSplit.duration),
            isFastest = split.number == fastestSplit.number,
            endLocation = split.endLocation
        )
    }
}
