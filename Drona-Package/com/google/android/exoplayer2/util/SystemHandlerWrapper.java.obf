package com.google.android.exoplayer2.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class SystemHandlerWrapper
  implements HandlerWrapper
{
  private final Handler handler;
  
  public SystemHandlerWrapper(Handler paramHandler)
  {
    handler = paramHandler;
  }
  
  public Looper getLooper()
  {
    return handler.getLooper();
  }
  
  public Message obtainMessage(int paramInt)
  {
    return handler.obtainMessage(paramInt);
  }
  
  public Message obtainMessage(int paramInt1, int paramInt2, int paramInt3)
  {
    return handler.obtainMessage(paramInt1, paramInt2, paramInt3);
  }
  
  public Message obtainMessage(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    return handler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject);
  }
  
  public Message obtainMessage(int paramInt, Object paramObject)
  {
    return handler.obtainMessage(paramInt, paramObject);
  }
  
  public boolean post(Runnable paramRunnable)
  {
    return handler.post(paramRunnable);
  }
  
  public boolean postDelayed(Runnable paramRunnable, long paramLong)
  {
    return handler.postDelayed(paramRunnable, paramLong);
  }
  
  public void removeCallbacksAndMessages(Object paramObject)
  {
    handler.removeCallbacksAndMessages(paramObject);
  }
  
  public void removeMessages(int paramInt)
  {
    handler.removeMessages(paramInt);
  }
  
  public boolean sendEmptyMessage(int paramInt)
  {
    return handler.sendEmptyMessage(paramInt);
  }
  
  public boolean sendEmptyMessageAtTime(int paramInt, long paramLong)
  {
    return handler.sendEmptyMessageAtTime(paramInt, paramLong);
  }
}
