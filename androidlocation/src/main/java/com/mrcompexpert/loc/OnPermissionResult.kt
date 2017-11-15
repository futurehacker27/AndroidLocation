package com.mrcompexpert.loc

interface OnPermissionResult {
    fun onPermissionResult(rq: Int, granted: Boolean)
}