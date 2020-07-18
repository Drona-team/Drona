package expo.modules.capabilities.admob;

import android.content.Context;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.Promise;

public class AdMobModule
  extends ExportedModule
{
  private static String sTestDeviceID;
  
  public AdMobModule(Context paramContext)
  {
    super(paramContext);
  }
  
  public static String getTestDeviceID()
  {
    return sTestDeviceID;
  }
  
  public String getName()
  {
    return "ExpoAdsAdMob";
  }
  
  public void setTestDeviceIDAsync(String paramString, Promise paramPromise)
  {
    if ((paramString != null) && (!"".equals(paramString)))
    {
      if ("EMULATOR".equals(paramString)) {
        sTestDeviceID = "B3EEABB8EE11C2BE770B684D95219ECB";
      } else {
        sTestDeviceID = paramString;
      }
    }
    else {
      sTestDeviceID = null;
    }
    paramPromise.resolve(null);
  }
}
