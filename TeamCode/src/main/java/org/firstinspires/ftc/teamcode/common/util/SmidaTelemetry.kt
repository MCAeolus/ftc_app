package org.firstinspires.ftc.teamcode.common.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import java.lang.reflect.Field

class SmidaTelemetry(val useDashboard : Boolean, val usePhone : Boolean, val instance : OpMode, val prefix : String = "") {

    val trackableData = hashMapOf<String, Field>()

    val sendMap = linkedMapOf<String, Any>()

    init {
        val fields = instance::class.java.declaredFields

        if(fields != null)
            for(field in fields)
                if (field.isAnnotationPresent(TelemetryField::class.java))
                    trackableData[this
                    fullOrNull field.getAnnotation(TelemetryField::class.java).data
                            ?: field.name] = field
    }

    fun update() {
        for(t in trackableData)
            sendData(t.value, t.key)
        send()
    }

    fun sendData(field : Field, name : String) {
        val o = field.get(instance)
        val k = o::class.nestedClasses

        val n2 = name.replace("_", " ")

        if(o is Trackable) {
            prepare(": ", n2)
            o.data().forEach { prepare(it.value, it.key) }
        }
        else if(o is IMachine|| o is IDriveTrain)
            prepare("does not interface Trackable", n2)
        else if(o is DcMotor)
            prepare(o.currentPosition, n2)
        else if(o is Servo)
            prepare(o.position, n2)
        else prepare(o, n2)
    }

    /**
     * small helper to slightly shorten code
     */
    infix fun fullOrNull(d : String) : String? = (if(d.isNotBlank()) d else null)


    fun prepare(d : Any, n : String) {
        sendMap.put(n, d)
    }

    fun send() {
        val packet = TelemetryPacket()

        for(p in sendMap) {
            val d = p.value

            if (d is Map<*,*>) {
                for (pair in d) {
                    packet.put(pair.key.toString(), pair.value)
                    if (usePhone) instance.telemetry.addData(pair.key.toString(), pair.value)
                }
            } else {
                packet.put(p.key, d)
                if (usePhone) instance.telemetry.addData(p.key, d)
            }
        }

        if(useDashboard) FtcDashboard.getInstance().sendTelemetryPacket(packet)
        instance.telemetry.update()

        sendMap.clear()
    }
}