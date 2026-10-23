package com.example.drivenext.presentation.common

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

private const val DATE_PICKER_TAG = "rental_date_picker"
private const val TIME_PICKER_TAG = "rental_time_picker"

fun Fragment.pickDateTime(
    @StringRes titleRes: Int,
    initialMillis: Long,
    minMillis: Long,
    onPicked: (Long) -> Unit
) {
    if (childFragmentManager.findFragmentByTag(DATE_PICKER_TAG) != null ||
        childFragmentManager.findFragmentByTag(TIME_PICKER_TAG) != null
    ) {
        return
    }

    val zone = ZoneId.systemDefault()
    val initial = Instant.ofEpochMilli(initialMillis).atZone(zone)
    val minDateUtcMillis = Instant.ofEpochMilli(minMillis)
        .atZone(zone)
        .toLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
    val initialDateUtcMillis = initial.toLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val constraints = CalendarConstraints.Builder()
        .setValidator(DateValidatorPointForward.from(minDateUtcMillis))
        .build()

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(titleRes)
        .setSelection(initialDateUtcMillis)
        .setCalendarConstraints(constraints)
        .build()

    datePicker.addOnPositiveButtonClickListener { selectedDateUtcMillis ->
        val date = Instant.ofEpochMilli(selectedDateUtcMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(titleRes)
            .setHour(initial.hour)
            .setMinute(initial.minute)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val millis = LocalDateTime.of(date, LocalTime.of(timePicker.hour, timePicker.minute))
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
            onPicked(millis)
        }

        timePicker.show(childFragmentManager, TIME_PICKER_TAG)
    }

    datePicker.show(childFragmentManager, DATE_PICKER_TAG)
}
