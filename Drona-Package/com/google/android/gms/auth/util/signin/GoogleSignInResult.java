package com.google.android.gms.auth.util.signin;

import com.google.android.gms.common.package_6.Result;
import com.google.android.gms.common.package_6.Status;

public class GoogleSignInResult
  implements Result
{
  private Status mStatus;
  private GoogleSignInAccount zzaz;
  
  public GoogleSignInResult(GoogleSignInAccount paramGoogleSignInAccount, Status paramStatus)
  {
    zzaz = paramGoogleSignInAccount;
    mStatus = paramStatus;
  }
  
  public GoogleSignInAccount getSignInAccount()
  {
    return zzaz;
  }
  
  public Status getStatus()
  {
    return mStatus;
  }
  
  public boolean isSuccess()
  {
    return mStatus.isSuccess();
  }
}
