package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.newresidence

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.common.drivetrain.util.EncoderAttachment
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.GlyphIntake

/**
 * Created by Nathan.Smith.19 on 1/12/2018.
 */

@Autonomous(name="Blue-Inner")@Disabled
class BLUE1_NEW : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        val EncoderAtt = EncoderAttachment(DRIVETRAIN)

        waitForStart()

        GlI.lift(true, true, true)
        hold(800)
        GlI.lift(false, false, false)


        slapper.out()
        hold(500)



        if(C_S.RGB().R < C_S.RGB().B)
            slapper.left()
        else slapper.right()


        hold(1000)
        slapper.resting()

        EncoderAtt.runByEncoder(0.0, -0.5, 0.0, 0.5, 24.5, this, 5.0F)

        reposition(IMU.initialOrientation.thirdAngle + 90)

        drive(0.0, -0.5, 0.0, 0.6, 3000)

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

        //EncoderAtt.runByEncoder(0.0, -0.5, 0.0, 0.5, 32.0, this, 5.0F)

    }
}