package com.google.android.gms.auth.util.credentials;

import com.google.android.gms.auth.util.Auth.AuthCredentialsOptions;
import com.google.android.gms.auth.util.Auth.AuthCredentialsOptions.Builder;

public final class CredentialsOptions
  extends Auth.AuthCredentialsOptions
{
  public static final CredentialsOptions DEFAULT = (CredentialsOptions)new Builder().fromUri();
  
  private CredentialsOptions(Builder paramBuilder)
  {
    super(paramBuilder);
  }
  
  public final class Builder
    extends Auth.AuthCredentialsOptions.Builder
  {
    public Builder() {}
    
    public final CredentialsOptions build()
    {
      return new CredentialsOptions(this, null);
    }
    
    public final Builder forceEnableSaveDialog()
    {
      mSortAscending = Boolean.valueOf(true);
      return this;
    }
  }
}
