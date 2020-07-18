package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;

@SafeParcelable.Class(creator="ResolveAccountResponseCreator")
public class ResolveAccountResponse
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ResolveAccountResponse> CREATOR = new DownloadProgressInfo.1();
  @SafeParcelable.Field(getter="getConnectionResult", id=3)
  private ConnectionResult zadi;
  @SafeParcelable.Field(getter="getSaveDefaultAccount", id=4)
  private boolean zagg;
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @SafeParcelable.Field(id=2)
  private IBinder zanx;
  @SafeParcelable.Field(getter="isFromCrossClientAuth", id=5)
  private boolean zapc;
  
  public ResolveAccountResponse(int paramInt)
  {
    this(new ConnectionResult(paramInt, null));
  }
  
  ResolveAccountResponse(int paramInt, IBinder paramIBinder, ConnectionResult paramConnectionResult, boolean paramBoolean1, boolean paramBoolean2)
  {
    zalf = paramInt;
    zanx = paramIBinder;
    zadi = paramConnectionResult;
    zagg = paramBoolean1;
    zapc = paramBoolean2;
  }
  
  public ResolveAccountResponse(ConnectionResult paramConnectionResult)
  {
    this(1, null, paramConnectionResult, false, false);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ResolveAccountResponse)) {
      return false;
    }
    paramObject = (ResolveAccountResponse)paramObject;
    return (zadi.equals(zadi)) && (getAccountAccessor().equals(paramObject.getAccountAccessor()));
  }
  
  public IAccountAccessor getAccountAccessor()
  {
    return IAccountAccessor.Stub.asInterface(zanx);
  }
  
  public ConnectionResult getConnectionResult()
  {
    return zadi;
  }
  
  public boolean getSaveDefaultAccount()
  {
    return zagg;
  }
  
  public boolean isFromCrossClientAuth()
  {
    return zapc;
  }
  
  public ResolveAccountResponse setAccountAccessor(IAccountAccessor paramIAccountAccessor)
  {
    if (paramIAccountAccessor == null) {
      paramIAccountAccessor = null;
    } else {
      paramIAccountAccessor = paramIAccountAccessor.asBinder();
    }
    zanx = paramIAccountAccessor;
    return this;
  }
  
  public ResolveAccountResponse setIsFromCrossClientAuth(boolean paramBoolean)
  {
    zapc = paramBoolean;
    return this;
  }
  
  public ResolveAccountResponse setSaveDefaultAccount(boolean paramBoolean)
  {
    zagg = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeIBinder(paramParcel, 2, zanx, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getConnectionResult(), paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 4, getSaveDefaultAccount());
    SafeParcelWriter.writeBoolean(paramParcel, 5, isFromCrossClientAuth());
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
