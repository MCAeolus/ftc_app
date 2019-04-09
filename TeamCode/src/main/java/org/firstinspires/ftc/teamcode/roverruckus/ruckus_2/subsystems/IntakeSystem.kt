package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem

class IntakeSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem() {

    var intakeLocked = false

    enum class IntakeMode(val ticks : Int) {
        DOWN(500), //TODO find actual value
        UP(0)
    }

    var intakeMode = IntakeMode.UP
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    var slidesPower = 0.0

    private val positionalMotorPower = 0.6

    private var shouldUpdate = false
    private var isUpdating = false


    private val intakePositionMotor = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.INTAKE_ARM_MOTOR)
    private val linearSlides = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.LINEAR_SLIDES)

    init {
        intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        intakePositionMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        intakePositionMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    override fun update(): LinkedHashMap<String, Any> {

        if(!intakeLocked && shouldUpdate) {
            when(intakeMode) {
                IntakeMode.UP -> {
                    intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
                    intakePositionMotor.targetPosition = intakeMode.ticks
                    intakePositionMotor.power = positionalMotorPower
                    intakePositionMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
                IntakeMode.DOWN -> {
                    intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
                    intakePositionMotor.targetPosition = intakeMode.ticks
                    intakePositionMotor.power = positionalMotorPower
                    intakePositionMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
            }
            shouldUpdate = false
        } else if(intakeLocked){
            intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            intakePositionMotor.power = 0.0
        }

        linearSlides.power = slidesPower

        if(isUpdating && !intakePositionMotor.isBusy) {
            isUpdating = false
            intakePositionMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }

        return linkedMapOf("intake mode" to intakeMode.name, "is updating" to isUpdating, "linear slides power" to slidesPower)
    }

    override fun replayData(): List<Any> {
        return listOf(intakeMode, slidesPower)
    }

    override fun updateFromReplay(l: List<Any>) {
        intakeMode = l[0] as IntakeMode
        slidesPower = l[1] as Double
    }
}
