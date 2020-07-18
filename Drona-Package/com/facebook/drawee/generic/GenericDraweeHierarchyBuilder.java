package com.facebook.drawee.generic;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import com.facebook.common.internal.Preconditions;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class GenericDraweeHierarchyBuilder
{
  public static final ScalingUtils.ScaleType DEFAULT_ACTUAL_IMAGE_SCALE_TYPE = ScalingUtils.ScaleType.CENTER_CROP;
  public static final int DEFAULT_FADE_DURATION = 300;
  public static final ScalingUtils.ScaleType DEFAULT_SCALE_TYPE = ScalingUtils.ScaleType.CENTER_INSIDE;
  @Nullable
  private ColorFilter mActualImageColorFilter;
  @Nullable
  private PointF mActualImageFocusPoint;
  @Nullable
  private Matrix mActualImageMatrix;
  @Nullable
  private ScalingUtils.ScaleType mActualImageScaleType;
  @Nullable
  private Drawable mBackground;
  private float mDesiredAspectRatio;
  private int mFadeDuration;
  @Nullable
  private Drawable mFailureImage;
  @Nullable
  private ScalingUtils.ScaleType mFailureImageScaleType;
  @Nullable
  private List<Drawable> mOverlays;
  @Nullable
  private Drawable mPlaceholderImage;
  @Nullable
  private ScalingUtils.ScaleType mPlaceholderImageScaleType;
  @Nullable
  private Drawable mPressedStateOverlay;
  @Nullable
  private Drawable mProgressBarImage;
  @Nullable
  private ScalingUtils.ScaleType mProgressBarImageScaleType;
  private Resources mResources;
  @Nullable
  private Drawable mRetryImage;
  @Nullable
  private ScalingUtils.ScaleType mRetryImageScaleType;
  @Nullable
  private RoundingParams mRoundingParams;
  
  public GenericDraweeHierarchyBuilder(Resources paramResources)
  {
    mResources = paramResources;
    init();
  }
  
  private void init()
  {
    mFadeDuration = 300;
    mDesiredAspectRatio = 0.0F;
    mPlaceholderImage = null;
    mPlaceholderImageScaleType = DEFAULT_SCALE_TYPE;
    mRetryImage = null;
    mRetryImageScaleType = DEFAULT_SCALE_TYPE;
    mFailureImage = null;
    mFailureImageScaleType = DEFAULT_SCALE_TYPE;
    mProgressBarImage = null;
    mProgressBarImageScaleType = DEFAULT_SCALE_TYPE;
    mActualImageScaleType = DEFAULT_ACTUAL_IMAGE_SCALE_TYPE;
    mActualImageMatrix = null;
    mActualImageFocusPoint = null;
    mActualImageColorFilter = null;
    mBackground = null;
    mOverlays = null;
    mPressedStateOverlay = null;
    mRoundingParams = null;
  }
  
  public static GenericDraweeHierarchyBuilder newInstance(Resources paramResources)
  {
    return new GenericDraweeHierarchyBuilder(paramResources);
  }
  
  private void validate()
  {
    if (mOverlays != null)
    {
      Iterator localIterator = mOverlays.iterator();
      while (localIterator.hasNext()) {
        Preconditions.checkNotNull((Drawable)localIterator.next());
      }
    }
  }
  
  public GenericDraweeHierarchy build()
  {
    validate();
    return new GenericDraweeHierarchy(this);
  }
  
  public ColorFilter getActualImageColorFilter()
  {
    return mActualImageColorFilter;
  }
  
  public PointF getActualImageFocusPoint()
  {
    return mActualImageFocusPoint;
  }
  
  public ScalingUtils.ScaleType getActualImageScaleType()
  {
    return mActualImageScaleType;
  }
  
  public Drawable getBackground()
  {
    return mBackground;
  }
  
  public float getDesiredAspectRatio()
  {
    return mDesiredAspectRatio;
  }
  
  public int getFadeDuration()
  {
    return mFadeDuration;
  }
  
  public Drawable getFailureImage()
  {
    return mFailureImage;
  }
  
  public ScalingUtils.ScaleType getFailureImageScaleType()
  {
    return mFailureImageScaleType;
  }
  
  public List getOverlays()
  {
    return mOverlays;
  }
  
  public Drawable getPlaceholderImage()
  {
    return mPlaceholderImage;
  }
  
  public ScalingUtils.ScaleType getPlaceholderImageScaleType()
  {
    return mPlaceholderImageScaleType;
  }
  
  public Drawable getPressedStateOverlay()
  {
    return mPressedStateOverlay;
  }
  
  public Drawable getProgressBarImage()
  {
    return mProgressBarImage;
  }
  
  public ScalingUtils.ScaleType getProgressBarImageScaleType()
  {
    return mProgressBarImageScaleType;
  }
  
  public Resources getResources()
  {
    return mResources;
  }
  
  public Drawable getRetryImage()
  {
    return mRetryImage;
  }
  
  public ScalingUtils.ScaleType getRetryImageScaleType()
  {
    return mRetryImageScaleType;
  }
  
  public RoundingParams getRoundingParams()
  {
    return mRoundingParams;
  }
  
  public GenericDraweeHierarchyBuilder reset()
  {
    init();
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setActualImageColorFilter(ColorFilter paramColorFilter)
  {
    mActualImageColorFilter = paramColorFilter;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setActualImageFocusPoint(PointF paramPointF)
  {
    mActualImageFocusPoint = paramPointF;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setActualImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mActualImageScaleType = paramScaleType;
    mActualImageMatrix = null;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setBackground(Drawable paramDrawable)
  {
    mBackground = paramDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setDesiredAspectRatio(float paramFloat)
  {
    mDesiredAspectRatio = paramFloat;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFadeDuration(int paramInt)
  {
    mFadeDuration = paramInt;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFailureImage(int paramInt)
  {
    mFailureImage = mResources.getDrawable(paramInt);
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFailureImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    mFailureImage = mResources.getDrawable(paramInt);
    mFailureImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFailureImage(Drawable paramDrawable)
  {
    mFailureImage = paramDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFailureImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    mFailureImage = paramDrawable;
    mFailureImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setFailureImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mFailureImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setOverlay(Drawable paramDrawable)
  {
    if (paramDrawable == null)
    {
      mOverlays = null;
      return this;
    }
    mOverlays = Arrays.asList(new Drawable[] { paramDrawable });
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setOverlays(List paramList)
  {
    mOverlays = paramList;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPlaceholderImage(int paramInt)
  {
    mPlaceholderImage = mResources.getDrawable(paramInt);
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPlaceholderImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    mPlaceholderImage = mResources.getDrawable(paramInt);
    mPlaceholderImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPlaceholderImage(Drawable paramDrawable)
  {
    mPlaceholderImage = paramDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPlaceholderImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    mPlaceholderImage = paramDrawable;
    mPlaceholderImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPlaceholderImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mPlaceholderImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setPressedStateOverlay(Drawable paramDrawable)
  {
    if (paramDrawable == null)
    {
      mPressedStateOverlay = null;
      return this;
    }
    StateListDrawable localStateListDrawable = new StateListDrawable();
    localStateListDrawable.addState(new int[] { 16842919 }, paramDrawable);
    mPressedStateOverlay = localStateListDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setProgressBarImage(int paramInt)
  {
    mProgressBarImage = mResources.getDrawable(paramInt);
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setProgressBarImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    mProgressBarImage = mResources.getDrawable(paramInt);
    mProgressBarImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setProgressBarImage(Drawable paramDrawable)
  {
    mProgressBarImage = paramDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setProgressBarImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    mProgressBarImage = paramDrawable;
    mProgressBarImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setProgressBarImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mProgressBarImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRetryImage(int paramInt)
  {
    mRetryImage = mResources.getDrawable(paramInt);
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRetryImage(int paramInt, ScalingUtils.ScaleType paramScaleType)
  {
    mRetryImage = mResources.getDrawable(paramInt);
    mRetryImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRetryImage(Drawable paramDrawable)
  {
    mRetryImage = paramDrawable;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRetryImage(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    mRetryImage = paramDrawable;
    mRetryImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRetryImageScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mRetryImageScaleType = paramScaleType;
    return this;
  }
  
  public GenericDraweeHierarchyBuilder setRoundingParams(RoundingParams paramRoundingParams)
  {
    mRoundingParams = paramRoundingParams;
    return this;
  }
}
