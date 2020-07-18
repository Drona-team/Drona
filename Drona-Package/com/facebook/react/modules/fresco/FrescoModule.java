package com.facebook.react.modules.fresco;

import android.content.ContextWrapper;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineConfig.Builder;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.common.ModuleDataCleaner.Cleanable;
import com.facebook.react.modules.network.CookieJarContainer;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.modules.network.OkHttpClientProvider;
import java.util.HashSet;
import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

@ReactModule(name="FrescoModule", needsEagerInit=true)
public class FrescoModule
  extends ReactContextBaseJavaModule
  implements ModuleDataCleaner.Cleanable, LifecycleEventListener
{
  public static final String NAME = "FrescoModule";
  private static boolean sHasBeenInitialized;
  private final boolean mClearOnDestroy;
  @Nullable
  private ImagePipelineConfig mConfig;
  
  public FrescoModule(ReactApplicationContext paramReactApplicationContext)
  {
    this(paramReactApplicationContext, true, null);
  }
  
  public FrescoModule(ReactApplicationContext paramReactApplicationContext, boolean paramBoolean)
  {
    this(paramReactApplicationContext, paramBoolean, null);
  }
  
  public FrescoModule(ReactApplicationContext paramReactApplicationContext, boolean paramBoolean, ImagePipelineConfig paramImagePipelineConfig)
  {
    super(paramReactApplicationContext);
    mClearOnDestroy = paramBoolean;
    mConfig = paramImagePipelineConfig;
  }
  
  private static ImagePipelineConfig getDefaultConfig(ReactContext paramReactContext)
  {
    return getDefaultConfigBuilder(paramReactContext).build();
  }
  
  public static ImagePipelineConfig.Builder getDefaultConfigBuilder(ReactContext paramReactContext)
  {
    HashSet localHashSet = new HashSet();
    localHashSet.add(new SystraceRequestListener());
    OkHttpClient localOkHttpClient = OkHttpClientProvider.createClient();
    ((CookieJarContainer)localOkHttpClient.cookieJar()).setCookieJar((CookieJar)new JavaNetCookieJar(new ForwardingCookieHandler(paramReactContext)));
    return OkHttpImagePipelineConfigFactory.newBuilder(paramReactContext.getApplicationContext(), localOkHttpClient).setNetworkFetcher(new ReactOkHttpNetworkFetcher(localOkHttpClient)).setDownsampleEnabled(false).setRequestListeners(localHashSet);
  }
  
  public static boolean hasBeenInitialized()
  {
    return sHasBeenInitialized;
  }
  
  public void clearSensitiveData()
  {
    Fresco.getImagePipeline().clearCaches();
  }
  
  public String getName()
  {
    return "FrescoModule";
  }
  
  public void initialize()
  {
    super.initialize();
    getReactApplicationContext().addLifecycleEventListener(this);
    if (!hasBeenInitialized())
    {
      if (mConfig == null) {
        mConfig = getDefaultConfig(getReactApplicationContext());
      }
      Fresco.initialize(getReactApplicationContext().getApplicationContext(), mConfig);
      sHasBeenInitialized = true;
    }
    else if (mConfig != null)
    {
      FLog.warn("ReactNative", "Fresco has already been initialized with a different config. The new Fresco configuration will be ignored!");
    }
    mConfig = null;
  }
  
  public void onHostDestroy()
  {
    if ((hasBeenInitialized()) && (mClearOnDestroy)) {
      Fresco.getImagePipeline().clearMemoryCaches();
    }
  }
  
  public void onHostPause() {}
  
  public void onHostResume() {}
}
