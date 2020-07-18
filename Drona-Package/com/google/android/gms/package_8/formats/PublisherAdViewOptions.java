package com.google.android.gms.package_8.formats;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.internal.ads.zzaga;
import com.google.android.gms.internal.ads.zzagb;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzyf;
import com.google.android.gms.internal.ads.zzzs;
import com.google.android.gms.internal.ads.zzzt;
import com.google.android.gms.package_8.doubleclick.AppEventListener;

@zzard
@SafeParcelable.Class(creator="PublisherAdViewOptionsCreator")
public final class PublisherAdViewOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.ads.formats.PublisherAdViewOptions> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.Field(getter="getManualImpressionsEnabled", id=1)
  private final boolean zzbqn;
  @Nullable
  @SafeParcelable.Field(getter="getAppEventListenerBinder", id=2, type="android.os.IBinder")
  private final zzzs zzbqo;
  @Nullable
  private AppEventListener zzbqp;
  @Nullable
  @SafeParcelable.Field(getter="getDelayedBannerAdListenerBinder", id=3)
  private final IBinder zzbqq;
  
  private PublisherAdViewOptions(Builder paramBuilder)
  {
    zzbqn = Builder.s(paramBuilder);
    zzbqp = Builder.getSoundPath(paramBuilder);
    if (zzbqp != null) {
      paramBuilder = new zzyf(zzbqp);
    } else {
      paramBuilder = null;
    }
    zzbqo = paramBuilder;
    zzbqq = null;
  }
  
  PublisherAdViewOptions(boolean paramBoolean, IBinder paramIBinder1, IBinder paramIBinder2)
  {
    zzbqn = paramBoolean;
    if (paramIBinder1 != null) {
      paramIBinder1 = zzzt.zzd(paramIBinder1);
    } else {
      paramIBinder1 = null;
    }
    zzbqo = paramIBinder1;
    zzbqq = paramIBinder2;
  }
  
  public final AppEventListener getAppEventListener()
  {
    return zzbqp;
  }
  
  public final boolean getManualImpressionsEnabled()
  {
    return zzbqn;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBoolean(paramParcel, 1, getManualImpressionsEnabled());
    IBinder localIBinder;
    if (zzbqo == null) {
      localIBinder = null;
    } else {
      localIBinder = zzbqo.asBinder();
    }
    SafeParcelWriter.writeIBinder(paramParcel, 2, localIBinder, false);
    SafeParcelWriter.writeIBinder(paramParcel, 3, zzbqq, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final zzzs zzkt()
  {
    return zzbqo;
  }
  
  public final zzaga zzku()
  {
    return zzagb.zzu(zzbqq);
  }
  
  public final class Builder
  {
    private boolean zzbqn = false;
    @Nullable
    private AppEventListener zzbqp;
    
    public Builder() {}
    
    public final PublisherAdViewOptions build()
    {
      return new PublisherAdViewOptions(this, null);
    }
    
    public final Builder setAppEventListener(AppEventListener paramAppEventListener)
    {
      zzbqp = paramAppEventListener;
      return this;
    }
    
    public final Builder setManualImpressionsEnabled(boolean paramBoolean)
    {
      zzbqn = paramBoolean;
      return this;
    }
  }
}
