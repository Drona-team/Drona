package androidx.media;

import android.os.BaseBundle;
import android.os.Bundle;
import java.util.Arrays;

class AudioAttributesImplBase
  implements AudioAttributesImpl
{
  int mContentType = 0;
  int mFlags = 0;
  int mLegacyStream = -1;
  int mUsage = 0;
  
  AudioAttributesImplBase() {}
  
  AudioAttributesImplBase(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mContentType = paramInt1;
    mFlags = paramInt2;
    mUsage = paramInt3;
    mLegacyStream = paramInt4;
  }
  
  public static AudioAttributesImpl fromBundle(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    int i = paramBundle.getInt("androidx.media.audio_attrs.USAGE", 0);
    return new AudioAttributesImplBase(paramBundle.getInt("androidx.media.audio_attrs.CONTENT_TYPE", 0), paramBundle.getInt("androidx.media.audio_attrs.FLAGS", 0), i, paramBundle.getInt("androidx.media.audio_attrs.LEGACY_STREAM_TYPE", -1));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AudioAttributesImplBase)) {
      return false;
    }
    paramObject = (AudioAttributesImplBase)paramObject;
    return (mContentType == paramObject.getContentType()) && (mFlags == paramObject.getFlags()) && (mUsage == paramObject.getUsage()) && (mLegacyStream == mLegacyStream);
  }
  
  public Object getAudioAttributes()
  {
    return null;
  }
  
  public int getContentType()
  {
    return mContentType;
  }
  
  public int getFlags()
  {
    int j = mFlags;
    int k = getLegacyStreamType();
    int i;
    if (k == 6)
    {
      i = j | 0x4;
    }
    else
    {
      i = j;
      if (k == 7) {
        i = j | 0x1;
      }
    }
    return i & 0x111;
  }
  
  public int getLegacyStreamType()
  {
    if (mLegacyStream != -1) {
      return mLegacyStream;
    }
    return AudioAttributesCompat.toVolumeStreamType(false, mFlags, mUsage);
  }
  
  public int getRawLegacyStreamType()
  {
    return mLegacyStream;
  }
  
  public int getUsage()
  {
    return mUsage;
  }
  
  public int getVolumeControlStream()
  {
    return AudioAttributesCompat.toVolumeStreamType(true, mFlags, mUsage);
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { Integer.valueOf(mContentType), Integer.valueOf(mFlags), Integer.valueOf(mUsage), Integer.valueOf(mLegacyStream) });
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("androidx.media.audio_attrs.USAGE", mUsage);
    localBundle.putInt("androidx.media.audio_attrs.CONTENT_TYPE", mContentType);
    localBundle.putInt("androidx.media.audio_attrs.FLAGS", mFlags);
    if (mLegacyStream != -1) {
      localBundle.putInt("androidx.media.audio_attrs.LEGACY_STREAM_TYPE", mLegacyStream);
    }
    return localBundle;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("AudioAttributesCompat:");
    if (mLegacyStream != -1)
    {
      localStringBuilder.append(" stream=");
      localStringBuilder.append(mLegacyStream);
      localStringBuilder.append(" derived");
    }
    localStringBuilder.append(" usage=");
    localStringBuilder.append(AudioAttributesCompat.usageToString(mUsage));
    localStringBuilder.append(" content=");
    localStringBuilder.append(mContentType);
    localStringBuilder.append(" flags=0x");
    localStringBuilder.append(Integer.toHexString(mFlags).toUpperCase());
    return localStringBuilder.toString();
  }
}
