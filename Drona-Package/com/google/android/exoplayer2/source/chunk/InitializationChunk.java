package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public final class InitializationChunk
  extends Chunk
{
  private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private long nextLoadPosition;
  
  public InitializationChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, ChunkExtractorWrapper paramChunkExtractorWrapper)
  {
    super(paramDataSource, paramDataSpec, 2, paramFormat, paramInt, paramObject, -9223372036854775807L, -9223372036854775807L);
    extractorWrapper = paramChunkExtractorWrapper;
  }
  
  public void cancelLoad()
  {
    loadCanceled = true;
  }
  
  /* Error */
  public void load()
    throws java.io.IOException, java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 44	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   4: aload_0
    //   5: getfield 46	com/google/android/exoplayer2/source/chunk/InitializationChunk:nextLoadPosition	J
    //   8: invokevirtual 52	com/google/android/exoplayer2/upstream/DataSpec:subrange	(J)Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   11: astore 7
    //   13: new 54	com/google/android/exoplayer2/extractor/DefaultExtractorInput
    //   16: dup
    //   17: aload_0
    //   18: getfield 58	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   21: aload 7
    //   23: getfield 61	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   26: aload_0
    //   27: getfield 58	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   30: aload 7
    //   32: invokevirtual 67	com/google/android/exoplayer2/upstream/StatsDataSource:open	(Lcom/google/android/exoplayer2/upstream/DataSpec;)J
    //   35: invokespecial 70	com/google/android/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lcom/google/android/exoplayer2/upstream/DataSource;JJ)V
    //   38: astore 7
    //   40: aload_0
    //   41: getfield 46	com/google/android/exoplayer2/source/chunk/InitializationChunk:nextLoadPosition	J
    //   44: lstore_2
    //   45: lload_2
    //   46: lconst_0
    //   47: lcmp
    //   48: ifne +17 -> 65
    //   51: aload_0
    //   52: getfield 30	com/google/android/exoplayer2/source/chunk/InitializationChunk:extractorWrapper	Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   55: aconst_null
    //   56: ldc2_w 24
    //   59: ldc2_w 24
    //   62: invokevirtual 76	com/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper:init	(Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper$TrackOutputProvider;JJ)V
    //   65: aload_0
    //   66: getfield 30	com/google/android/exoplayer2/source/chunk/InitializationChunk:extractorWrapper	Lcom/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   69: getfield 80	com/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
    //   72: astore 8
    //   74: iconst_0
    //   75: istore_1
    //   76: iload_1
    //   77: ifne +30 -> 107
    //   80: aload_0
    //   81: getfield 33	com/google/android/exoplayer2/source/chunk/InitializationChunk:loadCanceled	Z
    //   84: istore 6
    //   86: iload 6
    //   88: ifne +19 -> 107
    //   91: aload 8
    //   93: aload 7
    //   95: getstatic 21	com/google/android/exoplayer2/source/chunk/InitializationChunk:DUMMY_POSITION_HOLDER	Lcom/google/android/exoplayer2/extractor/PositionHolder;
    //   98: invokeinterface 86 3 0
    //   103: istore_1
    //   104: goto -28 -> 76
    //   107: iconst_1
    //   108: istore 6
    //   110: iload_1
    //   111: iconst_1
    //   112: if_icmpeq +6 -> 118
    //   115: goto +6 -> 121
    //   118: iconst_0
    //   119: istore 6
    //   121: iload 6
    //   123: invokestatic 92	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
    //   126: aload 7
    //   128: invokeinterface 98 1 0
    //   133: lstore_2
    //   134: aload_0
    //   135: getfield 44	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   138: getfield 61	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   141: lstore 4
    //   143: aload_0
    //   144: lload_2
    //   145: lload 4
    //   147: lsub
    //   148: putfield 46	com/google/android/exoplayer2/source/chunk/InitializationChunk:nextLoadPosition	J
    //   151: aload_0
    //   152: getfield 58	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   155: invokestatic 104	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   158: return
    //   159: astore 8
    //   161: aload 7
    //   163: invokeinterface 98 1 0
    //   168: lstore_2
    //   169: aload_0
    //   170: getfield 44	com/google/android/exoplayer2/source/chunk/Chunk:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   173: getfield 61	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   176: lstore 4
    //   178: aload_0
    //   179: lload_2
    //   180: lload 4
    //   182: lsub
    //   183: putfield 46	com/google/android/exoplayer2/source/chunk/InitializationChunk:nextLoadPosition	J
    //   186: aload 8
    //   188: athrow
    //   189: astore 7
    //   191: aload_0
    //   192: getfield 58	com/google/android/exoplayer2/source/chunk/Chunk:dataSource	Lcom/google/android/exoplayer2/upstream/StatsDataSource;
    //   195: invokestatic 104	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   198: aload 7
    //   200: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	InitializationChunk
    //   75	38	1	i	int
    //   44	136	2	l1	long
    //   141	40	4	l2	long
    //   84	38	6	bool	boolean
    //   11	151	7	localObject	Object
    //   189	10	7	localThrowable1	Throwable
    //   72	20	8	localExtractor	com.google.android.exoplayer2.extractor.Extractor
    //   159	28	8	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   65	74	159	java/lang/Throwable
    //   80	86	159	java/lang/Throwable
    //   91	104	159	java/lang/Throwable
    //   121	126	159	java/lang/Throwable
    //   13	45	189	java/lang/Throwable
    //   51	65	189	java/lang/Throwable
    //   126	143	189	java/lang/Throwable
    //   143	151	189	java/lang/Throwable
    //   161	178	189	java/lang/Throwable
    //   178	189	189	java/lang/Throwable
  }
}
