package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SimpleCacheSpan
  extends CacheSpan
{
  private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V3 = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.v3\\.exo$", 32);
  private static final String SUFFIX = ".v3.exo";
  
  private SimpleCacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    super(paramString, paramLong1, paramLong2, paramLong3, paramFile);
  }
  
  public static SimpleCacheSpan createCacheEntry(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    String str2 = paramFile.getName();
    String str1 = str2;
    File localFile = paramFile;
    if (!str2.endsWith(".v3.exo"))
    {
      paramFile = upgradeFile(paramFile, paramCachedContentIndex);
      localFile = paramFile;
      if (paramFile == null) {
        return null;
      }
      str1 = paramFile.getName();
    }
    paramFile = CACHE_FILE_PATTERN_V3.matcher(str1);
    if (!paramFile.matches()) {
      return null;
    }
    long l = localFile.length();
    paramCachedContentIndex = paramCachedContentIndex.getKeyForId(Integer.parseInt(paramFile.group(1)));
    if (paramCachedContentIndex == null) {
      return null;
    }
    return new SimpleCacheSpan(paramCachedContentIndex, Long.parseLong(paramFile.group(2)), l, Long.parseLong(paramFile.group(3)), localFile);
  }
  
  public static SimpleCacheSpan createClosedHole(String paramString, long paramLong1, long paramLong2)
  {
    return new SimpleCacheSpan(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createLookup(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createOpenHole(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static File getCacheFile(File paramFile, int paramInt, long paramLong1, long paramLong2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramInt);
    localStringBuilder.append(".");
    localStringBuilder.append(paramLong1);
    localStringBuilder.append(".");
    localStringBuilder.append(paramLong2);
    localStringBuilder.append(".v3.exo");
    return new File(paramFile, localStringBuilder.toString());
  }
  
  private static File upgradeFile(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    String str = paramFile.getName();
    Object localObject2 = CACHE_FILE_PATTERN_V2.matcher(str);
    Object localObject1 = localObject2;
    if (((Matcher)localObject2).matches())
    {
      str = Util.unescapeFileName(((Matcher)localObject2).group(1));
      localObject2 = str;
      if (str == null) {
        return null;
      }
    }
    else
    {
      localObject2 = CACHE_FILE_PATTERN_V1.matcher(str);
      localObject1 = localObject2;
      if (!((Matcher)localObject2).matches()) {
        return null;
      }
      localObject2 = ((Matcher)localObject2).group(1);
    }
    paramCachedContentIndex = getCacheFile(paramFile.getParentFile(), paramCachedContentIndex.assignIdForKey((String)localObject2), Long.parseLong(localObject1.group(2)), Long.parseLong(localObject1.group(3)));
    if (!paramFile.renameTo(paramCachedContentIndex)) {
      return null;
    }
    return paramCachedContentIndex;
  }
  
  public SimpleCacheSpan copyWithUpdatedLastAccessTime(int paramInt)
  {
    Assertions.checkState(isCached);
    long l = System.currentTimeMillis();
    File localFile = getCacheFile(file.getParentFile(), paramInt, position, l);
    return new SimpleCacheSpan(key, position, length, l, localFile);
  }
}
