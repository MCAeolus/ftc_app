package org.firstinspires.ftc.teamcode.common.util.math

import org.firstinspires.ftc.teamcode.common.util.math.Vector2d.Companion.EPSILON
import java.io.Serializable

class Pose2d(val position : Vector2d, val heading : Double) : Serializable {

    constructor(x : Double, y : Double, heading : Double) : this(Vector2d(x, y), heading)
    constructor(x : Int, y : Int, heading : Int) : this(Vector2d(x, y), heading.toDouble())


    fun x() = position.x
    fun y() = position.y

    fun distance(pose : Pose2d) = Math.hypot(x() - pose.x(), y() - pose.y())

    fun add(pose : Pose2d, normalize : Boolean = false) = Pose2d(position.add(pose.position), AngleUtil.normalize(heading + pose.heading))

    fun multiply(scalar : Double) = Pose2d(scalar * x(), scalar * y(), scalar * heading)

    fun multiply(velScalar : Double, headingScalar : Double ) = Pose2d(velScalar * x(), velScalar * y(), headingScalar * heading)

    fun copy() = Pose2d(position, heading)

    override fun equals(other: Any?): Boolean {
        if(other is Pose2d) {
            return (position == other.position && Math.abs(heading - other.heading) < EPSILON)
        }else return false
    }

    override fun toString(): String {
        return position.toString() + ", $heading original, ${heading * Vector2d.AngleUnit.DEGREES.convertTo} as degrees"
    }
}
