package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.content.Context
import android.icu.text.AlphabeticIndex
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.io.File


@Autonomous(name="New Browser")
class ReplayBrowser : LinearOpMode() {

    private var defaultRefresh = false

    private enum class Partition {
        DIRECTORY, REPLAYS, HEADER
    }

    override fun runOpMode() {

        val selectorIcon = ">>>"

        var lastPartition = Partition.HEADER
        var currentPartition = Partition.HEADER
        var directoryPosition = ""
        var selectorLoc = 0

        var buttonPressed = false
        var leftDPAD_pressed = false
        var rightDPAD_pressed = false
        var upDPAD_pressed = false
        var downDPAD_pressed = false
        var a_pressed = false
        var b_pressed = false
        var x_pressed = false
        var lbumper_pressed = false

        var timeSincePressHeld : Double = -1.0
        val shiftDelta = 0.25

        var x_presses = 0

        while(!isStopRequested) {

            val dir = if(directoryPosition == "")hardwareMap.appContext.filesDir else hardwareMap.appContext.getDir(directoryPosition, Context.MODE_PRIVATE)

            val replayList = dir.list { _ : File, s : String -> s.startsWith(TimeStampedData.REPLAY_PREFIX) }
            val directoryList = dir.list { _ : File, s : String -> s.startsWith(TimeStampedData.REPLAY_DIRECTORY_PREFIX) }
            val replayExists = replayList.isNotEmpty()
            val directoryExists = directoryList.isNotEmpty()

            if(gamepad1.dpad_left) { //DPAD LEFT
                if(!leftDPAD_pressed && !buttonPressed) {
                    buttonPressed = true
                    leftDPAD_pressed = true
                    timeSincePressHeld = time
                }
                if(leftDPAD_pressed && (timeSincePressHeld == time || time - timeSincePressHeld > shiftDelta)) {
                    timeSincePressHeld = time
                    currentPartition = when(currentPartition) {
                        Partition.HEADER -> currentPartition
                        Partition.DIRECTORY -> Partition.HEADER
                        Partition.REPLAYS -> if(directoryExists) Partition.DIRECTORY else Partition.HEADER
                    }
                }
            }else if(!gamepad1.dpad_left && leftDPAD_pressed) {
                buttonPressed = false
                leftDPAD_pressed = false
                timeSincePressHeld = -1.0
            }
            if(gamepad1.dpad_right) { //DPAD RIGHT
                if(!rightDPAD_pressed && !buttonPressed) {
                    buttonPressed = true
                    rightDPAD_pressed = true
                    timeSincePressHeld = time
                }
                if(rightDPAD_pressed && (timeSincePressHeld == time || time - timeSincePressHeld > shiftDelta)) {
                    timeSincePressHeld = time
                    currentPartition = when(currentPartition) {
                        Partition.HEADER -> if(directoryExists) Partition.DIRECTORY else if(replayExists) Partition.REPLAYS else currentPartition
                        Partition.DIRECTORY -> if(replayExists) Partition.REPLAYS else Partition.DIRECTORY
                        Partition.REPLAYS -> currentPartition
                    }
                }
            }else if(!gamepad1.dpad_right && rightDPAD_pressed) {
                buttonPressed = false
                rightDPAD_pressed = false
                timeSincePressHeld = -1.0
            }
            if(gamepad1.dpad_up) { //DPAD UP
                if(currentPartition != Partition.HEADER) { //this shouldn't work in the header partition.

                    if (!upDPAD_pressed && !buttonPressed) {
                        buttonPressed = true
                        upDPAD_pressed = true
                        timeSincePressHeld = time
                    }
                    if (upDPAD_pressed && (timeSincePressHeld == time || time - timeSincePressHeld > shiftDelta)) {
                        timeSincePressHeld = time
                        if(selectorLoc > 0) selectorLoc--
                    }
                }
            }else if(!gamepad1.dpad_up && upDPAD_pressed) {
                buttonPressed = false
                upDPAD_pressed = false
                timeSincePressHeld = -1.0
            }
            if(gamepad1.dpad_down) { //DPAD DOWN
                if(currentPartition != Partition.HEADER) { //this shouldn't work in the header partition.

                    if (!downDPAD_pressed && !buttonPressed) {
                        buttonPressed = true
                        downDPAD_pressed = true
                        timeSincePressHeld = time
                    }
                    if (downDPAD_pressed && (timeSincePressHeld == time || time - timeSincePressHeld > shiftDelta)) {
                        timeSincePressHeld = time
                        if(selectorLoc < (if(currentPartition == Partition.REPLAYS) replayList.lastIndex else directoryList.lastIndex)) selectorLoc++
                    }
                }
            }else if(!gamepad1.dpad_down && downDPAD_pressed) {
                buttonPressed = false
                downDPAD_pressed = false
                timeSincePressHeld = -1.0
            }
            if(gamepad1.a && !a_pressed && !buttonPressed) { //A Button
                buttonPressed = true
                a_pressed = true

                when(currentPartition) {
                    Partition.DIRECTORY -> directoryPosition += "/" + directoryList[selectorLoc]
                    Partition.REPLAYS -> RecordingConfig.FILE_NAME = replayList[selectorLoc]
                }
            }else if (!gamepad1.a && a_pressed) {
                buttonPressed = false
                a_pressed = false
            }
            if(gamepad1.b && !b_pressed && !buttonPressed) { //B Button
                buttonPressed = true
                b_pressed = true

                if(directoryPosition != "")
                    directoryPosition = dir.parent

            }else if(!gamepad1.b && b_pressed) {
                buttonPressed = false
                b_pressed = false
            }
            if(gamepad1.x && !x_pressed && !buttonPressed) { //X Button
                buttonPressed = true
                x_pressed = true

                if(currentPartition != Partition.HEADER) {
                    if (x_presses > 1) {
                        when (currentPartition) {
                            Partition.DIRECTORY -> hardwareMap.appContext.deleteFile(directoryList[selectorLoc])
                            Partition.REPLAYS -> hardwareMap.appContext.deleteFile(replayList[selectorLoc])
                        }
                        x_presses = 0
                    }
                    else
                        x_presses++
                }

            }else if(!gamepad1.x && x_pressed) {
                buttonPressed = false
                x_pressed = false
            }

            if(gamepad1.left_bumper && !lbumper_pressed && !buttonPressed) { //Left Bumper
                buttonPressed = true
                lbumper_pressed = true

                if(RecordingConfig.DIRECTORY_NAME != "")
                    hardwareMap.appContext.getDir(directoryPosition + "/${TimeStampedData.REPLAY_DIRECTORY_PREFIX}" + RecordingConfig.DIRECTORY_NAME, Context.MODE_PRIVATE)
            }else if(!gamepad1.left_bumper && lbumper_pressed) {
                buttonPressed = false
                lbumper_pressed = false
            }

            if(x_presses > 0 && (!gamepad1.atRest() && !gamepad1.x)) x_presses = 0

            if(currentPartition != lastPartition) {
                lastPartition = currentPartition
                selectorLoc = 0
            }

            defaultRefresh = false

            send("Current Directory", if(directoryPosition == "")"Master Directory" else directoryPosition, ":")
            send("D-PAD ↔ to change PARTITION. D-PAD ↕ to move file selector inside of partition.")
            if(RecordingConfig.DIRECTORY_NAME != "") send("Use left bumper to assign new directory with name: ${RecordingConfig.DIRECTORY_NAME}")
            send("'A' button selects current file.")
            send("Press 'X' button twice to delete the file or directory")
            if(directoryPosition != "")
                send("Press 'B' to go back to the former directory.")
            send("")

            when(currentPartition) {
                Partition.HEADER -> {
                    send(if(directoryExists) "|SUB-DIRECTORIES|" else "No sub-directories.")
                    send(if(replayExists) "|REPLAYS|" else "No replay files.")
                }
                Partition.DIRECTORY -> {
                    send("-- SUB-DIRECTORIES --")
                    for (i in 0 until directoryList.size)
                        send("$i |${if(selectorLoc == i)selectorIcon else " ".repeat(selectorIcon.length)})}${directoryList[i]}")
                    if(replayExists)send("|REPLAYS|")
                }
                Partition.REPLAYS -> {
                    if(directoryExists)send("|DIRECTORIES|")
                    send("-- REPLAYS --")
                    for (i in 0 until replayList.size)
                        send("$i |${if(selectorLoc == i)selectorIcon else " ".repeat(selectorIcon.length)})}${replayList[i]}", if(RecordingConfig.FILE_NAME == replayList[i])"**" else "")
                }
            }

            defaultRefresh = true

        }
        telemetry.captionValueSeparator = ":"
    }

    private fun send(h : String, p : String = "", separator : String = "", refresh : Boolean = defaultRefresh) {
        telemetry.captionValueSeparator = separator
        telemetry.isAutoClear = refresh
        telemetry.addData(h, p)
        telemetry.update()
    }

}