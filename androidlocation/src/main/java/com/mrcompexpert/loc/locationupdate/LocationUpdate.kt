package com.mrcompexpert.loc.locationupdate

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.support.annotation.RequiresPermission
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.mrcompexpert.loc.OnLocation
import com.mrcompexpert.loc.OnPermissionResult
import com.mrcompexpert.loc.R
import com.mrcompexpert.loc.permission.PermissionManager
import com.mrcompexpert.loc.util.LOC_PER
import com.mrcompexpert.loc.util.RC_SETTINGS
import com.mrcompexpert.loc.util.hasPermission

class LocationUpdate(private val activity: AppCompatActivity) {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }

    private val settingClient by lazy {
        LocationServices.getSettingsClient(activity)
    }

    private val locaSettingReq by lazy {
        LocationSettingsRequest.Builder().addLocationRequest(locReq).build()
    }

    private val locCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                p0?.let {
                    location = p0.lastLocation
                    listener?.onLocationFound(location)
                }

            }
        }
    }

    private val pm by lazy { PermissionManager(activity) }
    private val RC_PER = 222

    private lateinit var locReq: LocationRequest

    private var requestingLocationUpdate = false
    private var location: Location? = null

    var updateInterval = 3000L //Default 3s.
    var fastUpdateInt = 2000L // Degault 2s.
    var priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    var listener: OnLocation? = null


    /**
     * Init location request.
     */
    fun setUp(): LocationUpdate {
        // Build location request.
        locReq = LocationRequest()
        locReq.fastestInterval = fastUpdateInt
        locReq.interval = updateInterval
        locReq.priority = priority

        pm.listener = object : OnPermissionResult {
            override fun onPermissionResult(rq: Int, granted: Boolean) {
                if (granted) locaSettingReq else listener?.onLocationError(activity.resources.getString(R.string.msg_loc_per_missing))
            }

        }

        return this
    }

    // Must pass onActivityResult from your activity to here
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        pm.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SETTINGS -> {
                when (resultCode) {
                    RESULT_OK -> {
                        startLocationRequest()
                    }
                    RESULT_CANCELED -> {
                        requestingLocationUpdate = false
                        listener?.onLocationError(activity.getString(R.string.msg_loc_sett_disabled))
                    }
                }
            }
        }
    }

    /**
     * Start Location Update.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))
    fun startLocationRequest() {
        settingClient.checkLocationSettings(locaSettingReq)
                .addOnSuccessListener {
                    if (activity.hasPermission(LOC_PER)) {
                        fusedLocationClient.requestLocationUpdates(
                                locReq,
                                locCallback,
                                Looper.myLooper()
                        )
                        requestingLocationUpdate = true
                    } else {
                        pm.requestPermission(RC_PER, LOC_PER)
                    }
                }
                .addOnFailureListener {
                    val statusCode = (it as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val rae = (it as ResolvableApiException)
                                rae.startResolutionForResult(activity, RC_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                listener?.onLocationError(sie.localizedMessage)
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            requestingLocationUpdate = false
                            listener?.onLocationError(activity.getString(R.string.msg_loc_not_supp))
                        }
                    }

                }
    }

    /**
     * Stop Location Update.
     */
    fun stopLocationRequest() {
        if (!requestingLocationUpdate) return
        fusedLocationClient.removeLocationUpdates(locCallback)
                .addOnCompleteListener {
                    requestingLocationUpdate = false
                }
    }


}