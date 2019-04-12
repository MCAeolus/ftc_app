package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.json

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes.Autonomous
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.MecanumDrivetrain

object ConvertToGraphData {

    enum class ReplayPresets(val path : String, val start : String, val nom : String, val description : String) {
        CRATER_FAST("Autonomous/Crater/FAST/", "crater", "fast", "The goal of this autonomous mode is to quickly perform a single-sample run that drops from the lifter, places the totem and parks."),
        CRATER_SLOW("Autonomous/Crater/SLOW/", "crater", "slow", "The purpose of the slow autonomous mode is the same as the fast autonomous mode, but adds in a delay before going to place the totem in order to avoid collisions with another robot."),
        CRATER_NONE("Autonomous/Crater/NONE/", "crater", "none", "The none autonomous is a backup in-case there is any reason the robot can't or shouldn't run a full autonomous mode."),
        CRATER_DOUBLE("Autonomous/Crater/DOUBLE/", "crater", "double sample", "In the case that the alliance partner can't sample, this mode performs all the normal actions of an autonomous mode, but also claims the depot-side sample."),
        DEPOT_FAST("Autonomous/Depot/NORMAL/", "depot", "normal", "The normal depot operation is to unlatch from the lander, perform the depot sample field, place the team totem and park in the enemy crater.")

    }


    fun convert(preset : ReplayPresets, hardware : HardwareMap) : JsonObject {


        val jObject = JsonObject()
        val dataJArray = JsonArray()

        val streams = arrayOf(
                ReplayFile.DataStream(preset.path + Autonomous.SampleLocation.LEFT + ReplayFile.REPLAY_FILE_SUFFIX, hardware),
                ReplayFile.DataStream(preset.path + Autonomous.SampleLocation.CENTER + ReplayFile.REPLAY_FILE_SUFFIX, hardware),
                ReplayFile.DataStream(preset.path + Autonomous.SampleLocation.RIGHT + ReplayFile.REPLAY_FILE_SUFFIX, hardware)
        )


        for(stream in streams) {
            val pointArray = JsonArray()
            for(point in stream.getRawData()) {
                val drivetrainPoint = getDrivetrainByte(point)
                val resolvedPosition = drivetrainPoint.data[1] as Pose2d

                val pointObject = JsonObject()
                pointObject.addProperty("x", resolvedPosition.x())
                pointObject.addProperty("y", resolvedPosition.y())

                pointArray.add(pointObject)
            }


            dataJArray.add(pointArray)
        }

        jObject.addProperty("start", preset.start)
        jObject.addProperty("name", preset.nom)
        jObject.addProperty("description", preset.description)
        jObject.add("data", dataJArray)



        return jObject
    }

    fun convertAllPresets(hardware : HardwareMap) : List<JsonObject> {
        val jList = arrayListOf<JsonObject>()

        for(p in ReplayPresets.values()) jList.add(convert(p, hardware))

        return jList
    }

    private fun getDrivetrainByte(p : ReplayFile.DataPoint) : ReplayFile.DataByte {
        for(b in p.bytes) if(b.name == MecanumDrivetrain::class.simpleName) return b
        return ReplayFile.DataByte(MecanumDrivetrain::class.simpleName!!, listOf(Pose2d(0, 0, 0), Pose2d(0, 0, 0)))
    }
}