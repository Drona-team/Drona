package com.google.android.exoplayer2.audio;

import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public abstract interface AudioRendererEventListener
{
  public abstract void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2);
  
  public abstract void onAudioDisabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onAudioEnabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onAudioInputFormatChanged(Format paramFormat);
  
  public abstract void onAudioSessionId(int paramInt);
  
  public abstract void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2);
  
  public static final class EventDispatcher
  {
    @Nullable
    private final Handler handler;
    @Nullable
    private final AudioRendererEventListener listener;
    
    public EventDispatcher(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
    {
      if (paramAudioRendererEventListener != null) {
        paramHandler = (Handler)Assertions.checkNotNull(paramHandler);
      } else {
        paramHandler = null;
      }
      handler = paramHandler;
      listener = paramAudioRendererEventListener;
    }
    
    public void audioSessionId(int paramInt)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.a1B1YBHhPRCtc1MQAc2fSVEo22I(this, paramInt));
      }
    }
    
    public void audioTrackUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.oPQKly422CpX1mqIU2N6d76OGxk(this, paramInt, paramLong1, paramLong2));
      }
    }
    
    public void decoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.F29t8_xYSK7h_6CpLRlp2y2yb1E(this, paramString, paramLong1, paramLong2));
      }
    }
    
    public void disabled(DecoderCounters paramDecoderCounters)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.jb22FSnmUl2pGG0LguQS_Wd-LWk(this, paramDecoderCounters));
      }
    }
    
    public void enabled(DecoderCounters paramDecoderCounters)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.MUMUaHcEfIpwDLi9gxmScOQxifc(this, paramDecoderCounters));
      }
    }
    
    public void inputFormatChanged(Format paramFormat)
    {
      if (listener != null) {
        handler.post(new -..Lambda.AudioRendererEventListener.EventDispatcher.D7KvJbrpXrnWw4qzd_LI9ZtQytw(this, paramFormat));
      }
    }
  }
}
