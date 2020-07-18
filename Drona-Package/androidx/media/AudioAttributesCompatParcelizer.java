package androidx.media;

import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.VersionedParcel;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
public final class AudioAttributesCompatParcelizer
{
  public AudioAttributesCompatParcelizer() {}
  
  public static AudioAttributesCompat read(VersionedParcel paramVersionedParcel)
  {
    AudioAttributesCompat localAudioAttributesCompat = new AudioAttributesCompat();
    mImpl = ((AudioAttributesImpl)paramVersionedParcel.readVersionedParcelable(mImpl, 1));
    return localAudioAttributesCompat;
  }
  
  public static void write(AudioAttributesCompat paramAudioAttributesCompat, VersionedParcel paramVersionedParcel)
  {
    paramVersionedParcel.setSerializationFlags(false, false);
    paramVersionedParcel.writeVersionedParcelable(mImpl, 1);
  }
}
