package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.media.tv.TvInputManager
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.configuration.UnspecifiedMotor
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.RuckusOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.LiftMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.TimeoutException
import kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector


@Autonomous(name="RECORDING TEST OUT OF COMP")@Disabled
class RECORDINGTESTOUTOFCOMP : OpMode() {

    lateinit var RECORD : TimeStampedData.DataStream
    var STARTTIME = -1.0

    private val blacklistDevices = listOf("imu 1")

    override fun init() {
        msStuckDetectStop = 10000
        msStuckDetectLoop = 10000
    }

    override fun init_loop() {
        super.init_loop()

        telemetry.addData("File", if(RecordingConfig.desiredFilePath == "")"Not entered. Please wait to start." else RecordingConfig.desiredFilePath)

    }

    override fun start() {
        super.start()
        try {
            RECORD = TimeStampedData.DataStream(RecordingConfig.desiredFilePath, hardwareMap)
        }catch (e : FileNotFoundException) {
            telemetry.log().add("File not found. Stopping")
            requestOpModeStop()
        }

        if(RecordingConfig.desiredFilePath == "")requestOpModeStop()
    }

    override fun loop() {
        if(STARTTIME == -1.0)STARTTIME = time

        val elapsed = time - STARTTIME
        telemetry.addData("Elapsed time", "$elapsed seconds")

        /*
        RECORDING HERE
         */

        val point = RECORD.newPoint(elapsed)

        //grabbing all data from the hardware map

        deviceLoop@
        for(device in hardwareMap) {
            val name = hardwareMap.getNamesOf(device).first()

            if(blacklistDevices.contains(name)) continue@deviceLoop

            val data = when(device) {
                is DcMotor -> listOf(device.power, device.currentPosition.toDouble())
                is Servo -> listOf(device.position)
                is CRServo -> listOf(device.power)
                is BNO055IMU -> listOf(device.angularOrientation.toAxesOrder(AxesOrder.XYZ).thirdAngle.toDouble())
                else -> continue@deviceLoop
            }

            point.addByte(TimeStampedData.DataByte(name, data))
        }
    }

    override fun stop() {

        if(::RECORD.isInitialized)
            RECORD.write()
    }

}