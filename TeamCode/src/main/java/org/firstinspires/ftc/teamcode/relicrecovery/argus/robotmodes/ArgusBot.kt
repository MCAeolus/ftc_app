package org.firstinspires.ftc.teamcode.relicrecovery.argus.robotmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.util.CRYPTOBOX_POSITION_DATA
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.*
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.IMU

/**
 * Created by Nathan.Smith.19 on 1/19/2018.
 */

@TeleOp(name="ArgusBot")
class ArgusBot : Robot(MecanumDrive(arrayOf("FL", "FR", "BL", "BR")), //Here all motors, etc. are initialized
        mapOf(
                Pair("Glyph Throughput", GlyphThroughput()),           //each of these has their own class
                Pair("Jewel Slapper", JewelSlapper()),                 //which grabs motors, servos, sensors
                Pair("Relic Extender", RelicExtender()),               //properly handle shutting down each
                Pair("Ultrasonic Sensor Set", UltrasonicSensorSet()),  //component of the robot as well as
                Pair("IMU", IMU())                                     //custom handling methods.
        )) {

    val TIMER = ElapsedTime() //legacy timer for calculating deltas
    var LAST_TIME = 0.0       //last time timer was checked for delta
    var LAST_ENCODER_POSITION = 0 //last enooder position for delta

    var LAST_DELTAS = arrayListOf<Double>() //calculated deltas for averaging

    val CONST_SLOW_ROBOT_SPEED = 0.33 //slow coefficient
    val CONST_MAX_ROBOT_SPEED = 1.0 //normal coefficient


    var drivetrain_powerMod = 1.0 //current powermod of the drivetrain
    var left_stick1 = false //toggle boolean switch for left stick on controller 1

    var left_stick2 = false //toggle boolean switch for left stick on controller 2

    var x1 = false //toggle boolean switch for x button on controller 1
    var a1 = false //toggle boolean switch for a button on controller 1

    var left_bumper2 = false //legacy toggle boolean switch for left bumper on controller 2
    var right_bumper2 = false //legacy toggle boolean switch for right bumper on controller 2

    var left_trigger2 = false //legacy toggle boolean switch for left trigger on controller 2
    var right_trigger2 = false //legacy toggle boolean switch for right trigger on controller 2

    var dpad_down1 = false //toggle boolean switch for dpad down on controller 1
    var stopper_pos = GlyphThroughput.BlockPosition.DOWN //current position of the block stopper for the glyph outtake

    var runs = 0L //the total times that the main loop of tele-op has been run
    var liftRunning = false //whether or not the lift is being autonomously run

    lateinit var thru : GlyphThroughput //the object holder for the glyph throughput
    lateinit var jewel_slapper : JewelSlapper //the object holder for the jewel slapper
    lateinit var relic_extender : RelicExtender //the object holder for the relic extender
    lateinit var ultrasonic_set : UltrasonicSensorSet //the object holder for the ultrasonic sensors
    lateinit var IMU : IMU //the object holder for the internal REV IMU

    var US_sensor_side = UltrasonicSensorSet.UltrasonicSide.RIGHT //the sensor that is being used for ultrasonic data
    var crypto_placement = CRYPTOBOX_POSITION_DATA.CryptoPlacement.INSIDE //the coefficients being used for cryptobox positioning

    override fun start() { //the initialization method for all object containers that hold the different robot components
        //all of these components were already initialized before they are placed in object containers.

        thru = COMPONENTS["Glyph Throughput"] as GlyphThroughput
        thru.setStopper(GlyphThroughput.BlockPosition.DOWN)
        jewel_slapper = COMPONENTS["Jewel Slapper"] as JewelSlapper
        relic_extender = COMPONENTS["Relic Extender"] as RelicExtender
        ultrasonic_set = COMPONENTS["Ultrasonic Sensor Set"] as UltrasonicSensorSet
        IMU = COMPONENTS["IMU"] as IMU

        TIMER.reset() //reset the timer for use in the loop for calculating encoder position/time deltas
        LAST_TIME = TIMER.milliseconds() //initialize the time used to calculate encoder position/time deltas
        LAST_ENCODER_POSITION = thru.flyRunL.currentPosition //initialize the encoder position used to calculate encoder position/tiem deltas
    }


        override fun loop() { //the main heartbeat of tele-op.
            runs++ //increment the runs variable as the loop has run +1 times

            jewel_slapper.resting_nonlinear() //force the jewel slapper to hold its resting position so it doesnt fall out of place


            //////////////////////
            //gamepad 1 controls//
            //////////////////////

            //drivetrain control logic
            /**
             * Explanation:
             *
             * check if the left stick button has been pressed (and only triggers once, hence the left_stick1 variable,
             * which is used to state whether or not the button has already been pressed).
             * On activation, the drivetrain power mod is toggled between being full speed or slow speed coefficients.
             *
             */
            if(gamepad1.left_stick_button && !left_stick1){
                drivetrain_powerMod = if(drivetrain_powerMod == CONST_MAX_ROBOT_SPEED)
                    CONST_SLOW_ROBOT_SPEED else CONST_MAX_ROBOT_SPEED
                left_stick1 = true
            }else if(!gamepad1.left_stick_button)
                left_stick1 = false

            //drivetrain motor power application
            /**
             * Explanation:
             *
             * calculate all the x and y's modified by the drivetrain powermod to control the drivetrain using
             * the left (cardinal) and right (rotational) joysticks.
             */
            DRIVETRAIN.move(-gamepad1.left_stick_x.toDouble() * drivetrain_powerMod, gamepad1.left_stick_y.toDouble() * drivetrain_powerMod, -gamepad1.right_stick_x.toDouble() * drivetrain_powerMod)

            //intake control logic
            /**
             * Explanation:
             *
             * check if the left trigger has been pressed at all and make sure the lift is not up (which would cause
             * blocks to get stuck inside the robot). if this passes, then run the intake forwards.
             * if the right trigger has been pressed, then run the outtake backwards.
             */
            if(gamepad1.left_trigger > 0.0 && thru.liftPos != GlyphThroughput.LiftPosition.UP)
                thru.runFlywheels(GlyphThroughput.FlywheelDirection.INTAKE)
            else if(gamepad1.right_trigger > 0.0)
                thru.runFlywheels(GlyphThroughput.FlywheelDirection.OUTTAKE)
            else thru.stopFlywheels()

            //block dump logic
            /**
             * Explanations:
             *
             * if the b button has been pressed, rotate the block dump to vertical position to drop the blocks.
             * the default position is down, parallel to the ground.
             */
            if(gamepad1.b)
                thru.setBlockPosition(GlyphThroughput.BlockPosition.UP)
            else thru.setBlockPosition(GlyphThroughput.BlockPosition.DOWN)

            //block lift logic
            /**
             * Explanations:
             *
             * if the dpad down button is pressed (and has not already been pressed) then toggle the stopper
             * position.
             * the stopper moves between blockading the block dump and being positioned slightly beyond vertical
             */
            if(gamepad1.dpad_down && !dpad_down1){
                stopper_pos = if(stopper_pos == GlyphThroughput.BlockPosition.UP) GlyphThroughput.BlockPosition.DOWN
                else GlyphThroughput.BlockPosition.UP
                dpad_down1 = true
            } else if(!gamepad1.dpad_down && dpad_down1)
                dpad_down1 = false

            //actually apply the stopper position (this stops the stopper from being deinitialized)
            thru.setStopper(stopper_pos)

            //autnomous lifting control logic
            if(liftRunning) { //check whether or not the lift is currently autonomously running
                if(!thru.liftPull.isBusy){ //check whether or not the lift motor is 'busy'
                    //if the lift is no longer busy
                    liftRunning = false //set the liftRunning to false
                    thru.liftPull.power = 0.0 //set the motor power to 0
                    thru.liftPull.mode = DcMotor.RunMode.RUN_USING_ENCODER //set the motor mode to no longer run solely
                                                                           //off of encoders
                }
            }

            //autonomous lift activation logic
            /**
             * Explanation:
             *
             * if x has been pressed (and the lift isnt already lifting) then toggle the lift position
             * and begin the automatic logic loop.
             *
             * a side-effect of running this is that the robot drivetrain is toggled down so that glyphs don't
             * fall out as easily
             */
            if(gamepad1.x && !x1 && !liftRunning) {
                x1 = true
                liftRunning = true
                val liftpos = if (thru.liftPos == GlyphThroughput.LiftPosition.DOWN)
                    GlyphThroughput.LiftPosition.UP else GlyphThroughput.LiftPosition.DOWN

                if(liftpos == GlyphThroughput.LiftPosition.UP)
                    drivetrain_powerMod = CONST_SLOW_ROBOT_SPEED
                else if(liftpos == GlyphThroughput.LiftPosition.DOWN)
                    drivetrain_powerMod = CONST_MAX_ROBOT_SPEED

                thru.setLiftPosition_UNHANDLED(liftpos)
            }else if(!gamepad1.x && x1)
                x1 = false

            //autonomous cryptobox alignment
            /**
             * Explanation:
             *
             * if a has been pressed, attempt to auto-align the robot. a precursor is that the lift
             * may not be running at the same time.
             */
            if(gamepad1.a && !a1 && !liftRunning) {
                a1 = true
                driver_cryptoboxPlacement() //the method run to auto-position the robot.
            }else if(!gamepad1.a && a1)
                a1 = false

            //lift_backup logic
            /**
             * Explanation:
             *
             * Sometimes the lift gets out of position- in these cases the auto positioning will not function correctly.
             * here the lift can be manually positioned (given that the lift is not attempting to auto-position itself).
             *
             */
            if (!liftRunning) {
                if (gamepad1.left_bumper && !gamepad1.right_bumper)
                    thru.runLift_BACKUP(GlyphThroughput.LiftRun.UP, true)
                else if (gamepad1.right_bumper && !gamepad1.left_bumper)
                    thru.runLift_BACKUP(GlyphThroughput.LiftRun.DOWN, true)
                else thru.runLift_BACKUP(GlyphThroughput.LiftRun.DOWN, false)
            }
            //gamepad 2

            //relic extender running logic
            /**
             * Explanation:
             *
             * if the x or b buttons are pressed (respectively) then move the relic grabber in or out from the robot
             * the default movement direction of the vex motors is 0 (turned off)
             */
            if(gamepad2.x)
                relic_extender.runMotors(RelicExtender.Direction.OUT)
            else if(gamepad2.b)
                relic_extender.runMotors(RelicExtender.Direction.IN)
            else relic_extender.runMotors(RelicExtender.Direction.OFF)

            /**
             * Explanation:
             *
             * these controls are used each to change the positioning of the grabber and handle of the relic system.
             * dpad down is used to set the grabber (or claw) between being closed or open (defaulting to close)
             * dpad left and right switch out the positioning of the handle between all the way up (to lift the relic
             * over the side of the field) at grabbing level (for picking up a relic) and down (the default position)
             */
            if(gamepad2.dpad_down) relic_extender.setGrabberPosition(RelicExtender.GrabberPosition.OPEN)
            else relic_extender.setGrabberPosition(RelicExtender.GrabberPosition.CLOSED)

            if(gamepad2.dpad_right) relic_extender.setHandlePosition(RelicExtender.HandlePosition.UP)
            else if(gamepad2.dpad_left) relic_extender.setHandlePosition(RelicExtender.HandlePosition.GRAB)
            else relic_extender.setHandlePosition(RelicExtender.HandlePosition.DOWN)

            //actuate robot positions logic
            /**
             * Explanation:
             *
             * if the robot has failed to run autnomous properly and is still stuck in its 18x18 position, there
             * needs to be a manual way to expand the robot in tele-op.
             */
            if(gamepad2.left_stick_button && !left_stick2){
                thru.expand_throughtake()
                left_stick2 = true
            }else if(!gamepad2.left_stick_button && left_stick2)
                left_stick2 = false

            /**
             * calculate encoder position/time and averages over the past 100 runs for data collection
             * (this is primarily used for testing in autonomous)
             */
            if(LAST_DELTAS.size > 100)LAST_DELTAS.removeAt(0)

            val delta_pos = Math.abs((thru.flyRunL.currentPosition - LAST_ENCODER_POSITION) / (TIMER.milliseconds() - LAST_TIME))

            telemetry.addData("delta-flywheels", delta_pos)
            LAST_TIME = TIMER.milliseconds()
            LAST_ENCODER_POSITION = thru.flyRunL.currentPosition
            LAST_DELTAS.add(delta_pos)

            var avg_runs = 0.0
            for(x in LAST_DELTAS)
                avg_runs += x
            avg_runs = avg_runs/LAST_DELTAS.size
            telemetry.addData("AVGS", avg_runs) //print out the averages of the delta positioning
        }


    /**
     * Explanation:
     *
     * attempt to align the robot up to the cryptobox given set sensor positioning and alignment values.
     */
    fun driver_cryptoboxPlacement() {
        val us = ultrasonic_set.getSensor(US_sensor_side) //grab the ultrasonic sensor given the selected side
        val part = CRYPTOBOX_POSITION_DATA.getClosestPosition(crypto_placement, us.cmUltrasonic()) //select the closest position of the cryptobox based off of the curret robot position
        val final_position = CRYPTOBOX_POSITION_DATA.getPosition(crypto_placement, part) //get the end positioning

        var delta = Math.abs(final_position - us.cmUltrasonic()) //grab the initial delta of the positioning

        while(delta > 2 && !check_exit_driverPlacement()) { //run the placement loop- includes check to disabling loop
            delta = Math.abs(final_position - us.cmUltrasonic()) //update the position delta
            DRIVETRAIN.move( //move the drivetrain giving needed values to move the robot properly.
                    if(delta > 0) 0.7 else -0.7 * (if(US_sensor_side == UltrasonicSensorSet.UltrasonicSide.RIGHT)1 else -1),
                    0.0,
                    0.0,
                    0.6
            )
        }
        DRIVETRAIN.stop() //stop the drivetrain when the robot has finished moving.
    }

    /**------------Used for auto alignment
     * Explanation:
     *
     * when running the autonomous positioning, this passthrough checks whether or not the driver requests
     * stopping the alignment.
     */
    fun check_exit_driverPlacement() : Boolean {
        if(gamepad1.a && !a1) {
            a1 = true
            return true
        }else if(!gamepad1.a && a1)
            a1 = false
        return false
    }
}