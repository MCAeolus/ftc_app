package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util

import android.util.Log
import com.google.gson.*
import java.lang.NullPointerException
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

object JsonConversionUtil {

    /**
     * Deserialization implementation drawn upon from Google's GSON
     * https://github.com/google/gson/tree/master/gson/src/main
     */

    private val primativeTypes = arrayOf(Double::class, Float::class, Int::class, Short::class, Long::class, Byte::class, Boolean::class, String::class, Char::class)

    fun recursiveObjectToJson(o : Any) : JsonElement {

        if(o is Collection<*>) {
            val arr = JsonArray()

            for(v in o) {
                if(primativeTypes.contains(v!!::class)) {
                    when (v::class) { //I love primitives! /s
                        Int::class,
                        Short::class,
                        Long::class,
                        Float::class,
                        Byte::class,
                        Double::class -> arr.add(v as Number)
                        Char::class -> arr.add(v as Char)
                        Boolean::class -> arr.add(v as Boolean)
                        String::class -> arr.add(v as String)
                    }
                }

                else arr.add(recursiveObjectToJson(v))
            }

            return arr
        } else {
            val obj = JsonObject()
            obj.addProperty("type", o::class.java.name)

            val objValues = JsonObject()

            for (field in o::class.memberProperties) {

                val fieldValue = field.getter.call(o)!!

                if(primativeTypes.contains(fieldValue::class)) {
                    when(fieldValue::class) { //I love primitives! /s
                        Int::class,
                        Short::class,
                        Long::class,
                        Float::class,
                        Byte::class,
                        Double::class -> objValues.addProperty(field.name, fieldValue as Number)
                        Char::class -> objValues.addProperty(field.name, fieldValue as Char)
                        Boolean::class -> objValues.addProperty(field.name, fieldValue as Boolean)
                        String::class -> objValues.addProperty(field.name, fieldValue as String)
                    }
                } else objValues.add(field.name, recursiveObjectToJson(fieldValue))
            }

            obj.add("value", objValues)

            return obj
        }
    }

    fun recursiveJsonToObject(e : JsonElement) : Any {
        if(e.isJsonArray) {

            val jArray = e.asJsonArray
            val returnArray = arrayListOf<Any>()

            for(jElement in jArray) returnArray.add(recursiveJsonToObject(jElement))

            return returnArray

        }else {

            if(e.isJsonObject) {
                val jObj = e.asJsonObject
                val cType = Class.forName(jObj.get("type").asString)
                val jValues = jObj.getAsJsonObject("value")

                val newObject = cType.newInstance()

                for(field in newObject::class.declaredMemberProperties) {
                    field.isAccessible = true
                    field.javaField!!.set(newObject, recursiveJsonToObject(jValues.get(field.name)))
                }

                return newObject

            }else {
                val jPrim = e.asJsonPrimitive
                return when {
                    jPrim.isBoolean -> jPrim.asBoolean
                    jPrim.isString -> jPrim.asString
                    jPrim.isNumber -> jPrim.asNumber
                    else -> ""
                }
            }
        }
    }
}