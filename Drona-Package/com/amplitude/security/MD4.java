package com.amplitude.security;

import java.security.DigestException;
import java.security.MessageDigest;

public final class MD4
  extends MessageDigest
  implements Cloneable
{
  private byte[] buffer = new byte[64];
  private long bytes;
  private int cipher;
  private int count;
  private int counter;
  private int digest;
  private int padded;
  
  public MD4()
  {
    super("MD5");
    init();
  }
  
  private final void engineUpdate(byte[] paramArrayOfByte, int paramInt)
  {
    int i = digest;
    int n = cipher;
    int i1 = counter;
    int i2 = paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 3)] << 24;
    int j = ((n ^ i1) & i ^ i1) + i2 - 680876936 + count;
    int m = (j >>> 25 | j << 7) + i;
    int k = paramArrayOfByte[(paramInt + 4)];
    int i3 = paramArrayOfByte[(paramInt + 5)];
    j = paramInt + 10;
    k = k & 0xFF | (i3 & 0xFF) << 8 | (paramArrayOfByte[(j - 4)] & 0xFF) << 16 | paramArrayOfByte[(j - 3)] << 24;
    paramInt = ((i ^ n) & m ^ n) + k - 389564586 + i1;
    int i5 = (paramInt << 12 | paramInt >>> 20) + m;
    paramInt = paramArrayOfByte[(j - 2)] & 0xFF | (paramArrayOfByte[(j - 1)] & 0xFF) << 8 | (paramArrayOfByte[j] & 0xFF) << 16 | paramArrayOfByte[(j + 1)] << 24;
    n = ((m ^ i) & i5 ^ i) + paramInt + 606105819 + n;
    int i6 = (n << 17 | n >>> 15) + i5;
    i1 = paramArrayOfByte[(j + 2)] & 0xFF | (paramArrayOfByte[(j + 3)] & 0xFF) << 8 | (paramArrayOfByte[(j + 4)] & 0xFF) << 16 | paramArrayOfByte[(j + 5)] << 24;
    n = ((i5 ^ m) & i6 ^ m) + i1 - 1044525330 + i;
    int i4 = (n >>> 10 | n << 22) + i6;
    int i7 = j + 10;
    j = paramArrayOfByte[(i7 - 4)] & 0xFF | (paramArrayOfByte[(i7 - 3)] & 0xFF) << 8 | (paramArrayOfByte[(i7 - 2)] & 0xFF) << 16 | paramArrayOfByte[(i7 - 1)] << 24;
    m = ((i6 ^ i5) & i4 ^ i5) + j - 176418897 + m;
    n = (m << 7 | m >>> 25) + i4;
    i3 = paramArrayOfByte[i7] & 0xFF | (paramArrayOfByte[(i7 + 1)] & 0xFF) << 8 | (paramArrayOfByte[(i7 + 2)] & 0xFF) << 16 | paramArrayOfByte[(i7 + 3)] << 24;
    m = ((i4 ^ i6) & n ^ i6) + i3 + 1200080426 + i5;
    i5 = (m << 12 | m >>> 20) + n;
    m = paramArrayOfByte[(i7 + 4)];
    int i8 = paramArrayOfByte[(i7 + 5)];
    i7 += 10;
    m = (i8 & 0xFF) << 8 | m & 0xFF | (paramArrayOfByte[(i7 - 4)] & 0xFF) << 16 | paramArrayOfByte[(i7 - 3)] << 24;
    i6 = ((n ^ i4) & i5 ^ i4) + m - 1473231341 + i6;
    int i10 = (i6 << 17 | i6 >>> 15) + i5;
    i6 = paramArrayOfByte[(i7 - 2)];
    i6 = (paramArrayOfByte[(i7 - 1)] & 0xFF) << 8 | i6 & 0xFF | (paramArrayOfByte[i7] & 0xFF) << 16 | paramArrayOfByte[(i7 + 1)] << 24;
    i4 = ((i5 ^ n) & i10 ^ n) + i6 - 45705983 + i4;
    int i9 = (i4 << 22 | i4 >>> 10) + i10;
    i4 = paramArrayOfByte[(i7 + 2)];
    i4 = (paramArrayOfByte[(i7 + 3)] & 0xFF) << 8 | i4 & 0xFF | (paramArrayOfByte[(i7 + 4)] & 0xFF) << 16 | paramArrayOfByte[(i7 + 5)] << 24;
    n = ((i10 ^ i5) & i9 ^ i5) + i4 + 1770035416 + n;
    i8 = (n << 7 | n >>> 25) + i9;
    int i12 = i7 + 10;
    n = paramArrayOfByte[(i12 - 4)];
    n = (paramArrayOfByte[(i12 - 3)] & 0xFF) << 8 | n & 0xFF | (paramArrayOfByte[(i12 - 2)] & 0xFF) << 16 | paramArrayOfByte[(i12 - 1)] << 24;
    i5 = ((i9 ^ i10) & i8 ^ i10) + n - 1958414417 + i5;
    int i11 = (i5 << 12 | i5 >>> 20) + i8;
    i5 = paramArrayOfByte[i12];
    i7 = (paramArrayOfByte[(i12 + 1)] & 0xFF) << 8 | i5 & 0xFF | (paramArrayOfByte[(i12 + 2)] & 0xFF) << 16 | paramArrayOfByte[(i12 + 3)] << 24;
    i5 = ((i8 ^ i9) & i11 ^ i9) + i7 - 42063 + i10;
    i10 = (i5 << 17 | i5 >>> 15) + i11;
    i5 = paramArrayOfByte[(i12 + 4)];
    int i13 = paramArrayOfByte[(i12 + 5)];
    int i15 = i12 + 10;
    i5 = (i13 & 0xFF) << 8 | i5 & 0xFF | (paramArrayOfByte[(i15 - 4)] & 0xFF) << 16 | paramArrayOfByte[(i15 - 3)] << 24;
    i9 = ((i11 ^ i8) & i10 ^ i8) + i5 - 1990404162 + i9;
    i12 = (i9 << 22 | i9 >>> 10) + i10;
    i9 = paramArrayOfByte[(i15 - 2)];
    i9 = (paramArrayOfByte[(i15 - 1)] & 0xFF) << 8 | i9 & 0xFF | (paramArrayOfByte[i15] & 0xFF) << 16 | paramArrayOfByte[(i15 + 1)] << 24;
    i8 = ((i10 ^ i11) & i12 ^ i11) + i9 + 1804603682 + i8;
    i13 = (i8 << 7 | i8 >>> 25) + i12;
    i8 = paramArrayOfByte[(i15 + 2)];
    i8 = (paramArrayOfByte[(i15 + 3)] & 0xFF) << 8 | i8 & 0xFF | (paramArrayOfByte[(i15 + 4)] & 0xFF) << 16 | paramArrayOfByte[(i15 + 5)] << 24;
    i11 = ((i12 ^ i10) & i13 ^ i10) + i8 - 40341101 + i11;
    int i14 = (i11 << 12 | i11 >>> 20) + i13;
    int i16 = i15 + 10;
    i11 = paramArrayOfByte[(i16 - 4)];
    i11 = (paramArrayOfByte[(i16 - 3)] & 0xFF) << 8 | i11 & 0xFF | (paramArrayOfByte[(i16 - 2)] & 0xFF) << 16 | paramArrayOfByte[(i16 - 1)] << 24;
    i10 = ((i13 ^ i12) & i14 ^ i12) + i11 - 1502002290 + i10;
    i15 = (i10 << 17 | i10 >>> 15) + i14;
    i10 = paramArrayOfByte[i16];
    int i17 = paramArrayOfByte[(i16 + 1)];
    int i18 = paramArrayOfByte[(i16 + 2)];
    i10 = paramArrayOfByte[(i16 + 3)] << 24 | (i17 & 0xFF) << 8 | i10 & 0xFF | (i18 & 0xFF) << 16;
    i12 = ((i14 ^ i13) & i15 ^ i13) + i10 + 1236535329 + i12;
    i12 = (i12 << 22 | i12 >>> 10) + i15;
    i13 = ((i15 ^ i12) & i14 ^ i15) + k - 165796510 + i13;
    i13 = (i13 << 5 | i13 >>> 27) + i12;
    i14 = ((i12 ^ i13) & i15 ^ i12) + m - 1069501632 + i14;
    i14 = (i14 << 9 | i14 >>> 23) + i13;
    i15 = ((i13 ^ i14) & i12 ^ i13) + i5 + 643717713 + i15;
    i15 = (i15 << 14 | i15 >>> 18) + i14;
    i12 = ((i14 ^ i15) & i13 ^ i14) + i2 - 373897302 + i12;
    i12 = (i12 << 20 | i12 >>> 12) + i15;
    i13 = ((i15 ^ i12) & i14 ^ i15) + i3 - 701558691 + i13;
    i13 = (i13 << 5 | i13 >>> 27) + i12;
    i14 = ((i12 ^ i13) & i15 ^ i12) + i7 + 38016083 + i14;
    i14 = (i14 << 9 | i14 >>> 23) + i13;
    i15 = ((i13 ^ i14) & i12 ^ i13) + i10 - 660478335 + i15;
    i15 = (i15 << 14 | i15 >>> 18) + i14;
    i12 = ((i14 ^ i15) & i13 ^ i14) + j - 405537848 + i12;
    i12 = (i12 << 20 | i12 >>> 12) + i15;
    i13 = ((i15 ^ i12) & i14 ^ i15) + n + 568446438 + i13;
    i13 = (i13 << 5 | i13 >>> 27) + i12;
    i14 = ((i12 ^ i13) & i15 ^ i12) + i11 - 1019803690 + i14;
    i14 = (i14 << 9 | i14 >>> 23) + i13;
    i15 = ((i13 ^ i14) & i12 ^ i13) + i1 - 187363961 + i15;
    i15 = (i15 << 14 | i15 >>> 18) + i14;
    i12 = ((i14 ^ i15) & i13 ^ i14) + i4 + 1163531501 + i12;
    i12 = (i12 << 20 | i12 >>> 12) + i15;
    i13 = ((i15 ^ i12) & i14 ^ i15) + i8 - 1444681467 + i13;
    i13 = (i13 << 5 | i13 >>> 27) + i12;
    i14 = ((i12 ^ i13) & i15 ^ i12) + paramInt - 51403784 + i14;
    i14 = (i14 << 9 | i14 >>> 23) + i13;
    i15 = ((i13 ^ i14) & i12 ^ i13) + i6 + 1735328473 + i15;
    i15 = (i15 << 14 | i15 >>> 18) + i14;
    i12 = ((i14 ^ i15) & i13 ^ i14) + i9 - 1926607734 + i12;
    i12 = (i12 << 20 | i12 >>> 12) + i15;
    i13 = (i15 ^ i12 ^ i14) + i3 - 378558 + i13;
    i13 = (i13 << 4 | i13 >>> 28) + i12;
    i14 = (i12 ^ i13 ^ i15) + i4 - 2022574463 + i14;
    i14 = (i14 << 11 | i14 >>> 21) + i13;
    i15 = (i13 ^ i14 ^ i12) + i5 + 1839030562 + i15;
    i15 = (i15 << 16 | i15 >>> 16) + i14;
    i12 = (i14 ^ i15 ^ i13) + i11 - 35309556 + i12;
    i12 = (i12 << 23 | i12 >>> 9) + i15;
    i13 = (i15 ^ i12 ^ i14) + k - 1530992060 + i13;
    i13 = (i13 << 4 | i13 >>> 28) + i12;
    i14 = (i12 ^ i13 ^ i15) + j + 1272893353 + i14;
    i14 = (i14 << 11 | i14 >>> 21) + i13;
    i15 = (i13 ^ i14 ^ i12) + i6 - 155497632 + i15;
    i15 = (i15 << 16 | i15 >>> 16) + i14;
    i12 = (i14 ^ i15 ^ i13) + i7 - 1094730640 + i12;
    i12 = (i12 << 23 | i12 >>> 9) + i15;
    i13 = (i15 ^ i12 ^ i14) + i8 + 681279174 + i13;
    i13 = (i13 << 4 | i13 >>> 28) + i12;
    i14 = (i12 ^ i13 ^ i15) + i2 - 358537222 + i14;
    i14 = (i14 << 11 | i14 >>> 21) + i13;
    i15 = (i13 ^ i14 ^ i12) + i1 - 722521979 + i15;
    i15 = (i15 << 16 | i15 >>> 16) + i14;
    i12 = (i14 ^ i15 ^ i13) + m + 76029189 + i12;
    i12 = (i12 << 23 | i12 >>> 9) + i15;
    i13 = (i15 ^ i12 ^ i14) + n - 640364487 + i13;
    i13 = (i13 << 4 | i13 >>> 28) + i12;
    i14 = (i12 ^ i13 ^ i15) + i9 - 421815835 + i14;
    i14 = (i14 << 11 | i14 >>> 21) + i13;
    i15 = (i13 ^ i14 ^ i12) + i10 + 530742520 + i15;
    i15 = (i15 << 16 | i15 >>> 16) + i14;
    i12 = (i14 ^ i15 ^ i13) + paramInt - 995338651 + i12;
    i12 = (i12 << 23 | i12 >>> 9) + i15;
    i2 = ((i14 | i12) ^ i15) + i2 - 198630844 + i13;
    i2 = (i2 << 6 | i2 >>> 26) + i12;
    i6 = ((i15 | i2) ^ i12) + i6 + 1126891415 + i14;
    i6 = (i6 << 10 | i6 >>> 22) + i2;
    i11 = ((i12 | i6) ^ i2) + i11 - 1416354905 + i15;
    i11 = (i11 << 15 | i11 >>> 17) + i6;
    i3 = ((i2 | i11) ^ i6) + i3 - 57434055 + i12;
    i3 = (i3 << 21 | i3 >>> 11) + i11;
    i2 = ((i6 | i3) ^ i11) + i9 + 1700485571 + i2;
    i2 = (i2 << 6 | i2 >>> 26) + i3;
    i1 = ((i11 | i2) ^ i3) + i1 - 1894986606 + i6;
    i1 = (i1 << 10 | i1 >>> 22) + i2;
    i6 = ((i3 | i1) ^ i2) + i7 - 1051523 + i11;
    i6 = (i6 << 15 | i6 >>> 17) + i1;
    k = ((i2 | i6) ^ i1) + k - 2054922799 + i3;
    k = (k << 21 | k >>> 11) + i6;
    i2 = ((i1 | k) ^ i6) + i4 + 1873313359 + i2;
    i2 = (i2 >>> 26 | i2 << 6) + k;
    i1 = ((i6 | i2) ^ k) + i10 - 30611744 + i1;
    i1 = (i1 << 10 | i1 >>> 22) + i2;
    m = ((k | i1) ^ i2) + m - 1560198380 + i6;
    m = (m << 15 | m >>> 17) + i1;
    k = ((i2 | m) ^ i1) + i8 + 1309151649 + k;
    k = (k << 21 | k >>> 11) + m;
    j = ((i1 | k) ^ m) + j - 145523070 + i2;
    j = (j >>> 26 | j << 6) + k;
    i1 = ((m | j) ^ k) + i5 - 1120210379 + i1;
    i1 = (i1 << 10 | i1 >>> 22) + j;
    paramInt = ((k | i1) ^ j) + paramInt + 718787259 + m;
    paramInt = (paramInt << 15 | paramInt >>> 17) + i1;
    k = ((j | paramInt) ^ i1) + n - 343485551 + k;
    digest = (i + paramInt + (k << 21 | k >>> 11));
    cipher += paramInt;
    counter += i1;
    count += j;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    MD4 localMD4 = (MD4)super.clone();
    buffer = ((byte[])buffer.clone());
    return localMD4;
  }
  
  public int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DigestException
  {
    if (paramInt2 >= 16)
    {
      if (paramArrayOfByte.length - paramInt1 >= 16)
      {
        byte[] arrayOfByte = buffer;
        paramInt2 = padded;
        arrayOfByte[paramInt2] = Byte.MIN_VALUE;
        switch (paramInt2)
        {
        default: 
          break;
        case 56: 
          arrayOfByte[57] = 0;
        case 57: 
          arrayOfByte[58] = 0;
        case 58: 
          arrayOfByte[59] = 0;
        case 59: 
          arrayOfByte[60] = 0;
        case 60: 
          arrayOfByte[61] = 0;
        case 61: 
          arrayOfByte[62] = 0;
        case 62: 
          arrayOfByte[63] = 0;
        case 63: 
          engineUpdate(arrayOfByte, 0);
          paramInt2 = -1;
        }
        switch (paramInt2 & 0x7)
        {
        default: 
          break;
        case 7: 
          paramInt2 -= 3;
          break;
        case 6: 
          paramInt2 -= 2;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 5: 
          paramInt2 -= 1;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 4: 
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 3: 
          paramInt2 += 1;
          arrayOfByte[paramInt2] = 0;
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 2: 
          paramInt2 += 2;
          arrayOfByte[(paramInt2 - 1)] = 0;
          arrayOfByte[paramInt2] = 0;
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 1: 
          paramInt2 += 3;
          arrayOfByte[(paramInt2 - 2)] = 0;
          arrayOfByte[(paramInt2 - 1)] = 0;
          arrayOfByte[paramInt2] = 0;
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
          break;
        case 0: 
          paramInt2 += 4;
          arrayOfByte[(paramInt2 - 3)] = 0;
          arrayOfByte[(paramInt2 - 2)] = 0;
          arrayOfByte[(paramInt2 - 1)] = 0;
          arrayOfByte[paramInt2] = 0;
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
        }
        for (;;)
        {
          paramInt2 += 8;
          if (paramInt2 > 52) {
            break;
          }
          arrayOfByte[(paramInt2 - 4)] = 0;
          arrayOfByte[(paramInt2 - 3)] = 0;
          arrayOfByte[(paramInt2 - 2)] = 0;
          arrayOfByte[(paramInt2 - 1)] = 0;
          arrayOfByte[paramInt2] = 0;
          arrayOfByte[(paramInt2 + 1)] = 0;
          arrayOfByte[(paramInt2 + 2)] = 0;
          arrayOfByte[(paramInt2 + 3)] = 0;
        }
        paramInt2 = (int)bytes << 3;
        arrayOfByte[56] = ((byte)paramInt2);
        arrayOfByte[57] = ((byte)(paramInt2 >>> 8));
        arrayOfByte[58] = ((byte)(paramInt2 >>> 16));
        arrayOfByte[59] = ((byte)(paramInt2 >>> 24));
        paramInt2 = (int)(bytes >>> 29);
        arrayOfByte[60] = ((byte)paramInt2);
        arrayOfByte[61] = ((byte)(paramInt2 >>> 8));
        arrayOfByte[62] = ((byte)(paramInt2 >>> 16));
        arrayOfByte[63] = ((byte)(paramInt2 >>> 24));
        engineUpdate(arrayOfByte, 0);
        paramInt2 = count;
        paramArrayOfByte[paramInt1] = ((byte)paramInt2);
        paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >>> 8));
        paramArrayOfByte[(paramInt1 + 2)] = ((byte)(paramInt2 >>> 16));
        paramArrayOfByte[(paramInt1 + 3)] = ((byte)(paramInt2 >>> 24));
        paramInt2 = digest;
        paramArrayOfByte[(paramInt1 + 4)] = ((byte)paramInt2);
        paramArrayOfByte[(paramInt1 + 5)] = ((byte)(paramInt2 >>> 8));
        paramInt1 += 10;
        paramArrayOfByte[(paramInt1 - 4)] = ((byte)(paramInt2 >>> 16));
        paramArrayOfByte[(paramInt1 - 3)] = ((byte)(paramInt2 >>> 24));
        paramInt2 = cipher;
        paramArrayOfByte[(paramInt1 - 2)] = ((byte)paramInt2);
        paramArrayOfByte[(paramInt1 - 1)] = ((byte)(paramInt2 >>> 8));
        paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >>> 16));
        paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >>> 24));
        paramInt2 = counter;
        paramArrayOfByte[(paramInt1 + 2)] = ((byte)paramInt2);
        paramArrayOfByte[(paramInt1 + 3)] = ((byte)(paramInt2 >>> 8));
        paramArrayOfByte[(paramInt1 + 4)] = ((byte)(paramInt2 >>> 16));
        paramArrayOfByte[(paramInt1 + 5)] = ((byte)(paramInt2 >>> 24));
        engineReset();
        return 16;
      }
      throw new DigestException("insufficient space in output buffer to store the digest");
    }
    throw new DigestException("partial digests not returned");
  }
  
  public byte[] engineDigest()
  {
    byte[] arrayOfByte = new byte[16];
    try
    {
      engineDigest(arrayOfByte, 0, 16);
      return arrayOfByte;
    }
    catch (DigestException localDigestException)
    {
      for (;;) {}
    }
    return null;
  }
  
  public int engineGetDigestLength()
  {
    return 16;
  }
  
  public void engineReset()
  {
    padded = 0;
    bytes = 0L;
    byte[] arrayOfByte = buffer;
    int i = 60;
    int j;
    do
    {
      arrayOfByte[(i - 4)] = 0;
      arrayOfByte[(i - 3)] = 0;
      arrayOfByte[(i - 2)] = 0;
      arrayOfByte[(i - 1)] = 0;
      arrayOfByte[i] = 0;
      arrayOfByte[(i + 1)] = 0;
      arrayOfByte[(i + 2)] = 0;
      arrayOfByte[(i + 3)] = 0;
      j = i - 8;
      i = j;
    } while (j >= 0);
    init();
  }
  
  public void engineUpdate(byte paramByte)
  {
    bytes += 1L;
    if (padded < 63)
    {
      byte[] arrayOfByte = buffer;
      int i = padded;
      padded = (i + 1);
      arrayOfByte[i] = paramByte;
      return;
    }
    buffer[63] = paramByte;
    engineUpdate(buffer, padded);
    padded = 0;
  }
  
  public void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 + paramInt2 <= paramArrayOfByte.length))
    {
      bytes += paramInt2;
      int j = paramInt1;
      int i = paramInt2;
      if (padded > 0)
      {
        j = paramInt1;
        i = paramInt2;
        if (padded + paramInt2 >= 64)
        {
          byte[] arrayOfByte = buffer;
          j = padded;
          i = 64 - padded;
          System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, j, i);
          arrayOfByte = buffer;
          padded = 0;
          engineUpdate(arrayOfByte, 0);
          j = paramInt1 + i;
          i = paramInt2 - i;
        }
      }
      for (;;)
      {
        paramInt1 = j;
        paramInt2 = i;
        if (i < 512) {
          break;
        }
        engineUpdate(paramArrayOfByte, j);
        engineUpdate(paramArrayOfByte, j + 64);
        engineUpdate(paramArrayOfByte, j + 128);
        engineUpdate(paramArrayOfByte, j + 192);
        engineUpdate(paramArrayOfByte, j + 256);
        engineUpdate(paramArrayOfByte, j + 320);
        engineUpdate(paramArrayOfByte, j + 384);
        engineUpdate(paramArrayOfByte, j + 448);
        j += 512;
        i -= 512;
      }
      while (paramInt2 >= 64)
      {
        engineUpdate(paramArrayOfByte, paramInt1);
        paramInt1 += 64;
        paramInt2 -= 64;
      }
      if (paramInt2 > 0)
      {
        System.arraycopy(paramArrayOfByte, paramInt1, buffer, padded, paramInt2);
        padded += paramInt2;
      }
    }
    else
    {
      throw new ArrayIndexOutOfBoundsException(paramInt1);
    }
  }
  
  protected void init()
  {
    count = 1732584193;
    digest = -271733879;
    cipher = -1732584194;
    counter = 271733878;
  }
}
