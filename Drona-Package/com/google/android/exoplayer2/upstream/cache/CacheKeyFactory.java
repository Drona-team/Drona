package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSpec;

public abstract interface CacheKeyFactory
{
  public abstract String buildCacheKey(DataSpec paramDataSpec);
}
