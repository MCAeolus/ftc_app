package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes.backu

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.util.CRYPTOBOX_POSITION_DATA
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.UltrasonicSensorSet

/**
 * Created by Nathan.Smith.19 on 2/17/2018.
 */

@Autonomous(name="RED-OUTER-BACKUP")
class AutonomousREDOUTERBACKUP : AutonomousBase() {

    var intakeOut = false

    override fun runOpMode() {
        super.runOpMode()
        var vumark = VU_SECT.update()
        while(/**vumark == RelicRecoveryVuMark.UNKNOWN && **/!isStarted()){
            vumark = VU_SECT.update()
        }

        telemetry.addData("Vu", vumark.name)
        telemetry.update()
        waitForStart()

        val z_zero = IMU.initialOrientation.thirdAngle
        VU_SECT.deactivate()

        JewelSlapper.out()
        hold(1000)
        if(ColorSensor.RGB().R > ColorSensor.RGB().B)
            JewelSlapper.left()
        else JewelSlapper.right()

        hold(200)
        JewelSlapper.resting()

        hold(500)

        drive(0.0, -1.0, 0.0, 0.65, 800)

        hold(100)

        val z2 = z_zero + 90
        reposition(z2, Rotation.ONE_EIGHTY, 0.25, 1.5)

        hold(100)

        align(vumark, 0.8)

        hold(100)

        reposition(z2, Rotation.ONE_EIGHTY,0.25)
        hold(100)

        drive(0.0, 1.0, 0.0, 0.65, 150)
        hold(100)

        if(Math.abs(getMarkValue(vumark) - UltrasonicSensors.getSensor(UltrasonicSensorSet.UltrasonicSide.RIGHT).cmUltrasonic()) > 2) {
            align(vumark, 0.2)
            hold(100)
            reposition(z2, Rotation.ONE_EIGHTY, 0.25)
        }

        drive(0.0, 1.0, 0.0, 0.6, 400)

        drive(0.0, -1.0, 0.0, 0.5, 450)

        GlyphThroughput.setStopper(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.UP)
        drop()

        hold(100)

        drive(0.0, 1.0, 0.0, 0.65, 450)

        drive(0.0, -1.0, 0.0, 0.65, 400)

        hold(100)

        drive(0.0, 1.0, 0.0, 0.3, 150)
        drive(0.0, -1.0, 0.0, 0.3, 150)
        drive(0.0, 1.0, 0.0, 0.3, 150)
        drive(0.0, -1.0, 0.0, 0.3, 150)

    }

    fun drop() {
        if(!intakeOut){
            GlyphThroughput.setIntakePosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.IntakePosition.DOWN)
            intakeOut = true
        }

        hold(100)

        GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.UP)

        hold(100)
        GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.UP)
        hold(500)
        GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)
        hold(100)

        GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.DOWN)
    }

    fun align(mark : RelicRecoveryVuMark, power : Double) {
        reposition_DriveWithUltrasonic(getMarkValue(mark), UltrasonicSensorSet.UltrasonicSide.RIGHT, power, false)

    }

    fun getMarkValue(mark : RelicRecoveryVuMark) : Double {
        when(mark){
            RelicRecoveryVuMark.LEFT -> return CRYPTOBOX_POSITION_DATA.OUTSIDE_FAR_POSITION
            RelicRecoveryVuMark.UNKNOWN,
            RelicRecoveryVuMark.CENTER -> return CRYPTOBOX_POSITION_DATA.OUTSIDE_CENTER_POSITION
            RelicRecoveryVuMark.RIGHT -> return CRYPTOBOX_POSITION_DATA.OUTSIDE_CLOSE_POSITION
        }
    }
}