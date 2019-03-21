package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2

import com.qualcomm.robotcore.hardware.HardwareMap

abstract class Subsystem {

    abstract fun update() : LinkedHashMap<String, Any>

    abstract fun replayData() : List<Any>

    abstract fun updateFromReplay(l : List<Any>)
}