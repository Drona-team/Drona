package com.google.android.exoplayer2.decoder;

import android.annotation.TargetApi;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCodec.CryptoInfo.Pattern;
import com.google.android.exoplayer2.util.Util;

public final class CryptoInfo
{
  public int clearBlocks;
  public int encryptedBlocks;
  private final MediaCodec.CryptoInfo frameworkCryptoInfo;
  public byte[] iv;
  public byte[] key;
  public int mode;
  public int[] numBytesOfClearData;
  public int[] numBytesOfEncryptedData;
  public int numSubSamples;
  private final PatternHolderV24 patternHolder;
  
  public CryptoInfo()
  {
    Object localObject;
    if (Util.SDK_INT >= 16) {
      localObject = newFrameworkCryptoInfoV16();
    } else {
      localObject = null;
    }
    frameworkCryptoInfo = ((MediaCodec.CryptoInfo)localObject);
    if (Util.SDK_INT >= 24) {
      localObject = new PatternHolderV24(frameworkCryptoInfo, null);
    } else {
      localObject = null;
    }
    patternHolder = ((PatternHolderV24)localObject);
  }
  
  private MediaCodec.CryptoInfo newFrameworkCryptoInfoV16()
  {
    return new MediaCodec.CryptoInfo();
  }
  
  private void updateFrameworkCryptoInfoV16()
  {
    frameworkCryptoInfo.numSubSamples = numSubSamples;
    frameworkCryptoInfo.numBytesOfClearData = numBytesOfClearData;
    frameworkCryptoInfo.numBytesOfEncryptedData = numBytesOfEncryptedData;
    frameworkCryptoInfo.key = key;
    frameworkCryptoInfo.iv = iv;
    frameworkCryptoInfo.mode = mode;
    if (Util.SDK_INT >= 24) {
      patternHolder.start(encryptedBlocks, clearBlocks);
    }
  }
  
  public MediaCodec.CryptoInfo getFrameworkCryptoInfoV16()
  {
    return frameworkCryptoInfo;
  }
  
  public void set(int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4)
  {
    numSubSamples = paramInt1;
    numBytesOfClearData = paramArrayOfInt1;
    numBytesOfEncryptedData = paramArrayOfInt2;
    key = paramArrayOfByte1;
    iv = paramArrayOfByte2;
    mode = paramInt2;
    encryptedBlocks = paramInt3;
    clearBlocks = paramInt4;
    if (Util.SDK_INT >= 16) {
      updateFrameworkCryptoInfoV16();
    }
  }
  
  @TargetApi(24)
  private static final class PatternHolderV24
  {
    private final MediaCodec.CryptoInfo frameworkCryptoInfo;
    private final MediaCodec.CryptoInfo.Pattern pattern;
    
    private PatternHolderV24(MediaCodec.CryptoInfo paramCryptoInfo)
    {
      frameworkCryptoInfo = paramCryptoInfo;
      pattern = new MediaCodec.CryptoInfo.Pattern(0, 0);
    }
    
    private void start(int paramInt1, int paramInt2)
    {
      pattern.set(paramInt1, paramInt2);
      frameworkCryptoInfo.setPattern(pattern);
    }
  }
}
