package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.package_6.Sample;

public final class Msg<O extends com.google.android.gms.common.api.Api.ApiOptions>
{
  private final Api<O> mApi;
  private final O zabh;
  private final boolean zacu;
  private final int zacv;
  
  private Msg(Sample paramSample)
  {
    zacu = true;
    mApi = paramSample;
    zabh = null;
    zacv = System.identityHashCode(this);
  }
  
  private Msg(Sample paramSample, com.google.android.gms.common.package_6.Api.ApiOptions paramApiOptions)
  {
    zacu = false;
    mApi = paramSample;
    zabh = paramApiOptions;
    zacv = Objects.hashCode(new Object[] { mApi, zabh });
  }
  
  public static Msg readMessage(Sample paramSample)
  {
    return new Msg(paramSample);
  }
  
  public static Msg readMessage(Sample paramSample, com.google.android.gms.common.package_6.Api.ApiOptions paramApiOptions)
  {
    return new Msg(paramSample, paramApiOptions);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Msg)) {
      return false;
    }
    paramObject = (Msg)paramObject;
    return (!zacu) && (!zacu) && (Objects.equal(mApi, mApi)) && (Objects.equal(zabh, zabh));
  }
  
  public final String get()
  {
    return mApi.getName();
  }
  
  public final int hashCode()
  {
    return zacv;
  }
}
