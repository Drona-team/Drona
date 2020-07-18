package com.google.android.exoplayer2.extractor.ClickListeners;

import android.util.Pair;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FragmentedMp4Extractor
  implements Extractor
{
  private static final Format EMSG_FORMAT = Format.createSampleFormat(null, "application/x-emsg", Long.MAX_VALUE);
  public static final ExtractorsFactory FACTORY = -..Lambda.FragmentedMp4Extractor.i0zfpH_PcF0vytkdatCL0xeWFhQ.INSTANCE;
  public static final int FLAG_ENABLE_EMSG_TRACK = 4;
  private static final int FLAG_SIDELOADED = 8;
  public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
  public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 16;
  public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
  private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE;
  private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_ENCRYPTION_DATA = 2;
  private static final int STATE_READING_SAMPLE_CONTINUE = 4;
  private static final int STATE_READING_SAMPLE_START = 3;
  private static final String TAG = "FragmentedMp4Extractor";
  @Nullable
  private final TrackOutput additionalEmsgTrackOutput;
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader;
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private TrackOutput[] cea608TrackOutputs;
  private final List<Format> closedCaptionFormats;
  private final ArrayDeque<com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom> containerAtoms;
  private TrackBundle currentTrackBundle;
  private long durationUs;
  private TrackOutput[] emsgTrackOutputs;
  private long endOfMdatPosition;
  private final byte[] extendedTypeScratch;
  private ExtractorOutput extractorOutput;
  private final int flags;
  private boolean haveOutputSeekMap;
  private final ParsableByteArray nalBuffer;
  private final ParsableByteArray nalPrefix;
  private final ParsableByteArray nalStartCode;
  private int parserState;
  private int pendingMetadataSampleBytes;
  private final ArrayDeque<com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.MetadataSampleInfo> pendingMetadataSampleInfos;
  private long pendingSeekTimeUs;
  private boolean processSeiNalUnitPayload;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private int sampleSize;
  private long segmentIndexEarliestPresentationTimeUs;
  @Nullable
  private final DrmInitData sideloadedDrmInitData;
  @Nullable
  private final Track sideloadedTrack;
  @Nullable
  private final TimestampAdjuster timestampAdjuster;
  private final SparseArray<com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.TrackBundle> trackBundles;
  
  static
  {
    PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[] { -94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12 };
  }
  
  public FragmentedMp4Extractor()
  {
    this(0);
  }
  
  public FragmentedMp4Extractor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public FragmentedMp4Extractor(int paramInt, TimestampAdjuster paramTimestampAdjuster)
  {
    this(paramInt, paramTimestampAdjuster, null, null);
  }
  
  public FragmentedMp4Extractor(int paramInt, TimestampAdjuster paramTimestampAdjuster, Track paramTrack, DrmInitData paramDrmInitData)
  {
    this(paramInt, paramTimestampAdjuster, paramTrack, paramDrmInitData, Collections.emptyList());
  }
  
  public FragmentedMp4Extractor(int paramInt, TimestampAdjuster paramTimestampAdjuster, Track paramTrack, DrmInitData paramDrmInitData, List paramList)
  {
    this(paramInt, paramTimestampAdjuster, paramTrack, paramDrmInitData, paramList, null);
  }
  
  public FragmentedMp4Extractor(int paramInt, TimestampAdjuster paramTimestampAdjuster, Track paramTrack, DrmInitData paramDrmInitData, List paramList, TrackOutput paramTrackOutput)
  {
    int i;
    if (paramTrack != null) {
      i = 8;
    } else {
      i = 0;
    }
    flags = (paramInt | i);
    timestampAdjuster = paramTimestampAdjuster;
    sideloadedTrack = paramTrack;
    sideloadedDrmInitData = paramDrmInitData;
    closedCaptionFormats = Collections.unmodifiableList(paramList);
    additionalEmsgTrackOutput = paramTrackOutput;
    atomHeader = new ParsableByteArray(16);
    nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    nalPrefix = new ParsableByteArray(5);
    nalBuffer = new ParsableByteArray();
    extendedTypeScratch = new byte[16];
    containerAtoms = new ArrayDeque();
    pendingMetadataSampleInfos = new ArrayDeque();
    trackBundles = new SparseArray();
    durationUs = -9223372036854775807L;
    pendingSeekTimeUs = -9223372036854775807L;
    segmentIndexEarliestPresentationTimeUs = -9223372036854775807L;
    enterReadingAtomHeaderState();
  }
  
  private void enterReadingAtomHeaderState()
  {
    parserState = 0;
    atomHeaderBytesRead = 0;
  }
  
  private DefaultSampleValues getDefaultSampleValues(SparseArray paramSparseArray, int paramInt)
  {
    if (paramSparseArray.size() == 1) {
      return (DefaultSampleValues)paramSparseArray.valueAt(0);
    }
    return (DefaultSampleValues)Assertions.checkNotNull(paramSparseArray.get(paramInt));
  }
  
  private static DrmInitData getDrmInitDataFromAtoms(List paramList)
  {
    int j = paramList.size();
    int i = 0;
    Object localObject1;
    for (Object localObject2 = null; i < j; localObject2 = localObject1)
    {
      Object localObject3 = (Atom.LeafAtom)paramList.get(i);
      localObject1 = localObject2;
      if (type == Atom.TYPE_pssh)
      {
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = new ArrayList();
        }
        localObject2 = data.data;
        localObject3 = PsshAtomUtil.parseUuid((byte[])localObject2);
        if (localObject3 == null) {
          Log.w("FragmentedMp4Extractor", "Skipped pssh atom (failed to extract uuid)");
        } else {
          ((ArrayList)localObject1).add(new DrmInitData.SchemeData((UUID)localObject3, "video/mp4", (byte[])localObject2));
        }
      }
      i += 1;
    }
    if (localObject2 == null) {
      return null;
    }
    return new DrmInitData((List)localObject2);
  }
  
  private static TrackBundle getNextFragmentRun(SparseArray paramSparseArray)
  {
    int j = paramSparseArray.size();
    Object localObject = null;
    long l1 = Long.MAX_VALUE;
    int i = 0;
    while (i < j)
    {
      TrackBundle localTrackBundle = (TrackBundle)paramSparseArray.valueAt(i);
      long l2;
      if (currentTrackRunIndex == fragment.trunCount)
      {
        l2 = l1;
      }
      else
      {
        long l3 = fragment.trunDataPosition[currentTrackRunIndex];
        l2 = l1;
        if (l3 < l1)
        {
          localObject = localTrackBundle;
          l2 = l3;
        }
      }
      i += 1;
      l1 = l2;
    }
    return localObject;
  }
  
  private static TrackBundle getTrackBundle(SparseArray paramSparseArray, int paramInt)
  {
    if (paramSparseArray.size() == 1) {
      return (TrackBundle)paramSparseArray.valueAt(0);
    }
    return (TrackBundle)paramSparseArray.get(paramInt);
  }
  
  private void maybeInitExtraTracks()
  {
    Object localObject = emsgTrackOutputs;
    int k = 0;
    int i;
    if (localObject == null)
    {
      emsgTrackOutputs = new TrackOutput[2];
      if (additionalEmsgTrackOutput != null)
      {
        emsgTrackOutputs[0] = additionalEmsgTrackOutput;
        i = 1;
      }
      else
      {
        i = 0;
      }
      int j = i;
      if ((flags & 0x4) != 0)
      {
        emsgTrackOutputs[i] = extractorOutput.track(trackBundles.size(), 4);
        j = i + 1;
      }
      emsgTrackOutputs = ((TrackOutput[])Arrays.copyOf(emsgTrackOutputs, j));
      localObject = emsgTrackOutputs;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        localObject[i].format(EMSG_FORMAT);
        i += 1;
      }
    }
    if (cea608TrackOutputs == null)
    {
      cea608TrackOutputs = new TrackOutput[closedCaptionFormats.size()];
      i = k;
      while (i < cea608TrackOutputs.length)
      {
        localObject = extractorOutput.track(trackBundles.size() + 1 + i, 3);
        ((TrackOutput)localObject).format((Format)closedCaptionFormats.get(i));
        cea608TrackOutputs[i] = localObject;
        i += 1;
      }
    }
  }
  
  private void onContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    if (type == Atom.TYPE_moov)
    {
      onMoovContainerAtomRead(paramContainerAtom);
      return;
    }
    if (type == Atom.TYPE_moof)
    {
      onMoofContainerAtomRead(paramContainerAtom);
      return;
    }
    if (!containerAtoms.isEmpty()) {
      ((Atom.ContainerAtom)containerAtoms.peek()).add(paramContainerAtom);
    }
  }
  
  private void onEmsgLeafAtomRead(ParsableByteArray paramParsableByteArray)
  {
    if (emsgTrackOutputs != null)
    {
      if (emsgTrackOutputs.length == 0) {
        return;
      }
      paramParsableByteArray.setPosition(12);
      int j = paramParsableByteArray.bytesLeft();
      paramParsableByteArray.readNullTerminatedString();
      paramParsableByteArray.readNullTerminatedString();
      long l1 = paramParsableByteArray.readUnsignedInt();
      l1 = Util.scaleLargeTimestamp(paramParsableByteArray.readUnsignedInt(), 1000000L, l1);
      TrackOutput[] arrayOfTrackOutput = emsgTrackOutputs;
      int k = arrayOfTrackOutput.length;
      int i = 0;
      while (i < k)
      {
        TrackOutput localTrackOutput = arrayOfTrackOutput[i];
        paramParsableByteArray.setPosition(12);
        localTrackOutput.sampleData(paramParsableByteArray, j);
        i += 1;
      }
      if (segmentIndexEarliestPresentationTimeUs != -9223372036854775807L)
      {
        long l2 = segmentIndexEarliestPresentationTimeUs + l1;
        l1 = l2;
        if (timestampAdjuster != null) {
          l1 = timestampAdjuster.adjustSampleTimestamp(l2);
        }
        paramParsableByteArray = emsgTrackOutputs;
        k = paramParsableByteArray.length;
        i = 0;
        while (i < k)
        {
          paramParsableByteArray[i].sampleMetadata(l1, 1, j, 0, null);
          i += 1;
        }
      }
      pendingMetadataSampleInfos.addLast(new MetadataSampleInfo(l1, j));
      pendingMetadataSampleBytes += j;
    }
  }
  
  private void onLeafAtomRead(Atom.LeafAtom paramLeafAtom, long paramLong)
    throws ParserException
  {
    if (!containerAtoms.isEmpty())
    {
      ((Atom.ContainerAtom)containerAtoms.peek()).add(paramLeafAtom);
      return;
    }
    if (type == Atom.TYPE_sidx)
    {
      paramLeafAtom = parseSidx(data, paramLong);
      segmentIndexEarliestPresentationTimeUs = ((Long)first).longValue();
      extractorOutput.seekMap((SeekMap)second);
      haveOutputSeekMap = true;
      return;
    }
    if (type == Atom.TYPE_emsg) {
      onEmsgLeafAtomRead(data);
    }
  }
  
  private void onMoofContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    Object localObject2 = trackBundles;
    Object localObject1 = this;
    parseMoof(paramContainerAtom, (SparseArray)localObject2, flags, extendedTypeScratch);
    if (sideloadedDrmInitData != null) {
      localObject2 = null;
    } else {
      localObject2 = getDrmInitDataFromAtoms(leafChildren);
    }
    int j = 0;
    paramContainerAtom = (Atom.ContainerAtom)localObject1;
    int k;
    int i;
    if (localObject2 != null)
    {
      paramContainerAtom = trackBundles;
      k = paramContainerAtom.size();
      i = 0;
      for (;;)
      {
        paramContainerAtom = (Atom.ContainerAtom)localObject1;
        if (i >= k) {
          break;
        }
        paramContainerAtom = trackBundles;
        ((TrackBundle)paramContainerAtom.valueAt(i)).updateDrmInitData((DrmInitData)localObject2);
        i += 1;
      }
    }
    if (pendingSeekTimeUs != -9223372036854775807L)
    {
      localObject1 = trackBundles;
      k = ((SparseArray)localObject1).size();
      i = j;
      while (i < k)
      {
        localObject1 = trackBundles;
        ((TrackBundle)((SparseArray)localObject1).valueAt(i)).seek(pendingSeekTimeUs);
        i += 1;
      }
      pendingSeekTimeUs = -9223372036854775807L;
    }
  }
  
  private void onMoovContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    Object localObject1 = sideloadedTrack;
    int k = 0;
    int j = 0;
    boolean bool2 = true;
    boolean bool1;
    if (localObject1 == null) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1, "Unexpected moov box.");
    if (sideloadedDrmInitData != null) {
      localObject1 = sideloadedDrmInitData;
    } else {
      localObject1 = getDrmInitDataFromAtoms(leafChildren);
    }
    Object localObject2 = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
    SparseArray localSparseArray = new SparseArray();
    int m = leafChildren.size();
    long l = -9223372036854775807L;
    int i = 0;
    Object localObject3;
    while (i < m)
    {
      localObject3 = (Atom.LeafAtom)leafChildren.get(i);
      if (type == Atom.TYPE_trex)
      {
        localObject3 = parseTrex(data);
        localSparseArray.put(((Integer)first).intValue(), second);
      }
      else if (type == Atom.TYPE_mehd)
      {
        l = parseMehd(data);
      }
      i += 1;
    }
    localObject2 = new SparseArray();
    m = containerChildren.size();
    i = 0;
    while (i < m)
    {
      localObject3 = (Atom.ContainerAtom)containerChildren.get(i);
      if (type == Atom.TYPE_trak)
      {
        Atom.LeafAtom localLeafAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd);
        if ((flags & 0x10) != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        localObject3 = AtomParsers.parseTrak((Atom.ContainerAtom)localObject3, localLeafAtom, l, (DrmInitData)localObject1, bool1, false);
        if (localObject3 != null) {
          ((SparseArray)localObject2).put(id, localObject3);
        }
      }
      i += 1;
    }
    m = ((SparseArray)localObject2).size();
    if (trackBundles.size() == 0)
    {
      i = j;
      while (i < m)
      {
        paramContainerAtom = (Track)((SparseArray)localObject2).valueAt(i);
        localObject1 = new TrackBundle(extractorOutput.track(i, type));
        ((TrackBundle)localObject1).init(paramContainerAtom, getDefaultSampleValues(localSparseArray, id));
        trackBundles.put(id, localObject1);
        durationUs = Math.max(durationUs, durationUs);
        i += 1;
      }
      maybeInitExtraTracks();
      extractorOutput.endTracks();
      return;
    }
    if (trackBundles.size() == m) {
      bool1 = bool2;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1);
    i = k;
    while (i < m)
    {
      paramContainerAtom = (Track)((SparseArray)localObject2).valueAt(i);
      ((TrackBundle)trackBundles.get(id)).init(paramContainerAtom, getDefaultSampleValues(localSparseArray, id));
      i += 1;
    }
  }
  
  private void outputPendingMetadataSamples(long paramLong)
  {
    if (!pendingMetadataSampleInfos.isEmpty())
    {
      MetadataSampleInfo localMetadataSampleInfo = (MetadataSampleInfo)pendingMetadataSampleInfos.removeFirst();
      pendingMetadataSampleBytes -= size;
      long l2 = presentationTimeDeltaUs + paramLong;
      long l1 = l2;
      if (timestampAdjuster != null) {
        l1 = timestampAdjuster.adjustSampleTimestamp(l2);
      }
      TrackOutput[] arrayOfTrackOutput = emsgTrackOutputs;
      int j = arrayOfTrackOutput.length;
      int i = 0;
      while (i < j)
      {
        arrayOfTrackOutput[i].sampleMetadata(l1, 1, size, pendingMetadataSampleBytes, null);
        i += 1;
      }
    }
  }
  
  private static long parseMehd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0) {
      return paramParsableByteArray.readUnsignedInt();
    }
    return paramParsableByteArray.readUnsignedLongToLong();
  }
  
  private static void parseMoof(Atom.ContainerAtom paramContainerAtom, SparseArray paramSparseArray, int paramInt, byte[] paramArrayOfByte)
    throws ParserException
  {
    int j = containerChildren.size();
    int i = 0;
    while (i < j)
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)containerChildren.get(i);
      if (type == Atom.TYPE_traf) {
        parseTraf(localContainerAtom, paramSparseArray, paramInt, paramArrayOfByte);
      }
      i += 1;
    }
  }
  
  private static void parseSaio(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    int i = paramParsableByteArray.readInt();
    if ((Atom.parseFullAtomFlags(i) & 0x1) == 1) {
      paramParsableByteArray.skipBytes(8);
    }
    int j = paramParsableByteArray.readUnsignedIntToInt();
    if (j == 1)
    {
      i = Atom.parseFullAtomVersion(i);
      long l2 = auxiliaryDataPosition;
      long l1;
      if (i == 0) {
        l1 = paramParsableByteArray.readUnsignedInt();
      } else {
        l1 = paramParsableByteArray.readUnsignedLongToLong();
      }
      auxiliaryDataPosition = (l2 + l1);
      return;
    }
    paramParsableByteArray = new StringBuilder();
    paramParsableByteArray.append("Unexpected saio entry count: ");
    paramParsableByteArray.append(j);
    throw new ParserException(paramParsableByteArray.toString());
  }
  
  private static void parseSaiz(TrackEncryptionBox paramTrackEncryptionBox, ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    int m = perSampleIvSize;
    paramParsableByteArray.setPosition(8);
    int i = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    boolean bool = true;
    if ((i & 0x1) == 1) {
      paramParsableByteArray.skipBytes(8);
    }
    i = paramParsableByteArray.readUnsignedByte();
    int n = paramParsableByteArray.readUnsignedIntToInt();
    if (n == sampleCount)
    {
      if (i == 0)
      {
        paramTrackEncryptionBox = sampleHasSubsampleEncryptionTable;
        int j = 0;
        i = 0;
        for (;;)
        {
          k = i;
          if (j >= n) {
            break;
          }
          k = paramParsableByteArray.readUnsignedByte();
          i += k;
          if (k > m) {
            bool = true;
          } else {
            bool = false;
          }
          paramTrackEncryptionBox[j] = bool;
          j += 1;
        }
      }
      if (i <= m) {
        bool = false;
      }
      int k = i * n + 0;
      Arrays.fill(sampleHasSubsampleEncryptionTable, 0, n, bool);
      paramTrackFragment.initEncryptionData(k);
      return;
    }
    paramTrackEncryptionBox = new StringBuilder();
    paramTrackEncryptionBox.append("Length mismatch: ");
    paramTrackEncryptionBox.append(n);
    paramTrackEncryptionBox.append(", ");
    paramTrackEncryptionBox.append(sampleCount);
    throw new ParserException(paramTrackEncryptionBox.toString());
  }
  
  private static void parseSenc(ParsableByteArray paramParsableByteArray, int paramInt, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    if ((paramInt & 0x1) == 0)
    {
      boolean bool;
      if ((paramInt & 0x2) != 0) {
        bool = true;
      } else {
        bool = false;
      }
      paramInt = paramParsableByteArray.readUnsignedIntToInt();
      if (paramInt == sampleCount)
      {
        Arrays.fill(sampleHasSubsampleEncryptionTable, 0, paramInt, bool);
        paramTrackFragment.initEncryptionData(paramParsableByteArray.bytesLeft());
        paramTrackFragment.fillEncryptionData(paramParsableByteArray);
        return;
      }
      paramParsableByteArray = new StringBuilder();
      paramParsableByteArray.append("Length mismatch: ");
      paramParsableByteArray.append(paramInt);
      paramParsableByteArray.append(", ");
      paramParsableByteArray.append(sampleCount);
      throw new ParserException(paramParsableByteArray.toString());
    }
    throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
  }
  
  private static void parseSenc(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    parseSenc(paramParsableByteArray, 0, paramTrackFragment);
  }
  
  private static void parseSgpd(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, String paramString, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray1.setPosition(8);
    int i = paramParsableByteArray1.readInt();
    if (paramParsableByteArray1.readInt() != SAMPLE_GROUP_TYPE_seig) {
      return;
    }
    if (Atom.parseFullAtomVersion(i) == 1) {
      paramParsableByteArray1.skipBytes(4);
    }
    if (paramParsableByteArray1.readInt() == 1)
    {
      paramParsableByteArray2.setPosition(8);
      i = paramParsableByteArray2.readInt();
      if (paramParsableByteArray2.readInt() != SAMPLE_GROUP_TYPE_seig) {
        return;
      }
      i = Atom.parseFullAtomVersion(i);
      if (i == 1)
      {
        if (paramParsableByteArray2.readUnsignedInt() == 0L) {
          throw new ParserException("Variable length description in sgpd found (unsupported)");
        }
      }
      else if (i >= 2) {
        paramParsableByteArray2.skipBytes(4);
      }
      if (paramParsableByteArray2.readUnsignedInt() == 1L)
      {
        paramParsableByteArray2.skipBytes(1);
        i = paramParsableByteArray2.readUnsignedByte();
        boolean bool;
        if (paramParsableByteArray2.readUnsignedByte() == 1) {
          bool = true;
        } else {
          bool = false;
        }
        if (!bool) {
          return;
        }
        int j = paramParsableByteArray2.readUnsignedByte();
        byte[] arrayOfByte = new byte[16];
        paramParsableByteArray2.readBytes(arrayOfByte, 0, arrayOfByte.length);
        if ((bool) && (j == 0))
        {
          int k = paramParsableByteArray2.readUnsignedByte();
          paramParsableByteArray1 = new byte[k];
          paramParsableByteArray2.readBytes(paramParsableByteArray1, 0, k);
        }
        else
        {
          paramParsableByteArray1 = null;
        }
        definesEncryptionData = true;
        trackEncryptionBox = new TrackEncryptionBox(bool, paramString, j, arrayOfByte, (i & 0xF0) >> 4, i & 0xF, paramParsableByteArray1);
        return;
      }
      throw new ParserException("Entry count in sgpd != 1 (unsupported).");
    }
    throw new ParserException("Entry count in sbgp != 1 (unsupported).");
  }
  
  private static Pair parseSidx(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    int i = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    paramParsableByteArray.skipBytes(4);
    long l5 = paramParsableByteArray.readUnsignedInt();
    long l1;
    if (i == 0)
    {
      l2 = paramParsableByteArray.readUnsignedInt();
      l1 = paramLong + paramParsableByteArray.readUnsignedInt();
      paramLong = l2;
    }
    for (;;)
    {
      break;
      l1 = paramParsableByteArray.readUnsignedLongToLong();
      l2 = paramLong + paramParsableByteArray.readUnsignedLongToLong();
      paramLong = l1;
      l1 = l2;
    }
    long l3 = Util.scaleLargeTimestamp(paramLong, 1000000L, l5);
    paramParsableByteArray.skipBytes(2);
    int j = paramParsableByteArray.readUnsignedShort();
    int[] arrayOfInt = new int[j];
    long[] arrayOfLong1 = new long[j];
    long[] arrayOfLong2 = new long[j];
    long[] arrayOfLong3 = new long[j];
    long l2 = l3;
    i = 0;
    while (i < j)
    {
      int k = paramParsableByteArray.readInt();
      if ((k & 0x80000000) == 0)
      {
        long l4 = paramParsableByteArray.readUnsignedInt();
        arrayOfInt[i] = (k & 0x7FFFFFFF);
        arrayOfLong1[i] = l1;
        arrayOfLong3[i] = l2;
        paramLong += l4;
        l4 = Util.scaleLargeTimestamp(paramLong, 1000000L, l5);
        l2 = l4;
        arrayOfLong2[i] = (l4 - arrayOfLong3[i]);
        paramParsableByteArray.skipBytes(4);
        l1 += arrayOfInt[i];
        i += 1;
      }
      else
      {
        throw new ParserException("Unhandled indirect reference");
      }
    }
    return Pair.create(Long.valueOf(l3), new ChunkIndex(arrayOfInt, arrayOfLong1, arrayOfLong2, arrayOfLong3));
  }
  
  private static long parseTfdt(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 1) {
      return paramParsableByteArray.readUnsignedLongToLong();
    }
    return paramParsableByteArray.readUnsignedInt();
  }
  
  private static TrackBundle parseTfhd(ParsableByteArray paramParsableByteArray, SparseArray paramSparseArray)
  {
    paramParsableByteArray.setPosition(8);
    int m = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    paramSparseArray = getTrackBundle(paramSparseArray, paramParsableByteArray.readInt());
    if (paramSparseArray == null) {
      return null;
    }
    if ((m & 0x1) != 0)
    {
      long l = paramParsableByteArray.readUnsignedLongToLong();
      fragment.dataPosition = l;
      fragment.auxiliaryDataPosition = l;
    }
    DefaultSampleValues localDefaultSampleValues = defaultSampleValues;
    int i;
    if ((m & 0x2) != 0) {
      i = paramParsableByteArray.readUnsignedIntToInt() - 1;
    } else {
      i = sampleDescriptionIndex;
    }
    int j;
    if ((m & 0x8) != 0) {
      j = paramParsableByteArray.readUnsignedIntToInt();
    } else {
      j = duration;
    }
    int k;
    if ((m & 0x10) != 0) {
      k = paramParsableByteArray.readUnsignedIntToInt();
    } else {
      k = size;
    }
    if ((m & 0x20) != 0) {
      m = paramParsableByteArray.readUnsignedIntToInt();
    } else {
      m = flags;
    }
    fragment.header = new DefaultSampleValues(i, j, k, m);
    return paramSparseArray;
  }
  
  private static void parseTraf(Atom.ContainerAtom paramContainerAtom, SparseArray paramSparseArray, int paramInt, byte[] paramArrayOfByte)
    throws ParserException
  {
    paramSparseArray = parseTfhd(getLeafAtomOfTypeTYPE_tfhddata, paramSparseArray);
    if (paramSparseArray == null) {
      return;
    }
    TrackFragment localTrackFragment = fragment;
    long l2 = nextFragmentDecodeTime;
    paramSparseArray.reset();
    long l1 = l2;
    if (paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null)
    {
      l1 = l2;
      if ((paramInt & 0x2) == 0) {
        l1 = parseTfdt(getLeafAtomOfTypeTYPE_tfdtdata);
      }
    }
    parseTruns(paramContainerAtom, paramSparseArray, l1, paramInt);
    paramSparseArray = track.getSampleDescriptionEncryptionBox(header.sampleDescriptionIndex);
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
    if (localObject1 != null) {
      parseSaiz(paramSparseArray, data, localTrackFragment);
    }
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saio);
    if (localObject1 != null) {
      parseSaio(data, localTrackFragment);
    }
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_senc);
    if (localObject1 != null) {
      parseSenc(data, localTrackFragment);
    }
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
    Object localObject2 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
    if ((localObject1 != null) && (localObject2 != null))
    {
      localObject1 = data;
      localObject2 = data;
      if (paramSparseArray != null) {
        paramSparseArray = schemeType;
      } else {
        paramSparseArray = null;
      }
      parseSgpd((ParsableByteArray)localObject1, (ParsableByteArray)localObject2, paramSparseArray, localTrackFragment);
    }
    int i = leafChildren.size();
    paramInt = 0;
    while (paramInt < i)
    {
      paramSparseArray = (Atom.LeafAtom)leafChildren.get(paramInt);
      if (type == Atom.TYPE_uuid) {
        parseUuid(data, localTrackFragment, paramArrayOfByte);
      }
      paramInt += 1;
    }
  }
  
  private static Pair parseTrex(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(12);
    return Pair.create(Integer.valueOf(paramParsableByteArray.readInt()), new DefaultSampleValues(paramParsableByteArray.readUnsignedIntToInt() - 1, paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readInt()));
  }
  
  private static int parseTrun(TrackBundle paramTrackBundle, int paramInt1, long paramLong, int paramInt2, ParsableByteArray paramParsableByteArray, int paramInt3)
  {
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    Track localTrack = track;
    paramTrackBundle = fragment;
    DefaultSampleValues localDefaultSampleValues = header;
    trunLength[paramInt1] = paramParsableByteArray.readUnsignedIntToInt();
    trunDataPosition[paramInt1] = dataPosition;
    if ((i1 & 0x1) != 0)
    {
      localObject = trunDataPosition;
      localObject[paramInt1] += paramParsableByteArray.readInt();
    }
    int j;
    if ((i1 & 0x4) != 0) {
      j = 1;
    } else {
      j = 0;
    }
    int i = flags;
    if (j != 0) {
      i = paramParsableByteArray.readUnsignedIntToInt();
    }
    int k;
    if ((i1 & 0x100) != 0) {
      k = 1;
    } else {
      k = 0;
    }
    int m;
    if ((i1 & 0x200) != 0) {
      m = 1;
    } else {
      m = 0;
    }
    int n;
    if ((i1 & 0x400) != 0) {
      n = 1;
    } else {
      n = 0;
    }
    if ((i1 & 0x800) != 0) {
      i1 = 1;
    } else {
      i1 = 0;
    }
    Object localObject = editListDurations;
    long l2 = 0L;
    long l1 = l2;
    if (localObject != null)
    {
      l1 = l2;
      if (editListDurations.length == 1)
      {
        l1 = l2;
        if (editListDurations[0] == 0L) {
          l1 = Util.scaleLargeTimestamp(editListMediaTimes[0], 1000L, timescale);
        }
      }
    }
    localObject = sampleSizeTable;
    int[] arrayOfInt = sampleCompositionTimeOffsetTable;
    long[] arrayOfLong = sampleDecodingTimeTable;
    boolean[] arrayOfBoolean = sampleIsSyncFrameTable;
    if ((type == 2) && ((paramInt2 & 0x1) != 0)) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    int i4 = paramInt3 + trunLength[paramInt1];
    l2 = timescale;
    if (paramInt1 > 0) {
      paramLong = nextFragmentDecodeTime;
    }
    while (paramInt3 < i4)
    {
      int i2;
      if (k != 0) {
        i2 = paramParsableByteArray.readUnsignedIntToInt();
      } else {
        i2 = duration;
      }
      int i3;
      if (m != 0) {
        i3 = paramParsableByteArray.readUnsignedIntToInt();
      } else {
        i3 = size;
      }
      if ((paramInt3 == 0) && (j != 0)) {
        paramInt1 = i;
      } else if (n != 0) {
        paramInt1 = paramParsableByteArray.readInt();
      } else {
        paramInt1 = flags;
      }
      if (i1 != 0) {
        arrayOfInt[paramInt3] = ((int)(paramParsableByteArray.readInt() * 1000L / l2));
      } else {
        arrayOfInt[paramInt3] = 0;
      }
      arrayOfLong[paramInt3] = (Util.scaleLargeTimestamp(paramLong, 1000L, l2) - l1);
      localObject[paramInt3] = i3;
      int i5;
      if (((paramInt1 >> 16 & 0x1) == 0) && ((paramInt2 == 0) || (paramInt3 == 0))) {
        i5 = 1;
      } else {
        i5 = 0;
      }
      arrayOfBoolean[paramInt3] = i5;
      paramLong += i2;
      paramInt3 += 1;
    }
    nextFragmentDecodeTime = paramLong;
    return i4;
  }
  
  private static void parseTruns(Atom.ContainerAtom paramContainerAtom, TrackBundle paramTrackBundle, long paramLong, int paramInt)
  {
    paramContainerAtom = leafChildren;
    int i2 = paramContainerAtom.size();
    int i1 = 0;
    int i = 0;
    int j = 0;
    Object localObject;
    int n;
    int m;
    for (int k = 0; i < i2; k = m)
    {
      localObject = (Atom.LeafAtom)paramContainerAtom.get(i);
      n = j;
      m = k;
      if (type == Atom.TYPE_trun)
      {
        localObject = data;
        ((ParsableByteArray)localObject).setPosition(12);
        int i3 = ((ParsableByteArray)localObject).readUnsignedIntToInt();
        n = j;
        m = k;
        if (i3 > 0)
        {
          m = k + i3;
          n = j + 1;
        }
      }
      i += 1;
      j = n;
    }
    currentTrackRunIndex = 0;
    currentSampleInTrackRun = 0;
    currentSampleIndex = 0;
    fragment.initTables(j, k);
    j = 0;
    k = 0;
    i = i1;
    while (i < i2)
    {
      localObject = (Atom.LeafAtom)paramContainerAtom.get(i);
      n = j;
      m = k;
      if (type == Atom.TYPE_trun)
      {
        m = parseTrun(paramTrackBundle, j, paramLong, paramInt, data, k);
        n = j + 1;
      }
      i += 1;
      j = n;
      k = m;
    }
  }
  
  private static void parseUuid(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment, byte[] paramArrayOfByte)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    paramParsableByteArray.readBytes(paramArrayOfByte, 0, 16);
    if (!Arrays.equals(paramArrayOfByte, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
      return;
    }
    parseSenc(paramParsableByteArray, 16, paramTrackFragment);
  }
  
  private void processAtomEnded(long paramLong)
    throws ParserException
  {
    while ((!containerAtoms.isEmpty()) && (containerAtoms.peek()).endPosition == paramLong)) {
      onContainerAtomRead((Atom.ContainerAtom)containerAtoms.pop());
    }
    enterReadingAtomHeaderState();
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
      l1 = paramExtractorInput.getPosition() - atomHeaderBytesRead;
      if (atomType == Atom.TYPE_moof)
      {
        int j = trackBundles.size();
        int i = 0;
        while (i < j)
        {
          TrackFragment localTrackFragment = trackBundles.valueAt(i)).fragment;
          atomPosition = l1;
          auxiliaryDataPosition = l1;
          dataPosition = l1;
          i += 1;
        }
      }
      if (atomType == Atom.TYPE_mdat)
      {
        currentTrackBundle = null;
        endOfMdatPosition = (atomSize + l1);
        if (!haveOutputSeekMap)
        {
          extractorOutput.seekMap(new SeekMap.Unseekable(durationUs, l1));
          haveOutputSeekMap = true;
        }
        parserState = 2;
        return true;
      }
      if (shouldParseContainerAtom(atomType))
      {
        l1 = paramExtractorInput.getPosition() + atomSize - 8L;
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
        if (atomHeaderBytesRead == 8)
        {
          if (atomSize <= 2147483647L)
          {
            atomData = new ParsableByteArray((int)atomSize);
            System.arraycopy(atomHeader.data, 0, atomData.data, 0, 8);
            parserState = 1;
            return true;
          }
          throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
        }
        throw new ParserException("Leaf atom defines extended atom size (unsupported).");
      }
      if (atomSize <= 2147483647L)
      {
        atomData = null;
        parserState = 1;
        return true;
      }
      throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
    }
    throw new ParserException("Atom size less than header length (unsupported).");
  }
  
  private void readAtomPayload(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = (int)atomSize - atomHeaderBytesRead;
    if (atomData != null)
    {
      paramExtractorInput.readFully(atomData.data, 8, i);
      onLeafAtomRead(new Atom.LeafAtom(atomType, atomData), paramExtractorInput.getPosition());
    }
    else
    {
      paramExtractorInput.skipFully(i);
    }
    processAtomEnded(paramExtractorInput.getPosition());
  }
  
  private void readEncryptionData(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int j = trackBundles.size();
    Object localObject1 = null;
    long l1 = Long.MAX_VALUE;
    int i = 0;
    while (i < j)
    {
      TrackFragment localTrackFragment = trackBundles.valueAt(i)).fragment;
      Object localObject2 = localObject1;
      long l2 = l1;
      if (sampleEncryptionDataNeedsFill)
      {
        localObject2 = localObject1;
        l2 = l1;
        if (auxiliaryDataPosition < l1)
        {
          l2 = auxiliaryDataPosition;
          localObject2 = (TrackBundle)trackBundles.valueAt(i);
        }
      }
      i += 1;
      localObject1 = localObject2;
      l1 = l2;
    }
    if (localObject1 == null)
    {
      parserState = 3;
      return;
    }
    i = (int)(l1 - paramExtractorInput.getPosition());
    if (i >= 0)
    {
      paramExtractorInput.skipFully(i);
      fragment.fillEncryptionData(paramExtractorInput);
      return;
    }
    throw new ParserException("Offset to encryption data was negative.");
  }
  
  private boolean readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:659)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_moof) || (paramInt == Atom.TYPE_traf) || (paramInt == Atom.TYPE_mvex) || (paramInt == Atom.TYPE_edts);
  }
  
  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_sidx) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_tfdt) || (paramInt == Atom.TYPE_tfhd) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_trex) || (paramInt == Atom.TYPE_trun) || (paramInt == Atom.TYPE_pssh) || (paramInt == Atom.TYPE_saiz) || (paramInt == Atom.TYPE_saio) || (paramInt == Atom.TYPE_senc) || (paramInt == Atom.TYPE_uuid) || (paramInt == Atom.TYPE_sbgp) || (paramInt == Atom.TYPE_sgpd) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_mehd) || (paramInt == Atom.TYPE_emsg);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
    if (sideloadedTrack != null)
    {
      paramExtractorOutput = new TrackBundle(paramExtractorOutput.track(0, sideloadedTrack.type));
      paramExtractorOutput.init(sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
      trackBundles.put(0, paramExtractorOutput);
      maybeInitExtraTracks();
      extractorOutput.endTracks();
    }
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    do
    {
      for (;;)
      {
        switch (parserState)
        {
        default: 
          if (readSample(paramExtractorInput)) {
            return 0;
          }
          break;
        case 2: 
          readEncryptionData(paramExtractorInput);
          break;
        case 1: 
          readAtomPayload(paramExtractorInput);
        }
      }
    } while (readAtomHeader(paramExtractorInput));
    return -1;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    int j = trackBundles.size();
    int i = 0;
    while (i < j)
    {
      ((TrackBundle)trackBundles.valueAt(i)).reset();
      i += 1;
    }
    pendingMetadataSampleInfos.clear();
    pendingMetadataSampleBytes = 0;
    pendingSeekTimeUs = paramLong2;
    containerAtoms.clear();
    enterReadingAtomHeaderState();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return Sniffer.sniffFragmented(paramExtractorInput);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
  
  final class MetadataSampleInfo
  {
    public final long presentationTimeDeltaUs;
    public final int size;
    
    public MetadataSampleInfo(int paramInt)
    {
      presentationTimeDeltaUs = this$1;
      size = paramInt;
    }
  }
  
  final class TrackBundle
  {
    public int currentSampleInTrackRun;
    public int currentSampleIndex;
    public int currentTrackRunIndex;
    private final ParsableByteArray defaultInitializationVector = new ParsableByteArray();
    public DefaultSampleValues defaultSampleValues;
    private final ParsableByteArray encryptionSignalByte = new ParsableByteArray(1);
    public int firstSampleToOutputIndex;
    public final TrackFragment fragment = new TrackFragment();
    public Track track;
    
    public TrackBundle() {}
    
    private TrackEncryptionBox getEncryptionBoxIfEncrypted()
    {
      int i = fragment.header.sampleDescriptionIndex;
      TrackEncryptionBox localTrackEncryptionBox;
      if (fragment.trackEncryptionBox != null) {
        localTrackEncryptionBox = fragment.trackEncryptionBox;
      } else {
        localTrackEncryptionBox = track.getSampleDescriptionEncryptionBox(i);
      }
      if ((localTrackEncryptionBox != null) && (isEncrypted)) {
        return localTrackEncryptionBox;
      }
      return null;
    }
    
    private void skipSampleEncryptionData()
    {
      TrackEncryptionBox localTrackEncryptionBox = getEncryptionBoxIfEncrypted();
      if (localTrackEncryptionBox == null) {
        return;
      }
      ParsableByteArray localParsableByteArray = fragment.sampleEncryptionData;
      if (perSampleIvSize != 0) {
        localParsableByteArray.skipBytes(perSampleIvSize);
      }
      if (fragment.sampleHasSubsampleEncryptionTable(currentSampleIndex)) {
        localParsableByteArray.skipBytes(localParsableByteArray.readUnsignedShort() * 6);
      }
    }
    
    public void init(Track paramTrack, DefaultSampleValues paramDefaultSampleValues)
    {
      track = ((Track)Assertions.checkNotNull(paramTrack));
      defaultSampleValues = ((DefaultSampleValues)Assertions.checkNotNull(paramDefaultSampleValues));
      format(format);
      reset();
    }
    
    public boolean next()
    {
      currentSampleIndex += 1;
      currentSampleInTrackRun += 1;
      if (currentSampleInTrackRun == fragment.trunLength[currentTrackRunIndex])
      {
        currentTrackRunIndex += 1;
        currentSampleInTrackRun = 0;
        return false;
      }
      return true;
    }
    
    public int outputSampleEncryptionData()
    {
      Object localObject = getEncryptionBoxIfEncrypted();
      if (localObject == null) {
        return 0;
      }
      int i;
      if (perSampleIvSize != 0)
      {
        localParsableByteArray = fragment.sampleEncryptionData;
        i = perSampleIvSize;
      }
      else
      {
        localObject = defaultInitializationVector;
        defaultInitializationVector.reset((byte[])localObject, localObject.length);
        localParsableByteArray = defaultInitializationVector;
        i = localObject.length;
      }
      boolean bool = fragment.sampleHasSubsampleEncryptionTable(currentSampleIndex);
      localObject = encryptionSignalByte.data;
      if (bool) {
        j = 128;
      } else {
        j = 0;
      }
      localObject[0] = ((byte)(j | i));
      encryptionSignalByte.setPosition(0);
      sampleData(encryptionSignalByte, 1);
      sampleData(localParsableByteArray, i);
      if (!bool) {
        return i + 1;
      }
      ParsableByteArray localParsableByteArray = fragment.sampleEncryptionData;
      int j = localParsableByteArray.readUnsignedShort();
      localParsableByteArray.skipBytes(-2);
      j = j * 6 + 2;
      sampleData(localParsableByteArray, j);
      return i + 1 + j;
    }
    
    public void reset()
    {
      fragment.reset();
      currentSampleIndex = 0;
      currentTrackRunIndex = 0;
      currentSampleInTrackRun = 0;
      firstSampleToOutputIndex = 0;
    }
    
    public void seek(long paramLong)
    {
      paramLong = IpAddress.usToMs(paramLong);
      int i = currentSampleIndex;
      while ((i < fragment.sampleCount) && (fragment.getSamplePresentationTime(i) < paramLong))
      {
        if (fragment.sampleIsSyncFrameTable[i] != 0) {
          firstSampleToOutputIndex = i;
        }
        i += 1;
      }
    }
    
    public void updateDrmInitData(DrmInitData paramDrmInitData)
    {
      Object localObject = track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
      if (localObject != null) {
        localObject = schemeType;
      } else {
        localObject = null;
      }
      format(track.format.copyWithDrmInitData(paramDrmInitData.copyWithSchemeType((String)localObject)));
    }
  }
}
