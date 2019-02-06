package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.media.tv.TvInputManager
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.RuckusOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.LiftMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import java.util.concurrent.TimeoutException
import kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector


@Autonomous(name="Recording Mode")
class RecordingOPMode : RuckusOpMode() {

    lateinit var IMU_ : IMU
    lateinit var RECORD : TimeStampedData.DataStream
    var STARTTIME = -1.0

    override fun init() {
        super.init()

        IMU_ = IMU()
        IMU_.init(this)
    }

    override fun init_loop() {
        super.init_loop()

        telemetry.addData("File", if(RecordingConfig.desiredFilePath == "")"Not entered. Please wait to start." else RecordingConfig.desiredFilePath)

    }

    override fun start() {
        super.start()


        RECORD = TimeStampedData.DataStream(RecordingConfig.desiredFilePath, hardwareMap)

        if(RecordingConfig.FILE_NAME == "")requestOpModeStop()

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

        //grabbing all data from the hardware map

        deviceLoop@
        for(device in hardwareMap) {
            val name = hardwareMap.getNamesOf(device)

            val data = when(device) {
                is DcMotor -> listOf(device.power, device.currentPosition.toDouble())
                is Servo -> listOf(device.position)
                is CRServo -> listOf(device.power)
                is BNO055IMU -> listOf(device.angularOrientation.toAxesOrder(AxesOrder.XYZ).thirdAngle.toDouble())
                else -> continue@deviceLoop
            }

            point.addByte(TimeStampedData.DataByte(name.first(), data))
        }


        /** THIS IS AN OLD METHOD IN CASE NEW ONE DOESNT WORK
        //DRIVETRAIN
        DRIVETRAIN.motorMap().forEach{ point.addByte(TimeStampedData.DataByte(it.key, listOf(it.value.power, it.value.currentPosition.toDouble()))) }
        //IMU
        point.addByte(TimeStampedData.DataByte(IMU.Config.DEVICE_NAME, listOf(IMU_.XYZ().thirdAngle.toDouble())))
        //LIFT
        point.addByte(TimeStampedData.DataByte(HNAMES_RUCKUS.LIFT_MOTOR, listOf(LIFT.liftMotor.power, LIFT.liftMotor.currentPosition.toDouble())))
        //LINEAR SLIDES
        LINEAR_SLIDES.motorMap().forEach{ point.addByte(TimeStampedData.DataByte(it.key, listOf(it.value.power, it.value.currentPosition.toDouble()))) }
        //INTAKE
        point.addByte(TimeStampedData.DataByte(HNAMES_RUCKUS.TOTEM, listOf(INTAKE.totemServo.position)))
        **/
    }

    override fun stop() {
        RECORD.write()
    }

}