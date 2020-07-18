package com.google.android.exoplayer2.pc;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class AspectRatioFrameLayout
  extends FrameLayout
{
  private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01F;
  public static final int RESIZE_MODE_FILL = 3;
  public static final int RESIZE_MODE_FIT = 0;
  public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
  public static final int RESIZE_MODE_FIXED_WIDTH = 1;
  public static final int RESIZE_MODE_ZOOM = 4;
  private AspectRatioListener aspectRatioListener;
  private final AspectRatioUpdateDispatcher aspectRatioUpdateDispatcher;
  private int resizeMode = 0;
  private float videoAspectRatio;
  
  public AspectRatioFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AspectRatioFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (paramAttributeSet != null)
    {
      paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.AspectRatioFrameLayout, 0, 0);
      try
      {
        resizeMode = paramContext.getInt(R.styleable.AspectRatioFrameLayout_resize_mode, 0);
        paramContext.recycle();
      }
      catch (Throwable paramAttributeSet)
      {
        paramContext.recycle();
        throw paramAttributeSet;
      }
    }
    aspectRatioUpdateDispatcher = new AspectRatioUpdateDispatcher(null);
  }
  
  public int getResizeMode()
  {
    return resizeMode;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (videoAspectRatio <= 0.0F) {
      return;
    }
    int i = getMeasuredWidth();
    paramInt2 = i;
    int j = getMeasuredHeight();
    paramInt1 = j;
    float f1 = i;
    float f2 = j;
    float f3 = f1 / f2;
    float f4 = videoAspectRatio / f3 - 1.0F;
    if (Math.abs(f4) <= 0.01F)
    {
      aspectRatioUpdateDispatcher.scheduleUpdate(videoAspectRatio, f3, false);
      return;
    }
    i = resizeMode;
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 2: 
        paramInt2 = (int)(f2 * videoAspectRatio);
        break;
      case 1: 
        paramInt1 = (int)(f1 / videoAspectRatio);
        break;
      case 0: 
        if (f4 > 0.0F) {
          paramInt1 = (int)(f1 / videoAspectRatio);
        } else {
          paramInt2 = (int)(f2 * videoAspectRatio);
        }
        break;
      }
    } else if (f4 > 0.0F) {
      paramInt2 = (int)(f2 * videoAspectRatio);
    } else {
      paramInt1 = (int)(f1 / videoAspectRatio);
    }
    aspectRatioUpdateDispatcher.scheduleUpdate(videoAspectRatio, f3, true);
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824));
  }
  
  public void setAspectRatio(float paramFloat)
  {
    if (videoAspectRatio != paramFloat)
    {
      videoAspectRatio = paramFloat;
      requestLayout();
    }
  }
  
  public void setAspectRatioListener(AspectRatioListener paramAspectRatioListener)
  {
    aspectRatioListener = paramAspectRatioListener;
  }
  
  public void setResizeMode(int paramInt)
  {
    if (resizeMode != paramInt)
    {
      resizeMode = paramInt;
      requestLayout();
    }
  }
  
  public abstract interface AspectRatioListener
  {
    public abstract void onAspectRatioUpdated(float paramFloat1, float paramFloat2, boolean paramBoolean);
  }
  
  final class AspectRatioUpdateDispatcher
    implements Runnable
  {
    private boolean aspectRatioMismatch;
    private boolean isScheduled;
    private float naturalAspectRatio;
    private float targetAspectRatio;
    
    private AspectRatioUpdateDispatcher() {}
    
    public void run()
    {
      isScheduled = false;
      if (aspectRatioListener == null) {
        return;
      }
      aspectRatioListener.onAspectRatioUpdated(targetAspectRatio, naturalAspectRatio, aspectRatioMismatch);
    }
    
    public void scheduleUpdate(float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      targetAspectRatio = paramFloat1;
      naturalAspectRatio = paramFloat2;
      aspectRatioMismatch = paramBoolean;
      if (!isScheduled)
      {
        isScheduled = true;
        post(this);
      }
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface ResizeMode {}
}
