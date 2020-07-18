package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;

@SafeParcelable.Class(creator="ResolveAccountRequestCreator")
public class ResolveAccountRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ResolveAccountRequest> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.Field(getter="getAccount", id=2)
  private final Account mAccount;
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @SafeParcelable.Field(getter="getSessionId", id=3)
  private final int zapa;
  @SafeParcelable.Field(getter="getSignInAccountHint", id=4)
  private final GoogleSignInAccount zapb;
  
  ResolveAccountRequest(int paramInt1, Account paramAccount, int paramInt2, GoogleSignInAccount paramGoogleSignInAccount)
  {
    zalf = paramInt1;
    mAccount = paramAccount;
    zapa = paramInt2;
    zapb = paramGoogleSignInAccount;
  }
  
  public ResolveAccountRequest(Account paramAccount, int paramInt, GoogleSignInAccount paramGoogleSignInAccount)
  {
    this(2, paramAccount, paramInt, paramGoogleSignInAccount);
  }
  
  public Account getAccount()
  {
    return mAccount;
  }
  
  public int getSessionId()
  {
    return zapa;
  }
  
  public GoogleSignInAccount getSignInAccountHint()
  {
    return zapb;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getAccount(), paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, getSessionId());
    SafeParcelWriter.writeParcelable(paramParcel, 4, getSignInAccountHint(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
