package com.mrcompexpert.loc.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mrcompexpert.loc.OnLocation
import com.mrcompexpert.loc.locationupdate.LocationUpdate
import com.mrcompexpert.loc.simple.LastLocation
import kotlinx.android.synthetic.main.demo_act.*
import java.util.*

class DemoActivity : AppCompatActivity() {

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

        locUpdate.startLocationRequest()
        lastLocation.getLastLocation()
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