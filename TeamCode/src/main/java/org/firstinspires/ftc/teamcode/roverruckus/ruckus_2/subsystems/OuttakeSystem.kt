package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem

class OuttakeSystem(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem() {

    enum class DeliveryDirection(val speed : Double) {
        UP(0.6),
        DOWN(0.6),
        STOPPED(0.0)
    }

    private val deliveryLift = hardware.get(DcMotor::class.java, HNAMES_RUCKUS.OUTTAKE_DELIVERY_SLIDE)

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
        return linkedMapOf("delivery direction" to deliveryDirection.name)
    }

    override fun replayData(): List<Any> {
        return listOf(deliveryDirection)
    }

    override fun updateFromReplay(l: List<Any>) {
        deliveryDirection = l[0] as DeliveryDirection
    }
}