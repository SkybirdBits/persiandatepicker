package com.skybirdbits.persiandatepicker.utils

import com.skybirdbits.persiandatepicker.persiandate.OneMonthInYear
import com.skybirdbits.persiandatepicker.persiandate.PersianCalendar
import kotlin.collections.ArrayList

fun getCalendarMonthList(minYear: Int , maxYear: Int): List<OneMonthInYear>{

    var date = PersianCalendar(minYear , 1 , 1)
    val dateList: MutableList<OneMonthInYear> = ArrayList()


    while (date.year <= maxYear){
        val year = date.year
        val month = date.monthOfYear
        val days = ArrayList<Int>()

        while (date.dayOfMonth != date.monthLength){
            days.add(date.dayOfMonth)
            date = date.tomorrow
        }
        days.add(date.dayOfMonth)

        val oneMonthInYear = OneMonthInYear(year , month , days)
        dateList.add(oneMonthInYear)


        //reset day of month to 1 and go to the next month
        date = date.tomorrow

    }


    return dateList
}