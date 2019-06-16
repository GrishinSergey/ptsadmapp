package com.sagrishin.ptsadmapp.core.datetime

import org.joda.time.DateTime

fun <R> ClosedRange<DateTime>.mapByDay(action: (DateTime) -> R): List<R> {
    val res = mutableListOf<R>()
    var day = start
    while (day <= endInclusive) {
        res += action(day)
        day = day.plusDays(1)
    }

    return res
}


operator fun DateTime.contains(dateTime: DateTime): Boolean {
    return this.withTimeAtStartOfDay() <= dateTime && dateTime <= this.plusDays(1).withTimeAtStartOfDay()
}
