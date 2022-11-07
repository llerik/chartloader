package ru.sorokin.kirill.chartloader.data.repository

import android.graphics.PointF
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.sorokin.kirill.chartloader.data.converter.PointConverter
import ru.sorokin.kirill.chartloader.data.mapper.PointsApiMapper
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository
import java.io.IOException

/**
 * Тест на [PointsRepositoryImpl]
 *
 * @author Sorokin Kirill
 */
@ExtendWith(MockKExtension::class)
internal class PointsRepositoryImplTest {
    @MockK
    private lateinit var converter: PointConverter

    @MockK
    private lateinit var apiMapper: PointsApiMapper
    private lateinit var repositry: PointsRepository

    @BeforeEach
    fun setUp() {
        repositry = PointsRepositoryImpl(
            converter, apiMapper
        )
    }

    @Test
    fun getPoints_failure() {
        every { apiMapper.getPoints(10) } throws IOException()

        assertThat(repositry.getPoints(10)).isEqualTo(listOf<PointF>())
    }

    @Test
    fun getPoints_success(@MockK point: PointF) {
        every { apiMapper.getPoints(10) } returns mockk()
        every { converter.convert(any()) } returns listOf(point)

        assertThat(repositry.getPoints(10)).isEqualTo(listOf(point))
    }
}