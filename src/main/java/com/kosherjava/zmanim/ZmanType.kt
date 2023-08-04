package com.kosherjava.zmanim

enum class ZmanType(
    val friendlyNameEnglish: String,
    val friendlyNameHebrew: String
) {
    SHAA_ZMANIS("Halachic hour", "שעה זמנית"),
    CHATZOS_HALAYLAH("Halachic midnight", "חצות הלילה"),
    MISHMAR("Watch", "משמר"),
    ALOS("Dawn", "עלות השחר"),
    MISHEYAKIR("Earliest Tallis and Teffillin (Misheyakir)", " (משיכיר)טלית ותפילין הכי מוקדם"),
    HANAITZ("Sunrise", "הנץ החמה"),
    CHATZOS_HAYOM("Halachic midday", "חצות היום"),
    MINCHA_GEDOLAH("Greater Mincha", "מנחה גדולה"),
    MINCHA_KETANAH("Lesser Mincha", "מנחה קטנה"),
    PLAG_HAMINCHA("Plag Hamincha", "פלג המנחה"),
    SHKIAH("Sunset", "שקיעה"),
    BAIN_HASHMASHOS("Twilight", "בין השמשות"),
    TZAIS("Nightfall", "צאת הכוכבים"),
    MOLAD("New moon", "מולד"),

    //Related to rituals:
    SAMUCH_LEMINCHA_KETANA("Close to Mincha Ketana", "סמוך למנחה קטנה"),
    SOF_ZMAN_KRIAS_SHEMA("Latest time to say Shema", "סוף זמן קריאת שמע"),
    SOF_ZMAN_TEFILLAH("Latest time to daven Shacharis", "סוף זמן תפילה"),
    SOF_ZMAN_ACHILAS_CHAMETZ("Latest time to eat Chametz", "סוף זמן אכילת חמץ"),
    SOF_ZMAN_BIUR_CHAMETZ("Latest time to burn Chametz", "סוף זמן ביעור חמץ"),
    EARLIEST_MINCHA("Earliest Mincha", "מנחה הכי מוקדם"),
    EARLIEST_KIDDUSH_LEVANA("Earliest time to sanctify the moon", "תחילת זמן קידוש לבנה"),
    SOF_ZMAN_KIDDUSH_LEVANA("Latest time to sanctify the moon", "סוף זמן קידוש לבנה"),
    CANDLE_LIGHTING("Candle lighting", "הדלקת נרות")
}