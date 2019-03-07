package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.acmerobotics.dashboard.config.Config

@Config
object RecordingPreferences {

    const val preferenceKey = "recordingpreferences";

    @JvmField
    var filePath : String = ""

    @JvmField
    var newDirectory : String = ""

    fun setFileDestination(loc : String, context : Context) {
        filePath = loc
        saveToPrefs(context)
    }

    fun loadFromPrefs(context : Context) {
        filePath = context.getSharedPreferences(preferenceKey, MODE_PRIVATE).getString("lastPath", "")
    }

    fun saveToPrefs(context : Context) {
        context.getSharedPreferences(preferenceKey, MODE_PRIVATE).edit().putString("lastPath", filePath).apply()
    }
}