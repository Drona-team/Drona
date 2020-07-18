package com.google.android.exoplayer2.text.ClickListeners;

import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

abstract class CeaDecoder
  implements SubtitleDecoder
{
  private static final int NUM_INPUT_BUFFERS = 10;
  private static final int NUM_OUTPUT_BUFFERS = 2;
  private final ArrayDeque<com.google.android.exoplayer2.text.cea.CeaDecoder.CeaInputBuffer> availableInputBuffers = new ArrayDeque();
  private final ArrayDeque<SubtitleOutputBuffer> availableOutputBuffers;
  private CeaInputBuffer dequeuedInputBuffer;
  private long playbackPositionUs;
  private long queuedInputBufferCount;
  private final PriorityQueue<com.google.android.exoplayer2.text.cea.CeaDecoder.CeaInputBuffer> queuedInputBuffers;
  
  public CeaDecoder()
  {
    int j = 0;
    int i = 0;
    while (i < 10)
    {
      availableInputBuffers.add(new CeaInputBuffer(null));
      i += 1;
    }
    availableOutputBuffers = new ArrayDeque();
    i = j;
    while (i < 2)
    {
      availableOutputBuffers.add(new CeaOutputBuffer(null));
      i += 1;
    }
    queuedInputBuffers = new PriorityQueue();
  }
  
  private void releaseInputBuffer(CeaInputBuffer paramCeaInputBuffer)
  {
    paramCeaInputBuffer.clear();
    availableInputBuffers.add(paramCeaInputBuffer);
  }
  
  protected abstract Subtitle createSubtitle();
  
  protected abstract void decode(SubtitleInputBuffer paramSubtitleInputBuffer);
  
  public SubtitleInputBuffer dequeueInputBuffer()
    throws SubtitleDecoderException
  {
    boolean bool;
    if (dequeuedInputBuffer == null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    if (availableInputBuffers.isEmpty()) {
      return null;
    }
    dequeuedInputBuffer = ((CeaInputBuffer)availableInputBuffers.pollFirst());
    return dequeuedInputBuffer;
  }
  
  public SubtitleOutputBuffer dequeueOutputBuffer()
    throws SubtitleDecoderException
  {
    if (availableOutputBuffers.isEmpty()) {
      return null;
    }
    while ((!queuedInputBuffers.isEmpty()) && (queuedInputBuffers.peek()).timeUs <= playbackPositionUs))
    {
      CeaInputBuffer localCeaInputBuffer = (CeaInputBuffer)queuedInputBuffers.poll();
      Object localObject;
      if (localCeaInputBuffer.isEndOfStream())
      {
        localObject = (SubtitleOutputBuffer)availableOutputBuffers.pollFirst();
        ((Buffer)localObject).addFlag(4);
        releaseInputBuffer(localCeaInputBuffer);
        return localObject;
      }
      decode(localCeaInputBuffer);
      if (isNewSubtitleDataAvailable())
      {
        localObject = createSubtitle();
        if (!localCeaInputBuffer.isDecodeOnly())
        {
          SubtitleOutputBuffer localSubtitleOutputBuffer = (SubtitleOutputBuffer)availableOutputBuffers.pollFirst();
          localSubtitleOutputBuffer.setContent(timeUs, (Subtitle)localObject, Long.MAX_VALUE);
          releaseInputBuffer(localCeaInputBuffer);
          return localSubtitleOutputBuffer;
        }
      }
      releaseInputBuffer(localCeaInputBuffer);
    }
    return null;
  }
  
  public void flush()
  {
    queuedInputBufferCount = 0L;
    playbackPositionUs = 0L;
    while (!queuedInputBuffers.isEmpty()) {
      releaseInputBuffer((CeaInputBuffer)queuedInputBuffers.poll());
    }
    if (dequeuedInputBuffer != null)
    {
      releaseInputBuffer(dequeuedInputBuffer);
      dequeuedInputBuffer = null;
    }
  }
  
  public abstract String getName();
  
  protected abstract boolean isNewSubtitleDataAvailable();
  
  public void queueInputBuffer(SubtitleInputBuffer paramSubtitleInputBuffer)
    throws SubtitleDecoderException
  {
    boolean bool;
    if (paramSubtitleInputBuffer == dequeuedInputBuffer) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if (paramSubtitleInputBuffer.isDecodeOnly())
    {
      releaseInputBuffer(dequeuedInputBuffer);
    }
    else
    {
      paramSubtitleInputBuffer = dequeuedInputBuffer;
      long l = queuedInputBufferCount;
      queuedInputBufferCount = (1L + l);
      CeaInputBuffer.access$202(paramSubtitleInputBuffer, l);
      queuedInputBuffers.add(dequeuedInputBuffer);
    }
    dequeuedInputBuffer = null;
  }
  
  public void release() {}
  
  protected void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    paramSubtitleOutputBuffer.clear();
    availableOutputBuffers.add(paramSubtitleOutputBuffer);
  }
  
  public void setPositionUs(long paramLong)
  {
    playbackPositionUs = paramLong;
  }
  
  final class CeaInputBuffer
    extends SubtitleInputBuffer
    implements Comparable<com.google.android.exoplayer2.text.cea.CeaDecoder.CeaInputBuffer>
  {
    private long queuedInputBufferCount;
    
    private CeaInputBuffer() {}
    
    public int compareTo(CeaInputBuffer paramCeaInputBuffer)
    {
      if (isEndOfStream() != paramCeaInputBuffer.isEndOfStream())
      {
        if (isEndOfStream()) {
          return 1;
        }
      }
      else
      {
        long l2 = timeUs - timeUs;
        long l1 = l2;
        if (l2 == 0L)
        {
          l2 = queuedInputBufferCount - queuedInputBufferCount;
          l1 = l2;
          if (l2 == 0L) {
            return 0;
          }
        }
        if (l1 > 0L) {
          return 1;
        }
      }
      return -1;
    }
  }
  
  final class CeaOutputBuffer
    extends SubtitleOutputBuffer
  {
    private CeaOutputBuffer() {}
    
    public final void release()
    {
      releaseOutputBuffer(this);
    }
  }
}
