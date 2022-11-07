package ru.sorokin.kirill.chartloader.presentation.models

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

/**
 * Модель точки для рисования
 *
 * @param point координаты точки
 * @param firstNormal первая нормаль
 * @param secondNormal вторая нормаль
 *
 * @author Sorokin Kirill
 */
data class PointModel(
    val point: PointF,
    val firstNormal: PointF,
    val secondNormal: PointF
) : Parcelable {
    constructor(parcel: Parcel) : this(
        PointF(
            parcel.readFloat(),
            parcel.readFloat()
        ),
        PointF(
            parcel.readFloat(),
            parcel.readFloat()
        ),
        PointF(
            parcel.readFloat(),
            parcel.readFloat()
        )
    )

    fun move(diff: PointF) {
        point.x += diff.x
        point.y += diff.y

        firstNormal.x += diff.x
        firstNormal.y += diff.y

        secondNormal.x += diff.x
        secondNormal.y += diff.y
    }

    fun scale(factor: PointF) {
        point.x *= factor.x
        point.y *= factor.y

        firstNormal.x *= factor.x
        firstNormal.y *= factor.y

        secondNormal.x *= factor.x
        secondNormal.y *= factor.y
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(point.x)
        parcel.writeFloat(point.y)

        parcel.writeFloat(firstNormal.x)
        parcel.writeFloat(firstNormal.y)

        parcel.writeFloat(secondNormal.x)
        parcel.writeFloat(secondNormal.y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointModel> {
        override fun createFromParcel(parcel: Parcel): PointModel {
            return PointModel(parcel)
        }

        override fun newArray(size: Int): Array<PointModel?> {
            return arrayOfNulls(size)
        }
    }
}
