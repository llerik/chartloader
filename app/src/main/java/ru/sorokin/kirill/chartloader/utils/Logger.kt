package ru.sorokin.kirill.chartloader.utils

import android.util.Log

/**
 * @author Sorokin Kirill
 */
object Logger {
    private const val TAG = "chartloader_log_"
    private const val MAX_SIZE = 3000
    private var magicNumber = -1

    fun v(tag: String, message: String) {
        Log.v(TAG + tag, location + message)
    }

    fun w(tag: String, message: String) {
        Log.w(TAG + tag, location + message)
    }

    fun e(tag: String, message: String, t: Throwable?) {
        Log.e(TAG + tag, location + message, t)
    }

    fun e(tag: String, message: String) {
        val list = getChunk(message)
        for (part in list.indices) {
            Log.e(TAG + tag, location + list[part])
        }
    }

    fun i(tag: String, message: String) {
        Log.i(TAG + tag, location + message)
    }

    fun d(tag: String, message: String) {
        val list = getChunk(message)
        for (part in list) {
            Log.d(TAG + tag, location + part)
        }
    }

    fun v(tag: String) {
        Log.v(TAG + tag, location)
    }

    fun w(tag: String) {
        Log.w(TAG + tag, location)
    }

    fun e(tag: String) {
        Log.e(TAG + tag, location)
    }

    fun i(tag: String) {
        Log.i(TAG + tag, location)
    }

    fun d(tag: String) {
        Log.d(TAG + tag, location)
    }

    private val location: String
        get() = if (magicNumber > -1) {
            locationFast
        } else locationSlow
    private val locationSlow: String
        get() {
            val className = Logger::class.java.name
            val traces = Thread.currentThread().stackTrace
            var found = false
            for (i in traces.indices) {
                val trace = traces[i]
                try {
                    if (found) {
                        if (!trace.className.startsWith(className)) {
                            magicNumber = i
                            val clazz = Class.forName(trace.className)
                            return "[" + getClassName(clazz) + ":" + trace.methodName + ":" + trace.lineNumber + "]: "
                        }
                    } else {
                        if (trace.className.startsWith(className)) {
                            found = true
                        }
                    }
                } catch (e: ClassNotFoundException) {
                    Log.e(TAG, "getLocationSlow", e)
                }
            }
            return "[]: "
        }
    private val locationFast: String
        get() {
            val traces = Thread.currentThread().stackTrace
            val trace = traces[magicNumber]
            try {
                val clazz = Class.forName(trace.className)
                return "[" + getClassName(clazz) + ":" + trace.methodName + ":" + trace.lineNumber + "]: "
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "getLocationFast", e)
            }
            return "[]: "
        }

    private fun getClassName(clazz: Class<*>?): String {
        if (clazz != null) {
            val simpleName = clazz.simpleName
            if (simpleName.isNotEmpty()) {
                return simpleName
            }
            try {
                if (clazz.enclosingClass != null) {
                    return getClassName(clazz.enclosingClass)
                }
            } catch (_: Throwable) {
            }
            val name = clazz.name
            if (name.isNotEmpty()) {
                return name
            }
        }
        return ""
    }

    private fun getChunk(message: String): List<String> {
        val list: MutableList<String> = ArrayList()
        if (message.length > MAX_SIZE) {
            val countPart = message.length / MAX_SIZE + if (message.length % MAX_SIZE > 0) 1 else 0
            for (part in 0 until countPart) {
                val start = part * MAX_SIZE
                val end = (start + MAX_SIZE).coerceAtMost(message.length)
                list.add(getPartNumber(part, countPart) + message.substring(start, end))
            }
        } else {
            list.add(message)
        }
        return list
    }

    private fun getPartNumber(part: Int, countParts: Int): String {
        val partNumber = part + 1
        return "[part:$partNumber|$countParts] "
    }
}