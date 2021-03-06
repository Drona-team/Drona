package com.google.android.exoplayer2.extractor.ClickListeners;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;

public final class TrackEncryptionBox
{
  private static final String TYPE = "TrackEncryptionBox";
  public final TrackOutput.CryptoData cryptoData;
  public final byte[] defaultInitializationVector;
  public final boolean isEncrypted;
  public final int perSampleIvSize;
  @Nullable
  public final String schemeType;
  
  public TrackEncryptionBox(boolean paramBoolean, String paramString, int paramInt1, byte[] paramArrayOfByte1, int paramInt2, int paramInt3, byte[] paramArrayOfByte2)
  {
    int j = 0;
    int i;
    if (paramInt1 == 0) {
      i = 1;
    } else {
      i = 0;
    }
    if (paramArrayOfByte2 == null) {
      j = 1;
    }
    Assertions.checkArgument(j ^ i);
    isEncrypted = paramBoolean;
    schemeType = paramString;
    perSampleIvSize = paramInt1;
    defaultInitializationVector = paramArrayOfByte2;
    cryptoData = new TrackOutput.CryptoData(schemeToCryptoMode(paramString), paramArrayOfByte1, paramInt2, paramInt3);
  }
  
  private static int schemeToCryptoMode(String paramString)
  {
    if (paramString == null) {
      return 1;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != 3046605)
    {
      if (j != 3046671)
      {
        if (j != 3049879)
        {
          if ((j == 3049895) && (paramString.equals("cens"))) {
            i = 1;
          }
        }
        else if (paramString.equals("cenc")) {
          i = 0;
        }
      }
      else if (paramString.equals("cbcs")) {
        i = 3;
      }
    }
    else if (paramString.equals("cbc1")) {
      i = 2;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unsupported protection scheme type '");
      localStringBuilder.append(paramString);
      localStringBuilder.append("'. Assuming AES-CTR crypto mode.");
      Log.w("TrackEncryptionBox", localStringBuilder.toString());
      return 1;
    case 2: 
    case 3: 
      return 2;
    }
    return 1;
  }
}
