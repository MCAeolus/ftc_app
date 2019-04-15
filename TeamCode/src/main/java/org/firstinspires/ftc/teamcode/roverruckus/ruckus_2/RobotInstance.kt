package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.MecanumDrivetrain
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.IntakeSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.OuttakeSystem
import java.io.InvalidClassException
import java.util.concurrent.ExecutorService
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor

class RobotInstance(val opmode : OpMode, val hardware : HardwareMap) {

    val subsystems = HashMap<String, Subsystem>()

    val mecanumDrive = addSubsystem<MecanumDrivetrain>()
    val liftSystem = addSubsystem<LiftSystem>()
    val outtakeSystem = addSubsystem<OuttakeSystem>()
    val intakeSystem = addSubsystem<IntakeSystem>()

    var isStarted = false
        private set
    var isStopped = false
        private set

    constructor(opmode : OpMode) : this(opmode, opmode.hardwareMap)

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
        if(!isStopped) {
            isStopped = true
            for (subsystem in subsystems) {//use reflection to check if the subsystem has a #stop() method.
                for (func in subsystem.value::class.memberFunctions) if (func.name == "stop") func.call(subsystem.value)
                //subsystem.value.update()
            }
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

    private fun addSubsystem(subsystem : Subsystem) : Subsystem {
        subsystems[subsystem::class.simpleName!!] = subsystem
        return subsystem
    }

    private inline fun <reified T : Subsystem>addSubsystem() : T {
        val clazz = T::class.java
        val constructor = clazz.getConstructor(HardwareMap::class.java, RobotInstance::class.java)
        val newObject = constructor.newInstance(hardware, this)
        subsystems[clazz.simpleName!!] = newObject
        return newObject
    }
}