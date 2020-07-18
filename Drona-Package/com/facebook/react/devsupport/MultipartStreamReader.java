package com.facebook.react.devsupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

public class MultipartStreamReader
{
  private static final String CRLF = "\r\n";
  private final String mBoundary;
  private long mLastProgressEvent;
  private final BufferedSource mSource;
  
  public MultipartStreamReader(BufferedSource paramBufferedSource, String paramString)
  {
    mSource = paramBufferedSource;
    mBoundary = paramString;
  }
  
  private void emitChunk(Buffer paramBuffer, boolean paramBoolean, ChunkListener paramChunkListener)
    throws IOException
  {
    ByteString localByteString = ByteString.encodeUtf8("\r\n\r\n");
    long l = paramBuffer.indexOf(localByteString);
    if (l == -1L)
    {
      paramChunkListener.onChunkComplete(null, paramBuffer, paramBoolean);
      return;
    }
    Buffer localBuffer1 = new Buffer();
    Buffer localBuffer2 = new Buffer();
    paramBuffer.read(localBuffer1, l);
    paramBuffer.skip(localByteString.size());
    paramBuffer.readAll(localBuffer2);
    paramChunkListener.onChunkComplete(parseHeaders(localBuffer1), localBuffer2, paramBoolean);
  }
  
  private void emitProgress(Map paramMap, long paramLong, boolean paramBoolean, ChunkListener paramChunkListener)
    throws IOException
  {
    if (paramMap != null)
    {
      if (paramChunkListener == null) {
        return;
      }
      long l = System.currentTimeMillis();
      if ((l - mLastProgressEvent > 16L) || (paramBoolean))
      {
        mLastProgressEvent = l;
        if (paramMap.get("Content-Length") != null) {}
        for (l = Long.parseLong((String)paramMap.get("Content-Length"));; l = 0L) {
          break;
        }
        paramChunkListener.onChunkProgress(paramMap, paramLong, l);
      }
    }
  }
  
  private Map parseHeaders(Buffer paramBuffer)
  {
    HashMap localHashMap = new HashMap();
    paramBuffer = paramBuffer.readUtf8().split("\r\n");
    int j = paramBuffer.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramBuffer[i];
      int k = localObject.indexOf(":");
      if (k != -1) {
        localHashMap.put(localObject.substring(0, k).trim(), localObject.substring(k + 1).trim());
      }
      i += 1;
    }
    return localHashMap;
  }
  
  public boolean readAllParts(ChunkListener paramChunkListener)
    throws IOException
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("\r\n--");
    ((StringBuilder)localObject).append(mBoundary);
    ((StringBuilder)localObject).append("\r\n");
    ByteString localByteString1 = ByteString.encodeUtf8(((StringBuilder)localObject).toString());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("\r\n--");
    ((StringBuilder)localObject).append(mBoundary);
    ((StringBuilder)localObject).append("--");
    ((StringBuilder)localObject).append("\r\n");
    ByteString localByteString2 = ByteString.encodeUtf8(((StringBuilder)localObject).toString());
    ByteString localByteString3 = ByteString.encodeUtf8("\r\n\r\n");
    Buffer localBuffer1 = new Buffer();
    long l2 = 0L;
    long l3 = 0L;
    long l1 = 0L;
    localObject = null;
    for (;;)
    {
      long l5 = Math.max(l2 - localByteString2.size(), l3);
      long l4 = localBuffer1.indexOf(localByteString1, l5);
      l2 = l4;
      boolean bool;
      if (l4 == -1L)
      {
        l2 = localBuffer1.indexOf(localByteString2, l5);
        bool = true;
      }
      else
      {
        bool = false;
      }
      if (l2 == -1L)
      {
        l2 = localBuffer1.size();
        if (localObject == null)
        {
          l4 = localBuffer1.indexOf(localByteString3, l5);
          if (l4 >= 0L)
          {
            mSource.read(localBuffer1, l4);
            localObject = new Buffer();
            localBuffer1.copyTo((Buffer)localObject, l5, l4 - l5);
            l1 = ((Buffer)localObject).size() + localByteString3.size();
            localObject = parseHeaders((Buffer)localObject);
          }
        }
        else
        {
          emitProgress((Map)localObject, localBuffer1.size() - l1, false, paramChunkListener);
        }
        if (mSource.read(localBuffer1, '?') <= 0L) {
          return false;
        }
      }
      else
      {
        if (l3 > 0L)
        {
          Buffer localBuffer2 = new Buffer();
          localBuffer1.skip(l3);
          localBuffer1.read(localBuffer2, l2 - l3);
          emitProgress((Map)localObject, localBuffer2.size() - l1, true, paramChunkListener);
          emitChunk(localBuffer2, bool, paramChunkListener);
          l1 = 0L;
          localObject = null;
        }
        else
        {
          localBuffer1.skip(l2);
        }
        if (bool) {
          return true;
        }
        l3 = localByteString1.size();
        l2 = l3;
      }
    }
  }
  
  public static abstract interface ChunkListener
  {
    public abstract void onChunkComplete(Map paramMap, Buffer paramBuffer, boolean paramBoolean)
      throws IOException;
    
    public abstract void onChunkProgress(Map paramMap, long paramLong1, long paramLong2)
      throws IOException;
  }
}
