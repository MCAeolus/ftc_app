package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.LoggedField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.RuckusTelemetryConverter

class IntakeSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem(hardware, robot) {

    @LoggedField(description = "is intake locked")
    var intakeLocked = false

    enum class IntakePosition(val ticks : Int) {
        DOWN(1200) {
            override fun flip() = UP
        },
        UP(0) {
            override fun flip() = DOWN
        };

        abstract fun flip() : IntakePosition
    }

    enum class IntakeDirection(val speed : Double) {
        INTAKE(-0.8),
        OUTTAKE(0.8),
        STOPPED(0.0)
    }

    @LoggedField(description = "intake position")
    var intakePosition = IntakePosition.UP
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    @LoggedField(description = "intaking direction")
    var intakeDirection = IntakeDirection.STOPPED

    @LoggedField(description = "linear slide power")
    var linearSlidesPower = 0.0

    private val positionalMotorPower = 0.6

    private var shouldUpdate = false
    @LoggedField(description = "is updating")
    private var isUpdating = false


    private val intakePositionMotor = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.INTAKE_ARM_MOTOR)
    private val brushVexMotor = hardware.get(CRServo::class.java, HNAMES_RUCKUS.VEX_INTAKE_MOTOR)
    private val linearSlides = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.LINEAR_SLIDES)

    @LoggedField(description = "linear slides initial position")
    var initialLinearSlidesOrientation = 0
        private set

    var hasBeenPastThreshold = false

    init {
        intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        intakePositionMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        intakePositionMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER

        linearSlides.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        linearSlides.mode = DcMotor.RunMode.RUN_USING_ENCODER
        linearSlides.direction = DcMotorSimple.Direction.REVERSE

        initialLinearSlidesOrientation = linearSlides.currentPosition
    }

    override fun update(): LinkedHashMap<String, Any> {

        if(linearSlides.currentPosition <= initialLinearSlidesOrientation) {
            if (hasBeenPastThreshold) {
                hasBeenPastThreshold = false
                if(intakePosition == IntakePosition.DOWN) intakePosition = IntakePosition.UP
            }
        } else if(linearSlides.currentPosition >= initialLinearSlidesOrientation + 300){
            if(!hasBeenPastThreshold)
                hasBeenPastThreshold = true
        }

        if(!intakeLocked && shouldUpdate) {
            when(intakePosition) {
                IntakePosition.UP -> {
                    intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
                    intakePositionMotor.targetPosition = intakePosition.ticks
                    intakePositionMotor.power = positionalMotorPower
                    intakePositionMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
                IntakePosition.DOWN -> {
                    intakePositionMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
                    intakePositionMotor.targetPosition = intakePosition.ticks
                    intakePositionMotor.power = positionalMotorPower
                    intakePositionMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

                    isUpdating = true
                }
            }
            shouldUpdate = false
        } else if(intakeLocked)
            intakePositionMotor.power = 0.0

        brushVexMotor.power = intakeDirection.speed

        linearSlides.power = linearSlidesPower

        if(isUpdating && !intakePositionMotor.isBusy) {
            isUpdating = false
            intakePositionMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
            intakePositionMotor.power = 0.0
        }

        return RuckusTelemetryConverter.convertToMap(this)
    }

    override fun replayData(): List<Any> {
        return listOf(intakePosition, linearSlidesPower, intakeDirection)
    }

    override fun updateFromReplay(l: List<Any>) {
        intakePosition = l[0] as IntakePosition
        linearSlidesPower = l[1] as Double
        intakeDirection = l[2] as IntakeDirection
    }

    fun stop() {
        intakePositionMotor.power = 0.0
        linearSlides.power = 0.0
        brushVexMotor.power = 0.0
        /**
        intakePosition = IntakePosition.UP
        linearSlidesPower = 0.0
        intakeDirection = IntakeDirection.STOPPED
        **/
    }

}
