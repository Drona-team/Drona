package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

final class SampleMetadataQueue
{
  private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
  private int absoluteFirstIndex;
  private int capacity = 1000;
  private TrackOutput.CryptoData[] cryptoDatas = new TrackOutput.CryptoData[capacity];
  private int[] flags = new int[capacity];
  private Format[] formats = new Format[capacity];
  private long largestDiscardedTimestampUs = Long.MIN_VALUE;
  private long largestQueuedTimestampUs = Long.MIN_VALUE;
  private int length;
  private long[] offsets = new long[capacity];
  private int readPosition;
  private int relativeFirstIndex;
  private int[] sizes = new int[capacity];
  private int[] sourceIds = new int[capacity];
  private long[] timesUs = new long[capacity];
  private Format upstreamFormat;
  private boolean upstreamFormatRequired = true;
  private boolean upstreamKeyframeRequired = true;
  private int upstreamSourceId;
  
  public SampleMetadataQueue() {}
  
  private long discardSamples(int paramInt)
  {
    largestDiscardedTimestampUs = Math.max(largestDiscardedTimestampUs, getLargestTimestamp(paramInt));
    length -= paramInt;
    absoluteFirstIndex += paramInt;
    relativeFirstIndex += paramInt;
    if (relativeFirstIndex >= capacity) {
      relativeFirstIndex -= capacity;
    }
    readPosition -= paramInt;
    if (readPosition < 0) {
      readPosition = 0;
    }
    if (length == 0)
    {
      if (relativeFirstIndex == 0) {
        paramInt = capacity;
      } else {
        paramInt = relativeFirstIndex;
      }
      paramInt -= 1;
      return offsets[paramInt] + sizes[paramInt];
    }
    return offsets[relativeFirstIndex];
  }
  
  private int findSampleBefore(int paramInt1, int paramInt2, long paramLong, boolean paramBoolean)
  {
    int k = 0;
    int j = -1;
    int i = paramInt1;
    paramInt1 = k;
    while ((paramInt1 < paramInt2) && (timesUs[i] <= paramLong))
    {
      if ((!paramBoolean) || ((flags[i] & 0x1) != 0)) {
        j = paramInt1;
      }
      k = i + 1;
      i = k;
      if (k == capacity) {
        i = 0;
      }
      paramInt1 += 1;
    }
    return j;
  }
  
  private long getLargestTimestamp(int paramInt)
  {
    long l1 = Long.MIN_VALUE;
    if (paramInt == 0) {
      return Long.MIN_VALUE;
    }
    int i = getRelativeIndex(paramInt - 1);
    int j = 0;
    while (j < paramInt)
    {
      long l2 = Math.max(l1, timesUs[i]);
      l1 = l2;
      if ((flags[i] & 0x1) != 0) {
        return l2;
      }
      int k = i - 1;
      i = k;
      if (k == -1) {
        i = capacity - 1;
      }
      j += 1;
    }
    return l1;
  }
  
  private int getRelativeIndex(int paramInt)
  {
    paramInt = relativeFirstIndex + paramInt;
    if (paramInt < capacity) {
      return paramInt;
    }
    return paramInt - capacity;
  }
  
  public int advanceTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      int i = getRelativeIndex(readPosition);
      if ((hasNextSample()) && (paramLong >= timesUs[i]) && ((paramLong <= largestQueuedTimestampUs) || (paramBoolean2)))
      {
        i = findSampleBefore(i, length - readPosition, paramLong, paramBoolean1);
        if (i == -1) {
          return -1;
        }
        readPosition += i;
        return i;
      }
      return -1;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int advanceToEnd()
  {
    try
    {
      int i = length;
      int j = readPosition;
      readPosition = length;
      return i - j;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean attemptSplice(long paramLong)
  {
    try
    {
      int i = length;
      boolean bool = false;
      if (i == 0)
      {
        l = largestDiscardedTimestampUs;
        if (paramLong > l) {
          bool = true;
        }
        return bool;
      }
      long l = Math.max(largestDiscardedTimestampUs, getLargestTimestamp(readPosition));
      if (l >= paramLong) {
        return false;
      }
      int j = length;
      i = getRelativeIndex(length - 1);
      while ((j > readPosition) && (timesUs[i] >= paramLong))
      {
        int k = j - 1;
        int m = i - 1;
        j = k;
        i = m;
        if (m == -1)
        {
          i = capacity - 1;
          j = k;
        }
      }
      discardUpstreamSamples(absoluteFirstIndex + j);
      return true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, TrackOutput.CryptoData paramCryptoData)
  {
    try
    {
      boolean bool = upstreamKeyframeRequired;
      if (bool)
      {
        if ((paramInt1 & 0x1) == 0) {
          return;
        }
        upstreamKeyframeRequired = false;
      }
      Assertions.checkState(upstreamFormatRequired ^ true);
      commitSampleTimestamp(paramLong1);
      int i = getRelativeIndex(length);
      timesUs[i] = paramLong1;
      offsets[i] = paramLong2;
      sizes[i] = paramInt2;
      flags[i] = paramInt1;
      cryptoDatas[i] = paramCryptoData;
      formats[i] = upstreamFormat;
      sourceIds[i] = upstreamSourceId;
      length += 1;
      if (length == capacity)
      {
        paramInt1 = capacity + 1000;
        paramCryptoData = new int[paramInt1];
        long[] arrayOfLong1 = new long[paramInt1];
        long[] arrayOfLong2 = new long[paramInt1];
        int[] arrayOfInt1 = new int[paramInt1];
        int[] arrayOfInt2 = new int[paramInt1];
        TrackOutput.CryptoData[] arrayOfCryptoData = new TrackOutput.CryptoData[paramInt1];
        Format[] arrayOfFormat = new Format[paramInt1];
        paramInt2 = capacity - relativeFirstIndex;
        System.arraycopy(offsets, relativeFirstIndex, arrayOfLong1, 0, paramInt2);
        System.arraycopy(timesUs, relativeFirstIndex, arrayOfLong2, 0, paramInt2);
        System.arraycopy(flags, relativeFirstIndex, arrayOfInt1, 0, paramInt2);
        System.arraycopy(sizes, relativeFirstIndex, arrayOfInt2, 0, paramInt2);
        System.arraycopy(cryptoDatas, relativeFirstIndex, arrayOfCryptoData, 0, paramInt2);
        System.arraycopy(formats, relativeFirstIndex, arrayOfFormat, 0, paramInt2);
        System.arraycopy(sourceIds, relativeFirstIndex, paramCryptoData, 0, paramInt2);
        i = relativeFirstIndex;
        System.arraycopy(offsets, 0, arrayOfLong1, paramInt2, i);
        System.arraycopy(timesUs, 0, arrayOfLong2, paramInt2, i);
        System.arraycopy(flags, 0, arrayOfInt1, paramInt2, i);
        System.arraycopy(sizes, 0, arrayOfInt2, paramInt2, i);
        System.arraycopy(cryptoDatas, 0, arrayOfCryptoData, paramInt2, i);
        System.arraycopy(formats, 0, arrayOfFormat, paramInt2, i);
        System.arraycopy(sourceIds, 0, paramCryptoData, paramInt2, i);
        offsets = arrayOfLong1;
        timesUs = arrayOfLong2;
        flags = arrayOfInt1;
        sizes = arrayOfInt2;
        cryptoDatas = arrayOfCryptoData;
        formats = arrayOfFormat;
        sourceIds = paramCryptoData;
        relativeFirstIndex = 0;
        length = capacity;
        capacity = paramInt1;
      }
      return;
    }
    catch (Throwable paramCryptoData)
    {
      throw paramCryptoData;
    }
  }
  
  public void commitSampleTimestamp(long paramLong)
  {
    try
    {
      largestQueuedTimestampUs = Math.max(largestQueuedTimestampUs, paramLong);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long discardTo(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      if ((length != 0) && (paramLong >= timesUs[relativeFirstIndex]))
      {
        if ((paramBoolean2) && (readPosition != length)) {
          i = readPosition + 1;
        } else {
          i = length;
        }
        int i = findSampleBefore(relativeFirstIndex, i, paramLong, paramBoolean1);
        if (i == -1) {
          return -1L;
        }
        paramLong = discardSamples(i);
        return paramLong;
      }
      return -1L;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long discardToEnd()
  {
    try
    {
      int i = length;
      if (i == 0) {
        return -1L;
      }
      long l = discardSamples(length);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long discardToRead()
  {
    try
    {
      int i = readPosition;
      if (i == 0) {
        return -1L;
      }
      long l = discardSamples(readPosition);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long discardUpstreamSamples(int paramInt)
  {
    paramInt = getWriteIndex() - paramInt;
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= length - readPosition)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    length -= paramInt;
    largestQueuedTimestampUs = Math.max(largestDiscardedTimestampUs, getLargestTimestamp(length));
    if (length == 0) {
      return 0L;
    }
    paramInt = getRelativeIndex(length - 1);
    return offsets[paramInt] + sizes[paramInt];
  }
  
  public boolean format(Format paramFormat)
  {
    if (paramFormat == null)
    {
      try
      {
        upstreamFormatRequired = true;
        return false;
      }
      catch (Throwable paramFormat) {}
    }
    else
    {
      upstreamFormatRequired = false;
      boolean bool = Util.areEqual(paramFormat, upstreamFormat);
      if (bool) {
        return false;
      }
      upstreamFormat = paramFormat;
      return true;
    }
    throw paramFormat;
  }
  
  public int getFirstIndex()
  {
    return absoluteFirstIndex;
  }
  
  public long getFirstTimestampUs()
  {
    try
    {
      long l;
      if (length == 0) {
        l = Long.MIN_VALUE;
      } else {
        l = timesUs[relativeFirstIndex];
      }
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getLargestQueuedTimestampUs()
  {
    try
    {
      long l = largestQueuedTimestampUs;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getReadIndex()
  {
    return absoluteFirstIndex + readPosition;
  }
  
  public Format getUpstreamFormat()
  {
    try
    {
      Format localFormat;
      if (upstreamFormatRequired) {
        localFormat = null;
      } else {
        localFormat = upstreamFormat;
      }
      return localFormat;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getWriteIndex()
  {
    return absoluteFirstIndex + length;
  }
  
  public boolean hasNextSample()
  {
    try
    {
      int i = readPosition;
      int j = length;
      boolean bool;
      if (i != j) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int peekSourceId()
  {
    int i = getRelativeIndex(readPosition);
    if (hasNextSample()) {
      return sourceIds[i];
    }
    return upstreamSourceId;
  }
  
  public int read(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean1, boolean paramBoolean2, Format paramFormat, SampleExtrasHolder paramSampleExtrasHolder)
  {
    try
    {
      if (!hasNextSample())
      {
        if (paramBoolean2)
        {
          paramDecoderInputBuffer.setFlags(4);
          return -4;
        }
        if ((upstreamFormat != null) && ((paramBoolean1) || (upstreamFormat != paramFormat)))
        {
          format = upstreamFormat;
          return -5;
        }
        return -3;
      }
      int i = getRelativeIndex(readPosition);
      if ((!paramBoolean1) && (formats[i] == paramFormat))
      {
        paramBoolean1 = paramDecoderInputBuffer.isFlagsOnly();
        if (paramBoolean1) {
          return -3;
        }
        timeUs = timesUs[i];
        paramDecoderInputBuffer.setFlags(flags[i]);
        size = sizes[i];
        offset = offsets[i];
        cryptoData = cryptoDatas[i];
        readPosition += 1;
        return -4;
      }
      format = formats[i];
      return -5;
    }
    catch (Throwable paramFormatHolder)
    {
      throw paramFormatHolder;
    }
  }
  
  public void reset(boolean paramBoolean)
  {
    length = 0;
    absoluteFirstIndex = 0;
    relativeFirstIndex = 0;
    readPosition = 0;
    upstreamKeyframeRequired = true;
    largestDiscardedTimestampUs = Long.MIN_VALUE;
    largestQueuedTimestampUs = Long.MIN_VALUE;
    if (paramBoolean)
    {
      upstreamFormat = null;
      upstreamFormatRequired = true;
    }
  }
  
  public void rewind()
  {
    try
    {
      readPosition = 0;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean setReadPosition(int paramInt)
  {
    try
    {
      if ((absoluteFirstIndex <= paramInt) && (paramInt <= absoluteFirstIndex + length))
      {
        readPosition = (paramInt - absoluteFirstIndex);
        return true;
      }
      return false;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void sourceId(int paramInt)
  {
    upstreamSourceId = paramInt;
  }
  
  public static final class SampleExtrasHolder
  {
    public TrackOutput.CryptoData cryptoData;
    public long offset;
    public int size;
    
    public SampleExtrasHolder() {}
  }
}
