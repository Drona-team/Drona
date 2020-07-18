package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class AssetDataSource
  extends BaseDataSource
{
  private final AssetManager assetManager;
  private long bytesRemaining;
  @Nullable
  private InputStream inputStream;
  private boolean opened;
  @Nullable
  private Uri uri;
  
  public AssetDataSource(Context paramContext)
  {
    super(false);
    assetManager = paramContext.getAssets();
  }
  
  public AssetDataSource(Context paramContext, TransferListener paramTransferListener)
  {
    this(paramContext);
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  public void close()
    throws AssetDataSource.AssetDataSourceException
  {
    uri = null;
    try
    {
      InputStream localInputStream = inputStream;
      if (localInputStream != null) {
        localInputStream = inputStream;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      try
      {
        localInputStream.close();
        inputStream = null;
        if (!opened) {
          return;
        }
        opened = false;
        transferEnded();
        return;
      }
      catch (IOException localIOException)
      {
        throw new AssetDataSourceException(localIOException);
      }
      localThrowable = localThrowable;
      inputStream = null;
      if (opened)
      {
        opened = false;
        transferEnded();
      }
      throw localIOException;
    }
  }
  
  public Uri getUri()
  {
    return uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws AssetDataSource.AssetDataSourceException
  {
    uri = uri;
    Object localObject1 = uri;
    try
    {
      Object localObject2 = ((Uri)localObject1).getPath();
      localObject1 = localObject2;
      boolean bool = ((String)localObject2).startsWith("/android_asset/");
      if (bool)
      {
        localObject1 = ((String)localObject2).substring(15);
      }
      else
      {
        bool = ((String)localObject2).startsWith("/");
        if (bool) {
          localObject1 = ((String)localObject2).substring(1);
        }
      }
      transferInitializing(paramDataSpec);
      localObject2 = assetManager;
      localObject1 = ((AssetManager)localObject2).open((String)localObject1, 1);
      inputStream = ((InputStream)localObject1);
      localObject1 = inputStream;
      long l = position;
      l = ((InputStream)localObject1).skip(l);
      if (l >= position)
      {
        if (length != -1L)
        {
          bytesRemaining = length;
        }
        else
        {
          localObject1 = inputStream;
          int i = ((InputStream)localObject1).available();
          bytesRemaining = i;
          if (bytesRemaining == 2147483647L) {
            bytesRemaining = -1L;
          }
        }
        opened = true;
        transferStarted(paramDataSpec);
        return bytesRemaining;
      }
      paramDataSpec = new EOFException();
      throw paramDataSpec;
    }
    catch (IOException paramDataSpec)
    {
      throw new AssetDataSourceException((IOException)paramDataSpec);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws AssetDataSource.AssetDataSourceException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (bytesRemaining == 0L) {
      return -1;
    }
    long l1;
    long l2;
    if (bytesRemaining != -1L)
    {
      l1 = bytesRemaining;
      l2 = paramInt2;
    }
    try
    {
      l1 = Math.min(l1, l2);
      paramInt2 = (int)l1;
      InputStream localInputStream = inputStream;
      paramInt1 = localInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 == -1)
      {
        if (bytesRemaining == -1L) {
          return -1;
        }
        throw new AssetDataSourceException(new EOFException());
      }
      if (bytesRemaining != -1L) {
        bytesRemaining -= paramInt1;
      }
      bytesTransferred(paramInt1);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new AssetDataSourceException(paramArrayOfByte);
    }
  }
  
  public static final class AssetDataSourceException
    extends IOException
  {
    public AssetDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}
