package com.facebook.drawee.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.facebook.common.internal.VisibleForTesting;
import javax.annotation.Nullable;

public class GestureDetector
{
  @VisibleForTesting
  long mActionDownTime;
  @VisibleForTesting
  float mActionDownX;
  @VisibleForTesting
  float mActionDownY;
  @Nullable
  @VisibleForTesting
  ClickListener mClickListener;
  @VisibleForTesting
  boolean mIsCapturingGesture;
  @VisibleForTesting
  boolean mIsClickCandidate;
  @VisibleForTesting
  final float mSingleTapSlopPx;
  
  public GestureDetector(Context paramContext)
  {
    mSingleTapSlopPx = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    init();
  }
  
  public static GestureDetector newInstance(Context paramContext)
  {
    return new GestureDetector(paramContext);
  }
  
  public void init()
  {
    mClickListener = null;
    reset();
  }
  
  public boolean isCapturingGesture()
  {
    return mIsCapturingGesture;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    default: 
      return true;
    case 3: 
      mIsCapturingGesture = false;
      mIsClickCandidate = false;
      return true;
    case 2: 
      if ((Math.abs(paramMotionEvent.getX() - mActionDownX) > mSingleTapSlopPx) || (Math.abs(paramMotionEvent.getY() - mActionDownY) > mSingleTapSlopPx))
      {
        mIsClickCandidate = false;
        return true;
      }
      break;
    case 1: 
      mIsCapturingGesture = false;
      if ((Math.abs(paramMotionEvent.getX() - mActionDownX) > mSingleTapSlopPx) || (Math.abs(paramMotionEvent.getY() - mActionDownY) > mSingleTapSlopPx)) {
        mIsClickCandidate = false;
      }
      if ((mIsClickCandidate) && (paramMotionEvent.getEventTime() - mActionDownTime <= ViewConfiguration.getLongPressTimeout()) && (mClickListener != null)) {
        mClickListener.onClick();
      }
      mIsClickCandidate = false;
      return true;
    case 0: 
      mIsCapturingGesture = true;
      mIsClickCandidate = true;
      mActionDownTime = paramMotionEvent.getEventTime();
      mActionDownX = paramMotionEvent.getX();
      mActionDownY = paramMotionEvent.getY();
    }
    return true;
  }
  
  public void reset()
  {
    mIsCapturingGesture = false;
    mIsClickCandidate = false;
  }
  
  public void setClickListener(ClickListener paramClickListener)
  {
    mClickListener = paramClickListener;
  }
  
  public static abstract interface ClickListener
  {
    public abstract boolean onClick();
  }
}
