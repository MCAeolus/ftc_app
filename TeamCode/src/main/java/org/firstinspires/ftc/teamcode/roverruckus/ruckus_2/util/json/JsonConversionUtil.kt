package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.json

import com.google.gson.*
import com.google.gson.internal.LazilyParsedNumber
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d
import java.io.InvalidClassException
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

object JsonConversionUtil {

    /**
     * Inspiration drawn from Google's GSON
     * https://github.com/google/gson/tree/master/gson/src/main
     */

    private val primativeTypes = arrayOf(Double::class, Float::class, Int::class, Short::class, Long::class, Byte::class, Boolean::class, String::class, Char::class)
    val adapters = hashMapOf<Class<*>, ClassAdapter<*>>()

    init {
        adapters[Vector2d::class.java] = object : ClassAdapter<Vector2d>() {
            override fun newObject(): Vector2d = Vector2d(0, 0)
        }

        adapters[Pose2d::class.java] = object : ClassAdapter<Pose2d>() {
            override fun newObject(): Pose2d = Pose2d(0, 0, 0)
        }
    }

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
                        Int::class,           //there has to be a better way to do this
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

                if(cType.isEnum) {
                    for( enum in cType.enumConstants)
                        if((enum as Enum<*>).name == jValues.get("name").asString) return enum
                }

                val newObject = try {
                    cType.newInstance()
                } catch ( e : Exception ) {
                    if(adapters.containsKey(cType)) adapters[cType]!!.newObject()
                    else throw InvalidClassException("${cType.name} has no adapter or empty constructors!")
                }

                for(field in newObject!!::class.declaredMemberProperties) {
                    val shouldAccess = field.isAccessible

                    field.javaField!!.isAccessible = true
                    field.javaField!!.set(newObject, recursiveJsonToObject(jValues.get(field.name)))

                    field.javaField!!.isAccessible = shouldAccess
                }

                return newObject

            }else {
                val jPrim = e.asJsonPrimitive
                return when {
                    jPrim.isBoolean -> jPrim.asBoolean
                    jPrim.isString -> jPrim.asString
                    jPrim.isNumber -> ((jPrim.asNumber as LazilyParsedNumber)::class.java.getField("value").get(jPrim.asNumber) as String)
                    else -> "nil"
                }
            }
        }
    }

}