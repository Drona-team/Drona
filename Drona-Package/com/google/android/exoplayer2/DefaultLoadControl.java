package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;

public class DefaultLoadControl
  implements LoadControl
{
  public static final int DEFAULT_BACK_BUFFER_DURATION_MS = 0;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
  public static final int DEFAULT_MAX_BUFFER_MS = 50000;
  public static final int DEFAULT_MIN_BUFFER_MS = 15000;
  public static final boolean DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS = true;
  public static final boolean DEFAULT_RETAIN_BACK_BUFFER_FROM_KEYFRAME = false;
  public static final int DEFAULT_TARGET_BUFFER_BYTES = -1;
  private final DefaultAllocator allocator;
  private final long backBufferDurationUs;
  private final long bufferForPlaybackAfterRebufferUs;
  private final long bufferForPlaybackUs;
  private boolean isBuffering;
  private final long maxBufferUs;
  private final long minBufferUs;
  private final boolean prioritizeTimeOverSizeThresholds;
  private final PriorityTaskManager priorityTaskManager;
  private final boolean retainBackBufferFromKeyframe;
  private final int targetBufferBytesOverwrite;
  private int targetBufferSize;
  
  public DefaultLoadControl()
  {
    this(new DefaultAllocator(true, 65536));
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator)
  {
    this(paramDefaultAllocator, 15000, 50000, 2500, 5000, -1, true);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    this(paramDefaultAllocator, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, null);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, PriorityTaskManager paramPriorityTaskManager)
  {
    this(paramDefaultAllocator, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, paramPriorityTaskManager, 0, false);
  }
  
  protected DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean1, PriorityTaskManager paramPriorityTaskManager, int paramInt6, boolean paramBoolean2)
  {
    assertGreaterOrEqual(paramInt3, 0, "bufferForPlaybackMs", "0");
    assertGreaterOrEqual(paramInt4, 0, "bufferForPlaybackAfterRebufferMs", "0");
    assertGreaterOrEqual(paramInt1, paramInt3, "minBufferMs", "bufferForPlaybackMs");
    assertGreaterOrEqual(paramInt1, paramInt4, "minBufferMs", "bufferForPlaybackAfterRebufferMs");
    assertGreaterOrEqual(paramInt2, paramInt1, "maxBufferMs", "minBufferMs");
    assertGreaterOrEqual(paramInt6, 0, "backBufferDurationMs", "0");
    allocator = paramDefaultAllocator;
    minBufferUs = IpAddress.msToUs(paramInt1);
    maxBufferUs = IpAddress.msToUs(paramInt2);
    bufferForPlaybackUs = IpAddress.msToUs(paramInt3);
    bufferForPlaybackAfterRebufferUs = IpAddress.msToUs(paramInt4);
    targetBufferBytesOverwrite = paramInt5;
    prioritizeTimeOverSizeThresholds = paramBoolean1;
    priorityTaskManager = paramPriorityTaskManager;
    backBufferDurationUs = IpAddress.msToUs(paramInt6);
    retainBackBufferFromKeyframe = paramBoolean2;
  }
  
  private static void assertGreaterOrEqual(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    boolean bool;
    if (paramInt1 >= paramInt2) {
      bool = true;
    } else {
      bool = false;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString1);
    localStringBuilder.append(" cannot be less than ");
    localStringBuilder.append(paramString2);
    Assertions.checkArgument(bool, localStringBuilder.toString());
  }
  
  private void reset(boolean paramBoolean)
  {
    targetBufferSize = 0;
    if ((priorityTaskManager != null) && (isBuffering)) {
      priorityTaskManager.remove(0);
    }
    isBuffering = false;
    if (paramBoolean) {
      allocator.reset();
    }
  }
  
  protected int calculateTargetBufferSize(Renderer[] paramArrayOfRenderer, TrackSelectionArray paramTrackSelectionArray)
  {
    int i = 0;
    int k;
    for (int j = 0; i < paramArrayOfRenderer.length; j = k)
    {
      k = j;
      if (paramTrackSelectionArray.getChapters(i) != null) {
        k = j + Util.getDefaultBufferSize(paramArrayOfRenderer[i].getTrackType());
      }
      i += 1;
    }
    return j;
  }
  
  public Allocator getAllocator()
  {
    return allocator;
  }
  
  public long getBackBufferDurationUs()
  {
    return backBufferDurationUs;
  }
  
  public void onPrepared()
  {
    reset(false);
  }
  
  public void onReleased()
  {
    reset(true);
  }
  
  public void onStopped()
  {
    reset(true);
  }
  
  public void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    int i;
    if (targetBufferBytesOverwrite == -1) {
      i = calculateTargetBufferSize(paramArrayOfRenderer, paramTrackSelectionArray);
    } else {
      i = targetBufferBytesOverwrite;
    }
    targetBufferSize = i;
    allocator.setTargetBufferSize(targetBufferSize);
  }
  
  public boolean retainBackBufferFromKeyframe()
  {
    return retainBackBufferFromKeyframe;
  }
  
  public boolean shouldContinueLoading(long paramLong, float paramFloat)
  {
    int i = allocator.getTotalBytesAllocated();
    int j = targetBufferSize;
    boolean bool2 = true;
    if (i >= j) {
      i = 1;
    } else {
      i = 0;
    }
    boolean bool3 = isBuffering;
    long l2 = minBufferUs;
    long l1 = l2;
    if (paramFloat > 1.0F) {
      l1 = Math.min(Util.getMediaDurationForPlayoutDuration(l2, paramFloat), maxBufferUs);
    }
    if (paramLong < l1)
    {
      boolean bool1 = bool2;
      if (!prioritizeTimeOverSizeThresholds) {
        if (i == 0) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
      isBuffering = bool1;
    }
    else if ((paramLong >= maxBufferUs) || (i != 0))
    {
      isBuffering = false;
    }
    if ((priorityTaskManager != null) && (isBuffering != bool3)) {
      if (isBuffering) {
        priorityTaskManager.add(0);
      } else {
        priorityTaskManager.remove(0);
      }
    }
    return isBuffering;
  }
  
  public boolean shouldStartPlayback(long paramLong, float paramFloat, boolean paramBoolean)
  {
    long l = Util.getPlayoutDurationForMediaDuration(paramLong, paramFloat);
    if (paramBoolean) {
      paramLong = bufferForPlaybackAfterRebufferUs;
    } else {
      paramLong = bufferForPlaybackUs;
    }
    return (paramLong <= 0L) || (l >= paramLong) || ((!prioritizeTimeOverSizeThresholds) && (allocator.getTotalBytesAllocated() >= targetBufferSize));
  }
  
  public static final class Builder
  {
    private DefaultAllocator allocator = null;
    private int backBufferDurationMs = 0;
    private int bufferForPlaybackAfterRebufferMs = 5000;
    private int bufferForPlaybackMs = 2500;
    private boolean createDefaultLoadControlCalled;
    private int maxBufferMs = 50000;
    private int minBufferMs = 15000;
    private boolean prioritizeTimeOverSizeThresholds = true;
    private PriorityTaskManager priorityTaskManager = null;
    private boolean retainBackBufferFromKeyframe = false;
    private int targetBufferBytes = -1;
    
    public Builder() {}
    
    public DefaultLoadControl createDefaultLoadControl()
    {
      createDefaultLoadControlCalled = true;
      if (allocator == null) {
        allocator = new DefaultAllocator(true, 65536);
      }
      return new DefaultLoadControl(allocator, minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs, targetBufferBytes, prioritizeTimeOverSizeThresholds, priorityTaskManager, backBufferDurationMs, retainBackBufferFromKeyframe);
    }
    
    public Builder setAllocator(DefaultAllocator paramDefaultAllocator)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      allocator = paramDefaultAllocator;
      return this;
    }
    
    public Builder setBackBuffer(int paramInt, boolean paramBoolean)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      backBufferDurationMs = paramInt;
      retainBackBufferFromKeyframe = paramBoolean;
      return this;
    }
    
    public Builder setBufferDurationsMs(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      minBufferMs = paramInt1;
      maxBufferMs = paramInt2;
      bufferForPlaybackMs = paramInt3;
      bufferForPlaybackAfterRebufferMs = paramInt4;
      return this;
    }
    
    public Builder setPrioritizeTimeOverSizeThresholds(boolean paramBoolean)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      prioritizeTimeOverSizeThresholds = paramBoolean;
      return this;
    }
    
    public Builder setPriorityTaskManager(PriorityTaskManager paramPriorityTaskManager)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      priorityTaskManager = paramPriorityTaskManager;
      return this;
    }
    
    public Builder setTargetBufferBytes(int paramInt)
    {
      Assertions.checkState(createDefaultLoadControlCalled ^ true);
      targetBufferBytes = paramInt;
      return this;
    }
  }
}
