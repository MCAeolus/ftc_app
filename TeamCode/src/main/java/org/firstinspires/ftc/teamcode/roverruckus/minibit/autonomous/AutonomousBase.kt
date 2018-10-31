package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.drivetrain.util.PID
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

open class AutonomousBase(val autoClearTelemtry : Boolean) : LinearRobot(MiniTankDrive(), mapOf(Pair("LIFT", LiftSystem()), Pair("IMU", IMU()))) {

    lateinit var LIFT : LiftSystem
    lateinit var IMU : IMU
    val TIMER = ElapsedTime()

    val rot_controllerPID = PID(0.7F, 0.5F, 0.2F)

    val dashboard = FtcDashboard.getInstance()


    override fun runOpMode() {
        super.runOpMode()

        dashboard.telemetry.isAutoClear = autoClearTelemtry

        LIFT = COMPONENTS["LIFT"] as LiftSystem
        IMU = COMPONENTS["IMU"] as IMU
    }

    fun resetenc(final_mode : DcMotor.RunMode, motor : DcMotor){
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = final_mode
    }

    enum class RotationType {
        ONE_EIGHTY,
        THREE_SIXTY
    }
    fun PID_rot(angle : Double, rot : RotationType) {
        rot_controllerPID.stop()

        var ang = angle

        val isOneEighty = rot == RotationType.ONE_EIGHTY


        if(angle < 0 && !isOneEighty)ang = 360+angle

        while(Math.abs(ang - (if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle)) > 3 && opModeIsActive()){
            val pow = rot_controllerPID.calculate(ang.toFloat(), if(!isOneEighty)IMU.getZ360() else IMU.XYZ().thirdAngle)
            telemetryUpdate(linkedMapOf(
                    "Calculated Error (val)" to rot_controllerPID.ERROR_POST,
                    "Calculated Rotation PID (val)" to pow,
                    "Desired Rotation (degrees)" to angle,
                    "Correct Desired Rotation (degrees)" to ang,
                    "Current Rotation (degrees)" to if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle,
                    "Current Time (seconds)" to rot_controllerPID.TIME.seconds()
            ))
            DRIVETRAIN.move(0.0, 0.0, pow)
        }
        DRIVETRAIN.stop()
    }

    fun hold(millis : Long){
        TIMER.reset()
        while(TIMER.milliseconds() < millis && opModeIsActive()){ idle() }
    }

    fun telemetryUpdate(data : Map<String, Any>) {
        val packet = TelemetryPacket()
        packet.putAll(data)

        for(d in data) telemetry.addData(d.key, d.value)

        telemetry.update()
        dashboard.sendTelemetryPacket(packet)
    }



}