package com.google.android.exoplayer2.source.configurations;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ClickListeners.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.configurations.Ac3Extractor;
import com.google.android.exoplayer2.extractor.configurations.AdtsExtractor;
import com.google.android.exoplayer2.extractor.configurations.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.configurations.TsExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultHlsExtractorFactory
  implements HlsExtractorFactory
{
  public static final String AAC_FILE_EXTENSION = ".aac";
  public static final String AC3_FILE_EXTENSION = ".ac3";
  public static final String CMF_FILE_EXTENSION_PREFIX = ".cmf";
  public static final String EC3_FILE_EXTENSION = ".ec3";
  public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
  public static final String MP3_FILE_EXTENSION = ".mp3";
  public static final String MP4_FILE_EXTENSION = ".mp4";
  public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
  public static final String VTT_FILE_EXTENSION = ".vtt";
  public static final String WEBVTT_FILE_EXTENSION = ".webvtt";
  private final int payloadReaderFactoryFlags;
  
  public DefaultHlsExtractorFactory()
  {
    this(0);
  }
  
  public DefaultHlsExtractorFactory(int paramInt)
  {
    payloadReaderFactoryFlags = paramInt;
  }
  
  private static Pair buildResult(Extractor paramExtractor)
  {
    boolean bool;
    if ((!(paramExtractor instanceof AdtsExtractor)) && (!(paramExtractor instanceof Ac3Extractor)) && (!(paramExtractor instanceof Mp3Extractor))) {
      bool = false;
    } else {
      bool = true;
    }
    return new Pair(paramExtractor, Boolean.valueOf(bool));
  }
  
  private Extractor createExtractorByFileExtension(Uri paramUri, Format paramFormat, List paramList, DrmInitData paramDrmInitData, TimestampAdjuster paramTimestampAdjuster)
  {
    String str = paramUri.getLastPathSegment();
    paramUri = str;
    if (str == null) {
      paramUri = "";
    }
    if ((!"text/vtt".equals(sampleMimeType)) && (!paramUri.endsWith(".webvtt")) && (!paramUri.endsWith(".vtt")))
    {
      if (paramUri.endsWith(".aac")) {
        return new AdtsExtractor();
      }
      if ((!paramUri.endsWith(".ac3")) && (!paramUri.endsWith(".ec3")))
      {
        if (paramUri.endsWith(".mp3")) {
          return new Mp3Extractor(0, 0L);
        }
        if ((!paramUri.endsWith(".mp4")) && (!paramUri.startsWith(".m4", paramUri.length() - 4)) && (!paramUri.startsWith(".mp4", paramUri.length() - 5)) && (!paramUri.startsWith(".cmf", paramUri.length() - 5))) {
          return createTsExtractor(payloadReaderFactoryFlags, paramFormat, paramList, paramTimestampAdjuster);
        }
        if (paramList == null) {
          for (;;)
          {
            paramList = Collections.emptyList();
          }
        }
        return new FragmentedMp4Extractor(0, paramTimestampAdjuster, null, paramDrmInitData, paramList);
      }
      return new Ac3Extractor();
    }
    return new WebvttExtractor(language, paramTimestampAdjuster);
  }
  
  private static TsExtractor createTsExtractor(int paramInt, Format paramFormat, List paramList, TimestampAdjuster paramTimestampAdjuster)
  {
    paramInt |= 0x10;
    if (paramList != null) {
      paramInt |= 0x20;
    } else {
      paramList = Collections.singletonList(Format.createTextSampleFormat(null, "application/cea-608", 0, null));
    }
    paramFormat = codecs;
    int j = paramInt;
    if (!TextUtils.isEmpty(paramFormat))
    {
      int i = paramInt;
      if (!"audio/mp4a-latm".equals(MimeTypes.getAudioMediaMimeType(paramFormat))) {
        i = paramInt | 0x2;
      }
      j = i;
      if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(paramFormat))) {
        j = i | 0x4;
      }
    }
    return new TsExtractor(2, paramTimestampAdjuster, new DefaultTsPayloadReaderFactory(j, paramList));
  }
  
  private static boolean sniffQuietly(Extractor paramExtractor, ExtractorInput paramExtractorInput)
    throws InterruptedException, IOException
  {
    try
    {
      boolean bool = paramExtractor.sniff(paramExtractorInput);
      paramExtractorInput.resetPeekPosition();
      return bool;
    }
    catch (Throwable paramExtractor)
    {
      paramExtractorInput.resetPeekPosition();
      throw paramExtractor;
      paramExtractorInput.resetPeekPosition();
      return false;
    }
    catch (EOFException paramExtractor)
    {
      for (;;) {}
    }
  }
  
  public Pair createExtractor(Extractor paramExtractor, Uri paramUri, Format paramFormat, List paramList, DrmInitData paramDrmInitData, TimestampAdjuster paramTimestampAdjuster, Map paramMap, ExtractorInput paramExtractorInput)
    throws InterruptedException, IOException
  {
    if (paramExtractor != null)
    {
      if ((!(paramExtractor instanceof TsExtractor)) && (!(paramExtractor instanceof FragmentedMp4Extractor)))
      {
        if ((paramExtractor instanceof WebvttExtractor)) {
          return buildResult(new WebvttExtractor(language, paramTimestampAdjuster));
        }
        if ((paramExtractor instanceof AdtsExtractor)) {
          return buildResult(new AdtsExtractor());
        }
        if ((paramExtractor instanceof Ac3Extractor)) {
          return buildResult(new Ac3Extractor());
        }
        if ((paramExtractor instanceof Mp3Extractor)) {
          return buildResult(new Mp3Extractor());
        }
        paramUri = new StringBuilder();
        paramUri.append("Unexpected previousExtractor type: ");
        paramUri.append(paramExtractor.getClass().getSimpleName());
        throw new IllegalArgumentException(paramUri.toString());
      }
      return buildResult(paramExtractor);
    }
    paramUri = createExtractorByFileExtension(paramUri, paramFormat, paramList, paramDrmInitData, paramTimestampAdjuster);
    paramExtractorInput.resetPeekPosition();
    if (sniffQuietly(paramUri, paramExtractorInput)) {
      return buildResult(paramUri);
    }
    if (!(paramUri instanceof WebvttExtractor))
    {
      paramExtractor = new WebvttExtractor(language, paramTimestampAdjuster);
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    if (!(paramUri instanceof AdtsExtractor))
    {
      paramExtractor = new AdtsExtractor();
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    if (!(paramUri instanceof Ac3Extractor))
    {
      paramExtractor = new Ac3Extractor();
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    if (!(paramUri instanceof Mp3Extractor))
    {
      paramExtractor = new Mp3Extractor(0, 0L);
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    if (!(paramUri instanceof FragmentedMp4Extractor))
    {
      if (paramList != null) {
        paramExtractor = paramList;
      } else {
        paramExtractor = Collections.emptyList();
      }
      paramExtractor = new FragmentedMp4Extractor(0, paramTimestampAdjuster, null, paramDrmInitData, paramExtractor);
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    if (!(paramUri instanceof TsExtractor))
    {
      paramExtractor = createTsExtractor(payloadReaderFactoryFlags, paramFormat, paramList, paramTimestampAdjuster);
      if (sniffQuietly(paramExtractor, paramExtractorInput)) {
        return buildResult(paramExtractor);
      }
    }
    return buildResult(paramUri);
  }
}
