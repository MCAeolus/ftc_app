/**
package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous

import com.disnodeteam.dogecv.detectors.CryptoboxDetector
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.*
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot

/**
 * Created by Nathan.Smith.19 on 11/2/2017.
 */
@Autonomous(name="Autonomous(BLUE-1)", group="DEV")
class AutonDevBlue1 : AutonomousBase()
      {

          override fun runOpMode() {
              super.runOpMode()

              waitForStart()

              val z_zero = IMU.initialOrientation.thirdAngle
              VU_SECT.activate()
              val vumark = VU_SECT.update()
              VU_SECT.deactivate()
              //cryptoDetector.detectionMode = CryptoboxDetector.CryptoboxDetectionMode.HSV_BLUE
              cryptoDetector.downScaleFactor = 0.6
              cryptoDetector.speed = CryptoboxDetector.CryptoboxSpeed.SLOW
              cryptoDetector.enable()

              /**
              timer.reset()
              FISTER.extend(true)

              hold(750)

              if(C_S.RGB().B > C_S.RGB().R)
                  drive(0.0, 0.0, 1.0, 0.5, 200)
              else
                  drive(0.0, 0.0, -1.0, 0.5, 200) //moving CW
              FISTER.extend(false)
              **/

              drive(0.0, 1.0, 0.0, 1.0, 750)
              hold(200)
              reposition(z_zero - 90, 0.4)

              GlI.lift(true, true, false)  //release the bottom plate
              hold(900)
              GlI.lift(false, true, false)
              hold(900)
              GlI.lift(false, false, false)

              val cipherPos = cryptoDetector.cryptoBoxPositions[
                      (if(     vumark == RelicRecoveryVuMark.LEFT) 0
                       else if(vumark == RelicRecoveryVuMark.RIGHT)2
                       else 1)]
              cryptoDetector.disable()

              hold(100)

              //val turnDegrees = getTurnDegrees(cipherPos, 1.05) //1.05 meters
              //reposition(IMU.XYZ().thirdAngle - turnDegrees, 0.2)
              hold(200)

              drive(0.0, -1.0, 0.0, 1.0, 800)
              reposition(z_zero - 90)
              hold(200)

              expel_gli()
              hold(500)
              immobile_gli()
              hold(500)


              //
              // this may not be implemented by meet. end here if so.
              // also make sure to disable the glyph detector.
              //

              /**
              glyphDetector.enable()


              drive(0.0, 1.0, 0.0, 1.0, 200)
              reposition(z_zero + 25)

              //grab more glyphs

              //glyphDetector.


              **/
              linear_stop()
          }



}
 **/