package com.google.android.gms.auth.util.credentials;

import android.accounts.Account;
import com.google.android.gms.common.internal.Preconditions;

public final class IdentityProviders
{
  public static final String FACEBOOK = "https://www.facebook.com";
  public static final String GOOGLE = "https://accounts.google.com";
  public static final String LINKEDIN = "https://www.linkedin.com";
  public static final String MICROSOFT = "https://login.live.com";
  public static final String PAYPAL = "https://www.paypal.com";
  public static final String TWITTER = "https://twitter.com";
  public static final String YAHOO = "https://login.yahoo.com";
  
  private IdentityProviders() {}
  
  public static final String getIdentityProviderForAccount(Account paramAccount)
  {
    Preconditions.checkNotNull(paramAccount, "account cannot be null");
    if ("com.google".equals(type)) {
      return "https://accounts.google.com";
    }
    if ("com.facebook.auth.login".equals(type)) {
      return "https://www.facebook.com";
    }
    return null;
  }
}
