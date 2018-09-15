package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous

//import com.disnodeteam.dogecv.detectors.CryptoboxDetector
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.*

/**
 * Created by Nathan.Smith.19 on 11/2/2017.
 */
//@Autonomous(name="Autonomous(RED-1)", group="DEV")
class AutonDevRed1 : AutonomousBase()
      {

          override fun runOpMode() {
              super.runOpMode()

              var vumark = VU_SECT.update()
              while(vumark == RelicRecoveryVuMark.UNKNOWN && !isStarted()){
                  vumark = VU_SECT.update()
              }

              telemetry.addData("Vu", vumark.name)
              telemetry.update()
              waitForStart()

              val z_zero = IMU.initialOrientation.thirdAngle
              VU_SECT.deactivate()


              /**
              cryptoDetector.detectionMode = CryptoboxDetector.CryptoboxDetectionMode.HSV_RED
              cryptoDetector.downScaleFactor = 0.6
              cryptoDetector.speed = CryptoboxDetector.CryptoboxSpeed.SLOW
              cryptoDetector.enable()
                **/
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

              drive(1.0, 0.0, 0.0, 0.95, 1200)

              GlI.lift(true, false, true)
              hold(800)
              GlI.lift(false, false, true)
              hold(800)
              GlI.lift(false, false, false)
              reposition(z_zero + 90, 0.3)
              hold(100)
              drive(0.0, -1.0, 0.0, 0.6, 450)


              /**val cipherPos = cryptoDetector.cryptoBoxPositions[
                      (if(     vumark == RelicRecoveryVuMark.LEFT) 0
                       else if(vumark == RelicRecoveryVuMark.RIGHT)2
                       else 1)]**/
              //cryptoDetector.disable()

              hold(100)

              //val turnDegrees = getTurnDegrees(cipherPos, 1.0) //1.0 meters
              //reposition(IMU.XYZ().thirdAngle - turnDegrees, 0.2)
              //hold(200)

              GlI.drop(true)

              //ColorCounter.countSides(vumark, DRIVETRAIN, true)

              GlI.drop(false)

              drive(0.0, 1.0, 0.0, 0.5, 150)
              GlI.setDoors(GlyphIntake.DoorState.OPEN)
              hold(400)
              expel_gli()
              val HOLD = ElapsedTime()
              HOLD.reset()
              var hold1 = 0
              while(HOLD.milliseconds() < 5000){
                  hold1++
                  if((hold1 > 1000)) {
                      kick()
                      hold1 = 0
                  }
              }
              drive(0.0, 1.0, 0.0, 0.5, 100)
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

          fun choose1(x : RelicRecoveryVuMark): Float {
              when(x){
                  RelicRecoveryVuMark.LEFT -> return 80F
                  RelicRecoveryVuMark.CENTER -> return 60F
                  RelicRecoveryVuMark.RIGHT -> return 40F
              }
              return 60F
          }



}