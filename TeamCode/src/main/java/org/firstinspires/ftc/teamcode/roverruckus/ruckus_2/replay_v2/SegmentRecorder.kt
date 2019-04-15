package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes.TeleOp

@Autonomous(name="Segment Recorder")
class SegmentRecorder : TeleOp() {

    lateinit var RECORD : ReplayFile.DataStream
    var STARTTIME = -1.0

    val msSaveStateInterval = 5 //new point every 5 ms
    private var msLastSaveStateTime = -1L
    
    private var hasControllerUpdated = false
    private var controllerNotRunning = -1.0

    private var startingFileTime = -1.0
        private set

    override fun init() {
        super.init()

        RecordingPreferences.loadFromPrefs(hardwareMap.appContext)

        msStuckDetectStop = 10000
        msStuckDetectLoop = 10000
    }

    override fun init_loop() {
        super.init_loop()

        telemetry.addData("File", if(RecordingPreferences.filePath == "")"Not entered. Please wait to start." else RecordingPreferences.filePath)

    }

    override fun start() {
        super.start()

        RECORD = ReplayFile.DataStream(RecordingPreferences.filePath, hardwareMap)

        if(RecordingPreferences.filePath == "")requestOpModeStop()
        RECORD.load()

        super.robot.mecanumDrive.resetEncoders()

        startingFileTime = RECORD.getRawData().lastOrNull()?.time ?: 0.0
    }

    override fun loop() {
        super.loop()
        telemetry.clear()
        telemetry.addData("is slow mode", robot.mecanumDrive.inSlowMode)

        if(pad1.isResting && pad2.isResting && !hasControllerUpdated) {
            telemetry.addData("STATUS", "Recording will begin when the controller is used.")
            return
        }

        if (STARTTIME == -1.0) STARTTIME = time

        val elapsed = (time - STARTTIME) + startingFileTime

        controllerNotRunning = if(pad1.isResting && pad2.isResting) elapsed
                               else -1.0

        val msCurrentTime = System.currentTimeMillis()
        telemetry.addData("Elapsed time", "$elapsed seconds, since segment started: ${time - STARTTIME} seconds")

        //if (msCurrentTime - msLastSaveStateTime < msSaveStateInterval && msLastSaveStateTime != -1L)
        //    return

        msLastSaveStateTime = msCurrentTime


        /*
        RECORDING HERE
         */

        val point = RECORD.newPoint(elapsed)

        //grabbing all data from the hardware map


        for(subsystem in robot.subsystems)
            point.addByte(ReplayFile.DataByte(subsystem.key, subsystem.value.replayData()))
    }

    override fun stop() {
        super.stop()

        if(controllerNotRunning > -1.0) RECORD.trim(0.0, controllerNotRunning)
        if(::RECORD.isInitialized) RECORD.write()
    }
}