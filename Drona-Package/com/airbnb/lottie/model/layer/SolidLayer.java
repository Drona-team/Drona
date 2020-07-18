package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.animation.LPaint;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ValueCallbackKeyframeAnimation;
import com.airbnb.lottie.value.LottieValueCallback;

public class SolidLayer
  extends BaseLayer
{
  @Nullable
  private BaseKeyframeAnimation<ColorFilter, ColorFilter> colorFilterAnimation;
  private final Layer layerModel;
  private final Paint paint = new LPaint();
  private final Path path = new Path();
  private final float[] points = new float[8];
  private final RectF rect = new RectF();
  
  SolidLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    super(paramLottieDrawable, paramLayer);
    layerModel = paramLayer;
    paint.setAlpha(0);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(paramLayer.getSolidColor());
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    super.addValueCallback(paramObject, paramLottieValueCallback);
    if (paramObject == LottieProperty.COLOR_FILTER)
    {
      if (paramLottieValueCallback == null)
      {
        colorFilterAnimation = null;
        return;
      }
      colorFilterAnimation = new ValueCallbackKeyframeAnimation(paramLottieValueCallback);
    }
  }
  
  public void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    int j = Color.alpha(layerModel.getSolidColor());
    if (j == 0) {
      return;
    }
    int i;
    if (transform.getOpacity() == null) {
      i = 100;
    } else {
      i = ((Integer)transform.getOpacity().getValue()).intValue();
    }
    paramInt = (int)(paramInt / 255.0F * (j / 255.0F * i / 100.0F) * 255.0F);
    paint.setAlpha(paramInt);
    if (colorFilterAnimation != null) {
      paint.setColorFilter((ColorFilter)colorFilterAnimation.getValue());
    }
    if (paramInt > 0)
    {
      points[0] = 0.0F;
      points[1] = 0.0F;
      points[2] = layerModel.getSolidWidth();
      points[3] = 0.0F;
      points[4] = layerModel.getSolidWidth();
      points[5] = layerModel.getSolidHeight();
      points[6] = 0.0F;
      points[7] = layerModel.getSolidHeight();
      paramMatrix.mapPoints(points);
      path.reset();
      path.moveTo(points[0], points[1]);
      path.lineTo(points[2], points[3]);
      path.lineTo(points[4], points[5]);
      path.lineTo(points[6], points[7]);
      path.lineTo(points[0], points[1]);
      path.close();
      paramCanvas.drawPath(path, paint);
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix, boolean paramBoolean)
  {
    super.getBounds(paramRectF, paramMatrix, paramBoolean);
    rect.set(0.0F, 0.0F, layerModel.getSolidWidth(), layerModel.getSolidHeight());
    boundsMatrix.mapRect(rect);
    paramRectF.set(rect);
  }
}
