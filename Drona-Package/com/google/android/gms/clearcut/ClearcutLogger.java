package com.google.android.gms.clearcut;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.PendingResults;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import com.google.android.gms.internal.clearcut.zzaa;
import com.google.android.gms.internal.clearcut.zze;
import com.google.android.gms.internal.clearcut.zzge.zzv.zzb;
import com.google.android.gms.internal.clearcut.zzha;
import com.google.android.gms.internal.clearcut.zzj;
import com.google.android.gms.internal.clearcut.zzp;
import com.google.android.gms.internal.clearcut.zzr;
import com.google.android.gms.phenotype.ExperimentTokens;
import java.util.ArrayList;
import java.util.TimeZone;

@KeepForSdk
public final class ClearcutLogger
{
  private static final Api.AbstractClientBuilder<zzj, Api.ApiOptions.NoOptions> CLIENT_BUILDER = new SettingsActivity.2();
  private static final com.google.android.gms.common.api.Api.ClientKey<zzj> CLIENT_KEY = new com.google.android.gms.common.package_6.Api.ClientKey();
  @Deprecated
  public static final Api<Api.ApiOptions.NoOptions> bad_request = new Sample("ClearcutLogger.API", CLIENT_BUILDER, CLIENT_KEY);
  private static final ExperimentTokens[] conflict = new ExperimentTokens[0];
  private static final String[] feature_not_implemented = new String[0];
  private static final byte[][] forbidden = new byte[0][];
  private final Clock clock;
  private final PduHeaders get;
  private zzge.zzv.zzb id = zzge.zzv.zzb.zzbhk;
  private zzc mDefaultValue;
  private String mHour;
  private final Context mParent;
  private final int mVersionCode;
  private final String packageName;
  private final boolean post;
  private String thread;
  private String time;
  private final zza title;
  private int value = -1;
  
  private ClearcutLogger(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean, PduHeaders paramPduHeaders, Clock paramClock, zzc paramZzc, zza paramZza)
  {
    mParent = paramContext;
    packageName = paramContext.getPackageName();
    mVersionCode = getVersionCode(paramContext);
    value = -1;
    time = paramString1;
    mHour = paramString2;
    thread = null;
    post = paramBoolean;
    get = paramPduHeaders;
    clock = paramClock;
    mDefaultValue = new zzc();
    id = zzge.zzv.zzb.zzbhk;
    title = paramZza;
    if (paramBoolean)
    {
      if (paramString2 == null) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      Preconditions.checkArgument(paramBoolean, "can't be anonymous with an upload account");
    }
  }
  
  public ClearcutLogger(Context paramContext, String paramString1, String paramString2)
  {
    this(paramContext, -1, paramString1, paramString2, null, false, zze.zzb(paramContext), DefaultClock.getInstance(), null, (zza)new zzp(paramContext));
  }
  
  public static ClearcutLogger anonymousLogger(Context paramContext, String paramString)
  {
    return new ClearcutLogger(paramContext, -1, paramString, null, null, true, zze.zzb(paramContext), DefaultClock.getInstance(), null, (zza)new zzp(paramContext));
  }
  
  private static int[] apply(ArrayList paramArrayList)
  {
    if (paramArrayList == null) {
      return null;
    }
    int[] arrayOfInt = new int[paramArrayList.size()];
    paramArrayList = (ArrayList)paramArrayList;
    int k = paramArrayList.size();
    int j = 0;
    int i = 0;
    while (j < k)
    {
      Object localObject = paramArrayList.get(j);
      j += 1;
      arrayOfInt[i] = ((Integer)localObject).intValue();
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static int getVersionCode(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      return versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.wtf("ClearcutLogger", "This can't happen.", paramContext);
    }
    return 0;
  }
  
  public final LogEventBuilder newEvent(byte[] paramArrayOfByte)
  {
    return new LogEventBuilder(paramArrayOfByte, null);
  }
  
  public class LogEventBuilder
  {
    private ArrayList<String> c = null;
    private String hour = ClearcutLogger.getHour(ClearcutLogger.this);
    private zzge.zzv.zzb hours = ClearcutLogger.createProxy(ClearcutLogger.this);
    private ArrayList<ExperimentTokens> j = null;
    private String mTimezone = ClearcutLogger.getTimeString(ClearcutLogger.this);
    private String minutes = null;
    private ArrayList<Integer> o = null;
    private ArrayList<Integer> selection = null;
    private boolean sign = true;
    private ArrayList<byte[]> steps = null;
    private int timezone = ClearcutLogger.readString(ClearcutLogger.this);
    private final ClearcutLogger.zzb updateTrackInfo;
    private final zzha zzaa = new zzha();
    private boolean zzab = false;
    
    private LogEventBuilder(byte[] paramArrayOfByte)
    {
      this(paramArrayOfByte, null);
    }
    
    private LogEventBuilder(byte[] paramArrayOfByte, ClearcutLogger.zzb paramZzb)
    {
      zzaa.zzbkc = zzaa.zze(ClearcutLogger.access$getMParent(ClearcutLogger.this));
      zzaa.zzbjf = ClearcutLogger.getClock(ClearcutLogger.this).currentTimeMillis();
      zzaa.zzbjg = ClearcutLogger.getClock(ClearcutLogger.this).elapsedRealtime();
      paramZzb = zzaa;
      ClearcutLogger.readValue(ClearcutLogger.this);
      long l = zzaa.zzbjf;
      zzbju = (TimeZone.getDefault().getOffset(l) / 1000);
      if (paramArrayOfByte != null) {
        zzaa.zzbjp = paramArrayOfByte;
      }
      updateTrackInfo = null;
    }
    
    public void addTo()
    {
      if (!zzab)
      {
        zzab = true;
        Departure localDeparture = new Departure(new zzr(ClearcutLogger.toString(ClearcutLogger.this), ClearcutLogger.getVersionCode(ClearcutLogger.this), timezone, mTimezone, hour, minutes, ClearcutLogger.shift(ClearcutLogger.this), hours), zzaa, null, null, ClearcutLogger.set(null), null, ClearcutLogger.set(null), null, null, sign);
        if (ClearcutLogger.getTitleView(ClearcutLogger.this).equals(localDeparture))
        {
          ClearcutLogger.method_1(ClearcutLogger.this).appendEncodedStringValue(localDeparture);
          return;
        }
        PendingResults.immediatePendingResult(Status.RESULT_SUCCESS, null);
        return;
      }
      throw new IllegalStateException("do not reuse LogEventBuilder");
    }
    
    public LogEventBuilder setEventCode(int paramInt)
    {
      zzaa.zzbji = paramInt;
      return this;
    }
  }
  
  public static abstract interface zza
  {
    public abstract boolean equals(Departure paramDeparture);
  }
  
  public static abstract interface zzb
  {
    public abstract byte[] getIssuingDistributionPoint();
  }
  
  public static final class zzc
  {
    public zzc() {}
  }
}
