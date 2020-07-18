package com.google.android.exoplayer2.upgrade;

public class DecryptionException
  extends Exception
{
  public final int errorCode;
  
  public DecryptionException(int paramInt, String paramString)
  {
    super(paramString);
    errorCode = paramInt;
  }
}
