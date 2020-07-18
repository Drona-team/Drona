package com.google.android.exoplayer2.extractor.configurations;

import android.util.SparseArray;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.Collections;
import java.util.List;

public abstract interface TsPayloadReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws ParserException;
  
  public abstract void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TrackIdGenerator paramTrackIdGenerator);
  
  public abstract void seek();
  
  public final class DvbSubtitleInfo
  {
    public final byte[] initializationData;
    public final int type;
    
    public DvbSubtitleInfo(int paramInt, byte[] paramArrayOfByte)
    {
      type = paramInt;
      initializationData = paramArrayOfByte;
    }
  }
  
  public final class EsInfo
  {
    public final byte[] descriptorBytes;
    public final List<com.google.android.exoplayer2.extractor.ts.TsPayloadReader.DvbSubtitleInfo> dvbSubtitleInfos;
    public final String language;
    public final int streamType;
    
    public EsInfo(String paramString, List paramList, byte[] paramArrayOfByte)
    {
      streamType = this$1;
      language = paramString;
      if (paramList == null) {
        paramString = Collections.emptyList();
      } else {
        paramString = Collections.unmodifiableList(paramList);
      }
      dvbSubtitleInfos = paramString;
      descriptorBytes = paramArrayOfByte;
    }
  }
  
  public abstract interface Factory
  {
    public abstract SparseArray createInitialPayloadReaders();
    
    public abstract TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo);
  }
  
  public final class TrackIdGenerator
  {
    private static final int ID_UNSET = Integer.MIN_VALUE;
    private final int firstTrackId;
    private String formatId;
    private final String formatIdPrefix;
    private int trackId;
    private final int trackIdIncrement;
    
    public TrackIdGenerator(int paramInt)
    {
      this(this$1, paramInt);
    }
    
    public TrackIdGenerator(int paramInt1, int paramInt2)
    {
      Object localObject;
      if (this$1 != Integer.MIN_VALUE)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(this$1);
        ((StringBuilder)localObject).append("/");
        localObject = ((StringBuilder)localObject).toString();
      }
      else
      {
        localObject = "";
      }
      formatIdPrefix = ((String)localObject);
      firstTrackId = paramInt1;
      trackIdIncrement = paramInt2;
      trackId = Integer.MIN_VALUE;
    }
    
    private void maybeThrowUninitializedError()
    {
      if (trackId != Integer.MIN_VALUE) {
        return;
      }
      throw new IllegalStateException("generateNewId() must be called before retrieving ids.");
    }
    
    public void generateNewId()
    {
      int i;
      if (trackId == Integer.MIN_VALUE) {
        i = firstTrackId;
      } else {
        i = trackId + trackIdIncrement;
      }
      trackId = i;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(formatIdPrefix);
      localStringBuilder.append(trackId);
      formatId = localStringBuilder.toString();
    }
    
    public String getFormatId()
    {
      maybeThrowUninitializedError();
      return formatId;
    }
    
    public int getTrackId()
    {
      maybeThrowUninitializedError();
      return trackId;
    }
  }
}
