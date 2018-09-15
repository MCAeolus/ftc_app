package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer

/**
 * Created by Nathan.Smith.19 on 10/10/2017.
 */
//@Autonomous(name = "VUMARK TEST", group="DEV")
class VumarkIdentifier : LinearOpMode() {

    override fun runOpMode() {
        val cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName())
        val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)

        parameters.vuforiaLicenseKey = "AX+gX5z/////AAAAGemESFRCSEr9mlgYKyJlg7QkunLknhP5uXkpeqYVGio+FNmaVefvE0yC0ueWXyecPzSipBsPHsGK1aduCXc87+J6jRilxDjmP8aSmRGOopchZ7KhKbpKRLtcP+i408NrfCRzT1CVhaQ9YI3ObQApDcyNxoiz5G8TVCh7SIr1qL0NXHW1QEuCgGP2BhSJDhUomuotPTd11WcfoMyFqBg2FUb3iC1Msv4iIzUirCRkQU3boFywhIVSZ3cvM+1IWDh/AjMgNZ1Fy/jueNKirAQqQVDqC5cYn9aAvEpewgJAwMjYlaYaXSjb+hLkyh1+ZY2MLsZAbed3pUGJ8eI65QgI3NDGtJs3XL7r8rXoIVvnoezg"

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK
        val vuforia = ClassFactory.createVuforiaLocalizer(parameters)

        val relicTrack = vuforia.loadTrackablesFromAsset("RelicVuMark")
        val relicTemplate = relicTrack[0]

        telemetry.addData("!!INFO >", "Waiting for start..")
        telemetry.update()

        waitForStart()

        relicTrack.activate()

        while (opModeIsActive()) {
            val vumark = RelicRecoveryVuMark.from(relicTemplate)
            telemetry.addData("Mark:", vumark)
            telemetry.update()

        }

    }
}