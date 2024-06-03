package app.benaya.mystory.maps

import app.benaya.mystory.R
import app.benaya.mystory.databinding.ActivityPickMapBinding
import app.benaya.mystory.settings.LocationConverter
import app.benaya.mystory.settings.lightStatusBar
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class ActivityPickMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPickMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lightStatusBar(window, true)
        binding.btnPickLocation.visibility = View.GONE
        locationClick()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        getMyLastLocation()

        mMap.setOnMapClickListener {
            binding.btnPickLocation.visibility = View.VISIBLE
            locationPick = it
            val markerOptions = MarkerOptions()
            markerOptions.position(it)

            markerOptions.title(LocationConverter.getStringAddress(it, this))
            mMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 17f
            )
            mMap.animateCamera(location)
            mMap.addMarker(markerOptions)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.ERROR_PERMISSIONS),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    mMap.isMyLocationEnabled = true
                    showCurrentLocation(location)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.USE_LOCATION_NOTFOUND),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    mMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation(), DEFAULT_ZOOM)
                    )
                    mMap.isMyLocationEnabled = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun locationClick() {
        binding.apply {
            btnPickmyLocation.setOnClickListener {
                showAlertDialog(currentLocation)
            }

            btnPickLocation.setOnClickListener {
                showAlertDialog(locationPick)
            }
        }
    }

    private fun showAlertDialog(location: LatLng?) {
        val address = LocationConverter.getStringAddress(location, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(resources.getString(R.string.USE_LOCATION))
            .setMessage(address)
            .setPositiveButton(resources.getString(R.string.AGREE)) { _, _ ->
                resultData(address, location)
            }
            .setNegativeButton(resources.getString(R.string.DISAGREE)) { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun resultData(address: String, location: LatLng?) {
        val intent = Intent()
        intent.putExtra("address", address)
        intent.putExtra("lat", location?.latitude)
        intent.putExtra("lng", location?.longitude)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun defaultLocation() = LatLng(0.0, 0.0)

    private fun showCurrentLocation(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title(getString(R.string.USE_POINT_STARTS))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM))
    }

    companion object {
        var currentLocation: LatLng? = null
        var locationPick: LatLng? = null
        const val DEFAULT_ZOOM = 17.0f
    }
}