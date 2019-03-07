package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.common.controller.Button
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import java.io.File

@Autonomous(name="REPLAY BROWSER", group="replay")
class ReplayBrowser : LinearOpMode() {

    private var defaultRefresh = false

    private enum class Partition {
        DIRECTORY, REPLAYS, HEADER
    }

    override fun runOpMode() {

        val pad1 = SmidaGamepad(gamepad1, this)
        val button : (SmidaGamepad.GamePadButton) -> Button = pad1::getButton

        val selectorIcon = ">>>"

        var lastPartition = Partition.HEADER
        var currentPartition = Partition.HEADER
        val baseDir = hardwareMap.appContext.getExternalFilesDir(ReplayFile.EXTERNAL_DIRECTORY_HEADING)
        var directoryPosition = ""
        var lastDirectoryPosition = ""
        var selectorLoc = 0

        val shiftDelta = 0.25

        var x_presses = 0

        while(!isStopRequested) {
            pad1.handleUpdate()

            val dir = File(baseDir, directoryPosition)
            val replayList = dir.list { _ : File, s : String -> s.toLowerCase().endsWith(".json") }
            val directoryList = dir.list { _ : File, s : String -> !s.toLowerCase().endsWith(".json") }
            val replayExists = replayList.isNotEmpty()
            val directoryExists = directoryList.isNotEmpty()


            if(button(SmidaGamepad.GamePadButton.PAD_LEFT).isPressed)
                if(pad1.lastCheckedButton.holdingTimeCheck(shiftDelta, time)) {
                    currentPartition = when(currentPartition) {
                        Partition.HEADER -> currentPartition
                        Partition.DIRECTORY -> Partition.HEADER
                        Partition.REPLAYS -> if(directoryExists) Partition.DIRECTORY else Partition.HEADER
                    }
                }
            if(button(SmidaGamepad.GamePadButton.PAD_RIGHT).isPressed)
                if(pad1.lastCheckedButton.holdingTimeCheck(shiftDelta, time)) {
                    currentPartition = when(currentPartition) {
                        Partition.HEADER -> if(directoryExists) Partition.DIRECTORY else if(replayExists) Partition.REPLAYS else currentPartition
                        Partition.DIRECTORY -> if(replayExists) Partition.REPLAYS else Partition.DIRECTORY
                        Partition.REPLAYS -> currentPartition
                    }
                }
            if(button(SmidaGamepad.GamePadButton.PAD_UP).isPressed)
                if(pad1.lastCheckedButton.holdingTimeCheck(shiftDelta, time)) {
                    if (currentPartition != Partition.HEADER) //this shouldn't work in the header partition.
                        if (selectorLoc > 0) selectorLoc--

                }
            if(button(SmidaGamepad.GamePadButton.PAD_DOWN).isPressed)
                if(pad1.lastCheckedButton.holdingTimeCheck(shiftDelta, time)) {
                    if (currentPartition != Partition.HEADER) //this shouldn't work in the header partition.
                        if (selectorLoc < (if (currentPartition == Partition.REPLAYS) replayList.lastIndex else directoryList.lastIndex)) selectorLoc++
                }
            if(button(SmidaGamepad.GamePadButton.A).isIndividualActionButtonPress())
                when(currentPartition) {
                    Partition.DIRECTORY -> directoryPosition += (if(directoryPosition != "") "/" else "") + directoryList[selectorLoc]
                    Partition.REPLAYS -> RecordingPreferences.setFileDestination( directoryPosition + "/" + replayList[selectorLoc], hardwareMap.appContext)
                }
            if(button(SmidaGamepad.GamePadButton.B).isIndividualActionButtonPress())
                if(directoryPosition != "") {
                    val thLindex = directoryPosition.lastIndexOf('/')
                    directoryPosition = directoryPosition.substring(0, if(thLindex < 0) 0 else thLindex)
                }
            if(button(SmidaGamepad.GamePadButton.X).isIndividualActionButtonPress())
                if(currentPartition != Partition.HEADER) {
                    if (x_presses > 0) {
                        when (currentPartition) {
                            Partition.DIRECTORY -> {
                                val f = File(dir, directoryList[selectorLoc])
                                f.deleteRecursively()
                            }
                            Partition.REPLAYS -> File(dir, replayList[selectorLoc]).delete()
                        }
                        x_presses = 0
                    }
                    else x_presses++
                }
            if(button(SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed)
                if(RecordingPreferences.newDirectory != "") {
                    File(dir, RecordingPreferences.newDirectory).mkdirs()
                    RecordingPreferences.newDirectory = ""
                }

            if(x_presses > 0 && (!pad1.isResting && !button(SmidaGamepad.GamePadButton.X).isPressed)) x_presses = 0

            if(directoryPosition != lastDirectoryPosition) {
                lastDirectoryPosition = directoryPosition
                selectorLoc = 0
            }

            if(currentPartition != lastPartition) {
                lastPartition = currentPartition
                selectorLoc = 0
            }

            when(currentPartition) {
                Partition.DIRECTORY -> if(!directoryExists) currentPartition = Partition.HEADER
                Partition.REPLAYS -> if(!replayExists) currentPartition = Partition.HEADER
            }

            defaultRefresh = false

            if(x_presses > 0) {
                send("ARE YOU SURE YOU WANT TO DELETE ${if(currentPartition == Partition.DIRECTORY)directoryList[selectorLoc].toUpperCase() else replayList[selectorLoc].toUpperCase()}? Press again to confirm, or perform another action to cancel.")
            }else {

                send("Current Directory: ", if (directoryPosition == "") "Master Directory" else directoryPosition)
                send("D-PAD ↔ to change PARTITION. D-PAD ↕ to move file selector inside of partition.")
                if (RecordingPreferences.newDirectory != "") send("Use 'Left Bumper' to assign new directory with name: ${RecordingPreferences.newDirectory}")
                send("'A' button selects current file.")
                send("Press 'X' button twice to delete the file or directory")
                if (directoryPosition != "")
                    send("Press 'B' to go back to the former directory.")
                send("")

                when (currentPartition) {
                    Partition.HEADER -> {
                        send(if (directoryExists) "|SUB-DIRECTORIES(${directoryList.size})|" else "No sub-directories.")
                        send(if (replayExists) "|REPLAYS(${replayList.size})|" else "No replay files.")
                    }
                    Partition.DIRECTORY -> {
                        send("-- SUB-DIRECTORIES --")
                        for (i in 0 until directoryList.size)
                            send("$i |${if (selectorLoc == i) selectorIcon else " ".repeat(selectorIcon.length)})}${directoryList[i]}")
                        if (replayExists) send("|REPLAYS|")
                    }
                    Partition.REPLAYS -> {
                        if (directoryExists) send("|DIRECTORIES|")
                        send("-- REPLAYS --")
                        for (i in 0 until replayList.size)
                            send("$i |${if (selectorLoc == i) selectorIcon else " ".repeat(selectorIcon.length)})}${replayList[i]}", if (RecordingPreferences.filePath == directoryPosition + "/" + replayList[i]) "**" else "")
                    }
                }
            }
            telemetry.update()
        }
        telemetry.captionValueSeparator = ":"
    }

    private fun recursiveSubFiles(f : File) : List<File> {
        val reList = ArrayList<File>()
        if(f.isDirectory) {
            if(f.list().isNotEmpty()) {
                for(fx in f.listFiles()) {
                    if(fx.isDirectory) {
                        reList.add(fx)
                        reList.addAll(recursiveSubFiles(fx))
                    }
                }
            }
        }
        return reList
    }

    private fun send(h : String, p : String = "") = telemetry.addData(h, p)

}