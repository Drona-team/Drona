package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import java.io.IOException;
import java.util.List;

public final class DashUtil
{
  private DashUtil() {}
  
  private static Representation getFirstRepresentation(Period paramPeriod, int paramInt)
  {
    paramInt = paramPeriod.getAdaptationSetIndex(paramInt);
    if (paramInt == -1) {
      return null;
    }
    paramPeriod = adaptationSets.get(paramInt)).representations;
    if (paramPeriod.isEmpty()) {
      return null;
    }
    return (Representation)paramPeriod.get(0);
  }
  
  public static ChunkIndex loadChunkIndex(DataSource paramDataSource, int paramInt, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    paramDataSource = loadInitializationData(paramDataSource, paramInt, paramRepresentation, true);
    if (paramDataSource == null) {
      return null;
    }
    return (ChunkIndex)paramDataSource.getSeekMap();
  }
  
  public static DrmInitData loadDrmInitData(DataSource paramDataSource, Period paramPeriod)
    throws IOException, InterruptedException
  {
    int i = 2;
    Representation localRepresentation = getFirstRepresentation(paramPeriod, 2);
    Object localObject = localRepresentation;
    if (localRepresentation == null)
    {
      i = 1;
      paramPeriod = getFirstRepresentation(paramPeriod, 1);
      localObject = paramPeriod;
      if (paramPeriod == null) {
        return null;
      }
    }
    paramPeriod = format;
    paramDataSource = loadSampleFormat(paramDataSource, i, (Representation)localObject);
    if (paramDataSource == null) {
      return drmInitData;
    }
    return copyWithManifestFormatInfodrmInitData;
  }
  
  private static ChunkExtractorWrapper loadInitializationData(DataSource paramDataSource, int paramInt, Representation paramRepresentation, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    RangedUri localRangedUri1 = paramRepresentation.getInitializationUri();
    Object localObject = localRangedUri1;
    if (localRangedUri1 == null) {
      return null;
    }
    ChunkExtractorWrapper localChunkExtractorWrapper = newWrappedExtractor(paramInt, format);
    if (paramBoolean)
    {
      localObject = paramRepresentation.getIndexUri();
      if (localObject == null) {
        return null;
      }
      RangedUri localRangedUri2 = localRangedUri1.attemptMerge((RangedUri)localObject, baseUrl);
      if (localRangedUri2 == null) {
        loadInitializationData(paramDataSource, paramRepresentation, localChunkExtractorWrapper, localRangedUri1);
      } else {
        localObject = localRangedUri2;
      }
    }
    loadInitializationData(paramDataSource, paramRepresentation, localChunkExtractorWrapper, (RangedUri)localObject);
    return localChunkExtractorWrapper;
  }
  
  private static void loadInitializationData(DataSource paramDataSource, Representation paramRepresentation, ChunkExtractorWrapper paramChunkExtractorWrapper, RangedUri paramRangedUri)
    throws IOException, InterruptedException
  {
    new InitializationChunk(paramDataSource, new DataSpec(paramRangedUri.resolveUri(baseUrl), start, length, paramRepresentation.getCacheKey()), format, 0, null, paramChunkExtractorWrapper).load();
  }
  
  public static DashManifest loadManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return (DashManifest)ParsingLoadable.load(paramDataSource, new DashManifestParser(), paramUri, 4);
  }
  
  public static Format loadSampleFormat(DataSource paramDataSource, int paramInt, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    paramDataSource = loadInitializationData(paramDataSource, paramInt, paramRepresentation, false);
    if (paramDataSource == null) {
      return null;
    }
    return paramDataSource.getSampleFormats()[0];
  }
  
  private static ChunkExtractorWrapper newWrappedExtractor(int paramInt, Format paramFormat)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a7 = a6\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
}
