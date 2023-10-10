package com.kosherjava.zmanim

/**
 * A [ZmanAuthority] is also a [ZmanCalculationMethod] so that it is possible to say (for example) that
 * a zman calculated using [ZmanCalculationMethod.ZmaniyosDuration._72] for [ZmanType.ALOS] and [ZmanType.SHAA_ZMANIS] of the [ZmanAuthority.GRA].
 * This is because if the zman is based on [sha'os zmaniyos][ZmanType.SHAA_ZMANIS], it is unclear by what calculation
 * the sha'ah zmanis is calculated. And, sometimes authority's calculations cannot be easily described in terms of simple [ZmanCalculationMethod]s.
 * It is also more meaningful to the reader/end user that it uses the GRA's sha'ah zmanis rather than a calculation method.
 * */
open class ZmanAuthority(val name: String): ZmanCalculationMethod<String>(name) {
    data class AccordingTo(val authority: ZmanAuthority, val accordingTo: ZmanAuthority): ZmanAuthority("${authority.name} according to ${accordingTo.name}")

    override fun format() = "According to $name"

    companion object {
        val RABEINU_TAM_DIVREI_YOSEF = AccordingTo(RABEINU_TAM, DIVREI_YOSEF)
    }
    object Unanimous: ZmanAuthority(Strings.Unanimous) //only one, universal opinion / "The widely accepted or only opinion"

    object AHAVAT_SHALOM: ZmanAuthority(Strings.AHAVAT_SHALOM)
    object ARUCH_HASHULCHAN: ZmanAuthority(Strings.ARUCH_HASHULCHAN)
    object ATERET_TORAH: ZmanAuthority(Strings.ATERET_TORAH)
    object BAAL_HATANYA: ZmanAuthority(Strings.BAAL_HATANYA)
    object BICK: ZmanAuthority(Strings.BICK)
    object CHASSAN_SOFER: ZmanAuthority(Strings.CHASSAN_SOFER)
    object CHAVAS_YAIR: ZmanAuthority(Strings.CHAVAS_YAIR)
    object DIVREI_MALKIEL: ZmanAuthority(Strings.DIVREI_MALKIEL)
    object DIVREI_YOSEF: ZmanAuthority(Strings.DIVREI_YOSEF)
    object EDELSTEIN: ZmanAuthority(Strings.EDELSTEIN)
    object EIDOT_HAMIZRACH: ZmanAuthority(Strings.EIDOT_HAMIZRACH)
    object FEINSTEIN: ZmanAuthority(Strings.FEINSTEIN)
    object GEONIM: ZmanAuthority(Strings.GEONIM)
    object GRA: ZmanAuthority(Strings.GRA)
    object GREENWALD: ZmanAuthority(Strings.GREENWALD)
    object HISACHDUS_HARABONIM: ZmanAuthority(Strings.HISACHDUS_HARABONIM)
    object KAMENETSKY: ZmanAuthority(Strings.KAMENETSKY)
    object KOMARNO: ZmanAuthority(Strings.KOMARNO)
    object KRONGLASS: ZmanAuthority(Strings.KRONGLASS)
    object LECHEM_MISHNAH: ZmanAuthority(Strings.LECHEM_MISHNAH)
    object MAAGALEI_TZEDEK: ZmanAuthority(Strings.MAAGALEI_TZEDEK)
    object MAHARIK: ZmanAuthority(Strings.MAHARIK)
    object MAHARIL: ZmanAuthority(Strings.MAHARIL)
    object MGA: ZmanAuthority(Strings.MGA)
    object NEIMAN: ZmanAuthority(Strings.NEIMAN)
    object RAAVAN: ZmanAuthority(Strings.RAAVAN)
    object RABEINU_TAM: ZmanAuthority(Strings.RABEINU_TAM)
    object RABEINU_YONAH: ZmanAuthority(Strings.RABEINU_YONAH)

    object RAMBAM: ZmanAuthority(Strings.RAMBAM)
    object RAZEH: ZmanAuthority(Strings.RAZEH)
    object SATMER: ZmanAuthority(Strings.SATMER)
    object SHACH: ZmanAuthority(Strings.SHACH)
    object SHVUS_YAAKOV: ZmanAuthority(Strings.SHVUS_YAAKOV)
    object SHULCHAN_ARUCH: ZmanAuthority(Strings.SHULCHAN_ARUCH)
    object SHAKOW: ZmanAuthority(Strings.SHAKOW)
    object SHOR: ZmanAuthority(Strings.SHOR)
    object WEISS: ZmanAuthority(Strings.WEISS)
    object YAAVETZ: ZmanAuthority(Strings.YAAVETZ)
    object ZILBER: ZmanAuthority(Strings.ZILBER)

    object Strings {
        //zman authorities

        const val Unanimous = "Rabbi Yaakov Moshe Hillel (Yeshivat Chevrat Ahavat Shalom)"

        const val AHAVAT_SHALOM = "Rabbi Yaakov Moshe Hillel (Yeshivat Chevrat Ahavat Shalom)"
        const val ARUCH_HASHULCHAN = "Aruch Hashulchan"
        const val ATERET_TORAH = "Ateret Torah"
        const val BAAL_HATANYA = "Ba'al HaTanya"
        const val BICK = "Rabbi Avraham Yehoshua Bick"
        const val CHASSAN_SOFER = "Chassan Sofer"
        const val CHAVAS_YAIR = "Chavas Yair"
        const val DIVREI_YOSEF = "Divrei Yosef"
        const val DIVREI_MALKIEL = "Divrei Malkiel"
        const val EDELSTEIN = "Rabbi Hershel Edelstein"
        const val EIDOT_HAMIZRACH = "Calendars of Eidot Hamizrach"
        const val FEINSTEIN = "Rabbi Moshe Feinstein"
        const val GEONIM = "The Geonim"
        const val GRA = "Vilna Gaon (Gr\"a)"
        const val GREENWALD = "The Rebbe of Pupa - Rabbi Yosef Greenwald (Shu\"t Vaya'an Yosef)"
        const val HISACHDUS_HARABONIM = "Hisachdus Harabanim D'Artzos Habris Ve'Canada"
        const val KAMENETSKY = "Rabbi Shmuel Kamenetsky"
        const val KOMARNO = "Shach/Komarno"
        const val KRONGLASS = "Rabbi Dovid Kronglass (Divrei Chachamim)"
        const val LECHEM_MISHNAH = "Lechem Mishnah"
        const val MAAGALEI_TZEDEK = "Ma'agalei Tzedek"
        const val MAHARIK = "Maharik"
        const val MAHARIL = "Maharil"
        const val MGA = "Magen Avraham"
        const val NEIMAN = "Rabbi Yaakov Yitzchok Neiman (Kovetz Eitz Chaim)"
        const val RAAVAN = "Ra'avan (R' Elazar Ben Nosson)"
        const val RABEINU_TAM = "Rabeinu Tam"
        const val RABEINU_YONAH = "Rabeinu Yonah"
        const val RABEINU_TAM_DIVREI_YOSEF = "Rabeinu Tam acc. to Divrei Yosef"
        const val RAMBAM = "Rambam"
        const val RAZEH = "Chidushei U'Klalos HaRaze\"h/Menorah HaTehorah"//"חידושי וכללות הרז״ה/מנורה הטהורה"
        const val SATMER = "The Satmer Rav"
        const val SHVUS_YAAKOV = "Rabbi Yaakov Reischer (Shvus Yaakov)"
        const val SHULCHAN_ARUCH = "Shulchan Aruch"
        const val SHACH = "The Shach"
        const val SHAKOW = "Rabbi Yaakov Shakow (Luach Ikvei Hayom)"
        const val SHOR = "Rabbi Dovid Shor (Ayeles Hashachar Al Zmanim U'Shiurim)"
        const val WEISS = "Rabbi Yaakov Gershon Weiss (Yom Valayla Shel Torah)"
        const val YAAVETZ = "Rabbi Yaakov Emden (Yaavetz)"
        const val ZILBER = "Rabbi Yechiel Avraham Zilber (Birur Halacha)"
    }
}