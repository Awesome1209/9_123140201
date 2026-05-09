package org.example.project.platform

import platform.Foundation.NSDate

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000.0).toLong()
}
