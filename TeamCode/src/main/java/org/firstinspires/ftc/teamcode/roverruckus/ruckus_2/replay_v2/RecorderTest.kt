package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes.TeleOp

@Autonomous(name="RecorderTESTING")@Disabled
class RecorderTest : TeleOp() {

    lateinit var RECORD : ReplayFile.DataStream
    var STARTTIME = -1.0

    val msSaveStateInterval = 5 //TODO experiment with this value.
    private var msLastSaveStateTime = -1L
    
    private var hasControllerUpdated = false
    private var controllerNotRunning = -1.0

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

        super.robot.mecanumDrive.resetEncoders()
    }

    override fun loop() {
        super.loop()

        if(pad1.isResting && pad2.isResting && !hasControllerUpdated) {
            telemetry.addData("STATUS", "Recording will begin when the controller is used.")
            return
        }

        if (STARTTIME == -1.0) STARTTIME = time

        val elapsed = time - STARTTIME

        controllerNotRunning = if(pad1.isResting && pad2.isResting) elapsed
                               else -1.0

        val msCurrentTime = System.currentTimeMillis()
        telemetry.addData("Elapsed time", "$elapsed seconds")

        if (msCurrentTime - msLastSaveStateTime < msSaveStateInterval && msLastSaveStateTime != -1L)
            return

        msLastSaveStateTime = msCurrentTime


        /*
        RECORDING HERE
         */

        val point = RECORD.newPoint(elapsed)

        //grabbing all data from the hardware map


        for(subsystem in robot.subsystems)
            point.addByte(ReplayFile.DataByte(subsystem.key, subsystem.value.replayData()))

        /** OLD METHOD - good fer referencing...

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
        **/
    }

    override fun stop() {
        super.stop()

        if(controllerNotRunning > -1.0) RECORD.trim(0.0, controllerNotRunning)
        if(::RECORD.isInitialized) RECORD.write()
    }
}