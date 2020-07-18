package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ColorKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ValueCallbackKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.content.ShapeStroke;
import com.airbnb.lottie.model.content.ShapeStroke.LineCapType;
import com.airbnb.lottie.model.content.ShapeStroke.LineJoinType;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.value.LottieValueCallback;

public class StrokeContent
  extends BaseStrokeContent
{
  private final BaseKeyframeAnimation<Integer, Integer> colorAnimation;
  @Nullable
  private BaseKeyframeAnimation<ColorFilter, ColorFilter> colorFilterAnimation;
  private final boolean hidden;
  private final BaseLayer layer;
  private final String name;
  
  public StrokeContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, ShapeStroke paramShapeStroke)
  {
    super(paramLottieDrawable, paramBaseLayer, paramShapeStroke.getCapType().toPaintCap(), paramShapeStroke.getJoinType().toPaintJoin(), paramShapeStroke.getMiterLimit(), paramShapeStroke.getOpacity(), paramShapeStroke.getWidth(), paramShapeStroke.getLineDashPattern(), paramShapeStroke.getDashOffset());
    layer = paramBaseLayer;
    name = paramShapeStroke.getName();
    hidden = paramShapeStroke.isHidden();
    colorAnimation = paramShapeStroke.getColor().createAnimation();
    colorAnimation.addUpdateListener(this);
    paramBaseLayer.addAnimation(colorAnimation);
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    super.addValueCallback(paramObject, paramLottieValueCallback);
    if (paramObject == LottieProperty.STROKE_COLOR)
    {
      colorAnimation.setValueCallback(paramLottieValueCallback);
      return;
    }
    if (paramObject == LottieProperty.COLOR_FILTER)
    {
      if (paramLottieValueCallback == null)
      {
        colorFilterAnimation = null;
        return;
      }
      colorFilterAnimation = new ValueCallbackKeyframeAnimation(paramLottieValueCallback);
      colorFilterAnimation.addUpdateListener(this);
      layer.addAnimation(colorAnimation);
    }
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    if (hidden) {
      return;
    }
    paint.setColor(((ColorKeyframeAnimation)colorAnimation).getIntValue());
    if (colorFilterAnimation != null) {
      paint.setColorFilter((ColorFilter)colorFilterAnimation.getValue());
    }
    super.draw(paramCanvas, paramMatrix, paramInt);
  }
  
  public String getName()
  {
    return name;
  }
}
