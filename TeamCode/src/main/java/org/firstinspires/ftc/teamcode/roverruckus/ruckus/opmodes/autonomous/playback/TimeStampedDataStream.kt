package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import java.io.*
import java.lang.IllegalArgumentException
import java.util.*

class TimeStampedDataStream {


    class Data(val name : String, val timestamp : Double, val data : Array<Double>, val timeDelta : Double) : Serializable

    class Writer(output : OutputStream) {
        private var outputStream = ObjectOutputStream(output)
        private var pTimestamp = 0.0

        fun write(point : Data) {
            if(point.timestamp < pTimestamp) throw IllegalArgumentException("Time went backwards!") as Throwable

            outputStream.writeObject(point)
            pTimestamp = point.timestamp
        }
    }

    class Reader(input : InputStream) {
        private var inputStream = ObjectInputStream(input)
        private var nextPoint : Data? = null
        private var endOfStream : Boolean = false

        fun read() : Data? {
            if(nextPoint != null){
                val ret = nextPoint!!
                nextPoint = null
                return ret
            }

            return try {
                inputStream.readObject() as Data
            }catch(e : EOFException) {
                endOfStream = true
                null
            }
        }

        fun readUntil(time : Double) : List<Data> {
            val dataList = ArrayList<Data>()

            while(true){
                val d = read() ?: return dataList
                if(d.timestamp < time) dataList.add(d)
                else {
                    nextPoint = d
                    return dataList
                }
            }
        }

        fun fileDone() : Boolean {
            return nextPoint == null && endOfStream
        }
    }

}