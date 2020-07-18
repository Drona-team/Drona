package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileDataSource
  extends BaseDataSource
{
  private long bytesRemaining;
  @Nullable
  private RandomAccessFile file;
  private boolean opened;
  @Nullable
  private Uri uriString;
  
  public FileDataSource()
  {
    super(false);
  }
  
  public FileDataSource(TransferListener paramTransferListener)
  {
    this();
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  public void close()
    throws FileDataSource.FileDataSourceException
  {
    uriString = null;
    try
    {
      RandomAccessFile localRandomAccessFile = file;
      if (localRandomAccessFile != null) {
        localRandomAccessFile = file;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      try
      {
        localRandomAccessFile.close();
        file = null;
        if (!opened) {
          return;
        }
        opened = false;
        transferEnded();
        return;
      }
      catch (IOException localIOException)
      {
        throw new FileDataSourceException(localIOException);
      }
      localThrowable = localThrowable;
      file = null;
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
    return uriString;
  }
  
  public long open(DataSpec paramDataSpec)
    throws FileDataSource.FileDataSourceException
  {
    uriString = uri;
    try
    {
      transferInitializing(paramDataSpec);
      Object localObject = uri;
      localObject = new RandomAccessFile(((Uri)localObject).getPath(), "r");
      file = ((RandomAccessFile)localObject);
      localObject = file;
      long l = position;
      ((RandomAccessFile)localObject).seek(l);
      if (length == -1L)
      {
        localObject = file;
        l = ((RandomAccessFile)localObject).length();
        l -= position;
      }
      else
      {
        l = length;
      }
      bytesRemaining = l;
      if (bytesRemaining >= 0L)
      {
        opened = true;
        transferStarted(paramDataSpec);
        return bytesRemaining;
      }
      paramDataSpec = new EOFException();
      throw paramDataSpec;
    }
    catch (IOException paramDataSpec)
    {
      throw new FileDataSourceException((IOException)paramDataSpec);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws FileDataSource.FileDataSourceException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (bytesRemaining == 0L) {
      return -1;
    }
    RandomAccessFile localRandomAccessFile = file;
    long l1 = bytesRemaining;
    long l2 = paramInt2;
    try
    {
      l1 = Math.min(l1, l2);
      paramInt2 = (int)l1;
      paramInt1 = localRandomAccessFile.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 > 0)
      {
        bytesRemaining -= paramInt1;
        bytesTransferred(paramInt1);
        return paramInt1;
      }
    }
    catch (IOException paramArrayOfByte)
    {
      throw new FileDataSourceException(paramArrayOfByte);
    }
    return paramInt1;
  }
  
  public static class FileDataSourceException
    extends IOException
  {
    public FileDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}
