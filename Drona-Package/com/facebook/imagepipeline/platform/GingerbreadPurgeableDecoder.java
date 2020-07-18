package com.facebook.imagepipeline.platform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.MemoryFile;
import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.streams.LimitedInputStream;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.common.webp.WebpSupportStatus;
import com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

public class GingerbreadPurgeableDecoder
  extends DalvikPurgeableDecoder
{
  private static Method sGetFileDescriptorMethod;
  @Nullable
  private final WebpBitmapFactory mWebpBitmapFactory = WebpSupportStatus.loadWebpBitmapFactoryIfExists();
  
  public GingerbreadPurgeableDecoder() {}
  
  private static MemoryFile copyToMemoryFile(CloseableReference paramCloseableReference, int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    int i;
    if (paramArrayOfByte == null) {
      i = 0;
    } else {
      i = paramArrayOfByte.length;
    }
    Object localObject2 = null;
    MemoryFile localMemoryFile = new MemoryFile(null, i + paramInt);
    localMemoryFile.allowPurging(false);
    PooledByteBufferInputStream localPooledByteBufferInputStream;
    Object localObject1;
    try
    {
      localPooledByteBufferInputStream = new PooledByteBufferInputStream((PooledByteBuffer)paramCloseableReference.get());
      try
      {
        localLimitedInputStream = new LimitedInputStream(localPooledByteBufferInputStream, paramInt);
        try
        {
          localObject2 = localMemoryFile.getOutputStream();
          localObject1 = localObject2;
          try
          {
            ByteStreams.copy(localLimitedInputStream, (OutputStream)localObject2);
            if (paramArrayOfByte != null) {
              localMemoryFile.writeBytes(paramArrayOfByte, 0, paramInt, paramArrayOfByte.length);
            }
            CloseableReference.closeSafely(paramCloseableReference);
            Closeables.closeQuietly(localPooledByteBufferInputStream);
            Closeables.closeQuietly(localLimitedInputStream);
            Closeables.close((Closeable)localObject2, true);
            return localMemoryFile;
          }
          catch (Throwable paramArrayOfByte) {}
          localObject2 = localLimitedInputStream;
        }
        catch (Throwable paramArrayOfByte)
        {
          localObject1 = null;
        }
      }
      catch (Throwable paramArrayOfByte)
      {
        LimitedInputStream localLimitedInputStream;
        localObject1 = null;
      }
      CloseableReference.closeSafely(paramCloseableReference);
    }
    catch (Throwable paramArrayOfByte)
    {
      localPooledByteBufferInputStream = null;
      localObject1 = null;
    }
    Closeables.closeQuietly(localPooledByteBufferInputStream);
    Closeables.closeQuietly((InputStream)localObject2);
    Closeables.close(localObject1, true);
    throw paramArrayOfByte;
  }
  
  private Bitmap decodeFileDescriptorAsPurgeable(CloseableReference paramCloseableReference, int paramInt, byte[] paramArrayOfByte, BitmapFactory.Options paramOptions)
  {
    WebpBitmapFactory localWebpBitmapFactory = null;
    Object localObject = null;
    try
    {
      paramCloseableReference = copyToMemoryFile(paramCloseableReference, paramInt, paramArrayOfByte);
      paramArrayOfByte = paramCloseableReference;
      try
      {
        localObject = getMemoryFileDescriptor(paramCloseableReference);
        localWebpBitmapFactory = mWebpBitmapFactory;
        if (localWebpBitmapFactory != null)
        {
          localWebpBitmapFactory = mWebpBitmapFactory;
          paramOptions = Preconditions.checkNotNull(localWebpBitmapFactory.decodeFileDescriptor((FileDescriptor)localObject, null, paramOptions), "BitmapFactory returned null");
          paramOptions = (Bitmap)paramOptions;
          if (paramCloseableReference == null) {
            break label130;
          }
          paramCloseableReference.close();
          return paramOptions;
        }
        paramOptions = new IllegalStateException("WebpBitmapFactory is null");
        throw paramOptions;
      }
      catch (Throwable paramCloseableReference)
      {
        break label120;
      }
      catch (IOException paramArrayOfByte)
      {
        localObject = paramCloseableReference;
        paramCloseableReference = paramArrayOfByte;
      }
      throw Throwables.propagate(paramCloseableReference);
    }
    catch (Throwable paramCloseableReference)
    {
      paramArrayOfByte = (byte[])localObject;
    }
    catch (IOException paramCloseableReference)
    {
      localObject = localWebpBitmapFactory;
    }
    label120:
    if (paramArrayOfByte != null) {
      paramArrayOfByte.close();
    }
    throw paramCloseableReference;
    label130:
    return paramOptions;
  }
  
  private Method getFileDescriptorMethod()
  {
    try
    {
      Method localMethod1 = sGetFileDescriptorMethod;
      if (localMethod1 == null) {
        try
        {
          localMethod1 = MemoryFile.class.getDeclaredMethod("getFileDescriptor", new Class[0]);
          sGetFileDescriptorMethod = localMethod1;
        }
        catch (Exception localException)
        {
          throw Throwables.propagate(localException);
        }
      }
      Method localMethod2 = sGetFileDescriptorMethod;
      return localMethod2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private FileDescriptor getMemoryFileDescriptor(MemoryFile paramMemoryFile)
  {
    try
    {
      Method localMethod = getFileDescriptorMethod();
      paramMemoryFile = localMethod.invoke(paramMemoryFile, new Object[0]);
      return (FileDescriptor)paramMemoryFile;
    }
    catch (Exception paramMemoryFile)
    {
      throw Throwables.propagate(paramMemoryFile);
    }
  }
  
  protected Bitmap decodeByteArrayAsPurgeable(CloseableReference paramCloseableReference, BitmapFactory.Options paramOptions)
  {
    return decodeFileDescriptorAsPurgeable(paramCloseableReference, ((PooledByteBuffer)paramCloseableReference.get()).size(), null, paramOptions);
  }
  
  protected Bitmap decodeJPEGByteArrayAsPurgeable(CloseableReference paramCloseableReference, int paramInt, BitmapFactory.Options paramOptions)
  {
    byte[] arrayOfByte;
    if (DalvikPurgeableDecoder.endsWithEOI(paramCloseableReference, paramInt)) {
      arrayOfByte = null;
    } else {
      arrayOfByte = DalvikPurgeableDecoder.EOF_MARKER;
    }
    return decodeFileDescriptorAsPurgeable(paramCloseableReference, paramInt, arrayOfByte, paramOptions);
  }
}
