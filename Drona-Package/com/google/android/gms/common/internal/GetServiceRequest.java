package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.package_6.Scope;

@KeepForSdk
@SafeParcelable.Class(creator="GetServiceRequestCreator")
@SafeParcelable.Reserved({9})
public class GetServiceRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<GetServiceRequest> CREATOR = new AccountMirakel.2();
  @SafeParcelable.Field(id=4)
  String dataDir;
  @SafeParcelable.VersionField(id=1)
  private final int version;
  @SafeParcelable.Field(id=2)
  private final int zzdg;
  @SafeParcelable.Field(id=3)
  private int zzdh;
  @SafeParcelable.Field(id=5)
  IBinder zzdi;
  @SafeParcelable.Field(id=6)
  Scope[] zzdj;
  @SafeParcelable.Field(id=7)
  Bundle zzdk;
  @SafeParcelable.Field(id=8)
  Account zzdl;
  @SafeParcelable.Field(id=10)
  Feature[] zzdm;
  @SafeParcelable.Field(id=11)
  Feature[] zzdn;
  @SafeParcelable.Field(id=12)
  private boolean zzdo;
  
  public GetServiceRequest(int paramInt)
  {
    version = 4;
    zzdh = GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    zzdg = paramInt;
    zzdo = true;
  }
  
  GetServiceRequest(int paramInt1, int paramInt2, int paramInt3, String paramString, IBinder paramIBinder, Scope[] paramArrayOfScope, Bundle paramBundle, Account paramAccount, Feature[] paramArrayOfFeature1, Feature[] paramArrayOfFeature2, boolean paramBoolean)
  {
    version = paramInt1;
    zzdg = paramInt2;
    zzdh = paramInt3;
    if ("com.google.android.gms".equals(paramString)) {
      dataDir = "com.google.android.gms";
    } else {
      dataDir = paramString;
    }
    if (paramInt1 < 2)
    {
      paramString = null;
      if (paramIBinder != null) {
        paramString = AccountAccessor.getAccountBinderSafe(IAccountAccessor.Stub.asInterface(paramIBinder));
      }
      zzdl = paramString;
    }
    else
    {
      zzdi = paramIBinder;
      zzdl = paramAccount;
    }
    zzdj = paramArrayOfScope;
    zzdk = paramBundle;
    zzdm = paramArrayOfFeature1;
    zzdn = paramArrayOfFeature2;
    zzdo = paramBoolean;
  }
  
  public Bundle getExtraArgs()
  {
    return zzdk;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, version);
    SafeParcelWriter.writeInt(paramParcel, 2, zzdg);
    SafeParcelWriter.writeInt(paramParcel, 3, zzdh);
    SafeParcelWriter.writeString(paramParcel, 4, dataDir, false);
    SafeParcelWriter.writeIBinder(paramParcel, 5, zzdi, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 6, zzdj, paramInt, false);
    SafeParcelWriter.writeBundle(paramParcel, 7, zzdk, false);
    SafeParcelWriter.writeParcelable(paramParcel, 8, zzdl, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 10, zzdm, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 11, zzdn, paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 12, zzdo);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
