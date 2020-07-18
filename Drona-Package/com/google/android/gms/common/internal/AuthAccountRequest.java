package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.package_6.Scope;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SafeParcelable.Class(creator="AuthAccountRequestCreator")
public class AuthAccountRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<AuthAccountRequest> CREATOR = new AddressAndLabel.1();
  @SafeParcelable.Field(id=6, type="android.accounts.Account")
  private Account mItemType;
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @Deprecated
  @SafeParcelable.Field(id=2)
  private final IBinder zanx;
  @SafeParcelable.Field(id=3)
  private final Scope[] zany;
  @SafeParcelable.Field(id=4)
  private Integer zanz;
  @SafeParcelable.Field(id=5)
  private Integer zaoa;
  
  AuthAccountRequest(int paramInt, IBinder paramIBinder, Scope[] paramArrayOfScope, Integer paramInteger1, Integer paramInteger2, Account paramAccount)
  {
    zalf = paramInt;
    zanx = paramIBinder;
    zany = paramArrayOfScope;
    zanz = paramInteger1;
    zaoa = paramInteger2;
    mItemType = paramAccount;
  }
  
  public AuthAccountRequest(Account paramAccount, Set paramSet)
  {
    this(3, null, (Scope[])paramSet.toArray(new Scope[paramSet.size()]), null, null, (Account)Preconditions.checkNotNull(paramAccount));
  }
  
  public AuthAccountRequest(IAccountAccessor paramIAccountAccessor, Set paramSet)
  {
    this(3, paramIAccountAccessor.asBinder(), (Scope[])paramSet.toArray(new Scope[paramSet.size()]), null, null, null);
  }
  
  public Account getAccount()
  {
    if (mItemType != null) {
      return mItemType;
    }
    if (zanx != null) {
      return AccountAccessor.getAccountBinderSafe(IAccountAccessor.Stub.asInterface(zanx));
    }
    return null;
  }
  
  public Integer getOauthPolicy()
  {
    return zanz;
  }
  
  public Integer getPolicyAction()
  {
    return zaoa;
  }
  
  public Set getScopes()
  {
    return new HashSet(Arrays.asList(zany));
  }
  
  public AuthAccountRequest setOauthPolicy(Integer paramInteger)
  {
    zanz = paramInteger;
    return this;
  }
  
  public AuthAccountRequest setPolicyAction(Integer paramInteger)
  {
    zaoa = paramInteger;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeIBinder(paramParcel, 2, zanx, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 3, zany, paramInt, false);
    SafeParcelWriter.writeIntegerObject(paramParcel, 4, zanz, false);
    SafeParcelWriter.writeIntegerObject(paramParcel, 5, zaoa, false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, mItemType, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
