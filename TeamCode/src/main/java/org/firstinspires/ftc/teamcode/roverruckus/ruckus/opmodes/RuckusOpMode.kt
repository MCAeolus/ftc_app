package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
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

    private var gamepad1_lStickWasPressed = false
    private var gamepad1_rStickWasPressed = false

    override fun start() {
        LINEAR_SLIDES = COMPONENTS["LinearSlide"] as LinearSlideMachine
        INTAKE = COMPONENTS["Intake"] as IntakeMachine
    }

    override fun loop() {
        if(gamepad1.left_stick_button && !gamepad1_lStickWasPressed) {
            gamepad1_lStickWasPressed = true
            toggleMovementSlow = !toggleMovementSlow
            toggleRotationSlow = toggleMovementSlow
        }else if(!gamepad1.left_stick_button && gamepad1_lStickWasPressed) gamepad1_lStickWasPressed = false

        if(gamepad1.right_stick_button && !gamepad1_rStickWasPressed && !toggleMovementSlow) {
            gamepad1_rStickWasPressed = true
            toggleRotationSlow = !toggleRotationSlow
        }else if(!gamepad1.right_stick_button && gamepad1_rStickWasPressed) gamepad1_rStickWasPressed = false

        DRIVETRAIN.move(-gamepad1.left_stick_x.toDouble(), gamepad1.left_stick_y.toDouble(), -gamepad1.right_stick_x.toDouble() * if(toggleRotationSlow || toggleMovementSlow) 0.5 else 1.0, if(toggleMovementSlow) 0.5 else 1.0)

        if(gamepad1.left_trigger > 0 && !(gamepad1.right_trigger > 0)) LINEAR_SLIDES.runSlides(gamepad1.left_trigger)
        else if(gamepad1.right_trigger > 0 && !(gamepad1.left_trigger > 0)) LINEAR_SLIDES.runSlides(-gamepad1.right_trigger)
        else LINEAR_SLIDES.runSlides(0F)

        if(gamepad2.left_bumper) INTAKE.runIntake(IntakeMachine.IntakeDirection.INTAKE)
        else if(gamepad2.left_trigger > 0.0) INTAKE.runIntake(IntakeMachine.IntakeDirection.OUTTAKE)
        else INTAKE.runIntake(IntakeMachine.IntakeDirection.OFF)

        if(gamepad2.right_bumper) LIFT.runLift(LiftMachine.LiftDirection.UP)
        else if(gamepad2.right_trigger > 0) LIFT.runLift(LiftMachine.LiftDirection.DOWN)
        else LIFT.runLift(LiftMachine.LiftDirection.OFF)

        telemetry.addData("RUCKUS", "opmode is running ${time}")
        telemetry.addData("STATS", "movement toggle $toggleMovementSlow, rotation toggle $toggleRotationSlow")
    }
}