package com.mrcompexpert.loc.simple

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.mrcompexpert.loc.OnLocation
import com.mrcompexpert.loc.R
import com.mrcompexpert.loc.util.LOC_PER
import com.mrcompexpert.loc.util.hasPermission

class LastLocation(private val context: Context) {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var listener: OnLocation? = null

    /**
     * Get user last location from device.
     * Location can be null if no last location found.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))
    fun getLastLocation() {

        if (context.hasPermission(LOC_PER)) {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.let {
                        listener?.onLocationFound(it)
                    } ?: listener?.onLocationError(context.resources.getString(R.string.msg_loc_null))
                } else {
                    listener?.onLocationError(context.resources.getString(R.string.msg_req_fail))
                }
            }

        } else {
            listener?.onLocationError(context.resources.getString(R.string.msg_loc_per_missing))
        }

    }


}