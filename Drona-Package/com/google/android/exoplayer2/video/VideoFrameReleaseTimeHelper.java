package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.exoplayer2.util.Util;

@TargetApi(16)
public final class VideoFrameReleaseTimeHelper
{
  private static final long CHOREOGRAPHER_SAMPLE_DELAY_MILLIS = 500L;
  private static final long MAX_ALLOWED_DRIFT_NS = 20000000L;
  private static final int MIN_FRAMES_FOR_ADJUSTMENT = 6;
  private static final long VSYNC_OFFSET_PERCENTAGE = 80L;
  private long adjustedLastFrameTimeNs;
  private final DefaultDisplayListener displayListener;
  private long frameCount;
  private boolean haveSync;
  private long lastFramePresentationTimeUs;
  private long pendingAdjustedFrameTimeNs;
  private long syncFramePresentationTimeNs;
  private long syncUnadjustedReleaseTimeNs;
  private long vsyncDurationNs;
  private long vsyncOffsetNs;
  private final VSyncSampler vsyncSampler;
  private final WindowManager windowManager;
  
  public VideoFrameReleaseTimeHelper()
  {
    this(null);
  }
  
  public VideoFrameReleaseTimeHelper(Context paramContext)
  {
    DefaultDisplayListener localDefaultDisplayListener = null;
    if (paramContext != null)
    {
      Context localContext = paramContext.getApplicationContext();
      paramContext = localContext;
      windowManager = ((WindowManager)localContext.getSystemService("window"));
    }
    else
    {
      windowManager = null;
    }
    if (windowManager != null)
    {
      if (Util.SDK_INT >= 17) {
        localDefaultDisplayListener = maybeBuildDefaultDisplayListenerV17(paramContext);
      }
      displayListener = localDefaultDisplayListener;
      vsyncSampler = VSyncSampler.getInstance();
    }
    else
    {
      displayListener = null;
      vsyncSampler = null;
    }
    vsyncDurationNs = -9223372036854775807L;
    vsyncOffsetNs = -9223372036854775807L;
  }
  
  private static long closestVsync(long paramLong1, long paramLong2, long paramLong3)
  {
    paramLong2 += (paramLong1 - paramLong2) / paramLong3 * paramLong3;
    long l;
    if (paramLong1 <= paramLong2)
    {
      l = paramLong2 - paramLong3;
    }
    else
    {
      l = paramLong2;
      paramLong2 = paramLong3 + paramLong2;
    }
    if (paramLong2 - paramLong1 < paramLong1 - l) {
      return paramLong2;
    }
    return l;
  }
  
  private boolean isDriftTooLarge(long paramLong1, long paramLong2)
  {
    long l = syncFramePresentationTimeNs;
    return Math.abs(paramLong2 - syncUnadjustedReleaseTimeNs - (paramLong1 - l)) > 20000000L;
  }
  
  private DefaultDisplayListener maybeBuildDefaultDisplayListenerV17(Context paramContext)
  {
    paramContext = (DisplayManager)paramContext.getSystemService("display");
    if (paramContext == null) {
      return null;
    }
    return new DefaultDisplayListener(paramContext);
  }
  
  private void updateDefaultDisplayRefreshRateParams()
  {
    Display localDisplay = windowManager.getDefaultDisplay();
    if (localDisplay != null)
    {
      vsyncDurationNs = ((1.0E9D / localDisplay.getRefreshRate()));
      vsyncOffsetNs = (vsyncDurationNs * 80L / 100L);
    }
  }
  
  public long adjustReleaseTime(long paramLong1, long paramLong2)
  {
    long l3 = 1000L * paramLong1;
    if (haveSync)
    {
      if (paramLong1 != lastFramePresentationTimeUs)
      {
        frameCount += 1L;
        adjustedLastFrameTimeNs = pendingAdjustedFrameTimeNs;
      }
      if (frameCount >= 6L)
      {
        l1 = (l3 - syncFramePresentationTimeNs) / frameCount;
        l2 = adjustedLastFrameTimeNs + l1;
        if (isDriftTooLarge(l2, paramLong2))
        {
          haveSync = false;
          l1 = paramLong2;
          l2 = l3;
        }
        else
        {
          l1 = syncUnadjustedReleaseTimeNs + l2 - syncFramePresentationTimeNs;
        }
        break label139;
      }
      if (isDriftTooLarge(l3, paramLong2)) {
        haveSync = false;
      }
    }
    long l1 = paramLong2;
    long l2 = l3;
    label139:
    if (!haveSync)
    {
      syncFramePresentationTimeNs = l3;
      syncUnadjustedReleaseTimeNs = paramLong2;
      frameCount = 0L;
      haveSync = true;
    }
    lastFramePresentationTimeUs = paramLong1;
    pendingAdjustedFrameTimeNs = l2;
    if (vsyncSampler != null)
    {
      if (vsyncDurationNs == -9223372036854775807L) {
        return l1;
      }
      paramLong1 = vsyncSampler.sampledVsyncTimeNs;
      if (paramLong1 == -9223372036854775807L) {
        return l1;
      }
      return closestVsync(l1, paramLong1, vsyncDurationNs) - vsyncOffsetNs;
    }
    return l1;
  }
  
  public void disable()
  {
    if (windowManager != null)
    {
      if (displayListener != null) {
        displayListener.unregister();
      }
      vsyncSampler.removeObserver();
    }
  }
  
  public void enable()
  {
    haveSync = false;
    if (windowManager != null)
    {
      vsyncSampler.addObserver();
      if (displayListener != null) {
        displayListener.register();
      }
      updateDefaultDisplayRefreshRateParams();
    }
  }
  
  @TargetApi(17)
  private final class DefaultDisplayListener
    implements DisplayManager.DisplayListener
  {
    private final DisplayManager displayManager;
    
    public DefaultDisplayListener(DisplayManager paramDisplayManager)
    {
      displayManager = paramDisplayManager;
    }
    
    public void onDisplayAdded(int paramInt) {}
    
    public void onDisplayChanged(int paramInt)
    {
      if (paramInt == 0) {
        VideoFrameReleaseTimeHelper.this.updateDefaultDisplayRefreshRateParams();
      }
    }
    
    public void onDisplayRemoved(int paramInt) {}
    
    public void register()
    {
      displayManager.registerDisplayListener(this, null);
    }
    
    public void unregister()
    {
      displayManager.unregisterDisplayListener(this);
    }
  }
  
  private static final class VSyncSampler
    implements Choreographer.FrameCallback, Handler.Callback
  {
    private static final int CREATE_CHOREOGRAPHER = 0;
    private static final VSyncSampler INSTANCE = new VSyncSampler();
    private static final int MSG_ADD_OBSERVER = 1;
    private static final int MSG_REMOVE_OBSERVER = 2;
    private Choreographer choreographer;
    private final HandlerThread choreographerOwnerThread = new HandlerThread("ChoreographerOwner:Handler");
    private final Handler handler;
    private int observerCount;
    public volatile long sampledVsyncTimeNs = -9223372036854775807L;
    
    private VSyncSampler()
    {
      choreographerOwnerThread.start();
      handler = Util.createHandler(choreographerOwnerThread.getLooper(), this);
      handler.sendEmptyMessage(0);
    }
    
    private void addObserverInternal()
    {
      observerCount += 1;
      if (observerCount == 1) {
        choreographer.postFrameCallback(this);
      }
    }
    
    private void createChoreographerInstanceInternal()
    {
      choreographer = Choreographer.getInstance();
    }
    
    public static VSyncSampler getInstance()
    {
      return INSTANCE;
    }
    
    private void removeObserverInternal()
    {
      observerCount -= 1;
      if (observerCount == 0)
      {
        choreographer.removeFrameCallback(this);
        sampledVsyncTimeNs = -9223372036854775807L;
      }
    }
    
    public void addObserver()
    {
      handler.sendEmptyMessage(1);
    }
    
    public void doFrame(long paramLong)
    {
      sampledVsyncTimeNs = paramLong;
      choreographer.postFrameCallbackDelayed(this, 500L);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (what)
      {
      default: 
        return false;
      case 2: 
        removeObserverInternal();
        return true;
      case 1: 
        addObserverInternal();
        return true;
      }
      createChoreographerInstanceInternal();
      return true;
    }
    
    public void removeObserver()
    {
      handler.sendEmptyMessage(2);
    }
  }
}
