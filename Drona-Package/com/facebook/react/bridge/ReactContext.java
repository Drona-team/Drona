package com.facebook.react.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.queue.MessageQueueThread;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.common.LifecycleState;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReactContext
  extends ContextWrapper
{
  private static final String EARLY_JS_ACCESS_EXCEPTION_MESSAGE = "Tried to access a JS module before the React instance was fully set up. Calls to ReactContext#getJSModule should only happen once initialize() has been called on your native module.";
  private final CopyOnWriteArraySet<ActivityEventListener> mActivityEventListeners = new CopyOnWriteArraySet();
  @Nullable
  private CatalystInstance mCatalystInstance;
  @Nullable
  private WeakReference<Activity> mCurrentActivity;
  @Nullable
  private NativeModuleCallExceptionHandler mExceptionHandlerWrapper;
  @Nullable
  private LayoutInflater mInflater;
  @Nullable
  private MessageQueueThread mJSMessageQueueThread;
  private final CopyOnWriteArraySet<LifecycleEventListener> mLifecycleEventListeners = new CopyOnWriteArraySet();
  private LifecycleState mLifecycleState = LifecycleState.BEFORE_CREATE;
  @Nullable
  private NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
  @Nullable
  private MessageQueueThread mNativeModulesMessageQueueThread;
  @Nullable
  private MessageQueueThread mUiMessageQueueThread;
  private final CopyOnWriteArraySet<WindowFocusChangeListener> mWindowFocusEventListeners = new CopyOnWriteArraySet();
  
  public ReactContext(Context paramContext)
  {
    super(paramContext);
  }
  
  public void addActivityEventListener(ActivityEventListener paramActivityEventListener)
  {
    mActivityEventListeners.add(paramActivityEventListener);
  }
  
  public void addLifecycleEventListener(final LifecycleEventListener paramLifecycleEventListener)
  {
    mLifecycleEventListeners.add(paramLifecycleEventListener);
    if (hasActiveCatalystInstance()) {
      switch (2.$SwitchMap$com$facebook$react$common$LifecycleState[mLifecycleState.ordinal()])
      {
      default: 
        throw new RuntimeException("Unhandled lifecycle state.");
      case 3: 
        runOnUiQueueThread(new Runnable()
        {
          public void run()
          {
            if (!mLifecycleEventListeners.contains(paramLifecycleEventListener)) {
              return;
            }
            try
            {
              paramLifecycleEventListener.onHostResume();
              return;
            }
            catch (RuntimeException localRuntimeException)
            {
              handleException(localRuntimeException);
            }
          }
        });
      }
    }
  }
  
  public void addWindowFocusChangeListener(WindowFocusChangeListener paramWindowFocusChangeListener)
  {
    mWindowFocusEventListeners.add(paramWindowFocusChangeListener);
  }
  
  public void assertOnJSQueueThread()
  {
    ((MessageQueueThread)Assertions.assertNotNull(mJSMessageQueueThread)).assertIsOnThread();
  }
  
  public void assertOnNativeModulesQueueThread()
  {
    ((MessageQueueThread)Assertions.assertNotNull(mNativeModulesMessageQueueThread)).assertIsOnThread();
  }
  
  public void assertOnNativeModulesQueueThread(String paramString)
  {
    ((MessageQueueThread)Assertions.assertNotNull(mNativeModulesMessageQueueThread)).assertIsOnThread(paramString);
  }
  
  public void assertOnUiQueueThread()
  {
    ((MessageQueueThread)Assertions.assertNotNull(mUiMessageQueueThread)).assertIsOnThread();
  }
  
  public void destroy()
  {
    
    if (mCatalystInstance != null) {
      mCatalystInstance.destroy();
    }
  }
  
  public CatalystInstance getCatalystInstance()
  {
    return (CatalystInstance)Assertions.assertNotNull(mCatalystInstance);
  }
  
  public Activity getCurrentActivity()
  {
    if (mCurrentActivity == null) {
      return null;
    }
    return (Activity)mCurrentActivity.get();
  }
  
  public NativeModuleCallExceptionHandler getExceptionHandler()
  {
    if (mExceptionHandlerWrapper == null) {
      mExceptionHandlerWrapper = new ExceptionHandlerWrapper();
    }
    return mExceptionHandlerWrapper;
  }
  
  public JavaScriptModule getJSModule(Class paramClass)
  {
    if (mCatalystInstance != null) {
      return mCatalystInstance.getJSModule(paramClass);
    }
    throw new RuntimeException("Tried to access a JS module before the React instance was fully set up. Calls to ReactContext#getJSModule should only happen once initialize() has been called on your native module.");
  }
  
  public JavaScriptContextHolder getJavaScriptContextHolder()
  {
    return mCatalystInstance.getJavaScriptContextHolder();
  }
  
  public LifecycleState getLifecycleState()
  {
    return mLifecycleState;
  }
  
  public NativeModule getNativeModule(Class paramClass)
  {
    if (mCatalystInstance != null) {
      return mCatalystInstance.getNativeModule(paramClass);
    }
    throw new RuntimeException("Trying to call native module before CatalystInstance has been set!");
  }
  
  public Object getSystemService(String paramString)
  {
    if ("layout_inflater".equals(paramString))
    {
      if (mInflater == null) {
        mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
      }
      return mInflater;
    }
    return getBaseContext().getSystemService(paramString);
  }
  
  public void handleException(Exception paramException)
  {
    if ((mCatalystInstance != null) && (!mCatalystInstance.isDestroyed()) && (mNativeModuleCallExceptionHandler != null))
    {
      mNativeModuleCallExceptionHandler.handleException(paramException);
      return;
    }
    throw new RuntimeException(paramException);
  }
  
  public boolean hasActiveCatalystInstance()
  {
    return (mCatalystInstance != null) && (!mCatalystInstance.isDestroyed());
  }
  
  public boolean hasCatalystInstance()
  {
    return mCatalystInstance != null;
  }
  
  public boolean hasCurrentActivity()
  {
    return (mCurrentActivity != null) && (mCurrentActivity.get() != null);
  }
  
  public boolean hasNativeModule(Class paramClass)
  {
    if (mCatalystInstance != null) {
      return mCatalystInstance.hasNativeModule(paramClass);
    }
    throw new RuntimeException("Trying to call native module before CatalystInstance has been set!");
  }
  
  public void initializeMessageQueueThreads(ReactQueueConfiguration paramReactQueueConfiguration)
  {
    if ((mUiMessageQueueThread == null) && (mNativeModulesMessageQueueThread == null) && (mJSMessageQueueThread == null))
    {
      mUiMessageQueueThread = paramReactQueueConfiguration.getUIQueueThread();
      mNativeModulesMessageQueueThread = paramReactQueueConfiguration.getNativeModulesQueueThread();
      mJSMessageQueueThread = paramReactQueueConfiguration.getJSQueueThread();
      return;
    }
    throw new IllegalStateException("Message queue threads already initialized");
  }
  
  public void initializeWithInstance(CatalystInstance paramCatalystInstance)
  {
    if (paramCatalystInstance != null)
    {
      if (mCatalystInstance == null)
      {
        mCatalystInstance = paramCatalystInstance;
        initializeMessageQueueThreads(paramCatalystInstance.getReactQueueConfiguration());
        return;
      }
      throw new IllegalStateException("ReactContext has been already initialized");
    }
    throw new IllegalArgumentException("CatalystInstance cannot be null.");
  }
  
  public boolean isOnJSQueueThread()
  {
    return ((MessageQueueThread)Assertions.assertNotNull(mJSMessageQueueThread)).isOnThread();
  }
  
  public boolean isOnNativeModulesQueueThread()
  {
    return ((MessageQueueThread)Assertions.assertNotNull(mNativeModulesMessageQueueThread)).isOnThread();
  }
  
  public boolean isOnUiQueueThread()
  {
    return ((MessageQueueThread)Assertions.assertNotNull(mUiMessageQueueThread)).isOnThread();
  }
  
  public void onActivityResult(Activity paramActivity, int paramInt1, int paramInt2, Intent paramIntent)
  {
    Iterator localIterator = mActivityEventListeners.iterator();
    while (localIterator.hasNext())
    {
      ActivityEventListener localActivityEventListener = (ActivityEventListener)localIterator.next();
      try
      {
        localActivityEventListener.onActivityResult(paramActivity, paramInt1, paramInt2, paramIntent);
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
  }
  
  public void onHostDestroy()
  {
    UiThreadUtil.assertOnUiThread();
    mLifecycleState = LifecycleState.BEFORE_CREATE;
    Iterator localIterator = mLifecycleEventListeners.iterator();
    while (localIterator.hasNext())
    {
      LifecycleEventListener localLifecycleEventListener = (LifecycleEventListener)localIterator.next();
      try
      {
        localLifecycleEventListener.onHostDestroy();
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
    mCurrentActivity = null;
  }
  
  public void onHostPause()
  {
    mLifecycleState = LifecycleState.BEFORE_RESUME;
    ReactMarker.logMarker(ReactMarkerConstants.ON_HOST_PAUSE_START);
    Iterator localIterator = mLifecycleEventListeners.iterator();
    while (localIterator.hasNext())
    {
      LifecycleEventListener localLifecycleEventListener = (LifecycleEventListener)localIterator.next();
      try
      {
        localLifecycleEventListener.onHostPause();
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
    ReactMarker.logMarker(ReactMarkerConstants.ON_HOST_PAUSE_END);
  }
  
  public void onHostResume(Activity paramActivity)
  {
    mLifecycleState = LifecycleState.RESUMED;
    mCurrentActivity = new WeakReference(paramActivity);
    ReactMarker.logMarker(ReactMarkerConstants.ON_HOST_RESUME_START);
    paramActivity = mLifecycleEventListeners.iterator();
    while (paramActivity.hasNext())
    {
      LifecycleEventListener localLifecycleEventListener = (LifecycleEventListener)paramActivity.next();
      try
      {
        localLifecycleEventListener.onHostResume();
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
    ReactMarker.logMarker(ReactMarkerConstants.ON_HOST_RESUME_END);
  }
  
  public void onNewIntent(Activity paramActivity, Intent paramIntent)
  {
    UiThreadUtil.assertOnUiThread();
    mCurrentActivity = new WeakReference(paramActivity);
    paramActivity = mActivityEventListeners.iterator();
    while (paramActivity.hasNext())
    {
      ActivityEventListener localActivityEventListener = (ActivityEventListener)paramActivity.next();
      try
      {
        localActivityEventListener.onNewIntent(paramIntent);
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
  }
  
  public void onWindowFocusChange(boolean paramBoolean)
  {
    UiThreadUtil.assertOnUiThread();
    Iterator localIterator = mWindowFocusEventListeners.iterator();
    while (localIterator.hasNext())
    {
      WindowFocusChangeListener localWindowFocusChangeListener = (WindowFocusChangeListener)localIterator.next();
      try
      {
        localWindowFocusChangeListener.onWindowFocusChange(paramBoolean);
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
  }
  
  public void removeActivityEventListener(ActivityEventListener paramActivityEventListener)
  {
    mActivityEventListeners.remove(paramActivityEventListener);
  }
  
  public void removeLifecycleEventListener(LifecycleEventListener paramLifecycleEventListener)
  {
    mLifecycleEventListeners.remove(paramLifecycleEventListener);
  }
  
  public void removeWindowFocusChangeListener(WindowFocusChangeListener paramWindowFocusChangeListener)
  {
    mWindowFocusEventListeners.remove(paramWindowFocusChangeListener);
  }
  
  public void resetPerfStats()
  {
    if (mNativeModulesMessageQueueThread != null) {
      mNativeModulesMessageQueueThread.resetPerfStats();
    }
    if (mJSMessageQueueThread != null) {
      mJSMessageQueueThread.resetPerfStats();
    }
  }
  
  public void runOnJSQueueThread(Runnable paramRunnable)
  {
    ((MessageQueueThread)Assertions.assertNotNull(mJSMessageQueueThread)).runOnQueue(paramRunnable);
  }
  
  public void runOnNativeModulesQueueThread(Runnable paramRunnable)
  {
    ((MessageQueueThread)Assertions.assertNotNull(mNativeModulesMessageQueueThread)).runOnQueue(paramRunnable);
  }
  
  public void runOnUiQueueThread(Runnable paramRunnable)
  {
    ((MessageQueueThread)Assertions.assertNotNull(mUiMessageQueueThread)).runOnQueue(paramRunnable);
  }
  
  public void setNativeModuleCallExceptionHandler(NativeModuleCallExceptionHandler paramNativeModuleCallExceptionHandler)
  {
    mNativeModuleCallExceptionHandler = paramNativeModuleCallExceptionHandler;
  }
  
  public boolean startActivityForResult(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    Activity localActivity = getCurrentActivity();
    Assertions.assertNotNull(localActivity);
    localActivity.startActivityForResult(paramIntent, paramInt, paramBundle);
    return true;
  }
  
  public class ExceptionHandlerWrapper
    implements NativeModuleCallExceptionHandler
  {
    public ExceptionHandlerWrapper() {}
    
    public void handleException(Exception paramException)
    {
      ReactContext.this.handleException(paramException);
    }
  }
}
