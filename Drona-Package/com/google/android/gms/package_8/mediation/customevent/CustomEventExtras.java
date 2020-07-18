package com.google.android.gms.package_8.mediation.customevent;

import com.google.ads.mediation.NetworkExtras;
import java.util.HashMap;

@Deprecated
public final class CustomEventExtras
  implements NetworkExtras
{
  private final HashMap<String, Object> zzeob = new HashMap();
  
  public CustomEventExtras() {}
  
  public final Object getExtra(String paramString)
  {
    return zzeob.get(paramString);
  }
  
  public final void setExtra(String paramString, Object paramObject)
  {
    zzeob.put(paramString, paramObject);
  }
}
