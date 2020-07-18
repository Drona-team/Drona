package com.google.android.exoplayer2.audio;

import androidx.annotation.Nullable;

public final class AudioAttributes
{
  public static final AudioAttributes DEFAULT = new Builder().build();
  @Nullable
  private android.media.AudioAttributes audioAttributesV21;
  public final int contentType;
  public final int flags;
  public final int usage;
  
  private AudioAttributes(int paramInt1, int paramInt2, int paramInt3)
  {
    contentType = paramInt1;
    flags = paramInt2;
    usage = paramInt3;
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
      paramObject = (AudioAttributes)paramObject;
      return (contentType == contentType) && (flags == flags) && (usage == usage);
    }
    return false;
  }
  
  public android.media.AudioAttributes getAudioAttributesV21()
  {
    if (audioAttributesV21 == null) {
      audioAttributesV21 = new android.media.AudioAttributes.Builder().setContentType(contentType).setFlags(flags).setUsage(usage).build();
    }
    return audioAttributesV21;
  }
  
  public int hashCode()
  {
    return ((527 + contentType) * 31 + flags) * 31 + usage;
  }
  
  public static final class Builder
  {
    private int contentType = 0;
    private int flags = 0;
    private int usage = 1;
    
    public Builder() {}
    
    public AudioAttributes build()
    {
      return new AudioAttributes(contentType, flags, usage, null);
    }
    
    public Builder setContentType(int paramInt)
    {
      contentType = paramInt;
      return this;
    }
    
    public Builder setFlags(int paramInt)
    {
      flags = paramInt;
      return this;
    }
    
    public Builder setUsage(int paramInt)
    {
      usage = paramInt;
      return this;
    }
  }
}
