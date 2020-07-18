package expo.modules.capabilities.admob;

import android.content.Context;
import android.content.res.Resources;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.google.android.gms.package_8.AdSize;

class AdMobUtils
{
  AdMobUtils() {}
  
  static Bundle createEventForAdFailedToLoad(int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("error", errorStringForAdFailedCode(paramInt));
    return localBundle;
  }
  
  static Bundle createEventForSizeChange(Context paramContext, AdSize paramAdSize)
  {
    Bundle localBundle = new Bundle();
    int i;
    int j;
    if (paramAdSize == AdSize.SMART_BANNER)
    {
      i = toDIPFromPixel(paramContext, paramAdSize.getWidthInPixels(paramContext));
      j = toDIPFromPixel(paramContext, paramAdSize.getHeightInPixels(paramContext));
    }
    else
    {
      i = paramAdSize.getWidth();
      j = paramAdSize.getHeight();
    }
    localBundle.putDouble("width", i);
    localBundle.putDouble("height", j);
    return localBundle;
  }
  
  static String errorStringForAdFailedCode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 3: 
      return "ERROR_CODE_NO_FILL";
    case 2: 
      return "ERROR_CODE_NETWORK_ERROR";
    case 1: 
      return "ERROR_CODE_INVALID_REQUEST";
    }
    return "ERROR_CODE_INTERNAL_ERROR";
  }
  
  static AdSize getAdSizeFromString(String paramString)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 2081083770: 
      if (paramString.equals("mediumRectangle")) {
        i = 2;
      }
      break;
    case 1251459344: 
      if (paramString.equals("smartBannerPortrait")) {
        i = 5;
      }
      break;
    case 438737894: 
      if (paramString.equals("smartBannerLandscape")) {
        i = 6;
      }
      break;
    case -994916779: 
      if (paramString.equals("smartBanner")) {
        i = 7;
      }
      break;
    case -1294719333: 
      if (paramString.equals("fullBanner")) {
        i = 3;
      }
      break;
    case -1396342996: 
      if (paramString.equals("banner")) {
        i = 0;
      }
      break;
    case -1735624867: 
      if (paramString.equals("leaderBoard")) {
        i = 4;
      }
      break;
    case -2023649721: 
      if (paramString.equals("largeBanner")) {
        i = 1;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      return AdSize.BANNER;
    case 7: 
      return AdSize.SMART_BANNER;
    case 6: 
      return AdSize.SMART_BANNER;
    case 5: 
      return AdSize.SMART_BANNER;
    case 4: 
      return AdSize.LEADERBOARD;
    case 3: 
      return AdSize.FULL_BANNER;
    case 2: 
      return AdSize.MEDIUM_RECTANGLE;
    case 1: 
      return AdSize.LARGE_BANNER;
    }
    return AdSize.BANNER;
  }
  
  static int toDIPFromPixel(Context paramContext, int paramInt)
  {
    float f = getResourcesgetDisplayMetricsdensity;
    return (int)(paramInt / f);
  }
}
