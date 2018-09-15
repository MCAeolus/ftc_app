package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous
/*
import com.disnodeteam.dogecv.CameraViewDisplay
import com.disnodeteam.dogecv.detectors.CryptoboxDetector
import com.disnodeteam.dogecv.detectors.CryptoboxDetectorBlue
import com.disnodeteam.dogecv.detectors.GlyphDetector
**/
import com.qualcomm.robotcore.hardware.VoltageSensor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.*
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import kotlin.math.roundToLong

//import org.opencv.core.Point

/**
 * Created by Nathan.Smith.19 on 11/22/2017.
 */
open class AutonomousBase : LinearRobot(MecanumDrive(arrayOf("FL", "FR", "BL", "BR")),
                mapOf(
                        Pair("IMU", IMU()),
                        Pair("GlyphThroughput", GlyphIntake(arrayOf("CON_L", "CON_R", "L", "L2"), arrayOf("DO_L", "DO_R", "DRP", "KCK"))),
                        Pair("Color Sensor", ColorSensor()),
                        Pair("Slapper", SlapperTwo())
                )) {

    lateinit var C_S : ColorSensor
    lateinit var IMU : IMU
    //lateinit var ColorCounter : ColorCounterMachine
    lateinit var slapper : SlapperTwo
    lateinit var GlI : GlyphIntake
    lateinit var VU_SECT : VumarkSect
    lateinit var timer : ElapsedTime

    override fun runOpMode() {
        super.runOpMode()

        C_S = COMPONENTS["Color Sensor"] as ColorSensor
        hardwareMap.led.get("led").enable(false)

        //ColorCounter = COMPONENTS["Color Counter"] as ColorCounterMachine

        IMU = COMPONENTS["IMU"] as IMU

        VU_SECT = VumarkSect()
        VU_SECT.init(hardwareMap)

        GlI = COMPONENTS["GlyphThroughput"] as GlyphIntake
        GlI.setDoors(GlyphIntake.DoorState.CLOSED)
        GlI.drop(false)

        slapper = COMPONENTS["Slapper"] as SlapperTwo
        slapper.resting()

        /**
        glyphDetector = GlyphDetector()
        glyphDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance())
        **/

        telemetry.update()

        timer = ElapsedTime()

    }

    fun drive(x : Double, y : Double, r : Double, p : Double, t : Long = -1){
        DRIVETRAIN.move(
                x,
                y,
                r,
                p
        )
        if(t >= 0)hold(t)
        DRIVETRAIN.stop()
    }

    fun reposition(zPos : Float, p : Double = 0.35){

        while(Math.abs(IMU.XYZ().thirdAngle - zPos) > 3 && opModeIsActive()){
            val delta_pos = zPos - IMU.XYZ().thirdAngle
            DRIVETRAIN.move(
                    0.0,
                    0.0,
                    (if(delta_pos < 0) -1.0 else 1.0),
                    p
            )

        }
        DRIVETRAIN.stop()
    }

    /**fun getTurnDegrees(pixelPoint : Int, distAway : Double): Float {
        val centerX = cryptoDetector.SCREEN_WIDTH / 2

        val deltaX = pixelPoint - centerX

        val TWENTY_CM_IN_PIXELS = Math.abs(cryptoDetector.cryptoBoxPositions[1] - cryptoDetector.cryptoBoxPositions[0])

        val distAlt = (deltaX * .2)/TWENTY_CM_IN_PIXELS

        return Math.atan2(distAlt, distAway).toFloat()
    }**/

    fun expel_gli() {
        GlI.drop(false)
        GlI.kick(true)
        GlI.setDoors(GlyphIntake.DoorState.PARTIAL)
        GlI.runConveyors(GlyphIntake.DoorState.CLOSED, true)
    }

    fun kick() {
        GlI.kick(true)
        hold(200)
        GlI.kick(false)
    }

    fun immobile_gli() {
        GlI.drop(false)
        GlI.kick(false)
        GlI.setDoors(GlyphIntake.DoorState.OPEN)
        GlI.runConveyors(GlyphIntake.DoorState.CLOSED, false)
    }

    fun intake_gli() {
        GlI.drop(true)
        GlI.setDoors(GlyphIntake.DoorState.PARTIAL)
        GlI.runConveyors(GlyphIntake.DoorState.OPEN, true)
    }

    fun drive_with_ratio(x : Double, y : Double, r : Double, p : Double, t : Long = -1, VS : VoltageSensor) { //assume time is 13.5V

        val t_add = (Math.abs(13.5 - VS.voltage) * 150).roundToLong()
        drive(x, y, r, p, (t + t_add))
    }

    fun hold(time : Long){
        timer.reset()
        while(timer.milliseconds() < time){idle()}
    }
}