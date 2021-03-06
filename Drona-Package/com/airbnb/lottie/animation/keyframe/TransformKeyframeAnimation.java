package com.airbnb.lottie.animation.keyframe;

import android.graphics.Matrix;
import android.graphics.PointF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatableScaleValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import com.airbnb.lottie.value.ScaleXY;
import java.util.Collections;

public class TransformKeyframeAnimation
{
  @NonNull
  private BaseKeyframeAnimation<PointF, PointF> anchorPoint;
  @Nullable
  private BaseKeyframeAnimation<?, Float> endOpacity;
  private final Matrix matrix = new Matrix();
  @NonNull
  private BaseKeyframeAnimation<Integer, Integer> opacity;
  @NonNull
  private BaseKeyframeAnimation<?, PointF> position;
  @NonNull
  private BaseKeyframeAnimation<Float, Float> rotation;
  @NonNull
  private BaseKeyframeAnimation<ScaleXY, ScaleXY> scale;
  @Nullable
  private FloatKeyframeAnimation skew;
  @Nullable
  private FloatKeyframeAnimation skewAngle;
  private final Matrix skewMatrix1;
  private final Matrix skewMatrix2;
  private final Matrix skewMatrix3;
  private final float[] skewValues;
  @Nullable
  private BaseKeyframeAnimation<?, Float> startOpacity;
  
  public TransformKeyframeAnimation(AnimatableTransform paramAnimatableTransform)
  {
    Object localObject;
    if (paramAnimatableTransform.getAnchorPoint() == null) {
      localObject = null;
    } else {
      localObject = paramAnimatableTransform.getAnchorPoint().createAnimation();
    }
    anchorPoint = ((BaseKeyframeAnimation)localObject);
    if (paramAnimatableTransform.getPosition() == null) {
      localObject = null;
    } else {
      localObject = paramAnimatableTransform.getPosition().createAnimation();
    }
    position = ((BaseKeyframeAnimation)localObject);
    if (paramAnimatableTransform.getScale() == null) {
      localObject = null;
    } else {
      localObject = paramAnimatableTransform.getScale().createAnimation();
    }
    scale = ((BaseKeyframeAnimation)localObject);
    if (paramAnimatableTransform.getRotation() == null) {
      localObject = null;
    } else {
      localObject = paramAnimatableTransform.getRotation().createAnimation();
    }
    rotation = ((BaseKeyframeAnimation)localObject);
    if (paramAnimatableTransform.getSkew() == null) {
      localObject = null;
    } else {
      localObject = (FloatKeyframeAnimation)paramAnimatableTransform.getSkew().createAnimation();
    }
    skew = ((FloatKeyframeAnimation)localObject);
    if (skew != null)
    {
      skewMatrix1 = new Matrix();
      skewMatrix2 = new Matrix();
      skewMatrix3 = new Matrix();
      skewValues = new float[9];
    }
    else
    {
      skewMatrix1 = null;
      skewMatrix2 = null;
      skewMatrix3 = null;
      skewValues = null;
    }
    if (paramAnimatableTransform.getSkewAngle() == null) {
      localObject = null;
    } else {
      localObject = (FloatKeyframeAnimation)paramAnimatableTransform.getSkewAngle().createAnimation();
    }
    skewAngle = ((FloatKeyframeAnimation)localObject);
    if (paramAnimatableTransform.getOpacity() != null) {
      opacity = paramAnimatableTransform.getOpacity().createAnimation();
    }
    if (paramAnimatableTransform.getStartOpacity() != null) {
      startOpacity = paramAnimatableTransform.getStartOpacity().createAnimation();
    } else {
      startOpacity = null;
    }
    if (paramAnimatableTransform.getEndOpacity() != null)
    {
      endOpacity = paramAnimatableTransform.getEndOpacity().createAnimation();
      return;
    }
    endOpacity = null;
  }
  
  private void clearSkewValues()
  {
    int i = 0;
    while (i < 9)
    {
      skewValues[i] = 0.0F;
      i += 1;
    }
  }
  
  public void addAnimationsToLayer(BaseLayer paramBaseLayer)
  {
    paramBaseLayer.addAnimation(opacity);
    paramBaseLayer.addAnimation(startOpacity);
    paramBaseLayer.addAnimation(endOpacity);
    paramBaseLayer.addAnimation(anchorPoint);
    paramBaseLayer.addAnimation(position);
    paramBaseLayer.addAnimation(scale);
    paramBaseLayer.addAnimation(rotation);
    paramBaseLayer.addAnimation(skew);
    paramBaseLayer.addAnimation(skewAngle);
  }
  
  public void addListener(BaseKeyframeAnimation.AnimationListener paramAnimationListener)
  {
    if (opacity != null) {
      opacity.addUpdateListener(paramAnimationListener);
    }
    if (startOpacity != null) {
      startOpacity.addUpdateListener(paramAnimationListener);
    }
    if (endOpacity != null) {
      endOpacity.addUpdateListener(paramAnimationListener);
    }
    if (anchorPoint != null) {
      anchorPoint.addUpdateListener(paramAnimationListener);
    }
    if (position != null) {
      position.addUpdateListener(paramAnimationListener);
    }
    if (scale != null) {
      scale.addUpdateListener(paramAnimationListener);
    }
    if (rotation != null) {
      rotation.addUpdateListener(paramAnimationListener);
    }
    if (skew != null) {
      skew.addUpdateListener(paramAnimationListener);
    }
    if (skewAngle != null) {
      skewAngle.addUpdateListener(paramAnimationListener);
    }
  }
  
  public boolean applyValueCallback(Object paramObject, LottieValueCallback paramLottieValueCallback)
  {
    if (paramObject == LottieProperty.TRANSFORM_ANCHOR_POINT)
    {
      if (anchorPoint == null) {
        anchorPoint = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, new PointF());
      } else {
        anchorPoint.setValueCallback(paramLottieValueCallback);
      }
    }
    else if (paramObject == LottieProperty.TRANSFORM_POSITION)
    {
      if (position == null) {
        position = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, new PointF());
      } else {
        position.setValueCallback(paramLottieValueCallback);
      }
    }
    else if (paramObject == LottieProperty.TRANSFORM_SCALE)
    {
      if (scale == null) {
        scale = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, new ScaleXY());
      } else {
        scale.setValueCallback(paramLottieValueCallback);
      }
    }
    else if (paramObject == LottieProperty.TRANSFORM_ROTATION)
    {
      if (rotation == null) {
        rotation = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, Float.valueOf(0.0F));
      } else {
        rotation.setValueCallback(paramLottieValueCallback);
      }
    }
    else if (paramObject == LottieProperty.TRANSFORM_OPACITY)
    {
      if (opacity == null) {
        opacity = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, Integer.valueOf(100));
      } else {
        opacity.setValueCallback(paramLottieValueCallback);
      }
    }
    else if ((paramObject == LottieProperty.TRANSFORM_START_OPACITY) && (startOpacity != null))
    {
      if (startOpacity == null) {
        startOpacity = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, Integer.valueOf(100));
      } else {
        startOpacity.setValueCallback(paramLottieValueCallback);
      }
    }
    else if ((paramObject == LottieProperty.TRANSFORM_END_OPACITY) && (endOpacity != null))
    {
      if (endOpacity == null) {
        endOpacity = new ValueCallbackKeyframeAnimation(paramLottieValueCallback, Integer.valueOf(100));
      } else {
        endOpacity.setValueCallback(paramLottieValueCallback);
      }
    }
    else if ((paramObject == LottieProperty.TRANSFORM_SKEW) && (skew != null))
    {
      if (skew == null) {
        skew = new FloatKeyframeAnimation(Collections.singletonList(new Keyframe(Float.valueOf(0.0F))));
      }
      skew.setValueCallback(paramLottieValueCallback);
    }
    else
    {
      if ((paramObject != LottieProperty.TRANSFORM_SKEW_ANGLE) || (skewAngle == null)) {
        break label447;
      }
      if (skewAngle == null) {
        skewAngle = new FloatKeyframeAnimation(Collections.singletonList(new Keyframe(Float.valueOf(0.0F))));
      }
      skewAngle.setValueCallback(paramLottieValueCallback);
    }
    return true;
    label447:
    return false;
  }
  
  public BaseKeyframeAnimation getEndOpacity()
  {
    return endOpacity;
  }
  
  public Matrix getMatrix()
  {
    matrix.reset();
    Object localObject;
    if (position != null)
    {
      localObject = (PointF)position.getValue();
      if ((x != 0.0F) || (y != 0.0F)) {
        matrix.preTranslate(x, y);
      }
    }
    float f1;
    if (rotation != null)
    {
      if ((rotation instanceof ValueCallbackKeyframeAnimation)) {
        f1 = ((Float)rotation.getValue()).floatValue();
      } else {
        f1 = ((FloatKeyframeAnimation)rotation).getFloatValue();
      }
      if (f1 != 0.0F) {
        matrix.preRotate(f1);
      }
    }
    if (skew != null)
    {
      if (skewAngle == null) {
        f1 = 0.0F;
      } else {
        f1 = (float)Math.cos(Math.toRadians(-skewAngle.getFloatValue() + 90.0F));
      }
      float f2;
      if (skewAngle == null) {
        f2 = 1.0F;
      } else {
        f2 = (float)Math.sin(Math.toRadians(-skewAngle.getFloatValue() + 90.0F));
      }
      float f3 = (float)Math.tan(Math.toRadians(skew.getFloatValue()));
      clearSkewValues();
      skewValues[0] = f1;
      skewValues[1] = f2;
      localObject = skewValues;
      float f4 = -f2;
      localObject[3] = f4;
      skewValues[4] = f1;
      skewValues[8] = 1.0F;
      skewMatrix1.setValues(skewValues);
      clearSkewValues();
      skewValues[0] = 1.0F;
      skewValues[3] = f3;
      skewValues[4] = 1.0F;
      skewValues[8] = 1.0F;
      skewMatrix2.setValues(skewValues);
      clearSkewValues();
      skewValues[0] = f1;
      skewValues[1] = f4;
      skewValues[3] = f2;
      skewValues[4] = f1;
      skewValues[8] = 1.0F;
      skewMatrix3.setValues(skewValues);
      skewMatrix2.preConcat(skewMatrix1);
      skewMatrix3.preConcat(skewMatrix2);
      matrix.preConcat(skewMatrix3);
    }
    if (scale != null)
    {
      localObject = (ScaleXY)scale.getValue();
      if ((((ScaleXY)localObject).getScaleX() != 1.0F) || (((ScaleXY)localObject).getScaleY() != 1.0F)) {
        matrix.preScale(((ScaleXY)localObject).getScaleX(), ((ScaleXY)localObject).getScaleY());
      }
    }
    if (anchorPoint != null)
    {
      localObject = (PointF)anchorPoint.getValue();
      if ((x != 0.0F) || (y != 0.0F)) {
        matrix.preTranslate(-x, -y);
      }
    }
    return matrix;
  }
  
  public Matrix getMatrixForRepeater(float paramFloat)
  {
    Object localObject1 = position;
    TransformKeyframeAnimation localTransformKeyframeAnimation = this;
    Object localObject3 = null;
    if (localObject1 == null) {
      localObject1 = null;
    } else {
      localObject1 = (PointF)position.getValue();
    }
    localTransformKeyframeAnimation = this;
    BaseKeyframeAnimation localBaseKeyframeAnimation = scale;
    Object localObject2 = localTransformKeyframeAnimation;
    if (localBaseKeyframeAnimation == null) {
      localObject2 = null;
    } else {
      localObject2 = (ScaleXY)scale.getValue();
    }
    matrix.reset();
    if (localObject1 != null) {
      matrix.preTranslate(x * paramFloat, y * paramFloat);
    }
    if (localObject2 != null)
    {
      localObject1 = matrix;
      double d1 = ((ScaleXY)localObject2).getScaleX();
      double d2 = paramFloat;
      ((Matrix)localObject1).preScale((float)Math.pow(d1, d2), (float)Math.pow(((ScaleXY)localObject2).getScaleY(), d2));
    }
    localObject2 = rotation;
    localObject1 = localTransformKeyframeAnimation;
    if (localObject2 != null)
    {
      float f3 = ((Float)rotation.getValue()).floatValue();
      localObject2 = anchorPoint;
      if (localObject2 == null) {
        localObject1 = localObject3;
      } else {
        localObject1 = (PointF)anchorPoint.getValue();
      }
      localObject2 = matrix;
      float f2 = 0.0F;
      float f1;
      if (localObject1 == null) {
        f1 = 0.0F;
      } else {
        f1 = x;
      }
      if (localObject1 != null) {
        f2 = y;
      }
      ((Matrix)localObject2).preRotate(f3 * paramFloat, f1, f2);
    }
    return matrix;
  }
  
  public BaseKeyframeAnimation getOpacity()
  {
    return opacity;
  }
  
  public BaseKeyframeAnimation getStartOpacity()
  {
    return startOpacity;
  }
  
  public void setProgress(float paramFloat)
  {
    if (opacity != null) {
      opacity.setProgress(paramFloat);
    }
    if (startOpacity != null) {
      startOpacity.setProgress(paramFloat);
    }
    if (endOpacity != null) {
      endOpacity.setProgress(paramFloat);
    }
    if (anchorPoint != null) {
      anchorPoint.setProgress(paramFloat);
    }
    if (position != null) {
      position.setProgress(paramFloat);
    }
    if (scale != null) {
      scale.setProgress(paramFloat);
    }
    if (rotation != null) {
      rotation.setProgress(paramFloat);
    }
    if (skew != null) {
      skew.setProgress(paramFloat);
    }
    if (skewAngle != null) {
      skewAngle.setProgress(paramFloat);
    }
  }
}
