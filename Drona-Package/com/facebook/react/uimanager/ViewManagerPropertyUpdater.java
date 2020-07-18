package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReadableMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ViewManagerPropertyUpdater
{
  private static final String PAGE_KEY = "ViewManagerPropertyUpdater";
  private static final Map<Class<?>, ShadowNodeSetter<?>> SHADOW_NODE_SETTER_MAP = new HashMap();
  private static final Map<Class<?>, ViewManagerSetter<?, ?>> VIEW_MANAGER_SETTER_MAP = new HashMap();
  
  public ViewManagerPropertyUpdater() {}
  
  public static void clear()
  {
    ViewManagersPropertyCache.clear();
    VIEW_MANAGER_SETTER_MAP.clear();
    SHADOW_NODE_SETTER_MAP.clear();
  }
  
  private static Object findGeneratedSetter(Class paramClass)
  {
    Object localObject1 = paramClass.getName();
    try
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("$$PropsSetter");
      localObject2 = Class.forName(((StringBuilder)localObject2).toString()).newInstance();
      return localObject2;
    }
    catch (InstantiationException|IllegalAccessException paramClass)
    {
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Unable to instantiate methods getter for ");
      ((StringBuilder)localObject2).append((String)localObject1);
      throw new RuntimeException(((StringBuilder)localObject2).toString(), paramClass);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Could not find generated setter for ");
      ((StringBuilder)localObject1).append(paramClass);
      FLog.warn("ViewManagerPropertyUpdater", ((StringBuilder)localObject1).toString());
      return null;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;) {}
    }
  }
  
  private static ViewManagerSetter findManagerSetter(Class paramClass)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private static ShadowNodeSetter findNodeSetter(Class paramClass)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public static Map getNativeProps(Class paramClass1, Class paramClass2)
  {
    HashMap localHashMap = new HashMap();
    findManagerSetter(paramClass1).getProperties(localHashMap);
    findNodeSetter(paramClass2).getProperties(localHashMap);
    return localHashMap;
  }
  
  public static void updateProps(ReactShadowNode paramReactShadowNode, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    ShadowNodeSetter localShadowNodeSetter = findNodeSetter(paramReactShadowNode.getClass());
    paramReactStylesDiffMap = mBackingMap.getEntryIterator();
    while (paramReactStylesDiffMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramReactStylesDiffMap.next();
      localShadowNodeSetter.setProperty(paramReactShadowNode, (String)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public static void updateProps(ViewManager paramViewManager, View paramView, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    ViewManagerSetter localViewManagerSetter = findManagerSetter(paramViewManager.getClass());
    paramReactStylesDiffMap = mBackingMap.getEntryIterator();
    while (paramReactStylesDiffMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramReactStylesDiffMap.next();
      localViewManagerSetter.setProperty(paramViewManager, paramView, (String)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public static void updateProps(ViewManagerDelegate paramViewManagerDelegate, View paramView, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    paramReactStylesDiffMap = mBackingMap.getEntryIterator();
    while (paramReactStylesDiffMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramReactStylesDiffMap.next();
      paramViewManagerDelegate.setProperty(paramView, (String)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  private static class FallbackShadowNodeSetter<T extends ReactShadowNode>
    implements ViewManagerPropertyUpdater.ShadowNodeSetter<T>
  {
    private final Map<String, ViewManagersPropertyCache.PropSetter> mPropSetters;
    
    private FallbackShadowNodeSetter(Class paramClass)
    {
      mPropSetters = ViewManagersPropertyCache.getNativePropSettersForShadowNodeClass(paramClass);
    }
    
    public void getProperties(Map paramMap)
    {
      Iterator localIterator = mPropSetters.values().iterator();
      while (localIterator.hasNext())
      {
        ViewManagersPropertyCache.PropSetter localPropSetter = (ViewManagersPropertyCache.PropSetter)localIterator.next();
        paramMap.put(localPropSetter.getPropName(), localPropSetter.getPropType());
      }
    }
    
    public void setProperty(ReactShadowNode paramReactShadowNode, String paramString, Object paramObject)
    {
      paramString = (ViewManagersPropertyCache.PropSetter)mPropSetters.get(paramString);
      if (paramString != null) {
        paramString.updateShadowNodeProp(paramReactShadowNode, paramObject);
      }
    }
  }
  
  private static class FallbackViewManagerSetter<T extends ViewManager, V extends View>
    implements ViewManagerPropertyUpdater.ViewManagerSetter<T, V>
  {
    private final Map<String, ViewManagersPropertyCache.PropSetter> mPropSetters;
    
    private FallbackViewManagerSetter(Class paramClass)
    {
      mPropSetters = ViewManagersPropertyCache.getNativePropSettersForViewManagerClass(paramClass);
    }
    
    public void getProperties(Map paramMap)
    {
      Iterator localIterator = mPropSetters.values().iterator();
      while (localIterator.hasNext())
      {
        ViewManagersPropertyCache.PropSetter localPropSetter = (ViewManagersPropertyCache.PropSetter)localIterator.next();
        paramMap.put(localPropSetter.getPropName(), localPropSetter.getPropType());
      }
    }
    
    public void setProperty(ViewManager paramViewManager, View paramView, String paramString, Object paramObject)
    {
      paramString = (ViewManagersPropertyCache.PropSetter)mPropSetters.get(paramString);
      if (paramString != null) {
        paramString.updateViewProp(paramViewManager, paramView, paramObject);
      }
    }
  }
  
  public static abstract interface Settable
  {
    public abstract void getProperties(Map paramMap);
  }
  
  public static abstract interface ShadowNodeSetter<T extends ReactShadowNode>
    extends ViewManagerPropertyUpdater.Settable
  {
    public abstract void setProperty(ReactShadowNode paramReactShadowNode, String paramString, Object paramObject);
  }
  
  public static abstract interface ViewManagerSetter<T extends ViewManager, V extends View>
    extends ViewManagerPropertyUpdater.Settable
  {
    public abstract void setProperty(ViewManager paramViewManager, View paramView, String paramString, Object paramObject);
  }
}
