package com.gordon.studydemo

import android.app.Application
import com.sensorsdata.anlytics.SAConfigOptions
import com.sensorsdata.anlytics.SensorsDataAPI

class StudyApplication : Application() {

    private val SA_SERVER_URL =
        "https://sdkdebugtest.datasink.sensorsdata.cn/sa?project=default&token=cfb8b60e42e0ae9b"
    override fun onCreate() {
        super.onCreate()
        val saConfig = SAConfigOptions(
            SA_SERVER_URL
        )
            .setFlushBulkSize(50)
            .setFlushInterval(10)
            .enableLog(true)

        SensorsDataAPI.startWithConfigOptions(this, saConfig)
    }
}