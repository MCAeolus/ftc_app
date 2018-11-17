package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable

class TotemDropper() : IMachine, Trackable {

    lateinit var totem_servo : Servo

    override fun init(robot: IRobot) {
        totem_servo = robot.opMode().hardwareMap.get(Servo::class.java, "totem_servo")
    }

    fun dump() {
        totem_servo.position = 0.5
    }

    fun reset() {
        totem_servo.position = 1.0
    }

    override fun stop() = reset()


    override fun data(): Map<String, Any> {
        return linkedMapOf(
                "Totem servo position" to totem_servo.position
        )
    }
}