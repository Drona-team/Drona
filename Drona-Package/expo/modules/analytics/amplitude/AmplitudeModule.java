package expo.modules.analytics.amplitude;

import android.content.Context;
import com.amplitude.upgrade.Amplitude;
import com.amplitude.upgrade.AmplitudeClient;
import com.amplitude.upgrade.TrackingOptions;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.Promise;
import org.unimodules.core.arguments.ReadableArguments;

public class AmplitudeModule
  extends ExportedModule
{
  private AmplitudeClient mClient;
  private TrackingOptions mPendingTrackingOptions;
  
  public AmplitudeModule(Context paramContext)
  {
    super(paramContext);
  }
  
  private boolean rejectUnlessInitialized(Promise paramPromise)
  {
    if (mClient == null)
    {
      paramPromise.reject("E_NO_INIT", "Amplitude client has not been initialized, are you sure you have configured it with #init(apiKey)?");
      return true;
    }
    return false;
  }
  
  public void clearUserProperties(Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.clearUserProperties();
    paramPromise.resolve(null);
  }
  
  protected AmplitudeClient getClient(String paramString)
  {
    return Amplitude.getInstance(paramString);
  }
  
  public String getName()
  {
    return "ExpoAmplitude";
  }
  
  public void initialize(String paramString, Promise paramPromise)
  {
    mClient = getClient(paramString);
    if (mPendingTrackingOptions != null) {
      mClient.setTrackingOptions(mPendingTrackingOptions);
    }
    mClient.initialize(getContext(), paramString);
    paramPromise.resolve(null);
  }
  
  public void logEvent(String paramString, Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.logEvent(paramString);
    paramPromise.resolve(null);
  }
  
  public void logEventWithProperties(String paramString, Map paramMap, Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.logEvent(paramString, new JSONObject(paramMap));
    paramPromise.resolve(null);
  }
  
  public void setGroup(String paramString, List paramList, Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.setGroup(paramString, new JSONArray(paramList));
    paramPromise.resolve(null);
  }
  
  public void setTrackingOptions(ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    TrackingOptions localTrackingOptions = new TrackingOptions();
    if (paramReadableArguments.getBoolean("disableAdid")) {
      localTrackingOptions.disableAdid();
    }
    if (paramReadableArguments.getBoolean("disableCarrier")) {
      localTrackingOptions.disableCarrier();
    }
    if (paramReadableArguments.getBoolean("disableCity")) {
      localTrackingOptions.disableCity();
    }
    if (paramReadableArguments.getBoolean("disableCountry")) {
      localTrackingOptions.disableCountry();
    }
    if (paramReadableArguments.getBoolean("disableDeviceBrand")) {
      localTrackingOptions.disableDeviceBrand();
    }
    if (paramReadableArguments.getBoolean("disableDeviceModel")) {
      localTrackingOptions.disableDeviceModel();
    }
    if (paramReadableArguments.getBoolean("disableDMA")) {
      localTrackingOptions.disableDma();
    }
    if (paramReadableArguments.getBoolean("disableIPAddress")) {
      localTrackingOptions.disableIpAddress();
    }
    if (paramReadableArguments.getBoolean("disableLanguage")) {
      localTrackingOptions.disableLanguage();
    }
    if (paramReadableArguments.getBoolean("disableLatLng")) {
      localTrackingOptions.disableLatLng();
    }
    if (paramReadableArguments.getBoolean("disableOSName")) {
      localTrackingOptions.disableOsName();
    }
    if (paramReadableArguments.getBoolean("disableOSVersion")) {
      localTrackingOptions.disableOsVersion();
    }
    if (paramReadableArguments.getBoolean("disablePlatform")) {
      localTrackingOptions.disablePlatform();
    }
    if (paramReadableArguments.getBoolean("disableRegion")) {
      localTrackingOptions.disableRegion();
    }
    if (paramReadableArguments.getBoolean("disableVersionName")) {
      localTrackingOptions.disableVersionName();
    }
    if (mClient != null) {
      mClient.setTrackingOptions(localTrackingOptions);
    } else {
      mPendingTrackingOptions = localTrackingOptions;
    }
    paramPromise.resolve(null);
  }
  
  public void setUserId(String paramString, Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.setUserId(paramString);
    paramPromise.resolve(null);
  }
  
  public void setUserProperties(Map paramMap, Promise paramPromise)
  {
    if (rejectUnlessInitialized(paramPromise)) {
      return;
    }
    mClient.setUserProperties(new JSONObject(paramMap));
    paramPromise.resolve(null);
  }
}
