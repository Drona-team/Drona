package com.google.android.exoplayer2.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.Arrays;

@TargetApi(21)
public final class AudioCapabilities
{
  public static final AudioCapabilities DEFAULT_AUDIO_CAPABILITIES = new AudioCapabilities(new int[] { 2 }, 8);
  private static final int DEFAULT_MAX_CHANNEL_COUNT = 8;
  private final int maxChannelCount;
  private final int[] supportedEncodings;
  
  public AudioCapabilities(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt != null)
    {
      supportedEncodings = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
      Arrays.sort(supportedEncodings);
    }
    else
    {
      supportedEncodings = new int[0];
    }
    maxChannelCount = paramInt;
  }
  
  public static AudioCapabilities getCapabilities(Context paramContext)
  {
    return getCapabilities(paramContext.registerReceiver(null, new IntentFilter("android.media.action.HDMI_AUDIO_PLUG")));
  }
  
  static AudioCapabilities getCapabilities(Intent paramIntent)
  {
    if ((paramIntent != null) && (paramIntent.getIntExtra("android.media.extra.AUDIO_PLUG_STATE", 0) != 0)) {
      return new AudioCapabilities(paramIntent.getIntArrayExtra("android.media.extra.ENCODINGS"), paramIntent.getIntExtra("android.media.extra.MAX_CHANNEL_COUNT", 8));
    }
    return DEFAULT_AUDIO_CAPABILITIES;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AudioCapabilities)) {
      return false;
    }
    paramObject = (AudioCapabilities)paramObject;
    return (Arrays.equals(supportedEncodings, supportedEncodings)) && (maxChannelCount == maxChannelCount);
  }
  
  public int getMaxChannelCount()
  {
    return maxChannelCount;
  }
  
  public int hashCode()
  {
    return maxChannelCount + Arrays.hashCode(supportedEncodings) * 31;
  }
  
  public boolean supportsEncoding(int paramInt)
  {
    return Arrays.binarySearch(supportedEncodings, paramInt) >= 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AudioCapabilities[maxChannelCount=");
    localStringBuilder.append(maxChannelCount);
    localStringBuilder.append(", supportedEncodings=");
    localStringBuilder.append(Arrays.toString(supportedEncodings));
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}
