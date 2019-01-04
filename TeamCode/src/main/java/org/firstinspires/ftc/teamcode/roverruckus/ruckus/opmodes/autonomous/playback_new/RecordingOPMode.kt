package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.media.tv.TvInputManager
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.RuckusOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import java.util.concurrent.TimeoutException
import kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector


@Autonomous(name="Recording Mode")
class RecordingOPMode : RuckusOpMode() {

    lateinit var IMU : IMU
    lateinit var RECORD : TimeStampedData.DataStream
    var STARTTIME = -1.0

    override fun init() {
        super.init()

        IMU = IMU()
        IMU.init(this)
    }

    override fun init_loop() {
        super.init_loop()

        telemetry.addData("File", if(RecordingConfig.FILE_NAME == "")"Not entered. Please wait to start." else RecordingConfig.FILE_NAME)
        telemetry.addData("Will Trim?", RecordingConfig.SHOULD_TRIM)

    }

    override fun start() {
        super.start()

        if(RecordingConfig.FILE_NAME == "")requestOpModeStop()

        RECORD = TimeStampedData.DataStream(RecordingConfig.FILE_NAME, hardwareMap)

        (DRIVETRAIN as MecanumDriveTrain).resetEncoders()
    }

    override fun loop() {
        super.loop()
        if(STARTTIME == -1.0)STARTTIME = time

        val elapsed = time - STARTTIME
        telemetry.addData("Elapsed time", "$elapsed seconds")

        /*
        RECORDING HERE
         */

        val point = RECORD.newPoint(elapsed)

        DRIVETRAIN.motorMap().forEach{ point.addByte(TimeStampedData.DataByte(it.key, listOf(it.value.power, it.value.currentPosition.toDouble()))) }
        point.addByte(TimeStampedData.DataByte(IMU.DEVICE_NAME, listOf(IMU.XYZ().thirdAngle.toDouble())))
    }

    override fun stop() {
        this.stop()

        RECORD.write()
    }

}