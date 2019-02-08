package org.firstinspires.ftc.teamcode.common.controller

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.internal.ui.GamepadUser

//@Autonomous(name = "BUTTON TESTER")
class ButtonTester : LinearOpMode() {
    override fun runOpMode() {

        val tel: (String, Any) -> Telemetry.Item = telemetry::addData
        val updateTel: () -> Boolean = telemetry::update

        val pad1 = SmidaGamepad(gamepad1, this)

        val button: (SmidaGamepad.GamePadButton) -> Button = pad1::getButton

        telemetry.captionValueSeparator = ""

        val iteratorButtons = SmidaGamepad.GamePadButton.values().iterator()
        var currentButton: SmidaGamepad.GamePadButton = iteratorButtons.next()
        var currentButtonCl = button(currentButton)

        var taskComplete = false
        var task1 = false
        var task2 = false
        var clicks = 0
        while (!isStopRequested) {
            pad1.handleUpdate()
            tel("Current button: ${currentButton.name}", "")
            tel("Press status: ${currentButtonCl.isPressed}", "")
            if (currentButton.hasValue) tel("Button value: ${currentButtonCl.buttonValue}", "")
            if (currentButton.isJoystick) tel("Joystick values ${currentButtonCl.joystickValues.first}x ${currentButtonCl.joystickValues.second}y", "")

            if (task1 && task2) taskComplete = true

            if (iteratorButtons.hasNext() && taskComplete) {
                currentButton = iteratorButtons.next()
                currentButtonCl = button(currentButton)
                taskComplete = false
                task1 = false
                task2 = false
            } else if (!iteratorButtons.hasNext() && taskComplete) break

            if (!task1 && !currentButtonCl.isPressed)
                tel("Please press the button.", "")
            else if (!task1 && currentButtonCl.isPressed)
                task1 = true

            if (!task2 && task1 && clicks < 3) {
                if (currentButtonCl.holdingTimeCheck(0.5, time)) clicks++

                tel("Please hold down the button for 3 clicks ($clicks/3)", "")
            } else if (!task2 && clicks >= 3) {
                task2 = true
                clicks = 0
            }

            updateTel()
        }
     }
}