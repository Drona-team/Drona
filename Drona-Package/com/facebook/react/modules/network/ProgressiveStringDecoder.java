package com.facebook.react.modules.network;

import com.facebook.common.logging.FLog;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ProgressiveStringDecoder
{
  private static final String EMPTY_STRING = "";
  private final CharsetDecoder mDecoder;
  private byte[] remainder = null;
  
  public ProgressiveStringDecoder(Charset paramCharset)
  {
    mDecoder = paramCharset.newDecoder();
  }
  
  public String decodeNext(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt;
    if (remainder != null)
    {
      arrayOfByte = new byte[remainder.length + paramInt];
      System.arraycopy(remainder, 0, arrayOfByte, 0, remainder.length);
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, remainder.length, paramInt);
      i = paramInt + remainder.length;
    }
    int k = 1;
    ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte, 0, i);
    paramArrayOfByte = null;
    paramInt = 0;
    int j = 0;
    while ((paramInt == 0) && (j < 4))
    {
      Object localObject = mDecoder;
      try
      {
        localObject = ((CharsetDecoder)localObject).decode(localByteBuffer);
        paramInt = 1;
        paramArrayOfByte = (byte[])localObject;
      }
      catch (CharacterCodingException localCharacterCodingException)
      {
        for (;;) {}
      }
      j += 1;
      localByteBuffer = ByteBuffer.wrap(arrayOfByte, 0, i - j);
    }
    if ((paramInt == 0) || (j <= 0)) {
      k = 0;
    }
    if (k != 0)
    {
      remainder = new byte[j];
      System.arraycopy(arrayOfByte, i - j, remainder, 0, j);
    }
    else
    {
      remainder = null;
    }
    if (paramInt == 0)
    {
      FLog.warn("ReactNative", "failed to decode string from byte array");
      return "";
    }
    return new String(paramArrayOfByte.array(), 0, paramArrayOfByte.length());
  }
}
