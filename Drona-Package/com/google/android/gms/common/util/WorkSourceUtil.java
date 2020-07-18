package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Process;
import android.os.WorkSource;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@KeepForSdk
public class WorkSourceUtil
{
  private static final int zzhj = ;
  private static final Method zzhk = find();
  private static final Method zzhl = get();
  private static final Method zzhm = getInitCauseMethod();
  private static final Method zzhn = zzaa();
  private static final Method zzho = zzab();
  private static final Method zzhp = zzac();
  private static final Method zzhq = zzad();
  
  private WorkSourceUtil() {}
  
  private static void convert(WorkSource paramWorkSource, int paramInt, String paramString)
  {
    if (zzhl != null)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      paramString = zzhl;
      try
      {
        paramString.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt), str });
        return;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
        return;
      }
    }
    if (zzhk != null)
    {
      paramString = zzhk;
      try
      {
        paramString.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt) });
        return;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
      }
    }
  }
  
  private static Method find()
  {
    Object localObject = Integer.TYPE;
    try
    {
      localObject = WorkSource.class.getMethod("add", new Class[] { localObject });
      return localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return null;
  }
  
  public static WorkSource fromPackage(Context paramContext, String paramString)
  {
    if (paramContext != null)
    {
      if (paramContext.getPackageManager() == null) {
        break label126;
      }
      if (paramString == null) {
        return null;
      }
    }
    try
    {
      paramContext = Wrappers.packageManager(paramContext).getApplicationInfo(paramString, 0);
      if (paramContext == null)
      {
        paramContext = String.valueOf(paramString);
        if (paramContext.length() != 0) {
          paramContext = "Could not get applicationInfo from package: ".concat(paramContext);
        } else {
          paramContext = new String("Could not get applicationInfo from package: ");
        }
        Log.e("WorkSourceUtil", paramContext);
        return null;
      }
      return loadMore(uid, paramString);
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    paramContext = String.valueOf(paramString);
    if (paramContext.length() != 0) {
      paramContext = "Could not find package: ".concat(paramContext);
    } else {
      paramContext = new String("Could not find package: ");
    }
    Log.e("WorkSourceUtil", paramContext);
    return null;
    label126:
    return null;
  }
  
  public static WorkSource fromPackageAndModuleExperimentalPi(Context paramContext, String paramString1, String paramString2)
  {
    if ((paramContext != null) && (paramContext.getPackageManager() != null) && (paramString2 != null) && (paramString1 != null))
    {
      int i = get(paramContext, paramString1);
      if (i < 0) {
        return null;
      }
      paramContext = new WorkSource();
      if ((zzhp != null) && (zzhq != null))
      {
        Object localObject = zzhp;
        try
        {
          localObject = ((Method)localObject).invoke(paramContext, new Object[0]);
          if (i != zzhj)
          {
            Method localMethod = zzhq;
            localMethod.invoke(localObject, new Object[] { Integer.valueOf(i), paramString1 });
          }
          paramString1 = zzhq;
          i = zzhj;
          paramString1.invoke(localObject, new Object[] { Integer.valueOf(i), paramString2 });
          return paramContext;
        }
        catch (Exception paramString1)
        {
          Log.w("WorkSourceUtil", "Unable to assign chained blame through WorkSource", paramString1);
          return paramContext;
        }
      }
      convert(paramContext, i, paramString1);
      return paramContext;
    }
    Log.w("WorkSourceUtil", "Unexpected null arguments");
    return null;
  }
  
  private static int get(Context paramContext, String paramString)
  {
    try
    {
      paramContext = Wrappers.packageManager(paramContext).getApplicationInfo(paramString, 0);
      if (paramContext == null)
      {
        paramContext = String.valueOf(paramString);
        if (paramContext.length() != 0) {
          paramContext = "Could not get applicationInfo from package: ".concat(paramContext);
        } else {
          paramContext = new String("Could not get applicationInfo from package: ");
        }
        Log.e("WorkSourceUtil", paramContext);
        return -1;
      }
      return uid;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    paramContext = String.valueOf(paramString);
    if (paramContext.length() != 0) {
      paramContext = "Could not find package: ".concat(paramContext);
    } else {
      paramContext = new String("Could not find package: ");
    }
    Log.e("WorkSourceUtil", paramContext);
    return -1;
  }
  
  private static int get(WorkSource paramWorkSource)
  {
    if (zzhm != null)
    {
      Method localMethod = zzhm;
      try
      {
        paramWorkSource = localMethod.invoke(paramWorkSource, new Object[0]);
        paramWorkSource = (Integer)paramWorkSource;
        int i = paramWorkSource.intValue();
        return i;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
      }
    }
    return 0;
  }
  
  private static String get(WorkSource paramWorkSource, int paramInt)
  {
    if (zzho != null)
    {
      Method localMethod = zzho;
      try
      {
        paramWorkSource = localMethod.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt) });
        return (String)paramWorkSource;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
      }
    }
    return null;
  }
  
  private static Method get()
  {
    Object localObject;
    if (PlatformVersion.isAtLeastJellyBeanMR2()) {
      localObject = Integer.TYPE;
    }
    try
    {
      localObject = WorkSource.class.getMethod("add", new Class[] { localObject, String.class });
      return localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static Method getInitCauseMethod()
  {
    try
    {
      Method localMethod = WorkSource.class.getMethod("size", new Class[0]);
      return localMethod;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return null;
  }
  
  public static List getNames(WorkSource paramWorkSource)
  {
    int j = 0;
    int i;
    if (paramWorkSource == null) {
      i = 0;
    } else {
      i = get(paramWorkSource);
    }
    if (i == 0) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    while (j < i)
    {
      String str = get(paramWorkSource, j);
      if (!Strings.isEmptyOrWhitespace(str)) {
        localArrayList.add(str);
      }
      j += 1;
    }
    return localArrayList;
  }
  
  public static boolean hasWorkSourcePermission(Context paramContext)
  {
    if (paramContext == null) {
      return false;
    }
    if (paramContext.getPackageManager() == null) {
      return false;
    }
    return Wrappers.packageManager(paramContext).checkPermission("android.permission.UPDATE_DEVICE_STATS", paramContext.getPackageName()) == 0;
  }
  
  private static WorkSource loadMore(int paramInt, String paramString)
  {
    WorkSource localWorkSource = new WorkSource();
    convert(localWorkSource, paramInt, paramString);
    return localWorkSource;
  }
  
  private static Method zzaa()
  {
    Object localObject = Integer.TYPE;
    try
    {
      localObject = WorkSource.class.getMethod("get", new Class[] { localObject });
      return localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static Method zzab()
  {
    Object localObject;
    if (PlatformVersion.isAtLeastJellyBeanMR2()) {
      localObject = Integer.TYPE;
    }
    try
    {
      localObject = WorkSource.class.getMethod("getName", new Class[] { localObject });
      return localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static final Method zzac()
  {
    if (PlatformVersion.isAtLeastP()) {
      try
      {
        Method localMethod = WorkSource.class.getMethod("createWorkChain", new Class[0]);
        return localMethod;
      }
      catch (Exception localException)
      {
        Log.w("WorkSourceUtil", "Missing WorkChain API createWorkChain", localException);
      }
    }
    return null;
  }
  
  private static final Method zzad()
  {
    if (PlatformVersion.isAtLeastP()) {
      try
      {
        Object localObject = Class.forName("android.os.WorkSource$WorkChain");
        Class localClass = Integer.TYPE;
        localObject = ((Class)localObject).getMethod("addNode", new Class[] { localClass, String.class });
        return localObject;
      }
      catch (Exception localException)
      {
        Log.w("WorkSourceUtil", "Missing WorkChain class", localException);
      }
    }
    return null;
  }
}
