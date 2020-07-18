package com.facebook.drawee.generic;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Preconditions;
import com.facebook.drawee.drawable.ArrayDrawable;
import com.facebook.drawee.drawable.DrawableParent;
import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.drawable.MatrixDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.interfaces.SettableDraweeHierarchy;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class GenericDraweeHierarchy
  implements SettableDraweeHierarchy
{
  private static final int ACTUAL_IMAGE_INDEX = 2;
  private static final int BACKGROUND_IMAGE_INDEX = 0;
  private static final int FAILURE_IMAGE_INDEX = 5;
  private static final int OVERLAY_IMAGES_INDEX = 6;
  private static final int PLACEHOLDER_IMAGE_INDEX = 1;
  private static final int PROGRESS_BAR_IMAGE_INDEX = 3;
  private static final int RETRY_IMAGE_INDEX = 4;
  private final ForwardingDrawable mActualImageWrapper;
  private final Drawable mEmptyActualImageDrawable = new ColorDrawable(0);
  private final FadeDrawable mFadeDrawable;
  private final Resources mResources;
  @Nullable
  private RoundingParams mRoundingParams;
  private final RootDrawable mTopLevelDrawable;
  
  GenericDraweeHierarchy(GenericDraweeHierarchyBuilder paramGenericDraweeHierarchyBuilder)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("GenericDraweeHierarchy()");
    }
    mResources = paramGenericDraweeHierarchyBuilder.getResources();
    mRoundingParams = paramGenericDraweeHierarchyBuilder.getRoundingParams();
    mActualImageWrapper = new ForwardingDrawable(mEmptyActualImageDrawable);
    Object localObject = paramGenericDraweeHierarchyBuilder.getOverlays();
    int k = 1;
    int i;
    if (localObject != null) {
      i = paramGenericDraweeHierarchyBuilder.getOverlays().size();
    } else {
      i = 1;
    }
    int j;
    if (paramGenericDraweeHierarchyBuilder.getPressedStateOverlay() != null) {
      j = 1;
    } else {
      j = 0;
    }
    i += j;
    localObject = new Drawable[i + 6];
    localObject[0] = buildBranch(paramGenericDraweeHierarchyBuilder.getBackground(), null);
    localObject[1] = buildBranch(paramGenericDraweeHierarchyBuilder.getPlaceholderImage(), paramGenericDraweeHierarchyBuilder.getPlaceholderImageScaleType());
    localObject[2] = buildActualImageBranch(mActualImageWrapper, paramGenericDraweeHierarchyBuilder.getActualImageScaleType(), paramGenericDraweeHierarchyBuilder.getActualImageFocusPoint(), paramGenericDraweeHierarchyBuilder.getActualImageColorFilter());
    localObject[3] = buildBranch(paramGenericDraweeHierarchyBuilder.getProgressBarImage(), paramGenericDraweeHierarchyBuilder.getProgressBarImageScaleType());
    localObject[4] = buildBranch(paramGenericDraweeHierarchyBuilder.getRetryImage(), paramGenericDraweeHierarchyBuilder.getRetryImageScaleType());
    localObject[5] = buildBranch(paramGenericDraweeHierarchyBuilder.getFailureImage(), paramGenericDraweeHierarchyBuilder.getFailureImageScaleType());
    if (i > 0)
    {
      j = k;
      if (paramGenericDraweeHierarchyBuilder.getOverlays() != null)
      {
        Iterator localIterator = paramGenericDraweeHierarchyBuilder.getOverlays().iterator();
        i = 0;
        for (;;)
        {
          j = i;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject[(i + 6)] = buildBranch((Drawable)localIterator.next(), null);
          i += 1;
        }
      }
      if (paramGenericDraweeHierarchyBuilder.getPressedStateOverlay() != null) {
        localObject[(j + 6)] = buildBranch(paramGenericDraweeHierarchyBuilder.getPressedStateOverlay(), null);
      }
    }
    mFadeDrawable = new FadeDrawable((Drawable[])localObject);
    mFadeDrawable.setTransitionDuration(paramGenericDraweeHierarchyBuilder.getFadeDuration());
    mTopLevelDrawable = new RootDrawable(WrappingUtils.maybeWrapWithRoundedOverlayColor(mFadeDrawable, mRoundingParams));
    mTopLevelDrawable.mutate();
    resetFade();
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  private Drawable buildActualImageBranch(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType, PointF paramPointF, ColorFilter paramColorFilter)
  {
    paramDrawable.setColorFilter(paramColorFilter);
    return WrappingUtils.maybeWrapWithScaleType(paramDrawable, paramScaleType, paramPointF);
  }
  
  private Drawable buildBranch(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    return WrappingUtils.maybeWrapWithScaleType(WrappingUtils.maybeApplyLeafRounding(paramDrawable, mRoundingParams, mResources), paramScaleType);
  }
  
  private void fadeInLayer(int paramInt)
  {
    if (paramInt >= 0) {
      mFadeDrawable.fadeInLayer(paramInt);
    }
  }
  
  private void fadeOutBranches()
  {
    fadeOutLayer(1);
    fadeOutLayer(2);
    fadeOutLayer(3);
    fadeOutLayer(4);
    fadeOutLayer(5);
  }
  
  private void fadeOutLayer(int paramInt)
  {
    if (paramInt >= 0) {
      mFadeDrawable.fadeOutLayer(paramInt);
    }
  }
  
  private DrawableParent getParentDrawableAtIndex(int paramInt)
  {
    DrawableParent localDrawableParent = mFadeDrawable.getDrawableParentForIndex(paramInt);
    Object localObject = localDrawableParent;
    if ((localDrawableParent.getDrawable() instanceof MatrixDrawable)) {
      localObject = (MatrixDrawable)localDrawableParent.getDrawable();
    }
    if ((((DrawableParent)localObject).getDrawable() instanceof ScaleTypeDrawable)) {
      return (ScaleTypeDrawable)((DrawableParent)localObject).getDrawable();
    }
    return localObject;
  }
  
  private ScaleTypeDrawable getScaleTypeDrawableAtIndex(int paramInt)
  {
    DrawableParent localDrawableParent = getParentDrawableAtIndex(paramInt);
    if ((localDrawableParent instanceof ScaleTypeDrawable)) {
      return (ScaleTypeDrawable)localDrawableParent;
    }
    return WrappingUtils.wrapChildWithScaleType(localDrawableParent, ScalingUtils.ScaleType.FIT_XY);
  }
  
  private boolean hasScaleTypeDrawableAtIndex(int paramInt)
  {
    return getParentDrawableAtIndex(paramInt) instanceof ScaleTypeDrawable;
  }
  
  private void resetActualImages()
  {
    mActualImageWrapper.setDrawable(mEmptyActualImageDrawable);
  }
  
  private void resetFade()
  {
    if (mFadeDrawable != null)
    {
      mFadeDrawable.beginBatchMode();
      mFadeDrawable.fadeInAllLayers();
      fadeOutBranches();
      fadeInLayer(1);
      mFadeDrawable.finishTransitionImmediately();
      mFadeDrawable.endBatchMode();
    }
  }
  
  private void setChildDrawableAtIndex(int paramInt, Drawable paramDrawable)
  {
    if (paramDrawable == null)
    {
      mFadeDrawable.setDrawable(paramInt, null);
      return;
    }
    paramDrawable = WrappingUtils.maybeApplyLeafRounding(paramDrawable, mRoundingParams, mResources);
    getParentDrawableAtIndex(paramInt).setDrawable(paramDrawable);
  }
  
  private void setProgress(float paramFloat)
  {
    Drawable localDrawable = mFadeDrawable.getDrawable(3);
    if (localDrawable == null) {
      return;
    }
    if (paramFloat >= 0.999F)
    {
      if ((localDrawable instanceof Animatable)) {
        ((Animatable)localDrawable).stop();
      }
      fadeOutLayer(3);
    }
    else
    {
      if ((localDrawable instanceof Animatable)) {
        ((Animatable)localDrawable).start();
      }
      fadeInLayer(3);
    }
    localDrawable.setLevel(Math.round(paramFloat * 10000.0F));
  }
  
  public void getActualImageBounds(RectF paramRectF)
  {
    mActualImageWrapper.getTransformedBounds(paramRectF);
  }
  
  public ScalingUtils.ScaleType getActualImageScaleType()
  {
    if (!hasScaleTypeDrawableAtIndex(2)) {
      return null;
    }
    return getScaleTypeDrawableAtIndex(2).getScaleType();
  }
  
  public int getFadeDuration()
  {
    return mFadeDrawable.getTransitionDuration();
  }
  
  public RoundingParams getRoundingParams()
  {
    return mRoundingParams;
  }
  
  public Drawable getTopLevelDrawable()
  {
    return mTopLevelDrawable;
  }
  
  public boolean hasImage()
  {
    return mActualImageWrapper.getDrawable() != mEmptyActualImageDrawable;
  }
  
  public boolean hasPlaceholderImage()
  {
    return mFadeDrawable.getDrawable(1) != null;
  }
  
  public void reset()
  {
    resetActualImages();
    resetFade();
  }
  
  public void setActualImageColorFilter(ColorFilter paramColorFilter)
  {
    mActualImageWrapper.setColorFilter(paramColorFilter);
  }
  
  public void setActualImageFocusPoint(PointF paramPointF)
  {
    Preconditions.checkNotNull(paramPointF);
    getScaleTypeDrawableAtIndex(2).setFocusPoint(paramPointF);
  }
  
  public void setActualImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    Preconditions.checkNotNull(paramScaleType);
    getScaleTypeDrawableAtIndex(2).setScaleType(paramScaleType);
  }
  
  public void setBackgroundImage(Drawable paramDrawable)
  {
    setChildDrawableAtIndex(0, paramDrawable);
  }
  
  public void setControllerOverlay(Drawable paramDrawable)
  {
    mTopLevelDrawable.setControllerOverlay(paramDrawable);
  }
  
  public void setFadeDuration(int paramInt)
  {
    mFadeDrawable.setTransitionDuration(paramInt);
  }
  
  public void setFailure(Throwable paramThrowable)
  {
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    if (mFadeDrawable.getDrawable(5) != null) {
      fadeInLayer(5);
    } else {
      fadeInLayer(1);
    }
    mFadeDrawable.endBatchMode();
  }
  
  public void setFailureImage(int paramInt)
  {
    setFailureImage(mResources.getDrawable(paramInt));
  }
  
  public void setFailureImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    setFailureImage(mResources.getDrawable(paramInt), paramScaleType);
  }
  
  public void setFailureImage(Drawable paramDrawable)
  {
    setChildDrawableAtIndex(5, paramDrawable);
  }
  
  public void setFailureImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    setChildDrawableAtIndex(5, paramDrawable);
    getScaleTypeDrawableAtIndex(5).setScaleType(paramScaleType);
  }
  
  public void setImage(Drawable paramDrawable, float paramFloat, boolean paramBoolean)
  {
    paramDrawable = WrappingUtils.maybeApplyLeafRounding(paramDrawable, mRoundingParams, mResources);
    paramDrawable.mutate();
    mActualImageWrapper.setDrawable(paramDrawable);
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    fadeInLayer(2);
    setProgress(paramFloat);
    if (paramBoolean) {
      mFadeDrawable.finishTransitionImmediately();
    }
    mFadeDrawable.endBatchMode();
  }
  
  public void setOverlayImage(int paramInt, Drawable paramDrawable)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt + 6 < mFadeDrawable.getNumberOfLayers())) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "The given index does not correspond to an overlay image.");
    setChildDrawableAtIndex(paramInt + 6, paramDrawable);
  }
  
  public void setOverlayImage(Drawable paramDrawable)
  {
    setOverlayImage(0, paramDrawable);
  }
  
  public void setPlaceholderImage(int paramInt)
  {
    setPlaceholderImage(mResources.getDrawable(paramInt));
  }
  
  public void setPlaceholderImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    setPlaceholderImage(mResources.getDrawable(paramInt), paramScaleType);
  }
  
  public void setPlaceholderImage(Drawable paramDrawable)
  {
    setChildDrawableAtIndex(1, paramDrawable);
  }
  
  public void setPlaceholderImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    setChildDrawableAtIndex(1, paramDrawable);
    getScaleTypeDrawableAtIndex(1).setScaleType(paramScaleType);
  }
  
  public void setPlaceholderImageFocusPoint(PointF paramPointF)
  {
    Preconditions.checkNotNull(paramPointF);
    getScaleTypeDrawableAtIndex(1).setFocusPoint(paramPointF);
  }
  
  public void setProgress(float paramFloat, boolean paramBoolean)
  {
    if (mFadeDrawable.getDrawable(3) == null) {
      return;
    }
    mFadeDrawable.beginBatchMode();
    setProgress(paramFloat);
    if (paramBoolean) {
      mFadeDrawable.finishTransitionImmediately();
    }
    mFadeDrawable.endBatchMode();
  }
  
  public void setProgressBarImage(int paramInt)
  {
    setProgressBarImage(mResources.getDrawable(paramInt));
  }
  
  public void setProgressBarImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    setProgressBarImage(mResources.getDrawable(paramInt), paramScaleType);
  }
  
  public void setProgressBarImage(Drawable paramDrawable)
  {
    setChildDrawableAtIndex(3, paramDrawable);
  }
  
  public void setProgressBarImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    setChildDrawableAtIndex(3, paramDrawable);
    getScaleTypeDrawableAtIndex(3).setScaleType(paramScaleType);
  }
  
  public void setRetry(Throwable paramThrowable)
  {
    mFadeDrawable.beginBatchMode();
    fadeOutBranches();
    if (mFadeDrawable.getDrawable(4) != null) {
      fadeInLayer(4);
    } else {
      fadeInLayer(1);
    }
    mFadeDrawable.endBatchMode();
  }
  
  public void setRetryImage(int paramInt)
  {
    setRetryImage(mResources.getDrawable(paramInt));
  }
  
  public void setRetryImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    setRetryImage(mResources.getDrawable(paramInt), paramScaleType);
  }
  
  public void setRetryImage(Drawable paramDrawable)
  {
    setChildDrawableAtIndex(4, paramDrawable);
  }
  
  public void setRetryImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    setChildDrawableAtIndex(4, paramDrawable);
    getScaleTypeDrawableAtIndex(4).setScaleType(paramScaleType);
  }
  
  public void setRoundingParams(RoundingParams paramRoundingParams)
  {
    mRoundingParams = paramRoundingParams;
    WrappingUtils.updateOverlayColorRounding(mTopLevelDrawable, mRoundingParams);
    int i = 0;
    while (i < mFadeDrawable.getNumberOfLayers())
    {
      WrappingUtils.updateLeafRounding(getParentDrawableAtIndex(i), mRoundingParams, mResources);
      i += 1;
    }
  }
}
