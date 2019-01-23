package org.firstinspires.ftc.teamcode.common.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import kotlin.reflect.KClass

class DummyDriveTrain : IDriveTrain {
    override fun init(robot: IRobot) {
    }

    override fun move(x: Double, y: Double, r: Double, p: Double) {
    }

    override fun motorList(): List<DcMotor> {
        return listOf()
    }

    override fun motorMap(): Map<String, DcMotor> {
        return mapOf()
    }

    override fun driveClass(): KClass<out IDriveTrain> {
        return this::class
    }

    override fun stop() {
    }
}