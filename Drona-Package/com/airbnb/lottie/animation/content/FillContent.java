package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.Way;
import com.airbnb.lottie.animation.LPaint;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.ColorKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ValueCallbackKeyframeAnimation;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.content.ShapeFill;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.List;

public class FillContent
  implements DrawingContent, BaseKeyframeAnimation.AnimationListener, KeyPathElementContent
{
  private final BaseKeyframeAnimation<Integer, Integer> colorAnimation;
  @Nullable
  private BaseKeyframeAnimation<ColorFilter, ColorFilter> colorFilterAnimation;
  private final boolean hidden;
  private final BaseLayer layer;
  private final LottieDrawable lottieDrawable;
  private final String name;
  private final BaseKeyframeAnimation<Integer, Integer> opacityAnimation;
  private final Paint paint = new LPaint(1);
  private final Path path = new Path();
  private final List<PathContent> paths = new ArrayList();
  
  public FillContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, ShapeFill paramShapeFill)
  {
    layer = paramBaseLayer;
    name = paramShapeFill.getName();
    hidden = paramShapeFill.isHidden();
    lottieDrawable = paramLottieDrawable;
    if ((paramShapeFill.getColor() != null) && (paramShapeFill.getOpacity() != null))
    {
      path.setFillType(paramShapeFill.getFillType());
      colorAnimation = paramShapeFill.getColor().createAnimation();
      colorAnimation.addUpdateListener(this);
      paramBaseLayer.addAnimation(colorAnimation);
      opacityAnimation = paramShapeFill.getOpacity().createAnimation();
      opacityAnimation.addUpdateListener(this);
      paramBaseLayer.addAnimation(opacityAnimation);
      return;
    }
    colorAnimation = null;
    opacityAnimation = null;
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    if (paramObject == LottieProperty.COLOR)
    {
      colorAnimation.setValueCallback(paramLottieValueCallback);
      return;
    }
    if (paramObject == LottieProperty.OPACITY)
    {
      opacityAnimation.setValueCallback(paramLottieValueCallback);
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
      layer.addAnimation(colorFilterAnimation);
    }
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    if (hidden) {
      return;
    }
    Way.beginSection("FillContent#draw");
    paint.setColor(((ColorKeyframeAnimation)colorAnimation).getIntValue());
    int i = (int)(paramInt / 255.0F * ((Integer)opacityAnimation.getValue()).intValue() / 100.0F * 255.0F);
    Paint localPaint = paint;
    paramInt = 0;
    localPaint.setAlpha(MiscUtils.clamp(i, 0, 255));
    if (colorFilterAnimation != null) {
      paint.setColorFilter((ColorFilter)colorFilterAnimation.getValue());
    }
    path.reset();
    while (paramInt < paths.size())
    {
      path.addPath(((PathContent)paths.get(paramInt)).getPath(), paramMatrix);
      paramInt += 1;
    }
    paramCanvas.drawPath(path, paint);
    Way.endSection("FillContent#draw");
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix, boolean paramBoolean)
  {
    path.reset();
    int i = 0;
    while (i < paths.size())
    {
      path.addPath(((PathContent)paths.get(i)).getPath(), paramMatrix);
      i += 1;
    }
    path.computeBounds(paramRectF, false);
    paramRectF.set(left - 1.0F, top - 1.0F, right + 1.0F, bottom + 1.0F);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void onValueChanged()
  {
    lottieDrawable.invalidateSelf();
  }
  
  public void resolveKeyPath(KeyPath paramKeyPath1, int paramInt, List paramList, KeyPath paramKeyPath2)
  {
    MiscUtils.resolveKeyPath(paramKeyPath1, paramInt, paramList, paramKeyPath2, this);
  }
  
  public void setContents(List paramList1, List paramList2)
  {
    int i = 0;
    while (i < paramList2.size())
    {
      paramList1 = (Content)paramList2.get(i);
      if ((paramList1 instanceof PathContent)) {
        paths.add((PathContent)paramList1);
      }
      i += 1;
    }
  }
}
