package com.skybirdbits.persiandatepicker.persiandate

data class OneMonthInYear(val year : Int, val month:Int, val days: List<Int>, var selected : Boolean = false){

    override fun toString(): String {
        return "Year: $year, Month: $month , \nDays: $days"
    }


    override fun equals(other: Any?): Boolean {
        if (other !is OneMonthInYear) throw IllegalArgumentException("Object is not a type of MonthDays")
        return year == other.year && month == other.month
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + days.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }
}