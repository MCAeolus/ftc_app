package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.LoggedField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.RuckusTelemetryConverter

class OuttakeSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem(hardware, robot) {

    enum class DeliveryDirection(val speed : Double) {
        UP(1.0),
        DOWN(-0.25),
        STOPPED(0.0)
    }

    enum class DumpPosition(val pos : Double) {
        UP(0.1),
        DOWN(0.7)
    }

    private val deliveryLift = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.OUTTAKE_DELIVERY_SLIDE)
    private val dumpServo = hardware.get(Servo::class.java, HNAMES_RUCKUS.OUTTAKE_SERVO)

    init {
        deliveryLift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    @LoggedField(description = "delivery lift direction")
    var deliveryDirection = DeliveryDirection.STOPPED
        set(mode) {
            shouldUpdateSlides = true
            field = mode
        }

    @LoggedField(description = "dump position")
    var dumpPosition = DumpPosition.UP
        set(mode) {
            shouldUpdateDump = true
            field = mode
        }

    private var shouldUpdateSlides = false
    private var shouldUpdateDump = false

    @LoggedField(description = "controlling intake")
    var isControllingIntake = false

    @LoggedField(description = "slow mode intake")
    var isSlowModeIntake = false

    private var positionWhileControllingIntake = DeliveryDirection.STOPPED

    override fun update(): LinkedHashMap<String, Any> {

        if(shouldUpdateDump) {
            deliveryLift.power = deliveryDirection.speed * (if(isSlowModeIntake) 0.5 else 1.0)

            when(deliveryDirection) {
                DeliveryDirection.STOPPED -> if(isControllingIntake) {
                    isControllingIntake = false
                    robot.intakeSystem.intakePosition = IntakeSystem.IntakePosition.UP
                    positionWhileControllingIntake = DeliveryDirection.STOPPED
                }
                DeliveryDirection.UP,
                DeliveryDirection.DOWN -> {
                    if(!isControllingIntake && positionWhileControllingIntake != deliveryDirection) {
                        robot.intakeSystem.intakePosition = IntakeSystem.IntakePosition.DOWN
                        positionWhileControllingIntake = deliveryDirection
                        isControllingIntake = true
                    }
                }
            }

            shouldUpdateDump = false
        }

        if(shouldUpdateSlides) {
            dumpServo.position = dumpPosition.pos
            shouldUpdateSlides = false
        }
        return RuckusTelemetryConverter.convertToMap(this)
    }

    fun stop() {
        dumpServo.position = DumpPosition.UP.pos
        deliveryLift.power = 0.0
    }

    override fun replayData(): List<Any> {
        return listOf(deliveryDirection, dumpPosition)
    }

    override fun updateFromReplay(l: List<Any>) {
        deliveryDirection = l[0] as DeliveryDirection
        dumpPosition = l[1] as DumpPosition
    }
}