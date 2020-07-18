package androidx.core.opml;

import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;

@Deprecated
public final class ParcelableCompat
{
  private ParcelableCompat() {}
  
  public static Parcelable.Creator newCreator(ParcelableCompatCreatorCallbacks paramParcelableCompatCreatorCallbacks)
  {
    return new ParcelableCompatCreatorHoneycombMR2();
  }
  
  class ParcelableCompatCreatorHoneycombMR2<T>
    implements Parcelable.ClassLoaderCreator<T>
  {
    ParcelableCompatCreatorHoneycombMR2() {}
    
    public Object createFromParcel(Parcel paramParcel)
    {
      return ParcelableCompat.this.createFromParcel(paramParcel, null);
    }
    
    public Object createFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      return ParcelableCompat.this.createFromParcel(paramParcel, paramClassLoader);
    }
    
    public Object[] newArray(int paramInt)
    {
      return ParcelableCompat.this.newArray(paramInt);
    }
  }
}
