package com.mrcompexpert.loc.util

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

val RC_SETTINGS = 199

val LOC_PER : Array<String> = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

@TargetApi(Build.VERSION_CODES.M)
fun Context.hasPermission(perms: Array<String>): Boolean {
    var result = true
    for (per in perms)
        result = result && checkSelfPermission(per) == PackageManager.PERMISSION_GRANTED
    return result
}