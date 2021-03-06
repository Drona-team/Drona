package com.google.android.exoplayer2.pc;

import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Locale;

public class DebugTextViewHelper
  implements Player.EventListener, Runnable
{
  private static final int REFRESH_INTERVAL_MS = 1000;
  private final SimpleExoPlayer player;
  private boolean started;
  private final TextView textView;
  
  public DebugTextViewHelper(SimpleExoPlayer paramSimpleExoPlayer, TextView paramTextView)
  {
    boolean bool;
    if (paramSimpleExoPlayer.getApplicationLooper() == Looper.getMainLooper()) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    player = paramSimpleExoPlayer;
    textView = paramTextView;
  }
  
  private static String getDecoderCountersBufferCountString(DecoderCounters paramDecoderCounters)
  {
    if (paramDecoderCounters == null) {
      return "";
    }
    paramDecoderCounters.ensureUpdated();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(" sib:");
    localStringBuilder.append(skippedInputBufferCount);
    localStringBuilder.append(" sb:");
    localStringBuilder.append(skippedOutputBufferCount);
    localStringBuilder.append(" rb:");
    localStringBuilder.append(renderedOutputBufferCount);
    localStringBuilder.append(" db:");
    localStringBuilder.append(droppedBufferCount);
    localStringBuilder.append(" mcdb:");
    localStringBuilder.append(maxConsecutiveDroppedBufferCount);
    localStringBuilder.append(" dk:");
    localStringBuilder.append(droppedToKeyframeCount);
    return localStringBuilder.toString();
  }
  
  private static String getPixelAspectRatioString(float paramFloat)
  {
    if ((paramFloat != -1.0F) && (paramFloat != 1.0F))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(" par:");
      localStringBuilder.append(String.format(Locale.US, "%.02f", new Object[] { Float.valueOf(paramFloat) }));
      return localStringBuilder.toString();
    }
    return "";
  }
  
  protected String getAudioString()
  {
    Format localFormat = player.getAudioFormat();
    if (localFormat == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\n");
    localStringBuilder.append(sampleMimeType);
    localStringBuilder.append("(id:");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(" hz:");
    localStringBuilder.append(sampleRate);
    localStringBuilder.append(" ch:");
    localStringBuilder.append(channelCount);
    localStringBuilder.append(getDecoderCountersBufferCountString(player.getAudioDecoderCounters()));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  protected String getDebugString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getPlayerStateString());
    localStringBuilder.append(getVideoString());
    localStringBuilder.append(getAudioString());
    return localStringBuilder.toString();
  }
  
  protected String getPlayerStateString()
  {
    String str;
    switch (player.getPlaybackState())
    {
    default: 
      str = "unknown";
      break;
    case 4: 
      str = "ended";
      break;
    case 3: 
      str = "ready";
      break;
    case 2: 
      str = "buffering";
      break;
    case 1: 
      str = "idle";
    }
    return String.format("playWhenReady:%s playbackState:%s window:%s", new Object[] { Boolean.valueOf(player.getPlayWhenReady()), str, Integer.valueOf(player.getCurrentWindowIndex()) });
  }
  
  protected String getVideoString()
  {
    Format localFormat = player.getVideoFormat();
    if (localFormat == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\n");
    localStringBuilder.append(sampleMimeType);
    localStringBuilder.append("(id:");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(" r:");
    localStringBuilder.append(width);
    localStringBuilder.append("x");
    localStringBuilder.append(height);
    localStringBuilder.append(getPixelAspectRatioString(pixelWidthHeightRatio));
    localStringBuilder.append(getDecoderCountersBufferCountString(player.getVideoDecoderCounters()));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public final void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    updateAndPost();
  }
  
  public final void onPositionDiscontinuity(int paramInt)
  {
    updateAndPost();
  }
  
  public final void run()
  {
    updateAndPost();
  }
  
  public final void start()
  {
    if (started) {
      return;
    }
    started = true;
    player.addListener(this);
    updateAndPost();
  }
  
  public final void stop()
  {
    if (!started) {
      return;
    }
    started = false;
    player.removeListener(this);
    textView.removeCallbacks(this);
  }
  
  protected final void updateAndPost()
  {
    textView.setText(getDebugString());
    textView.removeCallbacks(this);
    textView.postDelayed(this, 1000L);
  }
}
