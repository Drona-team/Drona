package com.facebook.react.modules.debug;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.modules.core.ChoreographerCompat;
import com.facebook.react.modules.core.ChoreographerCompat.FrameCallback;
import com.facebook.react.uimanager.UIManagerModule;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FpsDebugFrameCallback
  extends ChoreographerCompat.FrameCallback
{
  private static final double EXPECTED_FRAME_TIME = 16.9D;
  private int m4PlusFrameStutters = 0;
  @Nullable
  private ChoreographerCompat mChoreographer;
  private final DidJSUpdateUiDuringFrameDetector mDidJSUpdateUiDuringFrameDetector;
  private int mExpectedNumFramesPrev = 0;
  private long mFirstFrameTime = -1L;
  private boolean mIsRecordingFpsInfoAtEachFrame = false;
  private long mLastFrameTime = -1L;
  private int mNumFrameCallbacks = 0;
  private int mNumFrameCallbacksWithBatchDispatches = 0;
  private final ReactContext mReactContext;
  private boolean mShouldStop = false;
  @Nullable
  private TreeMap<Long, FpsInfo> mTimeToFps;
  private final UIManagerModule mUIManagerModule;
  
  public FpsDebugFrameCallback(ReactContext paramReactContext)
  {
    mReactContext = paramReactContext;
    mUIManagerModule = ((UIManagerModule)paramReactContext.getNativeModule(UIManagerModule.class));
    mDidJSUpdateUiDuringFrameDetector = new DidJSUpdateUiDuringFrameDetector();
  }
  
  public void doFrame(long paramLong)
  {
    if (mShouldStop) {
      return;
    }
    if (mFirstFrameTime == -1L) {
      mFirstFrameTime = paramLong;
    }
    long l = mLastFrameTime;
    mLastFrameTime = paramLong;
    if (mDidJSUpdateUiDuringFrameDetector.getDidJSHitFrameAndCleanup(l, paramLong)) {
      mNumFrameCallbacksWithBatchDispatches += 1;
    }
    mNumFrameCallbacks += 1;
    int i = getExpectedNumFrames();
    if (i - mExpectedNumFramesPrev - 1 >= 4) {
      m4PlusFrameStutters += 1;
    }
    if (mIsRecordingFpsInfoAtEachFrame)
    {
      Assertions.assertNotNull(mTimeToFps);
      FpsInfo localFpsInfo = new FpsInfo(getNumFrames(), getNumJSFrames(), i, m4PlusFrameStutters, getFPS(), getJSFPS(), getTotalTimeMS());
      mTimeToFps.put(Long.valueOf(System.currentTimeMillis()), localFpsInfo);
    }
    mExpectedNumFramesPrev = i;
    if (mChoreographer != null) {
      mChoreographer.postFrameCallback(this);
    }
  }
  
  public int get4PlusFrameStutters()
  {
    return m4PlusFrameStutters;
  }
  
  public int getExpectedNumFrames()
  {
    return (int)(getTotalTimeMS() / 16.9D + 1.0D);
  }
  
  public double getFPS()
  {
    if (mLastFrameTime == mFirstFrameTime) {
      return 0.0D;
    }
    return getNumFrames() * 1.0E9D / (mLastFrameTime - mFirstFrameTime);
  }
  
  public FpsInfo getFpsInfo(long paramLong)
  {
    Assertions.assertNotNull(mTimeToFps, "FPS was not recorded at each frame!");
    Map.Entry localEntry = mTimeToFps.floorEntry(Long.valueOf(paramLong));
    if (localEntry == null) {
      return null;
    }
    return (FpsInfo)localEntry.getValue();
  }
  
  public double getJSFPS()
  {
    if (mLastFrameTime == mFirstFrameTime) {
      return 0.0D;
    }
    return getNumJSFrames() * 1.0E9D / (mLastFrameTime - mFirstFrameTime);
  }
  
  public int getNumFrames()
  {
    return mNumFrameCallbacks - 1;
  }
  
  public int getNumJSFrames()
  {
    return mNumFrameCallbacksWithBatchDispatches - 1;
  }
  
  public int getTotalTimeMS()
  {
    return (int)(mLastFrameTime - mFirstFrameTime) / 1000000;
  }
  
  public void reset()
  {
    mFirstFrameTime = -1L;
    mLastFrameTime = -1L;
    mNumFrameCallbacks = 0;
    m4PlusFrameStutters = 0;
    mNumFrameCallbacksWithBatchDispatches = 0;
    mIsRecordingFpsInfoAtEachFrame = false;
    mTimeToFps = null;
  }
  
  public void start()
  {
    mShouldStop = false;
    mReactContext.getCatalystInstance().addBridgeIdleDebugListener(mDidJSUpdateUiDuringFrameDetector);
    mUIManagerModule.setViewHierarchyUpdateDebugListener(mDidJSUpdateUiDuringFrameDetector);
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        FpsDebugFrameCallback.access$002(FpsDebugFrameCallback.this, ChoreographerCompat.getInstance());
        mChoreographer.postFrameCallback(jdField_this);
      }
    });
  }
  
  public void startAndRecordFpsAtEachFrame()
  {
    mTimeToFps = new TreeMap();
    mIsRecordingFpsInfoAtEachFrame = true;
    start();
  }
  
  public void stop()
  {
    mShouldStop = true;
    mReactContext.getCatalystInstance().removeBridgeIdleDebugListener(mDidJSUpdateUiDuringFrameDetector);
    mUIManagerModule.setViewHierarchyUpdateDebugListener(null);
  }
  
  public static class FpsInfo
  {
    public final double jsFps;
    public final double mAmount;
    public final int total4PlusFrameStutters;
    public final int totalExpectedFrames;
    public final int totalFrames;
    public final int totalJsFrames;
    public final int totalTimeMs;
    
    public FpsInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, int paramInt5)
    {
      totalFrames = paramInt1;
      totalJsFrames = paramInt2;
      totalExpectedFrames = paramInt3;
      total4PlusFrameStutters = paramInt4;
      mAmount = paramDouble1;
      jsFps = paramDouble2;
      totalTimeMs = paramInt5;
    }
  }
}