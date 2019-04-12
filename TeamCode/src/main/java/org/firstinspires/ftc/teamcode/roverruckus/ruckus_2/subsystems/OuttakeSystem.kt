package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.LoggedField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.RuckusTelemetryConverter

class OuttakeSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem(hardware, robot) {

    enum class DeliveryDirection(val speed : Double) {
        UP(1.0),
        DOWN(-1.0),
        STOPPED(0.0)
    }

    enum class DumpPosition(val pos : Double) {
        UP(0.0),
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
            shouldUpdate = true
            field = mode
        }

    @LoggedField(description = "dump position")
    var dumpPosition = DumpPosition.UP
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    private var shouldUpdate = false

    override fun update(): LinkedHashMap<String, Any> {

        deliveryLift.power = deliveryDirection.speed

        if(shouldUpdate) {
            dumpServo.position = dumpPosition.pos
            shouldUpdate = false
        }
        return RuckusTelemetryConverter.convertToMap(this)
    }

    override fun replayData(): List<Any> {
        return listOf(deliveryDirection, dumpPosition)
    }

    override fun updateFromReplay(l: List<Any>) {
        deliveryDirection = l[0] as DeliveryDirection
        dumpPosition = l[1] as DumpPosition
    }
}