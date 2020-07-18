package com.google.android.gms.package_8.mediation.supplement;

import com.google.android.gms.package_8.mediation.Adapter;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class RtbAdapter
  extends Adapter
{
  public RtbAdapter() {}
  
  public abstract void collectSignals(RtbSignalData paramRtbSignalData, SignalCallbacks paramSignalCallbacks);
}
