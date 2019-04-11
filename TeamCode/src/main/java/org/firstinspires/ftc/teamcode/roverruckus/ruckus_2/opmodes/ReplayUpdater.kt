package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.Intent.*
import android.util.Log
import com.google.gson.JsonElement
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.sun.tools.javac.util.FatalError
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.json.ConvertToGraphData
import java.io.File
import java.io.IOException
import java.util.*

@Autonomous(name="Update Replays")
class ReplayUpdater : LinearOpMode() {

    //val DEV_UUID = UUID.fromString("2036cdb1-416f-4fef-93f2-28a954b71efc")

    //var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    //val BLUETOOTH_DEVICE_ADDRESS = "44:85:00:7E:72:13"

    val defaultDir = "ReplayJSON"

    override fun runOpMode() {

        val baseDir = hardwareMap.appContext.getExternalFilesDir(ReplayFile.EXTERNAL_DIRECTORY_HEADING)

        if(!baseDir.list().contains(defaultDir)) File(baseDir, defaultDir).mkdirs()


        telemetry.addData("STATUS", "Converting replays to JSON.")
        telemetry.update()

        val jsons = ConvertToGraphData.convertAllPresets(hardwareMap)

        telemetry.addData("STATUS", "Begin writing to file.")
        telemetry.update()

        var it = 0
        for(j in jsons) {
            val file = File(baseDir, defaultDir + "/replay" + it++ + ".json")
            file.writeText(j.toString())
        }

        telemetry.addData("STATUS", "Done creating replays. ${it} total replays made.")
        telemetry.update()

        holdUntilStop()

        /**
        bluetoothAdapter.enable()

        while (!isStarted && !isStopRequested) send("STATUS", "Press 'play' to begin.")

        if(bluetoothAdapter.isEnabled) {

            while (opModeIsActive() && !isStopRequested) {

                val device = bluetoothAdapter.getRemoteDevice(BLUETOOTH_DEVICE_ADDRESS)
                bluetoothAdapter.cancelDiscovery()

                try {
                    val socket = device.createRfcommSocketToServiceRecord(DEV_UUID)
                    socket.connect()

                    send("STATUS", "Toasting server and stopping.")

                    val outStream = socket.outputStream

                    outStream.write("Hi".toByteArray())

                    send("STATUS", "Sent toast.")

                    send("STATUS", "Exited running code.")

                    socket.close()
                    break
                } catch (e: IOException) {

                    e.printStackTrace()
                }

            }//is enabled
        }

        holdUntilStop()
        bluetoothAdapter.disable()
        **/
    }


    private fun holdUntilStop() {
        while(!isStopRequested) {
            send("OPERATION", "Press STOP to end the operation mode.")
        }
    }

    private fun send(cap : String, desc : String) {
        telemetry.addData(cap, desc)
        telemetry.update()
    }


    private fun getFallbackSocket(socket : BluetoothSocket) : BluetoothSocket {
        try { //reflect to get to rfcomm method.
            val clazz = socket.remoteDevice.javaClass
            val method = clazz.getMethod("createRfcommSocket", Integer.TYPE)
            return method.invoke(socket.remoteDevice, 1) as BluetoothSocket
        } catch ( e : IOException ) {
            throw FatalError("Failure to create fallback.")
        }
    }
}
