package ru.sorokin.kirill.chartloader.data.parser

/**
 * Интерфейс парсера
 *
 * @author Sorokin Kirill
 */
interface Parser {
    /**
     * Получить из строки объект
     *
     * @param value исходная строка
     * @param clazz класс объекта результата
     */
    fun <T> parse(value: String, clazz: Class<T>): T

    /**
     * Преобразовать объект в строку
     *
     * @param obj объект
     */
    fun <T> serialize(obj: T): String
}