package suika.jp.nfcreader.AndroidDesign;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import suika.jp.nfcreader.GraphicView;

public class AnimationGraphic extends Animation{
    private GraphicView graphicview;

    // 中心座標
    private float centerX;
    private float centerY;

    // アニメーション角度
    private float oldAngle;
    private float newAngle;

    public AnimationGraphic(GraphicView graphicview, int newAngle) {
        this.oldAngle = graphicview.getAngle();
        this.newAngle = newAngle;
        this.graphicview = graphicview;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        graphicview.setAngle(angle);
        graphicview.requestLayout();
    }
}
