package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

open class AutonomousBase : LinearRobot(MiniTankDrive(), mapOf(Pair("LIFT", LiftSystem()), Pair("IMU", IMU()))) {

    lateinit var LIFT : LiftSystem
    lateinit var IMU : IMU
    val TIMER = ElapsedTime()

    override fun runOpMode() {
        super.runOpMode()

        LIFT = COMPONENTS["LIFT"] as LiftSystem
        IMU = COMPONENTS["IMU"] as IMU
    }


    fun hold(millis : Long){
        TIMER.reset()
        while(TIMER.milliseconds() < time && opModeIsActive()){idle()}
    }

}