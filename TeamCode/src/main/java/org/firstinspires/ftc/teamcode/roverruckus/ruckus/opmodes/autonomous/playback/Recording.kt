package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import java.io.InputStream
import java.io.OutputStream
import java.sql.Time

class Recording {

    class Record(private val hardware : HardwareMap, output : OutputStream) {

        private val writer = TimeStampedDataStream.Writer(output)

        var imuQueue = ArrayList<TimeStampedDataStream.Data>()
        var dcQueue = ArrayList<TimeStampedDataStream.Data>()
        var servoQueue = ArrayList<TimeStampedDataStream.Data>()

        fun queue(deviceName : String, time : Double) {
            val device = hardware.get(deviceName)
            when (device) {
                is DcMotor -> {
                    val toRecord = TimeStampedDataStream.Data(deviceName, time, arrayOf(device.power, device.currentPosition.toDouble()), getTimeDelta(time, dcQueue))
                    dcQueue.add(toRecord)
                }
                is Servo -> {
                    val toRecord = TimeStampedDataStream.Data(deviceName, time, arrayOf(device.position), getTimeDelta(time, servoQueue))
                    servoQueue.add(toRecord)
                }
                is BNO055IMU -> {
                    val toRecord = TimeStampedDataStream.Data(deviceName, time, arrayOf(device.angularOrientation.toAxesOrder(AxesOrder.XYZ).thirdAngle.toDouble()), getTimeDelta(time, imuQueue))
                    imuQueue.add(toRecord)
                }
            }
        }

        private fun getTimeDelta(time : Double, l : List<TimeStampedDataStream.Data>) = if(l.lastOrNull() == null) 0.0 else time - (l.lastOrNull() as TimeStampedDataStream.Data).timestamp

        fun recordQueue() {
            for(i in 0 until imuQueue.size) {
                writer.write(imuQueue[i])
                writer.write(dcQueue[i])
                writer.write(servoQueue[i])
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
                            //TODO probably won't be completed. The data should be used in an OpMode context, where it can be better applied.
                        }
                    }
            }
            return reader.fileDone()
        }

        fun playback_data(time : Double) : Pair<Array<TimeStampedDataStream.Data>, Boolean> {
            return Pair(reader.readUntil(time), reader.fileDone())
        }

    }
}