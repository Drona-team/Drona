package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class ParsingLoadable<T>
  implements Loader.Loadable
{
  private final StatsDataSource dataSource;
  public final DataSpec dataSpec;
  private final Parser<? extends T> parser;
  @Nullable
  private volatile T result;
  public final int type;
  
  public ParsingLoadable(DataSource paramDataSource, Uri paramUri, int paramInt, Parser paramParser)
  {
    this(paramDataSource, new DataSpec(paramUri, 3), paramInt, paramParser);
  }
  
  public ParsingLoadable(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt, Parser paramParser)
  {
    dataSource = new StatsDataSource(paramDataSource);
    dataSpec = paramDataSpec;
    type = paramInt;
    parser = paramParser;
  }
  
  public static Object load(DataSource paramDataSource, Parser paramParser, Uri paramUri, int paramInt)
    throws IOException
  {
    paramDataSource = new ParsingLoadable(paramDataSource, paramUri, paramInt, paramParser);
    paramDataSource.load();
    return Assertions.checkNotNull(paramDataSource.getResult());
  }
  
  public long bytesLoaded()
  {
    return dataSource.getBytesRead();
  }
  
  public final void cancelLoad() {}
  
  public Map getResponseHeaders()
  {
    return dataSource.getLastResponseHeaders();
  }
  
  public final Object getResult()
  {
    return result;
  }
  
  public Uri getUri()
  {
    return dataSource.getLastOpenedUri();
  }
  
  public final void load()
    throws IOException
  {
    dataSource.resetBytesRead();
    DataSourceInputStream localDataSourceInputStream = new DataSourceInputStream(dataSource, dataSpec);
    try
    {
      localDataSourceInputStream.open();
      Uri localUri = (Uri)Assertions.checkNotNull(dataSource.getUri());
      result = parser.parse(localUri, localDataSourceInputStream);
      Util.closeQuietly(localDataSourceInputStream);
      return;
    }
    catch (Throwable localThrowable)
    {
      Util.closeQuietly(localDataSourceInputStream);
      throw localThrowable;
    }
  }
  
  public static abstract interface Parser<T>
  {
    public abstract Object parse(Uri paramUri, InputStream paramInputStream)
      throws IOException;
  }
}
