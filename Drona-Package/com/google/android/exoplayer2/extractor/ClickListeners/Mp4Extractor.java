package com.google.android.exoplayer2.extractor.ClickListeners;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class Mp4Extractor
  implements Extractor, SeekMap
{
  private static final int BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
  public static final ExtractorsFactory FACTORY = -..Lambda.Mp4Extractor.quy71uYOGsneho91FZy1d2UGE1Q.INSTANCE;
  public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
  private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 10485760L;
  private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144L;
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private long[][] accumulatedSampleSizes;
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader;
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final ArrayDeque<com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom> containerAtoms;
  private long durationUs;
  private ExtractorOutput extractorOutput;
  private int firstVideoTrackIndex;
  private final int flags;
  private boolean isQuickTime;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private int sampleTrackIndex;
  private Mp4Track[] tracks;
  
  public Mp4Extractor()
  {
    this(0);
  }
  
  public Mp4Extractor(int paramInt)
  {
    flags = paramInt;
    atomHeader = new ParsableByteArray(16);
    containerAtoms = new ArrayDeque();
    nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    nalLength = new ParsableByteArray(4);
    sampleTrackIndex = -1;
  }
  
  private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] paramArrayOfMp4Track)
  {
    long[][] arrayOfLong = new long[paramArrayOfMp4Track.length][];
    int[] arrayOfInt = new int[paramArrayOfMp4Track.length];
    long[] arrayOfLong1 = new long[paramArrayOfMp4Track.length];
    boolean[] arrayOfBoolean = new boolean[paramArrayOfMp4Track.length];
    int i = 0;
    while (i < paramArrayOfMp4Track.length)
    {
      arrayOfLong[i] = new long[sampleTable.sampleCount];
      arrayOfLong1[i] = sampleTable.timestampsUs[0];
      i += 1;
    }
    long l1 = 0L;
    int j = 0;
    while (j < paramArrayOfMp4Track.length)
    {
      int k = -1;
      long l2 = Long.MAX_VALUE;
      i = 0;
      while (i < paramArrayOfMp4Track.length)
      {
        int m = k;
        long l3 = l2;
        if (arrayOfBoolean[i] == 0)
        {
          m = k;
          l3 = l2;
          if (arrayOfLong1[i] <= l2)
          {
            l3 = arrayOfLong1[i];
            m = i;
          }
        }
        i += 1;
        k = m;
        l2 = l3;
      }
      i = arrayOfInt[k];
      arrayOfLong[k][i] = l1;
      l1 += sampleTable.sizes[i];
      i += 1;
      arrayOfInt[k] = i;
      if (i < arrayOfLong[k].length)
      {
        arrayOfLong1[k] = sampleTable.timestampsUs[i];
      }
      else
      {
        arrayOfBoolean[k] = true;
        j += 1;
      }
    }
    return arrayOfLong;
  }
  
  private void enterReadingAtomHeaderState()
  {
    parserState = 0;
    atomHeaderBytesRead = 0;
  }
  
  private static int getSynchronizationSampleIndex(TrackSampleTable paramTrackSampleTable, long paramLong)
  {
    int j = paramTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
    int i = j;
    if (j == -1) {
      i = paramTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
    }
    return i;
  }
  
  private int getTrackIndexOfNextReadSample(long paramLong)
  {
    int i = 0;
    long l5 = Long.MAX_VALUE;
    int i3 = 1;
    long l3 = Long.MAX_VALUE;
    int m = -1;
    int i2 = -1;
    int k = 1;
    long l1 = Long.MAX_VALUE;
    while (i < tracks.length)
    {
      Mp4Track localMp4Track = tracks[i];
      int j = sampleIndex;
      long l7;
      if (j == sampleTable.sampleCount)
      {
        l7 = l5;
      }
      else
      {
        long l2 = sampleTable.offsets[j];
        long l6 = accumulatedSampleSizes[i][j];
        l7 = l2 - paramLong;
        if ((l7 >= 0L) && (l7 < 262144L)) {
          j = 0;
        } else {
          j = 1;
        }
        long l4;
        int i1;
        int n;
        if ((j != 0) || (k == 0))
        {
          l4 = l3;
          i1 = m;
          n = k;
          l2 = l1;
          if (j == k)
          {
            l4 = l3;
            i1 = m;
            n = k;
            l2 = l1;
            if (l7 >= l1) {}
          }
        }
        else
        {
          n = j;
          i1 = i;
          l2 = l7;
          l4 = l6;
        }
        l7 = l5;
        l3 = l4;
        m = i1;
        k = n;
        l1 = l2;
        if (l6 < l5)
        {
          i2 = i;
          l1 = l2;
          k = n;
          m = i1;
          l3 = l4;
          i3 = j;
          l7 = l6;
        }
      }
      i += 1;
      l5 = l7;
    }
    if ((l5 != Long.MAX_VALUE) && (i3 != 0))
    {
      if (l3 < l5 + 10485760L) {
        return m;
      }
      return i2;
    }
    return m;
  }
  
  private ArrayList getTrackSampleTables(Atom.ContainerAtom paramContainerAtom, GaplessInfoHolder paramGaplessInfoHolder, boolean paramBoolean)
    throws ParserException
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < containerChildren.size())
    {
      Object localObject = (Atom.ContainerAtom)containerChildren.get(i);
      if (type == Atom.TYPE_trak)
      {
        Track localTrack = AtomParsers.parseTrak((Atom.ContainerAtom)localObject, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), -9223372036854775807L, null, paramBoolean, isQuickTime);
        if (localTrack != null)
        {
          localObject = AtomParsers.parseStbl(localTrack, ((Atom.ContainerAtom)localObject).getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), paramGaplessInfoHolder);
          if (sampleCount != 0) {
            localArrayList.add(localObject);
          }
        }
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private static long maybeAdjustSeekOffset(TrackSampleTable paramTrackSampleTable, long paramLong1, long paramLong2)
  {
    int i = getSynchronizationSampleIndex(paramTrackSampleTable, paramLong1);
    if (i == -1) {
      return paramLong2;
    }
    return Math.min(offsets[i], paramLong2);
  }
  
  private void processAtomEnded(long paramLong)
    throws ParserException
  {
    while ((!containerAtoms.isEmpty()) && (containerAtoms.peek()).endPosition == paramLong))
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)containerAtoms.pop();
      if (type == Atom.TYPE_moov)
      {
        processMoovAtom(localContainerAtom);
        containerAtoms.clear();
        parserState = 2;
      }
      else if (!containerAtoms.isEmpty())
      {
        ((Atom.ContainerAtom)containerAtoms.peek()).add(localContainerAtom);
      }
    }
    if (parserState != 2) {
      enterReadingAtomHeaderState();
    }
  }
  
  private static boolean processFtypAtom(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {
      return true;
    }
    paramParsableByteArray.skipBytes(4);
    while (paramParsableByteArray.bytesLeft() > 0) {
      if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {
        return true;
      }
    }
    return false;
  }
  
  private void processMoovAtom(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    ArrayList localArrayList1 = new ArrayList();
    GaplessInfoHolder localGaplessInfoHolder = new GaplessInfoHolder();
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_udta);
    Object localObject3;
    Object localObject2;
    if (localObject1 != null)
    {
      localObject3 = AtomParsers.parseUdta((Atom.LeafAtom)localObject1, isQuickTime);
      localObject1 = localObject3;
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        localGaplessInfoHolder.setFromMetadata((Metadata)localObject3);
        localObject2 = localObject1;
      }
    }
    else
    {
      localObject2 = null;
    }
    int j = flags;
    int i = 0;
    boolean bool;
    if ((j & 0x1) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    ArrayList localArrayList2 = getTrackSampleTables(paramContainerAtom, localGaplessInfoHolder, bool);
    int m = localArrayList2.size();
    j = -1;
    long l1 = -9223372036854775807L;
    while (i < m)
    {
      TrackSampleTable localTrackSampleTable = (TrackSampleTable)localArrayList2.get(i);
      Track localTrack = track;
      Mp4Track localMp4Track = new Mp4Track(localTrack, localTrackSampleTable, extractorOutput.track(i, type));
      int k = maximumSize;
      localObject3 = format.copyWithMaxInputSize(k + 30);
      localObject1 = localObject3;
      paramContainerAtom = (Atom.ContainerAtom)localObject1;
      if (type == 1)
      {
        if (localGaplessInfoHolder.hasGaplessInfo()) {
          localObject1 = ((Format)localObject3).copyWithGaplessInfo(encoderDelay, encoderPadding);
        }
        paramContainerAtom = (Atom.ContainerAtom)localObject1;
        if (localObject2 != null) {
          paramContainerAtom = ((Format)localObject1).copyWithMetadata(localObject2);
        }
      }
      trackOutput.format(paramContainerAtom);
      long l2;
      if (durationUs != -9223372036854775807L) {
        l2 = durationUs;
      } else {
        l2 = durationUs;
      }
      l1 = Math.max(l1, l2);
      k = j;
      if (type == 2)
      {
        k = j;
        if (j == -1) {
          k = localArrayList1.size();
        }
      }
      localArrayList1.add(localMp4Track);
      i += 1;
      j = k;
    }
    firstVideoTrackIndex = j;
    durationUs = l1;
    tracks = ((Mp4Track[])localArrayList1.toArray(new Mp4Track[localArrayList1.size()]));
    accumulatedSampleSizes = calculateAccumulatedSampleSizes(tracks);
    extractorOutput.endTracks();
    extractorOutput.seekMap(this);
  }
  
  private boolean readAtomHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (atomHeaderBytesRead == 0)
    {
      if (!paramExtractorInput.readFully(atomHeader.data, 0, 8, true)) {
        return false;
      }
      atomHeaderBytesRead = 8;
      atomHeader.setPosition(0);
      atomSize = atomHeader.readUnsignedInt();
      atomType = atomHeader.readInt();
    }
    long l1;
    if (atomSize == 1L)
    {
      paramExtractorInput.readFully(atomHeader.data, 8, 8);
      atomHeaderBytesRead += 8;
      atomSize = atomHeader.readUnsignedLongToLong();
    }
    else if (atomSize == 0L)
    {
      long l3 = paramExtractorInput.getLength();
      l1 = l3;
      long l2 = l1;
      if (l3 == -1L)
      {
        l2 = l1;
        if (!containerAtoms.isEmpty()) {
          l2 = containerAtoms.peek()).endPosition;
        }
      }
      if (l2 != -1L) {
        atomSize = (l2 - paramExtractorInput.getPosition() + atomHeaderBytesRead);
      }
    }
    if (atomSize >= atomHeaderBytesRead)
    {
      if (shouldParseContainerAtom(atomType))
      {
        l1 = paramExtractorInput.getPosition() + atomSize - atomHeaderBytesRead;
        containerAtoms.push(new Atom.ContainerAtom(atomType, l1));
        if (atomSize == atomHeaderBytesRead)
        {
          processAtomEnded(l1);
          return true;
        }
        enterReadingAtomHeaderState();
        return true;
      }
      if (shouldParseLeafAtom(atomType))
      {
        boolean bool;
        if (atomHeaderBytesRead == 8) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        if (atomSize <= 2147483647L) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        atomData = new ParsableByteArray((int)atomSize);
        System.arraycopy(atomHeader.data, 0, atomData.data, 0, 8);
        parserState = 1;
        return true;
      }
      atomData = null;
      parserState = 1;
      return true;
    }
    throw new ParserException("Atom size less than header length (unsupported).");
  }
  
  private boolean readAtomPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l1 = atomSize - atomHeaderBytesRead;
    long l2 = paramExtractorInput.getPosition();
    if (atomData != null)
    {
      paramExtractorInput.readFully(atomData.data, atomHeaderBytesRead, (int)l1);
      if (atomType == Atom.TYPE_ftyp) {
        isQuickTime = processFtypAtom(atomData);
      } else if (!containerAtoms.isEmpty()) {
        ((Atom.ContainerAtom)containerAtoms.peek()).add(new Atom.LeafAtom(atomType, atomData));
      }
    }
    else
    {
      if (l1 >= 262144L) {
        break label135;
      }
      paramExtractorInput.skipFully((int)l1);
    }
    int i = 0;
    break label150;
    label135:
    position = (paramExtractorInput.getPosition() + l1);
    i = 1;
    label150:
    processAtomEnded(l2 + l1);
    return (i != 0) && (parserState != 2);
  }
  
  private int readSample(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l2 = paramExtractorInput.getPosition();
    if (sampleTrackIndex == -1)
    {
      sampleTrackIndex = getTrackIndexOfNextReadSample(l2);
      if (sampleTrackIndex == -1) {
        return -1;
      }
    }
    Mp4Track localMp4Track = tracks[sampleTrackIndex];
    TrackOutput localTrackOutput = trackOutput;
    int k = sampleIndex;
    long l1 = sampleTable.offsets[k];
    int j = sampleTable.sizes[k];
    l2 = l1 - l2 + sampleBytesWritten;
    if ((l2 >= 0L) && (l2 < 262144L))
    {
      l1 = l2;
      int i = j;
      if (track.sampleTransformation == 1)
      {
        l1 = l2 + 8L;
        i = j - 8;
      }
      paramExtractorInput.skipFully((int)l1);
      if (track.nalUnitLengthFieldLength != 0)
      {
        paramPositionHolder = nalLength.data;
        paramPositionHolder[0] = 0;
        paramPositionHolder[1] = 0;
        paramPositionHolder[2] = 0;
        int m = track.nalUnitLengthFieldLength;
        int n = 4 - track.nalUnitLengthFieldLength;
        for (;;)
        {
          j = i;
          if (sampleBytesWritten >= i) {
            break;
          }
          if (sampleCurrentNalBytesRemaining == 0)
          {
            paramExtractorInput.readFully(nalLength.data, n, m);
            nalLength.setPosition(0);
            sampleCurrentNalBytesRemaining = nalLength.readUnsignedIntToInt();
            nalStartCode.setPosition(0);
            localTrackOutput.sampleData(nalStartCode, 4);
            sampleBytesWritten += 4;
            i += n;
          }
          else
          {
            j = localTrackOutput.sampleData(paramExtractorInput, sampleCurrentNalBytesRemaining, false);
            sampleBytesWritten += j;
            sampleCurrentNalBytesRemaining -= j;
          }
        }
      }
      for (;;)
      {
        j = i;
        if (sampleBytesWritten >= i) {
          break;
        }
        j = localTrackOutput.sampleData(paramExtractorInput, i - sampleBytesWritten, false);
        sampleBytesWritten += j;
        sampleCurrentNalBytesRemaining -= j;
      }
      localTrackOutput.sampleMetadata(sampleTable.timestampsUs[k], sampleTable.flags[k], j, 0, null);
      sampleIndex += 1;
      sampleTrackIndex = -1;
      sampleBytesWritten = 0;
      sampleCurrentNalBytesRemaining = 0;
      return 0;
    }
    position = l1;
    return 1;
  }
  
  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_edts);
  }
  
  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_stts) || (paramInt == Atom.TYPE_stss) || (paramInt == Atom.TYPE_ctts) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_stsc) || (paramInt == Atom.TYPE_stsz) || (paramInt == Atom.TYPE_stz2) || (paramInt == Atom.TYPE_stco) || (paramInt == Atom.TYPE_co64) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_ftyp) || (paramInt == Atom.TYPE_udta);
  }
  
  private void updateSampleIndices(long paramLong)
  {
    Mp4Track[] arrayOfMp4Track = tracks;
    int m = arrayOfMp4Track.length;
    int i = 0;
    while (i < m)
    {
      Mp4Track localMp4Track = arrayOfMp4Track[i];
      TrackSampleTable localTrackSampleTable = sampleTable;
      int k = localTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
      int j = k;
      if (k == -1) {
        j = localTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
      }
      sampleIndex = j;
      i += 1;
    }
  }
  
  public long getDurationUs()
  {
    return durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    if (tracks.length == 0) {
      return new SeekMap.SeekPoints(SeekPoint.START);
    }
    long l2;
    long l1;
    label151:
    long l3;
    if (firstVideoTrackIndex != -1)
    {
      localObject = tracks[firstVideoTrackIndex].sampleTable;
      i = getSynchronizationSampleIndex((TrackSampleTable)localObject, paramLong);
      if (i == -1) {
        return new SeekMap.SeekPoints(SeekPoint.START);
      }
      l4 = timestampsUs[i];
      l2 = offsets[i];
      if ((l4 < paramLong) && (i < sampleCount - 1))
      {
        int j = ((TrackSampleTable)localObject).getIndexOfLaterOrEqualSynchronizationSample(paramLong);
        if ((j != -1) && (j != i))
        {
          paramLong = timestampsUs[j];
          l1 = offsets[j];
          break label151;
        }
      }
      l1 = -1L;
      paramLong = -9223372036854775807L;
      l3 = l1;
      l1 = l2;
      l2 = paramLong;
      paramLong = l3;
      l3 = l4;
    }
    else
    {
      l1 = Long.MAX_VALUE;
      l4 = -1L;
      l2 = -9223372036854775807L;
      l3 = paramLong;
      paramLong = l4;
    }
    int i = 0;
    long l4 = l1;
    while (i < tracks.length)
    {
      long l5 = l4;
      l1 = paramLong;
      if (i != firstVideoTrackIndex)
      {
        localObject = tracks[i].sampleTable;
        l5 = maybeAdjustSeekOffset((TrackSampleTable)localObject, l3, l4);
        l1 = paramLong;
        if (l2 != -9223372036854775807L) {
          l1 = maybeAdjustSeekOffset((TrackSampleTable)localObject, l2, paramLong);
        }
      }
      i += 1;
      l4 = l5;
      paramLong = l1;
    }
    Object localObject = new SeekPoint(l3, l4);
    if (l2 == -9223372036854775807L) {
      return new SeekMap.SeekPoints((SeekPoint)localObject);
    }
    return new SeekMap.SeekPoints((SeekPoint)localObject, new SeekPoint(l2, paramLong));
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    do
    {
      do
      {
        switch (parserState)
        {
        default: 
          throw new IllegalStateException();
        case 2: 
          return readSample(paramExtractorInput, paramPositionHolder);
        }
      } while (!readAtomPayload(paramExtractorInput, paramPositionHolder));
      return 1;
    } while (readAtomHeader(paramExtractorInput));
    return -1;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    containerAtoms.clear();
    atomHeaderBytesRead = 0;
    sampleTrackIndex = -1;
    sampleBytesWritten = 0;
    sampleCurrentNalBytesRemaining = 0;
    if (paramLong1 == 0L)
    {
      enterReadingAtomHeaderState();
      return;
    }
    if (tracks != null) {
      updateSampleIndices(paramLong2);
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return Sniffer.sniffUnfragmented(paramExtractorInput);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
  
  final class Mp4Track
  {
    public int sampleIndex;
    public final TrackSampleTable sampleTable;
    public final TrackOutput trackOutput;
    
    public Mp4Track(TrackSampleTable paramTrackSampleTable, TrackOutput paramTrackOutput)
    {
      sampleTable = paramTrackSampleTable;
      trackOutput = paramTrackOutput;
    }
  }
}
