package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.Repeater;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RepeaterContent
  implements DrawingContent, PathContent, GreedyContent, BaseKeyframeAnimation.AnimationListener, KeyPathElementContent
{
  private ContentGroup contentGroup;
  private final BaseKeyframeAnimation<Float, Float> copies;
  private final boolean hidden;
  private final BaseLayer layer;
  private final LottieDrawable lottieDrawable;
  private final Matrix matrix = new Matrix();
  private final String name;
  private final BaseKeyframeAnimation<Float, Float> offset;
  private final Path path = new Path();
  private final TransformKeyframeAnimation transform;
  
  public RepeaterContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, Repeater paramRepeater)
  {
    lottieDrawable = paramLottieDrawable;
    layer = paramBaseLayer;
    name = paramRepeater.getName();
    hidden = paramRepeater.isHidden();
    copies = paramRepeater.getCopies().createAnimation();
    paramBaseLayer.addAnimation(copies);
    copies.addUpdateListener(this);
    offset = paramRepeater.getOffset().createAnimation();
    paramBaseLayer.addAnimation(offset);
    offset.addUpdateListener(this);
    transform = paramRepeater.getTransform().createAnimation();
    transform.addAnimationsToLayer(paramBaseLayer);
    transform.addListener(this);
  }
  
  public void absorbContent(ListIterator paramListIterator)
  {
    if (contentGroup != null) {
      return;
    }
    while ((paramListIterator.hasPrevious()) && (paramListIterator.previous() != this)) {}
    ArrayList localArrayList = new ArrayList();
    while (paramListIterator.hasPrevious())
    {
      localArrayList.add(paramListIterator.previous());
      paramListIterator.remove();
    }
    Collections.reverse(localArrayList);
    contentGroup = new ContentGroup(lottieDrawable, layer, "Repeater", hidden, localArrayList, null);
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    if (transform.applyValueCallback(paramObject, paramLottieValueCallback)) {
      return;
    }
    if (paramObject == LottieProperty.REPEATER_COPIES)
    {
      copies.setValueCallback(paramLottieValueCallback);
      return;
    }
    if (paramObject == LottieProperty.REPEATER_OFFSET) {
      offset.setValueCallback(paramLottieValueCallback);
    }
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    float f1 = ((Float)copies.getValue()).floatValue();
    float f2 = ((Float)offset.getValue()).floatValue();
    float f3 = ((Float)transform.getStartOpacity().getValue()).floatValue() / 100.0F;
    float f4 = ((Float)transform.getEndOpacity().getValue()).floatValue() / 100.0F;
    int i = (int)f1 - 1;
    while (i >= 0)
    {
      matrix.set(paramMatrix);
      Matrix localMatrix = matrix;
      TransformKeyframeAnimation localTransformKeyframeAnimation = transform;
      float f6 = i;
      localMatrix.preConcat(localTransformKeyframeAnimation.getMatrixForRepeater(f6 + f2));
      float f5 = paramInt;
      f6 = MiscUtils.lerp(f3, f4, f6 / f1);
      contentGroup.draw(paramCanvas, matrix, (int)(f5 * f6));
      i -= 1;
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix, boolean paramBoolean)
  {
    contentGroup.getBounds(paramRectF, paramMatrix, paramBoolean);
  }
  
  public String getName()
  {
    return name;
  }
  
  public Path getPath()
  {
    Path localPath = contentGroup.getPath();
    path.reset();
    float f1 = ((Float)copies.getValue()).floatValue();
    float f2 = ((Float)offset.getValue()).floatValue();
    int i = (int)f1 - 1;
    while (i >= 0)
    {
      matrix.set(transform.getMatrixForRepeater(i + f2));
      path.addPath(localPath, matrix);
      i -= 1;
    }
    return path;
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
    contentGroup.setContents(paramList1, paramList2);
  }
}
