package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.content.Context
import com.qualcomm.robotcore.hardware.HardwareMap
import java.io.*
import java.util.*

class TimeStampedData {

    class DataStream(val fileName : String, val hardware : HardwareMap) {

        private val data = ArrayList<DataPoint>()
        private var iterableData: Iterator<DataPoint>? = null
        private var pointBuffer : DataPoint? = null

        fun load() {
            val datastream = ObjectInputStream(hardware.appContext.openFileInput(fileName))

            do {
                try {
                    data.add(datastream.readObject() as DataPoint)
                } catch ( e : EOFException ) { break }
            }while(true)

            datastream.close()
        }

        fun write() {
            val datastream = ObjectOutputStream(hardware.appContext.openFileOutput(fileName, Context.MODE_PRIVATE))

            data.forEach { datastream.writeObject(it) }

            datastream.close()
        }

        fun prepare() {
            iterableData = data.iterator()
        }

        fun nextPoint() : DataPoint? {
            if(iterableData == null && data.isNotEmpty()) prepare()
            else return null

            return if(pointBuffer != null) pointBuffer
            else iterableData?.next()
        }

        fun pointsUntil(time : Double) : Pair<List<DataPoint>, Boolean> {
            val retList = ArrayList<DataPoint>()

            do {
                val current = nextPoint()
                if(current != null) {
                    if(current.time > time) {
                        pointBuffer = current
                        break
                    }
                    else retList.add(current)
                }else break
            }while(true)

            return Pair(retList, hasFinished())
        }

        fun hasFinished() : Boolean = (iterableData?.hasNext() != true)

        fun newPoint(time : Double, bytes : ArrayList<DataByte>) : DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull(), bytes)
            data.add(point)
            return point
        }

        fun newPoint(time : Double) : DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull())
            data.add(point)
            return point
        }

        private infix fun Double.getDelta(point : DataPoint?) : Double = this - (point?.time ?: this)


        fun trim() {
            @Suppress("UNCHECKED_CAST")
            var newData = data.clone() as MutableList<DataPoint>

            var trimPointStart = -1
            var trimPointEnd = -1

            for (i in 0 until data.lastIndex) { // go from beginning to 1 less than the last index so that there can be a 'next' point always
                val current = data[i]
                val next = data[i + 1]

                if (!current.isSimilar(next)) {
                    trimPointStart = i //we will trim to this point NOT INCLUSIVE
                    break
                }
            }
            if (trimPointStart > 0)
                newData = newData.subList(trimPointStart, newData.lastIndex)

            for (i in newData.lastIndex..1) {
                val current = data[i]
                val last = data[i - 1]

                if (!current.isSimilar(last)) {
                    trimPointEnd = i
                    break
                }
            }

            if (trimPointEnd > 0)
                newData.subList(0, trimPointEnd)

            data.clear()
            var timeT = 0.0
            for(d in newData) {
                data.add(DataPoint(timeT, d.timeDelta, d.bytes))
                timeT += d.time

            }
        }
    }

    class DataPoint(val time: Double, val timeDelta: Double, val bytes: ArrayList<DataByte> = ArrayList()) : Serializable {
        fun addByte(data: DataByte): DataPoint {
            bytes.add(data)
            return this
        }

        fun isSimilar(point : DataPoint) : Boolean {
            bytes.forEach { if(!point.bytes.contains(it)) return false }
            return true
        }
    }

    class DataByte(val name: String, val data: List<Double>) : Serializable
}