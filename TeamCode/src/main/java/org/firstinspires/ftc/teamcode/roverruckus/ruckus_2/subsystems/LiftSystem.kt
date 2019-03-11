package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem

class LiftSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem() {

    private val liftMotor = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.LIFT_MOTOR)

    enum class LiftMode(val ticks : Int) {
        LIFTED(0),
        LOWERED(500), //TODO find real value
        MANUAL(-1)
    }

    var liftMode = LiftMode.LIFTED
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    private val liftMotorPower = 1.0

    var manualLiftPower = 0.0
        set(pow) {
            liftMode = LiftMode.MANUAL
            field = pow
        }

    init {
        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        liftMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        liftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    private var shouldUpdate = false
    private var isUpdating = false

    override fun update(): LinkedHashMap<String, Any> {

        if(shouldUpdate) {
            when(liftMode) {
                LiftMode.LIFTED -> {
                    liftMotor.targetPosition = liftMode.ticks
                    liftMotor.power = liftMotorPower
                    liftMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
                LiftSystem.LiftMode.LOWERED -> {
                    liftMotor.targetPosition = liftMode.ticks
                    liftMotor.power = liftMotorPower
                    liftMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
                LiftSystem.LiftMode.MANUAL -> {
                    liftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
                }
            }
            shouldUpdate = false
        }

        if(liftMode == LiftMode.MANUAL) liftMotor.power = manualLiftPower

        if(isUpdating && !liftMotor.isBusy) {
            isUpdating = false
            liftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }


        return linkedMapOf("lift mode" to liftMode, "is updating" to isUpdating)
    }

    fun stop() {
        liftMotor.power = 0.0
    }
}