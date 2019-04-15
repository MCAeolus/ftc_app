package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.MecanumWheelData
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object RuckusTelemetryConverter {

    private val adapters = HashMap<KClass<*>, RuckusTelemetryAdapter>()

    init {
        adapters[DcMotor::class] = object : RuckusTelemetryAdapter() {
            override fun asString(o: Any): String {
                o as DcMotor
                return "current power: ${o.power}, current position: ${o.currentPosition}, current mode: ${o.mode}"
            }
        }
        adapters[Servo::class] = object : RuckusTelemetryAdapter() {
            override fun asString(o: Any): String {
                o as Servo
                return "current position: ${o.position}"
            }
        }
        adapters[CRServo::class] = object : RuckusTelemetryAdapter() {
            override fun asString(o: Any): String {
                o as CRServo
                return "current power: ${o.power}"
            }
        }
        adapters[MecanumWheelData::class] = object : RuckusTelemetryAdapter() {
            override fun asString(o: Any): String {
                o as MecanumWheelData
                return "motor power: ${o.motor().power}, current position: ${o.motor().currentPosition}"
            }
        }
    }

    fun convertToMap(o : Any) : LinkedHashMap<String, Any> {
        val retMap = linkedMapOf<String, Any>()
        for(f in o::class.memberProperties) {
            val ann = f.findAnnotation<LoggedField>()
            if(ann != null) {
                f.isAccessible = true
                val rawValue = f.getter.call(o) ?: "null"
                val adapterKey = adapters[rawValue::class]
                val value = if(adapterKey == null) rawValue.toString() else adapterKey.asString(rawValue)
                val name = if(ann.description == "") f.name else ann.description
                retMap[name] = value
            }
        }
        return retMap
    }
}