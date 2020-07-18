package androidx.media;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TargetApi(21)
class AudioAttributesImplApi21
  implements AudioAttributesImpl
{
  private static final String PAGE_KEY = "AudioAttributesCompat21";
  static Method sAudioAttributesToLegacyStreamType;
  AudioAttributes mAudioAttributes;
  int mLegacyStreamType = -1;
  
  AudioAttributesImplApi21() {}
  
  AudioAttributesImplApi21(AudioAttributes paramAudioAttributes)
  {
    this(paramAudioAttributes, -1);
  }
  
  AudioAttributesImplApi21(AudioAttributes paramAudioAttributes, int paramInt)
  {
    mAudioAttributes = paramAudioAttributes;
    mLegacyStreamType = paramInt;
  }
  
  public static AudioAttributesImpl fromBundle(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    AudioAttributes localAudioAttributes = (AudioAttributes)paramBundle.getParcelable("androidx.media.audio_attrs.FRAMEWORKS");
    if (localAudioAttributes == null) {
      return null;
    }
    return new AudioAttributesImplApi21(localAudioAttributes, paramBundle.getInt("androidx.media.audio_attrs.LEGACY_STREAM_TYPE", -1));
  }
  
  static Method getAudioAttributesToLegacyStreamTypeMethod()
  {
    if (sAudioAttributesToLegacyStreamType == null) {}
    try
    {
      Method localMethod = AudioAttributes.class.getMethod("toLegacyStreamType", new Class[] { AudioAttributes.class });
      sAudioAttributesToLegacyStreamType = localMethod;
      return sAudioAttributesToLegacyStreamType;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;) {}
    }
    return null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AudioAttributesImplApi21)) {
      return false;
    }
    paramObject = (AudioAttributesImplApi21)paramObject;
    return mAudioAttributes.equals(mAudioAttributes);
  }
  
  public Object getAudioAttributes()
  {
    return mAudioAttributes;
  }
  
  public int getContentType()
  {
    return mAudioAttributes.getContentType();
  }
  
  public int getFlags()
  {
    return mAudioAttributes.getFlags();
  }
  
  public int getLegacyStreamType()
  {
    if (mLegacyStreamType != -1) {
      return mLegacyStreamType;
    }
    Object localObject1 = getAudioAttributesToLegacyStreamTypeMethod();
    if (localObject1 == null)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("No AudioAttributes#toLegacyStreamType() on API: ");
      ((StringBuilder)localObject1).append(Build.VERSION.SDK_INT);
      Log.w("AudioAttributesCompat21", ((StringBuilder)localObject1).toString());
      return -1;
    }
    Object localObject2 = mAudioAttributes;
    try
    {
      localObject1 = ((Method)localObject1).invoke(null, new Object[] { localObject2 });
      localObject1 = (Integer)localObject1;
      int i = ((Integer)localObject1).intValue();
      return i;
    }
    catch (InvocationTargetException|IllegalAccessException localInvocationTargetException)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("getLegacyStreamType() failed on API: ");
      ((StringBuilder)localObject2).append(Build.VERSION.SDK_INT);
      Log.w("AudioAttributesCompat21", ((StringBuilder)localObject2).toString(), localInvocationTargetException);
    }
    return -1;
  }
  
  public int getRawLegacyStreamType()
  {
    return mLegacyStreamType;
  }
  
  public int getUsage()
  {
    return mAudioAttributes.getUsage();
  }
  
  public int getVolumeControlStream()
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return mAudioAttributes.getVolumeControlStream();
    }
    return AudioAttributesCompat.toVolumeStreamType(true, getFlags(), getUsage());
  }
  
  public int hashCode()
  {
    return mAudioAttributes.hashCode();
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("androidx.media.audio_attrs.FRAMEWORKS", mAudioAttributes);
    if (mLegacyStreamType != -1) {
      localBundle.putInt("androidx.media.audio_attrs.LEGACY_STREAM_TYPE", mLegacyStreamType);
    }
    return localBundle;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AudioAttributesCompat: audioattributes=");
    localStringBuilder.append(mAudioAttributes);
    return localStringBuilder.toString();
  }
}
