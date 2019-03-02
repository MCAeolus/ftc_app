package org.firstinspires.ftc.teamcode.common.util

import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties

object TelemetryUtil {

    fun convertToMap(o : Any) : LinkedHashMap<String, Any> {
        val retMap = linkedMapOf<String, Any>()
        for(f in o::class.memberProperties) {
            val value = f.getter.call(o)
            retMap[f.name]= value ?: "null"
        }
        return retMap
    }

}