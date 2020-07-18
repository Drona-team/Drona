package com.facebook.react.views.swiperefresh;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.NativeGestureUtil;

public class ReactSwipeRefreshLayout
  extends SwipeRefreshLayout
{
  private static final float DEFAULT_CIRCLE_TARGET = 64.0F;
  private boolean mDidLayout = false;
  private boolean mIntercepted;
  private float mPrevTouchX;
  private float mProgressViewOffset = 0.0F;
  private boolean mRefreshing = false;
  private int mTouchSlop;
  
  public ReactSwipeRefreshLayout(ReactContext paramReactContext)
  {
    super(paramReactContext);
    mTouchSlop = ViewConfiguration.get(paramReactContext).getScaledTouchSlop();
  }
  
  private boolean shouldInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i != 0)
    {
      if (i != 2) {
        return true;
      }
      float f = Math.abs(paramMotionEvent.getX() - mPrevTouchX);
      if ((mIntercepted) || (f > mTouchSlop))
      {
        mIntercepted = true;
        return false;
      }
    }
    else
    {
      mPrevTouchX = paramMotionEvent.getX();
      mIntercepted = false;
    }
    return true;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((shouldInterceptTouchEvent(paramMotionEvent)) && (super.onInterceptTouchEvent(paramMotionEvent)))
    {
      NativeGestureUtil.notifyNativeGestureStarted(this, paramMotionEvent);
      if (getParent() != null)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (!mDidLayout)
    {
      mDidLayout = true;
      setProgressViewOffset(mProgressViewOffset);
      setRefreshing(mRefreshing);
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (getParent() != null) {
      getParent().requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  public void setProgressViewOffset(float paramFloat)
  {
    mProgressViewOffset = paramFloat;
    if (mDidLayout)
    {
      int i = getProgressCircleDiameter();
      setProgressViewOffset(false, Math.round(PixelUtil.toPixelFromDIP(paramFloat)) - i, Math.round(PixelUtil.toPixelFromDIP(paramFloat + 64.0F) - i));
    }
  }
  
  public void setRefreshing(boolean paramBoolean)
  {
    mRefreshing = paramBoolean;
    if (mDidLayout) {
      super.setRefreshing(paramBoolean);
    }
  }
}