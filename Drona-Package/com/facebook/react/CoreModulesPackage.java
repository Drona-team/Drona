package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ExceptionsManagerModule;
import com.facebook.react.modules.core.HeadlessJsTaskSupportModule;
import com.facebook.react.modules.core.Timing;
import com.facebook.react.modules.debug.DevSettingsModule;
import com.facebook.react.modules.debug.SourceCodeModule;
import com.facebook.react.modules.deviceinfo.DeviceInfoModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIManagerModule.ViewManagerResolver;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.systrace.Systrace;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CoreModulesPackage
  extends TurboReactPackage
  implements ReactPackageLogger
{
  private final DefaultHardwareBackBtnHandler mHardwareBackBtnHandler;
  private final boolean mLazyViewManagersEnabled;
  private final int mMinTimeLeftInFrameForNonBatchedOperationMs;
  private final ReactInstanceManager mReactInstanceManager;
  
  CoreModulesPackage(ReactInstanceManager paramReactInstanceManager, DefaultHardwareBackBtnHandler paramDefaultHardwareBackBtnHandler, UIImplementationProvider paramUIImplementationProvider, boolean paramBoolean, int paramInt)
  {
    mReactInstanceManager = paramReactInstanceManager;
    mHardwareBackBtnHandler = paramDefaultHardwareBackBtnHandler;
    mLazyViewManagersEnabled = paramBoolean;
    mMinTimeLeftInFrameForNonBatchedOperationMs = paramInt;
  }
  
  private UIManagerModule createUIManager(ReactApplicationContext paramReactApplicationContext)
  {
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_START);
    Systrace.beginSection(0L, "createUIManagerModule");
    try
    {
      boolean bool = mLazyViewManagersEnabled;
      if (bool)
      {
        paramReactApplicationContext = new UIManagerModule(paramReactApplicationContext, new UIManagerModule.ViewManagerResolver()
        {
          public ViewManager getViewManager(String paramAnonymousString)
          {
            return mReactInstanceManager.createViewManager(paramAnonymousString);
          }
          
          public List getViewManagerNames()
          {
            return mReactInstanceManager.getViewManagerNames();
          }
        }, mMinTimeLeftInFrameForNonBatchedOperationMs);
        Systrace.endSection(0L);
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
        return paramReactApplicationContext;
      }
      paramReactApplicationContext = new UIManagerModule(paramReactApplicationContext, mReactInstanceManager.getOrCreateViewManagers(paramReactApplicationContext), mMinTimeLeftInFrameForNonBatchedOperationMs);
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
      return paramReactApplicationContext;
    }
    catch (Throwable paramReactApplicationContext)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
      throw paramReactApplicationContext;
    }
  }
  
  public void endProcessPackage()
  {
    ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_END);
  }
  
  public NativeModule getModule(String paramString, ReactApplicationContext paramReactApplicationContext)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1861242489: 
      if (paramString.equals("UIManager")) {
        i = 7;
      }
      break;
    case 1256514152: 
      if (paramString.equals("HeadlessJsTaskSupport")) {
        i = 4;
      }
      break;
    case 881516744: 
      if (paramString.equals("SourceCode")) {
        i = 5;
      }
      break;
    case 512434409: 
      if (paramString.equals("ExceptionsManager")) {
        i = 3;
      }
      break;
    case -790603268: 
      if (paramString.equals("PlatformConstants")) {
        i = 0;
      }
      break;
    case -1037217463: 
      if (paramString.equals("DeviceEventManager")) {
        i = 1;
      }
      break;
    case -1520650172: 
      if (paramString.equals("DeviceInfo")) {
        i = 8;
      }
      break;
    case -1633589448: 
      if (paramString.equals("DevSettings")) {
        i = 2;
      }
      break;
    case -1789797270: 
      if (paramString.equals("Timing")) {
        i = 6;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      paramReactApplicationContext = new StringBuilder();
      paramReactApplicationContext.append("In CoreModulesPackage, could not find Native module for ");
      paramReactApplicationContext.append(paramString);
      throw new IllegalArgumentException(paramReactApplicationContext.toString());
    case 8: 
      return new DeviceInfoModule(paramReactApplicationContext);
    case 7: 
      return createUIManager(paramReactApplicationContext);
    case 6: 
      return new Timing(paramReactApplicationContext, mReactInstanceManager.getDevSupportManager());
    case 5: 
      return new SourceCodeModule(paramReactApplicationContext);
    case 4: 
      return new HeadlessJsTaskSupportModule(paramReactApplicationContext);
    case 3: 
      return new ExceptionsManagerModule(mReactInstanceManager.getDevSupportManager());
    case 2: 
      return new DevSettingsModule(mReactInstanceManager.getDevSupportManager());
    case 1: 
      return new DeviceEventManagerModule(paramReactApplicationContext, mHardwareBackBtnHandler);
    }
    return new AndroidInfoModule(paramReactApplicationContext);
  }
  
  public ReactModuleInfoProvider getReactModuleInfoProvider()
  {
    try
    {
      Object localObject = Class.forName("com.facebook.react.CoreModulesPackage$$ReactModuleInfoProvider").newInstance();
      return (ReactModuleInfoProvider)localObject;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", localInstantiationException);
      Class[] arrayOfClass = new Class[9];
      int i = 0;
      arrayOfClass[0] = AndroidInfoModule.class;
      arrayOfClass[1] = DeviceEventManagerModule.class;
      arrayOfClass[2] = DeviceInfoModule.class;
      arrayOfClass[3] = DevSettingsModule.class;
      arrayOfClass[4] = ExceptionsManagerModule.class;
      arrayOfClass[5] = HeadlessJsTaskSupportModule.class;
      arrayOfClass[6] = SourceCodeModule.class;
      arrayOfClass[7] = Timing.class;
      arrayOfClass[8] = UIManagerModule.class;
      final HashMap localHashMap = new HashMap();
      int j = arrayOfClass.length;
      while (i < j)
      {
        Class localClass = arrayOfClass[i];
        ReactModule localReactModule = (ReactModule)localClass.getAnnotation(ReactModule.class);
        localHashMap.put(localReactModule.name(), new ReactModuleInfo(localReactModule.name(), localClass.getName(), localReactModule.canOverrideExistingModule(), localReactModule.needsEagerInit(), localReactModule.hasConstants(), localReactModule.isCxxModule(), false));
        i += 1;
      }
      new ReactModuleInfoProvider()
      {
        public Map getReactModuleInfos()
        {
          return localHashMap;
        }
      };
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;) {}
    }
  }
  
  public void startProcessPackage()
  {
    ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_START);
  }
}