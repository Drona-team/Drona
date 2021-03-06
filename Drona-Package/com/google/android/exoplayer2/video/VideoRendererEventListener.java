package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.view.Surface;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public abstract interface VideoRendererEventListener
{
  public abstract void onDroppedFrames(int paramInt, long paramLong);
  
  public abstract void onRenderedFirstFrame(Surface paramSurface);
  
  public abstract void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2);
  
  public abstract void onVideoDisabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onVideoEnabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onVideoInputFormatChanged(Format paramFormat);
  
  public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  
  public static final class EventDispatcher
  {
    @Nullable
    private final Handler handler;
    @Nullable
    private final VideoRendererEventListener listener;
    
    public EventDispatcher(Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener)
    {
      if (paramVideoRendererEventListener != null) {
        paramHandler = (Handler)Assertions.checkNotNull(paramHandler);
      } else {
        paramHandler = null;
      }
      handler = paramHandler;
      listener = paramVideoRendererEventListener;
    }
    
    public void decoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.Y232CA7hogfrRJjYu2VeUSxg0VQ(this, paramString, paramLong1, paramLong2));
      }
    }
    
    public void disabled(DecoderCounters paramDecoderCounters)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.qTQ-0WnG_WelRJ9iR8L0OaiS0Go(this, paramDecoderCounters));
      }
    }
    
    public void droppedFrames(int paramInt, long paramLong)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.wpJzum9Nim-WREQi3I6t6RZgGzs(this, paramInt, paramLong));
      }
    }
    
    public void enabled(DecoderCounters paramDecoderCounters)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.Zf6ofdxzBBJ5SL288lE0HglRj8g(this, paramDecoderCounters));
      }
    }
    
    public void inputFormatChanged(Format paramFormat)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.26y6c6BFFT4OL6bJiMmdsfxDEMQ(this, paramFormat));
      }
    }
    
    public void renderedFirstFrame(Surface paramSurface)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.SFK5uUI0PHTm3Dg6Wdc1eRaQ9xk(this, paramSurface));
      }
    }
    
    public void videoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      if (listener != null) {
        handler.post(new -..Lambda.VideoRendererEventListener.EventDispatcher.TaBV3X3b5lKElsQ7tczViKAyQ3w(this, paramInt1, paramInt2, paramInt3, paramFloat));
      }
    }
  }
}
