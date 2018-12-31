package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import java.io.InputStream
import java.io.OutputStream

class Recording {

    class Record(private val hardware : HardwareMap, output : OutputStream) {

        private val writer = TimeStampedDataStream.Writer(output)

        fun record(deviceName : String, time : Double) {
            val device = hardware.get(deviceName)
            when (device) {
                is DcMotor -> writer.write(TimeStampedDataStream.Data(deviceName, time, arrayOf(device.power, device.currentPosition.toDouble())))
                is Servo -> writer.write(TimeStampedDataStream.Data(deviceName, time, arrayOf(device.position)))
                is BNO055IMU -> writer.write(TimeStampedDataStream.Data(deviceName, time, arrayOf(device.angularOrientation.toAxesOrder(AxesOrder.XYZ).thirdAngle.toDouble())))
            }
        }
    }

    class Play(input : InputStream, private val hardware : HardwareMap) {
        private val reader = TimeStampedDataStream.Reader(input)

        fun playback(time : Double) : Boolean {
            val data = reader.readUntil(time)
            for(d in data) {
                val device = hardware.get(d.name)
                if(device != null)
                    when(device) {
                        is DcMotor -> {
                            device.mode = DcMotor.RunMode.RUN_TO_POSITION
                            device.power = d.data[0]
                            device.targetPosition = d.data[1].toInt()
                        }
                        is Servo -> {
                            device.position = d.data[0]
                        }
                        is BNO055IMU -> {

                        }
                    }
            }
            return !reader.fileDone()
        }

    }
}