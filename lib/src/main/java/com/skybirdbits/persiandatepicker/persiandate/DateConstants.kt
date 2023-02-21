package com.skybirdbits.persiandatepicker.persiandate

val weekDayNames = arrayOf("شنبه" , "یکشنبه" , "دوشنبه" , "سه شنبه" , "چهارشنبه",
    "پنجشنبه" , "جمعه")
val monthNames = arrayOf("فروردین" , "اردیبهشت" , "خرداد" , "تیر" , "مرداد" , "شهریور" , "مهر",
    "آبان", "آذر" , "دی" ,"بهمن" ,"اسفند")


val weekDayNamesLatin = arrayOf(
    "Saturday" , "Sunday" , "Monday" , "Tuesday" , "Wednesday" ,"Thursday" , "Friday")

val monthNamesLatin = arrayOf(
    "Farvardin" , "Ordibehesht" ,"Khordad" , "Tir" , "Mordad" ,"Shahrivar" , "Mehr" ,"Aban" , "Azar",
    "Day" , "Bahman" , "Esfand"
)

enum class Fields(val value : Int){
    YEAR(0) , MONTH(1) ,
    WEEK_OF_YEAR (2) , WEEK_OF_MONTH(3),
    DAY_OF_WEEK(4),DAY_OF_WEEK_IN_MONTH(5)
    ,DAY_OF_MONTH(6) , DAY_OF_YEAR(7)

}

data class SolarData(val year : Int , val month:Int , val day : Int)