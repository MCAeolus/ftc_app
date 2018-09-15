package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.newresidence

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.GlyphIntake

/**
 * Created by Nathan.Smith.19 on 1/2/2018.
 */

@Autonomous(name="BLUE-1(Inner)")@Disabled
class Blue1Autonomous : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        //ColorCounter.RED_OR_BLUE = ColorCounterMachine.RoB.BLUE

        var vumark = VU_SECT.update()
        while(vumark == RelicRecoveryVuMark.UNKNOWN && !isStarted()){
            vumark = VU_SECT.update()
        }

        telemetry.addData("Vu", vumark.name)
        telemetry.update()

        waitForStart()

        val z_zero = IMU.initialOrientation.thirdAngle
        VU_SECT.deactivate()



        GlI.lift(true, true, true)
        hold(800)
        GlI.lift(false, false, false)

        drive(-1.0, 0.0, 0.0, 0.95, 1150)

        reposition(z_zero - 90, 0.3)
        hold(100)
        //drive(0.0, -1.0, 0.0, 0.6, 350)


        /**val cipherPos = cryptoDetector.cryptoBoxPositions[
        (if(     vumark == RelicRecoveryVuMark.LEFT) 0
        else if(vumark == RelicRecoveryVuMark.RIGHT)2
        else 1)]**/
        //cryptoDetector.disable()

        hold(100)

        //val turnDegrees = getTurnDegrees(cipherPos, 1.0) //1.0 meters
        //reposition(IMU.XYZ().thirdAngle - turnDegrees, 0.2)
        //hold(200)

        //ColorCounter.countSides(vumark, DRIVETRAIN, false)

        //reposition(z_zero + 90, 0.25)

        drive(0.0, 1.0, 0.0, 0.5, 100)

        GlI.setDoors(GlyphIntake.DoorState.OPEN)
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
        hold(500)

        linear_stop()
    }

}