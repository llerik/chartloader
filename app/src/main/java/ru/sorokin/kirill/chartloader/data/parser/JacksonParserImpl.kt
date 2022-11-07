package ru.sorokin.kirill.chartloader.data.parser

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule


/**
 * Реализация парсера на основе Jackson
 *
 * @author Sorokin Kirill
 */
class JacksonParserImpl: Parser {

    private val mapper = JsonMapper.builder()
        .addModule(KotlinModule(strictNullChecks = true))
        .build()

    override fun <T> parse(value: String, clazz: Class<T>): T {
        return mapper.readValue(value, clazz)
    }

    override fun <T> serialize(obj: T): String {
        return mapper.writeValueAsString(obj)
    }
}