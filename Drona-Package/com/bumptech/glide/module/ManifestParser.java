package com.bumptech.glide.module;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BaseBundle;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Deprecated
public final class ManifestParser
{
  private static final String GLIDE_MODULE_VALUE = "GlideModule";
  private static final String TAG = "ManifestParser";
  private final Context context;
  
  public ManifestParser(Context paramContext)
  {
    context = paramContext;
  }
  
  private static GlideModule parseModule(String paramString)
  {
    try
    {
      Class localClass = Class.forName(paramString);
      paramString = null;
      try
      {
        Object localObject = localClass.getDeclaredConstructor(new Class[0]);
        localObject = ((Constructor)localObject).newInstance(new Object[0]);
        paramString = (String)localObject;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throwInstantiateGlideModuleException(localClass, localInvocationTargetException);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throwInstantiateGlideModuleException(localClass, localNoSuchMethodException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throwInstantiateGlideModuleException(localClass, localIllegalAccessException);
      }
      catch (InstantiationException localInstantiationException)
      {
        throwInstantiateGlideModuleException(localClass, localInstantiationException);
      }
      if ((paramString instanceof GlideModule)) {
        return (GlideModule)paramString;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Expected instanceof GlideModule, but found: ");
      localStringBuilder.append(paramString);
      throw new RuntimeException(localStringBuilder.toString());
    }
    catch (ClassNotFoundException paramString)
    {
      throw new IllegalArgumentException("Unable to find GlideModule implementation", paramString);
    }
  }
  
  private static void throwInstantiateGlideModuleException(Class paramClass, Exception paramException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unable to instantiate GlideModule implementation for ");
    localStringBuilder.append(paramClass);
    throw new RuntimeException(localStringBuilder.toString(), paramException);
  }
  
  public List parse()
  {
    if (Log.isLoggable("ManifestParser", 3)) {
      Log.d("ManifestParser", "Loading Glide modules");
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = context;
    try
    {
      localObject1 = ((Context)localObject1).getPackageManager();
      Object localObject2 = context;
      localObject1 = ((PackageManager)localObject1).getApplicationInfo(((Context)localObject2).getPackageName(), 128);
      boolean bool;
      if (metaData == null)
      {
        bool = Log.isLoggable("ManifestParser", 3);
        if (bool)
        {
          Log.d("ManifestParser", "Got null app info metadata");
          return localArrayList;
        }
      }
      else
      {
        bool = Log.isLoggable("ManifestParser", 2);
        Object localObject3;
        if (bool)
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Got app info metadata: ");
          localObject3 = metaData;
          ((StringBuilder)localObject2).append(localObject3);
          Log.v("ManifestParser", ((StringBuilder)localObject2).toString());
        }
        localObject2 = metaData;
        localObject2 = ((BaseBundle)localObject2).keySet().iterator();
        for (;;)
        {
          bool = ((Iterator)localObject2).hasNext();
          if (!bool) {
            break;
          }
          localObject3 = ((Iterator)localObject2).next();
          localObject3 = (String)localObject3;
          Object localObject4 = metaData;
          bool = "GlideModule".equals(((BaseBundle)localObject4).get((String)localObject3));
          if (bool)
          {
            localArrayList.add(parseModule((String)localObject3));
            bool = Log.isLoggable("ManifestParser", 3);
            if (bool)
            {
              localObject4 = new StringBuilder();
              ((StringBuilder)localObject4).append("Loaded Glide module: ");
              ((StringBuilder)localObject4).append((String)localObject3);
              Log.d("ManifestParser", ((StringBuilder)localObject4).toString());
            }
          }
        }
        if (Log.isLoggable("ManifestParser", 3))
        {
          Log.d("ManifestParser", "Finished loading Glide modules");
          return localArrayList;
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new RuntimeException("Unable to find metadata to parse GlideModules", localNameNotFoundException);
    }
    return localNameNotFoundException;
  }
}
