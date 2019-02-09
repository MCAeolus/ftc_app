package org.firstinspires.ftc.teamcode.common.controller

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.internal.ui.GamepadUser
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import java.lang.reflect.Field
import kotlin.reflect.jvm.javaField

class SmidaGamepad(val gamepad : Gamepad, val opmode : OpMode) {

    enum class GamePadButton(val isPressable : Boolean = true, val hasValue : Boolean = false, val isJoystick : Boolean = false, val fields : Array<Field?>) {
        PAD_UP(fields = arrayOf(Gamepad::dpad_up.javaField)), PAD_DOWN(fields = arrayOf(Gamepad::dpad_down.javaField)),
        PAD_LEFT(fields = arrayOf(Gamepad::dpad_left.javaField)), PAD_RIGHT(fields = arrayOf(Gamepad::dpad_right.javaField)),

        X(fields = arrayOf(Gamepad::x.javaField)), Y(fields = arrayOf(Gamepad::y.javaField)),
        B(fields = arrayOf(Gamepad::b.javaField)), A(fields = arrayOf(Gamepad::a.javaField)),

        LEFT_STICK(isJoystick = true, fields = arrayOf(Gamepad::left_stick_button.javaField, Gamepad::left_stick_x.javaField, Gamepad::left_stick_y.javaField)),
        RIGHT_STICK(isJoystick = true, fields = arrayOf(Gamepad::right_stick_button.javaField, Gamepad::right_stick_x.javaField, Gamepad::right_stick_y.javaField)),

        LEFT_BUMPER(fields = arrayOf(Gamepad::left_bumper.javaField)),
        RIGHT_BUMPER(fields = arrayOf(Gamepad::right_bumper.javaField)),

        LEFT_TRIGGER(isPressable = false, hasValue = true, fields = arrayOf(Gamepad::left_trigger.javaField)), RIGHT_TRIGGER(isPressable = false, hasValue = true, fields = arrayOf(Gamepad::right_trigger.javaField)),

        BACK(fields = arrayOf(Gamepad::back.javaField)), START(fields = arrayOf(Gamepad::start.javaField)),
        NONE(isPressable = false, fields = arrayOf())
    }

    private val buttonMap : MutableMap<GamePadButton, Button> = mutableMapOf()

    var lastCheckedButton : Button
    var isResting : Boolean = true
        private set

    init {
        GamePadButton.values().forEach { buttonMap[it] = Button(this, it, it.fields) }
        lastCheckedButton = buttonMap[GamePadButton.NONE]!!
    }

    static fun getReflectButton() : (SmidaGamepad, GamePadButton) -> Button = SmidaGamepad::getButton

    fun getReflectButton() : (GamePadButton) -> Button = ::getButton

    fun getButton(button : GamePadButton) : Button {
        lastCheckedButton = buttonMap[button]!!
        return lastCheckedButton
    }

    static fun getButton(gamepad : SmidaGamepad, button : GamePadButton) = gamepad.getButton(button)

    infix fun SmidaGamepad.button(button : GamePadButton) = getButton(button)

    fun handleUpdate() {
        buttonMap.forEach { it.value.doUpdate(this) }
        isResting = gamepad.atRest()
    }
}
