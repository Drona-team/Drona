package com.google.android.gms.common.server;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;

@KeepForSdk
@SafeParcelable.Class(creator="FavaDiagnosticsEntityCreator")
public class FavaDiagnosticsEntity
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  @KeepForSdk
  public static final Parcelable.Creator<FavaDiagnosticsEntity> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @SafeParcelable.Field(id=2)
  private final String zapj;
  @SafeParcelable.Field(id=3)
  private final int zapk;
  
  public FavaDiagnosticsEntity(int paramInt1, String paramString, int paramInt2)
  {
    zalf = paramInt1;
    zapj = paramString;
    zapk = paramInt2;
  }
  
  public FavaDiagnosticsEntity(String paramString, int paramInt)
  {
    zalf = 1;
    zapj = paramString;
    zapk = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeString(paramParcel, 2, zapj, false);
    SafeParcelWriter.writeInt(paramParcel, 3, zapk);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
