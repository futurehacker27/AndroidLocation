package com.mrcompexpert.loc.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mrcompexpert.loc.OnLocation
import com.mrcompexpert.loc.locationupdate.LocationUpdate
import com.mrcompexpert.loc.simple.LastLocation
import com.mrcompexpert.loc.util.LOC_PER
import com.mrcompexpert.loc.util.hasPermission
import kotlinx.android.synthetic.main.demo_act.*
import java.util.*

class DemoActivity : AppCompatActivity() {

    private val rcLoc = 2222
    private val locUpdate by lazy { LocationUpdate(this).setUp() }

    private val lastLocation by lazy { LastLocation(applicationContext) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_act)

        locUpdate.listener = object : OnLocation {
            override fun onLocationError(msg: String) {
                updatedLocationTv.text = msg
            }

            override fun onLocationFound(loc: Location?) {
                updatedLocationTv.text = "loc : ${loc?.latitude}, ${loc?.longitude}\ntime : ${Date()}"
            }

        }

        lastLocation.listener = object : OnLocation {
            override fun onLocationError(msg: String) {
                lastLocationTv.text = msg
            }

            override fun onLocationFound(loc: Location?) {
                lastLocationTv.text = "loc : ${loc?.latitude}, ${loc?.longitude}"
            }

        }

        if (hasPermission(LOC_PER)) {
            locUpdate.startLocationRequest()
            lastLocation.getLastLocation()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOC_PER, rcLoc)
            } else {
                locUpdate.startLocationRequest()
                lastLocation.getLastLocation()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == rcLoc) {
            if (hasPermission(LOC_PER)) {
                locUpdate.startLocationRequest()
                lastLocation.getLastLocation()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locUpdate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing)
            locUpdate.stopLocationRequest()
    }

}