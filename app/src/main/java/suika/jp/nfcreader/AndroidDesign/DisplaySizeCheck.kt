package suika.jp.nfcreader.AndroidDesign

import java.lang.reflect.Method

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.View

object DisplaySizeCheck {

    fun getViewSize(View: View): Point {
        val point = Point(0, 0)
        point.set(View.width, View.height)

        return point
    }

}