package com.google.android.exoplayer2.upstream.cache;

import androidx.annotation.Nullable;
import java.io.File;

public class CacheSpan
  implements Comparable<CacheSpan>
{
  @Nullable
  public final File file;
  public final boolean isCached;
  public final String key;
  public final long lastAccessTimestamp;
  public final long length;
  public final long position;
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2)
  {
    this(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    key = paramString;
    position = paramLong1;
    length = paramLong2;
    boolean bool;
    if (paramFile != null) {
      bool = true;
    } else {
      bool = false;
    }
    isCached = bool;
    file = paramFile;
    lastAccessTimestamp = paramLong3;
  }
  
  public int compareTo(CacheSpan paramCacheSpan)
  {
    if (!key.equals(key)) {
      return key.compareTo(key);
    }
    boolean bool = position - position < 0L;
    if (!bool) {
      return 0;
    }
    if (bool) {
      return -1;
    }
    return 1;
  }
  
  public boolean isHoleSpan()
  {
    return isCached ^ true;
  }
  
  public boolean isOpenEnded()
  {
    return length == -1L;
  }
}
