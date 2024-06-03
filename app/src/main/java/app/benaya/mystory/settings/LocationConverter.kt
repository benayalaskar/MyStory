package app.benaya.mystory.settings

import java.lang.Exception
import java.lang.StringBuilder
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng


@Suppress("DEPRECATION")
class LocationConverter {
    companion object {
        fun getStringAddress(
            latlng: LatLng?,
            context: Context
        ): String {
            var location = "Location Not Found !"

            try {
                if (latlng != null) {
                    val address: Address?
                    val gc = Geocoder(context)
                    val list: List<Address> =
                        gc.getFromLocation(latlng.latitude, latlng.longitude, 1) as List<Address>
                    address = if (list.isNotEmpty()) list[0] else null

                    if (address != null) {
                        val city = address.locality
                        val state = address.adminArea
                        val country = address.countryName

                        location = address.getAddressLine(0)
                            ?: if (city != null && state != null && country != null) {
                                StringBuilder(city).append(", $state").append(",$country")
                                    .toString()
                            } else if (state != null && country != null) {
                                StringBuilder(state).append(",$country").toString()
                            } else country ?: "Location Not Found Name"
                    }

                }
            } catch (e: Exception) {
                Log.d("Error Converting!", "$e")
            }
            return location
        }

        fun convertLatLng(latitude: Double?, longtitude: Double?): LatLng? {
            return if (latitude != null && longtitude != null) {
                LatLng(latitude, longtitude)

            } else null
        }
    }
}