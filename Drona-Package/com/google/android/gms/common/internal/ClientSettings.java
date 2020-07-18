package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import androidx.collection.ArraySet;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.package_6.GoogleApiClient.Builder;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.signin.SignInOptions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@KeepForSdk
@VisibleForTesting
public final class ClientSettings
{
  public static final String KEY_CLIENT_SESSION_ID = "com.google.android.gms.common.internal.ClientSettings.sessionId";
  private final Account account;
  private final Set<com.google.android.gms.common.api.Scope> zabr;
  private final int zabt;
  private final View zabu;
  private final String zabv;
  private final String zabw;
  private final boolean zaby;
  private final Set<com.google.android.gms.common.api.Scope> zaob;
  private final Map<Api<?>, OptionalApiSettings> zaoc;
  private final SignInOptions zaod;
  private Integer zaoe;
  
  public ClientSettings(Account paramAccount, Set paramSet, Map paramMap, int paramInt, View paramView, String paramString1, String paramString2, SignInOptions paramSignInOptions)
  {
    this(paramAccount, paramSet, paramMap, paramInt, paramView, paramString1, paramString2, paramSignInOptions, false);
  }
  
  public ClientSettings(Account paramAccount, Set paramSet, Map paramMap, int paramInt, View paramView, String paramString1, String paramString2, SignInOptions paramSignInOptions, boolean paramBoolean)
  {
    account = paramAccount;
    if (paramSet == null) {
      paramAccount = Collections.EMPTY_SET;
    } else {
      paramAccount = Collections.unmodifiableSet(paramSet);
    }
    zabr = paramAccount;
    paramAccount = paramMap;
    if (paramMap == null) {
      paramAccount = Collections.EMPTY_MAP;
    }
    zaoc = paramAccount;
    zabu = paramView;
    zabt = paramInt;
    zabv = paramString1;
    zabw = paramString2;
    zaod = paramSignInOptions;
    zaby = paramBoolean;
    paramAccount = new HashSet(zabr);
    paramSet = zaoc.values().iterator();
    while (paramSet.hasNext()) {
      paramAccount.addAll(nextmScopes);
    }
    zaob = Collections.unmodifiableSet(paramAccount);
  }
  
  public static ClientSettings createDefault(Context paramContext)
  {
    return new GoogleApiClient.Builder(paramContext).buildClientSettings();
  }
  
  public final Account getAccount()
  {
    return account;
  }
  
  public final String getAccountName()
  {
    if (account != null) {
      return account.name;
    }
    return null;
  }
  
  public final Account getAccountOrDefault()
  {
    if (account != null) {
      return account;
    }
    return new Account("<<default account>>", "com.google");
  }
  
  public final Set getAllRequestedScopes()
  {
    return zaob;
  }
  
  public final Set getApplicableScopes(Sample paramSample)
  {
    paramSample = (OptionalApiSettings)zaoc.get(paramSample);
    if ((paramSample != null) && (!mScopes.isEmpty()))
    {
      HashSet localHashSet = new HashSet(zabr);
      localHashSet.addAll(mScopes);
      return localHashSet;
    }
    return zabr;
  }
  
  public final Integer getClientSessionId()
  {
    return zaoe;
  }
  
  public final int getGravityForPopups()
  {
    return zabt;
  }
  
  public final Map getOptionalApiSettings()
  {
    return zaoc;
  }
  
  public final String getRealClientClassName()
  {
    return zabw;
  }
  
  public final String getRealClientPackageName()
  {
    return zabv;
  }
  
  public final Set getRequiredScopes()
  {
    return zabr;
  }
  
  public final SignInOptions getSignInOptions()
  {
    return zaod;
  }
  
  public final View getViewForPopups()
  {
    return zabu;
  }
  
  public final boolean isSignInClientDisconnectFixEnabled()
  {
    return zaby;
  }
  
  public final void setClientSessionId(Integer paramInteger)
  {
    zaoe = paramInteger;
  }
  
  @KeepForSdk
  public static final class Builder
  {
    private Account mCurrentAccount;
    private int zabt = 0;
    private View zabu;
    private String zabv;
    private String zabw;
    private boolean zaby;
    private Map<Api<?>, ClientSettings.OptionalApiSettings> zaoc;
    private SignInOptions zaod = SignInOptions.DEFAULT;
    private ArraySet<com.google.android.gms.common.api.Scope> zaof;
    
    public Builder() {}
    
    public final Builder addAllRequiredScopes(Collection paramCollection)
    {
      if (zaof == null) {
        zaof = new ArraySet();
      }
      zaof.addAll(paramCollection);
      return this;
    }
    
    public final Builder addRequiredScope(com.google.android.gms.common.package_6.Scope paramScope)
    {
      if (zaof == null) {
        zaof = new ArraySet();
      }
      zaof.add(paramScope);
      return this;
    }
    
    public final ClientSettings build()
    {
      return new ClientSettings(mCurrentAccount, zaof, zaoc, zabt, zabu, zabv, zabw, zaod, zaby);
    }
    
    public final Builder enableSignInClientDisconnectFix()
    {
      zaby = true;
      return this;
    }
    
    public final Builder setAccount(Account paramAccount)
    {
      mCurrentAccount = paramAccount;
      return this;
    }
    
    public final Builder setGravityForPopups(int paramInt)
    {
      zabt = paramInt;
      return this;
    }
    
    public final Builder setOptionalApiSettingsMap(Map paramMap)
    {
      zaoc = paramMap;
      return this;
    }
    
    public final Builder setRealClientClassName(String paramString)
    {
      zabw = paramString;
      return this;
    }
    
    public final Builder setRealClientPackageName(String paramString)
    {
      zabv = paramString;
      return this;
    }
    
    public final Builder setSignInOptions(SignInOptions paramSignInOptions)
    {
      zaod = paramSignInOptions;
      return this;
    }
    
    public final Builder setViewForPopups(View paramView)
    {
      zabu = paramView;
      return this;
    }
  }
  
  public static final class OptionalApiSettings
  {
    public final Set<com.google.android.gms.common.api.Scope> mScopes;
    
    public OptionalApiSettings(Set paramSet)
    {
      Preconditions.checkNotNull(paramSet);
      mScopes = Collections.unmodifiableSet(paramSet);
    }
  }
}
