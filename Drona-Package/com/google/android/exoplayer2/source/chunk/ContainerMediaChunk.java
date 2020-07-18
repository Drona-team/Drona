package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public class ContainerMediaChunk
  extends BaseMediaChunk
{
  private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
  private final int chunkCount;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private boolean loadCompleted;
  private long nextLoadPosition;
  private final long sampleOffsetUs;
  
  public ContainerMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, int paramInt2, long paramLong6, ChunkExtractorWrapper paramChunkExtractorWrapper)
  {
    super(paramDataSource, paramDataSpec, paramFormat, paramInt1, paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
    chunkCount = paramInt2;
    sampleOffsetUs = paramLong6;
    extractorWrapper = paramChunkExtractorWrapper;
  }
  
  public final void cancelLoad()
  {
    loadCanceled = true;
  }
  
  public long getNextChunkIndex()
  {
    return chunkIndex + chunkCount;
  }
  
  public boolean isLoadCompleted()
  {
    return loadCompleted;
  }
  
  /* Error */
  public final void load()
    throws java.io.IOException, java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 63	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   4: aload_0
    //   5: getfield 65	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:nextLoadPosition	J
    //   8: invokevirtual 71	com/google/android/exoplayer2/upstream/DataSpec:subrange	(J)Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   11: astore 10
    //   13: new 73	com/google/android/exoplayer2/extractor/DefaultExtractorInput
    //   16: dup
    //   17: aload_0
    //   18: getfield 77	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   21: aload 10
    //   23: getfield 80	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   26: aload_0
    //   27: getfield 77	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   30: aload 10
    //   32: invokevirtual 86	com/google/android/exoplayer2/upstream/StatsDataSource:open	(Lcom/google/android/exoplayer2/upstream/DataSpec;)J
    //   35: invokespecial 89	com/google/android/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lcom/google/android/exoplayer2/upstream/DataSource;JJ)V
    //   38: astore 10
    //   40: aload_0
    //   41: getfield 65	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:nextLoadPosition	J
    //   44: lstore_2
    //   45: lload_2
    //   46: lconst_0
    //   47: lcmp
    //   48: ifne +112 -> 160
    //   51: aload_0
    //   52: invokevirtual 93	com/google/android/exoplayer2/source/chunk/BaseMediaChunk:getOutput	()Lcom/google/android/exoplayer2/source/chunk/BaseMediaChunkOutput;
    //   55: astore 11
    //   57: aload 11
    //   59: aload_0
    //   60: getfield 34	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:sampleOffsetUs	J
    //   63: invokevirtual 99	com/google/android/exoplayer2/source/chunk/BaseMediaChunkOutput:setSampleOffsetUs	(J)V
    //   66: aload_0
    //   67: getfield 36	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:extractorWrapper	Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   70: astore 12
    //   72: aload_0
    //   73: getfield 102	com/google/android/exoplayer2/source/chunk/BaseMediaChunk:clippedStartTimeUs	J
    //   76: lstore_2
    //   77: ldc2_w 103
    //   80: lstore 4
    //   82: lload_2
    //   83: ldc2_w 103
    //   86: lcmp
    //   87: ifne +10 -> 97
    //   90: ldc2_w 103
    //   93: lstore_2
    //   94: goto +19 -> 113
    //   97: aload_0
    //   98: getfield 102	com/google/android/exoplayer2/source/chunk/BaseMediaChunk:clippedStartTimeUs	J
    //   101: lstore_2
    //   102: aload_0
    //   103: getfield 34	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:sampleOffsetUs	J
    //   106: lstore 6
    //   108: lload_2
    //   109: lload 6
    //   111: lsub
    //   112: lstore_2
    //   113: aload_0
    //   114: getfield 107	com/google/android/exoplayer2/source/chunk/BaseMediaChunk:clippedEndTimeUs	J
    //   117: lstore 6
    //   119: lload 6
    //   121: ldc2_w 103
    //   124: lcmp
    //   125: ifne +6 -> 131
    //   128: goto +22 -> 150
    //   131: aload_0
    //   132: getfield 107	com/google/android/exoplayer2/source/chunk/BaseMediaChunk:clippedEndTimeUs	J
    //   135: lstore 4
    //   137: aload_0
    //   138: getfield 34	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:sampleOffsetUs	J
    //   141: lstore 6
    //   143: lload 4
    //   145: lload 6
    //   147: lsub
    //   148: lstore 4
    //   150: aload 12
    //   152: aload 11
    //   154: lload_2
    //   155: lload 4
    //   157: invokevirtual 113	com/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper:init	(Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper$TrackOutputProvider;JJ)V
    //   160: aload_0
    //   161: getfield 36	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:extractorWrapper	Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   164: getfield 117	com/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
    //   167: astore 11
    //   169: iconst_0
    //   170: istore 8
    //   172: iconst_0
    //   173: istore_1
    //   174: iload_1
    //   175: ifne +30 -> 205
    //   178: aload_0
    //   179: getfield 39	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:loadCanceled	Z
    //   182: istore 9
    //   184: iload 9
    //   186: ifne +19 -> 205
    //   189: aload 11
    //   191: aload 10
    //   193: getstatic 25	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:DUMMY_POSITION_HOLDER	Lcom/google/android/exoplayer2/extractor/PositionHolder;
    //   196: invokeinterface 123 3 0
    //   201: istore_1
    //   202: goto -28 -> 174
    //   205: iload_1
    //   206: iconst_1
    //   207: if_icmpeq +6 -> 213
    //   210: iconst_1
    //   211: istore 8
    //   213: iload 8
    //   215: invokestatic 129	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
    //   218: aload 10
    //   220: invokeinterface 134 1 0
    //   225: lstore_2
    //   226: aload_0
    //   227: getfield 63	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   230: getfield 80	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   233: lstore 4
    //   235: aload_0
    //   236: lload_2
    //   237: lload 4
    //   239: lsub
    //   240: putfield 65	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:nextLoadPosition	J
    //   243: aload_0
    //   244: getfield 77	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   247: invokestatic 140	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   250: aload_0
    //   251: iconst_1
    //   252: putfield 50	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:loadCompleted	Z
    //   255: return
    //   256: astore 11
    //   258: aload 10
    //   260: invokeinterface 134 1 0
    //   265: lstore_2
    //   266: aload_0
    //   267: getfield 63	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   270: getfield 80	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   273: lstore 4
    //   275: aload_0
    //   276: lload_2
    //   277: lload 4
    //   279: lsub
    //   280: putfield 65	com/google/android/exoplayer2/source/chunk/ContainerMediaChunk:nextLoadPosition	J
    //   283: aload 11
    //   285: athrow
    //   286: astore 10
    //   288: aload_0
    //   289: getfield 77	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   292: invokestatic 140	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   295: aload 10
    //   297: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	298	0	this	ContainerMediaChunk
    //   173	35	1	i	int
    //   44	233	2	l1	long
    //   80	198	4	l2	long
    //   106	40	6	l3	long
    //   170	44	8	bool1	boolean
    //   182	3	9	bool2	boolean
    //   11	248	10	localObject1	Object
    //   286	10	10	localThrowable1	Throwable
    //   55	135	11	localObject2	Object
    //   256	28	11	localThrowable2	Throwable
    //   70	81	12	localChunkExtractorWrapper	ChunkExtractorWrapper
    // Exception table:
    //   from	to	target	type
    //   160	169	256	java/lang/Throwable
    //   178	184	256	java/lang/Throwable
    //   189	202	256	java/lang/Throwable
    //   213	218	256	java/lang/Throwable
    //   13	45	286	java/lang/Throwable
    //   51	77	286	java/lang/Throwable
    //   97	108	286	java/lang/Throwable
    //   113	119	286	java/lang/Throwable
    //   131	143	286	java/lang/Throwable
    //   150	160	286	java/lang/Throwable
    //   218	235	286	java/lang/Throwable
    //   235	243	286	java/lang/Throwable
    //   258	275	286	java/lang/Throwable
    //   275	286	286	java/lang/Throwable
  }
}
