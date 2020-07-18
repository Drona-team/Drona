package com.facebook.react.modules.sound;

import android.media.AudioManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="SoundManager")
public class SoundManagerModule
  extends ReactContextBaseJavaModule
{
  public static final String NAME = "SoundManager";
  
  public SoundManagerModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public String getName()
  {
    return "SoundManager";
  }
  
  public void playTouchSound()
  {
    AudioManager localAudioManager = (AudioManager)getReactApplicationContext().getSystemService("audio");
    if (localAudioManager != null) {
      localAudioManager.playSoundEffect(0);
    }
  }
}
