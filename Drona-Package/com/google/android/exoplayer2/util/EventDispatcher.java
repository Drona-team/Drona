package com.google.android.exoplayer2.util;

import android.os.Handler;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventDispatcher<T>
{
  private final CopyOnWriteArrayList<HandlerAndListener<T>> listeners = new CopyOnWriteArrayList();
  
  public EventDispatcher() {}
  
  public void addListener(Handler paramHandler, Object paramObject)
  {
    boolean bool;
    if ((paramHandler != null) && (paramObject != null)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    removeListener(paramObject);
    listeners.add(new HandlerAndListener(paramHandler, paramObject));
  }
  
  public void dispatch(Event paramEvent)
  {
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((HandlerAndListener)localIterator.next()).dispatch(paramEvent);
    }
  }
  
  public void removeListener(Object paramObject)
  {
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext())
    {
      HandlerAndListener localHandlerAndListener = (HandlerAndListener)localIterator.next();
      if (listener == paramObject)
      {
        localHandlerAndListener.release();
        listeners.remove(localHandlerAndListener);
      }
    }
  }
  
  public static abstract interface Event<T>
  {
    public abstract void sendTo(Object paramObject);
  }
  
  private static final class HandlerAndListener<T>
  {
    private final Handler handler;
    private final T listener;
    private boolean released;
    
    public HandlerAndListener(Handler paramHandler, Object paramObject)
    {
      handler = paramHandler;
      listener = paramObject;
    }
    
    public void dispatch(EventDispatcher.Event paramEvent)
    {
      handler.post(new -..Lambda.EventDispatcher.HandlerAndListener.uD_JKgYUi0f_RBL7K02WSc4AoE4(this, paramEvent));
    }
    
    public void release()
    {
      released = true;
    }
  }
}
