package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.Arrays;

public abstract interface TrackOutput
{
  public abstract void format(Format paramFormat);
  
  public abstract int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException;
  
  public abstract void sampleData(ParsableByteArray paramParsableByteArray, int paramInt);
  
  public abstract void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, CryptoData paramCryptoData);
  
  public static final class CryptoData
  {
    public final int clearBlocks;
    public final int cryptoMode;
    public final int encryptedBlocks;
    public final byte[] encryptionKey;
    
    public CryptoData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    {
      cryptoMode = paramInt1;
      encryptionKey = paramArrayOfByte;
      encryptedBlocks = paramInt2;
      clearBlocks = paramInt3;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject != null)
      {
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        paramObject = (CryptoData)paramObject;
        return (cryptoMode == cryptoMode) && (encryptedBlocks == encryptedBlocks) && (clearBlocks == clearBlocks) && (Arrays.equals(encryptionKey, encryptionKey));
      }
      return false;
    }
    
    public int hashCode()
    {
      return ((cryptoMode * 31 + Arrays.hashCode(encryptionKey)) * 31 + encryptedBlocks) * 31 + clearBlocks;
    }
  }
}
