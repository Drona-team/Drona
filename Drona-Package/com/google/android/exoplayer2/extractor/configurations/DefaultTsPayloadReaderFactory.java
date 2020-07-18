package com.google.android.exoplayer2.extractor.configurations;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

public final class DefaultTsPayloadReaderFactory
  implements TsPayloadReader.Factory
{
  private static final int DESCRIPTOR_TAG_CAPTION_SERVICE = 134;
  public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int FLAG_DETECT_ACCESS_UNITS = 8;
  public static final int FLAG_IGNORE_AAC_STREAM = 2;
  public static final int FLAG_IGNORE_H264_STREAM = 4;
  public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
  public static final int FLAG_OVERRIDE_CAPTION_DESCRIPTORS = 32;
  private final List<Format> closedCaptionFormats;
  private final int flags;
  
  public DefaultTsPayloadReaderFactory()
  {
    this(0);
  }
  
  public DefaultTsPayloadReaderFactory(int paramInt)
  {
    this(paramInt, Collections.singletonList(Format.createTextSampleFormat(null, "application/cea-608", 0, null)));
  }
  
  public DefaultTsPayloadReaderFactory(int paramInt, List paramList)
  {
    flags = paramInt;
    closedCaptionFormats = paramList;
  }
  
  private SeiReader buildSeiReader(TsPayloadReader.EsInfo paramEsInfo)
  {
    return new SeiReader(getClosedCaptionFormats(paramEsInfo));
  }
  
  private UserDataReader buildUserDataReader(TsPayloadReader.EsInfo paramEsInfo)
  {
    return new UserDataReader(getClosedCaptionFormats(paramEsInfo));
  }
  
  private List getClosedCaptionFormats(TsPayloadReader.EsInfo paramEsInfo)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a9 = a8\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private boolean isSet(int paramInt)
  {
    return (paramInt & flags) != 0;
  }
  
  public SparseArray createInitialPayloadReaders()
  {
    return new SparseArray();
  }
  
  public TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 134: 
      if (isSet(16)) {
        return null;
      }
      return new SectionReader(new SpliceInfoSectionReader());
    case 130: 
    case 138: 
      return new PesReader(new DtsReader(language));
    case 129: 
    case 135: 
      return new PesReader(new Ac3Reader(language));
    case 89: 
      return new PesReader(new DvbSubtitleReader(dvbSubtitleInfos));
    case 36: 
      return new PesReader(new H265Reader(buildSeiReader(paramEsInfo)));
    case 27: 
      if (isSet(4)) {
        return null;
      }
      return new PesReader(new H264Reader(buildSeiReader(paramEsInfo), isSet(1), isSet(8)));
    case 21: 
      return new PesReader(new Id3Reader());
    case 17: 
      if (isSet(2)) {
        return null;
      }
      return new PesReader(new LatmReader(language));
    case 15: 
      if (isSet(2)) {
        return null;
      }
      return new PesReader(new AdtsReader(false, language));
    case 3: 
    case 4: 
      return new PesReader(new MpegAudioReader(language));
    }
    return new PesReader(new H262Reader(buildUserDataReader(paramEsInfo)));
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
}
