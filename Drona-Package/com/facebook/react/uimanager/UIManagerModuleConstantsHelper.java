package com.facebook.react.uimanager;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class UIManagerModuleConstantsHelper
{
  private static final String BUBBLING_EVENTS_KEY = "bubblingEventTypes";
  private static final String DIRECT_EVENTS_KEY = "directEventTypes";
  
  UIManagerModuleConstantsHelper() {}
  
  static Map createConstants(UIManagerModule.ViewManagerResolver paramViewManagerResolver)
  {
    Map localMap = UIManagerModuleConstants.getConstants();
    if (!ReactFeatureFlags.lazilyLoadViewManagers) {
      localMap.put("ViewManagerNames", paramViewManagerResolver.getViewManagerNames());
    }
    localMap.put("LazyViewManagersEnabled", Boolean.valueOf(true));
    return localMap;
  }
  
  static Map createConstants(List paramList, Map paramMap1, Map paramMap2)
  {
    Map localMap1 = UIManagerModuleConstants.getConstants();
    Map localMap2 = UIManagerModuleConstants.getBubblingEventTypeConstants();
    Map localMap3 = UIManagerModuleConstants.getDirectEventTypeConstants();
    if (paramMap1 != null) {
      paramMap1.putAll(localMap2);
    }
    if (paramMap2 != null) {
      paramMap2.putAll(localMap3);
    }
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Object localObject = (ViewManager)paramList.next();
      String str = ((ViewManager)localObject).getName();
      SystraceMessage.beginSection(0L, "UIManagerModuleConstantsHelper.createConstants").put("ViewManager", str).put("Lazy", Boolean.valueOf(false)).flush();
      try
      {
        localObject = createConstantsForViewManager((ViewManager)localObject, null, null, paramMap1, paramMap2);
        boolean bool = ((Map)localObject).isEmpty();
        if (!bool) {
          localMap1.put(str, localObject);
        }
        SystraceMessage.endSection(0L);
      }
      catch (Throwable paramList)
      {
        SystraceMessage.endSection(0L);
        throw paramList;
      }
    }
    localMap1.put("genericBubblingEventTypes", localMap2);
    localMap1.put("genericDirectEventTypes", localMap3);
    return localMap1;
  }
  
  static Map createConstantsForViewManager(ViewManager paramViewManager, Map paramMap1, Map paramMap2, Map paramMap3, Map paramMap4)
  {
    HashMap localHashMap = MapBuilder.newHashMap();
    Map localMap = paramViewManager.getExportedCustomBubblingEventTypeConstants();
    if (localMap != null)
    {
      recursiveMerge(paramMap3, localMap);
      recursiveMerge(localMap, paramMap1);
      localHashMap.put("bubblingEventTypes", localMap);
    }
    else if (paramMap1 != null)
    {
      localHashMap.put("bubblingEventTypes", paramMap1);
    }
    paramMap1 = paramViewManager.getExportedCustomDirectEventTypeConstants();
    if (paramMap1 != null)
    {
      recursiveMerge(paramMap4, paramMap1);
      recursiveMerge(paramMap1, paramMap2);
      localHashMap.put("directEventTypes", paramMap1);
    }
    else if (paramMap2 != null)
    {
      localHashMap.put("directEventTypes", paramMap2);
    }
    paramMap1 = paramViewManager.getExportedViewConstants();
    if (paramMap1 != null) {
      localHashMap.put("Constants", paramMap1);
    }
    paramMap1 = paramViewManager.getCommandsMap();
    if (paramMap1 != null) {
      localHashMap.put("Commands", paramMap1);
    }
    paramViewManager = paramViewManager.getNativeProps();
    if (!paramViewManager.isEmpty()) {
      localHashMap.put("NativeProps", paramViewManager);
    }
    return localHashMap;
  }
  
  static Map getDefaultExportableEventTypes()
  {
    return MapBuilder.get("bubblingEventTypes", UIManagerModuleConstants.getBubblingEventTypeConstants(), "directEventTypes", UIManagerModuleConstants.getDirectEventTypeConstants());
  }
  
  private static void recursiveMerge(Map paramMap1, Map paramMap2)
  {
    if ((paramMap1 != null) && (paramMap2 != null))
    {
      if (paramMap2.isEmpty()) {
        return;
      }
      Iterator localIterator = paramMap2.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        Object localObject2 = paramMap2.get(localObject1);
        Object localObject3 = paramMap1.get(localObject1);
        if ((localObject3 != null) && ((localObject2 instanceof Map)) && ((localObject3 instanceof Map))) {
          recursiveMerge((Map)localObject3, (Map)localObject2);
        } else {
          paramMap1.put(localObject1, localObject2);
        }
      }
    }
  }
}
