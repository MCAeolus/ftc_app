package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.testing

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.common.drivetrain.util.EncoderAttachment

/**
 * Created by Nathan.Smith.19 on 1/10/2018.
 */

@Autonomous(name= "Encoding test")@Disabled
class EncoderTest : AutonomousBase() {

    val FAR_ANGLE = 22F
    val MED_ANGLE = 18F
    val CLOSE_ANGLE = 10F

    override fun runOpMode() {
        super.runOpMode()
        val drEncode = EncoderAttachment(DRIVETRAIN)
        drEncode.init(this)

        var vumark = VU_SECT.update()
        while(vumark == RelicRecoveryVuMark.UNKNOWN && !isStarted()){
            vumark = VU_SECT.update()
        }

        telemetry.addData("Vu", vumark.name)
        telemetry.update()

        val zero = IMU.initialOrientation.thirdAngle
        VU_SECT.deactivate()

        waitForStart()

        GlI.lift(true, true, true)
        hold(800)
        GlI.lift(false, false, false)

        drEncode.runByEncoder(0.0, -0.5, 0.0, 0.4, 26.5, this, 5F) // move 1 inch
        reposition(zero - 90, 0.2)
        //drEncode.runByEncoder(0.0, -0.5, 0.0, 0.5, 33.0, this)


        val ang = getAngle(vumark, true)

        reposition(IMU.XYZ().thirdAngle - ang)
        //ColorCounter.countSides(vumark, DRIVETRAIN, false, drEncode, this)

        var dist_move = 33.5

        when(ang){
            CLOSE_ANGLE ->
                dist_move = 33.5
            MED_ANGLE ->
                dist_move = 35.0
            FAR_ANGLE ->
                dist_move = 37.0

        }

        drEncode.runByEncoder(0.0, -0.5, 0.0, 0.45, dist_move, this, 5F)


        /**
        GlI.setDoors(GlyphThroughput.DoorState.OPEN)
        hold(400)
        expel_gli()
        val HOLD = ElapsedTime()
        HOLD.reset()
        var hold1 = 0
        hold(1500)
        immobile_gli()
        hold(200)
        expel_gli()
        hold(200)
        immobile_gli()
        hold(200)
        expel_gli()
        hold(2000)

        drive(0.0, 1.0, 0.0, 0.5, 300)

        immobile_gli()
        **/

        GlI.lift(false, true, true)
        hold(800)
        GlI.lift(false, false, false)


        hold(500)
    }



    fun getAngle(mark : RelicRecoveryVuMark, comingfrom : Boolean /** true == false **/) : Float {

        when(mark) {
            RelicRecoveryVuMark.RIGHT -> return if(comingfrom) FAR_ANGLE else CLOSE_ANGLE
            RelicRecoveryVuMark.LEFT -> return if(comingfrom) CLOSE_ANGLE else FAR_ANGLE
            else -> return MED_ANGLE
        }
    }
}