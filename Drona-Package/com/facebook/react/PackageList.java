package com.facebook.react;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import com.RNFetchBlob.RNFetchBlobPackage;
import com.airbnb.android.react.lottie.LottiePackage;
import com.airbnb.android.react.maps.MapsPackage;
import com.bitgo.randombytes.RandomBytesPackage;
import com.bugsnag.BugsnagReactNative;
import com.clipsub.RNShake.RNShakeEventPackage;
import com.dooboolab.RNIap.RNIapPackage;
import com.facebook.react.shell.MainPackageConfig;
import com.facebook.react.shell.MainReactPackage;
import com.futurepress.staticserver.FPStaticServerPackage;
import com.geektime.rnonesignalandroid.ReactNativeOneSignalPackage;
import com.github.yamill.orientation.OrientationPackage;
import com.imagepicker.ImagePickerPackage;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage;
import com.masteratul.exceptionhandler.ReactNativeExceptionHandlerPackage;
import com.ocetnik.timer.BackgroundTimerPackage;
import com.polidea.reactnativeble.BlePackage;
import com.reactcommunity.rndatetimepicker.RNDateTimePickerPackage;
import com.reactnativecommunity.netinfo.NetInfoPackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
import com.rnfs.RNFSPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.swmansion.reanimated.ReanimatedPackage;
import com.swmansion.rnscreens.RNScreensPackage;
import com.uxcam.RNUxcamPackage;
import com.wenkesj.voice.VoicePackage;
import com.zmxv.RNSound.RNSoundPackage;
import email.apptailor.googlesignin.RNGoogleSigninPackage;
import fr.bamlab.rnimageresizer.ImageResizerPackage;
import io.expo.appearance.RNCAppearancePackage;
import java.util.ArrayList;
import java.util.Arrays;
import net.no_mad.tts.TextToSpeechPackage;
import org.json.RNSharePackage;
import widgets.innfactory.apiai.RNApiAiPackage;

public class PackageList
{
  private Application application;
  private MainPackageConfig mConfig;
  private ReactNativeHost reactNativeHost;
  
  public PackageList(Application paramApplication)
  {
    this(paramApplication, null);
  }
  
  public PackageList(Application paramApplication, MainPackageConfig paramMainPackageConfig)
  {
    reactNativeHost = null;
    application = paramApplication;
    mConfig = paramMainPackageConfig;
  }
  
  public PackageList(ReactNativeHost paramReactNativeHost)
  {
    this(paramReactNativeHost, null);
  }
  
  public PackageList(ReactNativeHost paramReactNativeHost, MainPackageConfig paramMainPackageConfig)
  {
    reactNativeHost = paramReactNativeHost;
    mConfig = paramMainPackageConfig;
  }
  
  private Application getApplication()
  {
    if (reactNativeHost == null) {
      return application;
    }
    return reactNativeHost.getApplication();
  }
  
  private Context getApplicationContext()
  {
    return getApplication().getApplicationContext();
  }
  
  private ReactNativeHost getReactNativeHost()
  {
    return reactNativeHost;
  }
  
  private Resources getResources()
  {
    return getApplication().getResources();
  }
  
  public ArrayList getPackages()
  {
    return new ArrayList(Arrays.asList(new ReactPackage[] { new MainReactPackage(mConfig), new RNDateTimePickerPackage(), new RNGoogleSigninPackage(), new NetInfoPackage(), new VoicePackage(), BugsnagReactNative.getPackage(), new LottiePackage(), new RNCAppearancePackage(), new BackgroundTimerPackage(), new BlePackage(), new ReactNativeConfigPackage(), new RNDeviceInfo(), new RNApiAiPackage(), new ReactNativeExceptionHandlerPackage(), new RNFSPackage(), new RNGestureHandlerPackage(), new RNIapPackage(), new ImagePickerPackage(), new ImageResizerPackage(), new MapsPackage(), new ReactNativeOneSignalPackage(), new OrientationPackage(), new RandomBytesPackage(), new ReanimatedPackage(), new RNScreensPackage(), new RNShakeEventPackage(), new RNSharePackage(), new RNSoundPackage(), new FPStaticServerPackage(), new TextToSpeechPackage(), new RNUxcamPackage(), new RNCWebViewPackage(), new RNFetchBlobPackage() }));
  }
}
