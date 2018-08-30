package suika.jp.nfcreader

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager

import java.util.Random

class GraphicView(context: Context) : View(context) {
    private var paint: Paint
    private var path: Path

    private val StrokeWidth1 = 20
    private val dp: Float
    private val activity: Activity? = null
    private val SIZE = 100f
    private val rect: RectF
    private var hOffset: Float = 0f
    private var vOffset: Float = 20f
    // 初期 Angle
    // Animationへ現在のangleを返す
    // Animationから新しいangleが設定される
    var angle = 10f
    var random: Int = Random().nextInt(2);

    init {
        paint = Paint()
        path = Path()

        // スクリーンサイズからdipのようなものを作る
        val metrics = DisplayMetrics()
        val winMan = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        winMan.defaultDisplay.getMetrics(metrics)

        dp = resources.displayMetrics.density
        // Arcの幅
        val strokeWidth = 30
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        // Arcの色
        paint.color = Color.argb(255, 255, 0, 0)
        // Arcの範囲
        rect = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        // 背景
        canvas.drawColor(Color.argb(255, 0, 0, 0))

        // Canvas 中心点
        val xc = (canvas.width / 2).toFloat()
        val yc = (canvas.height / 2).toFloat()
        triangle(canvas, xc, yc)
        animationText()
        canvas.drawTextOnPath("Please hold the IC card", path, hOffset, vOffset, paint)
    }

    private fun animationText() =  ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100
        addUpdateListener {
            hOffset += 1f
        }
        start()
    }

    private fun triangle(canvas: Canvas, xc: Float, yc: Float) {
        val tx1 = xc - SIZE * dp
        val ty1 = yc + SIZE * dp
        val tx2 = xc + SIZE * dp
        val ty2 = yc + SIZE * dp
        val ty3 = yc - (SIZE * Math.sqrt(3.0).toFloat() - SIZE) * dp
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.WHITE
        path.moveTo(tx2, ty2)
        path.lineTo(xc, ty3)
        path.lineTo(tx1, ty1)
        path.lineTo(tx2, ty2)
        canvas.drawPath(path, paint)
        paint.textSize = 40f  //テキストサイズセット
    }

    companion object {
        private val StrokeWidth2 = 40
        // Animation 開始地点をセット
        private val AngleTarget = 270
    }
}
