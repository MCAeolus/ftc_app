/**
package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous

import com.disnodeteam.dogecv.CameraViewDisplay
import com.disnodeteam.dogecv.detectors.TrianglesAreMyFavoriteColor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/**
 * Created by Nathan.Smith.19 on 12/8/2017.
 */
@TeleOp(name="Triangle Test")
class TriangleTest : LinearOpMode() {

    override fun runOpMode() {
        val triDetector = TrianglesAreMyFavoriteColor()
        triDetector.init(hardwareMap.appContext, CameraViewDisplay.getInstance())
        triDetector.enable()
        waitForStart()

        while(opModeIsActive()){
            telemetry.addData("Triangle count: ", triDetector.TRIANGLES.size)
            telemetry.update()
        }

        triDetector.disable()
    }
}**/