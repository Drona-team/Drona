package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import java.nio.ByteBuffer;

public abstract class SimpleSubtitleDecoder
  extends SimpleDecoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
  implements SubtitleDecoder
{
  private final String name;
  
  protected SimpleSubtitleDecoder(String paramString)
  {
    super(new SubtitleInputBuffer[2], new SubtitleOutputBuffer[2]);
    name = paramString;
    setInitialInputBufferSize(1024);
  }
  
  protected final SubtitleInputBuffer createInputBuffer()
  {
    return new SubtitleInputBuffer();
  }
  
  protected final SubtitleOutputBuffer createOutputBuffer()
  {
    return new SimpleSubtitleOutputBuffer(this);
  }
  
  protected final SubtitleDecoderException createUnexpectedDecodeException(Throwable paramThrowable)
  {
    return new SubtitleDecoderException("Unexpected decode error", paramThrowable);
  }
  
  protected abstract Subtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException;
  
  protected final SubtitleDecoderException decode(SubtitleInputBuffer paramSubtitleInputBuffer, SubtitleOutputBuffer paramSubtitleOutputBuffer, boolean paramBoolean)
  {
    Object localObject = data;
    try
    {
      localObject = decode(((ByteBuffer)localObject).array(), ((ByteBuffer)localObject).limit(), paramBoolean);
      long l1 = timeUs;
      long l2 = subsampleOffsetUs;
      paramSubtitleOutputBuffer.setContent(l1, (Subtitle)localObject, l2);
      paramSubtitleOutputBuffer.clearFlag(Integer.MIN_VALUE);
      return null;
    }
    catch (SubtitleDecoderException paramSubtitleInputBuffer) {}
    return paramSubtitleInputBuffer;
  }
  
  public final String getName()
  {
    return name;
  }
  
  protected final void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    super.releaseOutputBuffer(paramSubtitleOutputBuffer);
  }
  
  public void setPositionUs(long paramLong) {}
}
