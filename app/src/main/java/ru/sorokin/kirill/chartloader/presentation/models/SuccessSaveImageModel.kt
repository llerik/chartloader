package ru.sorokin.kirill.chartloader.presentation.models

/**
 * Модель сообщения об успешном сохранении изображения
 *
 * @param message текст сообщения
 * @param buttonName название кнопки
 * @param path путь к файлу
 *
 * @author Sorokin Kirill
 */
data class SuccessSaveImageModel(
    val message: String,
    val buttonName: String,
    val path: String
)
