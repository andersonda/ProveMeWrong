package com.danderson.provemewrong

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.text.format.DateFormat
import java.util.*

class DebateDatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private var datePicker: DatePickerDialog? = null
    private var timePicker: TimePickerDialog? = null
    private val EXTRA_DATE = "extra_date"
    private var resultIntent: Intent? = null
    private val DIALOG = 1
    private var cal = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        datePicker = DatePickerDialog(activity, this, year, month, day)
        timePicker = TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        resultIntent = activity?.intent

        return datePicker!!
    }

    override fun onDateSet(picker: DatePicker, year: Int, month: Int, day: Int) {
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        timePicker?.show()
    }

    override fun onTimeSet(picker: TimePicker, hourOfDay: Int, minute: Int) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        resultIntent?.putExtra(EXTRA_DATE, cal.time)
        targetFragment?.onActivityResult(DIALOG, Activity.RESULT_OK, resultIntent)
    }
}