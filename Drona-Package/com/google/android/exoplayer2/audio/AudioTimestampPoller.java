package com.google.android.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

final class AudioTimestampPoller
{
  private static final int ERROR_POLL_INTERVAL_US = 500000;
  private static final int FAST_POLL_INTERVAL_US = 5000;
  private static final int INITIALIZING_DURATION_US = 500000;
  private static final int SLOW_POLL_INTERVAL_US = 10000000;
  private static final int STATE_ERROR = 4;
  private static final int STATE_INITIALIZING = 0;
  private static final int STATE_NO_TIMESTAMP = 3;
  private static final int STATE_TIMESTAMP = 1;
  private static final int STATE_TIMESTAMP_ADVANCING = 2;
  @Nullable
  private final AudioTimestampV19 audioTimestamp;
  private long initialTimestampPositionFrames;
  private long initializeSystemTimeUs;
  private long lastTimestampSampleTimeUs;
  private long sampleIntervalUs;
  private int state;
  
  public AudioTimestampPoller(AudioTrack paramAudioTrack)
  {
    if (Util.SDK_INT >= 19)
    {
      audioTimestamp = new AudioTimestampV19(paramAudioTrack);
      reset();
      return;
    }
    audioTimestamp = null;
    updateState(3);
  }
  
  private void updateState(int paramInt)
  {
    state = paramInt;
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    case 4: 
      sampleIntervalUs = 500000L;
      return;
    case 2: 
    case 3: 
      sampleIntervalUs = 10000000L;
      return;
    case 1: 
      sampleIntervalUs = 5000L;
      return;
    }
    lastTimestampSampleTimeUs = 0L;
    initialTimestampPositionFrames = -1L;
    initializeSystemTimeUs = (System.nanoTime() / 1000L);
    sampleIntervalUs = 5000L;
  }
  
  public void acceptTimestamp()
  {
    if (state == 4) {
      reset();
    }
  }
  
  public long getTimestampPositionFrames()
  {
    if (audioTimestamp != null) {
      return audioTimestamp.getTimestampPositionFrames();
    }
    return -1L;
  }
  
  public long getTimestampSystemTimeUs()
  {
    if (audioTimestamp != null) {
      return audioTimestamp.getTimestampSystemTimeUs();
    }
    return -9223372036854775807L;
  }
  
  public boolean hasTimestamp()
  {
    if (state != 1) {
      return state == 2;
    }
    return true;
  }
  
  public boolean isTimestampAdvancing()
  {
    return state == 2;
  }
  
  public boolean maybePollTimestamp(long paramLong)
  {
    boolean bool;
    if (audioTimestamp != null)
    {
      if (paramLong - lastTimestampSampleTimeUs < sampleIntervalUs) {
        return false;
      }
      lastTimestampSampleTimeUs = paramLong;
      bool = audioTimestamp.maybeUpdateTimestamp();
      switch (state)
      {
      default: 
        throw new IllegalStateException();
      case 3: 
        if (!bool) {
          break;
        }
        reset();
        return bool;
      case 2: 
        if (bool) {
          break;
        }
        reset();
        return bool;
      case 1: 
        if (bool)
        {
          if (audioTimestamp.getTimestampPositionFrames() <= initialTimestampPositionFrames) {
            break;
          }
          updateState(2);
          return bool;
        }
        reset();
        return bool;
      case 0: 
        if (bool)
        {
          if (audioTimestamp.getTimestampSystemTimeUs() >= initializeSystemTimeUs)
          {
            initialTimestampPositionFrames = audioTimestamp.getTimestampPositionFrames();
            updateState(1);
            return bool;
          }
          return false;
        }
        if (paramLong - initializeSystemTimeUs <= 500000L) {
          break;
        }
        updateState(3);
      case 4: 
        return bool;
      }
    }
    else
    {
      return false;
    }
    return bool;
  }
  
  public void rejectTimestamp()
  {
    updateState(4);
  }
  
  public void reset()
  {
    if (audioTimestamp != null) {
      updateState(0);
    }
  }
  
  @TargetApi(19)
  private static final class AudioTimestampV19
  {
    private final AudioTimestamp audioTimestamp;
    private final AudioTrack audioTrack;
    private long lastTimestampPositionFrames;
    private long lastTimestampRawPositionFrames;
    private long rawTimestampFramePositionWrapCount;
    
    public AudioTimestampV19(AudioTrack paramAudioTrack)
    {
      audioTrack = paramAudioTrack;
      audioTimestamp = new AudioTimestamp();
    }
    
    public long getTimestampPositionFrames()
    {
      return lastTimestampPositionFrames;
    }
    
    public long getTimestampSystemTimeUs()
    {
      return audioTimestamp.nanoTime / 1000L;
    }
    
    public boolean maybeUpdateTimestamp()
    {
      boolean bool = audioTrack.getTimestamp(audioTimestamp);
      if (bool)
      {
        long l = audioTimestamp.framePosition;
        if (lastTimestampRawPositionFrames > l) {
          rawTimestampFramePositionWrapCount += 1L;
        }
        lastTimestampRawPositionFrames = l;
        lastTimestampPositionFrames = (l + (rawTimestampFramePositionWrapCount << 32));
      }
      return bool;
    }
  }
}
