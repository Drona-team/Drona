package com.google.android.exoplayer2;

public final class RendererConfiguration
{
  public static final RendererConfiguration DEFAULT = new RendererConfiguration(0);
  public final int tunnelingAudioSessionId;
  
  public RendererConfiguration(int paramInt)
  {
    tunnelingAudioSessionId = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (RendererConfiguration)paramObject;
      return tunnelingAudioSessionId == tunnelingAudioSessionId;
    }
    return false;
  }
  
  public int hashCode()
  {
    return tunnelingAudioSessionId;
  }
}
