package com.facebook.drawee.generic;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import com.facebook.drawee.R.styleable;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.imagepipeline.systrace.FrescoSystrace;

public class GenericDraweeHierarchyInflater
{
  public GenericDraweeHierarchyInflater() {}
  
  private static Drawable getDrawable(Context paramContext, TypedArray paramTypedArray, int paramInt)
  {
    paramInt = paramTypedArray.getResourceId(paramInt, 0);
    if (paramInt == 0) {
      return null;
    }
    return paramContext.getResources().getDrawable(paramInt);
  }
  
  private static RoundingParams getRoundingParams(GenericDraweeHierarchyBuilder paramGenericDraweeHierarchyBuilder)
  {
    if (paramGenericDraweeHierarchyBuilder.getRoundingParams() == null) {
      paramGenericDraweeHierarchyBuilder.setRoundingParams(new RoundingParams());
    }
    return paramGenericDraweeHierarchyBuilder.getRoundingParams();
  }
  
  private static ScalingUtils.ScaleType getScaleTypeFromXml(TypedArray paramTypedArray, int paramInt)
  {
    switch (paramTypedArray.getInt(paramInt, -2))
    {
    default: 
      throw new RuntimeException("XML attribute not specified!");
    case 8: 
      return ScalingUtils.ScaleType.FIT_BOTTOM_START;
    case 7: 
      return ScalingUtils.ScaleType.FOCUS_CROP;
    case 6: 
      return ScalingUtils.ScaleType.CENTER_CROP;
    case 5: 
      return ScalingUtils.ScaleType.CENTER_INSIDE;
    case 4: 
      return ScalingUtils.ScaleType.CENTER;
    case 3: 
      return ScalingUtils.ScaleType.FIT_END;
    case 2: 
      return ScalingUtils.ScaleType.FIT_CENTER;
    case 1: 
      return ScalingUtils.ScaleType.FIT_START;
    case 0: 
      return ScalingUtils.ScaleType.FIT_XY;
    }
    return null;
  }
  
  public static GenericDraweeHierarchyBuilder inflateBuilder(Context paramContext, AttributeSet paramAttributeSet)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("GenericDraweeHierarchyBuilder#inflateBuilder");
    }
    paramContext = updateBuilder(new GenericDraweeHierarchyBuilder(paramContext.getResources()), paramContext, paramAttributeSet);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return paramContext;
  }
  
  public static GenericDraweeHierarchy inflateHierarchy(Context paramContext, AttributeSet paramAttributeSet)
  {
    return inflateBuilder(paramContext, paramAttributeSet).build();
  }
  
  public static GenericDraweeHierarchyBuilder updateBuilder(GenericDraweeHierarchyBuilder paramGenericDraweeHierarchyBuilder, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet != null)
    {
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.GenericDraweeHierarchy);
      try
      {
        int m = localTypedArray.getIndexCount();
        boolean bool7 = true;
        j = 0;
        int k = 0;
        boolean bool8 = true;
        boolean bool6 = true;
        boolean bool5 = true;
        boolean bool4 = true;
        boolean bool3 = true;
        boolean bool2 = true;
        boolean bool1 = true;
        i = 0;
        for (;;)
        {
          paramAttributeSet = paramContext;
          if (k < m) {
            try
            {
              n = localTypedArray.getIndex(k);
              i1 = R.styleable.GenericDraweeHierarchy_actualImageScaleType;
              if (n == i1) {
                paramGenericDraweeHierarchyBuilder.setActualImageScaleType(getScaleTypeFromXml(localTypedArray, n));
              }
              for (;;)
              {
                break;
                i1 = R.styleable.GenericDraweeHierarchy_placeholderImage;
                if (n == i1)
                {
                  paramGenericDraweeHierarchyBuilder.setPlaceholderImage(getDrawable(paramAttributeSet, localTypedArray, n));
                }
                else
                {
                  i1 = R.styleable.GenericDraweeHierarchy_pressedStateOverlayImage;
                  if (n == i1)
                  {
                    paramGenericDraweeHierarchyBuilder.setPressedStateOverlay(getDrawable(paramAttributeSet, localTypedArray, n));
                  }
                  else
                  {
                    i1 = R.styleable.GenericDraweeHierarchy_progressBarImage;
                    if (n == i1)
                    {
                      paramGenericDraweeHierarchyBuilder.setProgressBarImage(getDrawable(paramAttributeSet, localTypedArray, n));
                    }
                    else
                    {
                      i1 = R.styleable.GenericDraweeHierarchy_fadeDuration;
                      if (n == i1)
                      {
                        paramGenericDraweeHierarchyBuilder.setFadeDuration(localTypedArray.getInt(n, 0));
                      }
                      else
                      {
                        i1 = R.styleable.GenericDraweeHierarchy_viewAspectRatio;
                        if (n == i1)
                        {
                          paramGenericDraweeHierarchyBuilder.setDesiredAspectRatio(localTypedArray.getFloat(n, 0.0F));
                        }
                        else
                        {
                          i1 = R.styleable.GenericDraweeHierarchy_placeholderImageScaleType;
                          if (n == i1)
                          {
                            paramGenericDraweeHierarchyBuilder.setPlaceholderImageScaleType(getScaleTypeFromXml(localTypedArray, n));
                          }
                          else
                          {
                            i1 = R.styleable.GenericDraweeHierarchy_retryImage;
                            if (n == i1)
                            {
                              paramGenericDraweeHierarchyBuilder.setRetryImage(getDrawable(paramAttributeSet, localTypedArray, n));
                            }
                            else
                            {
                              i1 = R.styleable.GenericDraweeHierarchy_retryImageScaleType;
                              if (n == i1)
                              {
                                paramGenericDraweeHierarchyBuilder.setRetryImageScaleType(getScaleTypeFromXml(localTypedArray, n));
                              }
                              else
                              {
                                i1 = R.styleable.GenericDraweeHierarchy_failureImage;
                                if (n == i1)
                                {
                                  paramGenericDraweeHierarchyBuilder.setFailureImage(getDrawable(paramAttributeSet, localTypedArray, n));
                                }
                                else
                                {
                                  i1 = R.styleable.GenericDraweeHierarchy_failureImageScaleType;
                                  if (n == i1)
                                  {
                                    paramGenericDraweeHierarchyBuilder.setFailureImageScaleType(getScaleTypeFromXml(localTypedArray, n));
                                  }
                                  else
                                  {
                                    i1 = R.styleable.GenericDraweeHierarchy_progressBarImageScaleType;
                                    if (n == i1)
                                    {
                                      paramGenericDraweeHierarchyBuilder.setProgressBarImageScaleType(getScaleTypeFromXml(localTypedArray, n));
                                    }
                                    else
                                    {
                                      i1 = R.styleable.GenericDraweeHierarchy_progressBarAutoRotateInterval;
                                      if (n == i1) {
                                        j = localTypedArray.getInteger(n, j);
                                      }
                                      for (;;)
                                      {
                                        break;
                                        i1 = R.styleable.GenericDraweeHierarchy_backgroundImage;
                                        if (n == i1)
                                        {
                                          paramGenericDraweeHierarchyBuilder.setBackground(getDrawable(paramAttributeSet, localTypedArray, n));
                                          break;
                                        }
                                        i1 = R.styleable.GenericDraweeHierarchy_overlayImage;
                                        if (n == i1)
                                        {
                                          paramGenericDraweeHierarchyBuilder.setOverlay(getDrawable(paramAttributeSet, localTypedArray, n));
                                          break;
                                        }
                                        i1 = R.styleable.GenericDraweeHierarchy_roundAsCircle;
                                        if (n == i1)
                                        {
                                          getRoundingParams(paramGenericDraweeHierarchyBuilder).setRoundAsCircle(localTypedArray.getBoolean(n, false));
                                          break;
                                        }
                                        i1 = R.styleable.GenericDraweeHierarchy_roundedCornerRadius;
                                        if (n == i1)
                                        {
                                          i = localTypedArray.getDimensionPixelSize(n, i);
                                        }
                                        else
                                        {
                                          i1 = R.styleable.GenericDraweeHierarchy_roundTopLeft;
                                          if (n == i1) {
                                            bool8 = localTypedArray.getBoolean(n, bool8);
                                          }
                                          for (;;)
                                          {
                                            break;
                                            i1 = R.styleable.GenericDraweeHierarchy_roundTopRight;
                                            if (n == i1)
                                            {
                                              bool4 = localTypedArray.getBoolean(n, bool4);
                                            }
                                            else
                                            {
                                              i1 = R.styleable.GenericDraweeHierarchy_roundBottomLeft;
                                              if (n == i1)
                                              {
                                                bool7 = localTypedArray.getBoolean(n, bool7);
                                              }
                                              else
                                              {
                                                i1 = R.styleable.GenericDraweeHierarchy_roundBottomRight;
                                                if (n == i1)
                                                {
                                                  bool3 = localTypedArray.getBoolean(n, bool3);
                                                }
                                                else
                                                {
                                                  i1 = R.styleable.GenericDraweeHierarchy_roundTopStart;
                                                  if (n == i1)
                                                  {
                                                    bool6 = localTypedArray.getBoolean(n, bool6);
                                                  }
                                                  else
                                                  {
                                                    i1 = R.styleable.GenericDraweeHierarchy_roundTopEnd;
                                                    if (n == i1)
                                                    {
                                                      bool5 = localTypedArray.getBoolean(n, bool5);
                                                    }
                                                    else
                                                    {
                                                      i1 = R.styleable.GenericDraweeHierarchy_roundBottomStart;
                                                      if (n == i1)
                                                      {
                                                        bool1 = localTypedArray.getBoolean(n, bool1);
                                                      }
                                                      else
                                                      {
                                                        i1 = R.styleable.GenericDraweeHierarchy_roundBottomEnd;
                                                        if (n != i1) {
                                                          break label750;
                                                        }
                                                        bool2 = localTypedArray.getBoolean(n, bool2);
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                          label750:
                                          i1 = R.styleable.GenericDraweeHierarchy_roundWithOverlayColor;
                                          if (n == i1)
                                          {
                                            getRoundingParams(paramGenericDraweeHierarchyBuilder).setOverlayColor(localTypedArray.getColor(n, 0));
                                            break;
                                          }
                                          i1 = R.styleable.GenericDraweeHierarchy_roundingBorderWidth;
                                          if (n == i1)
                                          {
                                            getRoundingParams(paramGenericDraweeHierarchyBuilder).setBorderWidth(localTypedArray.getDimensionPixelSize(n, 0));
                                            break;
                                          }
                                          i1 = R.styleable.GenericDraweeHierarchy_roundingBorderColor;
                                          if (n == i1)
                                          {
                                            getRoundingParams(paramGenericDraweeHierarchyBuilder).setBorderColor(localTypedArray.getColor(n, 0));
                                            break;
                                          }
                                          i1 = R.styleable.GenericDraweeHierarchy_roundingBorderPadding;
                                          if (n != i1) {
                                            break;
                                          }
                                          getRoundingParams(paramGenericDraweeHierarchyBuilder).setPadding(localTypedArray.getDimensionPixelSize(n, 0));
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
              k += 1;
            }
            catch (Throwable paramGenericDraweeHierarchyBuilder)
            {
              break label1135;
            }
          }
        }
        i3 = i;
        localTypedArray.recycle();
        if ((Build.VERSION.SDK_INT >= 17) && (paramContext.getResources().getConfiguration().getLayoutDirection() == 1))
        {
          if ((bool8) && (bool5)) {
            i = 1;
          } else {
            i = 0;
          }
          if ((bool4) && (bool6)) {
            k = 1;
          } else {
            k = 0;
          }
          if ((bool3) && (bool1)) {
            m = 1;
          } else {
            m = 0;
          }
          n = i;
          i1 = k;
          i2 = m;
          if (!bool7) {
            break label1120;
          }
          n = i;
          i1 = k;
          i2 = m;
          if (!bool2) {
            break label1120;
          }
        }
        else
        {
          if ((bool8) && (bool6)) {
            i = 1;
          } else {
            i = 0;
          }
          if ((bool4) && (bool5)) {
            k = 1;
          } else {
            k = 0;
          }
          if ((bool3) && (bool2)) {
            m = 1;
          } else {
            m = 0;
          }
          n = i;
          i1 = k;
          i2 = m;
          if (!bool7) {
            break label1120;
          }
          n = i;
          i1 = k;
          i2 = m;
          if (!bool1) {
            break label1120;
          }
        }
        i2 = 1;
        n = i;
        i1 = k;
        i = m;
        break label1131;
        label1120:
        k = 0;
        i = i2;
        i2 = k;
      }
      catch (Throwable paramGenericDraweeHierarchyBuilder)
      {
        label1131:
        label1135:
        localTypedArray.recycle();
        if (Build.VERSION.SDK_INT >= 17) {
          paramContext.getResources().getConfiguration().getLayoutDirection();
        }
        throw paramGenericDraweeHierarchyBuilder;
      }
    }
    int i1 = 1;
    int i3 = 0;
    int n = 1;
    int i = 1;
    int i2 = 1;
    int j = 0;
    if ((paramGenericDraweeHierarchyBuilder.getProgressBarImage() != null) && (j > 0)) {
      paramGenericDraweeHierarchyBuilder.setProgressBarImage(new AutoRotateDrawable(paramGenericDraweeHierarchyBuilder.getProgressBarImage(), j));
    }
    if (i3 > 0)
    {
      paramContext = getRoundingParams(paramGenericDraweeHierarchyBuilder);
      float f1;
      if (n != 0) {
        f1 = i3;
      } else {
        f1 = 0.0F;
      }
      float f2;
      if (i1 != 0) {
        f2 = i3;
      } else {
        f2 = 0.0F;
      }
      float f3;
      if (i != 0) {
        f3 = i3;
      } else {
        f3 = 0.0F;
      }
      float f4;
      if (i2 != 0) {
        f4 = i3;
      } else {
        f4 = 0.0F;
      }
      paramContext.setCornersRadii(f1, f2, f3, f4);
    }
    return paramGenericDraweeHierarchyBuilder;
  }
}
