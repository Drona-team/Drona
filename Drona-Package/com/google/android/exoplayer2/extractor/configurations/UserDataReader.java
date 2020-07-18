package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.text.ClickListeners.CeaUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.List;

final class UserDataReader
{
  private static final int USER_DATA_START_CODE = 434;
  private final List<Format> closedCaptionFormats;
  private final TrackOutput[] outputs;
  
  public UserDataReader(List paramList)
  {
    closedCaptionFormats = paramList;
    outputs = new TrackOutput[paramList.size()];
  }
  
  public void consume(long paramLong, ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() < 9) {
      return;
    }
    int i = paramParsableByteArray.readInt();
    int j = paramParsableByteArray.readInt();
    int k = paramParsableByteArray.readUnsignedByte();
    if ((i == 434) && (j == CeaUtil.USER_DATA_IDENTIFIER_GA94) && (k == 3)) {
      CeaUtil.consumeCcData(paramLong, paramParsableByteArray, outputs);
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    int i = 0;
    while (i < outputs.length)
    {
      paramTrackIdGenerator.generateNewId();
      TrackOutput localTrackOutput = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 3);
      Format localFormat = (Format)closedCaptionFormats.get(i);
      String str = sampleMimeType;
      boolean bool;
      if ((!"application/cea-608".equals(str)) && (!"application/cea-708".equals(str))) {
        bool = false;
      } else {
        bool = true;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid closed caption mime type provided: ");
      localStringBuilder.append(str);
      Assertions.checkArgument(bool, localStringBuilder.toString());
      localTrackOutput.format(Format.createTextSampleFormat(paramTrackIdGenerator.getFormatId(), str, null, -1, selectionFlags, language, accessibilityChannel, null, Long.MAX_VALUE, initializationData));
      outputs[i] = localTrackOutput;
      i += 1;
    }
  }
}
