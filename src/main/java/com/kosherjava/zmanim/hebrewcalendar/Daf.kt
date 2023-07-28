/*
 * Zmanim Java API
 * Copyright (C) 2011 - 2023 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA,
 * or connect to: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.kosherjava.zmanim.hebrewcalendar

/**
 * An Object representing a *daf* (page) in the [Daf Yomi](https://en.wikipedia.org/wiki/Daf_Yomi) cycle.
 *
 * @param masechtaNumber the *masechta* number in the order of the Daf Yomi to set as the current *masechta*.
 * @param daf the *daf* (page) number to set.
 *
 * @author  Eliyahu Hershfeld 2011 - 2023
 */
data class Daf(

    /**
     * The *masechta* number of the currently set *Daf*. The sequence is: Berachos, Shabbos, Eruvin,
     * Pesachim, Shekalim, Yoma, Sukkah, Beitzah, Rosh Hashana, Taanis, Megillah, Moed Katan, Chagigah, Yevamos, Kesubos,
     * Nedarim, Nazir, Sotah, Gitin, Kiddushin, Bava Kamma, Bava Metzia, Bava Basra, Sanhedrin, Makkos, Shevuos, Avodah
     * Zarah, Horiyos, Zevachim, Menachos, Chullin, Bechoros, Arachin, Temurah, Kerisos, Meilah, Kinnim, Tamid, Midos and
     * Niddah.
     */
    var masechtaNumber: Int,

    /**
     * The *daf* (page) number of the Daf Yomi.
     */
    var daf: Int
) {

    /**
     * Returns the transliterated name of the *masechta* (tractate) of the Daf Yomi. The list of *mashechtos*
     * is: Berachos, Shabbos, Eruvin, Pesachim, Shekalim, Yoma, Sukkah, Beitzah, Rosh Hashana, Taanis, Megillah, Moed Katan,
     * Chagigah, Yevamos, Kesubos, Nedarim, Nazir, Sotah, Gitin, Kiddushin, Bava Kamma, Bava Metzia, Bava Basra, Sanhedrin,
     * Makkos, Shevuos, Avodah Zarah, Horiyos, Zevachim, Menachos, Chullin, Bechoros, Arachin, Temurah, Kerisos, Meilah,
     * Kinnim, Tamid, Midos and Niddah.
     *
     * @return the transliterated name of the *masechta* (tractate) of the Daf Yomi such as Berachos.
     * @see .setMasechtaTransliterated
     */
    val masechtaTransliterated: String
        get() = masechtosBavliTransliterated[masechtaNumber]

    /**
     * Returns the *masechta* (tractate) of the Daf Yomi in Hebrew. The list is in the following format<br></br>
     * `["ברכות",
     * "שבת", "עירובין",
     * "פסחים", "שקלים", "יומא",
     * "סוכה", "ביצה", "ראש השנה",
     * "תענית", "מגילה", "מועד
     * קטן", "חגיגה", "יבמות",
     * "כתובות", "נדרים","נזיר",
     * "סוטה", "גיטין", "קידושין",
     * "בבא קמא", "בבא מציעא",
     * "בבא בתרא", "סנהדרין",
     * "מכות", "שבועות", "עבודה
     * זרה", "הוריות", "זבחים",
     * "מנחות", "חולין", "בכורות",
     * "ערכין", "תמורה", "כריתות",
     * "מעילה", "קינים", "תמיד",
     * "מידות", "נדה"]`.
     *
     * @return the *masechta* (tractate) of the Daf Yomi in Hebrew. As an example, it will return
     * ברכות
     * "" for Berachos.
     */
    val masechta: String
        get() = masechtosBavli[masechtaNumber]

    /**
     * Returns the transliterated name of the *masechta* (tractate) of the Daf Yomi in Yerushalmi. The list of
     * *mashechtos* is:
     * Berachos, Pe'ah, Demai, Kilayim, Shevi'is, Terumos, Ma'asros, Ma'aser Sheni, Chalah, Orlah, Bikurim,
     * Shabbos, Eruvin, Pesachim, Beitzah, Rosh Hashanah, Yoma, Sukah, Ta'anis, Shekalim, Megilah, Chagigah,
     * Moed Katan, Yevamos, Kesuvos, Sotah, Nedarim, Nazir, Gitin, Kidushin, Bava Kama, Bava Metzia,
     * Bava Basra, Shevuos, Makos, Sanhedrin, Avodah Zarah, Horayos, Nidah and No Daf Today.
     *
     * @return the transliterated name of the *masechta* (tractate) of the Daf Yomi such as Berachos.
     */
    val yerushalmiMasechtaTransliterated: String
        get() = yerushalmiMasechtosTransliterated[masechtaNumber]
    /**
     * Returns the Yerushalmi *masechta* (tractate) of the Daf Yomi in Hebrew. As an example, it will return
     * &#x05D1;&#x05E8;&#x05DB;&#x05D5;&#x05EA; for Berachos.
     *
     * @return the Yerushalmi *masechta* (tractate) of the Daf Yomi in Hebrew. As an example, it will return
     * &#x05D1;&#x05E8;&#x05DB;&#x05D5;&#x05EA; for Berachos.
     */
    val yerushalmiMasechta: String
        get() = yerushalmiMasechtos[masechtaNumber]



    companion object {
        //Bavli:

        var BRACHOS = "Berachos"
        var SHABBOS = "Shabbos"
        var ERUVIN = "Eruvin"
        var PESACHIM = "Pesachim"
        var SHEKALIM = "Shekalim"
        var YOMA = "Yoma"
        var SUKKAH = "Sukkah"
        var BEITZAH = "Beitzah"
        var ROSH_HASHANA = "Rosh Hashana"
        var TAANIS = "Ta'anis"
        var MEGILLAH = "Megillah"
        var MOED_KATAN = "Moed Katan"
        var CHAGIGAH = "Chagigah"
        var KESUBOS = "Kesubos"
        var KIDDUSHIN = "Kiddushin"
        var BAVA_KAMMA = "Bava Kamma"
        var MAKKOS = "Makkos"
        var HORIYOS = "Horiyos"
        var ZEVACHIM = "Zevachim"
        var MENACHOS = "Menachos"
        var CHULLIN = "Chullin"
        var BECHOROS = "Bechoros"
        var ARACHIN = "Arachin"
        var TEMURAH = "Temurah"
        var KERISOS = "Kerisos"
        var MEILAH = "Meilah"
        var KINNIM = "Kinnim"
        var TAMID = "Tamid"
        var MIDOS = "Midos"
        var NIDDAH = "Niddah"
        var YEVAMOS = "Yevamos"
        var NEDARIM = "Nedarim"
        var NAZIR = "Nazir"
        var SOTAH = "Sotah"
        var GITIN = "Gitin"
        var BAVA_METZIA = "Bava Metzia"
        var BAVA_BASRA = "Bava Basra"
        var SANHEDRIN = "Sanhedrin"
        var SHAVUOS = "Shevuos"
        var AVODAH_ZARA = "Avodah Zarah"

        //Yerushalmi specific:

        var PEAH = "Pe'ah"
        var DEMAI = "Demai"
        var KILAYIM = "Kilayim"
        var SHEVIIS = "Shevi'is"
        var TERUMOS = "Terumos"
        var MAASROS = "Ma'asros"
        var MAASER_SHENI = "Ma'aser Sheni"
        var CHALAH = "Chalah"
        var ORLAH = "Orlah"
        var BIKURIM = "Bikurim"

        var masechtosBavliTransliterated: Array<String> = arrayOf(
            BRACHOS, SHABBOS, ERUVIN, PESACHIM, SHEKALIM,
            YOMA, SUKKAH, BEITZAH, ROSH_HASHANA, TAANIS, MEGILLAH, MOED_KATAN, CHAGIGAH, YEVAMOS,
            KESUBOS, NEDARIM, NAZIR, SOTAH, GITIN, KIDDUSHIN, BAVA_KAMMA, BAVA_METZIA, BAVA_BASRA,
            SANHEDRIN, MAKKOS, SHAVUOS, AVODAH_ZARA, HORIYOS, ZEVACHIM, MENACHOS, CHULLIN, BECHOROS,
            ARACHIN, TEMURAH, KERISOS, MEILAH, KINNIM, TAMID, MIDOS, NIDDAH
        )

        /**
         * See [masechta].
         */
        val masechtosBavli: Array<String> = arrayOf(
            "\u05D1\u05E8\u05DB\u05D5\u05EA", "\u05E9\u05D1\u05EA",
            "\u05E2\u05D9\u05E8\u05D5\u05D1\u05D9\u05DF", "\u05E4\u05E1\u05D7\u05D9\u05DD",
            "\u05E9\u05E7\u05DC\u05D9\u05DD", "\u05D9\u05D5\u05DE\u05D0", "\u05E1\u05D5\u05DB\u05D4",
            "\u05D1\u05D9\u05E6\u05D4", "\u05E8\u05D0\u05E9 \u05D4\u05E9\u05E0\u05D4",
            "\u05EA\u05E2\u05E0\u05D9\u05EA", "\u05DE\u05D2\u05D9\u05DC\u05D4",
            "\u05DE\u05D5\u05E2\u05D3 \u05E7\u05D8\u05DF", "\u05D7\u05D2\u05D9\u05D2\u05D4",
            "\u05D9\u05D1\u05DE\u05D5\u05EA", "\u05DB\u05EA\u05D5\u05D1\u05D5\u05EA", "\u05E0\u05D3\u05E8\u05D9\u05DD",
            "\u05E0\u05D6\u05D9\u05E8", "\u05E1\u05D5\u05D8\u05D4", "\u05D2\u05D9\u05D8\u05D9\u05DF",
            "\u05E7\u05D9\u05D3\u05D5\u05E9\u05D9\u05DF", "\u05D1\u05D1\u05D0 \u05E7\u05DE\u05D0",
            "\u05D1\u05D1\u05D0 \u05DE\u05E6\u05D9\u05E2\u05D0", "\u05D1\u05D1\u05D0 \u05D1\u05EA\u05E8\u05D0",
            "\u05E1\u05E0\u05D4\u05D3\u05E8\u05D9\u05DF", "\u05DE\u05DB\u05D5\u05EA",
            "\u05E9\u05D1\u05D5\u05E2\u05D5\u05EA", "\u05E2\u05D1\u05D5\u05D3\u05D4 \u05D6\u05E8\u05D4",
            "\u05D4\u05D5\u05E8\u05D9\u05D5\u05EA", "\u05D6\u05D1\u05D7\u05D9\u05DD", "\u05DE\u05E0\u05D7\u05D5\u05EA",
            "\u05D7\u05D5\u05DC\u05D9\u05DF", "\u05D1\u05DB\u05D5\u05E8\u05D5\u05EA", "\u05E2\u05E8\u05DB\u05D9\u05DF",
            "\u05EA\u05DE\u05D5\u05E8\u05D4", "\u05DB\u05E8\u05D9\u05EA\u05D5\u05EA", "\u05DE\u05E2\u05D9\u05DC\u05D4",
            "\u05E7\u05D9\u05E0\u05D9\u05DD", "\u05EA\u05DE\u05D9\u05D3", "\u05DE\u05D9\u05D3\u05D5\u05EA",
            "\u05E0\u05D3\u05D4"
        )
        /**
         * Getter method to allow retrieving the list of Yerushalmi *masechtos* transliterated into into Latin chars.
         * The default uses Ashkenazi American English transliteration.
         *
         * @return the array of transliterated *masechta* (tractate) names of the Daf Yomi Yerushalmi.
         */
        /**
         * See [.getYerushalmiMasechtaTransliterated].
         */
        var yerushalmiMasechtosTransliterated: Array<String> = arrayOf(
            BRACHOS, PEAH, DEMAI, KILAYIM, SHEVIIS,
            TERUMOS, MAASROS, MAASER_SHENI, CHALAH, ORLAH, BIKURIM, SHABBOS, ERUVIN, PESACHIM,
            BEITZAH, ROSH_HASHANA, YOMA, SUKKAH, TAANIS, SHEKALIM, MEGILLAH, CHAGIGAH, MOED_KATAN,
            YEVAMOS, KESUBOS, SOTAH, NEDARIM, NAZIR, GITIN, KIDDUSHIN, BAVA_KAMMA, BAVA_METZIA,
            BAVA_BASRA, SHAVUOS, MAKKOS, SANHEDRIN, AVODAH_ZARA, HORIYOS, NIDDAH, "No Daf Today"
        )
        /**
         * Getter method to allow retrieving the list of Yerushalmi *masechtos*.
         *
         * @return the array of Hebrew *masechta* (tractate) names of the Daf Yomi Yerushalmi.
         */
        /**
         * See [.getYerushalmiMasechta].
         */
        val yerushalmiMasechtos: Array<String> = arrayOf(
            "\u05d1\u05e8\u05db\u05d5\u05ea",
            "\u05e4\u05d9\u05d0\u05d4",
            "\u05d3\u05de\u05d0\u05d9",
            "\u05db\u05dc\u05d0\u05d9\u05dd",
            "\u05e9\u05d1\u05d9\u05e2\u05d9\u05ea",
            "\u05ea\u05e8\u05d5\u05de\u05d5\u05ea",
            "\u05de\u05e2\u05e9\u05e8\u05d5\u05ea",
            "\u05de\u05e2\u05e9\u05e8 \u05e9\u05e0\u05d9",
            "\u05d7\u05dc\u05d4",
            "\u05e2\u05d5\u05e8\u05dc\u05d4",
            "\u05d1\u05d9\u05db\u05d5\u05e8\u05d9\u05dd",
            "\u05e9\u05d1\u05ea",
            "\u05e2\u05d9\u05e8\u05d5\u05d1\u05d9\u05df",
            "\u05e4\u05e1\u05d7\u05d9\u05dd",
            "\u05d1\u05d9\u05e6\u05d4",
            "\u05e8\u05d0\u05e9 \u05d4\u05e9\u05e0\u05d4",
            "\u05d9\u05d5\u05de\u05d0",
            "\u05e1\u05d5\u05db\u05d4",
            "\u05ea\u05e2\u05e0\u05d9\u05ea",
            "\u05e9\u05e7\u05dc\u05d9\u05dd",
            "\u05de\u05d2\u05d9\u05dc\u05d4",
            "\u05d7\u05d2\u05d9\u05d2\u05d4",
            "\u05de\u05d5\u05e2\u05d3 \u05e7\u05d8\u05df",
            "\u05d9\u05d1\u05de\u05d5\u05ea",
            "\u05db\u05ea\u05d5\u05d1\u05d5\u05ea",
            "\u05e1\u05d5\u05d8\u05d4",
            "\u05e0\u05d3\u05e8\u05d9\u05dd",
            "\u05e0\u05d6\u05d9\u05e8",
            "\u05d2\u05d9\u05d8\u05d9\u05df",
            "\u05e7\u05d9\u05d3\u05d5\u05e9\u05d9\u05df",
            "\u05d1\u05d1\u05d0 \u05e7\u05de\u05d0",
            "\u05d1\u05d1\u05d0 \u05de\u05e6\u05d9\u05e2\u05d0",
            "\u05d1\u05d1\u05d0 \u05d1\u05ea\u05e8\u05d0",
            "\u05e9\u05d1\u05d5\u05e2\u05d5\u05ea",
            "\u05de\u05db\u05d5\u05ea",
            "\u05e1\u05e0\u05d4\u05d3\u05e8\u05d9\u05df",
            "\u05e2\u05d1\u05d5\u05d3\u05d4 \u05d6\u05e8\u05d4",
            "\u05d4\u05d5\u05e8\u05d9\u05d5\u05ea",
            "\u05e0\u05d9\u05d3\u05d4",
            "\u05d0\u05d9\u05df \u05d3\u05e3 \u05d4\u05d9\u05d5\u05dd"
        )
    }
}