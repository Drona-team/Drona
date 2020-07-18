package com.facebook.react.views.scroll;

import androidx.annotation.Nullable;
import androidx.core.util.Pools.SynchronizedPool;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class ScrollEvent
  extends Event<ScrollEvent>
{
  private static final Pools.SynchronizedPool<ScrollEvent> EVENTS_POOL = new Pools.SynchronizedPool(3);
  private int mContentHeight;
  private int mContentWidth;
  @Nullable
  private ScrollEventType mScrollEventType;
  private int mScrollViewHeight;
  private int mScrollViewWidth;
  private int mScrollX;
  private int mScrollY;
  private double mXVelocity;
  private double mYVelocity;
  
  private ScrollEvent() {}
  
  private void init(int paramInt1, ScrollEventType paramScrollEventType, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    super.init(paramInt1);
    mScrollEventType = paramScrollEventType;
    mScrollX = paramInt2;
    mScrollY = paramInt3;
    mXVelocity = paramFloat1;
    mYVelocity = paramFloat2;
    mContentWidth = paramInt4;
    mContentHeight = paramInt5;
    mScrollViewWidth = paramInt6;
    mScrollViewHeight = paramInt7;
  }
  
  public static ScrollEvent obtain(int paramInt1, ScrollEventType paramScrollEventType, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    ScrollEvent localScrollEvent2 = (ScrollEvent)EVENTS_POOL.acquire();
    ScrollEvent localScrollEvent1 = localScrollEvent2;
    if (localScrollEvent2 == null) {
      localScrollEvent1 = new ScrollEvent();
    }
    localScrollEvent1.init(paramInt1, paramScrollEventType, paramInt2, paramInt3, paramFloat1, paramFloat2, paramInt4, paramInt5, paramInt6, paramInt7);
    return localScrollEvent1;
  }
  
  private WritableMap serializeEventData()
  {
    WritableMap localWritableMap1 = Arguments.createMap();
    localWritableMap1.putDouble("top", 0.0D);
    localWritableMap1.putDouble("bottom", 0.0D);
    localWritableMap1.putDouble("left", 0.0D);
    localWritableMap1.putDouble("right", 0.0D);
    WritableMap localWritableMap2 = Arguments.createMap();
    localWritableMap2.putDouble("x", PixelUtil.toDIPFromPixel(mScrollX));
    localWritableMap2.putDouble("y", PixelUtil.toDIPFromPixel(mScrollY));
    WritableMap localWritableMap3 = Arguments.createMap();
    localWritableMap3.putDouble("width", PixelUtil.toDIPFromPixel(mContentWidth));
    localWritableMap3.putDouble("height", PixelUtil.toDIPFromPixel(mContentHeight));
    WritableMap localWritableMap4 = Arguments.createMap();
    localWritableMap4.putDouble("width", PixelUtil.toDIPFromPixel(mScrollViewWidth));
    localWritableMap4.putDouble("height", PixelUtil.toDIPFromPixel(mScrollViewHeight));
    WritableMap localWritableMap5 = Arguments.createMap();
    localWritableMap5.putDouble("x", mXVelocity);
    localWritableMap5.putDouble("y", mYVelocity);
    WritableMap localWritableMap6 = Arguments.createMap();
    localWritableMap6.putMap("contentInset", localWritableMap1);
    localWritableMap6.putMap("contentOffset", localWritableMap2);
    localWritableMap6.putMap("contentSize", localWritableMap3);
    localWritableMap6.putMap("layoutMeasurement", localWritableMap4);
    localWritableMap6.putMap("velocity", localWritableMap5);
    localWritableMap6.putInt("target", getViewTag());
    localWritableMap6.putBoolean("responderIgnoreScroll", true);
    return localWritableMap6;
  }
  
  public boolean canCoalesce()
  {
    return mScrollEventType == ScrollEventType.SCROLL;
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    paramRCTEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
  }
  
  public short getCoalescingKey()
  {
    return 0;
  }
  
  public String getEventName()
  {
    return ScrollEventType.getJSEventName((ScrollEventType)Assertions.assertNotNull(mScrollEventType));
  }
  
  public void onDispose()
  {
    EVENTS_POOL.release(this);
  }
}