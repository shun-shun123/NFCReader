package suika.jp.nfcreader.AndroidDesign

import android.animation.ValueAnimator
import android.graphics.Color
import android.opengl.GLES20
import android.view.View
import android.view.animation.Animation
import android.view.animation.AlphaAnimation




class AndroidDesign {

    val Int.red get() = Color.red(this)
    val Int.blue get() = Color.blue(this)
    val Int.green get() = Color.green(this)

    fun colorAnimation(view: View, fromColor: Int, toColor: Int) =
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                addUpdateListener {
                    val animatedReverseFraction = 1 - it.animatedFraction
                    val color = Color.rgb(
                            (fromColor.red * animatedReverseFraction + toColor.red * it.animatedFraction).toInt(),
                            (fromColor.green * animatedReverseFraction + toColor.green * it.animatedFraction).toInt(),
                            (fromColor.blue * animatedReverseFraction + toColor.blue * it.animatedFraction).toInt()
                    )
                    view.setBackgroundColor(color)

                }
                start()
            }

    fun fadeText(view: View, duration: Long, fromAlpha: Float, toAlpha: Float){
        val aanim1 = AlphaAnimation(fromAlpha, toAlpha)
        aanim1.duration = duration
        aanim1.repeatCount = Animation.INFINITE
        aanim1.repeatMode = Animation.REVERSE
        view.startAnimation(aanim1)
    }


}