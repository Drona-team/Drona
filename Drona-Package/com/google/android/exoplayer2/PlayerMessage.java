package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;

public final class PlayerMessage
{
  private boolean deleteAfterDelivery;
  private Handler handler;
  private boolean isCanceled;
  private boolean isDelivered;
  private boolean isProcessed;
  private boolean isSent;
  @Nullable
  private Object payload;
  private long positionMs;
  private final Sender sender;
  private final Target target;
  private final Timeline timeline;
  private int type;
  private int windowIndex;
  
  public PlayerMessage(Sender paramSender, Target paramTarget, Timeline paramTimeline, int paramInt, Handler paramHandler)
  {
    sender = paramSender;
    target = paramTarget;
    timeline = paramTimeline;
    handler = paramHandler;
    windowIndex = paramInt;
    positionMs = -9223372036854775807L;
    deleteAfterDelivery = true;
  }
  
  public boolean blockUntilDelivered()
    throws InterruptedException
  {
    for (;;)
    {
      try
      {
        Assertions.checkState(isSent);
        if (handler.getLooper().getThread() != Thread.currentThread())
        {
          bool = true;
          Assertions.checkState(bool);
          if (!isProcessed)
          {
            wait();
            continue;
          }
          bool = isDelivered;
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public PlayerMessage cancel()
  {
    try
    {
      Assertions.checkState(isSent);
      isCanceled = true;
      markAsProcessed(false);
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean getDeleteAfterDelivery()
  {
    return deleteAfterDelivery;
  }
  
  public Handler getHandler()
  {
    return handler;
  }
  
  public Object getPayload()
  {
    return payload;
  }
  
  public long getPositionMs()
  {
    return positionMs;
  }
  
  public Target getTarget()
  {
    return target;
  }
  
  public Timeline getTimeline()
  {
    return timeline;
  }
  
  public int getType()
  {
    return type;
  }
  
  public int getWindowIndex()
  {
    return windowIndex;
  }
  
  public boolean isCanceled()
  {
    try
    {
      boolean bool = isCanceled;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void markAsProcessed(boolean paramBoolean)
  {
    try
    {
      isDelivered = (paramBoolean | isDelivered);
      isProcessed = true;
      notifyAll();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public PlayerMessage send()
  {
    Assertions.checkState(isSent ^ true);
    if (positionMs == -9223372036854775807L) {
      Assertions.checkArgument(deleteAfterDelivery);
    }
    isSent = true;
    sender.sendMessage(this);
    return this;
  }
  
  public PlayerMessage setDeleteAfterDelivery(boolean paramBoolean)
  {
    Assertions.checkState(isSent ^ true);
    deleteAfterDelivery = paramBoolean;
    return this;
  }
  
  public PlayerMessage setHandler(Handler paramHandler)
  {
    Assertions.checkState(isSent ^ true);
    handler = paramHandler;
    return this;
  }
  
  public PlayerMessage setPayload(Object paramObject)
  {
    Assertions.checkState(isSent ^ true);
    payload = paramObject;
    return this;
  }
  
  public PlayerMessage setPosition(int paramInt, long paramLong)
  {
    boolean bool2 = isSent;
    boolean bool1 = true;
    Assertions.checkState(bool2 ^ true);
    if (paramLong == -9223372036854775807L) {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    if ((paramInt >= 0) && ((timeline.isEmpty()) || (paramInt < timeline.getWindowCount())))
    {
      windowIndex = paramInt;
      positionMs = paramLong;
      return this;
    }
    throw new IllegalSeekPositionException(timeline, paramInt, paramLong);
  }
  
  public PlayerMessage setPosition(long paramLong)
  {
    Assertions.checkState(isSent ^ true);
    positionMs = paramLong;
    return this;
  }
  
  public PlayerMessage setType(int paramInt)
  {
    Assertions.checkState(isSent ^ true);
    type = paramInt;
    return this;
  }
  
  public static abstract interface Sender
  {
    public abstract void sendMessage(PlayerMessage paramPlayerMessage);
  }
  
  public static abstract interface Target
  {
    public abstract void handleMessage(int paramInt, Object paramObject)
      throws ExoPlaybackException;
  }
}
