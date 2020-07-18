package com.facebook.react.bridge;

import android.content.res.AssetManager;

public abstract interface JSBundleLoaderDelegate
{
  public abstract void loadScriptFromAssets(AssetManager paramAssetManager, String paramString, boolean paramBoolean);
  
  public abstract void loadScriptFromDeltaBundle(String paramString, NativeDeltaClient paramNativeDeltaClient, boolean paramBoolean);
  
  public abstract void loadScriptFromFile(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract void setSourceURLs(String paramString1, String paramString2);
}
