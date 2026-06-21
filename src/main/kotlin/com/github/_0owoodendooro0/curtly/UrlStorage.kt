package com.github._0owoodendooro0.curtly

import java.io.File
import java.util.concurrent.ConcurrentHashMap

interface UrlStorage {
    fun save(key: String, longUrl: String)
    fun get(key: String): String?
    fun getAll(): Map<String, String>
}

class InMemoryUrlStorage : UrlStorage {
    private val map = ConcurrentHashMap<String, String>()

    override fun save(key: String, longUrl: String) {
        map[key] = longUrl
    }

    override fun get(key: String): String? = map[key]

    override fun getAll(): Map<String, String> = map.toMap()
}

class FileUrlStorage(private val file: File) : UrlStorage {
    private val map = ConcurrentHashMap<String, String>()

    init {
        load()
    }

    @Synchronized
    private fun load() {
        if (file.exists()) {
            file.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        map[parts[0].trim()] = parts[1].trim()
                    }
                }
            }
        }
    }

    @Synchronized
    override fun save(key: String, longUrl: String) {
        map[key] = longUrl
        // Ensure the parent directory exists
        file.parentFile?.mkdirs()
        file.appendText("$key=$longUrl\n")
    }

    override fun get(key: String): String? = map[key]

    override fun getAll(): Map<String, String> = map.toMap()
}
