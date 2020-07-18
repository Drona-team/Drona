package com.google.android.exoplayer2.extractor;

public final class DummyExtractorOutput
  implements ExtractorOutput
{
  public DummyExtractorOutput() {}
  
  public void endTracks() {}
  
  public void seekMap(SeekMap paramSeekMap) {}
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    return new DummyTrackOutput();
  }
}
