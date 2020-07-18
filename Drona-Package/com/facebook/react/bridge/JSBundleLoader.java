package com.facebook.react.bridge;

import android.content.Context;
import com.facebook.react.common.DebugServerException;

public abstract class JSBundleLoader
{
  public JSBundleLoader() {}
  
  public static JSBundleLoader createAssetLoader(Context paramContext, final String paramString, final boolean paramBoolean)
  {
    new JSBundleLoader()
    {
      public String loadScript(JSBundleLoaderDelegate paramAnonymousJSBundleLoaderDelegate)
      {
        paramAnonymousJSBundleLoaderDelegate.loadScriptFromAssets(val$context.getAssets(), paramString, paramBoolean);
        return paramString;
      }
    };
  }
  
  public static JSBundleLoader createCachedBundleFromNetworkLoader(final String paramString1, String paramString2)
  {
    new JSBundleLoader()
    {
      public String loadScript(JSBundleLoaderDelegate paramAnonymousJSBundleLoaderDelegate)
      {
        String str1 = val$cachedFileLocation;
        String str2 = paramString1;
        try
        {
          paramAnonymousJSBundleLoaderDelegate.loadScriptFromFile(str1, str2, false);
          return paramString1;
        }
        catch (Exception paramAnonymousJSBundleLoaderDelegate)
        {
          throw DebugServerException.makeGeneric(paramString1, paramAnonymousJSBundleLoaderDelegate.getMessage(), paramAnonymousJSBundleLoaderDelegate);
        }
      }
    };
  }
  
  public static JSBundleLoader createDeltaFromNetworkLoader(String paramString, final NativeDeltaClient paramNativeDeltaClient)
  {
    new JSBundleLoader()
    {
      public String loadScript(JSBundleLoaderDelegate paramAnonymousJSBundleLoaderDelegate)
      {
        String str = val$sourceURL;
        NativeDeltaClient localNativeDeltaClient = paramNativeDeltaClient;
        try
        {
          paramAnonymousJSBundleLoaderDelegate.loadScriptFromDeltaBundle(str, localNativeDeltaClient, false);
          return val$sourceURL;
        }
        catch (Exception paramAnonymousJSBundleLoaderDelegate)
        {
          throw DebugServerException.makeGeneric(val$sourceURL, paramAnonymousJSBundleLoaderDelegate.getMessage(), paramAnonymousJSBundleLoaderDelegate);
        }
      }
    };
  }
  
  public static JSBundleLoader createFileLoader(String paramString)
  {
    return createFileLoader(paramString, paramString, false);
  }
  
  public static JSBundleLoader createFileLoader(String paramString1, final String paramString2, final boolean paramBoolean)
  {
    new JSBundleLoader()
    {
      public String loadScript(JSBundleLoaderDelegate paramAnonymousJSBundleLoaderDelegate)
      {
        paramAnonymousJSBundleLoaderDelegate.loadScriptFromFile(val$fileName, paramString2, paramBoolean);
        return val$fileName;
      }
    };
  }
  
  public static JSBundleLoader createRemoteDebuggerBundleLoader(final String paramString1, String paramString2)
  {
    new JSBundleLoader()
    {
      public String loadScript(JSBundleLoaderDelegate paramAnonymousJSBundleLoaderDelegate)
      {
        paramAnonymousJSBundleLoaderDelegate.setSourceURLs(val$realSourceURL, paramString1);
        return val$realSourceURL;
      }
    };
  }
  
  public abstract String loadScript(JSBundleLoaderDelegate paramJSBundleLoaderDelegate);
}
