package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import android.content.Context
import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.RuckusOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import java.io.FileOutputStream


@Autonomous(name = "Teleop Recorder")@Disabled
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
        telemetry.addData("Trim status", RecordingFileFromFTCDashboard.WILL_FILE_TRIM)

        if(RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD == "") telemetry.addData("Status", "waiting for file name to be updated.")
        else telemetry.addData("Status", "ready to start.")


    }

    override fun start() {
        super.start()

        if(RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD == "") requestOpModeStop()

        (DRIVETRAIN as MecanumDriveTrain).resetEncoders()
        //reset all encoders here


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
        if (startTime == -1.0) startTime = time

        val elapsed = time - startTime
        telemetry.addData("File", RecordingFileFromFTCDashboard.FILE_NAME_TO_RECORD)
        telemetry.addData("Elapsed Time", elapsed)


        //UPDATE GIVEN ADDED MACHINES
        DRIVETRAIN.motorMap().forEach{ recorder.queue(it.key, elapsed)}
        recorder.queue(org.firstinspires.ftc.teamcode.common.common_machines.IMU.Config.DEVICE_NAME, elapsed)
        //recorder.queue(HNAMES_RUCKUS.SERVO_DUMMY, elapsed)
    }

    override fun stop() {
        super.stop()

        //if(RecordingFileFromFTCDashboard.WILL_FILE_TRIM) trim()
        //recorder.recordQueue()

        try {
            output.close()
        }catch(e : Exception) {
            e.printStackTrace()
        }
    }

    private fun trim() {
        var differenceIndexBeginning = -1
        var differenceIndexEnd = -1

        for(i in 0 until recorder.imuQueue.size) {
            if(differenceIndexBeginning == -1 && (nextIsSame(i, recorder.imuQueue) != true || nextIsSame(i, recorder.dcQueue) != true || nextIsSame(i, recorder.servoQueue) != true))
                differenceIndexBeginning = i

            val revIndex = recorder.imuQueue.lastIndex - i
            if(differenceIndexEnd == -1 && (lastIsSame(revIndex, recorder.imuQueue) != true || lastIsSame(revIndex, recorder.dcQueue) != true || lastIsSame(revIndex, recorder.servoQueue) != true))
                differenceIndexEnd = revIndex

            if(differenceIndexBeginning != -1 && differenceIndexEnd != -1) break
        }

        if(differenceIndexEnd <= differenceIndexBeginning) differenceIndexEnd = recorder.imuQueue.lastIndex

        if(differenceIndexBeginning > -1) {
            val newListIMU = ArrayList<TimeStampedDataStream.Data>()
            val newListDC = ArrayList<TimeStampedDataStream.Data>()
            val newListSERVO = ArrayList<TimeStampedDataStream.Data>()

            var time_t = 0.0

            for(i in differenceIndexBeginning..differenceIndexEnd) {
                val old_d_imu = recorder.imuQueue[i]
                val old_d_dc = recorder.dcQueue[i]
                val old_d_servo = recorder.servoQueue[i]

                time_t+=old_d_imu.timeDelta

                newListIMU.add(TimeStampedDataStream.Data(old_d_imu.name, time_t, old_d_imu.data, old_d_imu.timeDelta))
                newListDC.add(TimeStampedDataStream.Data(old_d_dc.name, time_t, old_d_dc.data, old_d_dc.timeDelta))
                newListSERVO.add(TimeStampedDataStream.Data(old_d_servo.name, time_t, old_d_servo.data, old_d_servo.timeDelta))

            }

            recorder.imuQueue = newListIMU
            recorder.dcQueue = newListDC
            recorder.servoQueue = newListSERVO
        }
    }

    private fun nextIsSame(i : Int, l : List<TimeStampedDataStream.Data>) : Boolean? = if((l.lastIndex) >= (i + 1)) l[i].data.contentEquals(l[i + 1].data) else true
    private fun lastIsSame(i : Int, l : List<TimeStampedDataStream.Data>) : Boolean? = if((i - 1) >= 0) l[i].data.contentEquals(l[i - 1].data) else true

    //@Config
    object RecordingFileFromFTCDashboard {
        @JvmField var FILE_NAME_TO_RECORD = ""
        @JvmField var WILL_FILE_TRIM = true
    }
}