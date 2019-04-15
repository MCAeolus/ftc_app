package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.LoggedField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.RuckusTelemetryConverter

class LiftSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem(hardware, robot) {

    private val liftMotor = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.LIFT_MOTOR)

    enum class LiftMode(val ticks : Int) {
        LIFTED(0),
        LOWERED(500), //TODO find real value
        MANUAL(-1)
    }

    @LoggedField(description = "lift mode")
    var liftMode = LiftMode.LIFTED
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    private val liftMotorPower = 0.5

    @LoggedField(description = "manual lift power")
    var manualLiftPower = 0.0
        set(pow) {
            if((pow == 0.0 && field != 0.0) || pow != 0.0) {
                liftMode = LiftMode.MANUAL
                field = pow
            }
        }

    init {
        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        liftMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        liftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    private var shouldUpdate = false

    @LoggedField(description = "is updating")
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


        return RuckusTelemetryConverter.convertToMap(this)
    }

    fun stop() {
        /**
        manualLiftPower = 0.0
        liftMode = LiftMode.MANUAL
        **/
        liftMotor.power = 0.0
    }

    override fun replayData(): List<Any> {
        return listOf(liftMode, manualLiftPower)
    }

    override fun updateFromReplay(l: List<Any>) {
        liftMode = l[0] as LiftMode
        manualLiftPower = l[1] as Double
    }
}