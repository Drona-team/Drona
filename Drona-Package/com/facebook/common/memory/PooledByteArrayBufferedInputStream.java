package com.facebook.common.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.ResourceReleaser;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PooledByteArrayBufferedInputStream
  extends InputStream
{
  private static final String PAGE_KEY = "PooledByteInputStream";
  private int mBufferOffset;
  private int mBufferedSize;
  private final byte[] mByteArray;
  private boolean mClosed;
  private final InputStream mInputStream;
  private final ResourceReleaser<byte[]> mResourceReleaser;
  
  public PooledByteArrayBufferedInputStream(InputStream paramInputStream, byte[] paramArrayOfByte, ResourceReleaser paramResourceReleaser)
  {
    mInputStream = ((InputStream)Preconditions.checkNotNull(paramInputStream));
    mByteArray = ((byte[])Preconditions.checkNotNull(paramArrayOfByte));
    mResourceReleaser = ((ResourceReleaser)Preconditions.checkNotNull(paramResourceReleaser));
    mBufferedSize = 0;
    mBufferOffset = 0;
    mClosed = false;
  }
  
  private boolean ensureDataInBuffer()
    throws IOException
  {
    if (mBufferOffset < mBufferedSize) {
      return true;
    }
    int i = mInputStream.read(mByteArray);
    if (i <= 0) {
      return false;
    }
    mBufferedSize = i;
    mBufferOffset = 0;
    return true;
  }
  
  private void ensureNotClosed()
    throws IOException
  {
    if (!mClosed) {
      return;
    }
    throw new IOException("stream already closed");
  }
  
  public int available()
    throws IOException
  {
    boolean bool;
    if (mBufferOffset <= mBufferedSize) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    ensureNotClosed();
    return mBufferedSize - mBufferOffset + mInputStream.available();
  }
  
  public void close()
    throws IOException
  {
    if (!mClosed)
    {
      mClosed = true;
      mResourceReleaser.release(mByteArray);
      super.close();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    if (!mClosed)
    {
      FLog.e("PooledByteInputStream", "Finalized without closing");
      close();
    }
    super.finalize();
  }
  
  public int read()
    throws IOException
  {
    boolean bool;
    if (mBufferOffset <= mBufferedSize) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    ensureNotClosed();
    if (!ensureDataInBuffer()) {
      return -1;
    }
    byte[] arrayOfByte = mByteArray;
    int i = mBufferOffset;
    mBufferOffset = (i + 1);
    return arrayOfByte[i] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    boolean bool;
    if (mBufferOffset <= mBufferedSize) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    ensureNotClosed();
    if (!ensureDataInBuffer()) {
      return -1;
    }
    paramInt2 = Math.min(mBufferedSize - mBufferOffset, paramInt2);
    System.arraycopy(mByteArray, mBufferOffset, paramArrayOfByte, paramInt1, paramInt2);
    mBufferOffset += paramInt2;
    return paramInt2;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    boolean bool;
    if (mBufferOffset <= mBufferedSize) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    ensureNotClosed();
    long l = mBufferedSize - mBufferOffset;
    if (l >= paramLong)
    {
      mBufferOffset = ((int)(mBufferOffset + paramLong));
      return paramLong;
    }
    mBufferOffset = mBufferedSize;
    return l + mInputStream.skip(paramLong - l);
  }
}
