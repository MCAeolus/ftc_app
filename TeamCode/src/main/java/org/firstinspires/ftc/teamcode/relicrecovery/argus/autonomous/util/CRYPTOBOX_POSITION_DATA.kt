package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.util

/**
 * Created by Nathan.Smith.19 on 3/1/2018.
 */
class CRYPTOBOX_POSITION_DATA {

    enum class CryptoPlacement { OUTSIDE, INSIDE }
    enum class CryptoPart { CLOSE, CENTER, FAR }
    companion object {
        //inside
        val INNER_CLOSE_POSITION = 45.0
        val INNER_CENTER_POSITION = 62.0
        val INNER_FAR_POSITION = 80.0
        //outside
        val OUTSIDE_CLOSE_POSITION = 102.[[---
        0
        val OUTSIDE_CENTER_POSITION = 118.0
        val OUTSIDE_FAR_POSITION = 133.0

        val BACK_POS = 20.0

        fun getPosition(placement : CryptoPlacement, part : CryptoPart) : Double {
            if(placement == CryptoPlacement.INSIDE){
                when(part) {
                    CryptoPart.CLOSE -> return INNER_CLOSE_POSITION
                    CryptoPart.CENTER -> return INNER_CENTER_POSITION
                    CryptoPart.FAR -> return INNER_FAR_POSITION
                }
            }else {
                when(part) {
                    CryptoPart.CLOSE -> return OUTSIDE_CLOSE_POSITION
                    CryptoPart.CENTER -> return OUTSIDE_CENTER_POSITION
                    CryptoPart.FAR -> return OUTSIDE_FAR_POSITION
                }
            }
        }

        fun getClosestPosition(placement : CryptoPlacement, current_position : Double) : CryptoPart {
            var lowest_delta = 0

            val deltas =
                 listOf(
                    Math.abs(current_position - getPosition(placement, CryptoPart.CLOSE)),
                    Math.abs(current_position - getPosition(placement, CryptoPart.CENTER)),
                    Math.abs(current_position - getPosition(placement, CryptoPart.FAR))
            )

            for(i in 0 until deltas.size)
                if(deltas[i] < deltas[lowest_delta])lowest_delta = i

            when(lowest_delta) {
                0 -> return CryptoPart.CLOSE
                1 -> return CryptoPart.CENTER
                2 -> return CryptoPart.FAR
            }
            return CryptoPart.CENTER
        }
    }

}