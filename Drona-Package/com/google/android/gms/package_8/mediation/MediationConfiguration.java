package com.google.android.gms.package_8.mediation;

import android.os.Bundle;
import com.google.android.gms.package_8.AdFormat;

public class MediationConfiguration
{
  private final Bundle zzemv;
  private final AdFormat zzemz;
  
  public MediationConfiguration(AdFormat paramAdFormat, Bundle paramBundle)
  {
    zzemz = paramAdFormat;
    zzemv = paramBundle;
  }
  
  public AdFormat getFormat()
  {
    return zzemz;
  }
  
  public Bundle getServerParameters()
  {
    return zzemv;
  }
}
