package com.google.android.exoplayer2.source.chunk;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Map;

public abstract class Chunk
  implements Loader.Loadable
{
  protected final StatsDataSource dataSource;
  public final DataSpec dataSpec;
  public final long endTimeUs;
  public final long startTimeUs;
  public final Format trackFormat;
  @Nullable
  public final Object trackSelectionData;
  public final int trackSelectionReason;
  public final int type;
  
  public Chunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong1, long paramLong2)
  {
    dataSource = new StatsDataSource(paramDataSource);
    dataSpec = ((DataSpec)Assertions.checkNotNull(paramDataSpec));
    type = paramInt1;
    trackFormat = paramFormat;
    trackSelectionReason = paramInt2;
    trackSelectionData = paramObject;
    startTimeUs = paramLong1;
    endTimeUs = paramLong2;
  }
  
  public final long bytesLoaded()
  {
    return dataSource.getBytesRead();
  }
  
  public final long getDurationUs()
  {
    return endTimeUs - startTimeUs;
  }
  
  public final Map getResponseHeaders()
  {
    return dataSource.getLastResponseHeaders();
  }
  
  public final Uri getUri()
  {
    return dataSource.getLastOpenedUri();
  }
}
