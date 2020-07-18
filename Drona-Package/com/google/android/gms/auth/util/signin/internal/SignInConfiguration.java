package com.google.android.gms.auth.util.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;

@SafeParcelable.Class(creator="SignInConfigurationCreator")
@SafeParcelable.Reserved({1})
public final class SignInConfiguration
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.signin.internal.SignInConfiguration> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.Field(getter="getConsumerPkgName", id=2)
  private final String zzbr;
  @SafeParcelable.Field(getter="getGoogleConfig", id=5)
  private GoogleSignInOptions zzbs;
  
  public SignInConfiguration(String paramString, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzbr = Preconditions.checkNotEmpty(paramString);
    zzbs = paramGoogleSignInOptions;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SignInConfiguration)) {
      return false;
    }
    paramObject = (SignInConfiguration)paramObject;
    return (zzbr.equals(zzbr)) && (zzbs == null ? zzbs == null : zzbs.equals(zzbs));
  }
  
  public final GoogleSignInOptions getActiveAccount()
  {
    return zzbs;
  }
  
  public final int hashCode()
  {
    return new HashAccumulator().addObject(zzbr).addObject(zzbs).hash();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, zzbr, false);
    SafeParcelWriter.writeParcelable(paramParcel, 5, zzbs, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
