package com.mrcompexpert.loc.permission

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.mrcompexpert.loc.OnPermissionResult
import com.mrcompexpert.loc.util.EXTRA_DATA
import com.mrcompexpert.loc.util.EXTRA_RESULT
import com.mrcompexpert.loc.util.RC_PERM_ACT
import com.mrcompexpert.loc.util.hasPermission

class PermissionManager(private val activity: AppCompatActivity) {

    var listener: OnPermissionResult? = null
    var rq: Int = -1

    // Must pass onActivityResult from your activity to here
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_PERM_ACT -> {
                listener?.onPermissionResult(rq, data?.getBooleanExtra(EXTRA_RESULT, false) ?: false)
            }
        }
    }

    fun requestPermission(rq: Int, perms: Array<String>): Boolean {
        this.rq = rq
        val granted = activity.hasPermission(perms)
        if (!granted) {
            val intent = Intent(activity, PermissionActivity::class.java)
            intent.putExtra(EXTRA_DATA, perms)
            activity.startActivityForResult(intent, RC_PERM_ACT)
        }
        return granted
    }


}