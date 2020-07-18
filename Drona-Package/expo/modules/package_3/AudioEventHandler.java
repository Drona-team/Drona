package expo.modules.package_3;

public abstract interface AudioEventHandler
{
  public abstract void handleAudioFocusGained();
  
  public abstract void handleAudioFocusInterruptionBegan();
  
  public abstract void onPause();
  
  public abstract void onResume();
  
  public abstract void pauseImmediately();
  
  public abstract boolean requiresAudioFocus();
  
  public abstract void updateVolumeMuteAndDuck();
}
