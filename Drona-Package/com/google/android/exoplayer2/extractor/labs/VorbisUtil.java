package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;

final class VorbisUtil
{
  private static final String PAGE_KEY = "VorbisUtil";
  
  private VorbisUtil() {}
  
  public static int iLog(int paramInt)
  {
    int i = 0;
    while (paramInt > 0)
    {
      i += 1;
      paramInt >>>= 1;
    }
    return i;
  }
  
  private static long mapType1QuantValues(long paramLong1, long paramLong2)
  {
    return Math.floor(Math.pow(paramLong1, 1.0D / paramLong2));
  }
  
  private static CodeBook readBook(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    if (paramVorbisBitArray.readBits(24) == 5653314)
    {
      int m = paramVorbisBitArray.readBits(16);
      int n = paramVorbisBitArray.readBits(24);
      localObject = new long[n];
      boolean bool1 = paramVorbisBitArray.readBit();
      long l = 0L;
      int i = 0;
      if (!bool1)
      {
        boolean bool2 = paramVorbisBitArray.readBit();
        while (i < localObject.length)
        {
          if (bool2)
          {
            if (paramVorbisBitArray.readBit()) {
              localObject[i] = (paramVorbisBitArray.readBits(5) + 1);
            } else {
              localObject[i] = 0L;
            }
          }
          else {
            localObject[i] = (paramVorbisBitArray.readBits(5) + 1);
          }
          i += 1;
        }
      }
      i = paramVorbisBitArray.readBits(5) + 1;
      int j = 0;
      while (j < localObject.length)
      {
        int i1 = paramVorbisBitArray.readBits(iLog(n - j));
        int k = 0;
        while ((k < i1) && (j < localObject.length))
        {
          localObject[j] = i;
          j += 1;
          k += 1;
        }
        i += 1;
      }
      i = paramVorbisBitArray.readBits(4);
      if (i <= 2)
      {
        if ((i == 1) || (i == 2))
        {
          paramVorbisBitArray.skipBits(32);
          paramVorbisBitArray.skipBits(32);
          j = paramVorbisBitArray.readBits(4);
          paramVorbisBitArray.skipBits(1);
          if (i == 1)
          {
            if (m != 0) {
              l = mapType1QuantValues(n, m);
            }
          }
          else {
            l = n * m;
          }
          paramVorbisBitArray.skipBits((int)(l * (j + 1)));
        }
        return new CodeBook(m, n, (long[])localObject, i, bool1);
      }
      paramVorbisBitArray = new StringBuilder();
      paramVorbisBitArray.append("lookup type greater than 2 not decodable: ");
      paramVorbisBitArray.append(i);
      throw new ParserException(paramVorbisBitArray.toString());
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("expected code book to start with [0x56, 0x43, 0x42] at ");
    ((StringBuilder)localObject).append(paramVorbisBitArray.getPosition());
    throw new ParserException(((StringBuilder)localObject).toString());
  }
  
  private static void readFloors(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int n = paramVorbisBitArray.readBits(6);
    int i = 0;
    while (i < n + 1)
    {
      int j = paramVorbisBitArray.readBits(16);
      int i1;
      int[] arrayOfInt1;
      int k;
      int m;
      int[] arrayOfInt2;
      int i2;
      switch (j)
      {
      default: 
        paramVorbisBitArray = new StringBuilder();
        paramVorbisBitArray.append("floor type greater than 1 not decodable: ");
        paramVorbisBitArray.append(j);
        throw new ParserException(paramVorbisBitArray.toString());
      case 1: 
        i1 = paramVorbisBitArray.readBits(5);
        arrayOfInt1 = new int[i1];
        j = 0;
        for (k = -1; j < i1; k = m)
        {
          arrayOfInt1[j] = paramVorbisBitArray.readBits(4);
          m = k;
          if (arrayOfInt1[j] > k) {
            m = arrayOfInt1[j];
          }
          j += 1;
        }
        arrayOfInt2 = new int[k + 1];
        j = 0;
        while (j < arrayOfInt2.length)
        {
          arrayOfInt2[j] = (paramVorbisBitArray.readBits(3) + 1);
          m = paramVorbisBitArray.readBits(2);
          if (m > 0) {
            paramVorbisBitArray.skipBits(8);
          }
          k = 0;
          while (k < 1 << m)
          {
            paramVorbisBitArray.skipBits(8);
            k += 1;
          }
          j += 1;
        }
        paramVorbisBitArray.skipBits(2);
        i2 = paramVorbisBitArray.readBits(4);
        j = 0;
        m = 0;
        k = 0;
      }
      while (j < i1)
      {
        m += arrayOfInt2[arrayOfInt1[j]];
        while (k < m)
        {
          paramVorbisBitArray.skipBits(i2);
          k += 1;
        }
        j += 1;
        continue;
        paramVorbisBitArray.skipBits(8);
        paramVorbisBitArray.skipBits(16);
        paramVorbisBitArray.skipBits(16);
        paramVorbisBitArray.skipBits(6);
        paramVorbisBitArray.skipBits(8);
        k = paramVorbisBitArray.readBits(4);
        j = 0;
        while (j < k + 1)
        {
          paramVorbisBitArray.skipBits(8);
          j += 1;
        }
      }
      i += 1;
    }
  }
  
  private static void readMappings(int paramInt, VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int m = paramVorbisBitArray.readBits(6);
    int i = 0;
    while (i < m + 1)
    {
      int j = paramVorbisBitArray.readBits(16);
      if (j != 0)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("mapping type other than 0 not supported: ");
        localStringBuilder.append(j);
        Log.e("VorbisUtil", localStringBuilder.toString());
      }
      else
      {
        if (paramVorbisBitArray.readBit()) {
          j = paramVorbisBitArray.readBits(4) + 1;
        } else {
          j = 1;
        }
        if (paramVorbisBitArray.readBit())
        {
          int n = paramVorbisBitArray.readBits(8);
          k = 0;
          while (k < n + 1)
          {
            int i1 = paramInt - 1;
            paramVorbisBitArray.skipBits(iLog(i1));
            paramVorbisBitArray.skipBits(iLog(i1));
            k += 1;
          }
        }
        if (paramVorbisBitArray.readBits(2) != 0) {
          break label224;
        }
        if (j > 1)
        {
          k = 0;
          while (k < paramInt)
          {
            paramVorbisBitArray.skipBits(4);
            k += 1;
          }
        }
        int k = 0;
        while (k < j)
        {
          paramVorbisBitArray.skipBits(8);
          paramVorbisBitArray.skipBits(8);
          paramVorbisBitArray.skipBits(8);
          k += 1;
        }
      }
      i += 1;
      continue;
      label224:
      throw new ParserException("to reserved bits must be zero after mapping coupling steps");
    }
  }
  
  private static Mode[] readModes(VorbisBitArray paramVorbisBitArray)
  {
    int j = paramVorbisBitArray.readBits(6) + 1;
    Mode[] arrayOfMode = new Mode[j];
    int i = 0;
    while (i < j)
    {
      arrayOfMode[i] = new Mode(paramVorbisBitArray.readBit(), paramVorbisBitArray.readBits(16), paramVorbisBitArray.readBits(16), paramVorbisBitArray.readBits(8));
      i += 1;
    }
    return arrayOfMode;
  }
  
  private static void readResidues(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int m = paramVorbisBitArray.readBits(6);
    int i = 0;
    while (i < m + 1) {
      if (paramVorbisBitArray.readBits(16) <= 2)
      {
        paramVorbisBitArray.skipBits(24);
        paramVorbisBitArray.skipBits(24);
        paramVorbisBitArray.skipBits(24);
        int n = paramVorbisBitArray.readBits(6) + 1;
        paramVorbisBitArray.skipBits(8);
        int[] arrayOfInt = new int[n];
        int j = 0;
        int k;
        while (j < n)
        {
          int i1 = paramVorbisBitArray.readBits(3);
          if (paramVorbisBitArray.readBit()) {
            k = paramVorbisBitArray.readBits(5);
          } else {
            k = 0;
          }
          arrayOfInt[j] = (k * 8 + i1);
          j += 1;
        }
        j = 0;
        while (j < n)
        {
          k = 0;
          while (k < 8)
          {
            if ((arrayOfInt[j] & 1 << k) != 0) {
              paramVorbisBitArray.skipBits(8);
            }
            k += 1;
          }
          j += 1;
        }
        i += 1;
      }
      else
      {
        throw new ParserException("residueType greater than 2 is not decodable");
      }
    }
  }
  
  public static CommentHeader readVorbisCommentHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    int i = 0;
    verifyVorbisHeaderCapturePattern(3, paramParsableByteArray, false);
    String str = paramParsableByteArray.readString((int)paramParsableByteArray.readLittleEndianUnsignedInt());
    int j = str.length();
    long l = paramParsableByteArray.readLittleEndianUnsignedInt();
    String[] arrayOfString = new String[(int)l];
    j = 11 + j + 4;
    while (i < l)
    {
      arrayOfString[i] = paramParsableByteArray.readString((int)paramParsableByteArray.readLittleEndianUnsignedInt());
      j = j + 4 + arrayOfString[i].length();
      i += 1;
    }
    if ((paramParsableByteArray.readUnsignedByte() & 0x1) != 0) {
      return new CommentHeader(str, arrayOfString, j + 1);
    }
    throw new ParserException("framing bit expected to be set");
  }
  
  public static VorbisIdHeader readVorbisIdentificationHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    verifyVorbisHeaderCapturePattern(1, paramParsableByteArray, false);
    long l1 = paramParsableByteArray.readLittleEndianUnsignedInt();
    int i = paramParsableByteArray.readUnsignedByte();
    long l2 = paramParsableByteArray.readLittleEndianUnsignedInt();
    int j = paramParsableByteArray.readLittleEndianInt();
    int k = paramParsableByteArray.readLittleEndianInt();
    int m = paramParsableByteArray.readLittleEndianInt();
    int i1 = paramParsableByteArray.readUnsignedByte();
    int n = (int)Math.pow(2.0D, i1 & 0xF);
    i1 = (int)Math.pow(2.0D, (i1 & 0xF0) >> 4);
    boolean bool;
    if ((paramParsableByteArray.readUnsignedByte() & 0x1) > 0) {
      bool = true;
    } else {
      bool = false;
    }
    return new VorbisIdHeader(l1, i, l2, j, k, m, n, i1, bool, Arrays.copyOf(data, paramParsableByteArray.limit()));
  }
  
  public static Mode[] readVorbisModes(ParsableByteArray paramParsableByteArray, int paramInt)
    throws ParserException
  {
    int j = 0;
    verifyVorbisHeaderCapturePattern(5, paramParsableByteArray, false);
    int k = paramParsableByteArray.readUnsignedByte();
    VorbisBitArray localVorbisBitArray = new VorbisBitArray(data);
    localVorbisBitArray.skipBits(paramParsableByteArray.getPosition() * 8);
    int i = 0;
    while (i < k + 1)
    {
      readBook(localVorbisBitArray);
      i += 1;
    }
    k = localVorbisBitArray.readBits(6);
    i = j;
    while (i < k + 1) {
      if (localVorbisBitArray.readBits(16) == 0) {
        i += 1;
      } else {
        throw new ParserException("placeholder of time domain transforms not zeroed out");
      }
    }
    readFloors(localVorbisBitArray);
    readResidues(localVorbisBitArray);
    readMappings(paramInt, localVorbisBitArray);
    paramParsableByteArray = readModes(localVorbisBitArray);
    if (localVorbisBitArray.readBit()) {
      return paramParsableByteArray;
    }
    throw new ParserException("framing bit after modes not set as expected");
  }
  
  public static boolean verifyVorbisHeaderCapturePattern(int paramInt, ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws ParserException
  {
    if (paramParsableByteArray.bytesLeft() < 7)
    {
      if (paramBoolean) {
        return false;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("too short header: ");
      localStringBuilder.append(paramParsableByteArray.bytesLeft());
      throw new ParserException(localStringBuilder.toString());
    }
    if (paramParsableByteArray.readUnsignedByte() != paramInt)
    {
      if (paramBoolean) {
        return false;
      }
      paramParsableByteArray = new StringBuilder();
      paramParsableByteArray.append("expected header type ");
      paramParsableByteArray.append(Integer.toHexString(paramInt));
      throw new ParserException(paramParsableByteArray.toString());
    }
    if ((paramParsableByteArray.readUnsignedByte() == 118) && (paramParsableByteArray.readUnsignedByte() == 111) && (paramParsableByteArray.readUnsignedByte() == 114) && (paramParsableByteArray.readUnsignedByte() == 98) && (paramParsableByteArray.readUnsignedByte() == 105) && (paramParsableByteArray.readUnsignedByte() == 115)) {
      return true;
    }
    if (paramBoolean) {
      return false;
    }
    throw new ParserException("expected characters 'vorbis'");
  }
  
  public final class CodeBook
  {
    public final int dimensions;
    public final int entries;
    public final boolean isOrdered;
    public final long[] lengthMap;
    public final int lookupType;
    
    public CodeBook(int paramInt1, long[] paramArrayOfLong, int paramInt2, boolean paramBoolean)
    {
      dimensions = this$1;
      entries = paramInt1;
      lengthMap = paramArrayOfLong;
      lookupType = paramInt2;
      isOrdered = paramBoolean;
    }
  }
  
  public final class CommentHeader
  {
    public final String[] comments;
    public final int length;
    
    public CommentHeader(String[] paramArrayOfString, int paramInt)
    {
      comments = paramArrayOfString;
      length = paramInt;
    }
  }
  
  public final class Mode
  {
    public final boolean blockFlag;
    public final int mapping;
    public final int transformType;
    public final int windowType;
    
    public Mode(int paramInt1, int paramInt2, int paramInt3)
    {
      blockFlag = this$1;
      windowType = paramInt1;
      transformType = paramInt2;
      mapping = paramInt3;
    }
  }
  
  public final class VorbisIdHeader
  {
    public final int bitrateMax;
    public final int bitrateMin;
    public final int bitrateNominal;
    public final int blockSize0;
    public final int blockSize1;
    public final int channels;
    public final byte[] data;
    public final boolean framingFlag;
    public final long sampleRate;
    public final long version;
    
    public VorbisIdHeader(int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, byte[] paramArrayOfByte)
    {
      version = this$1;
      channels = paramInt1;
      sampleRate = paramLong;
      bitrateMax = paramInt2;
      bitrateNominal = paramInt3;
      bitrateMin = paramInt4;
      blockSize0 = paramInt5;
      blockSize1 = paramInt6;
      framingFlag = paramBoolean;
      data = paramArrayOfByte;
    }
    
    public int getApproximateBitrate()
    {
      if (bitrateNominal == 0) {
        return (bitrateMin + bitrateMax) / 2;
      }
      return bitrateNominal;
    }
  }
}
