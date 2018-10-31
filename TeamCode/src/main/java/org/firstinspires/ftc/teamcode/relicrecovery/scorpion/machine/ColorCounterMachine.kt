package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.drivetrain.util.EncoderAttachment
import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 12/7/2017.
 */
class ColorCounterMachine : IMachine {

    lateinit var sensor : com.qualcomm.robotcore.hardware.ColorSensor

    val R_VAL_DIFF = 7
    val B_VAL_DIFF = 7

    enum class RoB {
        RED, BLUE
    }
    var RED_OR_BLUE = RoB.RED

    override fun init(robot: IRobot) {
        sensor = robot.opMode().hardwareMap.get(com.qualcomm.robotcore.hardware.ColorSensor::class.java, "OD_OD")
        sensor.enableLed(false)
    }

    fun countSides(mark : RelicRecoveryVuMark, dr : IDriveTrain, direction : Boolean /**true == left**/, eAtt : EncoderAttachment, base : AutonomousBase): Boolean {
        var countDown = 0
        var baseRGB = 0
        var DIFF = 0

        when(mark){
            RelicRecoveryVuMark.LEFT ->
                countDown = if(direction) 2 else 0

            RelicRecoveryVuMark.RIGHT ->
                countDown = if(direction) 0 else 2

            else ->
                countDown = 1
        }

        base.telemetry.addData("count", countDown)
        base.telemetry.update()


        val CRYPTO_DIST = 7.6
        val SHELF_WIDTH = 1

        base.GlI.drop(true)

        moveToCryptoShelf(dr, direction, base.telemetry)

        base.GlI.drop(false)

        val dist_move = (CRYPTO_DIST * countDown)
        eAtt.runByEncoder(if(direction)0.5 else -0.5, 0.0, 0.0, 0.25, dist_move, base, 10F)

        base.GlI.drop(true)

        moveToCryptoShelf(dr, direction, base.telemetry)

        base.GlI.drop(false)

        val ext = 3.75

        eAtt.runByEncoder(if(direction)0.5 else -0.5, 0.0, 0.0, 0.3, ext, base, 10F)
        /**if(mark == RelicRecoveryVuMark.RIGHT){
            if(direction){

            }else{

            }
        }else if(mark == RelicRecoveryVuMark.CENTER){
            if(direction){

            }else{

            }
        }else if(mark == RelicRecoveryVuMark.LEFT){
            if(direction){

            }else{

            }
        }
        **/
        dr.stop()
        return true
    }

    override fun stop() {}

    fun moveToCryptoShelf(dr : IDriveTrain, direction : Boolean /**true == left**/, telem : Telemetry) {
        while((if(RED_OR_BLUE == RoB.RED)sensor.red() - sensor.blue() else sensor.blue() - sensor.red()) < 40){
            dr.move(if(direction)0.5 else -0.5, 0.0, 0.0, 0.25)
        }
        dr.stop()
    }


}