package com.sagrishin.ptsadmapp.api

import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

object DateTimeTypeAdapter {

    fun getDeserializer(): JsonDeserializer<DateTime> {
        return JsonDeserializer { json, _, _ -> DateTime.parse(json.asString) }
    }

    fun getSerializer(): JsonSerializer<DateTime> {
        return JsonSerializer { dateTime, _, _ ->
            JsonPrimitive(ISODateTimeFormat.dateTime().print(dateTime.toLocalDateTime()))
        }
    }

}
