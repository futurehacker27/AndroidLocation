package com.mrcompexpert.loc.util

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

const val RC_SETTINGS = 199
const val EXTRA_DATA = "extra_data"
const val EXTRA_RESULT = "extra_result"
const val RC_PERM = 111
const val RC_PERM_ACT = 112

val LOC_PER : Array<String> = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

@TargetApi(Build.VERSION_CODES.M)
fun Context.hasPermission(perms: Array<String>): Boolean {
    var result = true
    for (per in perms)
        result = result && checkSelfPermission(per) == PackageManager.PERMISSION_GRANTED
    return result
}