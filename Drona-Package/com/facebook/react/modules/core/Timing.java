package com.facebook.react.modules.core;

import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.SystemClock;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.jstasks.HeadlessJsTaskContext;
import com.facebook.react.jstasks.HeadlessJsTaskEventListener;
import com.facebook.react.module.annotations.ReactModule;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@ReactModule(name="Timing")
public final class Timing
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener, HeadlessJsTaskEventListener
{
  private static final float FRAME_DURATION_MS = 16.666666F;
  private static final float IDLE_CALLBACK_FRAME_DEADLINE_MS = 1.0F;
  public static final String NAME = "Timing";
  private final AtomicBoolean isPaused = new AtomicBoolean(true);
  private final AtomicBoolean isRunningTasks = new AtomicBoolean(false);
  @Nullable
  private IdleCallbackRunnable mCurrentIdleCallbackRunnable;
  private final DevSupportManager mDevSupportManager;
  private boolean mFrameCallbackPosted = false;
  private boolean mFrameIdleCallbackPosted = false;
  private final Object mIdleCallbackGuard = new Object();
  private final IdleFrameCallback mIdleFrameCallback = new IdleFrameCallback(null);
  private final ReactChoreographer mReactChoreographer;
  private boolean mSendIdleEvents = false;
  private final TimerFrameCallback mTimerFrameCallback = new TimerFrameCallback(null);
  private final Object mTimerGuard = new Object();
  private final SparseArray<Timer> mTimerIdsToTimers;
  private final PriorityQueue<Timer> mTimers;
  
  public Timing(ReactApplicationContext paramReactApplicationContext, DevSupportManager paramDevSupportManager)
  {
    super(paramReactApplicationContext);
    mDevSupportManager = paramDevSupportManager;
    mTimers = new PriorityQueue(11, new Comparator()
    {
      public int compare(Timing.Timer paramAnonymousTimer1, Timing.Timer paramAnonymousTimer2)
      {
        boolean bool = Timing.Timer.access$400(paramAnonymousTimer1) - Timing.Timer.access$400(paramAnonymousTimer2) < 0L;
        if (!bool) {
          return 0;
        }
        if (bool) {
          return -1;
        }
        return 1;
      }
    });
    mTimerIdsToTimers = new SparseArray();
    mReactChoreographer = ReactChoreographer.getInstance();
  }
  
  private void clearChoreographerIdleCallback()
  {
    if (mFrameIdleCallbackPosted)
    {
      mReactChoreographer.removeFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, mIdleFrameCallback);
      mFrameIdleCallbackPosted = false;
    }
  }
  
  private void clearFrameCallback()
  {
    HeadlessJsTaskContext localHeadlessJsTaskContext = HeadlessJsTaskContext.getInstance(getReactApplicationContext());
    if ((mFrameCallbackPosted) && (isPaused.get()) && (!localHeadlessJsTaskContext.hasActiveTasks()))
    {
      mReactChoreographer.removeFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, mTimerFrameCallback);
      mFrameCallbackPosted = false;
    }
  }
  
  private void maybeIdleCallback()
  {
    if ((isPaused.get()) && (!isRunningTasks.get())) {
      clearFrameCallback();
    }
  }
  
  private void maybeSetChoreographerIdleCallback()
  {
    Object localObject = mIdleCallbackGuard;
    try
    {
      if (mSendIdleEvents) {
        setChoreographerIdleCallback();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void setChoreographerCallback()
  {
    if (!mFrameCallbackPosted)
    {
      mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, mTimerFrameCallback);
      mFrameCallbackPosted = true;
    }
  }
  
  private void setChoreographerIdleCallback()
  {
    if (!mFrameIdleCallbackPosted)
    {
      mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, mIdleFrameCallback);
      mFrameIdleCallbackPosted = true;
    }
  }
  
  public void createTimer(int paramInt1, int paramInt2, double paramDouble, boolean paramBoolean)
  {
    long l1 = SystemClock.currentTimeMillis();
    long l2 = paramDouble;
    if ((mDevSupportManager.getDevSupportEnabled()) && (Math.abs(l2 - l1) > 60000L)) {
      ((JSTimers)getReactApplicationContext().getJSModule(JSTimers.class)).emitTimeDriftWarning("Debugger and device times have drifted by more than 60s. Please correct this by running adb shell \"date `date +%m%d%H%M%Y.%S`\" on your debugger machine.");
    }
    l1 = Math.max(0L, l2 - l1 + paramInt2);
    if ((paramInt2 == 0) && (!paramBoolean))
    {
      localObject = Arguments.createArray();
      ((WritableArray)localObject).pushInt(paramInt1);
      ((JSTimers)getReactApplicationContext().getJSModule(JSTimers.class)).callTimers((WritableArray)localObject);
      return;
    }
    Timer localTimer = new Timer(paramInt1, SystemClock.nanoTime() / 1000000L + l1, paramInt2, paramBoolean, null);
    Object localObject = mTimerGuard;
    try
    {
      mTimers.add(localTimer);
      mTimerIdsToTimers.put(paramInt1, localTimer);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void deleteTimer(int paramInt)
  {
    Object localObject = mTimerGuard;
    try
    {
      Timer localTimer = (Timer)mTimerIdsToTimers.get(paramInt);
      if (localTimer == null) {
        return;
      }
      mTimerIdsToTimers.remove(paramInt);
      mTimers.remove(localTimer);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public String getName()
  {
    return "Timing";
  }
  
  public void initialize()
  {
    getReactApplicationContext().addLifecycleEventListener(this);
    HeadlessJsTaskContext.getInstance(getReactApplicationContext()).addTaskEventListener(this);
  }
  
  public void onCatalystInstanceDestroy()
  {
    clearFrameCallback();
    clearChoreographerIdleCallback();
    HeadlessJsTaskContext.getInstance(getReactApplicationContext()).removeTaskEventListener(this);
  }
  
  public void onHeadlessJsTaskFinish(int paramInt)
  {
    if (!HeadlessJsTaskContext.getInstance(getReactApplicationContext()).hasActiveTasks())
    {
      isRunningTasks.set(false);
      clearFrameCallback();
      maybeIdleCallback();
    }
  }
  
  public void onHeadlessJsTaskStart(int paramInt)
  {
    if (!isRunningTasks.getAndSet(true))
    {
      setChoreographerCallback();
      maybeSetChoreographerIdleCallback();
    }
  }
  
  public void onHostDestroy()
  {
    clearFrameCallback();
    maybeIdleCallback();
  }
  
  public void onHostPause()
  {
    isPaused.set(true);
    clearFrameCallback();
    maybeIdleCallback();
  }
  
  public void onHostResume()
  {
    isPaused.set(false);
    setChoreographerCallback();
    maybeSetChoreographerIdleCallback();
  }
  
  public void setSendIdleEvents(final boolean paramBoolean)
  {
    Object localObject = mIdleCallbackGuard;
    try
    {
      mSendIdleEvents = paramBoolean;
      UiThreadUtil.runOnUiThread(new Runnable()
      {
        public void run()
        {
          Object localObject = mIdleCallbackGuard;
          try
          {
            if (paramBoolean) {
              Timing.this.setChoreographerIdleCallback();
            } else {
              Timing.this.clearChoreographerIdleCallback();
            }
            return;
          }
          catch (Throwable localThrowable)
          {
            throw localThrowable;
          }
        }
      });
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private class IdleCallbackRunnable
    implements Runnable
  {
    private volatile boolean mCancelled = false;
    private final long mFrameStartTime;
    
    public IdleCallbackRunnable(long paramLong)
    {
      mFrameStartTime = paramLong;
    }
    
    public void cancel()
    {
      mCancelled = true;
    }
    
    public void run()
    {
      if (mCancelled) {
        return;
      }
      long l1 = mFrameStartTime / 1000000L;
      l1 = SystemClock.uptimeMillis() - l1;
      long l2 = SystemClock.currentTimeMillis();
      if (16.666666F - (float)l1 < 1.0F) {
        return;
      }
      Object localObject = mIdleCallbackGuard;
      try
      {
        boolean bool = mSendIdleEvents;
        if (bool) {
          ((JSTimers)getReactApplicationContext().getJSModule(JSTimers.class)).callIdleCallbacks(l2 - l1);
        }
        Timing.access$1102(Timing.this, null);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  private class IdleFrameCallback
    extends ChoreographerCompat.FrameCallback
  {
    private IdleFrameCallback() {}
    
    public void doFrame(long paramLong)
    {
      if ((isPaused.get()) && (!isRunningTasks.get())) {
        return;
      }
      if (mCurrentIdleCallbackRunnable != null) {
        mCurrentIdleCallbackRunnable.cancel();
      }
      Timing.access$1102(Timing.this, new Timing.IdleCallbackRunnable(Timing.this, paramLong));
      getReactApplicationContext().runOnJSQueueThread(mCurrentIdleCallbackRunnable);
      mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, this);
    }
  }
  
  private static class Timer
  {
    private final int mCallbackID;
    private final int mInterval;
    private final boolean mRepeat;
    private long mTargetTime;
    
    private Timer(int paramInt1, long paramLong, int paramInt2, boolean paramBoolean)
    {
      mCallbackID = paramInt1;
      mTargetTime = paramLong;
      mInterval = paramInt2;
      mRepeat = paramBoolean;
    }
  }
  
  private class TimerFrameCallback
    extends ChoreographerCompat.FrameCallback
  {
    @Nullable
    private WritableArray mTimersToCall = null;
    
    private TimerFrameCallback() {}
    
    public void doFrame(long paramLong)
    {
      if ((isPaused.get()) && (!isRunningTasks.get())) {
        return;
      }
      paramLong /= 1000000L;
      Object localObject = mTimerGuard;
      try
      {
        while ((!mTimers.isEmpty()) && (access$300peekmTargetTime < paramLong))
        {
          Timing.Timer localTimer = (Timing.Timer)mTimers.poll();
          if (mTimersToCall == null) {
            mTimersToCall = Arguments.createArray();
          }
          mTimersToCall.pushInt(mCallbackID);
          if (mRepeat)
          {
            Timing.Timer.access$402(localTimer, mInterval + paramLong);
            mTimers.add(localTimer);
          }
          else
          {
            mTimerIdsToTimers.remove(mCallbackID);
          }
        }
        if (mTimersToCall != null)
        {
          ((JSTimers)getReactApplicationContext().getJSModule(JSTimers.class)).callTimers(mTimersToCall);
          mTimersToCall = null;
        }
        mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, this);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}