package com.google.android.exoplayer2.pc.spherical;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import androidx.annotation.Nullable;

class TouchTracker
  extends GestureDetector.SimpleOnGestureListener
  implements View.OnTouchListener
{
  static final float MAX_PITCH_DEGREES = 45.0F;
  private final PointF accumulatedTouchOffsetDegrees = new PointF();
  private final GestureDetector gestureDetector;
  private final Listener listener;
  private final PointF previousTouchPointPx = new PointF();
  private final float pxPerDegrees;
  private volatile float roll;
  @Nullable
  private SingleTapListener singleTapListener;
  
  public TouchTracker(Context paramContext, Listener paramListener, float paramFloat)
  {
    listener = paramListener;
    pxPerDegrees = paramFloat;
    gestureDetector = new GestureDetector(paramContext, this);
    roll = 3.1415927F;
  }
  
  public boolean onDown(MotionEvent paramMotionEvent)
  {
    previousTouchPointPx.set(paramMotionEvent.getX(), paramMotionEvent.getY());
    return true;
  }
  
  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    paramFloat1 = (paramMotionEvent2.getX() - previousTouchPointPx.x) / pxPerDegrees;
    paramFloat2 = (paramMotionEvent2.getY() - previousTouchPointPx.y) / pxPerDegrees;
    previousTouchPointPx.set(paramMotionEvent2.getX(), paramMotionEvent2.getY());
    double d = roll;
    float f1 = (float)Math.cos(d);
    float f2 = (float)Math.sin(d);
    paramMotionEvent1 = accumulatedTouchOffsetDegrees;
    x -= f1 * paramFloat1 - f2 * paramFloat2;
    paramMotionEvent1 = accumulatedTouchOffsetDegrees;
    y += f2 * paramFloat1 + f1 * paramFloat2;
    accumulatedTouchOffsetDegrees.y = Math.max(-45.0F, Math.min(45.0F, accumulatedTouchOffsetDegrees.y));
    listener.onScrollChange(accumulatedTouchOffsetDegrees);
    return true;
  }
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    if (singleTapListener != null) {
      return singleTapListener.onSingleTapUp(paramMotionEvent);
    }
    return false;
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    return gestureDetector.onTouchEvent(paramMotionEvent);
  }
  
  public void setRoll(float paramFloat)
  {
    roll = (-paramFloat);
  }
  
  public void setSingleTapListener(SingleTapListener paramSingleTapListener)
  {
    singleTapListener = paramSingleTapListener;
  }
  
  abstract interface Listener
  {
    public abstract void onScrollChange(PointF paramPointF);
  }
}
