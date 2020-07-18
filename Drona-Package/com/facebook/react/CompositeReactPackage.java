package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class CompositeReactPackage
  implements ViewManagerOnDemandReactPackage, ReactPackage
{
  private final List<ReactPackage> mChildReactPackages = new ArrayList();
  
  public CompositeReactPackage(ReactPackage paramReactPackage1, ReactPackage paramReactPackage2, ReactPackage... paramVarArgs)
  {
    mChildReactPackages.add(paramReactPackage1);
    mChildReactPackages.add(paramReactPackage2);
    Collections.addAll(mChildReactPackages, paramVarArgs);
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = mChildReactPackages.iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (ReactPackage)localIterator.next();
      Object localObject2;
      if ((localObject1 instanceof TurboReactPackage))
      {
        localObject1 = (TurboReactPackage)localObject1;
        localObject2 = ((TurboReactPackage)localObject1).getReactModuleInfoProvider().getReactModuleInfos().keySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          String str = (String)((Iterator)localObject2).next();
          localHashMap.put(str, ((TurboReactPackage)localObject1).getModule(str, paramReactApplicationContext));
        }
      }
      else
      {
        localObject1 = ((ReactPackage)localObject1).createNativeModules(paramReactApplicationContext).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (NativeModule)((Iterator)localObject1).next();
          localHashMap.put(((NativeModule)localObject2).getName(), localObject2);
        }
      }
    }
    return new ArrayList(localHashMap.values());
  }
  
  public ViewManager createViewManager(ReactApplicationContext paramReactApplicationContext, String paramString)
  {
    ListIterator localListIterator = mChildReactPackages.listIterator(mChildReactPackages.size());
    while (localListIterator.hasPrevious())
    {
      Object localObject = (ReactPackage)localListIterator.previous();
      if ((localObject instanceof ViewManagerOnDemandReactPackage))
      {
        localObject = ((ViewManagerOnDemandReactPackage)localObject).createViewManager(paramReactApplicationContext, paramString);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    return null;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = mChildReactPackages.iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((ReactPackage)localIterator1.next()).createViewManagers(paramReactApplicationContext).iterator();
      while (localIterator2.hasNext())
      {
        ViewManager localViewManager = (ViewManager)localIterator2.next();
        localHashMap.put(localViewManager.getName(), localViewManager);
      }
    }
    return new ArrayList(localHashMap.values());
  }
  
  public List getViewManagerNames(ReactApplicationContext paramReactApplicationContext)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = mChildReactPackages.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (ReactPackage)localIterator.next();
      if ((localObject instanceof ViewManagerOnDemandReactPackage))
      {
        localObject = ((ViewManagerOnDemandReactPackage)localObject).getViewManagerNames(paramReactApplicationContext);
        if (localObject != null) {
          localHashSet.addAll((Collection)localObject);
        }
      }
    }
    return new ArrayList(localHashSet);
  }
}
