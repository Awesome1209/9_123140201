package org.example.project.platform

import android.content.Context

object AndroidPlatformContext {
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun get(): Context? = applicationContext
}
