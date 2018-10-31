package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine

/**
 * Created by Nathan.Smith.19 on 3/1/2018.
 */
class RelicExtender : IMachine {

    enum class Direction(val speed : Double) { OUT(0.5), IN(-0.5), OFF(0.0) }
    enum class HandlePosition(val position : Double) { DOWN(0.15), GRAB(0.355), UP(0.65) }
    enum class GrabberPosition(val position : Double) { CLOSED(0.5), OPEN(0.00) }
    lateinit var motor1 : CRServo
    lateinit var motor2 : CRServo

    lateinit var r_handle : Servo
    lateinit var r_grabber : Servo

    override fun init(robot: IRobot) {
        motor1 = robot.opMode().hardwareMap.get(CRServo::class.java, "VM1")
        motor2 = robot.opMode().hardwareMap.get(CRServo::class.java, "VM2")

        r_handle = robot.opMode().hardwareMap.get(Servo::class.java, "R_H")
        r_grabber = robot.opMode().hardwareMap.get(Servo::class.java, "R_G")

        resting()
    }

    fun resting() {
        setGrabberPosition(GrabberPosition.CLOSED)
        setHandlePosition(HandlePosition.DOWN)
    }

    fun setGrabberPosition(grabber : GrabberPosition) {
        r_grabber.position = grabber.position
    }

    fun setHandlePosition(handle : HandlePosition) {
        r_handle.position = handle.position
    }

    fun runMotors(direction : Direction){
        motor1.power = direction.speed
        motor2.power = direction.speed
    }



    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}