package org.firstinspires.ftc.teamcode.common.controller

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import java.lang.reflect.Field


class Button(private val gamepad : SmidaGamepad, private val buttonType : SmidaGamepad.GamePadButton, private val buttonFields : Array<Field?>) {

    var wasPressed: Boolean = false
        private set
    var isPressed: Boolean = false
        private set
    var buttonValue: Double = 0.0
        private set
    var joystickValues: Pair<Double, Double> = Pair(0.0, 0.0) //x y
        private set
    private var initialPressTime: Double = -1.0
    private var lastPressCheck: Double = -1.0

    fun doUpdate() {
        if (buttonType.isPressable) {
            wasPressed = isPressed
            isPressed = buttonFields[0]!!.getBoolean(gamepad.gamepad)
            if (!wasPressed && isPressed) initialPressTime = gamepad.opmode.time
            if (!isPressed && wasPressed) initialPressTime = -1.0

        }
        if (buttonType.hasValue) {
            buttonValue = buttonFields[if(!buttonType.isPressable)0 else 1]!!.getDouble(gamepad.gamepad)
            if (!buttonType.isPressable && buttonType.hasValue) {
                wasPressed = isPressed
                isPressed = buttonValue > 0.0
                if (!wasPressed && isPressed) initialPressTime = gamepad.opmode.time
                if (!isPressed && wasPressed) initialPressTime = -1.0
            }
        }
        if (buttonType.isJoystick) joystickValues = Pair(-buttonFields[1]!!.getDouble(gamepad.gamepad), buttonFields[2]!!.getDouble(gamepad.gamepad))
    }

    fun holdingTimeCheck(delta: Double, time: Double): Boolean {
        if (initialPressTime > -1.0) {
            val ret = (lastPressCheck == -1.0 || time - lastPressCheck > delta)
            if (ret) lastPressCheck = time
            return ret
        }
        return false
    }

    fun isIndividualActionButtonPress() : Boolean = return !wasPressed && isPressed


    fun onlyThisIsPressing(other : GamePadButton) = this.isPressed && !gamepad.getButton(other).isPressed

}
