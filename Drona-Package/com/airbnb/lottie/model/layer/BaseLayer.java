package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build.VERSION;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.PerformanceTracker;
import com.airbnb.lottie.Way;
import com.airbnb.lottie.animation.LPaint;
import com.airbnb.lottie.animation.content.DrawingContent;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.FloatKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.MaskKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.KeyPathElement;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.Mask;
import com.airbnb.lottie.utils.Logger;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BaseLayer
  implements DrawingContent, BaseKeyframeAnimation.AnimationListener, KeyPathElement
{
  private static final int CLIP_SAVE_FLAG = 2;
  private static final int CLIP_TO_LAYER_SAVE_FLAG = 16;
  private static final int MATRIX_SAVE_FLAG = 1;
  private static final int SAVE_FLAGS = 19;
  private final List<BaseKeyframeAnimation<?, ?>> animations = new ArrayList();
  final Matrix boundsMatrix = new Matrix();
  private final Paint clearPaint = new LPaint(PorterDuff.Mode.CLEAR);
  private final Paint contentPaint = new LPaint(1);
  private final String drawTraceName;
  private final Paint dstInPaint = new LPaint(1, PorterDuff.Mode.DST_IN);
  private final Paint dstOutPaint = new LPaint(1, PorterDuff.Mode.DST_OUT);
  final Layer layerModel;
  final LottieDrawable lottieDrawable;
  @Nullable
  private MaskKeyframeAnimation mask;
  private final RectF maskBoundsRect = new RectF();
  private final Matrix matrix = new Matrix();
  private final RectF matteBoundsRect = new RectF();
  @Nullable
  private BaseLayer matteLayer;
  private final Paint mattePaint = new LPaint(1);
  @Nullable
  private BaseLayer parentLayer;
  private List<BaseLayer> parentLayers;
  private final Path path = new Path();
  private final RectF rect = new RectF();
  private final RectF tempMaskBoundsRect = new RectF();
  final TransformKeyframeAnimation transform;
  private boolean visible = true;
  
  BaseLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    lottieDrawable = paramLottieDrawable;
    layerModel = paramLayer;
    paramLottieDrawable = new StringBuilder();
    paramLottieDrawable.append(paramLayer.getName());
    paramLottieDrawable.append("#draw");
    drawTraceName = paramLottieDrawable.toString();
    if (paramLayer.getMatteType() == Layer.MatteType.INVERT) {
      mattePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    } else {
      mattePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }
    transform = paramLayer.getTransform().createAnimation();
    transform.addListener(this);
    if ((paramLayer.getMasks() != null) && (!paramLayer.getMasks().isEmpty()))
    {
      mask = new MaskKeyframeAnimation(paramLayer.getMasks());
      paramLottieDrawable = mask.getMaskAnimations().iterator();
      while (paramLottieDrawable.hasNext()) {
        ((BaseKeyframeAnimation)paramLottieDrawable.next()).addUpdateListener(this);
      }
      paramLottieDrawable = mask.getOpacityAnimations().iterator();
      while (paramLottieDrawable.hasNext())
      {
        paramLayer = (BaseKeyframeAnimation)paramLottieDrawable.next();
        addAnimation(paramLayer);
        paramLayer.addUpdateListener(this);
      }
    }
    setupInOutAnimations();
  }
  
  private void applyAddMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    contentPaint.setAlpha((int)(((Integer)paramBaseKeyframeAnimation2.getValue()).intValue() * 2.55F));
    paramCanvas.drawPath(path, contentPaint);
  }
  
  private void applyIntersectMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    saveLayerCompat(paramCanvas, rect, dstInPaint, true);
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    contentPaint.setAlpha((int)(((Integer)paramBaseKeyframeAnimation2.getValue()).intValue() * 2.55F));
    paramCanvas.drawPath(path, contentPaint);
    paramCanvas.restore();
  }
  
  private void applyInvertedAddMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    saveLayerCompat(paramCanvas, rect, contentPaint, true);
    paramCanvas.drawRect(rect, contentPaint);
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    contentPaint.setAlpha((int)(((Integer)paramBaseKeyframeAnimation2.getValue()).intValue() * 2.55F));
    paramCanvas.drawPath(path, dstOutPaint);
    paramCanvas.restore();
  }
  
  private void applyInvertedIntersectMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    saveLayerCompat(paramCanvas, rect, dstInPaint, true);
    paramCanvas.drawRect(rect, contentPaint);
    dstOutPaint.setAlpha((int)(((Integer)paramBaseKeyframeAnimation2.getValue()).intValue() * 2.55F));
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    paramCanvas.drawPath(path, dstOutPaint);
    paramCanvas.restore();
  }
  
  private void applyInvertedSubtractMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    saveLayerCompat(paramCanvas, rect, dstOutPaint, true);
    paramCanvas.drawRect(rect, contentPaint);
    dstOutPaint.setAlpha((int)(((Integer)paramBaseKeyframeAnimation2.getValue()).intValue() * 2.55F));
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    paramCanvas.drawPath(path, dstOutPaint);
    paramCanvas.restore();
  }
  
  private void applyMasks(Canvas paramCanvas, Matrix paramMatrix)
  {
    Way.beginSection("Layer#saveLayer");
    Object localObject1 = rect;
    Object localObject2 = dstInPaint;
    int i = 0;
    saveLayerCompat(paramCanvas, (RectF)localObject1, (Paint)localObject2, false);
    Way.endSection("Layer#saveLayer");
    while (i < mask.getMasks().size())
    {
      localObject1 = (Mask)mask.getMasks().get(i);
      localObject2 = (BaseKeyframeAnimation)mask.getMaskAnimations().get(i);
      BaseKeyframeAnimation localBaseKeyframeAnimation = (BaseKeyframeAnimation)mask.getOpacityAnimations().get(i);
      switch (localObject1.getMaskMode())
      {
      default: 
        break;
      case ???: 
        if (((Mask)localObject1).isInverted()) {
          applyInvertedAddMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        } else {
          applyAddMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        }
        break;
      case ???: 
        if (((Mask)localObject1).isInverted()) {
          applyInvertedIntersectMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        } else {
          applyIntersectMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        }
        break;
      case ???: 
        if (i == 0)
        {
          Paint localPaint = new Paint();
          localPaint.setColor(-16777216);
          paramCanvas.drawRect(rect, localPaint);
        }
        if (((Mask)localObject1).isInverted()) {
          applyInvertedSubtractMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        } else {
          applySubtractMask(paramCanvas, paramMatrix, (Mask)localObject1, (BaseKeyframeAnimation)localObject2, localBaseKeyframeAnimation);
        }
        break;
      }
      i += 1;
    }
    Way.beginSection("Layer#restoreLayer");
    paramCanvas.restore();
    Way.endSection("Layer#restoreLayer");
  }
  
  private void applySubtractMask(Canvas paramCanvas, Matrix paramMatrix, Mask paramMask, BaseKeyframeAnimation paramBaseKeyframeAnimation1, BaseKeyframeAnimation paramBaseKeyframeAnimation2)
  {
    paramMask = (Path)paramBaseKeyframeAnimation1.getValue();
    path.set(paramMask);
    path.transform(paramMatrix);
    paramCanvas.drawPath(path, dstOutPaint);
  }
  
  private void buildParentLayerListIfNeeded()
  {
    if (parentLayers != null) {
      return;
    }
    if (parentLayer == null)
    {
      parentLayers = Collections.emptyList();
      return;
    }
    parentLayers = new ArrayList();
    for (BaseLayer localBaseLayer = parentLayer; localBaseLayer != null; localBaseLayer = parentLayer) {
      parentLayers.add(localBaseLayer);
    }
  }
  
  private void clearCanvas(Canvas paramCanvas)
  {
    Way.beginSection("Layer#clearLayer");
    paramCanvas.drawRect(rect.left - 1.0F, rect.top - 1.0F, rect.right + 1.0F, rect.bottom + 1.0F, clearPaint);
    Way.endSection("Layer#clearLayer");
  }
  
  static BaseLayer forModel(Layer paramLayer, LottieDrawable paramLottieDrawable, LottieComposition paramLottieComposition)
  {
    switch (2.$SwitchMap$com$airbnb$lottie$model$layer$Layer$LayerType[paramLayer.getLayerType().ordinal()])
    {
    default: 
      paramLottieDrawable = new StringBuilder();
      paramLottieDrawable.append("Unknown layer type ");
      paramLottieDrawable.append(paramLayer.getLayerType());
      Logger.warning(paramLottieDrawable.toString());
      return null;
    case 6: 
      return new TextLayer(paramLottieDrawable, paramLayer);
    case 5: 
      return new NullLayer(paramLottieDrawable, paramLayer);
    case 4: 
      return new ImageLayer(paramLottieDrawable, paramLayer);
    case 3: 
      return new SolidLayer(paramLottieDrawable, paramLayer);
    case 2: 
      return new CompositionLayer(paramLottieDrawable, paramLayer, paramLottieComposition.getPrecomps(paramLayer.getRefId()), paramLottieComposition);
    }
    return new ShapeLayer(paramLottieDrawable, paramLayer);
  }
  
  private void intersectBoundsWithMask(RectF paramRectF, Matrix paramMatrix)
  {
    maskBoundsRect.set(0.0F, 0.0F, 0.0F, 0.0F);
    if (!hasMasksOnThisLayer()) {
      return;
    }
    int j = mask.getMasks().size();
    int i = 0;
    while (i < j)
    {
      Mask localMask = (Mask)mask.getMasks().get(i);
      Path localPath = (Path)((BaseKeyframeAnimation)mask.getMaskAnimations().get(i)).getValue();
      path.set(localPath);
      path.transform(paramMatrix);
      switch (localMask.getMaskMode())
      {
      default: 
        break;
      case ???: 
      case ???: 
        if (!localMask.isInverted()) {
          break;
        }
      case ???: 
        return;
      }
      path.computeBounds(tempMaskBoundsRect, false);
      if (i == 0) {
        maskBoundsRect.set(tempMaskBoundsRect);
      } else {
        maskBoundsRect.set(Math.min(maskBoundsRect.left, tempMaskBoundsRect.left), Math.min(maskBoundsRect.top, tempMaskBoundsRect.top), Math.max(maskBoundsRect.right, tempMaskBoundsRect.right), Math.max(maskBoundsRect.bottom, tempMaskBoundsRect.bottom));
      }
      i += 1;
    }
    if (!paramRectF.intersect(maskBoundsRect)) {
      paramRectF.set(0.0F, 0.0F, 0.0F, 0.0F);
    }
  }
  
  private void intersectBoundsWithMatte(RectF paramRectF, Matrix paramMatrix)
  {
    if (!hasMatteOnThisLayer()) {
      return;
    }
    if (layerModel.getMatteType() == Layer.MatteType.INVERT) {
      return;
    }
    matteBoundsRect.set(0.0F, 0.0F, 0.0F, 0.0F);
    matteLayer.getBounds(matteBoundsRect, paramMatrix, true);
    if (!paramRectF.intersect(matteBoundsRect)) {
      paramRectF.set(0.0F, 0.0F, 0.0F, 0.0F);
    }
  }
  
  private void invalidateSelf()
  {
    lottieDrawable.invalidateSelf();
  }
  
  private void recordRenderTime(float paramFloat)
  {
    lottieDrawable.getComposition().getPerformanceTracker().recordRenderTime(layerModel.getName(), paramFloat);
  }
  
  private void saveLayerCompat(Canvas paramCanvas, RectF paramRectF, Paint paramPaint, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 23)
    {
      int i;
      if (paramBoolean) {
        i = 31;
      } else {
        i = 19;
      }
      paramCanvas.saveLayer(paramRectF, paramPaint, i);
      return;
    }
    paramCanvas.saveLayer(paramRectF, paramPaint);
  }
  
  private void setVisible(boolean paramBoolean)
  {
    if (paramBoolean != visible)
    {
      visible = paramBoolean;
      invalidateSelf();
    }
  }
  
  private void setupInOutAnimations()
  {
    boolean bool2 = layerModel.getInOutKeyframes().isEmpty();
    boolean bool1 = true;
    if (!bool2)
    {
      final FloatKeyframeAnimation localFloatKeyframeAnimation = new FloatKeyframeAnimation(layerModel.getInOutKeyframes());
      localFloatKeyframeAnimation.setIsDiscrete();
      localFloatKeyframeAnimation.addUpdateListener(new BaseKeyframeAnimation.AnimationListener()
      {
        public void onValueChanged()
        {
          BaseLayer localBaseLayer = BaseLayer.this;
          boolean bool;
          if (localFloatKeyframeAnimation.getFloatValue() == 1.0F) {
            bool = true;
          } else {
            bool = false;
          }
          localBaseLayer.setVisible(bool);
        }
      });
      if (((Float)localFloatKeyframeAnimation.getValue()).floatValue() != 1.0F) {
        bool1 = false;
      }
      setVisible(bool1);
      addAnimation(localFloatKeyframeAnimation);
      return;
    }
    setVisible(true);
  }
  
  public void addAnimation(BaseKeyframeAnimation paramBaseKeyframeAnimation)
  {
    if (paramBaseKeyframeAnimation == null) {
      return;
    }
    animations.add(paramBaseKeyframeAnimation);
  }
  
  public void addValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    transform.applyValueCallback(paramObject, paramLottieValueCallback);
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    Way.beginSection(drawTraceName);
    if ((visible) && (!layerModel.isHidden()))
    {
      buildParentLayerListIfNeeded();
      Way.beginSection("Layer#parentMatrix");
      matrix.reset();
      matrix.set(paramMatrix);
      int i = parentLayers.size() - 1;
      while (i >= 0)
      {
        matrix.preConcat(parentLayers.get(i)).transform.getMatrix());
        i -= 1;
      }
      Way.endSection("Layer#parentMatrix");
      if (transform.getOpacity() == null) {
        i = 100;
      } else {
        i = ((Integer)transform.getOpacity().getValue()).intValue();
      }
      paramInt = (int)(paramInt / 255.0F * i / 100.0F * 255.0F);
      if ((!hasMatteOnThisLayer()) && (!hasMasksOnThisLayer()))
      {
        matrix.preConcat(transform.getMatrix());
        Way.beginSection("Layer#drawLayer");
        drawLayer(paramCanvas, matrix, paramInt);
        Way.endSection("Layer#drawLayer");
        recordRenderTime(Way.endSection(drawTraceName));
        return;
      }
      Way.beginSection("Layer#computeBounds");
      getBounds(rect, matrix, false);
      intersectBoundsWithMatte(rect, paramMatrix);
      matrix.preConcat(transform.getMatrix());
      intersectBoundsWithMask(rect, matrix);
      if (!rect.intersect(0.0F, 0.0F, paramCanvas.getWidth(), paramCanvas.getHeight())) {
        rect.set(0.0F, 0.0F, 0.0F, 0.0F);
      }
      Way.endSection("Layer#computeBounds");
      if (!rect.isEmpty())
      {
        Way.beginSection("Layer#saveLayer");
        saveLayerCompat(paramCanvas, rect, contentPaint, true);
        Way.endSection("Layer#saveLayer");
        clearCanvas(paramCanvas);
        Way.beginSection("Layer#drawLayer");
        drawLayer(paramCanvas, matrix, paramInt);
        Way.endSection("Layer#drawLayer");
        if (hasMasksOnThisLayer()) {
          applyMasks(paramCanvas, matrix);
        }
        if (hasMatteOnThisLayer())
        {
          Way.beginSection("Layer#drawMatte");
          Way.beginSection("Layer#saveLayer");
          saveLayerCompat(paramCanvas, rect, mattePaint, false);
          Way.endSection("Layer#saveLayer");
          clearCanvas(paramCanvas);
          matteLayer.draw(paramCanvas, paramMatrix, paramInt);
          Way.beginSection("Layer#restoreLayer");
          paramCanvas.restore();
          Way.endSection("Layer#restoreLayer");
          Way.endSection("Layer#drawMatte");
        }
        Way.beginSection("Layer#restoreLayer");
        paramCanvas.restore();
        Way.endSection("Layer#restoreLayer");
      }
      recordRenderTime(Way.endSection(drawTraceName));
      return;
    }
    Way.endSection(drawTraceName);
  }
  
  abstract void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt);
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix, boolean paramBoolean)
  {
    rect.set(0.0F, 0.0F, 0.0F, 0.0F);
    buildParentLayerListIfNeeded();
    boundsMatrix.set(paramMatrix);
    if (paramBoolean)
    {
      if (parentLayers != null)
      {
        int i = parentLayers.size() - 1;
        while (i >= 0)
        {
          boundsMatrix.preConcat(parentLayers.get(i)).transform.getMatrix());
          i -= 1;
        }
      }
      if (parentLayer != null) {
        boundsMatrix.preConcat(parentLayer.transform.getMatrix());
      }
    }
    boundsMatrix.preConcat(transform.getMatrix());
  }
  
  Layer getLayerModel()
  {
    return layerModel;
  }
  
  public String getName()
  {
    return layerModel.getName();
  }
  
  boolean hasMasksOnThisLayer()
  {
    return (mask != null) && (!mask.getMaskAnimations().isEmpty());
  }
  
  boolean hasMatteOnThisLayer()
  {
    return matteLayer != null;
  }
  
  public void onValueChanged()
  {
    invalidateSelf();
  }
  
  public void removeAnimation(BaseKeyframeAnimation paramBaseKeyframeAnimation)
  {
    animations.remove(paramBaseKeyframeAnimation);
  }
  
  void resolveChildKeyPath(KeyPath paramKeyPath1, int paramInt, List paramList, KeyPath paramKeyPath2) {}
  
  public void resolveKeyPath(KeyPath paramKeyPath1, int paramInt, List paramList, KeyPath paramKeyPath2)
  {
    if (!paramKeyPath1.matches(getName(), paramInt)) {
      return;
    }
    KeyPath localKeyPath1 = paramKeyPath2;
    if (!"__container".equals(getName()))
    {
      KeyPath localKeyPath2 = paramKeyPath2.addKey(getName());
      paramKeyPath2 = localKeyPath2;
      localKeyPath1 = paramKeyPath2;
      if (paramKeyPath1.fullyResolvesTo(getName(), paramInt))
      {
        paramList.add(localKeyPath2.resolve(this));
        localKeyPath1 = paramKeyPath2;
      }
    }
    if (paramKeyPath1.propagateToChildren(getName(), paramInt)) {
      resolveChildKeyPath(paramKeyPath1, paramInt + paramKeyPath1.incrementDepthBy(getName(), paramInt), paramList, localKeyPath1);
    }
  }
  
  public void setContents(List paramList1, List paramList2) {}
  
  void setMatteLayer(BaseLayer paramBaseLayer)
  {
    matteLayer = paramBaseLayer;
  }
  
  void setParentLayer(BaseLayer paramBaseLayer)
  {
    parentLayer = paramBaseLayer;
  }
  
  void setProgress(float paramFloat)
  {
    transform.setProgress(paramFloat);
    MaskKeyframeAnimation localMaskKeyframeAnimation = mask;
    int j = 0;
    if (localMaskKeyframeAnimation != null)
    {
      i = 0;
      while (i < mask.getMaskAnimations().size())
      {
        ((BaseKeyframeAnimation)mask.getMaskAnimations().get(i)).setProgress(paramFloat);
        i += 1;
      }
    }
    float f = paramFloat;
    if (layerModel.getTimeStretch() != 0.0F) {
      f = paramFloat / layerModel.getTimeStretch();
    }
    int i = j;
    if (matteLayer != null)
    {
      paramFloat = matteLayer.layerModel.getTimeStretch();
      matteLayer.setProgress(paramFloat * f);
      i = j;
    }
    while (i < animations.size())
    {
      ((BaseKeyframeAnimation)animations.get(i)).setProgress(f);
      i += 1;
    }
  }
}
