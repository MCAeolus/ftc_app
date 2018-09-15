package org.firstinspires.ftc.teamcode.common.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import kotlin.reflect.KClass

/**
 * Created by Nathan.Smith.19 on 9/18/2017.
 */
interface IDriveTrain {

    fun init(robot: IRobot) /* Grab motor values and set any initial internal values */

    fun move(x : Double, y : Double, r : Double, p : Double = 1.0) /*  */

    fun motorList() : List<DcMotor>

    fun motorMap() : Map<String, DcMotor>

    fun driveClass() : KClass<out IDriveTrain>

    fun stop()

}