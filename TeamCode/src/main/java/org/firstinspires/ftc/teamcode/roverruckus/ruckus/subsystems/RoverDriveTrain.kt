package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import kotlin.reflect.KClass

class RoverDriveTrain : IDriveTrain, Trackable {

    override fun init(robot: IRobot) {

    }

    override fun move(x: Double, y: Double, r: Double, p: Double) {

    }

    override fun motorList(): List<DcMotor> = listOf()

    override fun motorMap(): Map<String, DcMotor> = mapOf()

    override fun driveClass(): KClass<out IDriveTrain> = this::class

    override fun stop() {

    }

    override fun data(): Map<String, Any> = linkedMapOf()

}