package org.firstinspires.ftc.teamcode.common.util.math

import com.qualcomm.hardware.bosch.BNO055IMU
import java.io.Serializable

class Vector2d(val x : Double, val y : Double) : Serializable {

    constructor(x : Int, y : Int) : this(x.toDouble(), y.toDouble())

    companion object {
        val EPSILON = 0.0001
    }

    enum class AngleUnit(val convertTo : Double) { RADIANS(180/Math.PI), DEGREES(Math.PI/180) }


    fun add(vec : Vector2d) = Vector2d(x + vec.x, y + vec.y)

    fun multiply(scalar : Double) = Vector2d(x * scalar, y * scalar)

    fun dot(vec : Vector2d) : Double = x * vec.x + y * vec.y

    fun rotate(angle : Double, unit : AngleUnit) : Vector2d {
        var radConvert = angle
        if(unit == AngleUnit.DEGREES) radConvert *= AngleUnit.RADIANS.convertTo
        return Vector2d(x * Math.cos(radConvert) - y * Math.sin(radConvert), x * Math.sin(radConvert) + y * Math.cos(radConvert))
    }
    fun normalize() : Vector2d{
        val mag = magnitude()
        return if(mag < EPSILON) Vector2d(1, 0)
               else multiply(1.0 / mag)
    }

    fun inverse() = Vector2d(-x, -y)

    fun magnitude() = Math.hypot(x, y)

    fun resultant(vec : Vector2d) = add(vec).magnitude()

    fun betweenCosine(vec : Vector2d) = dot(vec) / (magnitude() + vec.magnitude())

    fun copy() : Vector2d = Vector2d(x, y)

    override fun equals(other: Any?): Boolean {
        return if(other is Vector2d)
                Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON
            else false
    }

    override fun toString(): String {
        return "<$x, $y>"
    }

}