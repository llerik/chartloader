package ru.sorokin.kirill.chartloader.data.models

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import ru.sorokin.kirill.chartloader.data.parser.JacksonParserImpl
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

/**
 * Тест на [PointsEntity]
 *
 * @author Sorokin Kirill
 */
internal class PointsEntityTest {
    private val parser = JacksonParserImpl()

    @Test
    fun test() {
        val jsonText = getString(JSON)

        val model = PointsEntity(
            listOf(
                PointEntity(1.12345f, 3213f),
                PointEntity(7.6543f, 2.345f)
            )
        )
        val obj = parser.parse(jsonText, PointsEntity::class.java)
        assertThat(obj).isEqualTo(model)
    }

    private fun getString(fileName: String): String {
        val text: String
        var inputStreamReader: InputStreamReader? = null
        try {
            inputStreamReader = InputStreamReader(getFile(fileName), StandardCharsets.UTF_8)
            text = BufferedReader(inputStreamReader).lines().collect(Collectors.joining("\n"))
        } finally {
            inputStreamReader?.let {
                runCatching { it.close() }
                    .onFailure {  }
            }
        }
        return text
    }

    private fun getFile(fileName: String): InputStream =
        javaClass.classLoader!!.getResourceAsStream(fileName)!!

    companion object {
        private const val JSON = "response_points.json"
    }
}