package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.util.CRYPTOBOX_POSITION_DATA
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.UltrasonicSensorSet
import java.util.concurrent.ThreadPoolExecutor
import javax.xml.xpath.XPathExpressionException

/**
 * Created by Nathan.Smith.19 on 2/17/2018.
 */
@Disabled
@Autonomous(name="RED-INNER")
class AutonomousREDINNER : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        GlyphThroughput.setStopper(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)
        var vumark = VU_SECT.update()
        while(/**vumark == RelicRecoveryVuMark.UNKNOWN && **/!isStarted()){
            vumark = VU_SECT.update()
        }

        telemetry.addData("Vu", vumark.name)
        telemetry.update()

        val time_forEXGlyphs = ElapsedTime()
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

        hold(400)

        drive(0.0, -1.0, 0.0, 0.65, 900)

        GlyphThroughput.setIntakePosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.IntakePosition.DOWN)

        hold(100)

        val z2 = 180 + z_zero
        reposition(z2, Rotation.THREE_SIXTY,0.25, 1.5)

        hold(100)

        align(vumark, 0.8)

        hold(100)

        reposition(z2, Rotation.THREE_SIXTY, 0.25)
        hold(100)

        if(Math.abs(getMarkValue(vumark) - UltrasonicSensors.getSensor(UltrasonicSensorSet.UltrasonicSide.RIGHT).cmUltrasonic()) > 2) {
            align(vumark, 0.2)
            hold(100)
            reposition(z2, Rotation.THREE_SIXTY, 0.25)
        }

        GlyphThroughput.setStopper(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.UP)

        align_vertical(CRYPTOBOX_POSITION_DATA.BACK_POS, 0.3)
        reposition(z2, Rotation.THREE_SIXTY, 0.2)

        drop(false, true)

        hold(100)

        drive(0.0, 1.0, 0.0, 0.4, 300)
        drive(0.0, -1.0, 0.0, 0.4, 500)
        GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)

        val z_move = z2 - setRotValue(vumark)
        reposition(z_move, Rotation.THREE_SIXTY, 0.25)
        GlyphThroughput.setStopper(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)

        initiateFlywheelValues()
        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE)

        drive(0.0, -1.0, 0.0, 0.7, if(vumark == RelicRecoveryVuMark.RIGHT) 1200 else 1100)
        hold(100)
        run_until_glyph(800, time_forEXGlyphs)
        hold(500)
        run_until_glyph(1000, time_forEXGlyphs)
        hold(100)
        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.OUTTAKE)
        //GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.UP)
        //GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.DOWN)
        reposition(z_move, Rotation.THREE_SIXTY, 0.25)
        drive(0.0, 1.0, 0.0, 0.7, if(vumark == RelicRecoveryVuMark.LEFT) 1100 else 1000)

        GlyphThroughput.stopFlywheels()
        reposition(z2, Rotation.THREE_SIXTY, 0.25)

        align(vumark, 0.5)
        hold(100)
        reposition(z2, Rotation.THREE_SIXTY, 0.25)
        hold(100)
        align_vertical(CRYPTOBOX_POSITION_DATA.BACK_POS, 0.3)
        hold(100)
        align(vumark, 0.2)
        hold(100)
        reposition(z2, Rotation.THREE_SIXTY, 0.25)
        hold(100)

        reposition(z2, Rotation.THREE_SIXTY, 0.25)
        drop(true, true)

        drive(0.0, 1.0, 0.0, 0.5, 400)
        drive(0.0, -1.0, 0.0, 0.5, 600)
        GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)
        GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.DOWN)
    }

    fun drop(doLift : Boolean, leavePlate : Boolean = false) {

        hold(100)

        if(doLift) {
            GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.UP)
            hold(100)
        }
        GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.UP)
        hold(500)
        if(!leavePlate) {
            GlyphThroughput.setBlockPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.BlockPosition.DOWN)
            hold(100)
        }

        if(doLift && !leavePlate)
            GlyphThroughput.setLiftPosition(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.LiftPosition.DOWN)
    }

    fun align(mark : RelicRecoveryVuMark, power : Double) {
        val shouldStop = reposition_DriveWithUltrasonic(getMarkValue(mark), UltrasonicSensorSet.UltrasonicSide.RIGHT, power, false)
        if(shouldStop) this.linear_stop()
    }

    fun align_vertical(distance : Double, power : Double) {
        val shouldStop = reposition_vertical(distance, power)
        if(shouldStop) this.linear_stop()
    }

    fun getMarkValue(mark : RelicRecoveryVuMark) : Double {
        when(mark){
            RelicRecoveryVuMark.LEFT -> return CRYPTOBOX_POSITION_DATA.INNER_FAR_POSITION
            RelicRecoveryVuMark.UNKNOWN,
            RelicRecoveryVuMark.CENTER -> return CRYPTOBOX_POSITION_DATA.INNER_CENTER_POSITION
            RelicRecoveryVuMark.RIGHT -> return CRYPTOBOX_POSITION_DATA.INNER_CLOSE_POSITION
        }
    }

    fun setRotValue(mark : RelicRecoveryVuMark) : Int {
        when(mark) {
            RelicRecoveryVuMark.RIGHT -> return 40
            RelicRecoveryVuMark.CENTER,
            RelicRecoveryVuMark.UNKNOWN,
            RelicRecoveryVuMark.LEFT -> return 30
        }
    }
}