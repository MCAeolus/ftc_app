package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables

/**
 * Created by Nathan.Smith.19 on 11/9/2017.
 */
class VumarkSect {

    lateinit var TEMPLATE : VuforiaTrackable
    lateinit var TRACKABLES : VuforiaTrackables

    fun init(hMap : HardwareMap): Boolean {
        val cameraMonitorViewId = hMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hMap.appContext.getPackageName())

        val parameters = VuforiaLocalizer.Parameters()

        parameters.vuforiaLicenseKey = "AX+gX5z/////AAAAGemESFRCSEr9mlgYKyJlg7QkunLknhP5uXkpeqYVGio+FNmaVefvE0yC0ueWXyecPzSipBsPHsGK1aduCXc87+J6jRilxDjmP8aSmRGOopchZ7KhKbpKRLtcP+i408NrfCRzT1CVhaQ9YI3ObQApDcyNxoiz5G8TVCh7SIr1qL0NXHW1QEuCgGP2BhSJDhUomuotPTd11WcfoMyFqBg2FUb3iC1Msv4iIzUirCRkQU3boFywhIVSZ3cvM+1IWDh/AjMgNZ1Fy/jueNKirAQqQVDqC5cYn9aAvEpewgJAwMjYlaYaXSjb+hLkyh1+ZY2MLsZAbed3pUGJ8eI65QgI3NDGtJs3XL7r8rXoIVvnoezg"

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK
        val vuforia = ClassFactory.createVuforiaLocalizer(parameters)

        TRACKABLES = vuforia.loadTrackablesFromAsset("RelicVuMark")
        TEMPLATE = TRACKABLES[0]

        TRACKABLES.activate()

        return true
    }

    fun activate() {
        TRACKABLES.activate()
    }

    fun deactivate() {
        TRACKABLES.deactivate()
    }

    fun update() = RelicRecoveryVuMark.from(TEMPLATE)
}