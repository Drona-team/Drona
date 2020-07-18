package com.amplitude.upgrade;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DeviceInfo
{
  public static final String DEVICE = "com.amplitude.api.DeviceInfo";
  public static final String OS_NAME = "android";
  private static final String SETTING_ADVERTISING_ID = "advertising_id";
  private static final String SETTING_LIMIT_AD_TRACKING = "limit_ad_tracking";
  private CachedInfo cachedInfo;
  private Context context;
  private boolean locationListening = true;
  
  public DeviceInfo(Context paramContext)
  {
    context = paramContext;
  }
  
  public static String generateUUID()
  {
    return UUID.randomUUID().toString();
  }
  
  private CachedInfo getCachedInfo()
  {
    if (cachedInfo == null) {
      cachedInfo = new CachedInfo(null);
    }
    return cachedInfo;
  }
  
  public String getAdvertisingId()
  {
    return getCachedInfoadvertisingId;
  }
  
  public String getBrand()
  {
    return getCachedInfobrand;
  }
  
  public String getCarrier()
  {
    return getCachedInfocarrier;
  }
  
  public String getCountry()
  {
    return getCachedInfocountry;
  }
  
  protected Geocoder getGeocoder()
  {
    return new Geocoder(context, Locale.ENGLISH);
  }
  
  public String getLanguage()
  {
    return getCachedInfolanguage;
  }
  
  public String getManufacturer()
  {
    return getCachedInfomanufacturer;
  }
  
  public String getModel()
  {
    return getCachedInfomodel;
  }
  
  public Location getMostRecentLocation()
  {
    boolean bool = isLocationListening();
    Location localLocation = null;
    if (!bool) {
      return null;
    }
    Object localObject3 = (LocationManager)context.getSystemService("location");
    if (localObject3 == null) {
      return null;
    }
    Object localObject1;
    try
    {
      List localList = ((LocationManager)localObject3).getProviders(true);
    }
    catch (SecurityException localSecurityException)
    {
      Diagnostics.getLogger().logError("Failed to get most recent location", localSecurityException);
      localObject1 = null;
    }
    if (localObject1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ((List)localObject1).iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (String)localIterator.next();
      try
      {
        localObject1 = ((LocationManager)localObject3).getLastKnownLocation((String)localObject1);
      }
      catch (Exception localException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Failed to get most recent location");
        Diagnostics.getLogger().logError("Failed to get most recent location", localException);
        localObject2 = null;
      }
      if (localObject2 != null) {
        localArrayList.add(localObject2);
      }
    }
    long l = -1L;
    localObject3 = localArrayList.iterator();
    Object localObject2 = localLocation;
    while (((Iterator)localObject3).hasNext())
    {
      localLocation = (Location)((Iterator)localObject3).next();
      if (localLocation.getTime() > l)
      {
        localObject2 = localLocation;
        l = localLocation.getTime();
      }
    }
    return localObject2;
  }
  
  public String getOsName()
  {
    return getCachedInfoosName;
  }
  
  public String getOsVersion()
  {
    return getCachedInfoosVersion;
  }
  
  public String getVersionName()
  {
    return getCachedInfoversionName;
  }
  
  public boolean isGooglePlayServicesEnabled()
  {
    return getCachedInfogpsEnabled;
  }
  
  public boolean isLimitAdTrackingEnabled()
  {
    return getCachedInfolimitAdTrackingEnabled;
  }
  
  public boolean isLocationListening()
  {
    return locationListening;
  }
  
  public void prefetch()
  {
    getCachedInfo();
  }
  
  public void setLocationListening(boolean paramBoolean)
  {
    locationListening = paramBoolean;
  }
  
  class CachedInfo
  {
    private String advertisingId = getAdvertisingId();
    private String brand = getBrand();
    private String carrier = getCarrier();
    private String country = getCountry();
    private boolean gpsEnabled = checkGPSEnabled();
    private String language = getLanguage();
    private boolean limitAdTrackingEnabled;
    private String manufacturer = getManufacturer();
    private String model = getModel();
    private String osName = getOsName();
    private String osVersion = getOsVersion();
    private String versionName = getVersionName();
    
    private CachedInfo() {}
    
    private boolean checkGPSEnabled()
    {
      try
      {
        Object localObject1 = Class.forName("com.google.android.gms.common.GooglePlayServicesUtil");
        localObject1 = ((Class)localObject1).getMethod("isGooglePlayServicesAvailable", new Class[] { Context.class });
        localObject2 = context;
        localObject1 = ((Method)localObject1).invoke(null, new Object[] { localObject2 });
        localObject1 = (Integer)localObject1;
        if (localObject1 != null)
        {
          int i = ((Integer)localObject1).intValue();
          if (i == 0) {
            return true;
          }
        }
      }
      catch (Exception localException)
      {
        Object localObject2 = AmplitudeLog.getLogger();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Error when checking for Google Play Services: ");
        localStringBuilder.append(localException);
        ((AmplitudeLog)localObject2).w("com.amplitude.api.DeviceInfo", localStringBuilder.toString());
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localException);
        return false;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services not available");
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localIllegalAccessException);
        return false;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services not available");
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localInvocationTargetException);
        return false;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services not available");
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localNoSuchMethodException);
        return false;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services Util not found!");
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localClassNotFoundException);
        return false;
      }
      catch (NoClassDefFoundError localNoClassDefFoundError)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services Util not found!");
        Diagnostics.getLogger().logError("Failed to check GPS enabled", localNoClassDefFoundError);
      }
      return false;
    }
    
    private String getAdvertisingId()
    {
      if ("Amazon".equals(getManufacturer())) {
        return getAndCacheAmazonAdvertisingId();
      }
      return getAndCacheGoogleAdvertisingId();
    }
    
    private String getAndCacheAmazonAdvertisingId()
    {
      ContentResolver localContentResolver = context.getContentResolver();
      boolean bool = false;
      if (Settings.Secure.getInt(localContentResolver, "limit_ad_tracking", 0) == 1) {
        bool = true;
      }
      limitAdTrackingEnabled = bool;
      advertisingId = Settings.Secure.getString(localContentResolver, "advertising_id");
      return advertisingId;
    }
    
    private String getAndCacheGoogleAdvertisingId()
    {
      try
      {
        Object localObject1 = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
        boolean bool1 = true;
        localObject1 = ((Class)localObject1).getMethod("getAdvertisingIdInfo", new Class[] { Context.class });
        Object localObject2 = DeviceInfo.this;
        localObject2 = context;
        localObject1 = ((Method)localObject1).invoke(null, new Object[] { localObject2 });
        localObject2 = localObject1.getClass();
        localObject2 = ((Class)localObject2).getMethod("isLimitAdTrackingEnabled", new Class[0]);
        localObject2 = ((Method)localObject2).invoke(localObject1, new Object[0]);
        localObject2 = (Boolean)localObject2;
        if (localObject2 != null)
        {
          boolean bool2 = ((Boolean)localObject2).booleanValue();
          if (bool2) {}
        }
        else
        {
          bool1 = false;
        }
        limitAdTrackingEnabled = bool1;
        localObject2 = localObject1.getClass();
        localObject2 = ((Class)localObject2).getMethod("getId", new Class[0]);
        localObject1 = ((Method)localObject2).invoke(localObject1, new Object[0]);
        advertisingId = ((String)localObject1);
      }
      catch (Exception localException)
      {
        AmplitudeLog.getLogger().e("com.amplitude.api.DeviceInfo", "Encountered an error connecting to Google Play Services", localException);
        Diagnostics.getLogger().logError("Failed to get ADID", localException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services not available");
        Diagnostics.getLogger().logError("Failed to get ADID", localInvocationTargetException);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        AmplitudeLog.getLogger().w("com.amplitude.api.DeviceInfo", "Google Play Services SDK not found!");
        Diagnostics.getLogger().logError("Failed to get ADID", localClassNotFoundException);
      }
      return advertisingId;
    }
    
    private String getBrand()
    {
      return Build.BRAND;
    }
    
    private String getCarrier()
    {
      Object localObject = DeviceInfo.this;
      try
      {
        localObject = context.getSystemService("phone");
        localObject = (TelephonyManager)localObject;
        localObject = ((TelephonyManager)localObject).getNetworkOperatorName();
        return localObject;
      }
      catch (Exception localException)
      {
        Diagnostics.getLogger().logError("Failed to get carrier", localException);
      }
      return null;
    }
    
    private String getCountry()
    {
      String str = getCountryFromLocation();
      if (!Utils.isEmptyString(str)) {
        return str;
      }
      str = getCountryFromNetwork();
      if (!Utils.isEmptyString(str)) {
        return str;
      }
      return getCountryFromLocale();
    }
    
    private String getCountryFromLocale()
    {
      return Locale.getDefault().getCountry();
    }
    
    private String getCountryFromLocation()
    {
      if (!isLocationListening()) {
        return null;
      }
      Object localObject1 = getMostRecentLocation();
      if (localObject1 != null) {
        try
        {
          boolean bool = Geocoder.isPresent();
          if (bool)
          {
            Object localObject2 = DeviceInfo.this;
            localObject1 = ((DeviceInfo)localObject2).getGeocoder().getFromLocation(((Location)localObject1).getLatitude(), ((Location)localObject1).getLongitude(), 1);
            if (localObject1 != null)
            {
              localObject1 = ((List)localObject1).iterator();
              do
              {
                bool = ((Iterator)localObject1).hasNext();
                if (!bool) {
                  break;
                }
                localObject2 = ((Iterator)localObject1).next();
                localObject2 = (Address)localObject2;
              } while (localObject2 == null);
              localObject1 = ((Address)localObject2).getCountryCode();
              return localObject1;
            }
          }
        }
        catch (IllegalStateException localIllegalStateException)
        {
          Diagnostics.getLogger().logError("Failed to get country from location", localIllegalStateException);
          return null;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Diagnostics.getLogger().logError("Failed to get country from location", localIllegalArgumentException);
          return null;
        }
        catch (NoSuchMethodError localNoSuchMethodError)
        {
          Diagnostics.getLogger().logError("Failed to get country from location", localNoSuchMethodError);
          return null;
        }
        catch (NullPointerException localNullPointerException)
        {
          Diagnostics.getLogger().logError("Failed to get country from location", localNullPointerException);
          return null;
        }
        catch (IOException localIOException)
        {
          Diagnostics.getLogger().logError("Failed to get country from location", localIOException);
        }
      }
      return null;
    }
    
    private String getCountryFromNetwork()
    {
      Object localObject = DeviceInfo.this;
      try
      {
        localObject = context.getSystemService("phone");
        localObject = (TelephonyManager)localObject;
        int i = ((TelephonyManager)localObject).getPhoneType();
        if (i != 2)
        {
          localObject = ((TelephonyManager)localObject).getNetworkCountryIso();
          if (localObject != null)
          {
            Locale localLocale = Locale.US;
            localObject = ((String)localObject).toUpperCase(localLocale);
            return localObject;
          }
        }
      }
      catch (Exception localException)
      {
        Diagnostics.getLogger().logError("Failed to get country from network", localException);
      }
      return null;
    }
    
    private String getLanguage()
    {
      return Locale.getDefault().getLanguage();
    }
    
    private String getManufacturer()
    {
      return Build.MANUFACTURER;
    }
    
    private String getModel()
    {
      return Build.MODEL;
    }
    
    private String getOsName()
    {
      return "android";
    }
    
    private String getOsVersion()
    {
      return Build.VERSION.RELEASE;
    }
    
    private String getVersionName()
    {
      Object localObject = DeviceInfo.this;
      try
      {
        localObject = context.getPackageManager();
        DeviceInfo localDeviceInfo = DeviceInfo.this;
        localObject = ((PackageManager)localObject).getPackageInfo(context.getPackageName(), 0);
        return versionName;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Diagnostics.getLogger().logError("Failed to get version name", localNameNotFoundException);
      }
      return null;
    }
  }
}
