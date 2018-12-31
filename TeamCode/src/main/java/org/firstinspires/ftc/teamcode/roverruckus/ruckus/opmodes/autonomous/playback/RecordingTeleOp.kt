package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import android.content.Context
import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.RuckusOpMode
import java.io.FileOutputStream


@Autonomous(name = "Teleop Recorder")
class RecordingTeleOp : RuckusOpMode() {

    private var startTime = -1.0
    private lateinit var recorder : Recording.Record
    private lateinit var output : FileOutputStream

    lateinit var IMU : IMU

    override fun init() {
        super.init()
        IMU = IMU()
        IMU.init(this)
    }

    override fun init_loop() {
        super.init_loop()

        telemetry.addData("File Name", RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD)

        if(RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD == "") telemetry.addData("Status", "waiting for file name to be updated.")
        else telemetry.addData("Status", "ready to start.")

    }

    override fun start() {
        super.start()

        if(RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD == "") requestOpModeStop()

        try {
            output = hardwareMap.appContext.openFileOutput(RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD, Context.MODE_PRIVATE)
            recorder = Recording.Record(hardwareMap, output)
        }catch(e : Exception) {
            e.printStackTrace()
            requestOpModeStop()
        }
    }

    override fun loop() {
        super.loop()
        if(startTime == -1.0) startTime = time

        val elapsed = time - startTime
        telemetry.addData("File", RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD)
        telemetry.addData("Elapsed Time", elapsed)


        //UPDATE GIVEN ADDED MACHINES
        DRIVETRAIN.motorList().forEach{ recorder.record(it.deviceName, elapsed)}
        recorder.record(IMU.DEVICE_NAME, elapsed)
    }

    override fun stop() {
        super.stop()

        try {
            output.close()
        }catch(e : Exception) {
            e.printStackTrace()
        }
    }

    @Config
    object RecordingFileFromFTCDashboard {
        @JvmField var FILE_NAME_TO_RECORD = ""
    }
}