package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.VersionedParcel;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
public class IconCompatParcelizer
{
  public IconCompatParcelizer() {}
  
  public static IconCompat read(VersionedParcel paramVersionedParcel)
  {
    IconCompat localIconCompat = new IconCompat();
    mType = paramVersionedParcel.readInt(mType, 1);
    mData = paramVersionedParcel.readByteArray(mData, 2);
    mParcelable = paramVersionedParcel.readParcelable(mParcelable, 3);
    mInt1 = paramVersionedParcel.readInt(mInt1, 4);
    mInt2 = paramVersionedParcel.readInt(mInt2, 5);
    mTintList = ((ColorStateList)paramVersionedParcel.readParcelable(mTintList, 6));
    mTintModeStr = paramVersionedParcel.readString(mTintModeStr, 7);
    localIconCompat.onPostParceling();
    return localIconCompat;
  }
  
  public static void write(IconCompat paramIconCompat, VersionedParcel paramVersionedParcel)
  {
    paramVersionedParcel.setSerializationFlags(true, true);
    paramIconCompat.onPreParceling(paramVersionedParcel.isStream());
    if (-1 != mType) {
      paramVersionedParcel.writeInt(mType, 1);
    }
    if (mData != null) {
      paramVersionedParcel.writeByteArray(mData, 2);
    }
    if (mParcelable != null) {
      paramVersionedParcel.writeParcelable(mParcelable, 3);
    }
    if (mInt1 != 0) {
      paramVersionedParcel.writeInt(mInt1, 4);
    }
    if (mInt2 != 0) {
      paramVersionedParcel.writeInt(mInt2, 5);
    }
    if (mTintList != null) {
      paramVersionedParcel.writeParcelable(mTintList, 6);
    }
    if (mTintModeStr != null) {
      paramVersionedParcel.writeString(mTintModeStr, 7);
    }
  }
}
