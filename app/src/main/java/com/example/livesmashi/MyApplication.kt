package com.example.livesmashi

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.multidex.MultiDex

class MyApplication : Application() {

    lateinit var context:Context
    override fun onCreate() {
        super.onCreate()
        //initialize preference
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }



    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getDeviceModel(): String {
        return (Build.MANUFACTURER
                + " " + Build.MODEL)
    }

    fun getOSVersion(): String {
        return (Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
    }

    fun getDeviceModelNameAndOS(): String {
        return (Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
    }
}