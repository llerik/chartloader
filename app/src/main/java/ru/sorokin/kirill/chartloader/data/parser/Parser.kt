package ru.sorokin.kirill.chartloader.data.parser

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface Parser {
    /**
     * todo
     *
     * @param value
     * @param clazz
     */
    fun <T> parse(value: String, clazz: Class<T>): T

    /**
     * todo
     *
     * @param obj
     */
    fun <T> serialize(obj: T): String
}