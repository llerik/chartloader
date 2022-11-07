package ru.sorokin.kirill.chartloader.presentation.main

import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifyAll
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.sorokin.kirill.chartloader.InstantExecutorExtension
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository
import ru.sorokin.kirill.chartloader.presentation.core.network.RxSupport
import ru.sorokin.kirill.chartloader.presentation.core.resource.ResourceManager
import ru.sorokin.kirill.chartloader.presentation.main.converter.PointModelConverter
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * Тест на [MainViewModel]
 *
 * @author Sorokin Kirill
 */
@ExtendWith(MockKExtension::class, InstantExecutorExtension::class)
internal class MainViewModelTest {
    @MockK
    private lateinit var repository: PointsRepository

    @MockK
    private lateinit var converter: PointModelConverter

    @MockK
    private lateinit var resourceManager: ResourceManager

    @MockK
    private lateinit var rxSupport: RxSupport

    @MockK(relaxUnitFun = true)
    private lateinit var errorObserver: Observer<String>

    @MockK(relaxUnitFun = true)
    private lateinit var progressObserver: Observer<Boolean>

    @MockK(relaxUnitFun = true)
    private lateinit var buttonEnableObserver: Observer<Boolean>

    @MockK(relaxUnitFun = true)
    private lateinit var dataObserver: Observer<List<PointModel>>

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        viewModel = MainViewModel(
            repository, converter, resourceManager, rxSupport
        )
        viewModel.getDataLiveData().observeForever(dataObserver)
        viewModel.getErrorLiveData().observeForever(errorObserver)
        viewModel.getProgressLiveData().observeForever(progressObserver)
        viewModel.getButtonEnableLiveData().observeForever(buttonEnableObserver)

        every { rxSupport.getIOScheduler() } returns Schedulers.trampoline()
        every { rxSupport.getMainScheduler() } returns Schedulers.trampoline()
    }

    @Test
    fun buttonClick_blank() {
        every {
            resourceManager.getString(R.string.error_message_input_empty)
        } returns "error_message_input_empty"

        viewModel.buttonClick("")

        verifyAll {
            buttonEnableObserver.onChanged(false)
            errorObserver.onChanged("error_message_input_empty")
            buttonEnableObserver.onChanged(true)
        }
    }

    @Test
    fun buttonClick_small() {
        every {
            resourceManager.getString(R.string.error_message_incorrect_number)
        } returns "error_message_incorrect_number"

        viewModel.buttonClick("1")

        verifyAll {
            buttonEnableObserver.onChanged(false)
            errorObserver.onChanged("error_message_incorrect_number")
            buttonEnableObserver.onChanged(true)
        }
    }

    @Test
    fun buttonClick_requestEmpty() {
        every {
            resourceManager.getString(R.string.error_message_something_went_wrong)
        } returns "error_message_something_went_wrong"
        every { repository.getPoints(10) } returns listOf()

        viewModel.buttonClick("10")

        verifyAll {
            buttonEnableObserver.onChanged(false)
            progressObserver.onChanged(true)
            repository.getPoints(10)
            progressObserver.onChanged(false)
            errorObserver.onChanged("error_message_something_went_wrong")
            buttonEnableObserver.onChanged(true)
        }
    }

    @Test
    fun buttonClick_requestException(@MockK exception: Exception) {
        every {
            resourceManager.getString(R.string.error_message_something_went_wrong)
        } returns "error_message_something_went_wrong"
        every { repository.getPoints(10) } throws exception

        viewModel.buttonClick("10")

        verifyAll {
            buttonEnableObserver.onChanged(false)
            progressObserver.onChanged(true)
            repository.getPoints(10)
            progressObserver.onChanged(false)
            errorObserver.onChanged("error_message_something_went_wrong")
            buttonEnableObserver.onChanged(true)
        }
    }

    @Test
    fun buttonClick_requestSuccess() {
        val data = listOf(mockk<PointModel>())
        every { repository.getPoints(10) } returns listOf(mockk())
        every { converter.convert(any()) } returns data

        viewModel.buttonClick("10")

        verifyAll {
            buttonEnableObserver.onChanged(false)
            progressObserver.onChanged(true)
            repository.getPoints(10)
            converter.convert(any())
            dataObserver.onChanged(data)
            progressObserver.onChanged(false)
            buttonEnableObserver.onChanged(true)
        }
    }
}