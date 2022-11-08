package ru.sorokin.kirill.chartloader.data.mapper

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.sorokin.kirill.chartloader.data.models.PointsEntity
import ru.sorokin.kirill.chartloader.data.parser.Parser
import ru.sorokin.kirill.chartloader.utils.Logger

/**
 * ApiMapper запроса точек
 *
 * @param parser парсер ответа от бэка
 * @param okHttpClient клиент для выполнения запроса к бэку
 *
 * @author Sorokin Kirill
 */
class PointsApiMapperImpl(
    private val parser: Parser,
    private val okHttpClient: OkHttpClient
) : PointsApiMapper {
    override fun getPoints(count: Int): PointsEntity {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(HOST)
            .addPathSegments(PATH)
            .addQueryParameter(COUNT_PARAM, count.toString())
            .toString()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = okHttpClient
            .newCall(request)
            .execute()
        Logger.d(TAG, "getPoints: $response")
        if (!response.isSuccessful) {
            throw DataException(response.message())
        }
        val bodyString = response.body()?.string() ?: throw DataException("Body is empty")
        Logger.d(TAG, "getPoints: $bodyString")
        return parser.parse(bodyString, PointsEntity::class.java)
    }

    companion object {
        private const val TAG = "PointsApiMapperImpl"
        private const val COUNT_PARAM = "count"
        private const val HOST = "hr-challenge.interactivestandard.com"
        private const val PATH = "api/test/points"
    }

}