package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.ParserException;
import java.io.IOException;

public class DefaultLoadErrorHandlingPolicy
  implements LoadErrorHandlingPolicy
{
  private static final int DEFAULT_BEHAVIOR_MIN_LOADABLE_RETRY_COUNT = -1;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_PROGRESSIVE_LIVE = 6;
  public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000L;
  private final int minimumLoadableRetryCount;
  
  public DefaultLoadErrorHandlingPolicy()
  {
    this(-1);
  }
  
  public DefaultLoadErrorHandlingPolicy(int paramInt)
  {
    minimumLoadableRetryCount = paramInt;
  }
  
  public long getBlacklistDurationMsFor(int paramInt1, long paramLong, IOException paramIOException, int paramInt2)
  {
    if ((paramIOException instanceof HttpDataSource.InvalidResponseCodeException))
    {
      paramInt1 = responseCode;
      if ((paramInt1 == 404) || (paramInt1 == 410)) {
        return 60000L;
      }
    }
    return -9223372036854775807L;
  }
  
  public int getMinimumLoadableRetryCount(int paramInt)
  {
    if (minimumLoadableRetryCount == -1)
    {
      if (paramInt == 7) {
        return 6;
      }
      return 3;
    }
    return minimumLoadableRetryCount;
  }
  
  public long getRetryDelayMsFor(int paramInt1, long paramLong, IOException paramIOException, int paramInt2)
  {
    if ((paramIOException instanceof ParserException)) {
      return -9223372036854775807L;
    }
    return Math.min((paramInt2 - 1) * 1000, 5000);
  }
}
