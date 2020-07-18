package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultDataSource
  implements DataSource
{
  private static final String PAGE_KEY = "DefaultDataSource";
  private static final String SCHEME_ASSET = "asset";
  private static final String SCHEME_CONTENT = "content";
  private static final String SCHEME_RAW = "rawresource";
  private static final String SCHEME_RTMP = "rtmp";
  @Nullable
  private DataSource assetDataSource;
  private final DataSource baseDataSource;
  @Nullable
  private DataSource contentDataSource;
  private final Context context;
  @Nullable
  private DataSource dataSchemeDataSource;
  @Nullable
  private DataSource dataSource;
  @Nullable
  private DataSource fileDataSource;
  @Nullable
  private DataSource rawResourceDataSource;
  @Nullable
  private DataSource rtmpDataSource;
  private final List<TransferListener> transferListeners;
  
  public DefaultDataSource(Context paramContext, DataSource paramDataSource)
  {
    context = paramContext.getApplicationContext();
    baseDataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
    transferListeners = new ArrayList();
  }
  
  public DefaultDataSource(Context paramContext, TransferListener paramTransferListener, DataSource paramDataSource)
  {
    this(paramContext, paramDataSource);
    if (paramTransferListener != null) {
      transferListeners.add(paramTransferListener);
    }
  }
  
  public DefaultDataSource(Context paramContext, TransferListener paramTransferListener, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSource(paramString, null, paramTransferListener, paramInt1, paramInt2, paramBoolean, null));
  }
  
  public DefaultDataSource(Context paramContext, TransferListener paramTransferListener, String paramString, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, paramString, 8000, 8000, paramBoolean);
  }
  
  public DefaultDataSource(Context paramContext, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramContext, new DefaultHttpDataSource(paramString, null, paramInt1, paramInt2, paramBoolean, null));
  }
  
  public DefaultDataSource(Context paramContext, String paramString, boolean paramBoolean)
  {
    this(paramContext, paramString, 8000, 8000, paramBoolean);
  }
  
  private void addListenersToDataSource(DataSource paramDataSource)
  {
    int i = 0;
    while (i < transferListeners.size())
    {
      paramDataSource.addTransferListener((TransferListener)transferListeners.get(i));
      i += 1;
    }
  }
  
  private DataSource getAssetDataSource()
  {
    if (assetDataSource == null)
    {
      assetDataSource = new AssetDataSource(context);
      addListenersToDataSource(assetDataSource);
    }
    return assetDataSource;
  }
  
  private DataSource getContentDataSource()
  {
    if (contentDataSource == null)
    {
      contentDataSource = new ContentDataSource(context);
      addListenersToDataSource(contentDataSource);
    }
    return contentDataSource;
  }
  
  private DataSource getDataSchemeDataSource()
  {
    if (dataSchemeDataSource == null)
    {
      dataSchemeDataSource = new DataSchemeDataSource();
      addListenersToDataSource(dataSchemeDataSource);
    }
    return dataSchemeDataSource;
  }
  
  private DataSource getFileDataSource()
  {
    if (fileDataSource == null)
    {
      fileDataSource = new FileDataSource();
      addListenersToDataSource(fileDataSource);
    }
    return fileDataSource;
  }
  
  private DataSource getRawResourceDataSource()
  {
    if (rawResourceDataSource == null)
    {
      rawResourceDataSource = new RawResourceDataSource(context);
      addListenersToDataSource(rawResourceDataSource);
    }
    return rawResourceDataSource;
  }
  
  private DataSource getRtmpDataSource()
  {
    if (rtmpDataSource == null) {}
    try
    {
      try
      {
        Object localObject = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
        localObject = ((Class)localObject).getConstructor(new Class[0]);
        localObject = ((Constructor)localObject).newInstance(new Object[0]);
        rtmpDataSource = ((DataSource)localObject);
        localObject = rtmpDataSource;
        addListenersToDataSource((DataSource)localObject);
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Error instantiating RTMP extension", localException);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;) {}
    }
    Log.w("DefaultDataSource", "Attempting to play RTMP stream without depending on the RTMP extension");
    if (rtmpDataSource == null) {
      rtmpDataSource = baseDataSource;
    }
    return rtmpDataSource;
  }
  
  private void maybeAddListenerToDataSource(DataSource paramDataSource, TransferListener paramTransferListener)
  {
    if (paramDataSource != null) {
      paramDataSource.addTransferListener(paramTransferListener);
    }
  }
  
  public void addTransferListener(TransferListener paramTransferListener)
  {
    baseDataSource.addTransferListener(paramTransferListener);
    transferListeners.add(paramTransferListener);
    maybeAddListenerToDataSource(fileDataSource, paramTransferListener);
    maybeAddListenerToDataSource(assetDataSource, paramTransferListener);
    maybeAddListenerToDataSource(contentDataSource, paramTransferListener);
    maybeAddListenerToDataSource(rtmpDataSource, paramTransferListener);
    maybeAddListenerToDataSource(dataSchemeDataSource, paramTransferListener);
    maybeAddListenerToDataSource(rawResourceDataSource, paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    if (dataSource != null) {
      try
      {
        dataSource.close();
        dataSource = null;
        return;
      }
      catch (Throwable localThrowable)
      {
        dataSource = null;
        throw localThrowable;
      }
    }
  }
  
  public Map getResponseHeaders()
  {
    if (dataSource == null) {
      return Collections.emptyMap();
    }
    return dataSource.getResponseHeaders();
  }
  
  public Uri getUri()
  {
    if (dataSource == null) {
      return null;
    }
    return dataSource.getUri();
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    boolean bool;
    if (dataSource == null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    String str = uri.getScheme();
    if (Util.isLocalFileUri(uri))
    {
      if (uri.getPath().startsWith("/android_asset/")) {
        dataSource = getAssetDataSource();
      } else {
        dataSource = getFileDataSource();
      }
    }
    else if ("asset".equals(str)) {
      dataSource = getAssetDataSource();
    } else if ("content".equals(str)) {
      dataSource = getContentDataSource();
    } else if ("rtmp".equals(str)) {
      dataSource = getRtmpDataSource();
    } else if ("data".equals(str)) {
      dataSource = getDataSchemeDataSource();
    } else if ("rawresource".equals(str)) {
      dataSource = getRawResourceDataSource();
    } else {
      dataSource = baseDataSource;
    }
    return dataSource.open(paramDataSpec);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return ((DataSource)Assertions.checkNotNull(dataSource)).read(paramArrayOfByte, paramInt1, paramInt2);
  }
}
