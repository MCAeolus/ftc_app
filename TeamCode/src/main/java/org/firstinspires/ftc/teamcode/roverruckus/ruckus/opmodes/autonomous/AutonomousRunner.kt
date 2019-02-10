package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import android.content.Context
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new.TimeStampedData
import java.io.File

@Autonomous(name = "Ruckus Runner")
class AutonomousRunner : AutonomousBase(true) {

    companion object {
        const val operatorName = "__OPERATION"

        fun isOperationDirectory(baseDir : File, dirPath : String) : Boolean{
            val f = File(baseDir, dirPath)
            if(f.exists()) return f.list().contains(operatorName)
            return false
        }
    }

    private val defaultDir = "${TimeStampedData.REPLAY_DIRECTORY_PREFIX}AutonomousRunner"

    override fun runOpMode() {

        val baseDir = hardwareMap.appContext.filesDir
        var currentDir = defaultDir

        var selectorLoc = 0

        var up_DPADpressed = false
        var down_DPADpressed = false
        var a_pressed = false
        var b_pressed = false
        var lbumper_pressed = false

        var buttonPressed = false

        var operationDir : File? = null

        if(!baseDir.list().contains(defaultDir)) File(baseDir, defaultDir).mkdirs()

        while(!isStarted) {

            val dir = File(baseDir, currentDir)

            val dirList = dir.list { _ : File, s : String -> s.startsWith(TimeStampedData.REPLAY_DIRECTORY_PREFIX) }

            if(gamepad1.dpad_up && !up_DPADpressed && !buttonPressed) {
                buttonPressed = true
                up_DPADpressed = true
                if(selectorLoc > 0) selectorLoc--
            }else if(!gamepad1.dpad_up && up_DPADpressed) {
                buttonPressed = false
                up_DPADpressed = false
            }

            if(gamepad1.dpad_down && !down_DPADpressed && !buttonPressed) {
                buttonPressed = true
                down_DPADpressed = true
                if(selectorLoc < dirList.lastIndex) selectorLoc++
            }else if(!gamepad1.dpad_down && down_DPADpressed) {
                buttonPressed = false
                down_DPADpressed = false
            }

            if(gamepad1.a && !a_pressed && !buttonPressed) {
                buttonPressed = true
                a_pressed = true

                if(isOperationDirectory(baseDir, currentDir + "/" + dirList[selectorLoc]))
                    operationDir = File(dir, dirList[selectorLoc])
                else {
                    currentDir += "/" + dirList[selectorLoc]
                }
            }else if(!gamepad1.a && a_pressed) {
                buttonPressed = false
                a_pressed = false
            }

            if(gamepad1.b && !b_pressed && !buttonPressed) {
                buttonPressed = true
                b_pressed = true

                if(operationDir != null) operationDir = null
                else if(currentDir != defaultDir) {
                    val thLindex = currentDir.lastIndexOf('/')
                    currentDir = currentDir.substring(0, if(thLindex < 0) defaultDir.length else thLindex)
                }
            }else if(!gamepad1.b && b_pressed) {
                buttonPressed = false
                b_pressed = false
            }

            if(gamepad1.left_bumper && !lbumper_pressed && !buttonPressed) {
                buttonPressed = true
                lbumper_pressed = true

                val selDir = currentDir + "/" + dirList[selectorLoc]

                if(isOperationDirectory(baseDir, selDir)) {
                     File(dir, dirList[selectorLoc]).listFiles().filter { it.name == operatorName }.forEach{ it.delete() }
                }else {
                    makeFile(operatorName, selDir)
                    makeFile("${TimeStampedData.REPLAY_PREFIX}${SamplePosition.LEFT.id}", selDir)
                    makeFile("${TimeStampedData.REPLAY_PREFIX}${SamplePosition.CENTER.id}", selDir)
                    makeFile("${TimeStampedData.REPLAY_PREFIX}${SamplePosition.RIGHT.id}", selDir)
                }
            }else if(!gamepad1.left_bumper && lbumper_pressed) {
                buttonPressed = false
                lbumper_pressed = false
            }

            if(operationDir != null) {
                send("Current selected operation mode: ${operationDir.path}")
                send("Ready to run.")
            }else {

                send("Please select the proper operation mode.")
                send("Use ↕ DPAD buttons to move selector, use 'A' to make the selection.")
                send("Use 'B' button to undo an operation selection or go back a directory.")
                send("Use 'left bumper' to designate or un-designate a directory as an operation directory.")
                send("")

                for (i in 0 until dirList.size)
                    send("${if (selectorLoc == i) ">>>" else "   "}${dirList[i]}", if (isOperationDirectory(baseDir, currentDir + "/" + dirList[i])) ": OPERATION MODE" else "->")
            }
            telemetry.update()
        }

        if(operationDir == null) requestOpModeStop()
        else {

            TFOD.activate()
            var sample_position = SamplePosition.N_A
            val timer_it = ElapsedTime()
            timer_it.reset()

            while (opModeIsActive() && timer_it.seconds() < 5) {
                sample_position = findSample_THREE(TFOD.updatedRecognitions)
                if (sample_position != SamplePosition.N_A && timer_it.seconds() > 1) break
            }

            val USE_RECORD = TimeStampedData.DataStream(operationDir.canonicalPath + "/${TimeStampedData.REPLAY_PREFIX}" + sample_position.id, hardwareMap)
            USE_RECORD.load()
            USE_RECORD.prepare()

            val start_time = time

            while (opModeIsActive()) {
                val elapsed = time - start_time
                val data = USE_RECORD.pointsUntil(elapsed)
                var targetRotation = 0.0
                data.first.forEach { iv ->
                    iv.bytes.forEach {
                        val device = hardwareMap.get(it.name)

                        when (device) {
                            is DcMotor -> {
                                device.mode = DcMotor.RunMode.RUN_USING_ENCODER
                                device.power = it.data[0]
                                device.targetPosition = it.data[1].toInt()
                            }
                            is Servo -> device.position = it.data[0]

                            is CRServo -> device.power = it.data[0]

                            is BNO055IMU -> targetRotation = it.data[0]
                        }
                    }
                }
                if (data.second) break
            }
            DRIVETRAIN.stop()
        }
    }

    private fun makeFile(file : String, dir : String = "") {
        val f = File(opMode().hardwareMap.appContext.filesDir, "$dir/$file")
        if(!f.exists()) f.createNewFile()
    }

    private fun send(h : String, p : String = "", separator : String = "", update : Boolean = false, refresh : Boolean = true) {
        telemetry.captionValueSeparator = separator
        telemetry.isAutoClear = refresh
        telemetry.addData(h, p)
        if(update)telemetry.update()
    }
}