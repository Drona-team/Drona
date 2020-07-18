package com.google.android.exoplayer2.source.configurations;

import android.net.Uri;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract interface HlsExtractorFactory
{
  public static final HlsExtractorFactory DEFAULT = new DefaultHlsExtractorFactory();
  
  public abstract Pair createExtractor(Extractor paramExtractor, Uri paramUri, Format paramFormat, List paramList, DrmInitData paramDrmInitData, TimestampAdjuster paramTimestampAdjuster, Map paramMap, ExtractorInput paramExtractorInput)
    throws InterruptedException, IOException;
}
