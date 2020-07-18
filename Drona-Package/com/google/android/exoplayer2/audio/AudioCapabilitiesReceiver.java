package com.google.android.exoplayer2.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

public final class AudioCapabilitiesReceiver
{
  @Nullable
  AudioCapabilities audioCapabilities;
  private final Context context;
  @Nullable
  private final Handler handler;
  private final Listener listener;
  @Nullable
  private final BroadcastReceiver receiver;
  
  public AudioCapabilitiesReceiver(Context paramContext, Handler paramHandler, Listener paramListener)
  {
    context = ((Context)Assertions.checkNotNull(paramContext));
    handler = paramHandler;
    listener = ((Listener)Assertions.checkNotNull(paramListener));
    if (Util.SDK_INT >= 21) {
      paramContext = new HdmiAudioPlugBroadcastReceiver(null);
    } else {
      paramContext = null;
    }
    receiver = paramContext;
  }
  
  public AudioCapabilitiesReceiver(Context paramContext, Listener paramListener)
  {
    this(paramContext, null, paramListener);
  }
  
  public AudioCapabilities register()
  {
    BroadcastReceiver localBroadcastReceiver = receiver;
    Object localObject = null;
    if (localBroadcastReceiver != null)
    {
      localObject = new IntentFilter("android.media.action.HDMI_AUDIO_PLUG");
      if (handler != null) {
        localObject = context.registerReceiver(receiver, (IntentFilter)localObject, null, handler);
      } else {
        localObject = context.registerReceiver(receiver, (IntentFilter)localObject);
      }
    }
    audioCapabilities = AudioCapabilities.getCapabilities((Intent)localObject);
    return audioCapabilities;
  }
  
  public void unregister()
  {
    if (receiver != null) {
      context.unregisterReceiver(receiver);
    }
  }
  
  private final class HdmiAudioPlugBroadcastReceiver
    extends BroadcastReceiver
  {
    private HdmiAudioPlugBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!isInitialStickyBroadcast())
      {
        paramContext = AudioCapabilities.getCapabilities(paramIntent);
        if (!paramContext.equals(audioCapabilities))
        {
          audioCapabilities = paramContext;
          listener.onAudioCapabilitiesChanged(paramContext);
        }
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onAudioCapabilitiesChanged(AudioCapabilities paramAudioCapabilities);
  }
}
