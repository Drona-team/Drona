package com.facebook.react.views.progressbar;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;

class ProgressBarContainerView
  extends FrameLayout
{
  private static final int MAX_PROGRESS = 1000;
  private boolean mAnimating = true;
  @Nullable
  private Integer mColor;
  private boolean mIndeterminate = true;
  private double mProgress;
  @Nullable
  private ProgressBar mProgressBar;
  
  public ProgressBarContainerView(Context paramContext)
  {
    super(paramContext);
  }
  
  private void setColor(ProgressBar paramProgressBar)
  {
    if (paramProgressBar.isIndeterminate()) {
      paramProgressBar = paramProgressBar.getIndeterminateDrawable();
    } else {
      paramProgressBar = paramProgressBar.getProgressDrawable();
    }
    if (paramProgressBar == null) {
      return;
    }
    if (mColor != null)
    {
      paramProgressBar.setColorFilter(mColor.intValue(), PorterDuff.Mode.SRC_IN);
      return;
    }
    paramProgressBar.clearColorFilter();
  }
  
  public void apply()
  {
    if (mProgressBar != null)
    {
      mProgressBar.setIndeterminate(mIndeterminate);
      setColor(mProgressBar);
      mProgressBar.setProgress((int)(mProgress * 1000.0D));
      if (mAnimating)
      {
        mProgressBar.setVisibility(0);
        return;
      }
      mProgressBar.setVisibility(4);
      return;
    }
    throw new JSApplicationIllegalArgumentException("setStyle() not called");
  }
  
  public void setAnimating(boolean paramBoolean)
  {
    mAnimating = paramBoolean;
  }
  
  public void setColor(Integer paramInteger)
  {
    mColor = paramInteger;
  }
  
  public void setIndeterminate(boolean paramBoolean)
  {
    mIndeterminate = paramBoolean;
  }
  
  public void setProgress(double paramDouble)
  {
    mProgress = paramDouble;
  }
  
  public void setStyle(String paramString)
  {
    int i = ReactProgressBarViewManager.getStyleFromString(paramString);
    mProgressBar = ReactProgressBarViewManager.createProgressBar(getContext(), i);
    mProgressBar.setMax(1000);
    removeAllViews();
    addView(mProgressBar, new ViewGroup.LayoutParams(-1, -1));
  }
}
