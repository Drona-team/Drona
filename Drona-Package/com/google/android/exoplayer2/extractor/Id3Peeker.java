package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.configurations.Id3Decoder;
import com.google.android.exoplayer2.metadata.configurations.Id3Decoder.FramePredicate;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;

public final class Id3Peeker
{
  private final ParsableByteArray scratch = new ParsableByteArray(10);
  
  public Id3Peeker() {}
  
  public Metadata peekId3Data(ExtractorInput paramExtractorInput, Id3Decoder.FramePredicate paramFramePredicate)
    throws IOException, InterruptedException
  {
    Object localObject = null;
    int i = 0;
    for (;;)
    {
      byte[] arrayOfByte = scratch.data;
      try
      {
        paramExtractorInput.peekFully(arrayOfByte, 0, 10);
        scratch.setPosition(0);
        if (scratch.readUnsignedInt24() == Id3Decoder.ID3_TAG)
        {
          scratch.skipBytes(3);
          int j = scratch.readSynchSafeInt();
          int k = j + 10;
          if (localObject == null)
          {
            localObject = new byte[k];
            System.arraycopy(scratch.data, 0, localObject, 0, 10);
            paramExtractorInput.peekFully((byte[])localObject, 10, j);
            localObject = new Id3Decoder(paramFramePredicate).decode((byte[])localObject, k);
          }
          else
          {
            paramExtractorInput.advancePeekPosition(j);
          }
          i += k;
        }
      }
      catch (EOFException paramFramePredicate)
      {
        for (;;) {}
      }
    }
    paramExtractorInput.resetPeekPosition();
    paramExtractorInput.advancePeekPosition(i);
    return localObject;
  }
}
