package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.TimestampedData
import com.qualcomm.robotcore.util.ThreadPool
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.MecanumDrivetrain
import java.util.concurrent.ExecutorService

class RobotInstance(val opmode : OpMode, val hardware : HardwareMap) {

    val mecanumDrive : MecanumDrivetrain = MecanumDrivetrain(opmode.hardwareMap)

    val subsystems = arrayListOf<Subsystem>()
    var isStarted = false
        private set


    constructor(opmode : OpMode) : this(opmode, opmode.hardwareMap)

    init {
        subsystems.add(mecanumDrive)
    }

    fun start() {
        if(!isStarted) {


            isStarted = true
        }
    }

    fun update() {
        val finalTelemetry = linkedMapOf<String, LinkedHashMap<String, Any>>()
        try {

            for (subsystem in subsystems) //we can assert the class name is not null because no subsystem is anonymous.
                finalTelemetry[subsystem::class.simpleName!! + "\n"] = subsystem.update() //update

        } catch (e: Throwable) {
            opmode.telemetry.log().add("ERROR: update runnable failed to update.")
            //oops
        }

        try {
            for (name in finalTelemetry.keys) {
                val line = opmode.telemetry.addLine(name)
                val dataIterator = finalTelemetry[name]!!.iterator() //there has to be a value because we are using the map keys.

                while (dataIterator.hasNext()) {
                    val currentData = dataIterator.next()
                    line.addData(currentData.key, currentData.value)
                }
            }
        } catch (e: Throwable) {
            opmode.telemetry.log().add("ERROR: update runnable failed to run telemetry")
            //oops again
        }
        opmode.telemetry.update()
    }

    fun stop() {
        mecanumDrive.stop()
    }

    fun hold(millis : Double) {
        val start = System.currentTimeMillis()
        while((start + millis) < System.currentTimeMillis())
            update()
    }


}