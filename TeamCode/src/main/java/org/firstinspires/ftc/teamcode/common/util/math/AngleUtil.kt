package org.firstinspires.ftc.teamcode.common.util.math

class AngleUtil {

    enum class ClampRange(val r : Double) {
        THREE_SIXTY(360.0), ONE_EIGHTY(180.0)
    }

    companion object {
        fun normalize(angle : Double, unit : Vector2d.AngleUnit = Vector2d.AngleUnit.RADIANS, range : ClampRange = ClampRange.THREE_SIXTY) : Double {
            var nAngle = angle
            if(unit == Vector2d.AngleUnit.RADIANS) nAngle *= Vector2d.AngleUnit.DEGREES.convertTo
            nAngle %= range.r
            if(nAngle < 0) nAngle += range.r
            return nAngle * when(unit) {
                Vector2d.AngleUnit.RADIANS -> unit.convertTo
                else -> 1.0
            }
        }
    }

}