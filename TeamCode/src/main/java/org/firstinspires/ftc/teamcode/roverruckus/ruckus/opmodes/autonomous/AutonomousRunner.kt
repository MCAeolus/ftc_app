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

    val defaultDir = "${TimeStampedData.REPLAY_DIRECTORY_PREFIX}AutonomousRunner"
    val operatorName = "__OPERATION"

    override fun runOpMode() {

        var dir : File
        var currentDir = defaultDir

        var selectorLoc = 0

        var up_DPADpressed = false
        var down_DPADpressed = false
        var a_pressed = false
        var b_pressed = false
        var lbumper_pressed = false

        var buttonPressed = false

        var operationDir : File? = null

        while(!isStarted) {

            dir = hardwareMap.appContext.getDir(currentDir, Context.MODE_PRIVATE)

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

                if(isOperationDirectory(currentDir + "/" + dirList[selectorLoc]))
                    operationDir = hardwareMap.appContext.getDir(currentDir + "/" + dirList[selectorLoc], Context.MODE_PRIVATE)

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
                else if(currentDir != defaultDir) currentDir = dir.parent
            }else if(!gamepad1.b && b_pressed) {
                buttonPressed = false
                b_pressed = false
            }

            if(gamepad1.left_bumper && !lbumper_pressed && !buttonPressed) {
                buttonPressed = true
                lbumper_pressed = true

                val selDir = currentDir + "/" + dirList[selectorLoc]

                if(isOperationDirectory(selDir)) {
                    for(f in hardwareMap.appContext.getDir(selDir, Context.MODE_PRIVATE).listFiles())
                        f.delete()
                }else {
                    makeFile(operatorName, selDir)
                    makeFile(SamplePosition.LEFT.id, selDir)
                    makeFile(SamplePosition.CENTER.id, selDir)
                    makeFile(SamplePosition.RIGHT.id, selDir)
                }
            }else if(!gamepad1.left_bumper && lbumper_pressed) {
                buttonPressed = false
                lbumper_pressed = false
            }

            if(operationDir != null) {
                send("Current selected operation mode: ${operationDir.absolutePath}")
                send("")
            }

            send("Please select the proper operation mode for the Autonomous Runner.")
            send("Use UP and DOWN DPAD buttons to move selector, use 'A' to make the selection.")
            send("Use 'B' button to undo your operation selection or to go back a directory.")
            send("Use 'left bumper' to designate or un-designate a directory as an operation directory.")
            send("")

            for(i in 0 until dirList.size)
                send("${if(selectorLoc == i)">>>" else "   "}${dirList[i]}", if(isOperationDirectory(currentDir + "/" + dirList[i])) "OPERATION MODE" else "->")

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

            val USE_RECORD = TimeStampedData.DataStream(operationDir.absolutePath + "/" + sample_position.id, hardwareMap)
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
        val f = File("${opMode().hardwareMap.appContext.getDir(dir, Context.MODE_PRIVATE).absolutePath}/$file")
        f.createNewFile()
    }

    private fun send(h : String, p : String = "", separator : String = "", update : Boolean = false, refresh : Boolean = true) {
        telemetry.captionValueSeparator = separator
        telemetry.isAutoClear = refresh
        telemetry.addData(h, p)
        if(update)telemetry.update()
    }

    private fun isOperationDirectory(dirPath : String) = hardwareMap.appContext.getDir(dirPath, Context.MODE_PRIVATE).list().contains(operatorName)
}