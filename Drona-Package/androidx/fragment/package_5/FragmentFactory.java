package androidx.fragment.package_5;

import androidx.collection.SimpleArrayMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FragmentFactory
{
  private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap();
  
  public FragmentFactory() {}
  
  static boolean isFragmentClass(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      paramClassLoader = loadClass(paramClassLoader, paramString);
      boolean bool = androidx.fragment.app.Fragment.class.isAssignableFrom(paramClassLoader);
      return bool;
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      for (;;) {}
    }
    return false;
  }
  
  private static Class loadClass(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    Class localClass2 = (Class)sClassMap.get(paramString);
    Class localClass1 = localClass2;
    if (localClass2 == null)
    {
      localClass1 = Class.forName(paramString, false, paramClassLoader);
      sClassMap.put(paramString, localClass1);
    }
    return localClass1;
  }
  
  public static Class loadFragmentClass(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      paramClassLoader = loadClass(paramClassLoader, paramString);
      return paramClassLoader;
    }
    catch (ClassCastException paramClassLoader)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": make sure class is a valid subclass of Fragment");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": make sure class name exists");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
  }
  
  public Fragment instantiate(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      paramClassLoader = loadFragmentClass(paramClassLoader, paramString);
      paramClassLoader = paramClassLoader.getConstructor(new Class[0]);
      paramClassLoader = paramClassLoader.newInstance(new Object[0]);
      return (Fragment)paramClassLoader;
    }
    catch (InvocationTargetException paramClassLoader)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": calling Fragment constructor caused an exception");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
    catch (NoSuchMethodException paramClassLoader)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": could not find Fragment constructor");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
    catch (IllegalAccessException paramClassLoader)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": make sure class name exists, is public, and has an empty constructor that is public");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
    catch (InstantiationException paramClassLoader)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to instantiate fragment ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(": make sure class name exists, is public, and has an empty constructor that is public");
      throw new Fragment.InstantiationException(localStringBuilder.toString(), paramClassLoader);
    }
  }
}
