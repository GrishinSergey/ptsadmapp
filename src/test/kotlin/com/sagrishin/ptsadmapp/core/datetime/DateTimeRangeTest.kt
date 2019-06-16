package com.sagrishin.ptsadmapp.core.datetime

import org.joda.time.DateTime
import org.junit.Test

class DateTimeRangeTest {

    @Test
    fun test() {
        val start = DateTime.now().minusWeeks(1)
        val end = DateTime.now().plusWeeks(1)
    }

}