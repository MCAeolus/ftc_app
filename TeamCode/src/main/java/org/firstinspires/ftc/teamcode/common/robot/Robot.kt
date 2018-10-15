package org.firstinspires.ftc.teamcode.common.robot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.util.IncompatibleComponentException
import org.firstinspires.ftc.teamcode.common.util.LinearOnly

/**
 * Created by Nathan.Smith.19 on 10/19/2017.
 */
abstract class Robot(val DRIVETRAIN : IDriveTrain, val COMPONENTS : Map<String, IMachine>) : OpMode(), IRobot {

    override fun opMode() = this

    override fun init() {
        telemetry.msTransmissionInterval = 100
        DRIVETRAIN.init(this)
        for(c in COMPONENTS.values) {
            if(c::class.java.getAnnotation(LinearOnly::class.java) != null) throw IncompatibleComponentException()
            else c.init(this)
        }
    }
}