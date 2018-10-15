package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.robotmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.*

/**
 * Created by Nathan.Smith.19 on 10/19/2017.
 */

@TeleOp(name="ScorpionBot")
class MecanumRobot : LinearRobot(MecanumDrive(arrayOf("FL", "FR", "BL", "BR")), //drive train
                           mapOf(Pair("GlyphThroughput",
                                      GlyphIntake(arrayOf("CON_L", "CON_R", "L", "L2"),
                                                  arrayOf("DO_L", "DO_R", "DRP", "KCK")) as IMachine)
                            ,
                                    Pair("Slapper", SlapperTwo() as IMachine)
                            ,
                                    Pair("IMU", IMU() as IMachine)
                            /**,
                                    Pair("Relic Extender", RelicExtenderMachine() as IMachine)**/)) {

    enum class GlyphPosition {
        IN, OUT
    }

    val POWER_MOD_CONSTANT = 0.5

    //CONTROLLER VALUES
    var DRIVETRAIN_LX = 0.0
    var DRIVETRAIN_LY = 0.0
    var DRIVETRAIN_RX = 0.0
    var POWER_MOD = false
    var POWER_MOD_PRESSED = false

    var LIFT_LOC = 0

    var DOOR_STATE = GlyphIntake.DoorState.OPEN

    var leftb2_pressed = false
    var rightb2_pressed = false

    var GLYPH_RUN_INTENT = GlyphPosition.OUT
    var GLYPH_ISRUNNING = false
    var GLYPH_INIT = true

    var GLIFT_DIR = true //true -> up
    var GLIFT_RUN = false

    var lTrig1_wasPressed = false
    var rTrig1_wasPressed = false

    var TIMEOUT = 0
    val TIMEOUT_MAX = 3

    var DOOR_RATIO = 0F

    var IS_LEVELING = false
    var a1_wasPressed = false

    var LEVELING_STARTED = false

    val GLYPH_TIMER = ElapsedTime()

    override fun runOpMode() {
        super.runOpMode()

        val GLYPH_INTAKE = COMPONENTS["GlyphThroughput"] as GlyphIntake
        GLYPH_INTAKE.setDoors(GlyphIntake.DoorState.CLOSED)
        GLYPH_INTAKE.drop(false)


        //val FISTER = COMPONENTS["fister"] as FisterMachine

        val IMU = COMPONENTS["IMU"] as IMU

        val SLAP = COMPONENTS["Slapper"] as SlapperTwo
        SLAP.resting()

        //val RelicExtender = COMPONENTS["Relic Extender"]  as RelicExtenderMachine

        waitForStart()

        //drive(1.0, 0.0, 0.0, 1.0) MOVES LEFT

        while (pass_through()) { //HEARTBEAT

            //EXECUTION

            //levelSelf(IMU)

            val orientation = IMU.IMU.angularOrientation.toAxesOrder(AxesOrder.XYZ)

            telemetry.addData("$GLYPH_ISRUNNING", "$GLYPH_RUN_INTENT")
            controlGlyphIntake(GLYPH_INTAKE)

            telemetry.addData("${GLYPH_INTAKE.GLYPH_POSITION}", "$GLYPH_INIT")
            telemetry.addData(IMU.IMU.angularOrientation.toString(), "")
            telemetry.addData("X${orientation.firstAngle} Y${orientation.secondAngle} Z${orientation.thirdAngle}","")

            DRIVETRAIN.move(
                    -DRIVETRAIN_LX,
                    DRIVETRAIN_LY,
                    -DRIVETRAIN_RX/1.1,
                    if (POWER_MOD) POWER_MOD_CONSTANT else 1.0
            )


            telemetry.update()
        }

        //EXIT
        linear_stop()
    }

    fun levelSelf(IMU : IMU){
        if(IS_LEVELING && !LEVELING_STARTED) {

            val timer = ElapsedTime()

            timer.reset()

            var x = 0.0
            var y = 0.0
            while(pass_through() && IS_LEVELING) {

                val orientation = IMU.IMU.angularOrientation.toAxesOrder(AxesOrder.XYZ)

                val delta_x = orientation.firstAngle - IMU.initialOrientation.firstAngle
                val delta_y = orientation.secondAngle - IMU.initialOrientation.secondAngle
                val MIN_DIFF = 3 //const
                val DIV_CONST = 5.0

                if(Math.abs(delta_x) > MIN_DIFF)x = -delta_x/DIV_CONST
                else x = 0.0
                if(Math.abs(delta_y) > MIN_DIFF)y = delta_y/DIV_CONST
                else y = 0.0

                drive(x, y, 0.0, 0.7)
            }
            IS_LEVELING = false
        }else LEVELING_STARTED = false
    }

    fun drive(x : Double, y : Double, r : Double, p : Double){

        DRIVETRAIN.move(
                x,
                y,
                r,
                p
        )
    }

    fun getAngleFromOrientation(i : Int, aO : Orientation): Float {
        when(i){
            0 -> return aO.firstAngle
            1 -> return aO.secondAngle
            2 -> return aO.thirdAngle
            else -> return aO.firstAngle
        }
    }

    fun updateControllerValues(){
        if(TIMEOUT > 0)TIMEOUT--
        //DRIVETRAIN MOVEMENT
        DRIVETRAIN_LX = gamepad1.left_stick_x.toDouble()
        DRIVETRAIN_LY = gamepad1.left_stick_y.toDouble()
        DRIVETRAIN_RX = gamepad1.right_stick_x.toDouble()

        //DRIVETRAIN POWER MODIFICATION
        if(gamepad1.left_bumper && !POWER_MOD_PRESSED) {
            POWER_MOD = !POWER_MOD
            POWER_MOD_PRESSED = true
        }else if(!gamepad1.left_bumper && POWER_MOD_PRESSED) POWER_MOD_PRESSED = false

        if(gamepad1.right_trigger == 0F && gamepad1.left_trigger == 0F)GLYPH_ISRUNNING = false

        if(lTrig1_wasPressed && gamepad1.left_trigger == 0F)lTrig1_wasPressed = false
        if(rTrig1_wasPressed && gamepad1.right_trigger == 0F)rTrig1_wasPressed = false

        if(gamepad1.left_trigger > 0F && !lTrig1_wasPressed && !rTrig1_wasPressed) {
            GLYPH_RUN_INTENT = GlyphPosition.IN
            GLYPH_ISRUNNING = true
            lTrig1_wasPressed = true
        }
        if(gamepad1.right_trigger > 0F && !rTrig1_wasPressed && !lTrig1_wasPressed){
            GLYPH_RUN_INTENT = GlyphPosition.OUT
            GLYPH_ISRUNNING = true
            rTrig1_wasPressed = true
        }

        //GAMEPAD2

        if(gamepad2.left_bumper && !leftb2_pressed){
            GLIFT_DIR = true
            GLIFT_RUN = true
        }else if(leftb2_pressed)leftb2_pressed = false

        if(gamepad2.right_bumper && !rightb2_pressed){
            GLIFT_DIR = false
            GLIFT_RUN = true
        }else if(rightb2_pressed)rightb2_pressed = false

        if(!gamepad2.left_bumper && !gamepad2.right_bumper)GLIFT_RUN = false

    }

    fun controlGlyphIntake(glI : GlyphIntake) {
        runGlyphBased(GLYPH_RUN_INTENT, glI)
        glI.lift(GLIFT_DIR, GLIFT_RUN, GLYPH_ISRUNNING)
    }

    fun runGlyphBased(glP : GlyphPosition, glI : GlyphIntake){
        if(GLYPH_ISRUNNING && (componentsChanged(glI) || GLYPH_INIT)) {
            GLYPH_INIT = false
            glI.GLYPH_POSITION = GLYPH_RUN_INTENT
            when (glP) {
                GlyphPosition.OUT -> {
                    glI.drop(false)
                    glI.kick(true)
                    glI.setDoors(GlyphIntake.DoorState.PARTIAL)
                    glI.runConveyors(GlyphIntake.DoorState.CLOSED, true)
                    GLYPH_TIMER.reset()
                    while (GLYPH_TIMER.time() < 0.2 && opModeIsActive() && !componentsChanged(glI)) {
                    }
                    glI.kick(false)
                }
                GlyphPosition.IN -> {
                    glI.runConveyors(GlyphIntake.DoorState.OPEN, true)
                    glI.setDoors(GlyphIntake.DoorState.PARTIAL)
                    glI.drop(true)
                }
            }
        }else if(!GLYPH_ISRUNNING){
            GLYPH_INIT = true
            if(!GLIFT_RUN)glI.setDoors(GlyphIntake.DoorState.OPEN)
            glI.runConveyors(GlyphIntake.DoorState.OPEN, false)
            glI.kick(false)
            glI.drop(false)
        }
    }

    fun pass_through(): Boolean {
        updateControllerValues()
        return opModeIsActive()
    }

    fun componentsChanged(glI : GlyphIntake) = (glI.GLYPH_POSITION != GLYPH_RUN_INTENT)
}