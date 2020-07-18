package com.google.android.exoplayer2.upstream.cache;

public abstract interface CacheEvictor
  extends Cache.Listener
{
  public abstract void onCacheInitialized();
  
  public abstract void onStartFile(Cache paramCache, String paramString, long paramLong1, long paramLong2);
}
