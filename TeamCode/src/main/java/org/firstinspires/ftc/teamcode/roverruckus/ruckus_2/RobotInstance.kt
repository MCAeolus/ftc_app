package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.TimestampedData
import com.qualcomm.robotcore.util.ThreadPool
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.IntakeSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.MecanumDrivetrain
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.OuttakeSystem
import java.util.concurrent.ExecutorService
import kotlin.reflect.full.memberFunctions

class RobotInstance(val opmode : OpMode, val hardware : HardwareMap) {

    val mecanumDrive = MecanumDrivetrain(hardware, this)
    val liftSystem = LiftSystem(hardware, this)
    val outtakeSystem = OuttakeSystem(hardware, this)
    val intakeSystem = IntakeSystem(hardware, this)

    val subsystems = HashMap<String, Subsystem>()
    var isStarted = false
        private set


    constructor(opmode : OpMode) : this(opmode, opmode.hardwareMap)

    init {
        addSubsystem(mecanumDrive)
        addSubsystem(liftSystem)
        addSubsystem(outtakeSystem)
        addSubsystem(intakeSystem)
    }

    fun start() {
        if(!isStarted) {


            isStarted = true
        }
    }

    fun update() {
        val finalTelemetry = linkedMapOf<String, LinkedHashMap<String, Any>>()
        try {

            for (subsystem in subsystems.values) //we can assert the class name is not null because no subsystem is anonymous.
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
        for(subsystem in subsystems) {//use reflection to check if the subsystem has a #stop() method.
            for (func in subsystem.value::class.memberFunctions) if (func.name == "stop") func.call(subsystem.value)
            subsystem.value.update()
        }
        /**
         * Does it make sense to use reflection for this? Not really.
         * This could be done just as easily with an interface and then all you have to do is check for the interface.
         * But is reflection cool? Yes.
         * So that's why this uses reflection.
         */

    }

    fun hold(millis : Double) {
        val start = System.currentTimeMillis()
        while((start + millis) < System.currentTimeMillis())
            update()
    }

    private fun addSubsystem(subsystem : Subsystem) {
        subsystems[subsystem::class.simpleName!!] = subsystem
    }


}