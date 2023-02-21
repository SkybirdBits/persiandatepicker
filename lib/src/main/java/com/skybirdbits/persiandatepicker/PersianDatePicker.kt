package com.skybirdbits.persiandatepicker


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skybirdbits.persiandatepicker.adapter.CalendarAdapter
import com.skybirdbits.persiandatepicker.adapter.OnDaySelectListener
import com.skybirdbits.persiandatepicker.adapter.currentItemPosition
import com.skybirdbits.persiandatepicker.persiandate.PersianCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * @author created by Younes Soleimani
 */
class PersianDatePicker(private val context: Context) {

    constructor(context: Context, parent: ViewGroup?) : this(context) {
        _parent = parent
    }

    //A coroutine scope for asynchronous tasks
    private val datePickerScope = CoroutineScope(Dispatchers.Default)

    //Parent of the dialog which shows on it
    private var _parent: ViewGroup? = null
    val parent: ViewGroup? get() = _parent

    //A dialog which contains root view it will be initialized in initDialog() method
    private var _dialog: AlertDialog? = null
    val dialog get() = _dialog

    //Root view of the dialog
    private lateinit var _root: View
    val root get() = _root

    //Head of the calendar contains today month and year
    private lateinit var todayMonthAndDayTextView: TextView
    private lateinit var todayYearTextView: TextView

    //recycler view which is container of the item months
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter

    //negative and positive buttons on dialog
    private lateinit var negativeButton: Button
    private lateinit var positiveButton: Button

    //The time which is selected from the days of the item months
    private var selectedTime: Long = PersianCalendar().timeMillis

    var dimAmount = 1f

    var languageFarsi = true

    init {
        createCalendarView()
    }

    /*
        initialize View of the calendar and set data to that it's called inside initCalendar method
        after  data is ready to set
     */

    fun createCalendarView() {
        onCreateCalendarView()
    }

    private fun onCreateCalendarView() {
        _root = LayoutInflater.from(context)
            .inflate(R.layout.layout_persian_calendar, _parent, false)


        //head of the calendar
        todayMonthAndDayTextView =
            _root.findViewById<MaterialTextView>(R.id.today_month_and_day_name)
        todayYearTextView = _root.findViewById<MaterialTextView>(R.id.today_year_name)

        //recycler view (month items container)
        recyclerView = _root.findViewById(R.id.recycler_date_view)
        adapter = CalendarAdapter(OnDaySelectListener { timeMillis -> selectedTime = timeMillis })
        recyclerView.adapter = adapter

        //snap on item in recycler view
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)


        negativeButton = _root.findViewById(R.id.negative_button)
        positiveButton = _root.findViewById(R.id.positive_button)

        //Default listener for dialog's buttons
        negativeButton.setOnClickListener { _dialog?.dismiss() }
        positiveButton.setOnClickListener { _dialog?.dismiss() }

        onAttachCalendarView()
    }

    private fun onAttachCalendarView() {
        val builder = AlertDialog.Builder(context)
        _dialog = builder.setView(_root).create()

        onConfigCalendarView()
    }

    private fun onConfigCalendarView() {

        _root.layoutDirection = if (languageFarsi) {
            negativeButton.text = context.getString(R.string.cancel)
            positiveButton.text = context.getString(R.string.ok)
            View.LAYOUT_DIRECTION_RTL
        } else {
            negativeButton.text = context.getString(R.string.cancel_latin)
            positiveButton.text = context.getString(R.string.ok_latin)
            View.LAYOUT_DIRECTION_LTR
        }


        val today = PersianCalendar()
        today.isLanguageFarsi = languageFarsi
        todayMonthAndDayTextView.text = today.toString()
        todayYearTextView.text = today.year.toString()

        datePickerScope.launch {
            adapter.isFarsi = languageFarsi
            adapter.init().collect {
                if (it)
                    withContext(Dispatchers.Main) {
                        if (currentItemPosition != null)
                            recyclerView.scrollToPosition(currentItemPosition!!)
                    }
            }
        }
    }

    private fun configCalendarView() {
        onConfigCalendarView()
    }


    fun show() {
        //call onConfig function to make sure locale changes affected
        configCalendarView()

        _dialog?.show()

        _dialog?.window?.attributes?.width = WindowManager.LayoutParams.WRAP_CONTENT
        _dialog?.window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        _dialog?.window?.setDimAmount(dimAmount)
    }

    fun setOnPositiveButtonClickListener(listener: OnClickListener) {
        positiveButton.setOnClickListener {
            listener.onClick(selectedTime)
            _dialog?.dismiss()
        }
    }

    fun setOnNegativeButtonClickListener(listener: OnClickListener) {
        negativeButton.setOnClickListener {
            listener.onClick(selectedTime)
            _dialog?.dismiss()
        }
    }


    /*
    * todo update minimum date in calendar
    * supports from 1357
    */

    fun updateMinYear(minYear: Int) {
        adapter.minYear = minYear
        adapter.init()
    }

    /*
     *todo update maximum date for showing in calendar
     */
    fun updateMaxYear(maxYear: Int) {
        adapter.maxYear = maxYear
        adapter.init()
    }

    interface OnClickListener {
        fun onClick(timeMillis: Long)
    }
}