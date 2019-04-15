package test.java

import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry.LoggedField
import org.junit.Test
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class RuckusTelemetryTest {


    class TestingData {

        @LoggedField(description = "some data example")
        private val someData = 1.0

        @LoggedField
        var test2 = 0.5

        @LoggedField
        private var sx = "hi"

        @LoggedField
        private val x = true

        @LoggedField
        private var pos = Pose2d(0.4, 3.0, 33.0)
    }


    @Test
    fun transformData() {

        val map = convertToMap(TestingData())

        println("size: ${map.size}")

        for( p in map ) println(p.key + "; " + p.value)

    }

    fun convertToMap(o : Any) : LinkedHashMap<String, Any> {
        val retMap = linkedMapOf<String, Any>()
        for(f in o::class.memberProperties) {
            val ann = f.findAnnotation<LoggedField>()
            println((ann != null).toString() + " :: null check")
            if(ann != null) {
                f.isAccessible = true
                val value = f.getter.call(o).toString()
                val name = if(ann.description == "") f.name else ann.description
                retMap[name] = value
            }
        }
        return retMap
    }
}