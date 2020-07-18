package com.facebook.react.views.drawer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.LayoutParams;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.NativeGestureUtil;

class ReactDrawerLayout
  extends DrawerLayout
{
  public static final int DEFAULT_DRAWER_WIDTH = -1;
  private int mDrawerPosition = 8388611;
  private int mDrawerWidth = -1;
  
  public ReactDrawerLayout(ReactContext paramReactContext)
  {
    super(paramReactContext);
  }
  
  void closeDrawer()
  {
    closeDrawer(mDrawerPosition);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    try
    {
      boolean bool = super.onInterceptTouchEvent(paramMotionEvent);
      if (bool)
      {
        NativeGestureUtil.notifyNativeGestureStarted(this, paramMotionEvent);
        return true;
      }
    }
    catch (IllegalArgumentException paramMotionEvent)
    {
      Log.w("ReactNative", "Error intercepting touch event.", paramMotionEvent);
    }
    return false;
  }
  
  void openDrawer()
  {
    openDrawer(mDrawerPosition);
  }
  
  void setDrawerPosition(int paramInt)
  {
    mDrawerPosition = paramInt;
    setDrawerProperties();
  }
  
  void setDrawerProperties()
  {
    if (getChildCount() == 2)
    {
      View localView = getChildAt(1);
      DrawerLayout.LayoutParams localLayoutParams = (DrawerLayout.LayoutParams)localView.getLayoutParams();
      gravity = mDrawerPosition;
      width = mDrawerWidth;
      localView.setLayoutParams(localLayoutParams);
      localView.setClickable(true);
    }
  }
  
  void setDrawerWidth(int paramInt)
  {
    mDrawerWidth = paramInt;
    setDrawerProperties();
  }
}