package com.facebook.react.touch;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.Nullable;

public class JSResponderHandler
  implements OnInterceptTouchEventListener
{
  private static final int JS_RESPONDER_UNSET = -1;
  private volatile int mCurrentJSResponder = -1;
  @Nullable
  private ViewParent mViewParentBlockingNativeResponder;
  
  public JSResponderHandler() {}
  
  private void maybeUnblockNativeResponder()
  {
    if (mViewParentBlockingNativeResponder != null)
    {
      mViewParentBlockingNativeResponder.requestDisallowInterceptTouchEvent(false);
      mViewParentBlockingNativeResponder = null;
    }
  }
  
  public void clearJSResponder()
  {
    mCurrentJSResponder = -1;
    maybeUnblockNativeResponder();
  }
  
  public boolean onInterceptTouchEvent(ViewGroup paramViewGroup, MotionEvent paramMotionEvent)
  {
    int i = mCurrentJSResponder;
    return (i != -1) && (paramMotionEvent.getAction() != 1) && (paramViewGroup.getId() == i);
  }
  
  public void setJSResponder(int paramInt, ViewParent paramViewParent)
  {
    mCurrentJSResponder = paramInt;
    maybeUnblockNativeResponder();
    if (paramViewParent != null)
    {
      paramViewParent.requestDisallowInterceptTouchEvent(true);
      mViewParentBlockingNativeResponder = paramViewParent;
    }
  }
}
