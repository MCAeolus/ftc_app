package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object RuckusTelemetryConverter {

    fun convertToMap(o : Any) : LinkedHashMap<String, Any> {
        val retMap = linkedMapOf<String, Any>()
        for(f in o::class.memberProperties) {
            val ann = f.findAnnotation<LoggedField>()
            if(ann != null) {
                f.isAccessible = true
                val value = f.getter.call(o).toString()
                val name = if(ann.description == "") f.name else ann.description
                retMap[name] = value ?: "null"
            }
        }
        return retMap
    }
}