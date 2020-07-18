package androidx.core.package_4;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Intent;
import androidx.annotation.RequiresApi;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@RequiresApi(28)
public class AppComponentFactory
  extends android.app.AppComponentFactory
{
  public AppComponentFactory() {}
  
  public final Activity instantiateActivity(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Activity)CoreComponentFactory.checkCompatWrapper(instantiateActivityCompat(paramClassLoader, paramString, paramIntent));
  }
  
  public Activity instantiateActivityCompat(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    try
    {
      paramClassLoader = Class.forName(paramString, false, paramClassLoader);
      paramClassLoader = paramClassLoader.getDeclaredConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (Activity)paramClassLoader;
    }
    catch (InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new RuntimeException("Couldn't call constructor", paramClassLoader);
    }
  }
  
  public final Application instantiateApplication(ClassLoader paramClassLoader, String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Application)CoreComponentFactory.checkCompatWrapper(instantiateApplicationCompat(paramClassLoader, paramString));
  }
  
  public Application instantiateApplicationCompat(ClassLoader paramClassLoader, String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    try
    {
      paramClassLoader = Class.forName(paramString, false, paramClassLoader);
      paramClassLoader = paramClassLoader.getDeclaredConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (Application)paramClassLoader;
    }
    catch (InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new RuntimeException("Couldn't call constructor", paramClassLoader);
    }
  }
  
  public final ContentProvider instantiateProvider(ClassLoader paramClassLoader, String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (ContentProvider)CoreComponentFactory.checkCompatWrapper(instantiateProviderCompat(paramClassLoader, paramString));
  }
  
  public ContentProvider instantiateProviderCompat(ClassLoader paramClassLoader, String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    try
    {
      paramClassLoader = Class.forName(paramString, false, paramClassLoader);
      paramClassLoader = paramClassLoader.getDeclaredConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (ContentProvider)paramClassLoader;
    }
    catch (InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new RuntimeException("Couldn't call constructor", paramClassLoader);
    }
  }
  
  public final BroadcastReceiver instantiateReceiver(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (BroadcastReceiver)CoreComponentFactory.checkCompatWrapper(instantiateReceiverCompat(paramClassLoader, paramString, paramIntent));
  }
  
  public BroadcastReceiver instantiateReceiverCompat(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    try
    {
      paramClassLoader = Class.forName(paramString, false, paramClassLoader);
      paramClassLoader = paramClassLoader.getDeclaredConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (BroadcastReceiver)paramClassLoader;
    }
    catch (InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new RuntimeException("Couldn't call constructor", paramClassLoader);
    }
  }
  
  public final Service instantiateService(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Service)CoreComponentFactory.checkCompatWrapper(instantiateServiceCompat(paramClassLoader, paramString, paramIntent));
  }
  
  public Service instantiateServiceCompat(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    try
    {
      paramClassLoader = Class.forName(paramString, false, paramClassLoader);
      paramClassLoader = paramClassLoader.getDeclaredConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (Service)paramClassLoader;
    }
    catch (InvocationTargetException|NoSuchMethodException paramClassLoader)
    {
      throw new RuntimeException("Couldn't call constructor", paramClassLoader);
    }
  }
}
