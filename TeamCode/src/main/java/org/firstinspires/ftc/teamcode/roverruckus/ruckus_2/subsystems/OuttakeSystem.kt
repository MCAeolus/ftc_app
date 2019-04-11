package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
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

    private val deliveryLift = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.OUTTAKE_DELIVERY_SLIDE)

    init {
        deliveryLift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    @LoggedField(description = "delivery lift direction")
    var deliveryDirection = DeliveryDirection.STOPPED
        set(mode) {
            shouldUpdate = true
            field = mode
        }

    private var shouldUpdate = false

    override fun update(): LinkedHashMap<String, Any> {
        if(shouldUpdate) {
            deliveryLift.power = deliveryDirection.speed
            shouldUpdate = false
        }
        return RuckusTelemetryConverter.convertToMap(this)
    }

    override fun replayData(): List<Any> {
        return listOf(deliveryDirection)
    }

    override fun updateFromReplay(l: List<Any>) {
        deliveryDirection = l[0] as DeliveryDirection
    }
}