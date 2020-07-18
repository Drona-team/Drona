package com.facebook.react.views.view;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class ReactViewBackgroundManager
{
  @Nullable
  private ReactViewBackgroundDrawable mReactBackgroundDrawable;
  private View mView;
  
  public ReactViewBackgroundManager(View paramView)
  {
    mView = paramView;
  }
  
  private ReactViewBackgroundDrawable getOrCreateReactViewBackground()
  {
    if (mReactBackgroundDrawable == null)
    {
      mReactBackgroundDrawable = new ReactViewBackgroundDrawable(mView.getContext());
      Object localObject = mView.getBackground();
      ViewCompat.setBackground(mView, null);
      if (localObject == null)
      {
        ViewCompat.setBackground(mView, mReactBackgroundDrawable);
      }
      else
      {
        localObject = new LayerDrawable(new Drawable[] { mReactBackgroundDrawable, localObject });
        ViewCompat.setBackground(mView, (Drawable)localObject);
      }
    }
    return mReactBackgroundDrawable;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    if ((paramInt == 0) && (mReactBackgroundDrawable == null)) {
      return;
    }
    getOrCreateReactViewBackground().setColor(paramInt);
  }
  
  public void setBorderColor(int paramInt, float paramFloat1, float paramFloat2)
  {
    getOrCreateReactViewBackground().setBorderColor(paramInt, paramFloat1, paramFloat2);
  }
  
  public void setBorderRadius(float paramFloat)
  {
    getOrCreateReactViewBackground().setRadius(paramFloat);
  }
  
  public void setBorderRadius(float paramFloat, int paramInt)
  {
    getOrCreateReactViewBackground().setRadius(paramFloat, paramInt);
  }
  
  public void setBorderStyle(String paramString)
  {
    getOrCreateReactViewBackground().setBorderStyle(paramString);
  }
  
  public void setBorderWidth(int paramInt, float paramFloat)
  {
    getOrCreateReactViewBackground().setBorderWidth(paramInt, paramFloat);
  }
}
