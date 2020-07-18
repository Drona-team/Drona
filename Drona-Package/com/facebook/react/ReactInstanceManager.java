package com.facebook.react;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.Printer;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.Assertions;
import com.facebook.infer.annotation.ThreadConfined;
import com.facebook.infer.annotation.ThreadSafe;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.CatalystInstanceImpl.Builder;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.JavaJSExecutor.Factory;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.NativeDeltaClient;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener;
import com.facebook.react.bridge.ProxyJavaScriptExecutor.Factory;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.devsupport.DevSupportManagerFactory;
import com.facebook.react.devsupport.ReactInstanceManagerDevHelper;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.modules.appregistry.AppRegistry;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import com.facebook.react.modules.fabric.ReactFabric;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.ReactRoot;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.soloader.SoLoader;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ThreadSafe
public class ReactInstanceManager
{
  private static final String PAGE_KEY = "ReactInstanceManager";
  private final Context mApplicationContext;
  private final Set<ReactRoot> mAttachedReactRoots = Collections.synchronizedSet(new HashSet());
  @Nullable
  private final NotThreadSafeBridgeIdleDebugListener mBridgeIdleDebugListener;
  @Nullable
  private final JSBundleLoader mBundleLoader;
  @Nullable
  private volatile Thread mCreateReactContextThread;
  @Nullable
  private Activity mCurrentActivity;
  @Nullable
  private volatile ReactContext mCurrentReactContext;
  @Nullable
  @ThreadConfined("UI")
  private DefaultHardwareBackBtnHandler mDefaultBackButtonImpl;
  private final DevSupportManager mDevSupportManager;
  private volatile boolean mHasStartedCreatingInitialContext = false;
  private volatile Boolean mHasStartedDestroying = Boolean.valueOf(false);
  @Nullable
  private final JSIModulePackage mJSIModulePackage;
  @Nullable
  private final String mJSMainModulePath;
  private final JavaScriptExecutorFactory mJavaScriptExecutorFactory;
  private volatile LifecycleState mLifecycleState;
  private final MemoryPressureRouter mMemoryPressureRouter;
  @Nullable
  private final NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
  private final List<ReactPackage> mPackages;
  @Nullable
  @ThreadConfined("UI")
  private ReactContextInitParams mPendingReactContextInitParams;
  private final Object mReactContextLock = new Object();
  private final Collection<ReactInstanceEventListener> mReactInstanceEventListeners = Collections.synchronizedList(new ArrayList());
  private final boolean mUseDeveloperSupport;
  private List<ViewManager> mViewManagers;
  
  ReactInstanceManager(Context paramContext, Activity paramActivity, DefaultHardwareBackBtnHandler paramDefaultHardwareBackBtnHandler, JavaScriptExecutorFactory paramJavaScriptExecutorFactory, JSBundleLoader paramJSBundleLoader, String paramString, List paramList, boolean paramBoolean1, NotThreadSafeBridgeIdleDebugListener paramNotThreadSafeBridgeIdleDebugListener, LifecycleState paramLifecycleState, UIImplementationProvider paramUIImplementationProvider, NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler, RedBoxHandler paramRedBoxHandler, boolean paramBoolean2, DevBundleDownloadListener paramDevBundleDownloadListener, int paramInt1, int paramInt2, JSIModulePackage paramJSIModulePackage, Map paramMap)
  {
    Log.d("ReactNative", "ReactInstanceManager.ctor()");
    initializeSoLoaderIfNecessary(paramContext);
    DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(paramContext);
    mApplicationContext = paramContext;
    mCurrentActivity = paramActivity;
    mDefaultBackButtonImpl = paramDefaultHardwareBackBtnHandler;
    mJavaScriptExecutorFactory = paramJavaScriptExecutorFactory;
    mBundleLoader = paramJSBundleLoader;
    mJSMainModulePath = paramString;
    mPackages = new ArrayList();
    mUseDeveloperSupport = paramBoolean1;
    Systrace.beginSection(0L, "ReactInstanceManager.initDevSupportManager");
    mDevSupportManager = DevSupportManagerFactory.create(paramContext, createDevHelperInterface(), mJSMainModulePath, paramBoolean1, paramRedBoxHandler, paramDevBundleDownloadListener, paramInt1, paramMap);
    Systrace.endSection(0L);
    mBridgeIdleDebugListener = paramNotThreadSafeBridgeIdleDebugListener;
    mLifecycleState = paramLifecycleState;
    mMemoryPressureRouter = new MemoryPressureRouter(paramContext);
    mNativeModuleCallExceptionHandler = paramNativeModuleCallExceptionHandler;
    paramContext = mPackages;
    try
    {
      PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: Use Split Packages");
      mPackages.add(new CoreModulesPackage(this, new DefaultHardwareBackBtnHandler()
      {
        public void invokeDefaultOnBackPressed()
        {
          ReactInstanceManager.this.invokeDefaultOnBackPressed();
        }
      }, paramUIImplementationProvider, paramBoolean2, paramInt2));
      if (mUseDeveloperSupport) {
        mPackages.add(new DebugCorePackage());
      }
      mPackages.addAll(paramList);
      mJSIModulePackage = paramJSIModulePackage;
      ReactChoreographer.initialize();
      if (mUseDeveloperSupport)
      {
        mDevSupportManager.startInspector();
        return;
      }
    }
    catch (Throwable paramActivity)
    {
      throw paramActivity;
    }
  }
  
  private void attachRootViewToInstance(ReactRoot paramReactRoot)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a6 = a5\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public static ReactInstanceManagerBuilder builder()
  {
    return new ReactInstanceManagerBuilder();
  }
  
  private void clearReactRoot(ReactRoot paramReactRoot)
  {
    paramReactRoot.getRootViewGroup().removeAllViews();
    paramReactRoot.getRootViewGroup().setId(-1);
  }
  
  private ReactInstanceManagerDevHelper createDevHelperInterface()
  {
    new ReactInstanceManagerDevHelper()
    {
      public Activity getCurrentActivity()
      {
        return mCurrentActivity;
      }
      
      public JavaScriptExecutorFactory getJavaScriptExecutorFactory()
      {
        return ReactInstanceManager.this.getJSExecutorFactory();
      }
      
      public void onJSBundleLoadedFromServer(NativeDeltaClient paramAnonymousNativeDeltaClient)
      {
        ReactInstanceManager.this.onJSBundleLoadedFromServer(paramAnonymousNativeDeltaClient);
      }
      
      public void onReloadWithJSDebugger(JavaJSExecutor.Factory paramAnonymousFactory)
      {
        ReactInstanceManager.this.onReloadWithJSDebugger(paramAnonymousFactory);
      }
      
      public void toggleElementInspector()
      {
        ReactInstanceManager.this.toggleElementInspector();
      }
    };
  }
  
  private ReactApplicationContext createReactContext(JavaScriptExecutor paramJavaScriptExecutor, JSBundleLoader paramJSBundleLoader)
  {
    Log.d("ReactNative", "ReactInstanceManager.createReactContext()");
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_REACT_CONTEXT_START, paramJavaScriptExecutor.getName());
    ReactApplicationContext localReactApplicationContext = new ReactApplicationContext(mApplicationContext);
    Object localObject;
    if (mNativeModuleCallExceptionHandler != null) {
      localObject = mNativeModuleCallExceptionHandler;
    } else {
      localObject = mDevSupportManager;
    }
    localReactApplicationContext.setNativeModuleCallExceptionHandler((NativeModuleCallExceptionHandler)localObject);
    NativeModuleRegistry localNativeModuleRegistry = processPackages(localReactApplicationContext, mPackages, false);
    paramJavaScriptExecutor = new CatalystInstanceImpl.Builder().setReactQueueConfigurationSpec(ReactQueueConfigurationSpec.createDefault()).setJSExecutor(paramJavaScriptExecutor).setRegistry(localNativeModuleRegistry).setJSBundleLoader(paramJSBundleLoader).setNativeModuleCallExceptionHandler((NativeModuleCallExceptionHandler)localObject);
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_START);
    Systrace.beginSection(0L, "createCatalystInstance");
    try
    {
      paramJavaScriptExecutor = paramJavaScriptExecutor.build();
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_END);
      localReactApplicationContext.initializeWithInstance(paramJavaScriptExecutor);
      if (mJSIModulePackage != null)
      {
        paramJavaScriptExecutor.addJSIModules(mJSIModulePackage.getJSIModules(localReactApplicationContext, paramJavaScriptExecutor.getJavaScriptContextHolder()));
        if (ReactFeatureFlags.useTurboModules) {
          paramJavaScriptExecutor.setTurboModuleManager(paramJavaScriptExecutor.getJSIModule(JSIModuleType.TurboModuleManager));
        }
      }
      if (mBridgeIdleDebugListener != null) {
        paramJavaScriptExecutor.addBridgeIdleDebugListener(mBridgeIdleDebugListener);
      }
      if (Systrace.isTracing(0L)) {
        paramJavaScriptExecutor.setGlobalVariable("__RCTProfileIsProfiling", "true");
      }
      ReactMarker.logMarker(ReactMarkerConstants.PRE_RUN_JS_BUNDLE_START);
      Systrace.beginSection(0L, "runJSBundle");
      paramJavaScriptExecutor.runJSBundle();
      Systrace.endSection(0L);
      return localReactApplicationContext;
    }
    catch (Throwable paramJavaScriptExecutor)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_END);
      throw paramJavaScriptExecutor;
    }
  }
  
  private void detachViewFromInstance(ReactRoot paramReactRoot, CatalystInstance paramCatalystInstance)
  {
    Log.d("ReactNative", "ReactInstanceManager.detachViewFromInstance()");
    UiThreadUtil.assertOnUiThread();
    if (paramReactRoot.getUIManagerType() == 2)
    {
      ((ReactFabric)paramCatalystInstance.getJSModule(ReactFabric.class)).unmountComponentAtNode(paramReactRoot.getRootViewTag());
      return;
    }
    ((AppRegistry)paramCatalystInstance.getJSModule(AppRegistry.class)).unmountApplicationComponentAtRootTag(paramReactRoot.getRootViewTag());
  }
  
  private JavaScriptExecutorFactory getJSExecutorFactory()
  {
    return mJavaScriptExecutorFactory;
  }
  
  private static void initializeSoLoaderIfNecessary(Context paramContext)
  {
    SoLoader.init(paramContext, false);
  }
  
  private void invokeDefaultOnBackPressed()
  {
    
    if (mDefaultBackButtonImpl != null) {
      mDefaultBackButtonImpl.invokeDefaultOnBackPressed();
    }
  }
  
  private void moveReactContextToCurrentLifecycleState()
  {
    try
    {
      if (mLifecycleState == LifecycleState.RESUMED) {
        moveToResumedLifecycleState(true);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void moveToBeforeCreateLifecycleState()
  {
    try
    {
      ReactContext localReactContext = getCurrentReactContext();
      if (localReactContext != null)
      {
        if (mLifecycleState == LifecycleState.RESUMED)
        {
          localReactContext.onHostPause();
          mLifecycleState = LifecycleState.BEFORE_RESUME;
        }
        if (mLifecycleState == LifecycleState.BEFORE_RESUME) {
          localReactContext.onHostDestroy();
        }
      }
      mLifecycleState = LifecycleState.BEFORE_CREATE;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void moveToBeforeResumeLifecycleState()
  {
    try
    {
      ReactContext localReactContext = getCurrentReactContext();
      if (localReactContext != null) {
        if (mLifecycleState == LifecycleState.BEFORE_CREATE)
        {
          localReactContext.onHostResume(mCurrentActivity);
          localReactContext.onHostPause();
        }
        else if (mLifecycleState == LifecycleState.RESUMED)
        {
          localReactContext.onHostPause();
        }
      }
      mLifecycleState = LifecycleState.BEFORE_RESUME;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void moveToResumedLifecycleState(boolean paramBoolean)
  {
    try
    {
      ReactContext localReactContext = getCurrentReactContext();
      if ((localReactContext != null) && ((paramBoolean) || (mLifecycleState == LifecycleState.BEFORE_RESUME) || (mLifecycleState == LifecycleState.BEFORE_CREATE))) {
        localReactContext.onHostResume(mCurrentActivity);
      }
      mLifecycleState = LifecycleState.RESUMED;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void onJSBundleLoadedFromServer(NativeDeltaClient paramNativeDeltaClient)
  {
    Log.d("ReactNative", "ReactInstanceManager.onJSBundleLoadedFromServer()");
    if (paramNativeDeltaClient == null) {
      paramNativeDeltaClient = JSBundleLoader.createCachedBundleFromNetworkLoader(mDevSupportManager.getSourceUrl(), mDevSupportManager.getDownloadedJSBundleFile());
    } else {
      paramNativeDeltaClient = JSBundleLoader.createDeltaFromNetworkLoader(mDevSupportManager.getSourceUrl(), paramNativeDeltaClient);
    }
    recreateReactContextInBackground(mJavaScriptExecutorFactory, paramNativeDeltaClient);
  }
  
  private void onReloadWithJSDebugger(JavaJSExecutor.Factory paramFactory)
  {
    Log.d("ReactNative", "ReactInstanceManager.onReloadWithJSDebugger()");
    recreateReactContextInBackground(new ProxyJavaScriptExecutor.Factory(paramFactory), JSBundleLoader.createRemoteDebuggerBundleLoader(mDevSupportManager.getJSBundleURLForRemoteDebugging(), mDevSupportManager.getSourceUrl()));
  }
  
  private void processPackage(ReactPackage paramReactPackage, NativeModuleRegistryBuilder paramNativeModuleRegistryBuilder)
  {
    SystraceMessage.beginSection(0L, "processPackage").put("className", paramReactPackage.getClass().getSimpleName()).flush();
    boolean bool = paramReactPackage instanceof ReactPackageLogger;
    if (bool) {
      ((ReactPackageLogger)paramReactPackage).startProcessPackage();
    }
    paramNativeModuleRegistryBuilder.processPackage(paramReactPackage);
    if (bool) {
      ((ReactPackageLogger)paramReactPackage).endProcessPackage();
    }
    SystraceMessage.endSection(0L).flush();
  }
  
  private NativeModuleRegistry processPackages(ReactApplicationContext paramReactApplicationContext, List paramList, boolean paramBoolean)
  {
    NativeModuleRegistryBuilder localNativeModuleRegistryBuilder = new NativeModuleRegistryBuilder(paramReactApplicationContext, this);
    ReactMarker.logMarker(ReactMarkerConstants.PROCESS_PACKAGES_START);
    paramReactApplicationContext = mPackages;
    for (;;)
    {
      try
      {
        paramList = paramList.iterator();
        if (paramList.hasNext())
        {
          localReactPackage = (ReactPackage)paramList.next();
          if ((paramBoolean) && (mPackages.contains(localReactPackage))) {
            continue;
          }
          Systrace.beginSection(0L, "createAndProcessCustomReactPackage");
          if (!paramBoolean) {}
        }
      }
      catch (Throwable paramList)
      {
        ReactPackage localReactPackage;
        throw paramList;
      }
      try
      {
        mPackages.add(localReactPackage);
        processPackage(localReactPackage, localNativeModuleRegistryBuilder);
        Systrace.endSection(0L);
      }
      catch (Throwable paramList) {}
    }
    Systrace.endSection(0L);
    throw paramList;
    ReactMarker.logMarker(ReactMarkerConstants.PROCESS_PACKAGES_END);
    ReactMarker.logMarker(ReactMarkerConstants.BUILD_NATIVE_MODULE_REGISTRY_START);
    Systrace.beginSection(0L, "buildNativeModuleRegistry");
    try
    {
      paramReactApplicationContext = localNativeModuleRegistryBuilder.build();
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.BUILD_NATIVE_MODULE_REGISTRY_END);
      return paramReactApplicationContext;
    }
    catch (Throwable paramReactApplicationContext)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.BUILD_NATIVE_MODULE_REGISTRY_END);
      throw paramReactApplicationContext;
    }
  }
  
  private void recreateReactContextInBackground(JavaScriptExecutorFactory paramJavaScriptExecutorFactory, JSBundleLoader paramJSBundleLoader)
  {
    Log.d("ReactNative", "ReactInstanceManager.recreateReactContextInBackground()");
    UiThreadUtil.assertOnUiThread();
    paramJavaScriptExecutorFactory = new ReactContextInitParams(paramJavaScriptExecutorFactory, paramJSBundleLoader);
    if (mCreateReactContextThread == null)
    {
      runCreateReactContextOnNewThread(paramJavaScriptExecutorFactory);
      return;
    }
    mPendingReactContextInitParams = paramJavaScriptExecutorFactory;
  }
  
  private void recreateReactContextInBackgroundFromBundleLoader()
  {
    Log.d("ReactNative", "ReactInstanceManager.recreateReactContextInBackgroundFromBundleLoader()");
    PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: load from BundleLoader");
    recreateReactContextInBackground(mJavaScriptExecutorFactory, mBundleLoader);
  }
  
  private void recreateReactContextInBackgroundInner()
  {
    Log.d("ReactNative", "ReactInstanceManager.recreateReactContextInBackgroundInner()");
    PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: recreateReactContextInBackground");
    UiThreadUtil.assertOnUiThread();
    if ((mUseDeveloperSupport) && (mJSMainModulePath != null))
    {
      final DeveloperSettings localDeveloperSettings = mDevSupportManager.getDevSettings();
      if (!Systrace.isTracing(0L))
      {
        if (mBundleLoader == null)
        {
          mDevSupportManager.handleReloadJS();
          return;
        }
        mDevSupportManager.isPackagerRunning(new PackagerStatusCallback()
        {
          public void onPackagerStatusFetched(final boolean paramAnonymousBoolean)
          {
            UiThreadUtil.runOnUiThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousBoolean)
                {
                  mDevSupportManager.handleReloadJS();
                  return;
                }
                if ((mDevSupportManager.hasUpToDateJSBundleInCache()) && (!val$devSettings.isRemoteJSDebugEnabled()))
                {
                  ReactInstanceManager.this.onJSBundleLoadedFromServer(null);
                  return;
                }
                val$devSettings.setRemoteJSDebugEnabled(false);
                ReactInstanceManager.this.recreateReactContextInBackgroundFromBundleLoader();
              }
            });
          }
        });
        return;
      }
    }
    recreateReactContextInBackgroundFromBundleLoader();
  }
  
  /* Error */
  private void runCreateReactContextOnNewThread(final ReactContextInitParams paramReactContextInitParams)
  {
    // Byte code:
    //   0: ldc -123
    //   2: ldc_w 716
    //   5: invokestatic 141	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   8: pop
    //   9: invokestatic 503	com/facebook/react/bridge/UiThreadUtil:assertOnUiThread	()V
    //   12: aload_0
    //   13: getfield 110	com/facebook/react/ReactInstanceManager:mAttachedReactRoots	Ljava/util/Set;
    //   16: astore_2
    //   17: aload_2
    //   18: monitorenter
    //   19: aload_0
    //   20: getfield 112	com/facebook/react/ReactInstanceManager:mReactContextLock	Ljava/lang/Object;
    //   23: astore_3
    //   24: aload_3
    //   25: monitorenter
    //   26: aload_0
    //   27: getfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   30: ifnull +16 -> 46
    //   33: aload_0
    //   34: aload_0
    //   35: getfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   38: invokespecial 722	com/facebook/react/ReactInstanceManager:tearDownReactContext	(Lcom/facebook/react/bridge/ReactContext;)V
    //   41: aload_0
    //   42: aconst_null
    //   43: putfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   46: aload_3
    //   47: monitorexit
    //   48: aload_2
    //   49: monitorexit
    //   50: aload_0
    //   51: new 724	java/lang/Thread
    //   54: dup
    //   55: aconst_null
    //   56: new 16	com/facebook/react/ReactInstanceManager$5
    //   59: dup
    //   60: aload_0
    //   61: aload_1
    //   62: invokespecial 726	com/facebook/react/ReactInstanceManager$5:<init>	(Lcom/facebook/react/ReactInstanceManager;Lcom/facebook/react/ReactInstanceManager$ReactContextInitParams;)V
    //   65: ldc_w 728
    //   68: invokespecial 731	java/lang/Thread:<init>	(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;)V
    //   71: putfield 275	com/facebook/react/ReactInstanceManager:mCreateReactContextThread	Ljava/lang/Thread;
    //   74: getstatic 734	com/facebook/react/bridge/ReactMarkerConstants:REACT_CONTEXT_THREAD_START	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   77: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   80: aload_0
    //   81: getfield 275	com/facebook/react/ReactInstanceManager:mCreateReactContextThread	Ljava/lang/Thread;
    //   84: invokevirtual 737	java/lang/Thread:start	()V
    //   87: return
    //   88: astore_1
    //   89: aload_3
    //   90: monitorexit
    //   91: aload_1
    //   92: athrow
    //   93: astore_1
    //   94: aload_2
    //   95: monitorexit
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	ReactInstanceManager
    //   0	98	1	paramReactContextInitParams	ReactContextInitParams
    //   16	79	2	localSet	Set
    //   23	67	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	46	88	java/lang/Throwable
    //   46	48	88	java/lang/Throwable
    //   89	91	88	java/lang/Throwable
    //   19	26	93	java/lang/Throwable
    //   48	50	93	java/lang/Throwable
    //   91	93	93	java/lang/Throwable
    //   94	96	93	java/lang/Throwable
  }
  
  /* Error */
  private void setupReactContext(final ReactApplicationContext paramReactApplicationContext)
  {
    // Byte code:
    //   0: ldc -123
    //   2: ldc_w 739
    //   5: invokestatic 141	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   8: pop
    //   9: getstatic 742	com/facebook/react/bridge/ReactMarkerConstants:PRE_SETUP_REACT_CONTEXT_END	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   12: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   15: getstatic 745	com/facebook/react/bridge/ReactMarkerConstants:SETUP_REACT_CONTEXT_START	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   18: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   21: lconst_0
    //   22: ldc_w 746
    //   25: invokestatic 174	com/facebook/systrace/Systrace:beginSection	(JLjava/lang/String;)V
    //   28: aload_0
    //   29: getfield 110	com/facebook/react/ReactInstanceManager:mAttachedReactRoots	Ljava/util/Set;
    //   32: astore_3
    //   33: aload_3
    //   34: monitorenter
    //   35: aload_0
    //   36: getfield 112	com/facebook/react/ReactInstanceManager:mReactContextLock	Ljava/lang/Object;
    //   39: astore 4
    //   41: aload 4
    //   43: monitorenter
    //   44: aload_0
    //   45: aload_1
    //   46: invokestatic 752	com/facebook/infer/annotation/Assertions:assertNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   49: checkcast 381	com/facebook/react/bridge/ReactContext
    //   52: putfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   55: aload 4
    //   57: monitorexit
    //   58: aload_1
    //   59: invokevirtual 756	com/facebook/react/bridge/ReactContext:getCatalystInstance	()Lcom/facebook/react/bridge/CatalystInstance;
    //   62: invokestatic 752	com/facebook/infer/annotation/Assertions:assertNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   65: checkcast 438	com/facebook/react/bridge/CatalystInstance
    //   68: astore 4
    //   70: aload 4
    //   72: invokeinterface 757 1 0
    //   77: aload_0
    //   78: getfield 186	com/facebook/react/ReactInstanceManager:mDevSupportManager	Lcom/facebook/react/devsupport/interfaces/DevSupportManager;
    //   81: aload_1
    //   82: invokeinterface 760 2 0
    //   87: aload_0
    //   88: getfield 200	com/facebook/react/ReactInstanceManager:mMemoryPressureRouter	Lcom/facebook/react/MemoryPressureRouter;
    //   91: aload 4
    //   93: invokevirtual 764	com/facebook/react/MemoryPressureRouter:addMemoryPressureListener	(Lcom/facebook/react/bridge/MemoryPressureListener;)V
    //   96: aload_0
    //   97: invokespecial 766	com/facebook/react/ReactInstanceManager:moveReactContextToCurrentLifecycleState	()V
    //   100: getstatic 769	com/facebook/react/bridge/ReactMarkerConstants:ATTACH_MEASURED_ROOT_VIEWS_START	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   103: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   106: aload_0
    //   107: getfield 110	com/facebook/react/ReactInstanceManager:mAttachedReactRoots	Ljava/util/Set;
    //   110: invokeinterface 772 1 0
    //   115: astore 4
    //   117: aload 4
    //   119: invokeinterface 659 1 0
    //   124: istore_2
    //   125: iload_2
    //   126: ifeq +20 -> 146
    //   129: aload_0
    //   130: aload 4
    //   132: invokeinterface 663 1 0
    //   137: checkcast 340	com/facebook/react/uimanager/ReactRoot
    //   140: invokespecial 774	com/facebook/react/ReactInstanceManager:attachRootViewToInstance	(Lcom/facebook/react/uimanager/ReactRoot;)V
    //   143: goto -26 -> 117
    //   146: getstatic 777	com/facebook/react/bridge/ReactMarkerConstants:ATTACH_MEASURED_ROOT_VIEWS_END	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   149: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   152: aload_3
    //   153: monitorexit
    //   154: aload_0
    //   155: getfield 121	com/facebook/react/ReactInstanceManager:mReactInstanceEventListeners	Ljava/util/Collection;
    //   158: invokeinterface 782 1 0
    //   163: anewarray 33	com/facebook/react/ReactInstanceManager$ReactInstanceEventListener
    //   166: astore_3
    //   167: new 22	com/facebook/react/ReactInstanceManager$6
    //   170: dup
    //   171: aload_0
    //   172: aload_0
    //   173: getfield 121	com/facebook/react/ReactInstanceManager:mReactInstanceEventListeners	Ljava/util/Collection;
    //   176: aload_3
    //   177: invokeinterface 786 2 0
    //   182: checkcast 788	[Lcom/facebook/react/ReactInstanceManager$ReactInstanceEventListener;
    //   185: aload_1
    //   186: invokespecial 791	com/facebook/react/ReactInstanceManager$6:<init>	(Lcom/facebook/react/ReactInstanceManager;[Lcom/facebook/react/ReactInstanceManager$ReactInstanceEventListener;Lcom/facebook/react/bridge/ReactApplicationContext;)V
    //   189: invokestatic 795	com/facebook/react/bridge/UiThreadUtil:runOnUiThread	(Ljava/lang/Runnable;)V
    //   192: lconst_0
    //   193: invokestatic 190	com/facebook/systrace/Systrace:endSection	(J)V
    //   196: getstatic 798	com/facebook/react/bridge/ReactMarkerConstants:SETUP_REACT_CONTEXT_END	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   199: invokestatic 423	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;)V
    //   202: aload_1
    //   203: new 24	com/facebook/react/ReactInstanceManager$7
    //   206: dup
    //   207: aload_0
    //   208: invokespecial 799	com/facebook/react/ReactInstanceManager$7:<init>	(Lcom/facebook/react/ReactInstanceManager;)V
    //   211: invokevirtual 802	com/facebook/react/bridge/ReactContext:runOnJSQueueThread	(Ljava/lang/Runnable;)V
    //   214: aload_1
    //   215: new 26	com/facebook/react/ReactInstanceManager$8
    //   218: dup
    //   219: aload_0
    //   220: invokespecial 803	com/facebook/react/ReactInstanceManager$8:<init>	(Lcom/facebook/react/ReactInstanceManager;)V
    //   223: invokevirtual 806	com/facebook/react/bridge/ReactContext:runOnNativeModulesQueueThread	(Ljava/lang/Runnable;)V
    //   226: return
    //   227: astore_1
    //   228: aload 4
    //   230: monitorexit
    //   231: aload_1
    //   232: athrow
    //   233: astore_1
    //   234: aload_3
    //   235: monitorexit
    //   236: aload_1
    //   237: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	ReactInstanceManager
    //   0	238	1	paramReactApplicationContext	ReactApplicationContext
    //   124	2	2	bool	boolean
    //   32	203	3	localObject1	Object
    //   39	190	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   44	58	227	java/lang/Throwable
    //   228	231	227	java/lang/Throwable
    //   35	44	233	java/lang/Throwable
    //   58	117	233	java/lang/Throwable
    //   117	125	233	java/lang/Throwable
    //   129	143	233	java/lang/Throwable
    //   146	154	233	java/lang/Throwable
    //   231	233	233	java/lang/Throwable
    //   234	236	233	java/lang/Throwable
  }
  
  private void tearDownReactContext(ReactContext paramReactContext)
  {
    Log.d("ReactNative", "ReactInstanceManager.tearDownReactContext()");
    UiThreadUtil.assertOnUiThread();
    if (mLifecycleState == LifecycleState.RESUMED) {
      paramReactContext.onHostPause();
    }
    Set localSet = mAttachedReactRoots;
    try
    {
      Iterator localIterator = mAttachedReactRoots.iterator();
      while (localIterator.hasNext()) {
        clearReactRoot((ReactRoot)localIterator.next());
      }
      paramReactContext.destroy();
      mDevSupportManager.onReactInstanceDestroyed(paramReactContext);
      mMemoryPressureRouter.removeMemoryPressureListener(paramReactContext.getCatalystInstance());
      return;
    }
    catch (Throwable paramReactContext)
    {
      throw paramReactContext;
    }
  }
  
  private void toggleElementInspector()
  {
    ReactContext localReactContext = getCurrentReactContext();
    if (localReactContext != null) {
      ((DeviceEventManagerModule.RCTDeviceEventEmitter)localReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("toggleElementInspector", null);
    }
  }
  
  public void addReactInstanceEventListener(ReactInstanceEventListener paramReactInstanceEventListener)
  {
    mReactInstanceEventListeners.add(paramReactInstanceEventListener);
  }
  
  public void attachRootView(ReactRoot paramReactRoot)
  {
    UiThreadUtil.assertOnUiThread();
    mAttachedReactRoots.add(paramReactRoot);
    clearReactRoot(paramReactRoot);
    ReactContext localReactContext = getCurrentReactContext();
    if ((mCreateReactContextThread == null) && (localReactContext != null)) {
      attachRootViewToInstance(paramReactRoot);
    }
  }
  
  public void createReactContextInBackground()
  {
    Log.d("ReactNative", "ReactInstanceManager.createReactContextInBackground()");
    UiThreadUtil.assertOnUiThread();
    if (!mHasStartedCreatingInitialContext)
    {
      mHasStartedCreatingInitialContext = true;
      recreateReactContextInBackgroundInner();
    }
  }
  
  public ViewManager createViewManager(String paramString)
  {
    Object localObject1 = mReactContextLock;
    try
    {
      ReactApplicationContext localReactApplicationContext = (ReactApplicationContext)getCurrentReactContext();
      if ((localReactApplicationContext != null) && (localReactApplicationContext.hasActiveCatalystInstance()))
      {
        localObject1 = mPackages;
        try
        {
          Iterator localIterator = mPackages.iterator();
          while (localIterator.hasNext())
          {
            Object localObject2 = (ReactPackage)localIterator.next();
            if ((localObject2 instanceof ViewManagerOnDemandReactPackage))
            {
              localObject2 = ((ViewManagerOnDemandReactPackage)localObject2).createViewManager(localReactApplicationContext, paramString);
              if (localObject2 != null) {
                return localObject2;
              }
            }
          }
          return null;
        }
        catch (Throwable paramString)
        {
          throw paramString;
        }
      }
      return null;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  /* Error */
  public void destroy()
  {
    // Byte code:
    //   0: invokestatic 503	com/facebook/react/bridge/UiThreadUtil:assertOnUiThread	()V
    //   3: invokestatic 208	com/facebook/debug/holder/PrinterHolder:getPrinter	()Lcom/facebook/debug/holder/Printer;
    //   6: getstatic 214	com/facebook/debug/tags/ReactDebugOverlayTags:RN_CORE	Lcom/facebook/debug/debugoverlay/model/DebugOverlayTag;
    //   9: ldc_w 849
    //   12: invokeinterface 222 3 0
    //   17: aload_0
    //   18: iconst_1
    //   19: invokestatic 129	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   22: putfield 131	com/facebook/react/ReactInstanceManager:mHasStartedDestroying	Ljava/lang/Boolean;
    //   25: aload_0
    //   26: getfield 166	com/facebook/react/ReactInstanceManager:mUseDeveloperSupport	Z
    //   29: ifeq +22 -> 51
    //   32: aload_0
    //   33: getfield 186	com/facebook/react/ReactInstanceManager:mDevSupportManager	Lcom/facebook/react/devsupport/interfaces/DevSupportManager;
    //   36: iconst_0
    //   37: invokeinterface 852 2 0
    //   42: aload_0
    //   43: getfield 186	com/facebook/react/ReactInstanceManager:mDevSupportManager	Lcom/facebook/react/devsupport/interfaces/DevSupportManager;
    //   46: invokeinterface 855 1 0
    //   51: aload_0
    //   52: invokespecial 857	com/facebook/react/ReactInstanceManager:moveToBeforeCreateLifecycleState	()V
    //   55: aload_0
    //   56: getfield 275	com/facebook/react/ReactInstanceManager:mCreateReactContextThread	Ljava/lang/Thread;
    //   59: ifnull +8 -> 67
    //   62: aload_0
    //   63: aconst_null
    //   64: putfield 275	com/facebook/react/ReactInstanceManager:mCreateReactContextThread	Ljava/lang/Thread;
    //   67: aload_0
    //   68: getfield 200	com/facebook/react/ReactInstanceManager:mMemoryPressureRouter	Lcom/facebook/react/MemoryPressureRouter;
    //   71: aload_0
    //   72: getfield 152	com/facebook/react/ReactInstanceManager:mApplicationContext	Landroid/content/Context;
    //   75: invokevirtual 859	com/facebook/react/MemoryPressureRouter:destroy	(Landroid/content/Context;)V
    //   78: aload_0
    //   79: getfield 112	com/facebook/react/ReactInstanceManager:mReactContextLock	Ljava/lang/Object;
    //   82: astore_1
    //   83: aload_1
    //   84: monitorenter
    //   85: aload_0
    //   86: getfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   89: ifnull +15 -> 104
    //   92: aload_0
    //   93: getfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   96: invokevirtual 813	com/facebook/react/bridge/ReactContext:destroy	()V
    //   99: aload_0
    //   100: aconst_null
    //   101: putfield 718	com/facebook/react/ReactInstanceManager:mCurrentReactContext	Lcom/facebook/react/bridge/ReactContext;
    //   104: aload_1
    //   105: monitorexit
    //   106: aload_0
    //   107: iconst_0
    //   108: putfield 123	com/facebook/react/ReactInstanceManager:mHasStartedCreatingInitialContext	Z
    //   111: aload_0
    //   112: aconst_null
    //   113: putfield 154	com/facebook/react/ReactInstanceManager:mCurrentActivity	Landroid/app/Activity;
    //   116: invokestatic 865	com/facebook/react/views/imagehelper/ResourceDrawableIdHelper:getInstance	()Lcom/facebook/react/views/imagehelper/ResourceDrawableIdHelper;
    //   119: invokevirtual 868	com/facebook/react/views/imagehelper/ResourceDrawableIdHelper:clear	()V
    //   122: aload_0
    //   123: iconst_0
    //   124: invokestatic 129	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   127: putfield 131	com/facebook/react/ReactInstanceManager:mHasStartedDestroying	Ljava/lang/Boolean;
    //   130: aload_0
    //   131: getfield 131	com/facebook/react/ReactInstanceManager:mHasStartedDestroying	Ljava/lang/Boolean;
    //   134: astore_1
    //   135: aload_1
    //   136: monitorenter
    //   137: aload_0
    //   138: getfield 131	com/facebook/react/ReactInstanceManager:mHasStartedDestroying	Ljava/lang/Boolean;
    //   141: invokevirtual 871	java/lang/Object:notifyAll	()V
    //   144: aload_1
    //   145: monitorexit
    //   146: return
    //   147: astore_2
    //   148: aload_1
    //   149: monitorexit
    //   150: aload_2
    //   151: athrow
    //   152: astore_2
    //   153: aload_1
    //   154: monitorexit
    //   155: aload_2
    //   156: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	this	ReactInstanceManager
    //   82	72	1	localObject	Object
    //   147	4	2	localThrowable1	Throwable
    //   152	4	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   137	146	147	java/lang/Throwable
    //   148	150	147	java/lang/Throwable
    //   85	104	152	java/lang/Throwable
    //   104	106	152	java/lang/Throwable
    //   153	155	152	java/lang/Throwable
  }
  
  public void detachRootView(ReactRoot paramReactRoot)
  {
    UiThreadUtil.assertOnUiThread();
    Set localSet = mAttachedReactRoots;
    try
    {
      if (mAttachedReactRoots.contains(paramReactRoot))
      {
        ReactContext localReactContext = getCurrentReactContext();
        mAttachedReactRoots.remove(paramReactRoot);
        if ((localReactContext != null) && (localReactContext.hasActiveCatalystInstance())) {
          detachViewFromInstance(paramReactRoot, localReactContext.getCatalystInstance());
        }
      }
      return;
    }
    catch (Throwable paramReactRoot)
    {
      throw paramReactRoot;
    }
  }
  
  public ReactContext getCurrentReactContext()
  {
    Object localObject = mReactContextLock;
    try
    {
      ReactContext localReactContext = mCurrentReactContext;
      return localReactContext;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DevSupportManager getDevSupportManager()
  {
    return mDevSupportManager;
  }
  
  public String getJsExecutorName()
  {
    return mJavaScriptExecutorFactory.toString();
  }
  
  public LifecycleState getLifecycleState()
  {
    return mLifecycleState;
  }
  
  public MemoryPressureRouter getMemoryPressureRouter()
  {
    return mMemoryPressureRouter;
  }
  
  public List getOrCreateViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_START);
    Systrace.beginSection(0L, "createAllViewManagers");
    try
    {
      List localList = mViewManagers;
      if (localList == null)
      {
        localList = mPackages;
        try
        {
          if (mViewManagers == null)
          {
            mViewManagers = new ArrayList();
            Iterator localIterator = mPackages.iterator();
            while (localIterator.hasNext())
            {
              ReactPackage localReactPackage = (ReactPackage)localIterator.next();
              mViewManagers.addAll(localReactPackage.createViewManagers(paramReactApplicationContext));
            }
            paramReactApplicationContext = mViewManagers;
            Systrace.endSection(0L);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_END);
            return paramReactApplicationContext;
          }
        }
        catch (Throwable paramReactApplicationContext)
        {
          throw paramReactApplicationContext;
        }
      }
      paramReactApplicationContext = mViewManagers;
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_END);
      return paramReactApplicationContext;
    }
    catch (Throwable paramReactApplicationContext)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_END);
      throw paramReactApplicationContext;
    }
  }
  
  public List getPackages()
  {
    return new ArrayList(mPackages);
  }
  
  public List getViewManagerNames()
  {
    Systrace.beginSection(0L, "ReactInstanceManager.getViewManagerNames");
    Object localObject1 = mReactContextLock;
    try
    {
      Object localObject2 = (ReactApplicationContext)getCurrentReactContext();
      if ((localObject2 != null) && (((ReactContext)localObject2).hasActiveCatalystInstance()))
      {
        localObject1 = mPackages;
        try
        {
          HashSet localHashSet = new HashSet();
          Iterator localIterator = mPackages.iterator();
          while (localIterator.hasNext())
          {
            Object localObject3 = (ReactPackage)localIterator.next();
            SystraceMessage.beginSection(0L, "ReactInstanceManager.getViewManagerName").put("Package", localObject3.getClass().getSimpleName()).flush();
            if ((localObject3 instanceof ViewManagerOnDemandReactPackage))
            {
              localObject3 = ((ViewManagerOnDemandReactPackage)localObject3).getViewManagerNames((ReactApplicationContext)localObject2);
              if (localObject3 != null) {
                localHashSet.addAll((Collection)localObject3);
              }
            }
            SystraceMessage.endSection(0L).flush();
          }
          Systrace.endSection(0L);
          localObject2 = new ArrayList(localHashSet);
          return localObject2;
        }
        catch (Throwable localThrowable1)
        {
          throw localThrowable1;
        }
      }
      return null;
    }
    catch (Throwable localThrowable2)
    {
      throw localThrowable2;
    }
  }
  
  public boolean hasStartedCreatingInitialContext()
  {
    return mHasStartedCreatingInitialContext;
  }
  
  public void onActivityResult(Activity paramActivity, int paramInt1, int paramInt2, Intent paramIntent)
  {
    ReactContext localReactContext = getCurrentReactContext();
    if (localReactContext != null) {
      localReactContext.onActivityResult(paramActivity, paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onBackPressed()
  {
    UiThreadUtil.assertOnUiThread();
    ReactContext localReactContext = mCurrentReactContext;
    if (localReactContext == null)
    {
      FLog.warn("ReactNative", "Instance detached from instance manager");
      invokeDefaultOnBackPressed();
      return;
    }
    ((DeviceEventManagerModule)localReactContext.getNativeModule(DeviceEventManagerModule.class)).emitHardwareBackPressed();
  }
  
  public void onHostDestroy()
  {
    
    if (mUseDeveloperSupport) {
      mDevSupportManager.setDevSupportEnabled(false);
    }
    moveToBeforeCreateLifecycleState();
    mCurrentActivity = null;
  }
  
  public void onHostDestroy(Activity paramActivity)
  {
    if (paramActivity == mCurrentActivity) {
      onHostDestroy();
    }
  }
  
  public void onHostPause()
  {
    UiThreadUtil.assertOnUiThread();
    mDefaultBackButtonImpl = null;
    if (mUseDeveloperSupport) {
      mDevSupportManager.setDevSupportEnabled(false);
    }
    moveToBeforeResumeLifecycleState();
  }
  
  public void onHostPause(Activity paramActivity)
  {
    Assertions.assertNotNull(mCurrentActivity);
    boolean bool;
    if (paramActivity == mCurrentActivity) {
      bool = true;
    } else {
      bool = false;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Pausing an activity that is not the current activity, this is incorrect! Current activity: ");
    localStringBuilder.append(mCurrentActivity.getClass().getSimpleName());
    localStringBuilder.append(" Paused activity: ");
    localStringBuilder.append(paramActivity.getClass().getSimpleName());
    Assertions.assertCondition(bool, localStringBuilder.toString());
    onHostPause();
  }
  
  public void onHostResume(final Activity paramActivity)
  {
    UiThreadUtil.assertOnUiThread();
    mCurrentActivity = paramActivity;
    if (mUseDeveloperSupport)
    {
      paramActivity = mCurrentActivity.getWindow().getDecorView();
      if (!ViewCompat.isAttachedToWindow(paramActivity)) {
        paramActivity.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
          public void onViewAttachedToWindow(View paramAnonymousView)
          {
            paramActivity.removeOnAttachStateChangeListener(this);
            mDevSupportManager.setDevSupportEnabled(true);
          }
          
          public void onViewDetachedFromWindow(View paramAnonymousView) {}
        });
      } else {
        mDevSupportManager.setDevSupportEnabled(true);
      }
    }
    moveToResumedLifecycleState(false);
  }
  
  public void onHostResume(Activity paramActivity, DefaultHardwareBackBtnHandler paramDefaultHardwareBackBtnHandler)
  {
    UiThreadUtil.assertOnUiThread();
    mDefaultBackButtonImpl = paramDefaultHardwareBackBtnHandler;
    onHostResume(paramActivity);
  }
  
  public void onNewIntent(Intent paramIntent)
  {
    UiThreadUtil.assertOnUiThread();
    ReactContext localReactContext = getCurrentReactContext();
    if (localReactContext == null)
    {
      FLog.warn("ReactNative", "Instance detached from instance manager");
      return;
    }
    String str = paramIntent.getAction();
    Uri localUri = paramIntent.getData();
    if (("android.intent.action.VIEW".equals(str)) && (localUri != null)) {
      ((DeviceEventManagerModule)localReactContext.getNativeModule(DeviceEventManagerModule.class)).emitNewIntentReceived(localUri);
    }
    localReactContext.onNewIntent(mCurrentActivity, paramIntent);
  }
  
  public void onWindowFocusChange(boolean paramBoolean)
  {
    UiThreadUtil.assertOnUiThread();
    ReactContext localReactContext = getCurrentReactContext();
    if (localReactContext != null) {
      localReactContext.onWindowFocusChange(paramBoolean);
    }
  }
  
  public void recreateReactContextInBackground()
  {
    Assertions.assertCondition(mHasStartedCreatingInitialContext, "recreateReactContextInBackground should only be called after the initial createReactContextInBackground call.");
    recreateReactContextInBackgroundInner();
  }
  
  public void removeReactInstanceEventListener(ReactInstanceEventListener paramReactInstanceEventListener)
  {
    mReactInstanceEventListeners.remove(paramReactInstanceEventListener);
  }
  
  public void showDevOptionsDialog()
  {
    UiThreadUtil.assertOnUiThread();
    mDevSupportManager.showDevOptionsDialog();
  }
  
  private class ReactContextInitParams
  {
    private final JSBundleLoader mJsBundleLoader;
    private final JavaScriptExecutorFactory mJsExecutorFactory;
    
    public ReactContextInitParams(JavaScriptExecutorFactory paramJavaScriptExecutorFactory, JSBundleLoader paramJSBundleLoader)
    {
      mJsExecutorFactory = ((JavaScriptExecutorFactory)Assertions.assertNotNull(paramJavaScriptExecutorFactory));
      mJsBundleLoader = ((JSBundleLoader)Assertions.assertNotNull(paramJSBundleLoader));
    }
    
    public JSBundleLoader getJsBundleLoader()
    {
      return mJsBundleLoader;
    }
    
    public JavaScriptExecutorFactory getJsExecutorFactory()
    {
      return mJsExecutorFactory;
    }
  }
  
  public static abstract interface ReactInstanceEventListener
  {
    public abstract void onReactContextInitialized(ReactContext paramReactContext);
  }
}
