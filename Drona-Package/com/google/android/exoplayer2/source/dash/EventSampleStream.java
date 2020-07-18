package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.metadata.emsg.EventMessageEncoder;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;

final class EventSampleStream
  implements SampleStream
{
  private int currentIndex;
  private final EventMessageEncoder eventMessageEncoder;
  private EventStream eventStream;
  private boolean eventStreamAppendable;
  private long[] eventTimesUs;
  private boolean isFormatSentDownstream;
  private long pendingSeekPositionUs;
  private final Format upstreamFormat;
  
  public EventSampleStream(EventStream paramEventStream, Format paramFormat, boolean paramBoolean)
  {
    upstreamFormat = paramFormat;
    eventStream = paramEventStream;
    eventMessageEncoder = new EventMessageEncoder();
    pendingSeekPositionUs = -9223372036854775807L;
    eventTimesUs = presentationTimesUs;
    updateEventStream(paramEventStream, paramBoolean);
  }
  
  public String eventStreamId()
  {
    return eventStream.resolve();
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  public void maybeThrowError()
    throws IOException
  {}
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    if ((!paramBoolean) && (isFormatSentDownstream))
    {
      if (currentIndex == eventTimesUs.length)
      {
        if (!eventStreamAppendable)
        {
          paramDecoderInputBuffer.setFlags(4);
          return -4;
        }
        return -3;
      }
      int i = currentIndex;
      currentIndex = (i + 1);
      paramFormatHolder = eventMessageEncoder.encode(eventStream.events[i], eventStream.timescale);
      if (paramFormatHolder != null)
      {
        paramDecoderInputBuffer.ensureSpaceForWrite(paramFormatHolder.length);
        paramDecoderInputBuffer.setFlags(1);
        data.put(paramFormatHolder);
        timeUs = eventTimesUs[i];
        return -4;
      }
      return -3;
    }
    format = upstreamFormat;
    isFormatSentDownstream = true;
    return -5;
  }
  
  public void seekToUs(long paramLong)
  {
    long[] arrayOfLong = eventTimesUs;
    int j = 0;
    currentIndex = Util.binarySearchCeil(arrayOfLong, paramLong, true, false);
    int i = j;
    if (eventStreamAppendable)
    {
      i = j;
      if (currentIndex == eventTimesUs.length) {
        i = 1;
      }
    }
    if (i == 0) {
      paramLong = -9223372036854775807L;
    }
    pendingSeekPositionUs = paramLong;
  }
  
  public int skipData(long paramLong)
  {
    int i = Math.max(currentIndex, Util.binarySearchCeil(eventTimesUs, paramLong, true, false));
    int j = currentIndex;
    currentIndex = i;
    return i - j;
  }
  
  public void updateEventStream(EventStream paramEventStream, boolean paramBoolean)
  {
    long l;
    if (currentIndex == 0) {
      l = -9223372036854775807L;
    } else {
      l = eventTimesUs[(currentIndex - 1)];
    }
    eventStreamAppendable = paramBoolean;
    eventStream = paramEventStream;
    eventTimesUs = presentationTimesUs;
    if (pendingSeekPositionUs != -9223372036854775807L)
    {
      seekToUs(pendingSeekPositionUs);
      return;
    }
    if (l != -9223372036854775807L) {
      currentIndex = Util.binarySearchCeil(eventTimesUs, l, false, false);
    }
  }
}
