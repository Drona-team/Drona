package com.facebook.react.jstasks;

import com.facebook.react.bridge.WritableMap;

public class HeadlessJsTaskConfig
{
  private final boolean mAllowedInForeground;
  private final WritableMap mData;
  private final HeadlessJsTaskRetryPolicy mRetryPolicy;
  private final String mTaskKey;
  private final long mTimeout;
  
  public HeadlessJsTaskConfig(HeadlessJsTaskConfig paramHeadlessJsTaskConfig)
  {
    mTaskKey = mTaskKey;
    mData = mData.copy();
    mTimeout = mTimeout;
    mAllowedInForeground = mAllowedInForeground;
    paramHeadlessJsTaskConfig = mRetryPolicy;
    if (paramHeadlessJsTaskConfig != null)
    {
      mRetryPolicy = paramHeadlessJsTaskConfig.copy();
      return;
    }
    mRetryPolicy = null;
  }
  
  public HeadlessJsTaskConfig(String paramString, WritableMap paramWritableMap)
  {
    this(paramString, paramWritableMap, 0L, false);
  }
  
  public HeadlessJsTaskConfig(String paramString, WritableMap paramWritableMap, long paramLong)
  {
    this(paramString, paramWritableMap, paramLong, false);
  }
  
  public HeadlessJsTaskConfig(String paramString, WritableMap paramWritableMap, long paramLong, boolean paramBoolean)
  {
    this(paramString, paramWritableMap, paramLong, paramBoolean, NoRetryPolicy.INSTANCE);
  }
  
  public HeadlessJsTaskConfig(String paramString, WritableMap paramWritableMap, long paramLong, boolean paramBoolean, HeadlessJsTaskRetryPolicy paramHeadlessJsTaskRetryPolicy)
  {
    mTaskKey = paramString;
    mData = paramWritableMap;
    mTimeout = paramLong;
    mAllowedInForeground = paramBoolean;
    mRetryPolicy = paramHeadlessJsTaskRetryPolicy;
  }
  
  WritableMap getData()
  {
    return mData;
  }
  
  HeadlessJsTaskRetryPolicy getRetryPolicy()
  {
    return mRetryPolicy;
  }
  
  String getTaskKey()
  {
    return mTaskKey;
  }
  
  long getTimeout()
  {
    return mTimeout;
  }
  
  boolean isAllowedInForeground()
  {
    return mAllowedInForeground;
  }
}
