package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.text.ClickListeners.CeaUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.List;

final class SeiReader
{
  private final List<Format> closedCaptionFormats;
  private final TrackOutput[] outputs;
  
  public SeiReader(List paramList)
  {
    closedCaptionFormats = paramList;
    outputs = new TrackOutput[paramList.size()];
  }
  
  public void consume(long paramLong, ParsableByteArray paramParsableByteArray)
  {
    CeaUtil.consume(paramLong, paramParsableByteArray, outputs);
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
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Invalid closed caption mime type provided: ");
      ((StringBuilder)localObject).append(str);
      Assertions.checkArgument(bool, ((StringBuilder)localObject).toString());
      if (mimeType != null) {
        localObject = mimeType;
      } else {
        localObject = paramTrackIdGenerator.getFormatId();
      }
      localTrackOutput.format(Format.createTextSampleFormat((String)localObject, str, null, -1, selectionFlags, language, accessibilityChannel, null, Long.MAX_VALUE, initializationData));
      outputs[i] = localTrackOutput;
      i += 1;
    }
  }
}
