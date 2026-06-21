package com.github._0owoodendooro0.curtly

class CurtlyService(
    private val storage: UrlStorage,
    private val keyGenerator: KeyGenerator = RandomAlphanumericKeyGenerator(),
    private val baseUrl: String = "http://localhost:8080/"
) {
    /**
     * Shortens a long URL and returns the full short URL.
     * Optionally accepts a custom key.
     */
    fun shorten(longUrl: String, customKey: String? = null): String {
        // Basic normalization (make sure it starts with http or https)
        val normalizedUrl = if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            "https://$longUrl"
        } else {
            longUrl
        }

        val key = if (customKey != null) {
            require(customKey.matches(Regex("[a-zA-Z0-9_-]+"))) { 
                "Custom key must contain only letters, numbers, hyphens or underscores" 
            }
            require(storage.get(customKey) == null) {
                "Custom key '$customKey' is already in use"
            }
            customKey
        } else {
            var generatedKey = keyGenerator.generate()
            // Avoid collisions
            while (storage.get(generatedKey) != null) {
                generatedKey = keyGenerator.generate()
            }
            generatedKey
        }

        storage.save(key, normalizedUrl)

        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "$normalizedBaseUrl$key"
    }

    /**
     * Resolves a key to the original long URL.
     */
    fun resolve(key: String): String? {
        return storage.get(key)
    }

    /**
     * Gets all stored URLs.
     */
    fun getAll(): Map<String, String> {
        return storage.getAll()
    }
}
