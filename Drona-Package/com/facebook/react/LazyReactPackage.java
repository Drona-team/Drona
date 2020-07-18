package com.facebook.react;

import com.facebook.react.bridge.ModuleHolder;
import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;

public abstract class LazyReactPackage
  implements ReactPackage
{
  public LazyReactPackage() {}
  
  public static ReactModuleInfoProvider getReactModuleInfoProviderViaReflection(LazyReactPackage paramLazyReactPackage)
  {
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramLazyReactPackage.getClass().getCanonicalName());
      ((StringBuilder)localObject).append("$$ReactModuleInfoProvider");
      localObject = Class.forName(((StringBuilder)localObject).toString());
      if (localObject != null) {
        try
        {
          localObject = ((Class)localObject).newInstance();
          return (ReactModuleInfoProvider)localObject;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append("Unable to instantiate ReactModuleInfoProvider for ");
          localStringBuilder2.append(paramLazyReactPackage.getClass());
          throw new RuntimeException(localStringBuilder2.toString(), localIllegalAccessException);
        }
        catch (InstantiationException localInstantiationException)
        {
          StringBuilder localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append("Unable to instantiate ReactModuleInfoProvider for ");
          localStringBuilder2.append(paramLazyReactPackage.getClass());
          throw new RuntimeException(localStringBuilder2.toString(), localInstantiationException);
        }
      }
      StringBuilder localStringBuilder1 = new StringBuilder();
      localStringBuilder1.append("ReactModuleInfoProvider class for ");
      localStringBuilder1.append(paramLazyReactPackage.getClass().getCanonicalName());
      localStringBuilder1.append(" not found.");
      throw new RuntimeException(localStringBuilder1.toString());
    }
    catch (ClassNotFoundException paramLazyReactPackage)
    {
      for (;;) {}
    }
    new ReactModuleInfoProvider()
    {
      public Map getReactModuleInfos()
      {
        return Collections.emptyMap();
      }
    };
  }
  
  public final List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    paramReactApplicationContext = getNativeModules(paramReactApplicationContext).iterator();
    while (paramReactApplicationContext.hasNext())
    {
      Object localObject = (ModuleSpec)paramReactApplicationContext.next();
      SystraceMessage.beginSection(0L, "createNativeModule").put("module", ((ModuleSpec)localObject).getType()).flush();
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_START, ((ModuleSpec)localObject).getName());
      try
      {
        localObject = (NativeModule)((ModuleSpec)localObject).getProvider().get();
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
        SystraceMessage.endSection(0L).flush();
        localArrayList.add(localObject);
      }
      catch (Throwable paramReactApplicationContext)
      {
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
        SystraceMessage.endSection(0L).flush();
        throw paramReactApplicationContext;
      }
    }
    return localArrayList;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    Object localObject = getViewManagers(paramReactApplicationContext);
    if ((localObject != null) && (!((List)localObject).isEmpty()))
    {
      paramReactApplicationContext = new ArrayList();
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        paramReactApplicationContext.add((ViewManager)((ModuleSpec)((Iterator)localObject).next()).getProvider().get());
      }
      return paramReactApplicationContext;
    }
    return Collections.emptyList();
  }
  
  public Iterable getNativeModuleIterator(ReactApplicationContext paramReactApplicationContext)
  {
    final Map localMap = getReactModuleInfoProvider().getReactModuleInfos();
    new Iterable()
    {
      public Iterator iterator()
      {
        new Iterator()
        {
          int position = 0;
          
          public boolean hasNext()
          {
            return position < val$nativeModules.size();
          }
          
          public ModuleHolder next()
          {
            Object localObject = val$nativeModules;
            int i = position;
            position = (i + 1);
            localObject = (ModuleSpec)((List)localObject).get(i);
            String str = ((ModuleSpec)localObject).getName();
            ReactModuleInfo localReactModuleInfo = (ReactModuleInfo)val$reactModuleInfoMap.get(str);
            if (localReactModuleInfo == null)
            {
              ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_START, str);
              try
              {
                localObject = (NativeModule)((ModuleSpec)localObject).getProvider().get();
                ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
                return new ModuleHolder((NativeModule)localObject);
              }
              catch (Throwable localThrowable)
              {
                ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
                throw localThrowable;
              }
            }
            return new ModuleHolder(localReactModuleInfo, localThrowable.getProvider());
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException("Cannot remove native modules from the list");
          }
        };
      }
    };
  }
  
  protected abstract List getNativeModules(ReactApplicationContext paramReactApplicationContext);
  
  public abstract ReactModuleInfoProvider getReactModuleInfoProvider();
  
  public List getViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
}
