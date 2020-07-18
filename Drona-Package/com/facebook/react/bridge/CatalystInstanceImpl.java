package com.facebook.react.bridge;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.queue.MessageQueueThread;
import com.facebook.react.bridge.queue.QueueThreadExceptionHandler;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.bridge.queue.ReactQueueConfigurationImpl;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.turbomodule.core.JSCallInvokerHolderImpl;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry;
import com.facebook.systrace.TraceListener;
import com.facebook.upgrade.HybridData;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@DoNotStrip
public class CatalystInstanceImpl
  implements CatalystInstance
{
  private static final AtomicInteger sNextInstanceIdForTrace = new AtomicInteger(1);
  private volatile boolean mAcceptCalls;
  private final CopyOnWriteArrayList<NotThreadSafeBridgeIdleDebugListener> mBridgeIdleListeners;
  private volatile boolean mDestroyed;
  private final HybridData mHybridData;
  private boolean mInitialized;
  private boolean mJSBundleHasLoaded;
  private final JSBundleLoader mJSBundleLoader;
  private final ArrayList<PendingJSCall> mJSCallsPendingInit;
  private final Object mJSCallsPendingInitLock;
  private final JSIModuleRegistry mJSIModuleRegistry;
  private final JavaScriptModuleRegistry mJSModuleRegistry;
  private JavaScriptContextHolder mJavaScriptContextHolder;
  private final String mJsPendingCallsTitleForTrace;
  private final NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
  private final NativeModuleRegistry mNativeModuleRegistry;
  private final MessageQueueThread mNativeModulesQueueThread;
  private final AtomicInteger mPendingJSCalls = new AtomicInteger(0);
  private final ReactQueueConfigurationImpl mReactQueueConfiguration;
  @Nullable
  private String mSourceURL;
  private final TraceListener mTraceListener;
  @Nullable
  private TurboModuleRegistry mTurboModuleRegistry;
  
  static {}
  
  private CatalystInstanceImpl(ReactQueueConfigurationSpec paramReactQueueConfigurationSpec, JavaScriptExecutor paramJavaScriptExecutor, NativeModuleRegistry paramNativeModuleRegistry, JSBundleLoader paramJSBundleLoader, NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("pending_js_calls_instance");
    localStringBuilder.append(sNextInstanceIdForTrace.getAndIncrement());
    mJsPendingCallsTitleForTrace = localStringBuilder.toString();
    mDestroyed = false;
    mJSCallsPendingInit = new ArrayList();
    mJSCallsPendingInitLock = new Object();
    mJSIModuleRegistry = new JSIModuleRegistry();
    mInitialized = false;
    mAcceptCalls = false;
    mTurboModuleRegistry = null;
    Log.d("ReactNative", "Initializing React Xplat Bridge.");
    com.facebook.systrace.Systrace.beginSection(0L, "createCatalystInstanceImpl");
    mHybridData = initHybrid();
    mReactQueueConfiguration = ReactQueueConfigurationImpl.create(paramReactQueueConfigurationSpec, new NativeExceptionHandler(null));
    mBridgeIdleListeners = new CopyOnWriteArrayList();
    mNativeModuleRegistry = paramNativeModuleRegistry;
    mJSModuleRegistry = new JavaScriptModuleRegistry();
    mJSBundleLoader = paramJSBundleLoader;
    mNativeModuleCallExceptionHandler = paramNativeModuleCallExceptionHandler;
    mNativeModulesQueueThread = mReactQueueConfiguration.getNativeModulesQueueThread();
    mTraceListener = new JSProfilerTraceListener(this);
    com.facebook.systrace.Systrace.endSection(0L);
    Log.d("ReactNative", "Initializing React Xplat Bridge before initializeBridge");
    com.facebook.systrace.Systrace.beginSection(0L, "initializeCxxBridge");
    initializeBridge(new BridgeCallback(this), paramJavaScriptExecutor, mReactQueueConfiguration.getJSQueueThread(), mNativeModulesQueueThread, mNativeModuleRegistry.getJavaModules(this), mNativeModuleRegistry.getCxxModules());
    Log.d("ReactNative", "Initializing React Xplat Bridge after initializeBridge");
    com.facebook.systrace.Systrace.endSection(0L);
    mJavaScriptContextHolder = new JavaScriptContextHolder(getJavaScriptContext());
  }
  
  private void decrementPendingJSCalls()
  {
    int j = mPendingJSCalls.decrementAndGet();
    int i;
    if (j == 0) {
      i = 1;
    } else {
      i = 0;
    }
    com.facebook.systrace.Systrace.traceCounter(0L, mJsPendingCallsTitleForTrace, j);
    if ((i != 0) && (!mBridgeIdleListeners.isEmpty())) {
      mNativeModulesQueueThread.runOnQueue(new Runnable()
      {
        public void run()
        {
          Iterator localIterator = mBridgeIdleListeners.iterator();
          while (localIterator.hasNext()) {
            ((NotThreadSafeBridgeIdleDebugListener)localIterator.next()).onTransitionToBridgeIdle();
          }
        }
      });
    }
  }
  
  private native long getJavaScriptContext();
  
  private String getNameFromAnnotation(Class paramClass)
  {
    Object localObject = (ReactModule)paramClass.getAnnotation(ReactModule.class);
    if (localObject != null) {
      return ((ReactModule)localObject).name();
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Could not find @ReactModule annotation in ");
    ((StringBuilder)localObject).append(paramClass.getCanonicalName());
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  private void incrementPendingJSCalls()
  {
    int j = mPendingJSCalls.getAndIncrement();
    int i;
    if (j == 0) {
      i = 1;
    } else {
      i = 0;
    }
    com.facebook.systrace.Systrace.traceCounter(0L, mJsPendingCallsTitleForTrace, j + 1);
    if ((i != 0) && (!mBridgeIdleListeners.isEmpty())) {
      mNativeModulesQueueThread.runOnQueue(new Runnable()
      {
        public void run()
        {
          Iterator localIterator = mBridgeIdleListeners.iterator();
          while (localIterator.hasNext()) {
            ((NotThreadSafeBridgeIdleDebugListener)localIterator.next()).onTransitionToBridgeBusy();
          }
        }
      });
    }
  }
  
  private static native HybridData initHybrid();
  
  private native void initializeBridge(ReactCallback paramReactCallback, JavaScriptExecutor paramJavaScriptExecutor, MessageQueueThread paramMessageQueueThread1, MessageQueueThread paramMessageQueueThread2, Collection paramCollection1, Collection paramCollection2);
  
  private native void jniCallJSCallback(int paramInt, NativeArray paramNativeArray);
  
  private native void jniCallJSFunction(String paramString1, String paramString2, NativeArray paramNativeArray);
  
  private native void jniExtendNativeModules(Collection paramCollection1, Collection paramCollection2);
  
  private native void jniHandleMemoryPressure(int paramInt);
  
  private native void jniLoadScriptFromAssets(AssetManager paramAssetManager, String paramString, boolean paramBoolean);
  
  private native void jniLoadScriptFromDeltaBundle(String paramString, NativeDeltaClient paramNativeDeltaClient, boolean paramBoolean);
  
  private native void jniLoadScriptFromFile(String paramString1, String paramString2, boolean paramBoolean);
  
  private native void jniRegisterSegment(int paramInt, String paramString);
  
  private native void jniSetSourceURL(String paramString);
  
  private void onNativeException(Exception paramException)
  {
    mNativeModuleCallExceptionHandler.handleException(paramException);
    mReactQueueConfiguration.getUIQueueThread().runOnQueue(new Runnable()
    {
      public void run()
      {
        destroy();
      }
    });
  }
  
  public void addBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener paramNotThreadSafeBridgeIdleDebugListener)
  {
    mBridgeIdleListeners.add(paramNotThreadSafeBridgeIdleDebugListener);
  }
  
  public void addJSIModules(List paramList)
  {
    mJSIModuleRegistry.registerModules(paramList);
  }
  
  public void callFunction(PendingJSCall paramPendingJSCall)
  {
    Object localObject;
    if (mDestroyed)
    {
      paramPendingJSCall = paramPendingJSCall.toString();
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Calling JS function after bridge has been destroyed: ");
      ((StringBuilder)localObject).append(paramPendingJSCall);
      FLog.warn("ReactNative", ((StringBuilder)localObject).toString());
      return;
    }
    if (!mAcceptCalls)
    {
      localObject = mJSCallsPendingInitLock;
      try
      {
        if (!mAcceptCalls)
        {
          mJSCallsPendingInit.add(paramPendingJSCall);
          return;
        }
      }
      catch (Throwable paramPendingJSCall)
      {
        throw paramPendingJSCall;
      }
    }
    paramPendingJSCall.call(this);
  }
  
  public void callFunction(String paramString1, String paramString2, NativeArray paramNativeArray)
  {
    callFunction(new PendingJSCall(paramString1, paramString2, paramNativeArray));
  }
  
  public void destroy()
  {
    Log.d("ReactNative", "CatalystInstanceImpl.destroy() start");
    UiThreadUtil.assertOnUiThread();
    if (mDestroyed) {
      return;
    }
    ReactMarker.logMarker(ReactMarkerConstants.DESTROY_CATALYST_INSTANCE_START);
    mDestroyed = true;
    mNativeModulesQueueThread.runOnQueue(new Runnable()
    {
      public void run()
      {
        mNativeModuleRegistry.notifyJSInstanceDestroy();
        mJSIModuleRegistry.notifyJSInstanceDestroy();
        Object localObject = mPendingJSCalls;
        int i = 0;
        if (((AtomicInteger)localObject).getAndSet(0) == 0) {
          i = 1;
        }
        if (!mBridgeIdleListeners.isEmpty())
        {
          localObject = mBridgeIdleListeners.iterator();
          while (((Iterator)localObject).hasNext())
          {
            NotThreadSafeBridgeIdleDebugListener localNotThreadSafeBridgeIdleDebugListener = (NotThreadSafeBridgeIdleDebugListener)((Iterator)localObject).next();
            if (i == 0) {
              localNotThreadSafeBridgeIdleDebugListener.onTransitionToBridgeIdle();
            }
            localNotThreadSafeBridgeIdleDebugListener.onBridgeDestroyed();
          }
        }
        if (ReactFeatureFlags.useTurboModules) {
          localObject = mJSIModuleRegistry.getModule(JSIModuleType.TurboModuleManager);
        } else {
          localObject = null;
        }
        getReactQueueConfiguration().getJSQueueThread().runOnQueue(new Runnable()
        {
          public void run()
          {
            if (val$turboModuleManager != null) {
              val$turboModuleManager.onCatalystInstanceDestroy();
            }
            getReactQueueConfiguration().getUIQueueThread().runOnQueue(new Runnable()
            {
              public void run()
              {
                AsyncTask.execute(new Runnable()
                {
                  public void run()
                  {
                    mJavaScriptContextHolder.clear();
                    mHybridData.resetNative();
                    getReactQueueConfiguration().destroy();
                    Log.d("ReactNative", "CatalystInstanceImpl.destroy() end");
                    ReactMarker.logMarker(ReactMarkerConstants.DESTROY_CATALYST_INSTANCE_END);
                  }
                });
              }
            });
          }
        });
      }
    });
    com.facebook.systrace.Systrace.unregisterListener(mTraceListener);
  }
  
  public void extendNativeModules(NativeModuleRegistry paramNativeModuleRegistry)
  {
    mNativeModuleRegistry.registerModules(paramNativeModuleRegistry);
    jniExtendNativeModules(paramNativeModuleRegistry.getJavaModules(this), paramNativeModuleRegistry.getCxxModules());
  }
  
  public native JSCallInvokerHolderImpl getJSCallInvokerHolder();
  
  public JSIModule getJSIModule(JSIModuleType paramJSIModuleType)
  {
    return mJSIModuleRegistry.getModule(paramJSIModuleType);
  }
  
  public JavaScriptModule getJSModule(Class paramClass)
  {
    return mJSModuleRegistry.getJavaScriptModule(this, paramClass);
  }
  
  public JavaScriptContextHolder getJavaScriptContextHolder()
  {
    return mJavaScriptContextHolder;
  }
  
  public NativeModule getNativeModule(Class paramClass)
  {
    return getNativeModule(getNameFromAnnotation(paramClass));
  }
  
  public NativeModule getNativeModule(String paramString)
  {
    if (mTurboModuleRegistry != null)
    {
      TurboModule localTurboModule = mTurboModuleRegistry.getModule(paramString);
      if (localTurboModule != null) {
        return (NativeModule)localTurboModule;
      }
    }
    return mNativeModuleRegistry.getModule(paramString);
  }
  
  public Collection getNativeModules()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(mNativeModuleRegistry.getAllModules());
    if (mTurboModuleRegistry != null)
    {
      Iterator localIterator = mTurboModuleRegistry.getModules().iterator();
      while (localIterator.hasNext()) {
        localArrayList.add((NativeModule)localIterator.next());
      }
    }
    return localArrayList;
  }
  
  public ReactQueueConfiguration getReactQueueConfiguration()
  {
    return mReactQueueConfiguration;
  }
  
  public String getSourceURL()
  {
    return mSourceURL;
  }
  
  public void handleMemoryPressure(int paramInt)
  {
    if (mDestroyed) {
      return;
    }
    jniHandleMemoryPressure(paramInt);
  }
  
  public boolean hasNativeModule(Class paramClass)
  {
    paramClass = getNameFromAnnotation(paramClass);
    if ((mTurboModuleRegistry != null) && (mTurboModuleRegistry.hasModule(paramClass))) {
      return true;
    }
    return mNativeModuleRegistry.hasModule(paramClass);
  }
  
  public boolean hasRunJSBundle()
  {
    Object localObject = mJSCallsPendingInitLock;
    for (;;)
    {
      try
      {
        if ((mJSBundleHasLoaded) && (mAcceptCalls))
        {
          bool = true;
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public void initialize()
  {
    Log.d("ReactNative", "CatalystInstanceImpl.initialize()");
    Assertions.assertCondition(mInitialized ^ true, "This catalyst instance has already been initialized");
    Assertions.assertCondition(mAcceptCalls, "RunJSBundle hasn't completed.");
    mInitialized = true;
    mNativeModulesQueueThread.runOnQueue(new Runnable()
    {
      public void run()
      {
        mNativeModuleRegistry.notifyJSInstanceInitialized();
      }
    });
  }
  
  public void invokeCallback(int paramInt, NativeArrayInterface paramNativeArrayInterface)
  {
    if (mDestroyed)
    {
      FLog.warn("ReactNative", "Invoking JS callback after bridge has been destroyed.");
      return;
    }
    jniCallJSCallback(paramInt, (NativeArray)paramNativeArrayInterface);
  }
  
  public boolean isDestroyed()
  {
    return mDestroyed;
  }
  
  public void loadScriptFromAssets(AssetManager paramAssetManager, String paramString, boolean paramBoolean)
  {
    mSourceURL = paramString;
    jniLoadScriptFromAssets(paramAssetManager, paramString, paramBoolean);
  }
  
  public void loadScriptFromDeltaBundle(String paramString, NativeDeltaClient paramNativeDeltaClient, boolean paramBoolean)
  {
    mSourceURL = paramString;
    jniLoadScriptFromDeltaBundle(paramString, paramNativeDeltaClient, paramBoolean);
  }
  
  public void loadScriptFromFile(String paramString1, String paramString2, boolean paramBoolean)
  {
    mSourceURL = paramString2;
    jniLoadScriptFromFile(paramString1, paramString2, paramBoolean);
  }
  
  public void registerSegment(int paramInt, String paramString)
  {
    jniRegisterSegment(paramInt, paramString);
  }
  
  public void removeBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener paramNotThreadSafeBridgeIdleDebugListener)
  {
    mBridgeIdleListeners.remove(paramNotThreadSafeBridgeIdleDebugListener);
  }
  
  public void runJSBundle()
  {
    Log.d("ReactNative", "CatalystInstanceImpl.runJSBundle()");
    Assertions.assertCondition(mJSBundleHasLoaded ^ true, "JS bundle was already loaded!");
    mJSBundleLoader.loadScript(this);
    Object localObject = mJSCallsPendingInitLock;
    try
    {
      mAcceptCalls = true;
      Iterator localIterator = mJSCallsPendingInit.iterator();
      while (localIterator.hasNext()) {
        ((PendingJSCall)localIterator.next()).call(this);
      }
      mJSCallsPendingInit.clear();
      mJSBundleHasLoaded = true;
      com.facebook.systrace.Systrace.registerListener(mTraceListener);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public native void setGlobalVariable(String paramString1, String paramString2);
  
  public void setSourceURLs(String paramString1, String paramString2)
  {
    mSourceURL = paramString1;
    jniSetSourceURL(paramString2);
  }
  
  public void setTurboModuleManager(JSIModule paramJSIModule)
  {
    mTurboModuleRegistry = ((TurboModuleRegistry)paramJSIModule);
  }
  
  private static class BridgeCallback
    implements ReactCallback
  {
    private final WeakReference<CatalystInstanceImpl> mOuter;
    
    BridgeCallback(CatalystInstanceImpl paramCatalystInstanceImpl)
    {
      mOuter = new WeakReference(paramCatalystInstanceImpl);
    }
    
    public void decrementPendingJSCalls()
    {
      CatalystInstanceImpl localCatalystInstanceImpl = (CatalystInstanceImpl)mOuter.get();
      if (localCatalystInstanceImpl != null) {
        localCatalystInstanceImpl.decrementPendingJSCalls();
      }
    }
    
    public void incrementPendingJSCalls()
    {
      CatalystInstanceImpl localCatalystInstanceImpl = (CatalystInstanceImpl)mOuter.get();
      if (localCatalystInstanceImpl != null) {
        localCatalystInstanceImpl.incrementPendingJSCalls();
      }
    }
    
    public void onBatchComplete()
    {
      CatalystInstanceImpl localCatalystInstanceImpl = (CatalystInstanceImpl)mOuter.get();
      if (localCatalystInstanceImpl != null) {
        mNativeModuleRegistry.onBatchComplete();
      }
    }
  }
  
  public static class Builder
  {
    @Nullable
    private JSBundleLoader mJSBundleLoader;
    @Nullable
    private JavaScriptExecutor mJSExecutor;
    @Nullable
    private NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
    @Nullable
    private ReactQueueConfigurationSpec mReactQueueConfigurationSpec;
    @Nullable
    private NativeModuleRegistry mRegistry;
    
    public Builder() {}
    
    public CatalystInstanceImpl build()
    {
      return new CatalystInstanceImpl((ReactQueueConfigurationSpec)Assertions.assertNotNull(mReactQueueConfigurationSpec), (JavaScriptExecutor)Assertions.assertNotNull(mJSExecutor), (NativeModuleRegistry)Assertions.assertNotNull(mRegistry), (JSBundleLoader)Assertions.assertNotNull(mJSBundleLoader), (NativeModuleCallExceptionHandler)Assertions.assertNotNull(mNativeModuleCallExceptionHandler), null);
    }
    
    public Builder setJSBundleLoader(JSBundleLoader paramJSBundleLoader)
    {
      mJSBundleLoader = paramJSBundleLoader;
      return this;
    }
    
    public Builder setJSExecutor(JavaScriptExecutor paramJavaScriptExecutor)
    {
      mJSExecutor = paramJavaScriptExecutor;
      return this;
    }
    
    public Builder setNativeModuleCallExceptionHandler(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
    {
      mNativeModuleCallExceptionHandler = paramNativeModuleCallExceptionHandler;
      return this;
    }
    
    public Builder setReactQueueConfigurationSpec(ReactQueueConfigurationSpec paramReactQueueConfigurationSpec)
    {
      mReactQueueConfigurationSpec = paramReactQueueConfigurationSpec;
      return this;
    }
    
    public Builder setRegistry(NativeModuleRegistry paramNativeModuleRegistry)
    {
      mRegistry = paramNativeModuleRegistry;
      return this;
    }
  }
  
  private static class JSProfilerTraceListener
    implements TraceListener
  {
    private final WeakReference<CatalystInstanceImpl> mOuter;
    
    public JSProfilerTraceListener(CatalystInstanceImpl paramCatalystInstanceImpl)
    {
      mOuter = new WeakReference(paramCatalystInstanceImpl);
    }
    
    public void onTraceStarted()
    {
      CatalystInstanceImpl localCatalystInstanceImpl = (CatalystInstanceImpl)mOuter.get();
      if (localCatalystInstanceImpl != null) {
        ((Systrace)localCatalystInstanceImpl.getJSModule(Systrace.class)).setEnabled(true);
      }
    }
    
    public void onTraceStopped()
    {
      CatalystInstanceImpl localCatalystInstanceImpl = (CatalystInstanceImpl)mOuter.get();
      if (localCatalystInstanceImpl != null) {
        ((Systrace)localCatalystInstanceImpl.getJSModule(Systrace.class)).setEnabled(false);
      }
    }
  }
  
  private class NativeExceptionHandler
    implements QueueThreadExceptionHandler
  {
    private NativeExceptionHandler() {}
    
    public void handleException(Exception paramException)
    {
      CatalystInstanceImpl.this.onNativeException(paramException);
    }
  }
  
  public static class PendingJSCall
  {
    @Nullable
    public NativeArray mArguments;
    public String mMethod;
    public String mModule;
    
    public PendingJSCall(String paramString1, String paramString2, NativeArray paramNativeArray)
    {
      mModule = paramString1;
      mMethod = paramString2;
      mArguments = paramNativeArray;
    }
    
    void call(CatalystInstanceImpl paramCatalystInstanceImpl)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(mModule);
      localStringBuilder.append(".");
      localStringBuilder.append(mMethod);
      localStringBuilder.append("(");
      String str;
      if (mArguments == null) {
        str = "";
      } else {
        str = mArguments.toString();
      }
      localStringBuilder.append(str);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
  }
}
