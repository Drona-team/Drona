package com.facebook.react.uimanager.events;

import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.core.util.Pools.SynchronizedPool;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.SoftAssertions;

public class TouchEvent
  extends Event<TouchEvent>
{
  private static final Pools.SynchronizedPool<TouchEvent> EVENTS_POOL = new Pools.SynchronizedPool(3);
  private static final int TOUCH_EVENTS_POOL_SIZE = 3;
  public static final long UNSET = Long.MIN_VALUE;
  private short mCoalescingKey;
  @Nullable
  private MotionEvent mMotionEvent;
  @Nullable
  private TouchEventType mTouchEventType;
  private float mViewX;
  private float mViewY;
  
  private TouchEvent() {}
  
  private void init(int paramInt, TouchEventType paramTouchEventType, MotionEvent paramMotionEvent, long paramLong, float paramFloat1, float paramFloat2, TouchEventCoalescingKeyHelper paramTouchEventCoalescingKeyHelper)
  {
    super.init(paramInt);
    short s = 0;
    boolean bool;
    if (paramLong != Long.MIN_VALUE) {
      bool = true;
    } else {
      bool = false;
    }
    SoftAssertions.assertCondition(bool, "Gesture start time must be initialized");
    paramInt = paramMotionEvent.getAction() & 0xFF;
    switch (paramInt)
    {
    default: 
      break;
    case 4: 
      paramTouchEventType = new StringBuilder();
      paramTouchEventType.append("Unhandled MotionEvent action: ");
      paramTouchEventType.append(paramInt);
      throw new RuntimeException(paramTouchEventType.toString());
    case 5: 
    case 6: 
      paramTouchEventCoalescingKeyHelper.incrementCoalescingKey(paramLong);
      break;
    case 3: 
      paramTouchEventCoalescingKeyHelper.removeCoalescingKey(paramLong);
      break;
    case 2: 
      s = paramTouchEventCoalescingKeyHelper.getCoalescingKey(paramLong);
      break;
    case 1: 
      paramTouchEventCoalescingKeyHelper.removeCoalescingKey(paramLong);
      break;
    }
    paramTouchEventCoalescingKeyHelper.addCoalescingKey(paramLong);
    mTouchEventType = paramTouchEventType;
    mMotionEvent = MotionEvent.obtain(paramMotionEvent);
    mCoalescingKey = s;
    mViewX = paramFloat1;
    mViewY = paramFloat2;
  }
  
  public static TouchEvent obtain(int paramInt, TouchEventType paramTouchEventType, MotionEvent paramMotionEvent, long paramLong, float paramFloat1, float paramFloat2, TouchEventCoalescingKeyHelper paramTouchEventCoalescingKeyHelper)
  {
    TouchEvent localTouchEvent2 = (TouchEvent)EVENTS_POOL.acquire();
    TouchEvent localTouchEvent1 = localTouchEvent2;
    if (localTouchEvent2 == null) {
      localTouchEvent1 = new TouchEvent();
    }
    localTouchEvent1.init(paramInt, paramTouchEventType, paramMotionEvent, paramLong, paramFloat1, paramFloat2, paramTouchEventCoalescingKeyHelper);
    return localTouchEvent1;
  }
  
  public boolean canCoalesce()
  {
    switch (1.$SwitchMap$com$facebook$react$uimanager$events$TouchEventType[((TouchEventType)Assertions.assertNotNull(mTouchEventType)).ordinal()])
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unknown touch event type: ");
      localStringBuilder.append(mTouchEventType);
      throw new RuntimeException(localStringBuilder.toString());
    case 4: 
      return true;
    }
    return false;
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    TouchesHelper.sendTouchEvent(paramRCTEventEmitter, (TouchEventType)Assertions.assertNotNull(mTouchEventType), getViewTag(), this);
  }
  
  public short getCoalescingKey()
  {
    return mCoalescingKey;
  }
  
  public String getEventName()
  {
    return TouchEventType.getJSEventName((TouchEventType)Assertions.assertNotNull(mTouchEventType));
  }
  
  public MotionEvent getMotionEvent()
  {
    Assertions.assertNotNull(mMotionEvent);
    return mMotionEvent;
  }
  
  public float getViewX()
  {
    return mViewX;
  }
  
  public float getViewY()
  {
    return mViewY;
  }
  
  public void onDispose()
  {
    ((MotionEvent)Assertions.assertNotNull(mMotionEvent)).recycle();
    mMotionEvent = null;
    EVENTS_POOL.release(this);
  }
}