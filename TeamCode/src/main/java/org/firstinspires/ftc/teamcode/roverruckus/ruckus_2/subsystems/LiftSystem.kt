package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem

class LiftSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem() {

    private val liftMotor = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.LIFT_MOTOR)



    init {
        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }



    override fun update(): LinkedHashMap<String, Any> {


        return linkedMapOf()
    }
}