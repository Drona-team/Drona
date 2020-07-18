package com.google.android.exoplayer2.http.okhttp;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Call.Factory;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpDataSource
  extends BaseDataSource
  implements HttpDataSource
{
  private static final byte[] SKIP_BUFFER = new byte['?'];
  private long bytesRead;
  private long bytesSkipped;
  private long bytesToRead;
  private long bytesToSkip;
  @Nullable
  private final CacheControl cacheControl;
  private final Call.Factory callFactory;
  @Nullable
  private final Predicate<String> contentTypePredicate;
  @Nullable
  private DataSpec dataSpec;
  @Nullable
  private final HttpDataSource.RequestProperties defaultRequestProperties;
  private boolean opened;
  private final HttpDataSource.RequestProperties requestProperties;
  @Nullable
  private Response response;
  @Nullable
  private InputStream responseByteStream;
  @Nullable
  private final String userAgent;
  
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.okhttp");
  }
  
  public OkHttpDataSource(Call.Factory paramFactory, String paramString, Predicate paramPredicate)
  {
    this(paramFactory, paramString, paramPredicate, null, null);
  }
  
  public OkHttpDataSource(Call.Factory paramFactory, String paramString, Predicate paramPredicate, CacheControl paramCacheControl, HttpDataSource.RequestProperties paramRequestProperties)
  {
    super(true);
    callFactory = ((Call.Factory)Assertions.checkNotNull(paramFactory));
    userAgent = paramString;
    contentTypePredicate = paramPredicate;
    cacheControl = paramCacheControl;
    defaultRequestProperties = paramRequestProperties;
    requestProperties = new HttpDataSource.RequestProperties();
  }
  
  private void closeConnectionQuietly()
  {
    if (response != null)
    {
      ((ResponseBody)Assertions.checkNotNull(response.body())).close();
      response = null;
    }
    responseByteStream = null;
  }
  
  private Request makeRequest(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException
  {
    long l1 = position;
    long l2 = length;
    boolean bool = paramDataSpec.isFlagSet(1);
    Object localObject1 = HttpUrl.parse(uri.toString());
    if (localObject1 != null)
    {
      Request.Builder localBuilder = new Request.Builder().url((HttpUrl)localObject1);
      if (cacheControl != null) {
        localBuilder.cacheControl(cacheControl);
      }
      if (defaultRequestProperties != null)
      {
        localObject1 = defaultRequestProperties.getSnapshot().entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          localBuilder.header((String)((Map.Entry)localObject2).getKey(), (String)((Map.Entry)localObject2).getValue());
        }
      }
      localObject1 = requestProperties.getSnapshot().entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localBuilder.header((String)((Map.Entry)localObject2).getKey(), (String)((Map.Entry)localObject2).getValue());
      }
      if ((l1 != 0L) || (l2 != -1L))
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("bytes=");
        ((StringBuilder)localObject1).append(l1);
        ((StringBuilder)localObject1).append("-");
        localObject2 = ((StringBuilder)localObject1).toString();
        localObject1 = localObject2;
        if (l2 != -1L)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append((String)localObject2);
          ((StringBuilder)localObject1).append(l1 + l2 - 1L);
          localObject1 = ((StringBuilder)localObject1).toString();
        }
        localBuilder.addHeader("Range", (String)localObject1);
      }
      if (userAgent != null) {
        localBuilder.addHeader("User-Agent", userAgent);
      }
      if (!bool) {
        localBuilder.addHeader("Accept-Encoding", "identity");
      }
      Object localObject2 = httpBody;
      localObject1 = null;
      if (localObject2 != null) {
        localObject1 = RequestBody.create(null, httpBody);
      } else if (httpMethod == 2) {
        localObject1 = RequestBody.create(null, Util.EMPTY_BYTE_ARRAY);
      }
      localBuilder.method(paramDataSpec.getHttpMethodString(), (RequestBody)localObject1);
      return localBuilder.build();
    }
    throw new HttpDataSource.HttpDataSourceException("Malformed URL", paramDataSpec, 1);
  }
  
  private int readInternal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    int i = paramInt2;
    if (bytesToRead != -1L)
    {
      long l = bytesToRead - bytesRead;
      if (l == 0L) {
        return -1;
      }
      i = (int)Math.min(paramInt2, l);
    }
    paramInt1 = ((InputStream)Util.castNonNull(responseByteStream)).read(paramArrayOfByte, paramInt1, i);
    if (paramInt1 == -1)
    {
      if (bytesToRead == -1L) {
        return -1;
      }
      throw new EOFException();
    }
    bytesRead += paramInt1;
    bytesTransferred(paramInt1);
    return paramInt1;
  }
  
  private void skipInternal()
    throws IOException
  {
    if (bytesSkipped == bytesToSkip) {
      return;
    }
    while (bytesSkipped != bytesToSkip)
    {
      int i = (int)Math.min(bytesToSkip - bytesSkipped, SKIP_BUFFER.length);
      i = ((InputStream)Util.castNonNull(responseByteStream)).read(SKIP_BUFFER, 0, i);
      if (!Thread.currentThread().isInterrupted())
      {
        if (i != -1)
        {
          bytesSkipped += i;
          bytesTransferred(i);
        }
        else
        {
          throw new EOFException();
        }
      }
      else {
        throw new InterruptedIOException();
      }
    }
  }
  
  protected final long bytesRead()
  {
    return bytesRead;
  }
  
  protected final long bytesRemaining()
  {
    if (bytesToRead == -1L) {
      return bytesToRead;
    }
    return bytesToRead - bytesRead;
  }
  
  protected final long bytesSkipped()
  {
    return bytesSkipped;
  }
  
  public void clearAllRequestProperties()
  {
    requestProperties.clear();
  }
  
  public void clearRequestProperty(String paramString)
  {
    Assertions.checkNotNull(paramString);
    requestProperties.remove(paramString);
  }
  
  public void close()
    throws HttpDataSource.HttpDataSourceException
  {
    if (opened)
    {
      opened = false;
      transferEnded();
      closeConnectionQuietly();
    }
  }
  
  public Map getResponseHeaders()
  {
    if (response == null) {
      return Collections.emptyMap();
    }
    return response.headers().toMultimap();
  }
  
  public Uri getUri()
  {
    if (response == null) {
      return null;
    }
    return Uri.parse(response.request().url().toString());
  }
  
  public long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException
  {
    dataSpec = paramDataSpec;
    long l2 = 0L;
    bytesRead = 0L;
    bytesSkipped = 0L;
    transferInitializing(paramDataSpec);
    Object localObject1 = makeRequest(paramDataSpec);
    Object localObject2 = callFactory;
    try
    {
      localObject1 = ((Call.Factory)localObject2).newCall((Request)localObject1).execute();
      response = ((Response)localObject1);
      localObject1 = response;
      localObject2 = Assertions.checkNotNull(((Response)localObject1).body());
      localObject2 = (ResponseBody)localObject2;
      InputStream localInputStream = ((ResponseBody)localObject2).byteStream();
      responseByteStream = localInputStream;
      int i = ((Response)localObject1).code();
      if (!((Response)localObject1).isSuccessful())
      {
        localObject2 = ((Response)localObject1).headers().toMultimap();
        closeConnectionQuietly();
        paramDataSpec = new HttpDataSource.InvalidResponseCodeException(i, ((Response)localObject1).message(), (Map)localObject2, paramDataSpec);
        if (i == 416) {
          paramDataSpec.initCause(new DataSourceException(0));
        }
        throw paramDataSpec;
      }
      localObject1 = ((ResponseBody)localObject2).contentType();
      if (localObject1 != null) {
        localObject1 = ((MediaType)localObject1).toString();
      } else {
        localObject1 = "";
      }
      if ((contentTypePredicate != null) && (!contentTypePredicate.evaluate(localObject1)))
      {
        closeConnectionQuietly();
        throw new HttpDataSource.InvalidContentTypeException((String)localObject1, paramDataSpec);
      }
      long l1 = l2;
      if (i == 200)
      {
        l1 = l2;
        if (position != 0L) {
          l1 = position;
        }
      }
      bytesToSkip = l1;
      l2 = length;
      l1 = -1L;
      if (l2 != -1L)
      {
        bytesToRead = length;
      }
      else
      {
        l2 = ((ResponseBody)localObject2).contentLength();
        if (l2 != -1L) {
          l1 = l2 - bytesToSkip;
        }
        bytesToRead = l1;
      }
      opened = true;
      transferStarted(paramDataSpec);
      return bytesToRead;
    }
    catch (IOException localIOException)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Unable to connect to ");
      ((StringBuilder)localObject2).append(uri);
      throw new HttpDataSource.HttpDataSourceException(((StringBuilder)localObject2).toString(), localIOException, paramDataSpec, 1);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws HttpDataSource.HttpDataSourceException
  {
    try
    {
      skipInternal();
      paramInt1 = readInternal(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new HttpDataSource.HttpDataSourceException(paramArrayOfByte, (DataSpec)Assertions.checkNotNull(dataSpec), 2);
    }
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    Assertions.checkNotNull(paramString1);
    Assertions.checkNotNull(paramString2);
    requestProperties.put(paramString1, paramString2);
  }
}
