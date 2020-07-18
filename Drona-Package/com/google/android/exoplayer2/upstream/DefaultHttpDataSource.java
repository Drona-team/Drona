package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultHttpDataSource
  extends BaseDataSource
  implements HttpDataSource
{
  private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
  public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
  private static final int HTTP_STATUS_PERMANENT_REDIRECT = 308;
  private static final int HTTP_STATUS_TEMPORARY_REDIRECT = 307;
  private static final long MAX_BYTES_TO_DRAIN = 2048L;
  private static final int MAX_REDIRECTS = 20;
  private static final String TAG = "DefaultHttpDataSource";
  private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference();
  private final boolean allowCrossProtocolRedirects;
  private long bytesRead;
  private long bytesSkipped;
  private long bytesToRead;
  private long bytesToSkip;
  private final int connectTimeoutMillis;
  @Nullable
  private HttpURLConnection connection;
  @Nullable
  private final Predicate<String> contentTypePredicate;
  @Nullable
  private DataSpec dataSpec;
  @Nullable
  private final HttpDataSource.RequestProperties defaultRequestProperties;
  @Nullable
  private InputStream inputStream;
  private boolean opened;
  private final int readTimeoutMillis;
  private final HttpDataSource.RequestProperties requestProperties;
  private final String userAgent;
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate)
  {
    this(paramString, paramPredicate, 8000, 8000);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate, int paramInt1, int paramInt2)
  {
    this(paramString, paramPredicate, paramInt1, paramInt2, false, null);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate, int paramInt1, int paramInt2, boolean paramBoolean, HttpDataSource.RequestProperties paramRequestProperties)
  {
    super(true);
    userAgent = Assertions.checkNotEmpty(paramString);
    contentTypePredicate = paramPredicate;
    requestProperties = new HttpDataSource.RequestProperties();
    connectTimeoutMillis = paramInt1;
    readTimeoutMillis = paramInt2;
    allowCrossProtocolRedirects = paramBoolean;
    defaultRequestProperties = paramRequestProperties;
  }
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate, TransferListener paramTransferListener)
  {
    this(paramString, paramPredicate, paramTransferListener, 8000, 8000);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate, TransferListener paramTransferListener, int paramInt1, int paramInt2)
  {
    this(paramString, paramPredicate, paramTransferListener, paramInt1, paramInt2, false, null);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate paramPredicate, TransferListener paramTransferListener, int paramInt1, int paramInt2, boolean paramBoolean, HttpDataSource.RequestProperties paramRequestProperties)
  {
    this(paramString, paramPredicate, paramInt1, paramInt2, paramBoolean, paramRequestProperties);
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  private void closeConnectionQuietly()
  {
    if (connection != null)
    {
      HttpURLConnection localHttpURLConnection = connection;
      try
      {
        localHttpURLConnection.disconnect();
      }
      catch (Exception localException)
      {
        Log.e("DefaultHttpDataSource", "Unexpected error while disconnecting", localException);
      }
      connection = null;
    }
  }
  
  private static long getContentLength(HttpURLConnection paramHttpURLConnection)
  {
    Object localObject1 = paramHttpURLConnection.getHeaderField("Content-Length");
    if (!TextUtils.isEmpty((CharSequence)localObject1)) {}
    try
    {
      l1 = Long.parseLong((String)localObject1);
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      Object localObject2;
      for (;;) {}
    }
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("Unexpected Content-Length [");
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("]");
    Log.e("DefaultHttpDataSource", ((StringBuilder)localObject2).toString());
    long l1 = -1L;
    paramHttpURLConnection = paramHttpURLConnection.getHeaderField("Content-Range");
    if (!TextUtils.isEmpty(paramHttpURLConnection))
    {
      localObject2 = CONTENT_RANGE_HEADER.matcher(paramHttpURLConnection);
      if (((Matcher)localObject2).find())
      {
        try
        {
          long l2 = Long.parseLong(((Matcher)localObject2).group(2));
          long l3 = Long.parseLong(((Matcher)localObject2).group(1));
          l2 = l2 - l3 + 1L;
          if (l1 < 0L) {
            return l2;
          }
          if (l1 == l2) {
            return l1;
          }
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Inconsistent headers [");
          ((StringBuilder)localObject2).append((String)localObject1);
          ((StringBuilder)localObject2).append("] [");
          ((StringBuilder)localObject2).append(paramHttpURLConnection);
          ((StringBuilder)localObject2).append("]");
          Log.w("DefaultHttpDataSource", ((StringBuilder)localObject2).toString());
          l2 = Math.max(l1, l2);
          return l2;
        }
        catch (NumberFormatException localNumberFormatException1)
        {
          for (;;) {}
        }
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Unexpected Content-Range [");
        ((StringBuilder)localObject1).append(paramHttpURLConnection);
        ((StringBuilder)localObject1).append("]");
        Log.e("DefaultHttpDataSource", ((StringBuilder)localObject1).toString());
        return l1;
      }
    }
    return l1;
  }
  
  private static URL handleRedirect(URL paramURL, String paramString)
    throws IOException
  {
    if (paramString != null)
    {
      paramString = new URL(paramURL, paramString);
      paramURL = paramString.getProtocol();
      if (!"https".equals(paramURL))
      {
        if ("http".equals(paramURL)) {
          return paramString;
        }
        paramString = new StringBuilder();
        paramString.append("Unsupported protocol redirect: ");
        paramString.append(paramURL);
        throw new ProtocolException(paramString.toString());
      }
    }
    else
    {
      throw new ProtocolException("Null location redirect");
    }
    return paramString;
  }
  
  private HttpURLConnection makeConnection(DataSpec paramDataSpec)
    throws IOException
  {
    Object localObject1 = new URL(uri.toString());
    int i = httpMethod;
    Object localObject2 = httpBody;
    long l2 = position;
    long l1 = length;
    boolean bool = paramDataSpec.isFlagSet(1);
    if (!allowCrossProtocolRedirects) {
      return makeConnection((URL)localObject1, i, (byte[])localObject2, l2, l1, bool, true);
    }
    int j = 0;
    int k;
    for (paramDataSpec = (DataSpec)localObject2;; paramDataSpec = (DataSpec)localObject2)
    {
      k = j + 1;
      if (j > 20) {
        break;
      }
      localObject2 = makeConnection((URL)localObject1, i, paramDataSpec, l2, l1, bool, false);
      j = ((HttpURLConnection)localObject2).getResponseCode();
      String str = ((HttpURLConnection)localObject2).getHeaderField("Location");
      if (((i != 1) && (i != 3)) || ((j != 300) && (j != 301) && (j != 302) && (j != 303) && (j != 307) && (j != 308)))
      {
        if (i == 2)
        {
          if ((j != 300) && (j != 301) && (j != 302) && (j != 303)) {
            break label298;
          }
          ((HttpURLConnection)localObject2).disconnect();
          paramDataSpec = handleRedirect((URL)localObject1, str);
          localObject2 = null;
          i = 1;
        }
        else
        {
          return localObject2;
        }
      }
      else
      {
        ((HttpURLConnection)localObject2).disconnect();
        localObject1 = handleRedirect((URL)localObject1, str);
        localObject2 = paramDataSpec;
        paramDataSpec = (DataSpec)localObject1;
      }
      j = k;
      localObject1 = paramDataSpec;
    }
    paramDataSpec = new StringBuilder();
    paramDataSpec.append("Too many redirects: ");
    paramDataSpec.append(k);
    throw new NoRouteToHostException(paramDataSpec.toString());
    label298:
    return localObject2;
  }
  
  private HttpURLConnection makeConnection(URL paramURL, int paramInt, byte[] paramArrayOfByte, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
    localHttpURLConnection.setConnectTimeout(connectTimeoutMillis);
    localHttpURLConnection.setReadTimeout(readTimeoutMillis);
    Object localObject;
    if (defaultRequestProperties != null)
    {
      paramURL = defaultRequestProperties.getSnapshot().entrySet().iterator();
      while (paramURL.hasNext())
      {
        localObject = (Map.Entry)paramURL.next();
        localHttpURLConnection.setRequestProperty((String)((Map.Entry)localObject).getKey(), (String)((Map.Entry)localObject).getValue());
      }
    }
    paramURL = requestProperties.getSnapshot().entrySet().iterator();
    while (paramURL.hasNext())
    {
      localObject = (Map.Entry)paramURL.next();
      localHttpURLConnection.setRequestProperty((String)((Map.Entry)localObject).getKey(), (String)((Map.Entry)localObject).getValue());
    }
    if ((paramLong1 != 0L) || (paramLong2 != -1L))
    {
      paramURL = new StringBuilder();
      paramURL.append("bytes=");
      paramURL.append(paramLong1);
      paramURL.append("-");
      localObject = paramURL.toString();
      paramURL = (URL)localObject;
      if (paramLong2 != -1L)
      {
        paramURL = new StringBuilder();
        paramURL.append((String)localObject);
        paramURL.append(paramLong1 + paramLong2 - 1L);
        paramURL = paramURL.toString();
      }
      localHttpURLConnection.setRequestProperty("Range", paramURL);
    }
    localHttpURLConnection.setRequestProperty("User-Agent", userAgent);
    if (!paramBoolean1) {
      localHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
    }
    localHttpURLConnection.setInstanceFollowRedirects(paramBoolean2);
    if (paramArrayOfByte != null) {
      paramBoolean1 = true;
    } else {
      paramBoolean1 = false;
    }
    localHttpURLConnection.setDoOutput(paramBoolean1);
    localHttpURLConnection.setRequestMethod(DataSpec.getStringForHttpMethod(paramInt));
    if (paramArrayOfByte != null)
    {
      localHttpURLConnection.setFixedLengthStreamingMode(paramArrayOfByte.length);
      localHttpURLConnection.connect();
      paramURL = localHttpURLConnection.getOutputStream();
      paramURL.write(paramArrayOfByte);
      paramURL.close();
      return localHttpURLConnection;
    }
    localHttpURLConnection.connect();
    return localHttpURLConnection;
  }
  
  private static void maybeTerminateInputStream(HttpURLConnection paramHttpURLConnection, long paramLong)
  {
    if ((Util.SDK_INT != 19) && (Util.SDK_INT != 20)) {
      return;
    }
    try
    {
      paramHttpURLConnection = paramHttpURLConnection.getInputStream();
      if (paramLong == -1L)
      {
        int i = paramHttpURLConnection.read();
        if (i != -1) {}
      }
      else if (paramLong <= 2048L)
      {
        return;
      }
      Object localObject = paramHttpURLConnection.getClass().getName();
      boolean bool = "com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream".equals(localObject);
      if (!bool)
      {
        bool = "com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream".equals(localObject);
        if (!bool) {}
      }
      else
      {
        localObject = paramHttpURLConnection.getClass().getSuperclass();
        localObject = ((Class)localObject).getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
        ((Method)localObject).setAccessible(true);
        ((Method)localObject).invoke(paramHttpURLConnection, new Object[0]);
        return;
      }
    }
    catch (Exception paramHttpURLConnection) {}
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
    paramInt1 = inputStream.read(paramArrayOfByte, paramInt1, i);
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
    long l = bytesSkipped;
    Object localObject2 = this;
    if (l == bytesToSkip) {
      return;
    }
    byte[] arrayOfByte2 = (byte[])skipBufferReference.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    Object localObject1 = localObject2;
    if (arrayOfByte2 == null)
    {
      arrayOfByte1 = new byte['?'];
      localObject1 = localObject2;
    }
    while (bytesSkipped != bytesToSkip)
    {
      l = bytesToSkip;
      localObject2 = localObject1;
      int i = (int)Math.min(l - bytesSkipped, arrayOfByte1.length);
      i = inputStream.read(arrayOfByte1, 0, i);
      if (!Thread.currentThread().isInterrupted())
      {
        if (i != -1)
        {
          bytesSkipped += i;
          ((BaseDataSource)localObject2).bytesTransferred(i);
          localObject1 = localObject2;
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
    skipBufferReference.set(arrayOfByte1);
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
    try
    {
      InputStream localInputStream = inputStream;
      if (localInputStream != null)
      {
        maybeTerminateInputStream(connection, bytesRemaining());
        localInputStream = inputStream;
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException)
        {
          throw new HttpDataSource.HttpDataSourceException(localIOException, dataSpec, 3);
        }
      }
      inputStream = null;
      closeConnectionQuietly();
      if (opened)
      {
        opened = false;
        transferEnded();
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      inputStream = null;
      closeConnectionQuietly();
      if (opened)
      {
        opened = false;
        transferEnded();
      }
      throw localThrowable;
    }
  }
  
  protected final HttpURLConnection getConnection()
  {
    return connection;
  }
  
  public Map getResponseHeaders()
  {
    if (connection == null) {
      return Collections.emptyMap();
    }
    return connection.getHeaderFields();
  }
  
  public Uri getUri()
  {
    if (connection == null) {
      return null;
    }
    return Uri.parse(connection.getURL().toString());
  }
  
  /* Error */
  public long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 497	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   5: lconst_0
    //   6: lstore 5
    //   8: aload_0
    //   9: lconst_0
    //   10: putfield 434	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesRead	J
    //   13: aload_0
    //   14: lconst_0
    //   15: putfield 450	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesSkipped	J
    //   18: aload_0
    //   19: aload_1
    //   20: invokevirtual 535	com/google/android/exoplayer2/upstream/BaseDataSource:transferInitializing	(Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   23: aload_0
    //   24: aload_1
    //   25: invokespecial 537	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:makeConnection	(Lcom/google/android/exoplayer2/upstream/DataSpec;)Ljava/net/HttpURLConnection;
    //   28: astore 7
    //   30: aload_0
    //   31: aload 7
    //   33: putfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   36: aload_0
    //   37: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   40: astore 7
    //   42: aload 7
    //   44: invokevirtual 274	java/net/HttpURLConnection:getResponseCode	()I
    //   47: istore_2
    //   48: aload_0
    //   49: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   52: astore 7
    //   54: aload 7
    //   56: invokevirtual 540	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
    //   59: astore 7
    //   61: iload_2
    //   62: sipush 200
    //   65: if_icmplt +225 -> 290
    //   68: iload_2
    //   69: sipush 299
    //   72: if_icmple +6 -> 78
    //   75: goto +215 -> 290
    //   78: aload_0
    //   79: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   82: invokevirtual 543	java/net/HttpURLConnection:getContentType	()Ljava/lang/String;
    //   85: astore 7
    //   87: aload_0
    //   88: getfield 92	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:contentTypePredicate	Lcom/google/android/exoplayer2/util/Predicate;
    //   91: ifnull +35 -> 126
    //   94: aload_0
    //   95: getfield 92	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:contentTypePredicate	Lcom/google/android/exoplayer2/util/Predicate;
    //   98: aload 7
    //   100: invokeinterface 548 2 0
    //   105: ifeq +6 -> 111
    //   108: goto +18 -> 126
    //   111: aload_0
    //   112: invokespecial 502	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   115: new 550	com/google/android/exoplayer2/upstream/HttpDataSource$InvalidContentTypeException
    //   118: dup
    //   119: aload 7
    //   121: aload_1
    //   122: invokespecial 553	com/google/android/exoplayer2/upstream/HttpDataSource$InvalidContentTypeException:<init>	(Ljava/lang/String;Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   125: athrow
    //   126: lload 5
    //   128: lstore_3
    //   129: iload_2
    //   130: sipush 200
    //   133: if_icmpne +20 -> 153
    //   136: lload 5
    //   138: lstore_3
    //   139: aload_1
    //   140: getfield 260	com/google/android/exoplayer2/upstream/DataSpec:position	J
    //   143: lconst_0
    //   144: lcmp
    //   145: ifeq +8 -> 153
    //   148: aload_1
    //   149: getfield 260	com/google/android/exoplayer2/upstream/DataSpec:position	J
    //   152: lstore_3
    //   153: aload_0
    //   154: lload_3
    //   155: putfield 452	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToSkip	J
    //   158: aload_1
    //   159: iconst_1
    //   160: invokevirtual 267	com/google/android/exoplayer2/upstream/DataSpec:isFlagSet	(I)Z
    //   163: ifne +67 -> 230
    //   166: aload_1
    //   167: getfield 263	com/google/android/exoplayer2/upstream/DataSpec:length	J
    //   170: lstore 5
    //   172: ldc2_w 174
    //   175: lstore_3
    //   176: lload 5
    //   178: ldc2_w 174
    //   181: lcmp
    //   182: ifeq +14 -> 196
    //   185: aload_0
    //   186: aload_1
    //   187: getfield 263	com/google/android/exoplayer2/upstream/DataSpec:length	J
    //   190: putfield 432	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   193: goto +45 -> 238
    //   196: aload_0
    //   197: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   200: invokestatic 555	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:getContentLength	(Ljava/net/HttpURLConnection;)J
    //   203: lstore 5
    //   205: lload 5
    //   207: ldc2_w 174
    //   210: lcmp
    //   211: ifeq +11 -> 222
    //   214: lload 5
    //   216: aload_0
    //   217: getfield 452	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToSkip	J
    //   220: lsub
    //   221: lstore_3
    //   222: aload_0
    //   223: lload_3
    //   224: putfield 432	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   227: goto +11 -> 238
    //   230: aload_0
    //   231: aload_1
    //   232: getfield 263	com/google/android/exoplayer2/upstream/DataSpec:length	J
    //   235: putfield 432	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   238: aload_0
    //   239: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   242: astore 7
    //   244: aload 7
    //   246: invokevirtual 390	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   249: astore 7
    //   251: aload_0
    //   252: aload 7
    //   254: putfield 439	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   257: aload_0
    //   258: iconst_1
    //   259: putfield 504	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   262: aload_0
    //   263: aload_1
    //   264: invokevirtual 558	com/google/android/exoplayer2/upstream/BaseDataSource:transferStarted	(Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   267: aload_0
    //   268: getfield 432	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   271: lreturn
    //   272: astore 7
    //   274: aload_0
    //   275: invokespecial 502	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   278: new 488	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   281: dup
    //   282: aload 7
    //   284: aload_1
    //   285: iconst_1
    //   286: invokespecial 500	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/io/IOException;Lcom/google/android/exoplayer2/upstream/DataSpec;I)V
    //   289: athrow
    //   290: aload_0
    //   291: getfield 121	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   294: invokevirtual 518	java/net/HttpURLConnection:getHeaderFields	()Ljava/util/Map;
    //   297: astore 8
    //   299: aload_0
    //   300: invokespecial 502	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   303: new 560	com/google/android/exoplayer2/upstream/HttpDataSource$InvalidResponseCodeException
    //   306: dup
    //   307: iload_2
    //   308: aload 7
    //   310: aload 8
    //   312: aload_1
    //   313: invokespecial 563	com/google/android/exoplayer2/upstream/HttpDataSource$InvalidResponseCodeException:<init>	(ILjava/lang/String;Ljava/util/Map;Lcom/google/android/exoplayer2/upstream/DataSpec;)V
    //   316: astore_1
    //   317: iload_2
    //   318: sipush 416
    //   321: if_icmpne +16 -> 337
    //   324: aload_1
    //   325: new 565	com/google/android/exoplayer2/upstream/DataSourceException
    //   328: dup
    //   329: iconst_0
    //   330: invokespecial 567	com/google/android/exoplayer2/upstream/DataSourceException:<init>	(I)V
    //   333: invokevirtual 571	java/io/IOException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   336: pop
    //   337: aload_1
    //   338: athrow
    //   339: astore 7
    //   341: aload_0
    //   342: invokespecial 502	com/google/android/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   345: new 157	java/lang/StringBuilder
    //   348: dup
    //   349: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   352: astore 8
    //   354: aload 8
    //   356: ldc_w 573
    //   359: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: pop
    //   363: aload 8
    //   365: aload_1
    //   366: getfield 246	com/google/android/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   369: invokevirtual 249	android/net/Uri:toString	()Ljava/lang/String;
    //   372: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   375: pop
    //   376: new 488	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   379: dup
    //   380: aload 8
    //   382: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   385: aload 7
    //   387: aload_1
    //   388: iconst_1
    //   389: invokespecial 576	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/lang/String;Ljava/io/IOException;Lcom/google/android/exoplayer2/upstream/DataSpec;I)V
    //   392: athrow
    //   393: astore 7
    //   395: new 157	java/lang/StringBuilder
    //   398: dup
    //   399: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   402: astore 8
    //   404: aload 8
    //   406: ldc_w 573
    //   409: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   412: pop
    //   413: aload 8
    //   415: aload_1
    //   416: getfield 246	com/google/android/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   419: invokevirtual 249	android/net/Uri:toString	()Ljava/lang/String;
    //   422: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   425: pop
    //   426: new 488	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   429: dup
    //   430: aload 8
    //   432: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   435: aload 7
    //   437: aload_1
    //   438: iconst_1
    //   439: invokespecial 576	com/google/android/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/lang/String;Ljava/io/IOException;Lcom/google/android/exoplayer2/upstream/DataSpec;I)V
    //   442: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	443	0	this	DefaultHttpDataSource
    //   0	443	1	paramDataSpec	DataSpec
    //   47	275	2	i	int
    //   128	96	3	l1	long
    //   6	209	5	l2	long
    //   28	225	7	localObject1	Object
    //   272	37	7	localIOException1	IOException
    //   339	47	7	localIOException2	IOException
    //   393	43	7	localIOException3	IOException
    //   297	134	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   244	251	272	java/io/IOException
    //   42	48	339	java/io/IOException
    //   54	61	339	java/io/IOException
    //   23	30	393	java/io/IOException
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
      throw new HttpDataSource.HttpDataSourceException(paramArrayOfByte, dataSpec, 2);
    }
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    Assertions.checkNotNull(paramString1);
    Assertions.checkNotNull(paramString2);
    requestProperties.put(paramString1, paramString2);
  }
}
