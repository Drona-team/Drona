package com.google.android.gms.auth.util.signin;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;

@SafeParcelable.Class(creator="SignInAccountCreator")
@SafeParcelable.Reserved({1})
public class SignInAccount
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.signin.SignInAccount> CREATOR = new VerticalProgressBar.SavedState.1();
  @Deprecated
  @SafeParcelable.Field(defaultValue="", id=4)
  private String zzba;
  @SafeParcelable.Field(getter="getGoogleSignInAccount", id=7)
  private GoogleSignInAccount zzbb;
  @Deprecated
  @SafeParcelable.Field(defaultValue="", id=8)
  private String zzbc;
  
  SignInAccount(String paramString1, GoogleSignInAccount paramGoogleSignInAccount, String paramString2)
  {
    zzbb = paramGoogleSignInAccount;
    zzba = Preconditions.checkNotEmpty(paramString1, "8.3 and 8.4 SDKs require non-null email");
    zzbc = Preconditions.checkNotEmpty(paramString2, "8.3 and 8.4 SDKs require non-null userId");
  }
  
  public final GoogleSignInAccount getGoogleSignInAccount()
  {
    return zzbb;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 4, zzba, false);
    SafeParcelWriter.writeParcelable(paramParcel, 7, zzbb, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 8, zzbc, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
