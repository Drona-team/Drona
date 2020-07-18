package com.facebook.soloader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class ApplicationSoSource
  extends SoSource
{
  private Context applicationContext;
  private int flags;
  private DirectorySoSource soSource;
  
  public ApplicationSoSource(Context paramContext, int paramInt)
  {
    applicationContext = paramContext.getApplicationContext();
    if (applicationContext == null)
    {
      Log.w("SoLoader", "context.getApplicationContext returned null, holding reference to original context.");
      applicationContext = paramContext;
    }
    flags = paramInt;
    soSource = new DirectorySoSource(new File(applicationContext.getApplicationInfo().nativeLibraryDir), paramInt);
  }
  
  public void addToLdLibraryPath(Collection paramCollection)
  {
    soSource.addToLdLibraryPath(paramCollection);
  }
  
  public boolean checkAndMaybeUpdate()
    throws IOException
  {
    Object localObject1 = soSource.soDirectory;
    Context localContext = applicationContext;
    Object localObject2 = applicationContext;
    try
    {
      localContext = localContext.createPackageContext(((Context)localObject2).getPackageName(), 0);
      localObject2 = localContext.getApplicationInfo();
      localObject2 = nativeLibraryDir;
      localObject2 = new File((String)localObject2);
      boolean bool = ((File)localObject1).equals(localObject2);
      if (!bool)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Native library directory updated from ");
        localStringBuilder.append(localObject1);
        localStringBuilder.append(" to ");
        localStringBuilder.append(localObject2);
        Log.d("SoLoader", localStringBuilder.toString());
        flags |= 0x1;
        int i = flags;
        localObject1 = new DirectorySoSource((File)localObject2, i);
        soSource = ((DirectorySoSource)localObject1);
        localObject1 = soSource;
        i = flags;
        ((SoSource)localObject1).prepare(i);
        applicationContext = localContext;
        return true;
      }
      return false;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new RuntimeException(localNameNotFoundException);
    }
  }
  
  public int loadLibrary(String paramString, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
    throws IOException
  {
    return soSource.loadLibrary(paramString, paramInt, paramThreadPolicy);
  }
  
  protected void prepare(int paramInt)
    throws IOException
  {
    soSource.prepare(paramInt);
  }
  
  public String toString()
  {
    return soSource.toString();
  }
  
  public File unpackLibrary(String paramString)
    throws IOException
  {
    return soSource.unpackLibrary(paramString);
  }
}
