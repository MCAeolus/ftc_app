package org.firstinspires.ftc.teamcode.common.robot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.IMachine

/**
 * Created by Nathan.Smith.19 on 10/21/2017.
 */
abstract class LinearRobot(val DRIVETRAIN : IDriveTrain, val COMPONENTS : Map<String, IMachine>) : LinearOpMode(), IRobot {

    override fun opMode() = this

    override fun runOpMode() {
        telemetry.msTransmissionInterval = 100
        DRIVETRAIN.init(this)
        for(c in COMPONENTS.values) {
            c.init(this)
        }
    }

    fun linear_stop() {
        DRIVETRAIN.stop()
        for (c in COMPONENTS.values)
            c.stop()
    }

    fun holdToShutdown() {
        while(opModeIsActive()) idle()
    }

}