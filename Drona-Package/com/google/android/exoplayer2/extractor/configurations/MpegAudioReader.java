package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class MpegAudioReader
  implements ElementaryStreamReader
{
  private static final int HEADER_SIZE = 4;
  private static final int STATE_FINDING_HEADER = 0;
  private static final int STATE_READING_FRAME = 2;
  private static final int STATE_READING_HEADER = 1;
  private String formatId;
  private int frameBytesRead;
  private long frameDurationUs;
  private int frameSize;
  private boolean hasOutputFormat;
  private final MpegAudioHeader header;
  private final ParsableByteArray headerScratch = new ParsableByteArray(4);
  private final String language;
  private boolean lastByteWasFF;
  private TrackOutput output;
  private int state = 0;
  private long timeUs;
  
  public MpegAudioReader()
  {
    this(null);
  }
  
  public MpegAudioReader(String paramString)
  {
    headerScratch.data[0] = -1;
    header = new MpegAudioHeader();
    language = paramString;
  }
  
  private void findHeader(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = data;
    int i = paramParsableByteArray.getPosition();
    int k = paramParsableByteArray.limit();
    while (i < k)
    {
      boolean bool;
      if ((arrayOfByte[i] & 0xFF) == 255) {
        bool = true;
      } else {
        bool = false;
      }
      int j;
      if ((lastByteWasFF) && ((arrayOfByte[i] & 0xE0) == 224)) {
        j = 1;
      } else {
        j = 0;
      }
      lastByteWasFF = bool;
      if (j != 0)
      {
        paramParsableByteArray.setPosition(i + 1);
        lastByteWasFF = false;
        headerScratch.data[1] = arrayOfByte[i];
        frameBytesRead = 2;
        state = 1;
        return;
      }
      i += 1;
    }
    paramParsableByteArray.setPosition(k);
  }
  
  private void readFrameRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), frameSize - frameBytesRead);
    output.sampleData(paramParsableByteArray, i);
    frameBytesRead += i;
    if (frameBytesRead < frameSize) {
      return;
    }
    output.sampleMetadata(timeUs, 1, frameSize, 0, null);
    timeUs += frameDurationUs;
    frameBytesRead = 0;
    state = 0;
  }
  
  private void readHeaderRemainder(ParsableByteArray paramParsableByteArray)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), 4 - frameBytesRead);
    paramParsableByteArray.readBytes(headerScratch.data, frameBytesRead, i);
    frameBytesRead += i;
    if (frameBytesRead < 4) {
      return;
    }
    headerScratch.setPosition(0);
    if (!MpegAudioHeader.populateHeader(headerScratch.readInt(), header))
    {
      frameBytesRead = 0;
      state = 1;
      return;
    }
    frameSize = header.frameSize;
    if (!hasOutputFormat)
    {
      frameDurationUs = (header.samplesPerFrame * 1000000L / header.sampleRate);
      paramParsableByteArray = Format.createAudioSampleFormat(formatId, header.mimeType, null, -1, 4096, header.channels, header.sampleRate, null, null, 0, language);
      output.format(paramParsableByteArray);
      hasOutputFormat = true;
    }
    headerScratch.setPosition(0);
    output.sampleData(headerScratch, 4);
    state = 2;
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0) {
      switch (state)
      {
      default: 
        throw new IllegalStateException();
      case 2: 
        readFrameRemainder(paramParsableByteArray);
        break;
      case 1: 
        readHeaderRemainder(paramParsableByteArray);
        break;
      case 0: 
        findHeader(paramParsableByteArray);
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    formatId = paramTrackIdGenerator.getFormatId();
    output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 1);
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    timeUs = paramLong;
  }
  
  public void seek()
  {
    state = 0;
    frameBytesRead = 0;
    lastByteWasFF = false;
  }
}
