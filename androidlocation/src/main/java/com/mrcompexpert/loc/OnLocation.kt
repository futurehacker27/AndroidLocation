package com.mrcompexpert.loc

import android.location.Location

interface OnLocation {
    fun onLocationFound(loc: Location)
    fun onLocationError(msg: String)
}