package com.facebook.imagepipeline.decoder;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.Pool;
import com.facebook.common.memory.PooledByteArrayBufferedInputStream;
import com.facebook.common.util.StreamUtil;
import com.facebook.imagepipeline.image.EncodedImage;
import java.io.IOException;
import java.io.InputStream;

public class ProgressiveJpegParser
{
  private static final int BUFFER_SIZE = 16384;
  private static final int NOT_A_JPEG = 6;
  private static final int READ_FIRST_JPEG_BYTE = 0;
  private static final int READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA = 2;
  private static final int READ_MARKER_SECOND_BYTE = 3;
  private static final int READ_SECOND_JPEG_BYTE = 1;
  private static final int READ_SIZE_FIRST_BYTE = 4;
  private static final int READ_SIZE_SECOND_BYTE = 5;
  private int mBestScanEndOffset;
  private int mBestScanNumber;
  private final ByteArrayPool mByteArrayPool;
  private int mBytesParsed;
  private boolean mEndMarkerRead;
  private int mLastByteRead;
  private int mNextFullScanNumber;
  private int mParserState;
  
  public ProgressiveJpegParser(ByteArrayPool paramByteArrayPool)
  {
    mByteArrayPool = ((ByteArrayPool)Preconditions.checkNotNull(paramByteArrayPool));
    mBytesParsed = 0;
    mLastByteRead = 0;
    mNextFullScanNumber = 0;
    mBestScanEndOffset = 0;
    mBestScanNumber = 0;
    mParserState = 0;
  }
  
  private boolean doParseMoreData(InputStream paramInputStream)
  {
    int i = mBestScanNumber;
    while (mParserState != 6) {
      try
      {
        int j = paramInputStream.read();
        if (j != -1)
        {
          mBytesParsed += 1;
          if (mEndMarkerRead)
          {
            mParserState = 6;
            mEndMarkerRead = false;
            return false;
          }
          int k;
          switch (mParserState)
          {
          default: 
            Preconditions.checkState(false);
            break;
          case 5: 
            k = (mLastByteRead << 8) + j - 2;
            long l = k;
            StreamUtil.skip(paramInputStream, l);
            mBytesParsed += k;
            mParserState = 2;
            break;
          case 4: 
            mParserState = 5;
            break;
          case 3: 
            if (j == 255)
            {
              mParserState = 3;
            }
            else if (j == 0)
            {
              mParserState = 2;
            }
            else if (j == 217)
            {
              mEndMarkerRead = true;
              k = mBytesParsed;
              newScanOrImageEndFound(k - 2);
              mParserState = 2;
            }
            else
            {
              if (j == 218)
              {
                k = mBytesParsed;
                newScanOrImageEndFound(k - 2);
              }
              boolean bool = doesMarkerStartSegment(j);
              if (bool) {
                mParserState = 4;
              } else {
                mParserState = 2;
              }
            }
            break;
          case 2: 
            if (j == 255) {
              mParserState = 3;
            }
            break;
          case 1: 
            if (j == 216) {
              mParserState = 2;
            } else {
              mParserState = 6;
            }
            break;
          case 0: 
            if (j == 255) {
              mParserState = 1;
            } else {
              mParserState = 6;
            }
            break;
          }
          mLastByteRead = j;
        }
      }
      catch (IOException paramInputStream)
      {
        Throwables.propagate(paramInputStream);
      }
    }
    return (mParserState != 6) && (mBestScanNumber != i);
  }
  
  private static boolean doesMarkerStartSegment(int paramInt)
  {
    if (paramInt == 1) {
      return false;
    }
    if ((paramInt >= 208) && (paramInt <= 215)) {
      return false;
    }
    return (paramInt != 217) && (paramInt != 216);
  }
  
  private void newScanOrImageEndFound(int paramInt)
  {
    if (mNextFullScanNumber > 0) {
      mBestScanEndOffset = paramInt;
    }
    paramInt = mNextFullScanNumber;
    mNextFullScanNumber = (paramInt + 1);
    mBestScanNumber = paramInt;
  }
  
  public int getBestScanEndOffset()
  {
    return mBestScanEndOffset;
  }
  
  public int getBestScanNumber()
  {
    return mBestScanNumber;
  }
  
  public boolean isEndMarkerRead()
  {
    return mEndMarkerRead;
  }
  
  public boolean isJpeg()
  {
    return (mBytesParsed > 1) && (mParserState != 6);
  }
  
  public boolean parseMoreData(EncodedImage paramEncodedImage)
  {
    if (mParserState == 6) {
      return false;
    }
    if (paramEncodedImage.getSize() <= mBytesParsed) {
      return false;
    }
    paramEncodedImage = new PooledByteArrayBufferedInputStream(paramEncodedImage.getInputStream(), (byte[])mByteArrayPool.get(16384), mByteArrayPool);
    long l = mBytesParsed;
    try
    {
      StreamUtil.skip(paramEncodedImage, l);
      boolean bool = doParseMoreData(paramEncodedImage);
      Closeables.closeQuietly(paramEncodedImage);
      return bool;
    }
    catch (Throwable localThrowable) {}catch (IOException localIOException)
    {
      Throwables.propagate(localIOException);
      Closeables.closeQuietly(paramEncodedImage);
      return false;
    }
    Closeables.closeQuietly(paramEncodedImage);
    throw localIOException;
  }
}