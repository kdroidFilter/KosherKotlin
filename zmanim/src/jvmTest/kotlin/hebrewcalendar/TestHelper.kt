package hebrewcalendar
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import kotlinx.datetime.TimeZone

object TestHelper {
    val lakewood = GeoLocation("Lakewood, NJ", 40.0721087, -74.2400243, 15.0, TimeZone.of("America/New_York"))
    val samoa = GeoLocation("Apia, Samoa", -13.8599098, -171.8031745, 1858.0, TimeZone.of("Pacific/Apia"))
    val jerusalem = GeoLocation("Jerusalem, Israel", 31.7781161, 35.233804, 740.0, TimeZone.of("Asia/Jerusalem"))
    val losAngeles = GeoLocation("Los Angeles, CA", 34.0201613, -118.6919095, 71.0, TimeZone.of("America/Los_Angeles"))
    val tokyo = GeoLocation("Tokyo, Japan", 35.6733227, 139.6403486, 40.0, TimeZone.of("Asia/Tokyo"))
    val arcticNunavut = GeoLocation("Fort Conger, NU Canada", 81.7449398, -64.7945858, 127.0, TimeZone.of("America/Toronto"))
    val basicLocations = listOf(lakewood, jerusalem, losAngeles, tokyo, arcticNunavut, samoa)

    val hooperBay = GeoLocation("Hooper Bay, Alaska", 61.520182, -166.1740437, 8.0, TimeZone.of("America/Anchorage"))
    val daneborg = GeoLocation("Daneborg, Greenland", 74.2999996, -20.2420877, 0.0, TimeZone.of("America/Godthab"))
    val arcticLocations = listOf(hooperBay, daneborg)

    val allLocations = basicLocations + arcticLocations
    val allJavaLocations = allLocations.map { it.toJava() }
    fun GeoLocation.toJava() =
        com.kosherjava.zmanim.util.GeoLocation(
            locationName,
            latitude,
            longitude,
            elevation,
            java.util.TimeZone.getTimeZone(timeZone.id)
        )
}