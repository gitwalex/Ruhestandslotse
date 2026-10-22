package com.gerwalex.retire

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun getSystemLocale(): String