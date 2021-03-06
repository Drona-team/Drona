package com.google.android.exoplayer2.audio;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioFocusRequest.Builder;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class AudioFocusManager
{
  private static final int AUDIO_FOCUS_STATE_HAVE_FOCUS = 1;
  private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT = 2;
  private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT_DUCK = 3;
  private static final int AUDIO_FOCUS_STATE_LOST_FOCUS = -1;
  private static final int AUDIO_FOCUS_STATE_NO_FOCUS = 0;
  private static final String PAGE_KEY = "AudioFocusManager";
  public static final int PLAYER_COMMAND_DO_NOT_PLAY = -1;
  public static final int PLAYER_COMMAND_PLAY_WHEN_READY = 1;
  public static final int PLAYER_COMMAND_WAIT_FOR_CALLBACK = 0;
  private static final float VOLUME_MULTIPLIER_DEFAULT = 1.0F;
  private static final float VOLUME_MULTIPLIER_DUCK = 0.2F;
  @Nullable
  private AudioAttributes audioAttributes;
  private AudioFocusRequest audioFocusRequest;
  private int audioFocusState;
  @Nullable
  private final AudioManager audioManager;
  private int focusGain;
  private final AudioFocusListener focusListener;
  private final PlayerControl playerControl;
  private boolean rebuildAudioFocusRequest;
  private float volumeMultiplier = 1.0F;
  
  public AudioFocusManager(Context paramContext, PlayerControl paramPlayerControl)
  {
    if (paramContext == null) {
      paramContext = null;
    } else {
      paramContext = (AudioManager)paramContext.getApplicationContext().getSystemService("audio");
    }
    audioManager = paramContext;
    playerControl = paramPlayerControl;
    focusListener = new AudioFocusListener(null);
    audioFocusState = 0;
  }
  
  private void abandonAudioFocus()
  {
    abandonAudioFocus(false);
  }
  
  private void abandonAudioFocus(boolean paramBoolean)
  {
    if ((focusGain == 0) && (audioFocusState == 0)) {
      return;
    }
    if ((focusGain != 1) || (audioFocusState == -1) || (paramBoolean))
    {
      if (Util.SDK_INT >= 26) {
        abandonAudioFocusV26();
      } else {
        abandonAudioFocusDefault();
      }
      audioFocusState = 0;
    }
  }
  
  private void abandonAudioFocusDefault()
  {
    ((AudioManager)Assertions.checkNotNull(audioManager)).abandonAudioFocus(focusListener);
  }
  
  private void abandonAudioFocusV26()
  {
    if (audioFocusRequest != null) {
      ((AudioManager)Assertions.checkNotNull(audioManager)).abandonAudioFocusRequest(audioFocusRequest);
    }
  }
  
  private static int convertAudioAttributesToFocusGain(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes == null) {
      return 0;
    }
    switch (usage)
    {
    default: 
      break;
    case 15: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unidentified audio usage: ");
      localStringBuilder.append(usage);
      Log.w("AudioFocusManager", localStringBuilder.toString());
      return 0;
    case 16: 
      if (Util.SDK_INT >= 19) {
        return 4;
      }
      return 2;
    case 11: 
      if (contentType == 1) {
        return 2;
      }
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 12: 
    case 13: 
      return 3;
    case 3: 
      return 0;
    case 2: 
    case 4: 
      return 2;
    case 1: 
    case 14: 
      return 1;
    }
    Log.w("AudioFocusManager", "Specify a proper usage in the audio attributes for audio focus handling. Using AUDIOFOCUS_GAIN by default.");
    return 1;
  }
  
  private int handleIdle(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 1;
    }
    return -1;
  }
  
  private int requestAudioFocus()
  {
    if (focusGain == 0)
    {
      if (audioFocusState != 0)
      {
        abandonAudioFocus(true);
        return 1;
      }
    }
    else
    {
      if (audioFocusState == 0)
      {
        int i;
        if (Util.SDK_INT >= 26) {
          i = requestAudioFocusV26();
        } else {
          i = requestAudioFocusDefault();
        }
        if (i == 1) {
          i = 1;
        } else {
          i = 0;
        }
        audioFocusState = i;
      }
      if (audioFocusState == 0) {
        return -1;
      }
      if (audioFocusState == 2) {
        return 0;
      }
    }
    return 1;
  }
  
  private int requestAudioFocusDefault()
  {
    return ((AudioManager)Assertions.checkNotNull(audioManager)).requestAudioFocus(focusListener, Util.getStreamTypeForAudioUsage(checkNotNullaudioAttributes)).usage), focusGain);
  }
  
  private int requestAudioFocusV26()
  {
    if ((audioFocusRequest == null) || (rebuildAudioFocusRequest))
    {
      AudioFocusRequest.Builder localBuilder;
      if (audioFocusRequest == null) {
        localBuilder = new AudioFocusRequest.Builder(focusGain);
      } else {
        localBuilder = new AudioFocusRequest.Builder(audioFocusRequest);
      }
      boolean bool = willPauseWhenDucked();
      audioFocusRequest = localBuilder.setAudioAttributes(((AudioAttributes)Assertions.checkNotNull(audioAttributes)).getAudioAttributesV21()).setWillPauseWhenDucked(bool).setOnAudioFocusChangeListener(focusListener).build();
      rebuildAudioFocusRequest = false;
    }
    return ((AudioManager)Assertions.checkNotNull(audioManager)).requestAudioFocus(audioFocusRequest);
  }
  
  private boolean willPauseWhenDucked()
  {
    return (audioAttributes != null) && (audioAttributes.contentType == 1);
  }
  
  public float getVolumeMultiplier()
  {
    return volumeMultiplier;
  }
  
  public int handlePrepare(boolean paramBoolean)
  {
    if (audioManager == null) {
      return 1;
    }
    if (paramBoolean) {
      return requestAudioFocus();
    }
    return -1;
  }
  
  public int handleSetPlayWhenReady(boolean paramBoolean, int paramInt)
  {
    if (audioManager == null) {
      return 1;
    }
    if (!paramBoolean)
    {
      abandonAudioFocus();
      return -1;
    }
    if (paramInt == 1) {
      return handleIdle(paramBoolean);
    }
    return requestAudioFocus();
  }
  
  public void handleStop()
  {
    if (audioManager == null) {
      return;
    }
    abandonAudioFocus(true);
  }
  
  public int setAudioAttributes(AudioAttributes paramAudioAttributes, boolean paramBoolean, int paramInt)
  {
    if ((audioAttributes == null) && (paramAudioAttributes == null))
    {
      if (paramBoolean) {
        return 1;
      }
      return -1;
    }
    Assertions.checkNotNull(audioManager, "SimpleExoPlayer must be created with a context to handle audio focus.");
    if (!Util.areEqual(audioAttributes, paramAudioAttributes))
    {
      audioAttributes = paramAudioAttributes;
      focusGain = convertAudioAttributesToFocusGain(paramAudioAttributes);
      boolean bool;
      if ((focusGain != 1) && (focusGain != 0)) {
        bool = false;
      } else {
        bool = true;
      }
      Assertions.checkArgument(bool, "Automatic handling of audio focus is only available for USAGE_MEDIA and USAGE_GAME.");
      if ((paramBoolean) && ((paramInt == 2) || (paramInt == 3))) {
        return requestAudioFocus();
      }
    }
    if (paramInt == 1) {
      return handleIdle(paramBoolean);
    }
    return handlePrepare(paramBoolean);
  }
  
  private class AudioFocusListener
    implements AudioManager.OnAudioFocusChangeListener
  {
    private AudioFocusListener() {}
    
    public void onAudioFocusChange(int paramInt)
    {
      StringBuilder localStringBuilder;
      if (paramInt != 1) {
        switch (paramInt)
        {
        default: 
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Unknown focus change type: ");
          localStringBuilder.append(paramInt);
          Log.w("AudioFocusManager", localStringBuilder.toString());
          return;
        case -1: 
          AudioFocusManager.access$102(AudioFocusManager.this, -1);
          break;
        case -2: 
          AudioFocusManager.access$102(AudioFocusManager.this, 2);
          break;
        case -3: 
          if (AudioFocusManager.this.willPauseWhenDucked()) {
            AudioFocusManager.access$102(AudioFocusManager.this, 2);
          } else {
            AudioFocusManager.access$102(AudioFocusManager.this, 3);
          }
          break;
        }
      } else {
        AudioFocusManager.access$102(AudioFocusManager.this, 1);
      }
      switch (audioFocusState)
      {
      default: 
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unknown audio focus state: ");
        localStringBuilder.append(audioFocusState);
        throw new IllegalStateException(localStringBuilder.toString());
      case 2: 
        playerControl.executePlayerCommand(0);
        break;
      case 1: 
        playerControl.executePlayerCommand(1);
        break;
      case -1: 
        playerControl.executePlayerCommand(-1);
        AudioFocusManager.this.abandonAudioFocus(true);
      }
      float f;
      if (audioFocusState == 3) {
        f = 0.2F;
      } else {
        f = 1.0F;
      }
      if (volumeMultiplier != f)
      {
        AudioFocusManager.access$502(AudioFocusManager.this, f);
        playerControl.setVolumeMultiplier(f);
      }
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface PlayerCommand {}
  
  public static abstract interface PlayerControl
  {
    public abstract void executePlayerCommand(int paramInt);
    
    public abstract void setVolumeMultiplier(float paramFloat);
  }
}
