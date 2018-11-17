package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import com.vuforia.Vuforia
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.drivetrain.util.PID
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

open class AutonomousBase(val autoClearTelemtry : Boolean, val useTF : Boolean) : LinearRobot(MiniTankDrive(), mapOf(Pair("LIFT", LiftSystem()), Pair("IMU", IMU()))) {

    val TF_GOLD_LABEL = "Gold Mineral"
    val TF_SILVER_LABEL = "Silver Mineral"

    lateinit var LIFT : LiftSystem
    lateinit var IMU : IMU

    lateinit var VUFORIA : VuforiaLocalizer
    lateinit var TFOD : TFObjectDetector

    val TIMER = ElapsedTime()

    val rot_controllerPID = PID(0.7F, 0.5F, 0.2F)

    val dashboard = FtcDashboard.getInstance()


    //ENCODER VALUES
    val ENCODER_40_PPR = 1120
    val ENCODER_60_PPR = 1680
    val FRONT_WHEEL_DIAMETER_INCH = 4
    val BACK_WHEEL_DIAMETER_INCH = 3.85
    val GEARING = 1.0

    val FRONT_ENCODER_INCH = ((ENCODER_60_PPR * GEARING) / (FRONT_WHEEL_DIAMETER_INCH * Math.PI)).roundToInt()
    val BACK_ENCODER_INCH = ((ENCODER_40_PPR * GEARING) / (BACK_WHEEL_DIAMETER_INCH * Math.PI)).roundToInt()

    val DRIVE_SPEED = 1.0 //default drive speed
    val TURN_SPEED = 0.6 //default turn speed
    val HEADING_THRESHOLD = 1.0 //can be smaller

    val P_TURN = 0.1
    val P_DRIVE = 0.15


    override fun runOpMode() {
        super.runOpMode()

        //dashboard.telemetry.isAutoClear = autoClearTelemtry

        LIFT = COMPONENTS["LIFT"] as LiftSystem
        IMU = COMPONENTS["IMU"] as IMU

        if(useTF && ClassFactory.getInstance().canCreateTFObjectDetector()) {
            useVuforia()
            useTFOD()
        }

        telemetry.addData("autonomous base finished.", "")
        telemetry.update()
    }

    fun resetenc(final_mode : DcMotor.RunMode, motor : DcMotor){
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = final_mode
    }

    enum class RotationType {
        ONE_EIGHTY,
        THREE_SIXTY
    }
    fun PID_rot(angle : Double, rot : RotationType) {
        rot_controllerPID.stop()

        var ang = angle

        val isOneEighty = rot == RotationType.ONE_EIGHTY


        if(angle < 0 && !isOneEighty)ang = 360+angle

        while(Math.abs(ang - (if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle)) > 3 && opModeIsActive()){
            val pow = rot_controllerPID.calculate(ang.toFloat(), if(!isOneEighty)IMU.getZ360() else IMU.XYZ().thirdAngle)
            telemetryUpdate(linkedMapOf(
                    "Calculated Error (val)" to rot_controllerPID.ERROR_POST,
                    "Calculated Rotation PID (val)" to pow,
                    "Desired Rotation (degrees)" to angle,
                    "Correct Desired Rotation (degrees)" to ang,
                    "Current Rotation (degrees)" to if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle,
                    "Current Time (seconds)" to rot_controllerPID.TIME.seconds()
            ))
            DRIVETRAIN.move(0.0, 0.0, pow)
        }
        DRIVETRAIN.stop()
    }

    fun hold(millis : Long){
        TIMER.reset()
        while(TIMER.milliseconds() < millis && opModeIsActive()){ idle() }
    }

    fun telemetryUpdate(data : Map<String, Any>) {
        val packet = TelemetryPacket()
        packet.putAll(data)

        for(d in data) telemetry.addData(d.key, d.value)

        telemetry.update()
        dashboard.sendTelemetryPacket(packet)
    }

    fun useTFOD() {
        val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        TFOD = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, VUFORIA)
        TFOD.loadModelFromAsset("RoverRuckus.tflite", TF_GOLD_LABEL, TF_SILVER_LABEL)
    }

    fun useVuforia() {
        val vu_param = VuforiaLocalizer.Parameters()

        vu_param.vuforiaLicenseKey = HARDWARENAMES_MINIBOT.VUFORIA_KEY.v
        vu_param.cameraName = hardwareMap.get(WebcamName::class.java, "Webcam 1")

        VUFORIA = ClassFactory.getInstance().createVuforia(vu_param)
    }

    enum class SamplePosition {
        LEFT,
        CENTER,
        RIGHT,
        N_A
    }

    fun findSample_THREE(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) {
            var gold_dist = -1F
            var silver1_dist = -1F
            var silver2_dist = -1F

            for(r in recog) {
                if (r.label == TF_SILVER_LABEL) {
                    if (silver1_dist > -1) silver2_dist = r.left
                    else silver1_dist = r.left
                } else if (r.label == TF_GOLD_LABEL) gold_dist = r.left
            }

            if(gold_dist > -1 && silver1_dist > -1 && silver2_dist > -1){
                if(gold_dist < silver1_dist && gold_dist < silver2_dist) return SamplePosition.LEFT
                else if((gold_dist < silver1_dist && gold_dist > silver2_dist) || (gold_dist < silver2_dist && gold_dist > silver1_dist)) return SamplePosition.CENTER
                else return SamplePosition.RIGHT
            }
            else return SamplePosition.N_A
        }

        else return SamplePosition.N_A
    }
    fun findSample(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) {
            var gold_dist = -1F
            var silver1_dist = -1F
            var silver2_dist = -1F

            for(r in recog) {
                if (r.label == TF_SILVER_LABEL) {
                    if (silver1_dist > -1) silver2_dist = r.left
                    else silver1_dist = r.left
                } else if (r.label == TF_GOLD_LABEL) gold_dist = r.left
            }
            telemetry.addData("gold dist", gold_dist)
            telemetry.addData("silver 1 dist", silver1_dist)
            telemetry.addData("silver 2 dist", silver2_dist)

            if(gold_dist > -1 && silver1_dist > -1 && silver2_dist > -1){
                if(gold_dist < silver1_dist && gold_dist < silver2_dist) return SamplePosition.LEFT
                else if(gold_dist < silver1_dist && gold_dist > silver2_dist) return SamplePosition.CENTER
                else return SamplePosition.RIGHT
            }else {
                val face = getSampleFace()
                telemetry.addData("sample face", face.name)
                if(silver1_dist > -1 && silver2_dist > -1) when(face){
                    SampleFace.LEFT -> return SamplePosition.RIGHT
                    SampleFace.RIGHT -> return SamplePosition.LEFT
                    else -> return SamplePosition.N_A
                }
                if(silver1_dist > -1 && gold_dist > -1) {
                    var goldIsLeft = false
                    if(silver1_dist > gold_dist) goldIsLeft = true

                    when (face) {
                        SampleFace.LEFT -> return if(goldIsLeft) SamplePosition.LEFT else SamplePosition.CENTER
                        SampleFace.RIGHT -> return if(goldIsLeft) SamplePosition.CENTER else SamplePosition.RIGHT
                        else -> return SamplePosition.CENTER
                    }
                }
                else return SamplePosition.N_A
            }
        }
        else return SamplePosition.N_A
    }

    enum class SampleFace {
        CENTER,
        LEFT,
        RIGHT
    }
    fun getSampleFace() : SampleFace {
        val raw = IMU.getZ360()
        telemetry.addData("raw angle", raw)
        val center_diff = 10
        if    ((raw <= center_diff || raw >= (360-center_diff))           || //CENTER
                (raw >= (90 - center_diff) && raw <= (90 + center_diff))  ||
                (raw >= (180 - center_diff) && raw <= (180 + center_diff))||
                (raw >= (270 - center_diff) && raw <= (270 + center_diff)))return SampleFace.CENTER
        else if((raw < (360 - center_diff) && raw > 315)||                  //RIGHT
                (raw < (90 - center_diff) && raw > 45)  ||
                (raw < (180 - center_diff) && raw > 135)||
                (raw < (270 - center_diff) && raw > 225))return SampleFace.RIGHT
        else if((raw > (center_diff) && raw < 45)       ||                  //LEFT
                (raw > (90 + center_diff) && raw < 135) ||
                (raw > (180 + center_diff) && raw < 225)||
                (raw > (270 + center_diff) && raw < 315))return SampleFace.LEFT
        else return SampleFace.CENTER
    }

    fun rot_noPID(angle : Double, type : RotationType) {


        var ang = angle

        val isOneEighty = type == RotationType.ONE_EIGHTY

        if(ang < 0 && !isOneEighty) ang+= 360

        val timerrot = ElapsedTime()
        timerrot.reset()
        while(timerrot.time() < 5 && Math.abs(ang - (if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle)) > 4 && opModeIsActive()){
            val pow = if(Math.abs(ang - (if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle)) > 9) 0.7 else 0.2
            val dir = ang - (if(!isOneEighty) IMU.getZ360() else IMU.XYZ().thirdAngle)
            DRIVETRAIN.move(0.0, 0.0, pow * (if(dir > 0) 1.0 else -1.0))
        }
        DRIVETRAIN.stop()
    }

    fun drive(speed : Double = DRIVE_SPEED, distance : Double) {
        val frontCounts = distance * FRONT_ENCODER_INCH
        val backCounts = distance * BACK_ENCODER_INCH

        DRIVETRAIN as MiniTankDrive

        val keepAngle = IMU.XYZ().thirdAngle.toDouble()

        for(pair in DRIVETRAIN.motorMap()) {
            when(pair.key) {
                HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_LEFT.v,
                HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_RIGHT.v -> pair.value.targetPosition = pair.value.currentPosition + frontCounts.toInt()
                HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_LEFT.v,
                HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_RIGHT.v -> pair.value.targetPosition = pair.value.currentPosition + backCounts.toInt()
            }
        }

        DRIVETRAIN.motorList().forEach { it.mode = DcMotor.RunMode.RUN_TO_POSITION }

        val run_speed  = Range.clip(speed.absoluteValue, 0.0, 1.0)
        DRIVETRAIN.powerSet(run_speed, run_speed)

        while(opModeIsActive() && motorsAreBusy()) {
            val error = getError(keepAngle)
            var steer = getSteer(error, P_DRIVE)

            steer *= if(distance < 0) -1.0 else 1.0

            var leftSpeed = speed - steer
            var rightSpeed = speed + steer

            val max = Math.max(leftSpeed, rightSpeed)

            if(max > 1.0) {
                leftSpeed /= max
                rightSpeed /= max
            }

            DRIVETRAIN.powerSet(leftSpeed, rightSpeed)
        }

        DRIVETRAIN.motorList().forEach { it.mode = DcMotor.RunMode.RUN_USING_ENCODER }
        DRIVETRAIN.stop()
    }

    fun turn(speed : Double = TURN_SPEED, angle : Double) {
        while(opModeIsActive() && !isHeading(speed, angle)) {
            telemetry.addData("turning", "current error: ${getError(angle)}")
            telemetry.update()
        }
    }

    fun motorsAreBusy() : Boolean {
        DRIVETRAIN.motorList().forEach { if(it.isBusy) return true }
        return false
    }

    fun isHeading(speed : Double, angle : Double, pCO : Double = P_TURN) : Boolean {
        val error = getError(angle)
        val steer : Double
        var onTarget = false
        val leftSpeed : Double
        val rightSpeed : Double

        DRIVETRAIN as MiniTankDrive

        if(Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0
            onTarget = true
        } else
            steer = getSteer(error, pCO)

        rightSpeed = steer * speed
        leftSpeed = -rightSpeed

        DRIVETRAIN.powerSet(leftSpeed, rightSpeed)
        return onTarget
    }

    fun getError(targetAngle : Double) : Double {
        var error : Double = targetAngle - IMU.XYZ().thirdAngle

        while(error > 180) error -= 360
        while(error <= -180) error += 360

        return error
    }

    fun getSteer(error : Double, pCO : Double) : Double = Range.clip(error * pCO, -1.0, 1.0)


}