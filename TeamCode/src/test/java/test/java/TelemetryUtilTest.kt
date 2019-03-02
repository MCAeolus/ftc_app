package test.java

import org.firstinspires.ftc.teamcode.common.util.TelemetryUtil
import org.junit.Assert.*

class TelemetryUtilTest {

    class TestingUnitData {
        var something = 1
    }

    @org.junit.Test
    fun testConvertToMap() {
        val ma = TelemetryUtil.convertToMap(TestingUnitData())
        val expected = LinkedHashMap<String, Any>()
        expected["something"] = 1

        assertEquals("testing data works", expected, ma)
    }
}