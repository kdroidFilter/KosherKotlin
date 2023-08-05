package com.kosherjava.zmanim

sealed class ZmanOpinion<T>(val value: T) {

    /* One or both of these must be implemented! */
    open fun format(): String = format(true)
    open fun format(inEnglish: Boolean): String = format()

    object Unanimous: ZmanOpinion<Any?>(null) //only one, universal opinion

    data class FixedMinutes(val minutes: Int): ZmanOpinion<Int>(minutes) {
        override fun format() = "Day is $minutes minutes before sunrise / after sunset"
    }
    data class FixedMinutesFloat(val minutes: Float): ZmanOpinion<Float>(minutes) {
        override fun format() = "Day is $minutes minutes before sunrise / after sunset"
    }
    data class ZmaniyosMinutes(val minutes: Int): ZmanOpinion<Int>(minutes) {
        override fun format(inEnglish: Boolean) = buildString {
            append("Day is ")
            append(minutes)
            append(
                if(inEnglish) "Halachic Minutes"
                else          " זמניות "
            )
            append("minutes before sunrise / after sunset")
        }
    }
    data class Degrees(val degrees: Float): ZmanOpinion<Float>(degrees) {
        override fun format(): String = "Day is ${degrees}˚ below sunrise / sunset"
    }
    data class Authority(val name: String): ZmanOpinion<String>(name) {
        override fun format() = "According to $name"
        companion object {

            //ZmanOpinion authorities
            const val KOMARNO = "Shach/Komarno"
            const val AHAVAT_SHALOM = "Ahavat Shalom"
            const val RABEINU_TAM = "Rabeinu Tam"
            const val RABEINU_YONAH = "Rabeinu Yonah"
            const val RABEINU_TAM_DIVREI_YOSEF = "Rabeinu Tam acc. to Divrei Yosef"
            const val ATERET_TORAH = "Ateret Torah"
            const val FEINSTEIN = "Rabbi Moshe Feinstein"
            const val ARUCH_HASHULCHAN = "Aruch Hashulchan"
            const val SHULCHAN_ARUCH = "Shulchan Aruch"
            const val MAHARIL = "Maharil"
            const val GRA = "Vilna Gaon (Gr\"a)"
            const val MGA = "Magen Avraham"
            const val BAAL_HATANYA = "Ba'al HaTanya"
        }
    }
}