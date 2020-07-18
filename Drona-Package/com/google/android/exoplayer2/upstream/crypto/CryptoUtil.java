package com.google.android.exoplayer2.upstream.crypto;

final class CryptoUtil
{
  private CryptoUtil() {}
  
  public static long getFNV64Hash(String paramString)
  {
    long l = 0L;
    if (paramString == null) {
      return 0L;
    }
    int i = 0;
    while (i < paramString.length())
    {
      l ^= paramString.charAt(i);
      l += (l << 1) + (l << 4) + (l << 5) + (l << 7) + (l << 8) + (l << 40);
      i += 1;
    }
    return l;
  }
}
