package com.google.android.gms.package_8.doubleclick;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzaaz;
import com.google.android.gms.internal.ads.zzaba;
import com.google.android.gms.package_8.mediation.NetworkExtras;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;
import java.util.Set;

@VisibleForTesting
public final class PublisherAdRequest
{
  public static final String DEVICE_ID_EMULATOR = "B3EEABB8EE11C2BE770B684D95219ECB";
  public static final int ERROR_CODE_INTERNAL_ERROR = 0;
  public static final int ERROR_CODE_INVALID_REQUEST = 1;
  public static final int ERROR_CODE_NETWORK_ERROR = 2;
  public static final int ERROR_CODE_NO_FILL = 3;
  public static final int GENDER_FEMALE = 2;
  public static final int GENDER_MALE = 1;
  public static final int GENDER_UNKNOWN = 0;
  public static final String MAX_AD_CONTENT_RATING_G = "G";
  public static final String MAX_AD_CONTENT_RATING_MA = "MA";
  public static final String MAX_AD_CONTENT_RATING_PG = "PG";
  public static final String MAX_AD_CONTENT_RATING_T = "T";
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE = 0;
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE = 1;
  public static final int TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED = -1;
  private final zzaaz zzaam;
  
  private PublisherAdRequest(Builder paramBuilder)
  {
    zzaam = new zzaaz(Builder.getSoundPath(paramBuilder));
  }
  
  public static void updateCorrelator() {}
  
  public final Date getBirthday()
  {
    return zzaam.getBirthday();
  }
  
  public final String getContentUrl()
  {
    return zzaam.getContentUrl();
  }
  
  public final Bundle getCustomEventExtrasBundle(Class paramClass)
  {
    return zzaam.getCustomEventExtrasBundle(paramClass);
  }
  
  public final Bundle getCustomTargeting()
  {
    return zzaam.getCustomTargeting();
  }
  
  public final int getGender()
  {
    return zzaam.getGender();
  }
  
  public final Set getKeywords()
  {
    return zzaam.getKeywords();
  }
  
  public final Location getLocation()
  {
    return zzaam.getLocation();
  }
  
  public final boolean getManualImpressionsEnabled()
  {
    return zzaam.getManualImpressionsEnabled();
  }
  
  public final NetworkExtras getNetworkExtras(Class paramClass)
  {
    return zzaam.getNetworkExtras(paramClass);
  }
  
  public final Bundle getNetworkExtrasBundle(Class paramClass)
  {
    return zzaam.getNetworkExtrasBundle(paramClass);
  }
  
  public final String getPublisherProvidedId()
  {
    return zzaam.getPublisherProvidedId();
  }
  
  public final boolean isTestDevice(Context paramContext)
  {
    return zzaam.isTestDevice(paramContext);
  }
  
  public final zzaaz zzde()
  {
    return zzaam;
  }
  
  @VisibleForTesting
  public final class Builder
  {
    private final zzaba zzaan = new zzaba();
    
    public Builder() {}
    
    public final Builder addCategoryExclusion(String paramString)
    {
      zzaan.zzcc(paramString);
      return this;
    }
    
    public final Builder addCustomEventExtrasBundle(Class paramClass, Bundle paramBundle)
    {
      zzaan.zzb(paramClass, paramBundle);
      return this;
    }
    
    public final Builder addCustomTargeting(String paramString1, String paramString2)
    {
      zzaan.zze(paramString1, paramString2);
      return this;
    }
    
    public final Builder addCustomTargeting(String paramString, List paramList)
    {
      if (paramList != null) {
        zzaan.zze(paramString, TextUtils.join(",", paramList));
      }
      return this;
    }
    
    public final Builder addKeyword(String paramString)
    {
      zzaan.zzbw(paramString);
      return this;
    }
    
    public final Builder addNetworkExtras(NetworkExtras paramNetworkExtras)
    {
      zzaan.zza(paramNetworkExtras);
      return this;
    }
    
    public final Builder addNetworkExtrasBundle(Class paramClass, Bundle paramBundle)
    {
      zzaan.zza(paramClass, paramBundle);
      return this;
    }
    
    public final Builder addTestDevice(String paramString)
    {
      zzaan.zzbx(paramString);
      return this;
    }
    
    public final PublisherAdRequest build()
    {
      return new PublisherAdRequest(this, null);
    }
    
    public final Builder setBirthday(Date paramDate)
    {
      zzaan.zza(paramDate);
      return this;
    }
    
    public final Builder setContentUrl(String paramString)
    {
      Preconditions.checkNotNull(paramString, "Content URL must be non-null.");
      Preconditions.checkNotEmpty(paramString, "Content URL must be non-empty.");
      boolean bool;
      if (paramString.length() <= 512) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "Content URL must not exceed %d in length.  Provided length was %d.", new Object[] { Integer.valueOf(512), Integer.valueOf(paramString.length()) });
      zzaan.zzbz(paramString);
      return this;
    }
    
    public final Builder setGender(int paramInt)
    {
      zzaan.zzcn(paramInt);
      return this;
    }
    
    public final Builder setIsDesignedForFamilies(boolean paramBoolean)
    {
      zzaan.zzu(paramBoolean);
      return this;
    }
    
    public final Builder setLocation(Location paramLocation)
    {
      zzaan.zza(paramLocation);
      return this;
    }
    
    public final Builder setManualImpressionsEnabled(boolean paramBoolean)
    {
      zzaan.setManualImpressionsEnabled(paramBoolean);
      return this;
    }
    
    public final Builder setMaxAdContentRating(String paramString)
    {
      zzaan.zzcd(paramString);
      return this;
    }
    
    public final Builder setPublisherProvidedId(String paramString)
    {
      zzaan.zzca(paramString);
      return this;
    }
    
    public final Builder setRequestAgent(String paramString)
    {
      zzaan.zzcb(paramString);
      return this;
    }
    
    public final Builder setTagForUnderAgeOfConsent(int paramInt)
    {
      zzaan.zzco(paramInt);
      return this;
    }
    
    public final Builder tagForChildDirectedTreatment(boolean paramBoolean)
    {
      zzaan.zzt(paramBoolean);
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface MaxAdContentRating {}
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface TagForUnderAgeOfConsent {}
}
