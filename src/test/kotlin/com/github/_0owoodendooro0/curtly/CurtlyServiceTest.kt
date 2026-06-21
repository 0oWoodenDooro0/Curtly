package com.github._0owoodendooro0.curtly

import java.io.File
import kotlin.test.*

class CurtlyServiceTest {

    @Test
    fun testInMemoryUrlStorage() {
        val storage = InMemoryUrlStorage()
        assertNull(storage.get("key1"))
        
        storage.save("key1", "https://google.com")
        assertEquals("https://google.com", storage.get("key1"))
        assertEquals(mapOf("key1" to "https://google.com"), storage.getAll())
    }

    @Test
    fun testFileUrlStorage() {
        val tempFile = File.createTempFile("curtly_test", ".db")
        tempFile.deleteOnExit()

        val storage = FileUrlStorage(tempFile)
        storage.save("keyA", "https://github.com")
        storage.save("keyB", "https://kotlinlang.org")

        // Read from the same file storage instance
        assertEquals("https://github.com", storage.get("keyA"))
        assertEquals("https://kotlinlang.org", storage.get("keyB"))

        // Create a new file storage instance pointing to the same file to verify persistence load
        val storage2 = FileUrlStorage(tempFile)
        assertEquals("https://github.com", storage2.get("keyA"))
        assertEquals("https://kotlinlang.org", storage2.get("keyB"))
    }

    @Test
    fun testCurtlyServiceUrlNormalization() {
        val storage = InMemoryUrlStorage()
        val service = CurtlyService(storage, baseUrl = "http://localhost/")

        // Tests adding https if protocol is missing
        val short1 = service.shorten("google.com")
        val key1 = short1.removePrefix("http://localhost/")
        assertEquals("https://google.com", service.resolve(key1))

        // Tests keeping protocol if it is present
        val short2 = service.shorten("http://example.com")
        val key2 = short2.removePrefix("http://localhost/")
        assertEquals("http://example.com", service.resolve(key2))
    }

    @Test
    fun testCurtlyServiceCustomKey() {
        val storage = InMemoryUrlStorage()
        val service = CurtlyService(storage, baseUrl = "http://localhost/")

        val short = service.shorten("https://google.com", "myCustomKey")
        assertEquals("http://localhost/myCustomKey", short)
        assertEquals("https://google.com", service.resolve("myCustomKey"))

        // Custom key already in use should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            service.shorten("https://yahoo.com", "myCustomKey")
        }

        // Invalid characters in custom key should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            service.shorten("https://yahoo.com", "invalid/key")
        }
    }
}
