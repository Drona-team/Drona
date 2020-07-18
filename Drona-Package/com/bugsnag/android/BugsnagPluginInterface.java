package com.bugsnag.android;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv={1, 0, 3}, d1={"\000.\n\002\030\002\n\002\020\000\n\002\b\002\n\002\020%\n\002\030\002\n\002\030\002\n\000\n\002\020#\n\000\n\002\020\002\n\000\n\002\030\002\n\002\b\005\b?\002\030\0002\0020\001B\007\b\002?\006\002\020\002J\034\020\t\032\0020\n2\006\020\013\032\0020\f2\n\020\r\032\006\022\002\b\0030\005H\001J\020\020\016\032\0020\n2\006\020\013\032\0020\fH\001J\022\020\017\032\0020\n2\n\020\r\032\006\022\002\b\0030\005J\024\020\020\032\0020\n2\n\020\r\032\006\022\002\b\0030\005H\001R\036\020\003\032\022\022\b\022\006\022\002\b\0030\005\022\004\022\0020\0060\004X?\016?\006\002\n\000R\030\020\007\032\f\022\b\022\006\022\002\b\0030\0050\bX?\016?\006\002\n\000?\006\021"}, d2={"Lcom/bugsnag/android/BugsnagPluginInterface;", "", "()V", "plugins", "", "Ljava/lang/Class;", "Lcom/bugsnag/android/BugsnagPlugin;", "registeredPluginClasses", "", "loadPlugin", "", "client", "Lcom/bugsnag/android/Client;", "clz", "loadRegisteredPlugins", "registerPlugin", "unloadPlugin", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
public final class BugsnagPluginInterface
{
  public static final BugsnagPluginInterface INSTANCE = new BugsnagPluginInterface();
  private static Map<Class<?>, BugsnagPlugin> plugins = (Map)new LinkedHashMap();
  private static Set<Class<?>> registeredPluginClasses = (Set)new LinkedHashSet();
  
  private BugsnagPluginInterface() {}
  
  public final void loadPlugin(Client paramClient, Class paramClass)
  {
    Intrinsics.checkParameterIsNotNull(paramClient, "client");
    Intrinsics.checkParameterIsNotNull(paramClass, "clz");
    BugsnagPlugin localBugsnagPlugin = (BugsnagPlugin)plugins.get(paramClass);
    Object localObject = localBugsnagPlugin;
    if (localBugsnagPlugin == null) {}
    try
    {
      localObject = paramClass.newInstance();
      if (localObject != null)
      {
        localObject = (BugsnagPlugin)localObject;
        break label70;
      }
      localObject = new TypeCastException("null cannot be cast to non-null type com.bugsnag.android.BugsnagPlugin");
      localObject = (Throwable)localObject;
      throw ((Throwable)localObject);
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    localObject = null;
    label70:
    if ((localObject != null) && (!((BugsnagPlugin)localObject).getLoaded()))
    {
      plugins.put(paramClass, localObject);
      ((BugsnagPlugin)localObject).loadPlugin(paramClient);
      ((BugsnagPlugin)localObject).setLoaded(true);
      return;
    }
  }
  
  public final void loadRegisteredPlugins(Client paramClient)
  {
    Intrinsics.checkParameterIsNotNull(paramClient, "client");
    Iterator localIterator = ((Iterable)registeredPluginClasses).iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      INSTANCE.loadPlugin(paramClient, localClass);
    }
  }
  
  public final void registerPlugin(Class paramClass)
  {
    Intrinsics.checkParameterIsNotNull(paramClass, "clz");
    registeredPluginClasses.add(paramClass);
  }
  
  public final void unloadPlugin(Class paramClass)
  {
    Intrinsics.checkParameterIsNotNull(paramClass, "clz");
    paramClass = (BugsnagPlugin)plugins.get(paramClass);
    if ((paramClass != null) && (paramClass.getLoaded()))
    {
      paramClass.unloadPlugin();
      paramClass.setLoaded(false);
    }
  }
}
