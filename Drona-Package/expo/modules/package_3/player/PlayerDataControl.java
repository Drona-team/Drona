package expo.modules.package_3.player;

import android.os.BaseBundle;
import android.os.Bundle;
import android.widget.MediaController.MediaPlayerControl;

public class PlayerDataControl
  implements MediaController.MediaPlayerControl
{
  private final PlayerData mPlayerData;
  
  public PlayerDataControl(PlayerData paramPlayerData)
  {
    mPlayerData = paramPlayerData;
  }
  
  public boolean canPause()
  {
    return true;
  }
  
  public boolean canSeekBackward()
  {
    return true;
  }
  
  public boolean canSeekForward()
  {
    return true;
  }
  
  public int getAudioSessionId()
  {
    return mPlayerData.getAudioSessionId();
  }
  
  public int getBufferPercentage()
  {
    Bundle localBundle = mPlayerData.getStatus();
    if ((localBundle.getBoolean("isLoaded")) && (localBundle.containsKey("durationMillis")) && (localBundle.containsKey("playableDurationMillis")))
    {
      double d = localBundle.getInt("durationMillis");
      return (int)(localBundle.getInt("playableDurationMillis") / d * 100.0D);
    }
    return 0;
  }
  
  public int getCurrentPosition()
  {
    Bundle localBundle = mPlayerData.getStatus();
    if (localBundle.getBoolean("isLoaded")) {
      return localBundle.getInt("positionMillis");
    }
    return 0;
  }
  
  public int getDuration()
  {
    Bundle localBundle = mPlayerData.getStatus();
    if ((localBundle.getBoolean("isLoaded")) && (localBundle.containsKey("durationMillis"))) {
      return localBundle.getInt("durationMillis");
    }
    return 0;
  }
  
  public boolean isFullscreen()
  {
    return mPlayerData.isPresentedFullscreen();
  }
  
  public boolean isPlaying()
  {
    Bundle localBundle = mPlayerData.getStatus();
    return (localBundle.getBoolean("isLoaded")) && (localBundle.getBoolean("isPlaying"));
  }
  
  public void pause()
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("shouldPlay", false);
    mPlayerData.setStatus(localBundle, null);
  }
  
  public void seekTo(int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putDouble("positionMillis", paramInt);
    mPlayerData.setStatus(localBundle, null);
  }
  
  public void start()
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("shouldPlay", true);
    mPlayerData.setStatus(localBundle, null);
  }
  
  public void toggleFullscreen()
  {
    mPlayerData.toggleFullscreen();
  }
}
