package com.google.android.gms.auth.util.credentials;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SafeParcelable.Class(creator="CredentialRequestCreator")
public final class CredentialRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.credentials.CredentialRequest> CREATOR = new PaymentIntent.Output.1();
  @SafeParcelable.Field(getter="isPasswordLoginSupported", id=1)
  private final boolean keepUpdated;
  @SafeParcelable.Field(id=1000)
  private final int txPower;
  @SafeParcelable.Field(getter="getAccountTypes", id=2)
  private final String[] zzaa;
  @SafeParcelable.Field(getter="getCredentialPickerConfig", id=3)
  private final CredentialPickerConfig zzab;
  @SafeParcelable.Field(getter="getCredentialHintPickerConfig", id=4)
  private final CredentialPickerConfig zzac;
  @SafeParcelable.Field(getter="isIdTokenRequested", id=5)
  private final boolean zzad;
  @SafeParcelable.Field(getter="getServerClientId", id=6)
  private final String zzae;
  @SafeParcelable.Field(getter="getIdTokenNonce", id=7)
  private final String zzaf;
  @SafeParcelable.Field(getter="getRequireUserMediation", id=8)
  private final boolean zzag;
  
  CredentialRequest(int paramInt, boolean paramBoolean1, String[] paramArrayOfString, CredentialPickerConfig paramCredentialPickerConfig1, CredentialPickerConfig paramCredentialPickerConfig2, boolean paramBoolean2, String paramString1, String paramString2, boolean paramBoolean3)
  {
    txPower = paramInt;
    keepUpdated = paramBoolean1;
    zzaa = ((String[])Preconditions.checkNotNull(paramArrayOfString));
    paramArrayOfString = paramCredentialPickerConfig1;
    if (paramCredentialPickerConfig1 == null) {
      paramArrayOfString = new CredentialPickerConfig.Builder().build();
    }
    zzab = paramArrayOfString;
    paramArrayOfString = paramCredentialPickerConfig2;
    if (paramCredentialPickerConfig2 == null) {
      paramArrayOfString = new CredentialPickerConfig.Builder().build();
    }
    zzac = paramArrayOfString;
    if (paramInt < 3)
    {
      zzad = true;
      zzae = null;
      zzaf = null;
    }
    else
    {
      zzad = paramBoolean2;
      zzae = paramString1;
      zzaf = paramString2;
    }
    zzag = paramBoolean3;
  }
  
  private CredentialRequest(Builder paramBuilder)
  {
    this(4, Builder.getEWAHIterator(paramBuilder), Builder.fromField(paramBuilder), Builder.log1p(paramBuilder), Builder.getRemoteURL(paramBuilder), Builder.isInheritable(paramBuilder), Builder.getDbFilename(paramBuilder), Builder.getSoundPath(paramBuilder), false);
  }
  
  public final String[] getAccountTypes()
  {
    return zzaa;
  }
  
  public final Set getAccountTypesSet()
  {
    return new HashSet(Arrays.asList(zzaa));
  }
  
  public final CredentialPickerConfig getCredentialHintPickerConfig()
  {
    return zzac;
  }
  
  public final CredentialPickerConfig getCredentialPickerConfig()
  {
    return zzab;
  }
  
  public final String getIdTokenNonce()
  {
    return zzaf;
  }
  
  public final String getServerClientId()
  {
    return zzae;
  }
  
  public final boolean getSupportsPasswordLogin()
  {
    return isPasswordLoginSupported();
  }
  
  public final boolean isIdTokenRequested()
  {
    return zzad;
  }
  
  public final boolean isPasswordLoginSupported()
  {
    return keepUpdated;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBoolean(paramParcel, 1, isPasswordLoginSupported());
    SafeParcelWriter.writeStringArray(paramParcel, 2, getAccountTypes(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getCredentialPickerConfig(), paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, getCredentialHintPickerConfig(), paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 5, isIdTokenRequested());
    SafeParcelWriter.writeString(paramParcel, 6, getServerClientId(), false);
    SafeParcelWriter.writeString(paramParcel, 7, getIdTokenNonce(), false);
    SafeParcelWriter.writeInt(paramParcel, 1000, txPower);
    SafeParcelWriter.writeBoolean(paramParcel, 8, zzag);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class Builder
  {
    private boolean saveThumbnail;
    private String[] zzaa;
    private CredentialPickerConfig zzab;
    private CredentialPickerConfig zzac;
    private boolean zzad = false;
    @Nullable
    private String zzae = null;
    @Nullable
    private String zzaf;
    private boolean zzag = false;
    
    public Builder() {}
    
    public final CredentialRequest build()
    {
      if (zzaa == null) {
        zzaa = new String[0];
      }
      if ((!saveThumbnail) && (zzaa.length == 0)) {
        throw new IllegalStateException("At least one authentication method must be specified");
      }
      return new CredentialRequest(this, null);
    }
    
    public final Builder setAccountTypes(String... paramVarArgs)
    {
      String[] arrayOfString = paramVarArgs;
      if (paramVarArgs == null) {
        arrayOfString = new String[0];
      }
      zzaa = arrayOfString;
      return this;
    }
    
    public final Builder setCredentialHintPickerConfig(CredentialPickerConfig paramCredentialPickerConfig)
    {
      zzac = paramCredentialPickerConfig;
      return this;
    }
    
    public final Builder setCredentialPickerConfig(CredentialPickerConfig paramCredentialPickerConfig)
    {
      zzab = paramCredentialPickerConfig;
      return this;
    }
    
    public final Builder setIdTokenNonce(String paramString)
    {
      zzaf = paramString;
      return this;
    }
    
    public final Builder setIdTokenRequested(boolean paramBoolean)
    {
      zzad = paramBoolean;
      return this;
    }
    
    public final Builder setPasswordLoginSupported(boolean paramBoolean)
    {
      saveThumbnail = paramBoolean;
      return this;
    }
    
    public final Builder setServerClientId(String paramString)
    {
      zzae = paramString;
      return this;
    }
    
    public final Builder setSupportsPasswordLogin(boolean paramBoolean)
    {
      return setPasswordLoginSupported(paramBoolean);
    }
  }
}
