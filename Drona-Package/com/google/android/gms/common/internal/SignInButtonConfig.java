package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.package_6.Scope;

@SafeParcelable.Class(creator="SignInButtonConfigCreator")
public class SignInButtonConfig
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<SignInButtonConfig> CREATOR = new PaymentIntent.Output.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @Deprecated
  @SafeParcelable.Field(getter="getScopes", id=4)
  private final Scope[] zany;
  @SafeParcelable.Field(getter="getButtonSize", id=2)
  private final int zapd;
  @SafeParcelable.Field(getter="getColorScheme", id=3)
  private final int zape;
  
  SignInButtonConfig(int paramInt1, int paramInt2, int paramInt3, Scope[] paramArrayOfScope)
  {
    zalf = paramInt1;
    zapd = paramInt2;
    zape = paramInt3;
    zany = paramArrayOfScope;
  }
  
  public SignInButtonConfig(int paramInt1, int paramInt2, Scope[] paramArrayOfScope)
  {
    this(1, paramInt1, paramInt2, null);
  }
  
  public int getButtonSize()
  {
    return zapd;
  }
  
  public int getColorScheme()
  {
    return zape;
  }
  
  public Scope[] getScopes()
  {
    return zany;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeInt(paramParcel, 2, getButtonSize());
    SafeParcelWriter.writeInt(paramParcel, 3, getColorScheme());
    SafeParcelWriter.writeTypedArray(paramParcel, 4, getScopes(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
