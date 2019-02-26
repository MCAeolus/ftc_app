package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.controller.Button
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.*
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.javaField

@TeleOp(name="Ruckus")
open class RuckusOpMode : Robot(MecanumDriveTrain(), mapOf("Outtake" to OuttakeMachine(), "Intake" to IntakeMachine(), "Lift" to LiftMachine())){

    lateinit var OUTTAKE : OuttakeMachine
    lateinit var INTAKE : IntakeMachine
    lateinit var LIFT : LiftMachine

    private var toggleRotationSlow = false
    private var toggleMovementSlow = false

    private var wasIntakeUp = false
    private var wasIntakeLocked = false

    lateinit var pad1 : SmidaGamepad
    lateinit var pad2 : SmidaGamepad

    private val button = SmidaGamepad.getReflectButton()

    private val layout = mapOf(
        "Slow Toggle" to (SmidaGamepad.GamePadButton.LEFT_STICK to ::pad1.javaField),
        "Rotation Toggle" to (SmidaGamepad.GamePadButton.RIGHT_STICK to ::pad1.javaField),
        "Cardinal Drive" to (SmidaGamepad.GamePadButton.LEFT_STICK to ::pad1.javaField),
        "Rotational Drive" to (SmidaGamepad.GamePadButton.RIGHT_STICK to ::pad1.javaField),

        "Move Intake Out" to (SmidaGamepad.GamePadButton.LEFT_TRIGGER to ::pad1.javaField),
        "Move Intake In" to (SmidaGamepad.GamePadButton.RIGHT_TRIGGER to ::pad1.javaField),

        "Outtake Dumper" to (SmidaGamepad.GamePadButton.B to ::pad2.javaField),

        "Outtake Slide Up" to (SmidaGamepad.GamePadButton.LEFT_BUMPER to ::pad1.javaField),
        "Outtake Slide Down" to (SmidaGamepad.GamePadButton.RIGHT_BUMPER to ::pad1.javaField),

        "Intake Arm Toggle" to (SmidaGamepad.GamePadButton.X to ::pad2.javaField),

        "Run Intake In" to (SmidaGamepad.GamePadButton.LEFT_BUMPER to ::pad2.javaField),
        "Run Intake Out" to (SmidaGamepad.GamePadButton.RIGHT_BUMPER to ::pad2.javaField),

        "Lift Move Up" to (SmidaGamepad.GamePadButton.LEFT_TRIGGER to ::pad2.javaField),
        "Lift Move Down" to (SmidaGamepad.GamePadButton.RIGHT_TRIGGER to ::pad2.javaField)
    )

    override fun start() {
        OUTTAKE = COMPONENTS["Outtake"] as OuttakeMachine
        INTAKE = COMPONENTS["Intake"] as IntakeMachine
        LIFT = COMPONENTS["Lift"] as LiftMachine

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    private fun getButton(configOption : String) : Button {
        val config = layout[configOption] as Pair<SmidaGamepad.GamePadButton, Field?>
        return button(config.second!!.get(this) as SmidaGamepad, config.first)
    }

    override fun loop() {
        pad1.handleUpdate()
        pad2.handleUpdate()

        if(getButton("Slow Toggle").isIndividualActionButtonPress()) {
          toggleMovementSlow = !toggleMovementSlow
          toggleRotationSlow = toggleMovementSlow
        }

        if(getButton("Rotation Toggle").isIndividualActionButtonPress())
          toggleRotationSlow = !toggleRotationSlow

        val lJoy = getButton("Cardinal Drive")
        val rJoy = getButton("Rotational Drive")

        DRIVETRAIN.move(lJoy.joystickValues.first,
            lJoy.joystickValues.second,
            rJoy.joystickValues.first * if(toggleRotationSlow || toggleMovementSlow) 0.5 else 1.0,
            if(toggleMovementSlow) 0.5 else 1.0)

        when {
            getButton("Move Intake Out").onlyThisIsPressing(SmidaGamepad.GamePadButton.RIGHT_TRIGGER) ->
                INTAKE.runSlides(IntakeMachine.ArmDirection.OUT)
            getButton("Move Intake In").onlyThisIsPressing(SmidaGamepad.GamePadButton.LEFT_TRIGGER) ->
                INTAKE.runSlides(IntakeMachine.ArmDirection.IN)
            else -> INTAKE.runSlides(IntakeMachine.ArmDirection.OFF)
        }

        if(getButton("Outtake Dumper").isPressed)
            OUTTAKE.setDumpPosition(OuttakeMachine.DumpPosition.DUMP)
        else OUTTAKE.setDumpPosition(OuttakeMachine.DumpPosition.RESET)

        if(getButton("Outtake Slide Up").isPressed || getButton("Outtake Slide Down").isPressed) {
            if(!wasIntakeLocked) wasIntakeLocked = true
            INTAKE.armPosition(IntakeMachine.ArmPosition.DOWN)
        } else {
            if(wasIntakeLocked && wasIntakeUp) {
                INTAKE.armPosition(IntakeMachine.ArmPosition.UP)
                wasIntakeLocked = false
            }
            if (getButton("Intake Arm Toggle").isIndividualActionButtonPress()) {
                INTAKE.toggleArmPosition()
                wasIntakeUp = INTAKE.armPosition == IntakeMachine.ArmPosition.UP
            }
        }

        if(getButton("Run Intake In").isPressed) INTAKE.runIntake(IntakeMachine.IntakeDirection.INTAKE)
        else if(getButton("Run Intake Out").isPressed) INTAKE.runIntake(IntakeMachine.IntakeDirection.OUTTAKE)
        else INTAKE.runIntake(IntakeMachine.IntakeDirection.OFF)

        when {
            getButton("Outtake Slide Up").isPressed -> OUTTAKE.runSlides(1.0f);
            getButton("Outtake Slide Down").isPressed -> OUTTAKE.runSlides(-1.0f);
            else -> OUTTAKE.runSlides(0.0f)
        }

        when {
            getButton("Lift Move Up").isPressed -> LIFT.runLift(LiftMachine.LiftDirection.UP)
            getButton("Lift Move Down").isPressed -> LIFT.runLift(LiftMachine.LiftDirection.DOWN)
            else -> LIFT.runLift(LiftMachine.LiftDirection.OFF)
        }

        if(button(pad2, SmidaGamepad.GamePadButton.A).isIndividualActionButtonPress()) {
            LIFT.liftPosition(LiftMachine.LiftPosition.DOWN)
        }

        telemetry.addData("RUCKUS", "opmode is running for $time seconds")
        telemetry.addData("STATS", "movement toggle $toggleMovementSlow, rotation toggle $toggleRotationSlow")
    }
}
