package com.google.android.gms.common.util;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.ShowFirstParty;

@KeepForSdk
@ShowFirstParty
public class IpAddress
{
  private static final char[] zzgy = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  private static final char[] zzgz = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  
  public IpAddress() {}
  
  public static String bytesToStringLowercase(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar = new char[paramArrayOfByte.length << 1];
    int i = 0;
    int j = 0;
    while (i < paramArrayOfByte.length)
    {
      int k = paramArrayOfByte[i] & 0xFF;
      int m = j + 1;
      arrayOfChar[j] = zzgz[(k >>> 4)];
      j = m + 1;
      arrayOfChar[m] = zzgz[(k & 0xF)];
      i += 1;
    }
    return new String(arrayOfChar);
  }
  
  public static String bytesToStringUppercase(byte[] paramArrayOfByte)
  {
    return bytesToStringUppercase(paramArrayOfByte, false);
  }
  
  public static String bytesToStringUppercase(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    int j = paramArrayOfByte.length;
    StringBuilder localStringBuilder = new StringBuilder(j << 1);
    int i = 0;
    while ((i < j) && ((!paramBoolean) || (i != j - 1) || ((paramArrayOfByte[i] & 0xFF) != 0)))
    {
      localStringBuilder.append(zzgy[((paramArrayOfByte[i] & 0xF0) >>> 4)]);
      localStringBuilder.append(zzgy[(paramArrayOfByte[i] & 0xF)]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static byte[] stringToBytes(String paramString)
    throws IllegalArgumentException
  {
    int k = paramString.length();
    if (k % 2 == 0)
    {
      byte[] arrayOfByte = new byte[k / 2];
      int j;
      for (int i = 0; i < k; i = j)
      {
        int m = i / 2;
        j = i + 2;
        arrayOfByte[m] = ((byte)Integer.parseInt(paramString.substring(i, j), 16));
      }
      return arrayOfByte;
    }
    throw new IllegalArgumentException("Hex string has odd number of characters");
  }
}
