package com.RNFetchBlob;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

class RNFetchBlobConfig
{
  public ReadableMap addAndroidDownloads;
  public String address;
  public String appendExt;
  public Boolean auto;
  public ReadableArray binaryContentTypes;
  public Boolean fileCache;
  public Boolean followRedirect;
  public Boolean increment;
  public String mime;
  public Boolean overwrite;
  public String path;
  public long timeout;
  public Boolean trusty;
  public Boolean wifiOnly;
  
  RNFetchBlobConfig(ReadableMap paramReadableMap)
  {
    boolean bool2 = false;
    wifiOnly = Boolean.valueOf(false);
    overwrite = Boolean.valueOf(true);
    timeout = 60000L;
    increment = Boolean.valueOf(false);
    followRedirect = Boolean.valueOf(true);
    Object localObject2 = null;
    binaryContentTypes = null;
    if (paramReadableMap == null) {
      return;
    }
    if (paramReadableMap.hasKey("fileCache")) {
      bool1 = paramReadableMap.getBoolean("fileCache");
    } else {
      bool1 = false;
    }
    fileCache = Boolean.valueOf(bool1);
    if (paramReadableMap.hasKey("path")) {
      localObject1 = paramReadableMap.getString("path");
    } else {
      localObject1 = null;
    }
    path = ((String)localObject1);
    if (paramReadableMap.hasKey("appendExt")) {
      localObject1 = paramReadableMap.getString("appendExt");
    } else {
      localObject1 = "";
    }
    appendExt = ((String)localObject1);
    if (paramReadableMap.hasKey("trusty")) {
      bool1 = paramReadableMap.getBoolean("trusty");
    } else {
      bool1 = false;
    }
    trusty = Boolean.valueOf(bool1);
    if (paramReadableMap.hasKey("wifiOnly")) {
      bool1 = paramReadableMap.getBoolean("wifiOnly");
    } else {
      bool1 = false;
    }
    wifiOnly = Boolean.valueOf(bool1);
    if (paramReadableMap.hasKey("addAndroidDownloads")) {
      addAndroidDownloads = paramReadableMap.getMap("addAndroidDownloads");
    }
    if (paramReadableMap.hasKey("binaryContentTypes")) {
      binaryContentTypes = paramReadableMap.getArray("binaryContentTypes");
    }
    if ((path != null) && (path.toLowerCase().contains("?append=true"))) {
      overwrite = Boolean.valueOf(false);
    }
    if (paramReadableMap.hasKey("overwrite")) {
      overwrite = Boolean.valueOf(paramReadableMap.getBoolean("overwrite"));
    }
    if (paramReadableMap.hasKey("followRedirect")) {
      followRedirect = Boolean.valueOf(paramReadableMap.getBoolean("followRedirect"));
    }
    if (paramReadableMap.hasKey("key")) {
      localObject1 = paramReadableMap.getString("key");
    } else {
      localObject1 = null;
    }
    address = ((String)localObject1);
    Object localObject1 = localObject2;
    if (paramReadableMap.hasKey("contentType")) {
      localObject1 = paramReadableMap.getString("contentType");
    }
    mime = ((String)localObject1);
    if (paramReadableMap.hasKey("increment")) {
      bool1 = paramReadableMap.getBoolean("increment");
    } else {
      bool1 = false;
    }
    increment = Boolean.valueOf(bool1);
    boolean bool1 = bool2;
    if (paramReadableMap.hasKey("auto")) {
      bool1 = paramReadableMap.getBoolean("auto");
    }
    auto = Boolean.valueOf(bool1);
    if (paramReadableMap.hasKey("timeout")) {
      timeout = paramReadableMap.getInt("timeout");
    }
  }
}
