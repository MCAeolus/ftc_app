package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous

import com.qualcomm.robotcore.hardware.HardwareDeviceHealth
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.*
import org.firstinspires.ftc.teamcode.common.common_machines.IMU

/**
 * Created by Nathan.Smith.19 on 2/14/2018.
 */
open class

AutonomousBase : LinearRobot(MecanumDrive(arrayOf("FL", "FR", "BL", "BR")),
                    mapOf(
                            Pair("IMU", IMU()),
                            Pair("GlyphThroughput", GlyphThroughput()),
                            Pair("Color Sensor", ColorSensor()),
                            Pair("Slapper", JewelSlapper()),
                            Pair("Ultrasonic Pair", UltrasonicSensorSet()),
                            Pair("Relic Extender", RelicExtender())
                    )) {

    enum class Rotation { THREE_SIXTY, ONE_EIGHTY}


    lateinit var IMU : IMU
    lateinit var GlyphThroughput : GlyphThroughput
    lateinit var ColorSensor : ColorSensor
    lateinit var JewelSlapper : JewelSlapper
    lateinit var UltrasonicSensors : UltrasonicSensorSet
    lateinit var VU_SECT : VumarkSect

    val TIMER = ElapsedTime()

    var DESIRABLE_ENCODING_RATE : Double = 0.0
    var THRESHOLD_ENCODING_RATE_DIFFERENCE : Double = 0.0
    var FLYWHEEL_ENCODING_HASINITIALIZED = false

    override fun runOpMode() {
        super.runOpMode()

        IMU = COMPONENTS["IMU"] as IMU
        GlyphThroughput = COMPONENTS["GlyphThroughput"] as GlyphThroughput
        ColorSensor = COMPONENTS["Color Sensor"] as ColorSensor
        JewelSlapper = COMPONENTS["Slapper"] as JewelSlapper
        UltrasonicSensors = COMPONENTS["Ultrasonic Pair"] as UltrasonicSensorSet
        VU_SECT = VumarkSect()
        VU_SECT.init(hardwareMap)
    }


    fun hold(time : Long){
        TIMER.reset()
        while(TIMER.milliseconds() < time && opModeIsActive()){idle()}
    }

    fun drive(x : Double, y : Double, r : Double, p : Double, t : Long = -1){
        driveVal(x, y, r, p)
        if(t >= 0)hold(t)
        DRIVETRAIN.stop()
    }

    fun driveVal(x : Double, y : Double, r : Double, p : Double){
        DRIVETRAIN.move(
                -x,
                y,
                -r,
                p
        )
    }

    fun horizontallyAlign(distance : Double, side : UltrasonicSensorSet.UltrasonicSide, power : Double, rot : Rotation) {
        val us = UltrasonicSensors.getSensor(side)

        val initialRot = IMU.XYZ().thirdAngle
        var deltaDistance = distance - (us.cmUltrasonic())
        while(Math.abs(deltaDistance) > 1 && opModeIsActive()){
            deltaDistance = distance - (us.cmUltrasonic())
            driveVal(   if(deltaDistance > 0) 0.7 else -0.7,
                    0.0,
                    0.0,
                    power
            )
        }
        DRIVETRAIN.stop()
        reposition(initialRot, rot)
    }

    fun reposition(zPos : Float, rot : Rotation, p : Double = 0.35, sensitivity : Double = 3.0){

        when(rot) { //there are two ways to handle degrees- [-180, 180] degrees and [0, 360] degrees. We need to
                    //employ one of the two for autonomous for accurate position handling.
            Rotation.ONE_EIGHTY -> { //for the limit of [-180, 180] degrees
                while(Math.abs(IMU.XYZ().thirdAngle - zPos) > sensitivity && opModeIsActive()){ //we run to check whether ot not the delta is meeting the given sensitivity.
                    val delta_pos = zPos - IMU.XYZ().thirdAngle //grab the delta (primarily for the sign- should the robot rotate CW or CCW?)
                    driveVal( //a default method that allows us to simply control the robot using values and no time parameter.
                            0.0,
                            0.0,
                            (if(delta_pos < 0) 1.0 else -1.0),
                            p
                    )

                }
            }
            Rotation.THREE_SIXTY -> { //for the limit of [0, 360] degrees
                while(Math.abs(IMU.getZ360() - zPos) > sensitivity && opModeIsActive()){ //we run to check whether ot not the delta is meeting the given sensitivity.
                    val delta_pos = zPos - IMU.getZ360() //grab the delta (primarily for the sign- should the robot rotate CW or CCW?)
                    driveVal( //a default method that allows us to simply control the robot using values an no time parameter.
                            0.0,
                            0.0,
                            (if(delta_pos < 0) 1.0 else -1.0),
                            p
                    )

                }
            }
        }

        DRIVETRAIN.stop() //at the end of this method the drivetrain should stop moving.
    }

    fun reposition_DriveWithUltrasonic(distance : Double, side : UltrasonicSensorSet.UltrasonicSide, p : Double, flip : Boolean) : Boolean {
        val us = UltrasonicSensors.getSensor(side) //grab the ultrasonic sensor that has been defined (left, right, or back sensor)

        var watchdog_unhealthyStart = ElapsedTime() //this is discussed in W1
        var isUnhealthy = false //discussed in W1
        var shouldStop_error = false //discussed in W1
        var deltaDistance = distance - (us.cmUltrasonic()) //the initial delta position between the ideal position and the current position
        while(Math.abs(deltaDistance) > 1 && opModeIsActive()){ //check until the position is within its ideal sensitivity or the op mode ends

            val watchdog_return = watchdog_sensorHealthStatus(isUnhealthy, us.deviceClient, 3000, watchdog_unhealthyStart) //discussed in W1

            isUnhealthy = watchdog_return.first //watchdog return values, disccused in W1
            shouldStop_error = watchdog_return.second

            if(!isUnhealthy) { //whether or not the watchdog has found an issue
                deltaDistance = distance - (us.cmUltrasonic()) //update the distance delta
                driveVal((if (deltaDistance > 0) 0.7 else -0.7) * if (!flip) 1 else -1, //basic driving method used by applying normal drivetrain values
                        0.0,
                        0.0,
                        p
                )
            }else
                DRIVETRAIN.stop() //if the watchdog has found an issue then no activity should occur until the watchdog
                                  //either resolves the issue or forces the opmode to shut down.

            if(shouldStop_error)break //if watchdog has found a fatal issue stop the opmode.
        }
        DRIVETRAIN.stop() //stop the robot once the method has finished.

        return shouldStop_error //return whether or not the opmode should stop itself.
    }

    fun watchdog_sensorHealthStatus(isUnhealthy : Boolean, sensor : I2cDeviceSynch, timeout : Long, timer : ElapsedTime) : Pair<Boolean, Boolean> {
        var unhealthy = isUnhealthy //a copy of the unhealthy boolean- this is passed back and forth between the host method and this method
        var shouldStop = false //whether or not the host method should instantly stop its run.

        if(sensor.healthStatus == HardwareDeviceHealth.HealthStatus.UNHEALTHY) { //check if the given sensor has become unhealthy
            if(!unhealthy) { //if the robot was not formerly unhealthy then it starts now
                timer.reset() //the passed timer should reset
                unhealthy = true //set the unhealthy boolean to be true
            }else {
                if(timer.milliseconds() > timeout) shouldStop = true //if more time has passed than the given timeout method then stop all methods
            }
        }
        else if(sensor.healthStatus == HardwareDeviceHealth.HealthStatus.HEALTHY) //if the sensor is healthy
            if(unhealthy)unhealthy = false //if the sensor was unhealthy, then reset any calls

        return Pair(unhealthy, shouldStop) //return a pair of booleans- 1. giving whether or not the sensor is unhealthy, 2. returning whether or not the host method should end
    }

    fun reposition_vertical(distance : Double, p : Double) : Boolean {
        val us = UltrasonicSensors.sensor_b //there is only one sensor that can be used for this- the back ultrasonic sensor.

        var deltaDistance = distance - (us.cmUltrasonic()) //the initial delta position.

        var isUnhealthy = false //explained in W1
        var shouldStop_error = false //explained in W1

        val watchdog_unhealthyStart = ElapsedTime() //explained in W1

        while(Math.abs(deltaDistance) > 1 && opModeIsActive()){ //loop while either the position delta is too large or the op mode is still active.

            val watchdog_return = watchdog_sensorHealthStatus(isUnhealthy, us.deviceClient, 2000, watchdog_unhealthyStart) //return values for the watchdog, explained in W1

            isUnhealthy = watchdog_return.first //watchdog return values, explained in W1
            shouldStop_error = watchdog_return.second //watchdog return values, explained in W1

            if(!isUnhealthy) { //whether or not watchdog has found an issue.
                deltaDistance = distance - (us.cmUltrasonic()) //update the position delta
                driveVal(0.0, //a basic driving method using power values for the motors to calculate.
                        if (deltaDistance > 0) -0.7 else 0.7,
                        0.0,
                        p
                )
            }else DRIVETRAIN.stop() //if the watchdog has found an issue the robot should not keep running.

            if(shouldStop_error) break //if the watchdog cant resolve the issue, stop the robot.
        }
        DRIVETRAIN.stop() //stop driving the drivetrain once the method has finished.
        return shouldStop_error //return whether or not the opmode should end after this return.
    }

    fun initiateFlywheelValues() {
        val initial_encoder_pos = GlyphThroughput.flyRunL.currentPosition //we are going to calculate a rough estimate of how fast the intake should run when there is no glyphs.

        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE) //run the intake
        hold(2000) //wait 2000 milliseconds (or 2 seconds) for the wheels to spin up and run some.
        GlyphThroughput.stopFlywheels() //stop the flywheels.

        val final_encoder_pos = GlyphThroughput.flyRunL.currentPosition //grab the final position of the encoders
        val delta_encoder_pos = final_encoder_pos - initial_encoder_pos //calculate the encoder position delta

        DESIRABLE_ENCODING_RATE = Math.abs(delta_encoder_pos / 2000.0) //calculate the encoder/time
        THRESHOLD_ENCODING_RATE_DIFFERENCE = DESIRABLE_ENCODING_RATE / 2.75//this is the threshold for minimum speed of the intake if there is no glyph in it.
        FLYWHEEL_ENCODING_HASINITIALIZED = true //global variable that allows the program to know the values have been properly initialized.

        telemetry.addData("FLYWHEEL RATIO", DESIRABLE_ENCODING_RATE) //write the values to phone for data collection.
        telemetry.addData("RUNRATE THRESHOLD", THRESHOLD_ENCODING_RATE_DIFFERENCE)
        telemetry.update()
    }

    @Deprecated(message="no longer in use- use run_until_glyph()")
    fun grabGlyph(millis : Long, timer : ElapsedTime) {

        if(!FLYWHEEL_ENCODING_HASINITIALIZED)
            initiateFlywheelValues()

        var last_encoder_position = GlyphThroughput.flyRunL.currentPosition
        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE)

        val encode_check_time = 20

        var shouldEnd = false

        timer.reset()

        var last_millis_check = timer.milliseconds()
        var tries = 0
        var run = 0

        DRIVETRAIN.move(0.0, -1.0, 0.0, 0.7)
        while(timer.milliseconds() < millis && !shouldEnd) {
            val current_millis = timer.milliseconds()
            if(timer.milliseconds() - last_millis_check >= encode_check_time) {
                run++
                telemetry.addData("Runs", run)
                telemetry.update()

                val delta_milliseconds = (current_millis - last_millis_check)
                val delta_encoder_position = (GlyphThroughput.flyRunL.currentPosition - last_encoder_position)
                val encoder_rate_delta =
                Math.abs(delta_encoder_position / delta_milliseconds)

                last_millis_check = timer.milliseconds()
                last_encoder_position = GlyphThroughput.flyRunL.currentPosition

                if (encoder_rate_delta  <= THRESHOLD_ENCODING_RATE_DIFFERENCE) {
                    if (tries < 2) {
                        DRIVETRAIN.stop()
                        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.OUTTAKE)
                        hold(300)
                        drive(0.0, 1.0, 0.0, 0.7, timer.milliseconds().toLong())
                        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE)
                        timer.reset()

                        last_millis_check = timer.milliseconds()

                        tries++
                        DRIVETRAIN.move(0.0, -1.0, 0.0, 0.7)
                    } else {
                        drive(0.0, 1.0, 0.0, 0.7, timer.milliseconds().toLong())
                        shouldEnd = true
                    }
                }else {
                    GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE)
                }
            }
        }
        if(!shouldEnd)drive(0.0, 1.0, 0.0, 0.7, millis)

    }

    fun run_until_glyph(timeout : Long, timer : ElapsedTime) {
        if(!FLYWHEEL_ENCODING_HASINITIALIZED) //check if the flywheel coefficients are initialized
            initiateFlywheelValues() //if not then initialize them

        var last_encoder_position = GlyphThroughput.flyRunL.currentPosition //set first encoder position for deltas
        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.INTAKE) //set the glyph intake to run

        timer.reset() //reset the timeout timer (for when the glyph intake should stop trying to run)

        var last_millis_check = timer.milliseconds() //last time the while loop ran- for calculating deltas

        var block_millis = 0L //how long the drivetrain should run backwards after picking up a glyph
        var run = 0 //measure how many times the while loop has run

        DRIVETRAIN.move(0.0, -1.0, 0.0, 0.7) //move the drivetrain forwards
        while(timer.milliseconds() < timeout) { //run while the method has not timed out
            val current_millis = timer.milliseconds() //current time for deltas
            val current_encodes = GlyphThroughput.flyRunL.currentPosition //current encode position for deltas
            run++//increment run counter

            telemetry.addData("Runs", run) //update counter on phone
            telemetry.update()

            val delta_milliseconds = (current_millis - last_millis_check) //time delta for encoder/time delta
            val delta_encoder_position = (current_encodes - last_encoder_position) //encoder delta for encoder/time delta
            val encoder_rate_delta = //complete delta for position/time
                    Math.abs(delta_encoder_position / delta_milliseconds)

            last_encoder_position = current_encodes //update the former encoder position
            last_millis_check = current_millis //update the former time position

            if (encoder_rate_delta  <= THRESHOLD_ENCODING_RATE_DIFFERENCE) { //check if the delta is below the run threshold
                block_millis = current_millis.toLong() //now that the threshold has been met set how long the drivetrain should run backwards.
                break
            }
        }
        hold(150) //hold 250 milliseconds for the drivetrain to keep running into the glyph
        DRIVETRAIN.stop() //stop the drivetrain from moving forwards
        hold(1000) //let the flywheels run for 1 more second
        GlyphThroughput.runFlywheels(org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.GlyphThroughput.FlywheelDirection.OUTTAKE) //set the flywheels to outtake to remove any excess glyphs
        drive(0.0, 1.0, 0.0, 0.7, block_millis) //reset the position of the drivetrain
    }


}