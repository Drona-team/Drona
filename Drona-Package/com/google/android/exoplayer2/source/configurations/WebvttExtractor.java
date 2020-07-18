package com.google.android.exoplayer2.source.configurations;

import android.text.TextUtils;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.text.webvtt.WebvttParserUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttExtractor
  implements Extractor
{
  private static final int HEADER_MAX_LENGTH = 9;
  private static final int HEADER_MIN_LENGTH = 6;
  private static final Pattern LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
  private static final Pattern MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
  private final String language;
  private ExtractorOutput output;
  private byte[] sampleData;
  private final ParsableByteArray sampleDataWrapper;
  private int sampleSize;
  private final TimestampAdjuster timestampAdjuster;
  
  public WebvttExtractor(String paramString, TimestampAdjuster paramTimestampAdjuster)
  {
    language = paramString;
    timestampAdjuster = paramTimestampAdjuster;
    sampleDataWrapper = new ParsableByteArray();
    sampleData = new byte['?'];
  }
  
  private TrackOutput buildTrackOutput(long paramLong)
  {
    TrackOutput localTrackOutput = output.track(0, 3);
    localTrackOutput.format(Format.createTextSampleFormat(null, "text/vtt", null, -1, 0, language, null, paramLong));
    output.endTracks();
    return localTrackOutput;
  }
  
  private void processSample()
    throws ParserException
  {
    Object localObject2 = new ParsableByteArray(sampleData);
    WebvttParserUtil.validateWebvttHeaderLine((ParsableByteArray)localObject2);
    long l1 = 0L;
    long l2 = 0L;
    for (;;)
    {
      localObject1 = ((ParsableByteArray)localObject2).readLine();
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        break label185;
      }
      if (((String)localObject1).startsWith("X-TIMESTAMP-MAP"))
      {
        Matcher localMatcher1 = LOCAL_TIMESTAMP.matcher((CharSequence)localObject1);
        if (!localMatcher1.find()) {
          break label147;
        }
        Matcher localMatcher2 = MEDIA_TIMESTAMP.matcher((CharSequence)localObject1);
        if (!localMatcher2.find()) {
          break;
        }
        l2 = WebvttParserUtil.parseTimestampUs(localMatcher1.group(1));
        l1 = TimestampAdjuster.ptsToUs(Long.parseLong(localMatcher2.group(1)));
      }
    }
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("X-TIMESTAMP-MAP doesn't contain media timestamp: ");
    ((StringBuilder)localObject2).append((String)localObject1);
    throw new ParserException(((StringBuilder)localObject2).toString());
    label147:
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("X-TIMESTAMP-MAP doesn't contain local timestamp: ");
    ((StringBuilder)localObject2).append((String)localObject1);
    throw new ParserException(((StringBuilder)localObject2).toString());
    label185:
    Object localObject1 = WebvttParserUtil.findNextCueHeader((ParsableByteArray)localObject2);
    if (localObject1 == null)
    {
      buildTrackOutput(0L);
      return;
    }
    long l3 = WebvttParserUtil.parseTimestampUs(((Matcher)localObject1).group(1));
    l1 = timestampAdjuster.adjustTsTimestamp(TimestampAdjuster.usToPts(l1 + l3 - l2));
    localObject1 = buildTrackOutput(l1 - l3);
    sampleDataWrapper.reset(sampleData, sampleSize);
    ((TrackOutput)localObject1).sampleData(sampleDataWrapper, sampleSize);
    ((TrackOutput)localObject1).sampleMetadata(l1, 1, sampleSize, 0, null);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    output = paramExtractorOutput;
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int j = (int)paramExtractorInput.getLength();
    if (sampleSize == sampleData.length)
    {
      paramPositionHolder = sampleData;
      if (j != -1) {
        i = j;
      } else {
        i = sampleData.length;
      }
      sampleData = Arrays.copyOf(paramPositionHolder, i * 3 / 2);
    }
    int i = paramExtractorInput.read(sampleData, sampleSize, sampleData.length - sampleSize);
    if (i != -1)
    {
      sampleSize += i;
      if ((j == -1) || (sampleSize != j)) {
        return 0;
      }
    }
    processSample();
    return -1;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    throw new IllegalStateException();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.peekFully(sampleData, 0, 6, false);
    sampleDataWrapper.reset(sampleData, 6);
    if (WebvttParserUtil.isWebvttHeaderLine(sampleDataWrapper)) {
      return true;
    }
    paramExtractorInput.peekFully(sampleData, 6, 3, false);
    sampleDataWrapper.reset(sampleData, 9);
    return WebvttParserUtil.isWebvttHeaderLine(sampleDataWrapper);
  }
}
