package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.PlaybackParameters;
import java.nio.ByteBuffer;

public abstract interface AudioSink
{
  public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;
  
  public abstract void configure(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws AudioSink.ConfigurationException;
  
  public abstract void disableTunneling();
  
  public abstract void enableTunnelingV21(int paramInt);
  
  public abstract long getCurrentPositionUs(boolean paramBoolean);
  
  public abstract PlaybackParameters getPlaybackParameters();
  
  public abstract boolean handleBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioSink.InitializationException, AudioSink.WriteException;
  
  public abstract void handleDiscontinuity();
  
  public abstract boolean hasPendingData();
  
  public abstract boolean isEnded();
  
  public abstract void pause();
  
  public abstract void play();
  
  public abstract void playToEndOfStream()
    throws AudioSink.WriteException;
  
  public abstract void release();
  
  public abstract void reset();
  
  public abstract void setAudioAttributes(AudioAttributes paramAudioAttributes);
  
  public abstract void setAudioSessionId(int paramInt);
  
  public abstract void setAuxEffectInfo(AuxEffectInfo paramAuxEffectInfo);
  
  public abstract void setListener(Listener paramListener);
  
  public abstract PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters);
  
  public abstract void setVolume(float paramFloat);
  
  public abstract boolean supportsOutput(int paramInt1, int paramInt2);
  
  public static final class ConfigurationException
    extends Exception
  {
    public ConfigurationException(String paramString)
    {
      super();
    }
    
    public ConfigurationException(Throwable paramThrowable)
    {
      super();
    }
  }
  
  public static final class InitializationException
    extends Exception
  {
    public final int audioTrackState;
    
    public InitializationException(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super();
      audioTrackState = paramInt1;
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onAudioSessionId(int paramInt);
    
    public abstract void onPositionDiscontinuity();
    
    public abstract void onUnderrun(int paramInt, long paramLong1, long paramLong2);
  }
  
  public static final class WriteException
    extends Exception
  {
    public final int errorCode;
    
    public WriteException(int paramInt)
    {
      super();
      errorCode = paramInt;
    }
  }
}
