package com.luanbarbosagomes.poiapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


object IconUtils {

    fun fromVector(context: Context, icon: Int): BitmapDescriptor {
        with(ContextCompat.getDrawable(context, icon)) {
            val bitmap = Bitmap.createBitmap(
                this?.intrinsicWidth ?: 0,
                this?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            this?.setBounds(0, 0, canvas.width, canvas.height)
            this?.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}
