package com.bumptech.glide.load.data;

import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.LogTime;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUrlFetcher
  implements DataFetcher<InputStream>
{
  @VisibleForTesting
  static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory();
  private static final int INVALID_STATUS_CODE = -1;
  private static final int MAXIMUM_REDIRECTS = 5;
  private static final String TAG = "HttpUrlFetcher";
  private final HttpUrlConnectionFactory connectionFactory;
  private final GlideUrl glideUrl;
  private volatile boolean isCancelled;
  private InputStream stream;
  private final int timeout;
  private HttpURLConnection urlConnection;
  
  public HttpUrlFetcher(GlideUrl paramGlideUrl, int paramInt)
  {
    this(paramGlideUrl, paramInt, DEFAULT_CONNECTION_FACTORY);
  }
  
  HttpUrlFetcher(GlideUrl paramGlideUrl, int paramInt, HttpUrlConnectionFactory paramHttpUrlConnectionFactory)
  {
    glideUrl = paramGlideUrl;
    timeout = paramInt;
    connectionFactory = paramHttpUrlConnectionFactory;
  }
  
  private InputStream getStreamForSuccessfulRequest(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    if (TextUtils.isEmpty(paramHttpURLConnection.getContentEncoding()))
    {
      int i = paramHttpURLConnection.getContentLength();
      stream = ContentLengthInputStream.obtain(paramHttpURLConnection.getInputStream(), i);
    }
    else
    {
      if (Log.isLoggable("HttpUrlFetcher", 3))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Got non empty content encoding: ");
        localStringBuilder.append(paramHttpURLConnection.getContentEncoding());
        Log.d("HttpUrlFetcher", localStringBuilder.toString());
      }
      stream = paramHttpURLConnection.getInputStream();
    }
    return stream;
  }
  
  private static boolean isHttpOk(int paramInt)
  {
    return paramInt / 100 == 2;
  }
  
  private static boolean isHttpRedirect(int paramInt)
  {
    return paramInt / 100 == 3;
  }
  
  private InputStream loadDataWithRedirects(URL paramURL1, int paramInt, URL paramURL2, Map paramMap)
    throws IOException
  {
    if ((paramInt >= 5) || (paramURL2 != null)) {}
    try
    {
      boolean bool = paramURL1.toURI().equals(paramURL2.toURI());
      if (bool)
      {
        paramURL2 = new HttpException("In re-direct loop");
        throw paramURL2;
      }
    }
    catch (URISyntaxException paramURL2)
    {
      int i;
      for (;;) {}
    }
    urlConnection = connectionFactory.build(paramURL1);
    paramURL2 = paramMap.entrySet().iterator();
    while (paramURL2.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramURL2.next();
      urlConnection.addRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
    }
    urlConnection.setConnectTimeout(timeout);
    urlConnection.setReadTimeout(timeout);
    urlConnection.setUseCaches(false);
    urlConnection.setDoInput(true);
    urlConnection.setInstanceFollowRedirects(false);
    urlConnection.connect();
    stream = urlConnection.getInputStream();
    if (isCancelled) {
      return null;
    }
    i = urlConnection.getResponseCode();
    if (isHttpOk(i)) {
      return getStreamForSuccessfulRequest(urlConnection);
    }
    if (isHttpRedirect(i))
    {
      paramURL2 = urlConnection.getHeaderField("Location");
      if (!TextUtils.isEmpty(paramURL2))
      {
        paramURL2 = new URL(paramURL1, paramURL2);
        cleanup();
        return loadDataWithRedirects(paramURL2, paramInt + 1, paramURL1, paramMap);
      }
      throw new HttpException("Received empty or null redirect url");
    }
    if (i == -1) {
      throw new HttpException(i);
    }
    throw new HttpException(urlConnection.getResponseMessage(), i);
    throw new HttpException("Too many (> 5) redirects!");
  }
  
  public void cancel()
  {
    isCancelled = true;
  }
  
  public void cleanup()
  {
    InputStream localInputStream;
    if (stream != null) {
      localInputStream = stream;
    }
    try
    {
      localInputStream.close();
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    if (urlConnection != null) {
      urlConnection.disconnect();
    }
    urlConnection = null;
  }
  
  public Class getDataClass()
  {
    return InputStream.class;
  }
  
  public DataSource getDataSource()
  {
    return DataSource.REMOTE;
  }
  
  public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
  {
    long l = LogTime.getLogTime();
    try
    {
      paramPriority = glideUrl;
      GlideUrl localGlideUrl;
      boolean bool;
      paramPriority = new StringBuilder();
    }
    catch (Throwable paramPriority)
    {
      try
      {
        paramPriority = paramPriority.toURL();
        localGlideUrl = glideUrl;
        paramDataCallback.onDataReady(loadDataWithRedirects(paramPriority, 0, null, localGlideUrl.getHeaders()));
        if (!Log.isLoggable("HttpUrlFetcher", 2)) {
          return;
        }
        paramPriority = new StringBuilder();
      }
      catch (IOException paramPriority)
      {
        bool = Log.isLoggable("HttpUrlFetcher", 3);
        if (!bool) {
          break label86;
        }
        Log.d("HttpUrlFetcher", "Failed to load data for url", paramPriority);
        paramDataCallback.onLoadFailed(paramPriority);
        if (!Log.isLoggable("HttpUrlFetcher", 2)) {
          return;
        }
      }
      paramPriority = paramPriority;
    }
    label86:
    paramPriority.append("Finished http url fetcher fetch in ");
    paramPriority.append(LogTime.getElapsedMillis(l));
    Log.v("HttpUrlFetcher", paramPriority.toString());
    return;
    if (Log.isLoggable("HttpUrlFetcher", 2))
    {
      paramDataCallback = new StringBuilder();
      paramDataCallback.append("Finished http url fetcher fetch in ");
      paramDataCallback.append(LogTime.getElapsedMillis(l));
      Log.v("HttpUrlFetcher", paramDataCallback.toString());
    }
    throw paramPriority;
  }
  
  private static class DefaultHttpUrlConnectionFactory
    implements HttpUrlFetcher.HttpUrlConnectionFactory
  {
    DefaultHttpUrlConnectionFactory() {}
    
    public HttpURLConnection build(URL paramURL)
      throws IOException
    {
      return (HttpURLConnection)paramURL.openConnection();
    }
  }
  
  static abstract interface HttpUrlConnectionFactory
  {
    public abstract HttpURLConnection build(URL paramURL)
      throws IOException;
  }
}
