package com.google.android.gms.auth.util.credentials;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;

@SafeParcelable.Class(creator="HintRequestCreator")
public final class HintRequest
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.credentials.HintRequest> CREATOR = new Point.1();
  @SafeParcelable.Field(id=1000)
  private final int startMinute;
  @SafeParcelable.Field(getter="getAccountTypes", id=4)
  private final String[] zzaa;
  @SafeParcelable.Field(getter="isIdTokenRequested", id=5)
  private final boolean zzad;
  @SafeParcelable.Field(getter="getServerClientId", id=6)
  private final String zzae;
  @SafeParcelable.Field(getter="getIdTokenNonce", id=7)
  private final String zzaf;
  @SafeParcelable.Field(getter="getHintPickerConfig", id=1)
  private final CredentialPickerConfig zzah;
  @SafeParcelable.Field(getter="isEmailAddressIdentifierSupported", id=2)
  private final boolean zzai;
  @SafeParcelable.Field(getter="isPhoneNumberIdentifierSupported", id=3)
  private final boolean zzaj;
  
  HintRequest(int paramInt, CredentialPickerConfig paramCredentialPickerConfig, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString, boolean paramBoolean3, String paramString1, String paramString2)
  {
    startMinute = paramInt;
    zzah = ((CredentialPickerConfig)Preconditions.checkNotNull(paramCredentialPickerConfig));
    zzai = paramBoolean1;
    zzaj = paramBoolean2;
    zzaa = ((String[])Preconditions.checkNotNull(paramArrayOfString));
    if (startMinute < 2)
    {
      zzad = true;
      zzae = null;
      zzaf = null;
      return;
    }
    zzad = paramBoolean3;
    zzae = paramString1;
    zzaf = paramString2;
  }
  
  private HintRequest(Builder paramBuilder)
  {
    this(2, Builder.log1p(paramBuilder), Builder.isInheritable(paramBuilder), Builder.getEWAHIterator(paramBuilder), Builder.fromField(paramBuilder), Builder.getArticleUrl(paramBuilder), Builder.getDbFilename(paramBuilder), Builder.getSoundPath(paramBuilder));
  }
  
  public final String[] getAccountTypes()
  {
    return zzaa;
  }
  
  public final CredentialPickerConfig getHintPickerConfig()
  {
    return zzah;
  }
  
  public final String getIdTokenNonce()
  {
    return zzaf;
  }
  
  public final String getServerClientId()
  {
    return zzae;
  }
  
  public final boolean isEmailAddressIdentifierSupported()
  {
    return zzai;
  }
  
  public final boolean isIdTokenRequested()
  {
    return zzad;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 1, getHintPickerConfig(), paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 2, isEmailAddressIdentifierSupported());
    SafeParcelWriter.writeBoolean(paramParcel, 3, zzaj);
    SafeParcelWriter.writeStringArray(paramParcel, 4, getAccountTypes(), false);
    SafeParcelWriter.writeBoolean(paramParcel, 5, isIdTokenRequested());
    SafeParcelWriter.writeString(paramParcel, 6, getServerClientId(), false);
    SafeParcelWriter.writeString(paramParcel, 7, getIdTokenNonce(), false);
    SafeParcelWriter.writeInt(paramParcel, 1000, startMinute);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class Builder
  {
    private String[] zzaa;
    private boolean zzad = false;
    private String zzae;
    private String zzaf;
    private CredentialPickerConfig zzah = new CredentialPickerConfig.Builder().build();
    private boolean zzai;
    private boolean zzaj;
    
    public Builder() {}
    
    public final HintRequest build()
    {
      if (zzaa == null) {
        zzaa = new String[0];
      }
      if ((!zzai) && (!zzaj) && (zzaa.length == 0)) {
        throw new IllegalStateException("At least one authentication method must be specified");
      }
      return new HintRequest(this, null);
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
    
    public final Builder setEmailAddressIdentifierSupported(boolean paramBoolean)
    {
      zzai = paramBoolean;
      return this;
    }
    
    public final Builder setHintPickerConfig(CredentialPickerConfig paramCredentialPickerConfig)
    {
      zzah = ((CredentialPickerConfig)Preconditions.checkNotNull(paramCredentialPickerConfig));
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
    
    public final Builder setPhoneNumberIdentifierSupported(boolean paramBoolean)
    {
      zzaj = paramBoolean;
      return this;
    }
    
    public final Builder setServerClientId(String paramString)
    {
      zzae = paramString;
      return this;
    }
  }
}
