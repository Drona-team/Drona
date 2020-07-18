package com.facebook.react.uimanager.events;

import android.util.LongSparseArray;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.ChoreographerCompat.FrameCallback;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.core.ReactChoreographer.CallbackType;
import com.facebook.systrace.Systrace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EventDispatcher
  implements LifecycleEventListener
{
  private static final Comparator<Event> EVENT_COMPARATOR = new Comparator()
  {
    public int compare(Event paramAnonymousEvent1, Event paramAnonymousEvent2)
    {
      if ((paramAnonymousEvent1 == null) && (paramAnonymousEvent2 == null)) {
        return 0;
      }
      if (paramAnonymousEvent1 == null) {
        return -1;
      }
      if (paramAnonymousEvent2 == null) {
        return 1;
      }
      boolean bool = paramAnonymousEvent1.getTimestampMs() - paramAnonymousEvent2.getTimestampMs() < 0L;
      if (!bool) {
        return 0;
      }
      if (bool) {
        return -1;
      }
      return 1;
    }
  };
  private final ScheduleDispatchFrameCallback mCurrentFrameCallback = new ScheduleDispatchFrameCallback(null);
  private final DispatchEventsRunnable mDispatchEventsRunnable = new DispatchEventsRunnable(null);
  private final LongSparseArray<Integer> mEventCookieToLastEventIdx = new LongSparseArray();
  private final Map<String, Short> mEventNameToEventId = MapBuilder.newHashMap();
  private final ArrayList<Event> mEventStaging = new ArrayList();
  private final Object mEventsStagingLock = new Object();
  private Event[] mEventsToDispatch = new Event[16];
  private final Object mEventsToDispatchLock = new Object();
  private int mEventsToDispatchSize = 0;
  private volatile boolean mHasDispatchScheduled = false;
  private final AtomicInteger mHasDispatchScheduledCount = new AtomicInteger();
  private final ArrayList<EventDispatcherListener> mListeners = new ArrayList();
  private short mNextEventTypeId = 0;
  private final List<BatchEventDispatchedListener> mPostEventDispatchListeners = new ArrayList();
  private final ReactApplicationContext mReactContext;
  private volatile ReactEventEmitter mReactEventEmitter;
  
  public EventDispatcher(ReactApplicationContext paramReactApplicationContext)
  {
    mReactContext = paramReactApplicationContext;
    mReactContext.addLifecycleEventListener(this);
    mReactEventEmitter = new ReactEventEmitter(mReactContext);
  }
  
  private void addEventToEventsToDispatch(Event paramEvent)
  {
    if (mEventsToDispatchSize == mEventsToDispatch.length) {
      mEventsToDispatch = ((Event[])Arrays.copyOf(mEventsToDispatch, mEventsToDispatch.length * 2));
    }
    Event[] arrayOfEvent = mEventsToDispatch;
    int i = mEventsToDispatchSize;
    mEventsToDispatchSize = (i + 1);
    arrayOfEvent[i] = paramEvent;
  }
  
  private void clearEventsToDispatch()
  {
    Arrays.fill(mEventsToDispatch, 0, mEventsToDispatchSize, null);
    mEventsToDispatchSize = 0;
  }
  
  private long getEventCookie(int paramInt, String paramString, short paramShort)
  {
    Short localShort = (Short)mEventNameToEventId.get(paramString);
    short s;
    if (localShort != null)
    {
      s = localShort.shortValue();
    }
    else
    {
      s = mNextEventTypeId;
      mNextEventTypeId = ((short)(s + 1));
      mEventNameToEventId.put(paramString, Short.valueOf(s));
    }
    return getEventCookie(paramInt, s, paramShort);
  }
  
  private static long getEventCookie(int paramInt, short paramShort1, short paramShort2)
  {
    long l = paramInt;
    return (paramShort1 & 0xFFFF) << 32 | l | (paramShort2 & 0xFFFF) << 48;
  }
  
  private void maybePostFrameCallbackFromNonUI()
  {
    if (mReactEventEmitter != null) {
      mCurrentFrameCallback.maybePostFromNonUI();
    }
  }
  
  private void moveStagedEventsToDispatchQueue()
  {
    Object localObject4 = mEventsStagingLock;
    for (;;)
    {
      int i;
      try
      {
        Object localObject5 = mEventsToDispatchLock;
        i = 0;
        try
        {
          if (i < mEventStaging.size())
          {
            Object localObject1 = (Event)mEventStaging.get(i);
            if (!((Event)localObject1).canCoalesce())
            {
              addEventToEventsToDispatch((Event)localObject1);
              break label245;
            }
            long l = getEventCookie(((Event)localObject1).getViewTag(), ((Event)localObject1).getEventName(), ((Event)localObject1).getCoalescingKey());
            Integer localInteger = (Integer)mEventCookieToLastEventIdx.get(l);
            localObject3 = null;
            if (localInteger == null)
            {
              mEventCookieToLastEventIdx.put(l, Integer.valueOf(mEventsToDispatchSize));
            }
            else
            {
              localObject3 = mEventsToDispatch[localInteger.intValue()];
              Event localEvent = ((Event)localObject1).coalesce((Event)localObject3);
              if (localEvent == localObject3) {
                break label235;
              }
              mEventCookieToLastEventIdx.put(l, Integer.valueOf(mEventsToDispatchSize));
              mEventsToDispatch[localInteger.intValue()] = null;
              localObject1 = localEvent;
            }
            if (localObject1 != null) {
              addEventToEventsToDispatch((Event)localObject1);
            }
            if (localObject3 == null) {
              break label245;
            }
            ((Event)localObject3).dispose();
            break label245;
          }
          else
          {
            mEventStaging.clear();
            return;
          }
        }
        catch (Throwable localThrowable1)
        {
          throw localThrowable1;
        }
        Object localObject3 = localThrowable2;
      }
      catch (Throwable localThrowable2)
      {
        throw localThrowable2;
      }
      label235:
      Object localObject2 = null;
      continue;
      label245:
      i += 1;
    }
  }
  
  private void stopFrameCallback()
  {
    UiThreadUtil.assertOnUiThread();
    mCurrentFrameCallback.stop();
  }
  
  public void addBatchEventDispatchedListener(BatchEventDispatchedListener paramBatchEventDispatchedListener)
  {
    mPostEventDispatchListeners.add(paramBatchEventDispatchedListener);
  }
  
  public void addListener(EventDispatcherListener paramEventDispatcherListener)
  {
    mListeners.add(paramEventDispatcherListener);
  }
  
  public void dispatchAllEvents()
  {
    maybePostFrameCallbackFromNonUI();
  }
  
  public void dispatchEvent(Event paramEvent)
  {
    Assertions.assertCondition(paramEvent.isInitialized(), "Dispatched event hasn't been initialized");
    Object localObject = mListeners.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((EventDispatcherListener)((Iterator)localObject).next()).onEventDispatch(paramEvent);
    }
    localObject = mEventsStagingLock;
    try
    {
      mEventStaging.add(paramEvent);
      Systrace.startAsyncFlow(0L, paramEvent.getEventName(), paramEvent.getUniqueID());
      maybePostFrameCallbackFromNonUI();
      return;
    }
    catch (Throwable paramEvent)
    {
      throw paramEvent;
    }
  }
  
  public void onCatalystInstanceDestroyed()
  {
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        EventDispatcher.this.stopFrameCallback();
      }
    });
  }
  
  public void onHostDestroy()
  {
    stopFrameCallback();
  }
  
  public void onHostPause()
  {
    stopFrameCallback();
  }
  
  public void onHostResume()
  {
    maybePostFrameCallbackFromNonUI();
  }
  
  public void registerEventEmitter(int paramInt, RCTEventEmitter paramRCTEventEmitter)
  {
    mReactEventEmitter.register(paramInt, paramRCTEventEmitter);
  }
  
  public void removeBatchEventDispatchedListener(BatchEventDispatchedListener paramBatchEventDispatchedListener)
  {
    mPostEventDispatchListeners.remove(paramBatchEventDispatchedListener);
  }
  
  public void removeListener(EventDispatcherListener paramEventDispatcherListener)
  {
    mListeners.remove(paramEventDispatcherListener);
  }
  
  public void unregisterEventEmitter(int paramInt)
  {
    mReactEventEmitter.unregister(paramInt);
  }
  
  private class DispatchEventsRunnable
    implements Runnable
  {
    private DispatchEventsRunnable() {}
    
    public void run()
    {
      Systrace.beginSection(0L, "DispatchEventsRunnable");
      for (;;)
      {
        try
        {
          Systrace.endAsyncFlow(0L, "ScheduleDispatchFrameCallback", mHasDispatchScheduledCount.getAndIncrement());
          Object localObject = EventDispatcher.this;
          int j = 0;
          EventDispatcher.access$402((EventDispatcher)localObject, false);
          Assertions.assertNotNull(mReactEventEmitter);
          localObject = mEventsToDispatchLock;
          int i;
          try
          {
            if (mEventsToDispatchSize > 0)
            {
              i = j;
              if (mEventsToDispatchSize > 1)
              {
                Arrays.sort(mEventsToDispatch, 0, mEventsToDispatchSize, EventDispatcher.EVENT_COMPARATOR);
                i = j;
              }
              if (i < mEventsToDispatchSize)
              {
                Event localEvent = mEventsToDispatch[i];
                if (localEvent != null)
                {
                  Systrace.endAsyncFlow(0L, localEvent.getEventName(), localEvent.getUniqueID());
                  localEvent.dispatch(mReactEventEmitter);
                  localEvent.dispose();
                }
              }
              else
              {
                EventDispatcher.this.clearEventsToDispatch();
                mEventCookieToLastEventIdx.clear();
              }
            }
            else
            {
              localObject = mPostEventDispatchListeners.iterator();
              boolean bool = ((Iterator)localObject).hasNext();
              if (bool)
              {
                ((BatchEventDispatchedListener)((Iterator)localObject).next()).onBatchEventDispatched();
                continue;
              }
              Systrace.endSection(0L);
              return;
            }
          }
          catch (Throwable localThrowable2)
          {
            throw localThrowable2;
          }
          i += 1;
        }
        catch (Throwable localThrowable1)
        {
          Systrace.endSection(0L);
          throw localThrowable1;
        }
      }
    }
  }
  
  private class ScheduleDispatchFrameCallback
    extends ChoreographerCompat.FrameCallback
  {
    private volatile boolean mIsPosted = false;
    private boolean mShouldStop = false;
    
    private ScheduleDispatchFrameCallback() {}
    
    private void post()
    {
      ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, mCurrentFrameCallback);
    }
    
    public void doFrame(long paramLong)
    {
      
      if (mShouldStop) {
        mIsPosted = false;
      } else {
        post();
      }
      Systrace.beginSection(0L, "ScheduleDispatchFrameCallback");
      try
      {
        EventDispatcher.this.moveStagedEventsToDispatchQueue();
        boolean bool = mHasDispatchScheduled;
        if (!bool)
        {
          EventDispatcher.access$402(EventDispatcher.this, true);
          Systrace.startAsyncFlow(0L, "ScheduleDispatchFrameCallback", mHasDispatchScheduledCount.get());
          mReactContext.runOnJSQueueThread(mDispatchEventsRunnable);
        }
        Systrace.endSection(0L);
        return;
      }
      catch (Throwable localThrowable)
      {
        Systrace.endSection(0L);
        throw localThrowable;
      }
    }
    
    public void maybePost()
    {
      if (!mIsPosted)
      {
        mIsPosted = true;
        post();
      }
    }
    
    public void maybePostFromNonUI()
    {
      if (mIsPosted) {
        return;
      }
      if (mReactContext.isOnUiQueueThread())
      {
        maybePost();
        return;
      }
      mReactContext.runOnUiQueueThread(new Runnable()
      {
        public void run()
        {
          maybePost();
        }
      });
    }
    
    public void stop()
    {
      mShouldStop = true;
    }
  }
}
