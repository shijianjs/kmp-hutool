package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Editor
import io.github.shijianjs.kmphutool.core.lang.UUID
import io.github.shijianjs.kmphutool.core.lang.id.IdConstants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UuidAndEditorTest {
    @Test
    fun parsesAndFormatsUuids() {
        val text = "123e4567-e89b-12d3-a456-426614174000"
        val uuid = UUID.fromString(text)
        assertEquals(text, uuid.toString())
        assertEquals(32, uuid.toString(true).length)
        assertEquals(1, uuid.version())
    }

    @Test
    fun randomUuidIsVersion4() {
        val uuid = UUID.randomUUID()
        assertEquals(4, uuid.version())
        assertEquals(4, uuid.toString().count { it == '-' })
        assertEquals(uuid, UUID.fromString(uuid.toString()))
    }

    @Test
    fun editorDropsNulls() {
        val editor = Editor<String> { if (it.isEmpty()) null else it.uppercase() }
        assertEquals("A", editor.edit("a"))
        assertEquals(null, editor.edit(""))
    }

    @Test
    fun idConstantsAreInRange() {
        assertTrue(IdConstants.DEFAULT_WORKER_ID in 0..31)
        assertTrue(IdConstants.DEFAULT_DATACENTER_ID in 0..31)
        assertTrue(IdConstants.DEFAULT_SNOWFLAKE.nextId() > 0)
    }
}

