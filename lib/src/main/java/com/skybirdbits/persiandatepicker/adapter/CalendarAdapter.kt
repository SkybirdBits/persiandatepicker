package com.skybirdbits.persiandatepicker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skybirdbits.persiandatepicker.R
import com.skybirdbits.persiandatepicker.persiandate.OneMonthInYear
import com.skybirdbits.persiandatepicker.persiandate.PersianCalendar
import com.skybirdbits.persiandatepicker.utils.getCalendarMonthList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

var selectedDate: PersianCalendar? = null
var currentItemPosition: Int? = null

class CalendarAdapter(private val listener: OnDaySelectListener) :
    RecyclerView.Adapter<CalendarAdapter.ItemMonthViewHolder>() {

    var monthList = mutableListOf<OneMonthInYear>()

    var minYear: Int = 1360
    var maxYear: Int = 1460

    var isFarsi = true

    fun init(): Flow<Boolean> = flow {
        submitCalendarMonths(minYear, maxYear)
        emit(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMonthViewHolder {
        val viewResource = if (isFarsi) R.layout.list_item_month else R.layout.list_item_month_latin
        val view = LayoutInflater.from(parent.context).inflate(viewResource, parent, false)

        return ItemMonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemMonthViewHolder, position: Int) {
        val item = monthList[position]
        holder.bind(item, listener)
    }


    override fun getItemCount(): Int {
        return monthList.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        monthList.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun submitCalendarMonths(minYear: Int, maxYear: Int) {

        withContext(Dispatchers.Default) {
            monthList = getCalendarMonthList(minYear, maxYear) as MutableList<OneMonthInYear>
            setSelectedDateToCurrentDate()

        }
    }

    private fun setSelectedDateToCurrentDate() {

        if (selectedDate == null) {

            val currentDate = PersianCalendar()

            selectedDate = currentDate
            for (index in monthList.indices) {
                val currentMonth = monthList[index]
                if (currentMonth.year == selectedDate!!.year && currentMonth.month == selectedDate!!.monthOfYear) {
                    currentItemPosition = index
                    currentMonth.selected = true
                    break
                }
            }
        } else {
            var index = 0
            while (index < monthList.size) {
                val currentMonth = monthList[index]
                if (currentMonth.year == selectedDate!!.year && currentMonth.month == selectedDate!!.monthOfYear) {
                    currentMonth.selected = true
                    break
                }
                index++
            }
        }
    }

    inner class ItemMonthViewHolder(rootView: View) :
        RecyclerView.ViewHolder(rootView) {

        private val yearTextView = rootView.findViewById<MaterialTextView>(R.id.year)
        private val monthTextView = rootView.findViewById<MaterialTextView>(R.id.month)

        private val tableLayout = rootView.findViewById<LinearLayout>(R.id.daysTable)

        private var firstDayOfCurrentMonthDate: PersianCalendar? = null

        private lateinit var daysOfOneMonth: OneMonthInYear

        fun bind(oneMonthInYear: OneMonthInYear, onDaySelectListener: OnDaySelectListener) {

            this.daysOfOneMonth = oneMonthInYear
            val currentMonth = oneMonthInYear.month
            val currentYear = oneMonthInYear.year

            firstDayOfCurrentMonthDate =
                PersianCalendar(
                    currentYear,
                    currentMonth,
                    oneMonthInYear.days[0]
                )

            firstDayOfCurrentMonthDate!!.isLanguageFarsi = isFarsi

            setDayOfMonths(onDaySelectListener)
            setYearAndMonth()

        }

        private fun setYearAndMonth() {
            yearTextView.text = firstDayOfCurrentMonthDate?.year.toString()
            monthTextView.text = firstDayOfCurrentMonthDate?.nameOfMonthYear
        }

        private fun setDayOfMonths(selectListener: OnDaySelectListener) {

            resetDays()

            //index of days which equals to month's length
            var dayIndex = 0

            for (row in 0 until tableLayout.size) {
                var columnIndex = 0

                if (row == 0)
                    columnIndex = firstDayOfCurrentMonthDate!!.dayOfWeek - 1

                val tableRow = tableLayout[row] as LinearLayout

                for (col in columnIndex until tableRow.size) {

                    val cell = tableRow[col] as MaterialTextView

                    if (dayIndex < daysOfOneMonth.days.size) {

                        val day = daysOfOneMonth.days[dayIndex++]

                        cell.text = day.toString()
                        configCellClick(cell, selectListener)

                        if (daysOfOneMonth.selected) {
                            val date =
                                PersianCalendar(daysOfOneMonth.year, daysOfOneMonth.month, day)

                            if (date == selectedDate) makeCellSelected(cell)
                        }
                    }

                }
            }
        }

        private fun configCellClick(
            cell: MaterialTextView,
            selectListener: OnDaySelectListener,
        ) {

            val day = cell.text.toString().toInt()

            val date = PersianCalendar(
                daysOfOneMonth.year,
                daysOfOneMonth.month,
                day
            )

            cell.setOnClickListener {

                /*
                    if selected date is in current month and year so
                    perform reset every cell before make another one selectable
                 */

                if (adapterPosition == currentItemPosition)
                    resetAllCellsSelection()


                if (currentItemPosition != null) {
                    monthList[currentItemPosition!!].selected = false
                    notifyItemChanged(currentItemPosition!!)
                    currentItemPosition = adapterPosition
                    monthList[currentItemPosition!!].selected = true
                }


                selectedDate = date
                selectListener.onSelect(selectedDate!!)

                makeCellSelected(cell)

                notifyItemChanged(adapterPosition)
            }

        }

        //called for every item in view holder on scrolling
        private fun resetDays() {

            for (row in 0 until tableLayout.size) {

                val tableRow = tableLayout[row] as LinearLayout

                for (col in 0 until tableRow.size) {

                    val cell = tableRow[col] as MaterialTextView
                    cell.text = ""
                    //reset click listener for current cell
                    cell.setOnClickListener {}
                    resetCellSelection(cell)

                }
            }
        }

        //reset every cell
        private fun resetAllCellsSelection() {
            for (row in 0 until tableLayout.size) {

                var columnIndex = 0

                //check first day of month in week day
                if (row == 0)
                    columnIndex = firstDayOfCurrentMonthDate!!.dayOfWeek - 1


                val tableRow = tableLayout[row] as LinearLayout

                for (col in columnIndex until tableRow.size) {
                    val checkableCell = tableRow[col] as MaterialTextView
                    resetCellSelection(checkableCell)
                }

            }
        }

        //reset selection view of a specific cell
        private fun resetCellSelection(cell: MaterialTextView) {
            cell.background = ResourcesCompat
                .getDrawable(cell.resources, R.drawable.selector_calendar_day, null)
        }

        //Makes a specific cell selected
        private fun makeCellSelected(cell: MaterialTextView) {
            cell.background = ResourcesCompat
                .getDrawable(cell.resources, R.drawable.shape_calendar_day_selected, null)
        }

    }
}

class OnDaySelectListener(val listener: (timeMillis: Long) -> Unit) {
    fun onSelect(selected: PersianCalendar) =
        listener(
            selected.timeMillis
        )
}