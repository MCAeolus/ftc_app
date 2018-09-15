package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import kotlin.reflect.KClass

class MiniTankDrive() : IDriveTrain {
    override fun init(robot: IRobot) {

    }

    override fun move(x: Double, y: Double, r: Double, p: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun motorList(): List<DcMotor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun motorMap(): Map<String, DcMotor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun driveClass(): KClass<out IDriveTrain> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}