package ru.sorokin.kirill.chartloader.presentation.models

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

/**
 * Модель точки для рисования
 *
 * @param point координаты точки
 * @param offset координата смещения для отрисовки кривой
 *
 * @author Sorokin Kirill
 */
data class PointModel(
    val point: PointF,
    val offset: PointF = PointF(0f, 0f)
) : Parcelable {
    constructor(parcel: Parcel) : this(
        PointF(
            parcel.readFloat(),
            parcel.readFloat()
        ),
        PointF(
            parcel.readFloat(),
            parcel.readFloat()
        )
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(point.x)
        parcel.writeFloat(point.y)
        parcel.writeFloat(offset.x)
        parcel.writeFloat(offset.y)
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
