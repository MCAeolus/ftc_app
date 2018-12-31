package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.common.util.TelemetryField
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.TotemDropper

@TeleOp(name="Telemetry Operation Mode")@Disabled
class TelemetryOp : Robot(MiniTankDrive(), mapOf(Pair("Lift", LiftSystem()), Pair("IMU", IMU()), Pair("TOTEM", TotemDropper()))) {

    @TelemetryField lateinit var LIFT_SYSTEM: LiftSystem
    @TelemetryField lateinit var IMU: IMU
    @TelemetryField lateinit var TOTEM : TotemDropper

    lateinit var SMIDA_TELE : SmidaTelemetry

    override fun start() {
        LIFT_SYSTEM = COMPONENTS["Lift"] as LiftSystem
        IMU = COMPONENTS["IMU"] as IMU
        TOTEM = COMPONENTS["TOTEM"] as TotemDropper
        for (m in DRIVETRAIN.motorList())
            m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        LIFT_SYSTEM.lift_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        LIFT_SYSTEM.lift_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        SMIDA_TELE = SmidaTelemetry(true, true, this, "")

    }

    override fun loop() {
        //telemetry()
        SMIDA_TELE.update()

        if(gamepad1.b){
            resetenc(DcMotor.RunMode.RUN_USING_ENCODER, LIFT_SYSTEM.lift_motorL)
            resetenc(DcMotor.RunMode.RUN_USING_ENCODER, LIFT_SYSTEM.lift_motorR)
        }

        if(gamepad1.x)
            TOTEM.dump()
        else TOTEM.reset()

    }

    fun resetenc(final_mode : DcMotor.RunMode, motor : DcMotor){
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = final_mode
    }
}