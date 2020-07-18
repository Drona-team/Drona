package androidx.media;

import android.media.AudioAttributes;
import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.VersionedParcel;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
public final class AudioAttributesImplApi21Parcelizer
{
  public AudioAttributesImplApi21Parcelizer() {}
  
  public static AudioAttributesImplApi21 read(VersionedParcel paramVersionedParcel)
  {
    AudioAttributesImplApi21 localAudioAttributesImplApi21 = new AudioAttributesImplApi21();
    mAudioAttributes = ((AudioAttributes)paramVersionedParcel.readParcelable(mAudioAttributes, 1));
    mLegacyStreamType = paramVersionedParcel.readInt(mLegacyStreamType, 2);
    return localAudioAttributesImplApi21;
  }
  
  public static void write(AudioAttributesImplApi21 paramAudioAttributesImplApi21, VersionedParcel paramVersionedParcel)
  {
    paramVersionedParcel.setSerializationFlags(false, false);
    paramVersionedParcel.writeParcelable(mAudioAttributes, 1);
    paramVersionedParcel.writeInt(mLegacyStreamType, 2);
  }
}
