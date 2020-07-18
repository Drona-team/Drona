package com.google.android.gms.package_8.internal.overlay;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.ads.internal.overlay.zzc;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import com.google.android.gms.internal.ads.zzard;

@zzard
@SafeParcelable.Class(creator="AdLauncherIntentInfoCreator")
@SafeParcelable.Reserved({1})
public final class Attachment
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzc> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.Field(id=9)
  public final Intent intent;
  @SafeParcelable.Field(id=4)
  public final String mimeType;
  @SafeParcelable.Field(id=5)
  public final String packageName;
  @SafeParcelable.Field(id=3)
  public final String uri;
  @SafeParcelable.Field(id=2)
  private final String zzdjg;
  @SafeParcelable.Field(id=6)
  public final String zzdjh;
  @SafeParcelable.Field(id=7)
  public final String zzdji;
  @SafeParcelable.Field(id=8)
  private final String zzdjj;
  
  public Attachment(Intent paramIntent)
  {
    this(null, null, null, null, null, null, null, paramIntent);
  }
  
  public Attachment(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    this(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, null);
  }
  
  public Attachment(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, Intent paramIntent)
  {
    zzdjg = paramString1;
    uri = paramString2;
    mimeType = paramString3;
    packageName = paramString4;
    zzdjh = paramString5;
    zzdji = paramString6;
    zzdjj = paramString7;
    intent = paramIntent;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, zzdjg, false);
    SafeParcelWriter.writeString(paramParcel, 3, uri, false);
    SafeParcelWriter.writeString(paramParcel, 4, mimeType, false);
    SafeParcelWriter.writeString(paramParcel, 5, packageName, false);
    SafeParcelWriter.writeString(paramParcel, 6, zzdjh, false);
    SafeParcelWriter.writeString(paramParcel, 7, zzdji, false);
    SafeParcelWriter.writeString(paramParcel, 8, zzdjj, false);
    SafeParcelWriter.writeParcelable(paramParcel, 9, intent, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}