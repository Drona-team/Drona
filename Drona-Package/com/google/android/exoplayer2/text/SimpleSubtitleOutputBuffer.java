package com.google.android.exoplayer2.text;

final class SimpleSubtitleOutputBuffer
  extends SubtitleOutputBuffer
{
  private final SimpleSubtitleDecoder owner;
  
  public SimpleSubtitleOutputBuffer(SimpleSubtitleDecoder paramSimpleSubtitleDecoder)
  {
    owner = paramSimpleSubtitleDecoder;
  }
  
  public final void release()
  {
    owner.releaseOutputBuffer(this);
  }
}
