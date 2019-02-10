package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.IntakeMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.LiftMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.LinearSlideMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
@TeleOp(name="Ruckus")
open class RuckusOpMode : Robot(MecanumDriveTrain(), mapOf("LinearSlide" to LinearSlideMachine(), "Intake" to IntakeMachine(), "Lift" to LiftMachine())){

    lateinit var LINEAR_SLIDES : LinearSlideMachine
    lateinit var INTAKE : IntakeMachine
    lateinit var LIFT : LiftMachine

    private var toggleRotationSlow = false
    private var toggleMovementSlow = false

    lateinit var pad1 : SmidaGamepad
    lateinit var pad2 : SmidaGamepad

    private val button = SmidaGamepad.getReflectButton()

    override fun start() {
        LINEAR_SLIDES = COMPONENTS["LinearSlide"] as LinearSlideMachine
        INTAKE = COMPONENTS["Intake"] as IntakeMachine
        LIFT = COMPONENTS["Lift"] as LiftMachine

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    override fun loop() {
        pad1.handleUpdate()
        pad2.handleUpdate()

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK).isIndividualActionButtonPress()) {
          toggleMovementSlow = !toggleMovementSlow
          toggleRotationSlow = toggleMovementSlow
        }
        if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_STICK).isIndividualActionButtonPress())
          toggleRotationSlow = !toggleRotationSlow

        /**if(gamepad1.left_stick_button && !gamepad1_lStickWasPressed) {
            gamepad1_lStickWasPressed = true
            toggleMovementSlow = !toggleMovementSlow
            toggleRotationSlow = toggleMovementSlow
        }else if(!gamepad1.left_stick_button && gamepad1_lStickWasPressed) gamepad1_lStickWasPressed = false


        if(gamepad1.right_stick_button && !gamepad1_rStickWasPressed && !toggleMovementSlow) {
            gamepad1_rStickWasPressed = true
            toggleRotationSlow = !toggleRotationSlow
        }else if(!gamepad1.right_stick_button && gamepad1_rStickWasPressed) gamepad1_rStickWasPressed = false
        **/

        val lJoy = button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK)
        val rJoy = button(pad1, SmidaGamepad.GamePadButton.RIGHT_STICK)

        DRIVETRAIN.move(lJoy.joystickValues.first,
            lJoy.joystickValues.second,
            rJoy.joystickValues.first * if(toggleRotationSlow || toggleMovementSlow) 0.5 else 1.0,
            if(toggleMovementSlow) 0.5 else 1.0)

        when {
            button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).onlyThisIsPressing(SmidaGamepad.GamePadButton.RIGHT_TRIGGER) ->
                LINEAR_SLIDES.runSlides(pad1.lastCheckedButton.buttonValue.toFloat())
            button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).onlyThisIsPressing(SmidaGamepad.GamePadButton.LEFT_TRIGGER) ->
                LINEAR_SLIDES.runSlides(-pad1.lastCheckedButton.buttonValue.toFloat())
            else -> LINEAR_SLIDES.runSlides(0F)
        }

        /**
        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).onlyThisIsPressing(SmidaGamepad.GamePadButton.RIGHT_TRIGGER))
            LINEAR_SLIDES.runSlides(button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).buttonValue.toFloat())
        if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).onlyThisIsPressing(SmidaGamepad.GamePadButton.LEFT_TRIGGER))
            LINEAR_SLIDES.runSlides(-button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).buttonValue.toFloat())
        else LINEAR_SLIDES.runSlides(0F)

        if(gamepad1.left_trigger > 0 && !(gamepad1.right_trigger > 0)) LINEAR_SLIDES.runSlides(gamepad1.left_trigger)
        else if(gamepad1.right_trigger > 0 && !(gamepad1.left_trigger > 0)) LINEAR_SLIDES.runSlides(-gamepad1.right_trigger)
        else LINEAR_SLIDES.runSlides(0F)
        **/

        when {
            button(pad2, SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed -> LIFT.runLift(LiftMachine.LiftDirection.UP)
            button(pad2, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed -> LIFT.runLift(LiftMachine.LiftDirection.DOWN)
            else -> LIFT.runLift(LiftMachine.LiftDirection.OFF)
        }

        telemetry.addData("RUCKUS", "opmode is running for $time seconds")
        telemetry.addData("STATS", "movement toggle $toggleMovementSlow, rotation toggle $toggleRotationSlow")
    }
}
