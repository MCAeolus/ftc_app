package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_STREAM
import android.util.Log
import com.google.gson.JsonElement
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.json.ConvertToGraphData
import java.io.IOException
import java.util.*

@Autonomous(name="Send By Bluetooth")
class SendByBluetooth : LinearOpMode() {

    val APP_NAME = "4221 Transfer"
    val APP_UUID = UUID.fromString("2036cdb1-416f-4fef-93f2-28a954b71efc")

    var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private inner class BluetoothServerThread : Thread() {
        private val serverSocket : BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, APP_UUID)
        }

        override fun run() {
            var findingClient = true
            while(opModeIsActive() && findingClient) {
                val socket : BluetoothSocket? = try {
                    serverSocket?.accept()
                } catch (e : IOException) {
                    Log.e("BLUETOOTH","Did not accept socket.")
                    findingClient = false
                    null
                }
                socket?.also() {
                    sendData(it)
                    serverSocket?.close()
                    findingClient = false

                }
            }
        }

        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e : IOException) {
                Log.e("BLUETOOTH", "Failed to close connection.")
            }
        }

    }

    val BLUETOOTH_DEVICE_ADDRESS = "44:85:00:7E:72:13"

    override fun runOpMode() {

        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable()

            while (!isStopRequested && !isStarted) send("INFO", "Press play to begin searching.")

            val jsonList = ConvertToGraphData.convertAllPresets(hardwareMap)
            telemetry.log().add("Json list: " + jsonList.size)
            //send("STATUS", "All replays converted to JSON.")

            val intent = Intent()
            intent.action = ACTION_SEND
            intent.type = "text/plain"

            intent.putExtra(EXTRA_STREAM, "Hi.")
            //for(json in jsonList)
            //    intent.putExtra(EXTRA_STREAM, json.toString())

            hardwareMap.appContext.startActivity(intent)

            /**

            val serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, APP_UUID)

            var isDone = false

            while (opModeIsActive() && !isDone) {
                send("STATUS", "Waiting on connection.")

                val socket : BluetoothSocket? = try {
                    serverSocket.accept(1000)
                } catch (e : IOException ) {
                    Log.e("BLUETOOTH", "Failed to accept client.")
                    null
                } catch (e : Exception ) {
                    Log.e("BLUETOOTH", "alternate error $e")
                    null
                }
                socket.also {
                    sendData(it!!)
                    serverSocket.close()
                    send("STATUS", "Data sent.")
                    isDone = true
                }
            }
            serverSocket.close()
            **/
        } else
            send("ERROR", "Bluetooth adapter doesn't exist.")

        bluetoothAdapter?.disable()
        holdUntilStop()








        /*val adapter = BluetoothAdapter.getDefaultAdapter()
        val infoLine = telemetry.addLine("INFO")
        send("STATUS", "Starting..")

        if (adapter != null) {
            infoLine.addData("ADAPTER", "EXISTS")
            send("STATUS", "Adapter acquired.")
            adapter.enable()

            infoLine.addData("ENABLED", "TRUE")
            send("STATUS", "Adapter enabled.")
            val devices = adapter.bondedDevices
            var deviceFound = false
            var desiredDevice = if(devices.isEmpty()) null else devices.first()

            while(!deviceFound && !isStopRequested) {
                for( d in adapter.bondedDevices ) {
                    send(d.name, d.address)
                    if(d.address == BLUETOOTH_DEVICE_ADDRESS) {
                        desiredDevice = d
                        deviceFound = true
                    }
                }
            }

            if(desiredDevice != null) {

                val socket = desiredDevice.createRfcommSocketToServiceRecord(desiredDevice.uuids[0].uuid)

                if(socket != null) {
                    val outputStream = socket.outputStream

                    send("STATUS", "Establishing socket connection.")

                    while(!socket.isConnected && !isStopRequested)
                        try {
                            socket.connect()
                        } catch ( e : Exception ) {
                            Log.wtf("[BLUETOOTH]", e)
                        }

                    if(socket.isConnected) {

                        send("STATUS", "Socket initiated.")

                        val jsonList = ConvertToGraphData.convertAllPresets(hardwareMap)
                        telemetry.log().add("Json list: " + jsonList.size)
                        send("STATUS", "All replays converted to JSON.")

                        telemetry.log().add("stream is null? " + (outputStream == null))

                        outputStream.write("Hi".toByteArray())
                    }
                    else
                        send("STATUS", "Could not establish socket connection.")

                    // for (json in jsonList) outputStream.write("Hello!".toByteArray())
                    //send("STATUS", "${jsonList.size} JSON REPLAYS HAVE BEEN TRANSFERRED.")

                } else
                    send("STATUS", "Could not establish socket connection.")
            } else
                send("STATUS", "Device is null.")

        } else
            send("STATUS", "There is no Bluetooth adapter on this device.")

        adapter.disable()
        holdUntilStop()*/
    }

    private fun sendData(socket : BluetoothSocket) {
        val outputStream = socket.outputStream

        val jsonList = ConvertToGraphData.convertAllPresets(hardwareMap)
        telemetry.log().add("Json list: " + jsonList.size)
        send("STATUS", "All replays converted to JSON.")

        outputStream.write("Hi".toByteArray())
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
}