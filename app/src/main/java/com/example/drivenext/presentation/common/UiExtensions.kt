package com.example.drivenext.presentation.common

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.drivenext.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_PICKER_TAG = "date_picker"
private const val PHOTOS_DIRECTORY = "photos"

fun Fragment.showErrorDialog(message: String) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.dialog_error_title)
        .setMessage(message)
        .setPositiveButton(R.string.dialog_ok, null)
        .show()
}

fun Fragment.showInfoDialog(@StringRes titleRes: Int, @StringRes messageRes: Int) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(titleRes)
        .setMessage(messageRes)
        .setPositiveButton(R.string.dialog_ok, null)
        .show()
}

fun Fragment.showDatePicker(@StringRes titleRes: Int, pattern: String, onPicked: (String) -> Unit) {
    if (childFragmentManager.findFragmentByTag(DATE_PICKER_TAG) != null) return

    val constraints = CalendarConstraints.Builder()
        .setValidator(DateValidatorPointBackward.now())
        .build()

    val picker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(titleRes)
        .setCalendarConstraints(constraints)
        .build()

    picker.addOnPositiveButtonClickListener { selectedDateUtcMillis ->
        val date = Instant.ofEpochMilli(selectedDateUtcMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        onPicked(date.format(DateTimeFormatter.ofPattern(pattern, Locale.ROOT)))
    }

    picker.show(childFragmentManager, DATE_PICKER_TAG)
}

fun Context.createCameraOutputUri(): Uri {
    val directory = File(cacheDir, PHOTOS_DIRECTORY).apply { mkdirs() }
    val file = File.createTempFile("photo_", ".jpg", directory)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}

fun ImageView.showImageFromUri(uriString: String, maxSizePx: Int): Boolean {
    if (tag == uriString) return true
    return try {
        val source = ImageDecoder.createSource(context.contentResolver, uriString.toUri())
        val drawable = ImageDecoder.decodeDrawable(source) { decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val largest = maxOf(width, height)
            if (largest > maxSizePx) {
                val scale = maxSizePx.toFloat() / largest
                decoder.setTargetSize(
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1)
                )
            }
        }
        setPadding(0, 0, 0, 0)
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageDrawable(drawable)
        tag = uriString
        true
    } catch (e: Exception) {
        tag = uriString
        false
    }
}
