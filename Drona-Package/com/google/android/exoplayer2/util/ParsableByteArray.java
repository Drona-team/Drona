package com.google.android.exoplayer2.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class ParsableByteArray
{
  public byte[] data;
  private int limit;
  private int position;
  
  public ParsableByteArray()
  {
    data = Util.EMPTY_BYTE_ARRAY;
  }
  
  public ParsableByteArray(int paramInt)
  {
    data = new byte[paramInt];
    limit = paramInt;
  }
  
  public ParsableByteArray(byte[] paramArrayOfByte)
  {
    data = paramArrayOfByte;
    limit = paramArrayOfByte.length;
  }
  
  public ParsableByteArray(byte[] paramArrayOfByte, int paramInt)
  {
    data = paramArrayOfByte;
    limit = paramInt;
  }
  
  public int bytesLeft()
  {
    return limit - position;
  }
  
  public int capacity()
  {
    return data.length;
  }
  
  public int getPosition()
  {
    return position;
  }
  
  public int limit()
  {
    return limit;
  }
  
  public char peekChar()
  {
    return (char)((data[position] & 0xFF) << 8 | data[(position + 1)] & 0xFF);
  }
  
  public int peekUnsignedByte()
  {
    return data[position] & 0xFF;
  }
  
  public void readBytes(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    readBytes(data, 0, paramInt);
    paramParsableBitArray.setPosition(0);
  }
  
  public void readBytes(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramByteBuffer.put(data, position, paramInt);
    position += paramInt;
  }
  
  public void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    System.arraycopy(data, position, paramArrayOfByte, paramInt1, paramInt2);
    position += paramInt2;
  }
  
  public double readDouble()
  {
    return Double.longBitsToDouble(readLong());
  }
  
  public float readFloat()
  {
    return Float.intBitsToFloat(readInt());
  }
  
  public int readInt()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    k = arrayOfByte[k];
    arrayOfByte = data;
    int m = position;
    position = (m + 1);
    return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | arrayOfByte[m] & 0xFF;
  }
  
  public int readInt24()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    return (i & 0xFF) << 24 >> 8 | (j & 0xFF) << 8 | arrayOfByte[k] & 0xFF;
  }
  
  public String readLine()
  {
    if (bytesLeft() == 0) {
      return null;
    }
    int i = position;
    while ((i < limit) && (!Util.isLinebreak(data[i]))) {
      i += 1;
    }
    if ((i - position >= 3) && (data[position] == -17) && (data[(position + 1)] == -69) && (data[(position + 2)] == -65)) {
      position += 3;
    }
    String str = Util.fromUtf8Bytes(data, position, i - position);
    position = i;
    if (position == limit) {
      return str;
    }
    if (data[position] == 13)
    {
      position += 1;
      if (position == limit) {
        return str;
      }
    }
    if (data[position] == 10) {
      position += 1;
    }
    return str;
  }
  
  public int readLittleEndianInt()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    k = arrayOfByte[k];
    arrayOfByte = data;
    int m = position;
    position = (m + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (k & 0xFF) << 16 | (arrayOfByte[m] & 0xFF) << 24;
  }
  
  public int readLittleEndianInt24()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (arrayOfByte[k] & 0xFF) << 16;
  }
  
  public long readLittleEndianLong()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l4 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l5 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l6 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l7 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    return l1 & 0xFF | (l2 & 0xFF) << 8 | (l3 & 0xFF) << 16 | (l4 & 0xFF) << 24 | (l5 & 0xFF) << 32 | (l6 & 0xFF) << 40 | (l7 & 0xFF) << 48 | (0xFF & arrayOfByte[i]) << 56;
  }
  
  public short readLittleEndianShort()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    return (short)(i & 0xFF | (arrayOfByte[j] & 0xFF) << 8);
  }
  
  public long readLittleEndianUnsignedInt()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    return l1 & 0xFF | (l2 & 0xFF) << 8 | (l3 & 0xFF) << 16 | (0xFF & arrayOfByte[i]) << 24;
  }
  
  public int readLittleEndianUnsignedInt24()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (arrayOfByte[k] & 0xFF) << 16;
  }
  
  public int readLittleEndianUnsignedIntToInt()
  {
    int i = readLittleEndianInt();
    if (i >= 0) {
      return i;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Top bit not zero: ");
    localStringBuilder.append(i);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public int readLittleEndianUnsignedShort()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    return i & 0xFF | (arrayOfByte[j] & 0xFF) << 8;
  }
  
  public long readLong()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l4 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l5 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l6 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l7 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    return (l1 & 0xFF) << 56 | (l2 & 0xFF) << 48 | (l3 & 0xFF) << 40 | (l4 & 0xFF) << 32 | (l5 & 0xFF) << 24 | (l6 & 0xFF) << 16 | (l7 & 0xFF) << 8 | 0xFF & arrayOfByte[i];
  }
  
  public String readNullTerminatedString()
  {
    if (bytesLeft() == 0) {
      return null;
    }
    int i = position;
    while ((i < limit) && (data[i] != 0)) {
      i += 1;
    }
    String str = Util.fromUtf8Bytes(data, position, i - position);
    position = i;
    if (position < limit) {
      position += 1;
    }
    return str;
  }
  
  public String readNullTerminatedString(int paramInt)
  {
    if (paramInt == 0) {
      return "";
    }
    int i = position + paramInt - 1;
    if ((i < limit) && (data[i] == 0)) {
      i = paramInt - 1;
    } else {
      i = paramInt;
    }
    String str = Util.fromUtf8Bytes(data, position, i);
    position += paramInt;
    return str;
  }
  
  public short readShort()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    return (short)((i & 0xFF) << 8 | arrayOfByte[j] & 0xFF);
  }
  
  public String readString(int paramInt)
  {
    return readString(paramInt, Charset.forName("UTF-8"));
  }
  
  public String readString(int paramInt, Charset paramCharset)
  {
    paramCharset = new String(data, position, paramInt, paramCharset);
    position += paramInt;
    return paramCharset;
  }
  
  public int readSynchSafeInt()
  {
    return readUnsignedByte() << 21 | readUnsignedByte() << 14 | readUnsignedByte() << 7 | readUnsignedByte();
  }
  
  public int readUnsignedByte()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    return arrayOfByte[i] & 0xFF;
  }
  
  public int readUnsignedFixedPoint1616()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    position += 2;
    return (i & 0xFF) << 8 | j & 0xFF;
  }
  
  public long readUnsignedInt()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = data;
    i = position;
    position = (i + 1);
    return (l1 & 0xFF) << 24 | (l2 & 0xFF) << 16 | (l3 & 0xFF) << 8 | 0xFF & arrayOfByte[i];
  }
  
  public int readUnsignedInt24()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = data;
    int k = position;
    position = (k + 1);
    return (i & 0xFF) << 16 | (j & 0xFF) << 8 | arrayOfByte[k] & 0xFF;
  }
  
  public int readUnsignedIntToInt()
  {
    int i = readInt();
    if (i >= 0) {
      return i;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Top bit not zero: ");
    localStringBuilder.append(i);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public long readUnsignedLongToLong()
  {
    long l = readLong();
    if (l >= 0L) {
      return l;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Top bit not zero: ");
    localStringBuilder.append(l);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public int readUnsignedShort()
  {
    byte[] arrayOfByte = data;
    int i = position;
    position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = data;
    int j = position;
    position = (j + 1);
    return (i & 0xFF) << 8 | arrayOfByte[j] & 0xFF;
  }
  
  public long readUtf8EncodedLong()
  {
    Object localObject2 = data;
    Object localObject1 = this;
    long l = localObject2[position];
    int i = 7;
    int j;
    int k;
    for (;;)
    {
      j = 1;
      if (i < 0) {
        break;
      }
      k = 1 << i;
      if ((k & l) == 0L)
      {
        if (i < 6)
        {
          l &= k - 1;
          i = 7 - i;
          break label85;
        }
        if (i != 7) {
          break;
        }
        i = 1;
        break label85;
      }
      i -= 1;
    }
    i = 0;
    label85:
    if (i != 0)
    {
      while (j < i)
      {
        byte[] arrayOfByte = data;
        localObject2 = localObject1;
        k = arrayOfByte[(position + j)];
        if ((k & 0xC0) == 128)
        {
          l = l << 6 | k & 0x3F;
          j += 1;
          localObject1 = localObject2;
        }
        else
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Invalid UTF-8 sequence continuation byte: ");
          ((StringBuilder)localObject1).append(l);
          throw new NumberFormatException(((StringBuilder)localObject1).toString());
        }
      }
      position += i;
      return l;
    }
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("Invalid UTF-8 sequence first byte: ");
    ((StringBuilder)localObject1).append(l);
    throw new NumberFormatException(((StringBuilder)localObject1).toString());
  }
  
  public void reset()
  {
    position = 0;
    limit = 0;
  }
  
  public void reset(int paramInt)
  {
    byte[] arrayOfByte;
    if (capacity() < paramInt) {
      arrayOfByte = new byte[paramInt];
    } else {
      arrayOfByte = data;
    }
    reset(arrayOfByte, paramInt);
  }
  
  public void reset(byte[] paramArrayOfByte)
  {
    reset(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    data = paramArrayOfByte;
    limit = paramInt;
    position = 0;
  }
  
  public void setLimit(int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= data.length)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    limit = paramInt;
  }
  
  public void setPosition(int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= limit)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    position = paramInt;
  }
  
  public void skipBytes(int paramInt)
  {
    setPosition(position + paramInt);
  }
}
