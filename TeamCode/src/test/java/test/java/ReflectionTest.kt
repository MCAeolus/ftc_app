package test.java

import org.junit.Test
import kotlin.reflect.full.memberFunctions

class ReflectionTest {


    class TestingClass {
        fun stop() {
            println("called")
        }
    }


    @Test
    fun canCheckFunctionByName() {

        val claz = TestingClass()

        for(func in claz::class.memberFunctions) if(func.name == "stop") func.call(claz)

    }
}