package fraktal.io.android.demo

import android.app.Application
import fraktal.io.android.demo.shared.db.DbLocator

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        DbLocator.createDb(this)
    }
}