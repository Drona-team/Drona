package com.google.android.exoplayer2.extractor.webm;

import android.util.Pair;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class MatroskaExtractor
  implements Extractor
{
  private static final int BLOCK_STATE_DATA = 2;
  private static final int BLOCK_STATE_HEADER = 1;
  private static final int BLOCK_STATE_START = 0;
  private static final String CODEC_ID_AAC = "A_AAC";
  private static final String CODEC_ID_AC3 = "A_AC3";
  private static final String CODEC_ID_ACM = "A_MS/ACM";
  private static final String CODEC_ID_ASS = "S_TEXT/ASS";
  private static final String CODEC_ID_DTS = "A_DTS";
  private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
  private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
  private static final String CODEC_ID_DVBSUB = "S_DVBSUB";
  private static final String CODEC_ID_E_AC3 = "A_EAC3";
  private static final String CODEC_ID_FLAC = "A_FLAC";
  private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
  private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
  private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
  private static final String CODEC_ID_MP2 = "A_MPEG/L2";
  private static final String CODEC_ID_MP3 = "A_MPEG/L3";
  private static final String CODEC_ID_MPEG2 = "V_MPEG2";
  private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
  private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
  private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
  private static final String CODEC_ID_OPUS = "A_OPUS";
  private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
  private static final String CODEC_ID_PGS = "S_HDMV/PGS";
  private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
  private static final String CODEC_ID_THEORA = "V_THEORA";
  private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
  private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
  private static final String CODEC_ID_VORBIS = "A_VORBIS";
  private static final String CODEC_ID_VP8 = "V_VP8";
  private static final String CODEC_ID_VP9 = "V_VP9";
  private static final String DOC_TYPE_MATROSKA = "matroska";
  private static final String DOC_TYPE_WEBM = "webm";
  private static final int ENCRYPTION_IV_SIZE = 8;
  public static final ExtractorsFactory FACTORY = -..Lambda.MatroskaExtractor.jNXW0tyYIOPE6N2jicocV6rRvBs.INSTANCE;
  public static final int FLAG_DISABLE_SEEK_FOR_CUES = 1;
  private static final int FOURCC_COMPRESSION_DIVX = 1482049860;
  private static final int FOURCC_COMPRESSION_VC1 = 826496599;
  private static final int ID_AUDIO = 225;
  private static final int ID_AUDIO_BIT_DEPTH = 25188;
  private static final int ID_BLOCK = 161;
  private static final int ID_BLOCK_DURATION = 155;
  private static final int ID_BLOCK_GROUP = 160;
  private static final int ID_CHANNELS = 159;
  private static final int ID_CLUSTER = 524531317;
  private static final int ID_CODEC_DELAY = 22186;
  private static final int ID_CODEC_ID = 134;
  private static final int ID_CODEC_PRIVATE = 25506;
  private static final int ID_COLOUR = 21936;
  private static final int ID_COLOUR_PRIMARIES = 21947;
  private static final int ID_COLOUR_RANGE = 21945;
  private static final int ID_COLOUR_TRANSFER = 21946;
  private static final int ID_CONTENT_COMPRESSION = 20532;
  private static final int ID_CONTENT_COMPRESSION_ALGORITHM = 16980;
  private static final int ID_CONTENT_COMPRESSION_SETTINGS = 16981;
  private static final int ID_CONTENT_ENCODING = 25152;
  private static final int ID_CONTENT_ENCODINGS = 28032;
  private static final int ID_CONTENT_ENCODING_ORDER = 20529;
  private static final int ID_CONTENT_ENCODING_SCOPE = 20530;
  private static final int ID_CONTENT_ENCRYPTION = 20533;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 18407;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 18408;
  private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 18401;
  private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 18402;
  private static final int ID_CUES = 475249515;
  private static final int ID_CUE_CLUSTER_POSITION = 241;
  private static final int ID_CUE_POINT = 187;
  private static final int ID_CUE_TIME = 179;
  private static final int ID_CUE_TRACK_POSITIONS = 183;
  private static final int ID_DEFAULT_DURATION = 2352003;
  private static final int ID_DISPLAY_HEIGHT = 21690;
  private static final int ID_DISPLAY_UNIT = 21682;
  private static final int ID_DISPLAY_WIDTH = 21680;
  private static final int ID_DOC_TYPE = 17026;
  private static final int ID_DOC_TYPE_READ_VERSION = 17029;
  private static final int ID_DURATION = 17545;
  private static final int ID_EBML = 440786851;
  private static final int ID_EBML_READ_VERSION = 17143;
  private static final int ID_FLAG_DEFAULT = 136;
  private static final int ID_FLAG_FORCED = 21930;
  private static final int ID_INFO = 357149030;
  private static final int ID_LANGUAGE = 2274716;
  private static final int ID_LUMNINANCE_MAX = 21977;
  private static final int ID_LUMNINANCE_MIN = 21978;
  private static final int ID_MASTERING_METADATA = 21968;
  private static final int ID_MAX_CLL = 21948;
  private static final int ID_MAX_FALL = 21949;
  private static final int ID_NAME = 21358;
  private static final int ID_PIXEL_HEIGHT = 186;
  private static final int ID_PIXEL_WIDTH = 176;
  private static final int ID_PRIMARY_B_CHROMATICITY_X = 21973;
  private static final int ID_PRIMARY_B_CHROMATICITY_Y = 21974;
  private static final int ID_PRIMARY_G_CHROMATICITY_X = 21971;
  private static final int ID_PRIMARY_G_CHROMATICITY_Y = 21972;
  private static final int ID_PRIMARY_R_CHROMATICITY_X = 21969;
  private static final int ID_PRIMARY_R_CHROMATICITY_Y = 21970;
  private static final int ID_PROJECTION = 30320;
  private static final int ID_PROJECTION_PRIVATE = 30322;
  private static final int ID_REFERENCE_BLOCK = 251;
  private static final int ID_SAMPLING_FREQUENCY = 181;
  private static final int ID_SEEK = 19899;
  private static final int ID_SEEK_HEAD = 290298740;
  private static final int ID_SEEK_ID = 21419;
  private static final int ID_SEEK_POSITION = 21420;
  private static final int ID_SEEK_PRE_ROLL = 22203;
  private static final int ID_SEGMENT = 408125543;
  private static final int ID_SEGMENT_INFO = 357149030;
  private static final int ID_SIMPLE_BLOCK = 163;
  private static final int ID_STEREO_MODE = 21432;
  private static final int ID_TIMECODE_SCALE = 2807729;
  private static final int ID_TIME_CODE = 231;
  private static final int ID_TRACKS = 374648427;
  private static final int ID_TRACK_ENTRY = 174;
  private static final int ID_TRACK_NUMBER = 215;
  private static final int ID_TRACK_TYPE = 131;
  private static final int ID_VIDEO = 224;
  private static final int ID_WHITE_POINT_CHROMATICITY_X = 21975;
  private static final int ID_WHITE_POINT_CHROMATICITY_Y = 21976;
  private static final int LACING_EBML = 3;
  private static final int LACING_FIXED_SIZE = 2;
  private static final int LACING_NONE = 0;
  private static final int LACING_XIPH = 1;
  private static final int OPUS_MAX_INPUT_SIZE = 5760;
  private static final String PAGE_KEY = "MatroskaExtractor";
  private static final byte[] SSA_DIALOGUE_FORMAT = Util.getUtf8Bytes("Format: Start, End, ReadOrder, Layer, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
  private static final byte[] SSA_PREFIX = { 68, 105, 97, 108, 111, 103, 117, 101, 58, 32, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 58, 48, 48, 58, 48, 48, 58, 48, 48, 44 };
  private static final int SSA_PREFIX_END_TIMECODE_OFFSET = 21;
  private static final byte[] SSA_TIMECODE_EMPTY = { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
  private static final String SSA_TIMECODE_FORMAT = "%01d:%02d:%02d:%02d";
  private static final long SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR = 10000L;
  private static final byte[] SUBRIP_PREFIX = { 49, 10, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 32, 45, 45, 62, 32, 48, 48, 58, 48, 48, 58, 48, 48, 44, 48, 48, 48, 10 };
  private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
  private static final byte[] SUBRIP_TIMECODE_EMPTY = { 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
  private static final String SUBRIP_TIMECODE_FORMAT = "%02d:%02d:%02d,%03d";
  private static final long SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR = 1000L;
  private static final int TRACK_TYPE_AUDIO = 2;
  private static final int UNSET_ENTRY_ID = -1;
  private static final int VORBIS_MAX_INPUT_SIZE = 8192;
  private static final int WAVE_FORMAT_EXTENSIBLE = 65534;
  private static final int WAVE_FORMAT_PCM = 1;
  private static final int WAVE_FORMAT_SIZE = 18;
  private static final UUID WAVE_SUBFORMAT_PCM = new UUID(72057594037932032L, -9223371306706625679L);
  private long blockDurationUs;
  private int blockFlags;
  private int blockLacingSampleCount;
  private int blockLacingSampleIndex;
  private int[] blockLacingSampleSizes;
  private int blockState;
  private long blockTimeUs;
  private int blockTrackNumber;
  private int blockTrackNumberLength;
  private long clusterTimecodeUs = -9223372036854775807L;
  private LongArray cueClusterPositions;
  private LongArray cueTimesUs;
  private long cuesContentPosition = -1L;
  private Track currentTrack;
  private long durationTimecode = -9223372036854775807L;
  private long durationUs = -9223372036854775807L;
  private final ParsableByteArray encryptionInitializationVector;
  private final ParsableByteArray encryptionSubsampleData;
  private ByteBuffer encryptionSubsampleDataBuffer;
  private ExtractorOutput extractorOutput;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private final EbmlReader reader;
  private int sampleBytesRead;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private boolean sampleEncodingHandled;
  private boolean sampleInitializationVectorRead;
  private int samplePartitionCount;
  private boolean samplePartitionCountRead;
  private boolean sampleRead;
  private boolean sampleSeenReferenceBlock;
  private byte sampleSignalByte;
  private boolean sampleSignalByteRead;
  private final ParsableByteArray sampleStrippedBytes;
  private final ParsableByteArray scratch;
  private int seekEntryId;
  private final ParsableByteArray seekEntryIdBytes;
  private long seekEntryPosition;
  private boolean seekForCues;
  private final boolean seekForCuesEnabled;
  private long seekPositionAfterBuildingCues = -1L;
  private boolean seenClusterPositionForCurrentCuePoint;
  private long segmentContentPosition = -1L;
  private long segmentContentSize;
  private boolean sentSeekMap;
  private final ParsableByteArray subtitleSample;
  private long timecodeScale = -9223372036854775807L;
  private final SparseArray<com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.Track> tracks;
  private final VarintReader varintReader;
  private final ParsableByteArray vorbisNumPageSamples;
  
  public MatroskaExtractor()
  {
    this(0);
  }
  
  public MatroskaExtractor(int paramInt)
  {
    this(new DefaultEbmlReader(), paramInt);
  }
  
  MatroskaExtractor(EbmlReader paramEbmlReader, int paramInt)
  {
    reader = paramEbmlReader;
    reader.init(new InnerEbmlReaderOutput(null));
    boolean bool = true;
    if ((paramInt & 0x1) != 0) {
      bool = false;
    }
    seekForCuesEnabled = bool;
    varintReader = new VarintReader();
    tracks = new SparseArray();
    scratch = new ParsableByteArray(4);
    vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(-1).array());
    seekEntryIdBytes = new ParsableByteArray(4);
    nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    nalLength = new ParsableByteArray(4);
    sampleStrippedBytes = new ParsableByteArray();
    subtitleSample = new ParsableByteArray();
    encryptionInitializationVector = new ParsableByteArray(8);
    encryptionSubsampleData = new ParsableByteArray();
  }
  
  private SeekMap buildSeekMap()
  {
    if ((segmentContentPosition != -1L) && (durationUs != -9223372036854775807L) && (cueTimesUs != null) && (cueTimesUs.size() != 0) && (cueClusterPositions != null) && (cueClusterPositions.size() == cueTimesUs.size()))
    {
      int m = cueTimesUs.size();
      int[] arrayOfInt = new int[m];
      long[] arrayOfLong1 = new long[m];
      long[] arrayOfLong2 = new long[m];
      long[] arrayOfLong3 = new long[m];
      int k = 0;
      int j = 0;
      int i;
      for (;;)
      {
        i = k;
        if (j >= m) {
          break;
        }
        arrayOfLong3[j] = cueTimesUs.get(j);
        arrayOfLong1[j] = (segmentContentPosition + cueClusterPositions.get(j));
        j += 1;
      }
      for (;;)
      {
        j = m - 1;
        if (i >= j) {
          break;
        }
        j = i + 1;
        arrayOfInt[i] = ((int)(arrayOfLong1[j] - arrayOfLong1[i]));
        arrayOfLong3[j] -= arrayOfLong3[i];
        i = j;
      }
      arrayOfInt[j] = ((int)(segmentContentPosition + segmentContentSize - arrayOfLong1[j]));
      arrayOfLong2[j] = (durationUs - arrayOfLong3[j]);
      cueTimesUs = null;
      cueClusterPositions = null;
      return new ChunkIndex(arrayOfInt, arrayOfLong1, arrayOfLong2, arrayOfLong3);
    }
    cueTimesUs = null;
    cueClusterPositions = null;
    return new SeekMap.Unseekable(durationUs);
  }
  
  private void commitSampleToOutput(Track paramTrack, long paramLong)
  {
    if (trueHdSampleRechunker != null)
    {
      trueHdSampleRechunker.sampleMetadata(paramTrack, paramLong);
    }
    else
    {
      if ("S_TEXT/UTF8".equals(codecId)) {
        commitSubtitleSample(paramTrack, "%02d:%02d:%02d,%03d", 19, 1000L, SUBRIP_TIMECODE_EMPTY);
      } else if ("S_TEXT/ASS".equals(codecId)) {
        commitSubtitleSample(paramTrack, "%01d:%02d:%02d:%02d", 21, 10000L, SSA_TIMECODE_EMPTY);
      }
      output.sampleMetadata(paramLong, blockFlags, sampleBytesWritten, 0, cryptoData);
    }
    sampleRead = true;
    resetSample();
  }
  
  private void commitSubtitleSample(Track paramTrack, String paramString, int paramInt, long paramLong, byte[] paramArrayOfByte)
  {
    setSampleDuration(subtitleSample.data, blockDurationUs, paramString, paramInt, paramLong, paramArrayOfByte);
    output.sampleData(subtitleSample, subtitleSample.limit());
    sampleBytesWritten += subtitleSample.limit();
  }
  
  private static int[] ensureArrayCapacity(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt == null) {
      return new int[paramInt];
    }
    if (paramArrayOfInt.length >= paramInt) {
      return paramArrayOfInt;
    }
    return new int[Math.max(paramArrayOfInt.length * 2, paramInt)];
  }
  
  private static boolean isCodecSupported(String paramString)
  {
    return ("V_VP8".equals(paramString)) || ("V_VP9".equals(paramString)) || ("V_MPEG2".equals(paramString)) || ("V_MPEG4/ISO/SP".equals(paramString)) || ("V_MPEG4/ISO/ASP".equals(paramString)) || ("V_MPEG4/ISO/AP".equals(paramString)) || ("V_MPEG4/ISO/AVC".equals(paramString)) || ("V_MPEGH/ISO/HEVC".equals(paramString)) || ("V_MS/VFW/FOURCC".equals(paramString)) || ("V_THEORA".equals(paramString)) || ("A_OPUS".equals(paramString)) || ("A_VORBIS".equals(paramString)) || ("A_AAC".equals(paramString)) || ("A_MPEG/L2".equals(paramString)) || ("A_MPEG/L3".equals(paramString)) || ("A_AC3".equals(paramString)) || ("A_EAC3".equals(paramString)) || ("A_TRUEHD".equals(paramString)) || ("A_DTS".equals(paramString)) || ("A_DTS/EXPRESS".equals(paramString)) || ("A_DTS/LOSSLESS".equals(paramString)) || ("A_FLAC".equals(paramString)) || ("A_MS/ACM".equals(paramString)) || ("A_PCM/INT/LIT".equals(paramString)) || ("S_TEXT/UTF8".equals(paramString)) || ("S_TEXT/ASS".equals(paramString)) || ("S_VOBSUB".equals(paramString)) || ("S_HDMV/PGS".equals(paramString)) || ("S_DVBSUB".equals(paramString));
  }
  
  private boolean maybeSeekForCues(PositionHolder paramPositionHolder, long paramLong)
  {
    if (seekForCues)
    {
      seekPositionAfterBuildingCues = paramLong;
      position = cuesContentPosition;
      seekForCues = false;
      return true;
    }
    if ((sentSeekMap) && (seekPositionAfterBuildingCues != -1L))
    {
      position = seekPositionAfterBuildingCues;
      seekPositionAfterBuildingCues = -1L;
      return true;
    }
    return false;
  }
  
  private void readScratch(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    if (scratch.limit() >= paramInt) {
      return;
    }
    if (scratch.capacity() < paramInt) {
      scratch.reset(Arrays.copyOf(scratch.data, Math.max(scratch.data.length * 2, paramInt)), scratch.limit());
    }
    paramExtractorInput.readFully(scratch.data, scratch.limit(), paramInt - scratch.limit());
    scratch.setLimit(paramInt);
  }
  
  private int readToOutput(ExtractorInput paramExtractorInput, TrackOutput paramTrackOutput, int paramInt)
    throws IOException, InterruptedException
  {
    int i = sampleStrippedBytes.bytesLeft();
    if (i > 0)
    {
      i = Math.min(paramInt, i);
      paramInt = i;
      paramTrackOutput.sampleData(sampleStrippedBytes, i);
    }
    else
    {
      paramInt = paramTrackOutput.sampleData(paramExtractorInput, paramInt, false);
    }
    sampleBytesRead += paramInt;
    sampleBytesWritten += paramInt;
    return paramInt;
  }
  
  private void readToTarget(ExtractorInput paramExtractorInput, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    int i = Math.min(paramInt2, sampleStrippedBytes.bytesLeft());
    paramExtractorInput.readFully(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
    if (i > 0) {
      sampleStrippedBytes.readBytes(paramArrayOfByte, paramInt1, i);
    }
    sampleBytesRead += paramInt2;
  }
  
  private void resetSample()
  {
    sampleBytesRead = 0;
    sampleBytesWritten = 0;
    sampleCurrentNalBytesRemaining = 0;
    sampleEncodingHandled = false;
    sampleSignalByteRead = false;
    samplePartitionCountRead = false;
    samplePartitionCount = 0;
    sampleSignalByte = 0;
    sampleInitializationVectorRead = false;
    sampleStrippedBytes.reset();
  }
  
  private long scaleTimecodeToUs(long paramLong)
    throws ParserException
  {
    if (timecodeScale != -9223372036854775807L) {
      return Util.scaleLargeTimestamp(paramLong, timecodeScale, 1000L);
    }
    throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
  }
  
  private static void setSampleDuration(byte[] paramArrayOfByte1, long paramLong1, String paramString, int paramInt, long paramLong2, byte[] paramArrayOfByte2)
  {
    if (paramLong1 == -9223372036854775807L)
    {
      paramString = paramArrayOfByte2;
    }
    else
    {
      int i = (int)(paramLong1 / 3600000000L);
      paramLong1 -= i * 3600 * 1000000L;
      int j = (int)(paramLong1 / 60000000L);
      paramLong1 -= j * 60 * 1000000L;
      int k = (int)(paramLong1 / 1000000L);
      int m = (int)((paramLong1 - k * 1000000L) / paramLong2);
      paramString = Util.getUtf8Bytes(String.format(Locale.US, paramString, new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) }));
    }
    System.arraycopy(paramString, 0, paramArrayOfByte1, paramInt, paramArrayOfByte2.length);
  }
  
  private void writeSampleData(ExtractorInput paramExtractorInput, Track paramTrack, int paramInt)
    throws IOException, InterruptedException
  {
    if ("S_TEXT/UTF8".equals(codecId))
    {
      writeSubtitleSampleData(paramExtractorInput, SUBRIP_PREFIX, paramInt);
      return;
    }
    if ("S_TEXT/ASS".equals(codecId))
    {
      writeSubtitleSampleData(paramExtractorInput, SSA_PREFIX, paramInt);
      return;
    }
    TrackOutput localTrackOutput = output;
    boolean bool2 = sampleEncodingHandled;
    boolean bool1 = true;
    int j;
    int i;
    byte[] arrayOfByte;
    if (!bool2)
    {
      if (hasContentEncryption)
      {
        blockFlags &= 0xBFFFFFFF;
        bool2 = sampleSignalByteRead;
        j = 128;
        if (!bool2)
        {
          paramExtractorInput.readFully(scratch.data, 0, 1);
          sampleBytesRead += 1;
          if ((scratch.data[0] & 0x80) != 128)
          {
            sampleSignalByte = scratch.data[0];
            sampleSignalByteRead = true;
          }
          else
          {
            throw new ParserException("Extension bit is set in signal byte");
          }
        }
        if ((sampleSignalByte & 0x1) == 1) {
          i = 1;
        } else {
          i = 0;
        }
        if (i != 0)
        {
          if ((sampleSignalByte & 0x2) == 2) {
            i = 1;
          } else {
            i = 0;
          }
          blockFlags |= 0x40000000;
          if (!sampleInitializationVectorRead)
          {
            paramExtractorInput.readFully(encryptionInitializationVector.data, 0, 8);
            sampleBytesRead += 8;
            sampleInitializationVectorRead = true;
            arrayOfByte = scratch.data;
            if (i == 0) {
              j = 0;
            }
            arrayOfByte[0] = ((byte)(j | 0x8));
            scratch.setPosition(0);
            localTrackOutput.sampleData(scratch, 1);
            sampleBytesWritten += 1;
            encryptionInitializationVector.setPosition(0);
            localTrackOutput.sampleData(encryptionInitializationVector, 8);
            sampleBytesWritten += 8;
          }
          if (i != 0)
          {
            if (!samplePartitionCountRead)
            {
              paramExtractorInput.readFully(scratch.data, 0, 1);
              sampleBytesRead += 1;
              scratch.setPosition(0);
              samplePartitionCount = scratch.readUnsignedByte();
              samplePartitionCountRead = true;
            }
            i = samplePartitionCount * 4;
            scratch.reset(i);
            paramExtractorInput.readFully(scratch.data, 0, i);
            sampleBytesRead += i;
            short s = (short)(samplePartitionCount / 2 + 1);
            int m = s * 6 + 2;
            if ((encryptionSubsampleDataBuffer == null) || (encryptionSubsampleDataBuffer.capacity() < m)) {
              encryptionSubsampleDataBuffer = ByteBuffer.allocate(m);
            }
            encryptionSubsampleDataBuffer.position(0);
            encryptionSubsampleDataBuffer.putShort(s);
            i = 0;
            int k;
            for (j = 0; i < samplePartitionCount; j = k)
            {
              k = scratch.readUnsignedIntToInt();
              if (i % 2 == 0) {
                encryptionSubsampleDataBuffer.putShort((short)(k - j));
              } else {
                encryptionSubsampleDataBuffer.putInt(k - j);
              }
              i += 1;
            }
            i = paramInt - sampleBytesRead - j;
            if (samplePartitionCount % 2 == 1)
            {
              encryptionSubsampleDataBuffer.putInt(i);
            }
            else
            {
              encryptionSubsampleDataBuffer.putShort((short)i);
              encryptionSubsampleDataBuffer.putInt(0);
            }
            encryptionSubsampleData.reset(encryptionSubsampleDataBuffer.array(), m);
            localTrackOutput.sampleData(encryptionSubsampleData, m);
            sampleBytesWritten += m;
          }
        }
      }
      else if (sampleStrippedBytes != null)
      {
        sampleStrippedBytes.reset(sampleStrippedBytes, sampleStrippedBytes.length);
      }
      sampleEncodingHandled = true;
    }
    paramInt += sampleStrippedBytes.limit();
    if ((!"V_MPEG4/ISO/AVC".equals(codecId)) && (!"V_MPEGH/ISO/HEVC".equals(codecId))) {
      if (trueHdSampleRechunker != null)
      {
        if (sampleStrippedBytes.limit() != 0) {
          bool1 = false;
        }
        Assertions.checkState(bool1);
        trueHdSampleRechunker.startSample(paramExtractorInput, blockFlags, paramInt);
      }
    }
    while (sampleBytesRead < paramInt)
    {
      readToOutput(paramExtractorInput, localTrackOutput, paramInt - sampleBytesRead);
      continue;
      arrayOfByte = nalLength.data;
      arrayOfByte[0] = 0;
      arrayOfByte[1] = 0;
      arrayOfByte[2] = 0;
      i = nalUnitLengthFieldLength;
      j = nalUnitLengthFieldLength;
      while (sampleBytesRead < paramInt) {
        if (sampleCurrentNalBytesRemaining == 0)
        {
          readToTarget(paramExtractorInput, arrayOfByte, 4 - j, i);
          nalLength.setPosition(0);
          sampleCurrentNalBytesRemaining = nalLength.readUnsignedIntToInt();
          nalStartCode.setPosition(0);
          localTrackOutput.sampleData(nalStartCode, 4);
          sampleBytesWritten += 4;
        }
        else
        {
          sampleCurrentNalBytesRemaining -= readToOutput(paramExtractorInput, localTrackOutput, sampleCurrentNalBytesRemaining);
        }
      }
    }
    if ("A_VORBIS".equals(codecId))
    {
      vorbisNumPageSamples.setPosition(0);
      localTrackOutput.sampleData(vorbisNumPageSamples, 4);
      sampleBytesWritten += 4;
    }
  }
  
  private void writeSubtitleSampleData(ExtractorInput paramExtractorInput, byte[] paramArrayOfByte, int paramInt)
    throws IOException, InterruptedException
  {
    int i = paramArrayOfByte.length + paramInt;
    if (subtitleSample.capacity() < i) {
      subtitleSample.data = Arrays.copyOf(paramArrayOfByte, i + paramInt);
    } else {
      System.arraycopy(paramArrayOfByte, 0, subtitleSample.data, 0, paramArrayOfByte.length);
    }
    paramExtractorInput.readFully(subtitleSample.data, paramArrayOfByte.length, paramInt);
    subtitleSample.reset(i);
  }
  
  void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if ((paramInt1 != 161) && (paramInt1 != 163))
    {
      if (paramInt1 != 16981)
      {
        if (paramInt1 != 18402)
        {
          if (paramInt1 != 21419)
          {
            if (paramInt1 != 25506)
            {
              if (paramInt1 == 30322)
              {
                currentTrack.projectionData = new byte[paramInt2];
                paramExtractorInput.readFully(currentTrack.projectionData, 0, paramInt2);
                return;
              }
              paramExtractorInput = new StringBuilder();
              paramExtractorInput.append("Unexpected id: ");
              paramExtractorInput.append(paramInt1);
              throw new ParserException(paramExtractorInput.toString());
            }
            currentTrack.codecPrivate = new byte[paramInt2];
            paramExtractorInput.readFully(currentTrack.codecPrivate, 0, paramInt2);
            return;
          }
          Arrays.fill(seekEntryIdBytes.data, (byte)0);
          paramExtractorInput.readFully(seekEntryIdBytes.data, 4 - paramInt2, paramInt2);
          seekEntryIdBytes.setPosition(0);
          seekEntryId = ((int)seekEntryIdBytes.readUnsignedInt());
          return;
        }
        localObject1 = new byte[paramInt2];
        paramExtractorInput.readFully((byte[])localObject1, 0, paramInt2);
        currentTrack.cryptoData = new TrackOutput.CryptoData(1, (byte[])localObject1, 0, 0);
        return;
      }
      currentTrack.sampleStrippedBytes = new byte[paramInt2];
      paramExtractorInput.readFully(currentTrack.sampleStrippedBytes, 0, paramInt2);
      return;
    }
    if (blockState == 0)
    {
      blockTrackNumber = ((int)varintReader.readUnsignedVarint(paramExtractorInput, false, true, 8));
      blockTrackNumberLength = varintReader.getLastLength();
      blockDurationUs = -9223372036854775807L;
      blockState = 1;
      scratch.reset();
    }
    Object localObject1 = (Track)tracks.get(blockTrackNumber);
    if (localObject1 == null)
    {
      paramExtractorInput.skipFully(paramInt2 - blockTrackNumberLength);
      blockState = 0;
      return;
    }
    if (blockState == 1)
    {
      readScratch(paramExtractorInput, 3);
      int i = (scratch.data[2] & 0x6) >> 1;
      if (i == 0)
      {
        blockLacingSampleCount = 1;
        blockLacingSampleSizes = ensureArrayCapacity(blockLacingSampleSizes, 1);
        blockLacingSampleSizes[0] = (paramInt2 - blockTrackNumberLength - 3);
      }
      else
      {
        if (paramInt1 != 163) {
          break label1183;
        }
        readScratch(paramExtractorInput, 4);
        blockLacingSampleCount = ((scratch.data[3] & 0xFF) + 1);
        blockLacingSampleSizes = ensureArrayCapacity(blockLacingSampleSizes, blockLacingSampleCount);
        if (i == 2)
        {
          paramInt2 = (paramInt2 - blockTrackNumberLength - 4) / blockLacingSampleCount;
          Arrays.fill(blockLacingSampleSizes, 0, blockLacingSampleCount, paramInt2);
        }
        else
        {
          int j;
          int k;
          int m;
          int n;
          Object localObject2;
          if (i == 1)
          {
            j = 0;
            i = 4;
            k = 0;
            while (j < blockLacingSampleCount - 1)
            {
              blockLacingSampleSizes[j] = 0;
              m = i;
              do
              {
                i = m + 1;
                readScratch(paramExtractorInput, i);
                n = scratch.data[(i - 1)] & 0xFF;
                localObject2 = blockLacingSampleSizes;
                localObject2[j] += n;
                m = i;
              } while (n == 255);
              k += blockLacingSampleSizes[j];
              j += 1;
            }
            blockLacingSampleSizes[(blockLacingSampleCount - 1)] = (paramInt2 - blockTrackNumberLength - i - k);
          }
          else
          {
            if (i != 3) {
              break label1148;
            }
            j = 0;
            i = 4;
            k = 0;
            while (j < blockLacingSampleCount - 1)
            {
              blockLacingSampleSizes[j] = 0;
              m = i + 1;
              readScratch(paramExtractorInput, m);
              localObject2 = scratch.data;
              int i1 = m - 1;
              if (localObject2[i1] != 0)
              {
                i = 0;
                while (i < 8)
                {
                  int i2 = 1 << 7 - i;
                  if ((scratch.data[i1] & i2) != 0)
                  {
                    n = m + i;
                    readScratch(paramExtractorInput, n);
                    long l1 = i2 & scratch.data[i1] & 0xFF;
                    m = i1 + 1;
                    while (m < n)
                    {
                      l1 = l1 << 8 | scratch.data[m] & 0xFF;
                      m += 1;
                    }
                    l2 = l1;
                    if (j > 0) {
                      l2 = l1 - ((1L << i * 7 + 6) - 1L);
                    }
                    i = n;
                    break label870;
                  }
                  i += 1;
                }
                long l2 = 0L;
                i = m;
                label870:
                if ((l2 >= -2147483648L) && (l2 <= 2147483647L))
                {
                  m = (int)l2;
                  localObject2 = blockLacingSampleSizes;
                  if (j != 0) {
                    m += blockLacingSampleSizes[(j - 1)];
                  }
                  localObject2[j] = m;
                  k += blockLacingSampleSizes[j];
                  j += 1;
                }
                else
                {
                  throw new ParserException("EBML lacing sample size out of range.");
                }
              }
              else
              {
                throw new ParserException("No valid varint length mask found");
              }
            }
            blockLacingSampleSizes[(blockLacingSampleCount - 1)] = (paramInt2 - blockTrackNumberLength - i - k);
          }
        }
      }
      paramInt2 = scratch.data[0];
      i = scratch.data[1];
      blockTimeUs = (clusterTimecodeUs + scaleTimecodeToUs(paramInt2 << 8 | i & 0xFF));
      if ((scratch.data[2] & 0x8) == 8) {
        i = 1;
      } else {
        i = 0;
      }
      if ((type != 2) && ((paramInt1 != 163) || ((scratch.data[2] & 0x80) != 128))) {
        paramInt2 = 0;
      } else {
        paramInt2 = 1;
      }
      if (i != 0) {
        i = Integer.MIN_VALUE;
      } else {
        i = 0;
      }
      blockFlags = (paramInt2 | i);
      blockState = 2;
      blockLacingSampleIndex = 0;
      break label1194;
      label1148:
      paramExtractorInput = new StringBuilder();
      paramExtractorInput.append("Unexpected lacing value: ");
      paramExtractorInput.append(i);
      throw new ParserException(paramExtractorInput.toString());
      label1183:
      throw new ParserException("Lacing only supported in SimpleBlocks.");
    }
    label1194:
    if (paramInt1 == 163)
    {
      while (blockLacingSampleIndex < blockLacingSampleCount)
      {
        writeSampleData(paramExtractorInput, (Track)localObject1, blockLacingSampleSizes[blockLacingSampleIndex]);
        commitSampleToOutput((Track)localObject1, blockTimeUs + blockLacingSampleIndex * defaultSampleDurationNs / 1000);
        blockLacingSampleIndex += 1;
      }
      blockState = 0;
      return;
    }
    writeSampleData(paramExtractorInput, (Track)localObject1, blockLacingSampleSizes[0]);
  }
  
  void endMasterElement(int paramInt)
    throws ParserException
  {
    if (paramInt != 160)
    {
      if (paramInt != 174)
      {
        if (paramInt != 19899)
        {
          if (paramInt != 25152)
          {
            if (paramInt != 28032)
            {
              if (paramInt != 357149030)
              {
                if (paramInt != 374648427)
                {
                  if (paramInt != 475249515) {
                    return;
                  }
                  if (!sentSeekMap)
                  {
                    extractorOutput.seekMap(buildSeekMap());
                    sentSeekMap = true;
                  }
                }
                else
                {
                  if (tracks.size() != 0)
                  {
                    extractorOutput.endTracks();
                    return;
                  }
                  throw new ParserException("No valid tracks were found");
                }
              }
              else
              {
                if (timecodeScale == -9223372036854775807L) {
                  timecodeScale = 1000000L;
                }
                if (durationTimecode != -9223372036854775807L) {
                  durationUs = scaleTimecodeToUs(durationTimecode);
                }
              }
            }
            else if (currentTrack.hasContentEncryption)
            {
              if (currentTrack.sampleStrippedBytes == null) {
                return;
              }
              throw new ParserException("Combining encryption and compression is not supported");
            }
          }
          else if (currentTrack.hasContentEncryption)
          {
            if (currentTrack.cryptoData != null)
            {
              currentTrack.drmInitData = new DrmInitData(new DrmInitData.SchemeData[] { new DrmInitData.SchemeData(IpAddress.UUID_NIL, "video/webm", currentTrack.cryptoData.encryptionKey) });
              return;
            }
            throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
          }
        }
        else if ((seekEntryId != -1) && (seekEntryPosition != -1L))
        {
          if (seekEntryId == 475249515) {
            cuesContentPosition = seekEntryPosition;
          }
        }
        else {
          throw new ParserException("Mandatory element SeekID or SeekPosition not found");
        }
      }
      else
      {
        if (isCodecSupported(currentTrack.codecId))
        {
          currentTrack.initializeOutput(extractorOutput, currentTrack.number);
          tracks.put(currentTrack.number, currentTrack);
        }
        currentTrack = null;
      }
    }
    else
    {
      if (blockState != 2) {
        return;
      }
      if (!sampleSeenReferenceBlock) {
        blockFlags |= 0x1;
      }
      commitSampleToOutput((Track)tracks.get(blockTrackNumber), blockTimeUs);
      blockState = 0;
    }
  }
  
  void floatElement(int paramInt, double paramDouble)
  {
    if (paramInt != 181)
    {
      if (paramInt != 17545)
      {
        switch (paramInt)
        {
        default: 
          return;
        case 21978: 
          currentTrack.minMasteringLuminance = ((float)paramDouble);
          return;
        case 21977: 
          currentTrack.maxMasteringLuminance = ((float)paramDouble);
          return;
        case 21976: 
          currentTrack.whitePointChromaticityY = ((float)paramDouble);
          return;
        case 21975: 
          currentTrack.whitePointChromaticityX = ((float)paramDouble);
          return;
        case 21974: 
          currentTrack.primaryBChromaticityY = ((float)paramDouble);
          return;
        case 21973: 
          currentTrack.primaryBChromaticityX = ((float)paramDouble);
          return;
        case 21972: 
          currentTrack.primaryGChromaticityY = ((float)paramDouble);
          return;
        case 21971: 
          currentTrack.primaryGChromaticityX = ((float)paramDouble);
          return;
        case 21970: 
          currentTrack.primaryRChromaticityY = ((float)paramDouble);
          return;
        }
        currentTrack.primaryRChromaticityX = ((float)paramDouble);
        return;
      }
      durationTimecode = (paramDouble);
      return;
    }
    currentTrack.sampleRate = ((int)paramDouble);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
  }
  
  void integerElement(int paramInt, long paramLong)
    throws ParserException
  {
    boolean bool2 = false;
    boolean bool1 = false;
    Object localObject;
    switch (paramInt)
    {
    default: 
      
    case 2807729: 
      timecodeScale = paramLong;
      return;
    case 2352003: 
      currentTrack.defaultSampleDurationNs = ((int)paramLong);
      return;
    case 25188: 
      currentTrack.audioBitDepth = ((int)paramLong);
      return;
    case 22203: 
      currentTrack.seekPreRollNs = paramLong;
      return;
    case 22186: 
      currentTrack.codecDelayNs = paramLong;
      return;
    case 21949: 
      currentTrack.maxFrameAverageLuminance = ((int)paramLong);
      return;
    case 21948: 
      currentTrack.maxContentLuminance = ((int)paramLong);
      return;
    case 21947: 
      currentTrack.hasColorInfo = true;
      paramInt = (int)paramLong;
      if (paramInt != 1)
      {
        if (paramInt != 9)
        {
          switch (paramInt)
          {
          default: 
            return;
          }
          currentTrack.colorSpace = 2;
          return;
        }
        currentTrack.colorSpace = 6;
        return;
      }
      currentTrack.colorSpace = 1;
      return;
    case 21946: 
      paramInt = (int)paramLong;
      if (paramInt != 1)
      {
        if (paramInt != 16) {
          if (paramInt == 18) {}
        }
        switch (paramInt)
        {
        default: 
          return;
          currentTrack.colorTransfer = 7;
          return;
          currentTrack.colorTransfer = 6;
          return;
        }
      }
      currentTrack.colorTransfer = 3;
      return;
    case 21945: 
      switch ((int)paramLong)
      {
      default: 
        return;
      case 2: 
        currentTrack.colorRange = 1;
        return;
      }
      currentTrack.colorRange = 2;
      return;
    case 21930: 
      localObject = currentTrack;
      if (paramLong == 1L) {
        bool1 = true;
      }
      flagForced = bool1;
      return;
    case 21690: 
      currentTrack.displayHeight = ((int)paramLong);
      return;
    case 21682: 
      currentTrack.displayUnit = ((int)paramLong);
      return;
    case 21680: 
      currentTrack.displayWidth = ((int)paramLong);
      return;
    case 21432: 
      paramInt = (int)paramLong;
      if (paramInt != 3)
      {
        if (paramInt != 15)
        {
          switch (paramInt)
          {
          default: 
            return;
          case 1: 
            currentTrack.stereoMode = 2;
            return;
          }
          currentTrack.stereoMode = 0;
          return;
        }
        currentTrack.stereoMode = 3;
        return;
      }
      currentTrack.stereoMode = 1;
      return;
    case 21420: 
      seekEntryPosition = (paramLong + segmentContentPosition);
      return;
    case 20530: 
      if (paramLong == 1L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ContentEncodingScope ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 20529: 
      if (paramLong == 0L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ContentEncodingOrder ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 18408: 
      if (paramLong == 1L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("AESSettingsCipherMode ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 18401: 
      if (paramLong == 5L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ContentEncAlgo ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 17143: 
      if (paramLong == 1L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("EBMLReadVersion ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 17029: 
      if ((paramLong >= 1L) && (paramLong <= 2L)) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("DocTypeReadVersion ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 16980: 
      if (paramLong == 3L) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ContentCompAlgo ");
      ((StringBuilder)localObject).append(paramLong);
      ((StringBuilder)localObject).append(" not supported");
      throw new ParserException(((StringBuilder)localObject).toString());
    case 251: 
      sampleSeenReferenceBlock = true;
      return;
    case 241: 
      if (!seenClusterPositionForCurrentCuePoint)
      {
        cueClusterPositions.add(paramLong);
        seenClusterPositionForCurrentCuePoint = true;
        return;
      }
      break;
    case 231: 
      clusterTimecodeUs = scaleTimecodeToUs(paramLong);
      return;
    case 215: 
      currentTrack.number = ((int)paramLong);
      return;
    case 186: 
      currentTrack.height = ((int)paramLong);
      return;
    case 179: 
      cueTimesUs.add(scaleTimecodeToUs(paramLong));
      return;
    case 176: 
      currentTrack.width = ((int)paramLong);
      return;
    case 159: 
      currentTrack.channelCount = ((int)paramLong);
      return;
    case 155: 
      blockDurationUs = scaleTimecodeToUs(paramLong);
      return;
    case 136: 
      localObject = currentTrack;
      bool1 = bool2;
      if (paramLong == 1L) {
        bool1 = true;
      }
      flagDefault = bool1;
      return;
    case 131: 
      currentTrack.type = ((int)paramLong);
    }
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = 0;
    sampleRead = false;
    int j = 1;
    while ((j != 0) && (!sampleRead))
    {
      boolean bool2 = reader.read(paramExtractorInput);
      boolean bool1 = bool2;
      j = bool1;
      if (bool2)
      {
        j = bool1;
        if (maybeSeekForCues(paramPositionHolder, paramExtractorInput.getPosition())) {
          return 1;
        }
      }
    }
    if (j == 0)
    {
      while (i < tracks.size())
      {
        ((Track)tracks.valueAt(i)).outputPendingSampleMetadata();
        i += 1;
      }
      return -1;
    }
    return 0;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    clusterTimecodeUs = -9223372036854775807L;
    int i = 0;
    blockState = 0;
    reader.reset();
    varintReader.reset();
    resetSample();
    while (i < tracks.size())
    {
      ((Track)tracks.valueAt(i)).reset();
      i += 1;
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return new Sniffer().sniff(paramExtractorInput);
  }
  
  void startMasterElement(int paramInt, long paramLong1, long paramLong2)
    throws ParserException
  {
    if (paramInt != 160)
    {
      if (paramInt != 174)
      {
        if (paramInt != 187)
        {
          if (paramInt != 19899)
          {
            if (paramInt != 20533)
            {
              if (paramInt != 21968)
              {
                if (paramInt != 25152) {
                  if (paramInt != 408125543)
                  {
                    if (paramInt != 475249515)
                    {
                      if (paramInt != 524531317) {
                        return;
                      }
                      if (!sentSeekMap)
                      {
                        if ((seekForCuesEnabled) && (cuesContentPosition != -1L))
                        {
                          seekForCues = true;
                          return;
                        }
                        extractorOutput.seekMap(new SeekMap.Unseekable(durationUs));
                        sentSeekMap = true;
                      }
                    }
                    else
                    {
                      cueTimesUs = new LongArray();
                      cueClusterPositions = new LongArray();
                    }
                  }
                  else
                  {
                    if ((segmentContentPosition != -1L) && (segmentContentPosition != paramLong1)) {
                      throw new ParserException("Multiple Segment elements not supported");
                    }
                    segmentContentPosition = paramLong1;
                    segmentContentSize = paramLong2;
                  }
                }
              }
              else {
                currentTrack.hasColorInfo = true;
              }
            }
            else {
              currentTrack.hasContentEncryption = true;
            }
          }
          else
          {
            seekEntryId = -1;
            seekEntryPosition = -1L;
          }
        }
        else {
          seenClusterPositionForCurrentCuePoint = false;
        }
      }
      else {
        currentTrack = new Track(null);
      }
    }
    else {
      sampleSeenReferenceBlock = false;
    }
  }
  
  void stringElement(int paramInt, String paramString)
    throws ParserException
  {
    if (paramInt != 134)
    {
      if (paramInt != 17026)
      {
        if (paramInt != 21358)
        {
          if (paramInt != 2274716) {
            return;
          }
          Track.access$202(currentTrack, paramString);
          return;
        }
        currentTrack.name = paramString;
        return;
      }
      if (!"webm".equals(paramString))
      {
        if ("matroska".equals(paramString)) {
          return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("DocType ");
        localStringBuilder.append(paramString);
        localStringBuilder.append(" not supported");
        throw new ParserException(localStringBuilder.toString());
      }
    }
    else
    {
      currentTrack.codecId = paramString;
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
  
  final class InnerEbmlReaderOutput
    implements EbmlReaderOutput
  {
    private InnerEbmlReaderOutput() {}
    
    public void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
      throws IOException, InterruptedException
    {
      MatroskaExtractor.this.binaryElement(paramInt1, paramInt2, paramExtractorInput);
    }
    
    public void endMasterElement(int paramInt)
      throws ParserException
    {
      MatroskaExtractor.this.endMasterElement(paramInt);
    }
    
    public void floatElement(int paramInt, double paramDouble)
      throws ParserException
    {
      MatroskaExtractor.this.floatElement(paramInt, paramDouble);
    }
    
    public int getElementType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 0;
      case 181: 
      case 17545: 
      case 21969: 
      case 21970: 
      case 21971: 
      case 21972: 
      case 21973: 
      case 21974: 
      case 21975: 
      case 21976: 
      case 21977: 
      case 21978: 
        return 5;
      case 161: 
      case 163: 
      case 16981: 
      case 18402: 
      case 21419: 
      case 25506: 
      case 30322: 
        return 4;
      case 160: 
      case 174: 
      case 183: 
      case 187: 
      case 224: 
      case 225: 
      case 18407: 
      case 19899: 
      case 20532: 
      case 20533: 
      case 21936: 
      case 21968: 
      case 25152: 
      case 28032: 
      case 30320: 
      case 290298740: 
      case 357149030: 
      case 374648427: 
      case 408125543: 
      case 440786851: 
      case 475249515: 
      case 524531317: 
        return 1;
      case 134: 
      case 17026: 
      case 21358: 
      case 2274716: 
        return 3;
      }
      return 2;
    }
    
    public void integerElement(int paramInt, long paramLong)
      throws ParserException
    {
      MatroskaExtractor.this.integerElement(paramInt, paramLong);
    }
    
    public boolean isLevel1Element(int paramInt)
    {
      return (paramInt == 357149030) || (paramInt == 524531317) || (paramInt == 475249515) || (paramInt == 374648427);
    }
    
    public void startMasterElement(int paramInt, long paramLong1, long paramLong2)
      throws ParserException
    {
      MatroskaExtractor.this.startMasterElement(paramInt, paramLong1, paramLong2);
    }
    
    public void stringElement(int paramInt, String paramString)
      throws ParserException
    {
      MatroskaExtractor.this.stringElement(paramInt, paramString);
    }
  }
  
  final class Track
  {
    private static final int DEFAULT_MAX_CLL = 1000;
    private static final int DEFAULT_MAX_FALL = 200;
    private static final int DISPLAY_UNIT_PIXELS = 0;
    private static final int MAX_CHROMATICITY = 50000;
    public int audioBitDepth = -1;
    public int channelCount = 1;
    public long codecDelayNs = 0L;
    public String codecId;
    public byte[] codecPrivate;
    public int colorRange = -1;
    public int colorSpace = -1;
    public int colorTransfer = -1;
    public TrackOutput.CryptoData cryptoData;
    public int defaultSampleDurationNs;
    public int displayHeight = -1;
    public int displayUnit = 0;
    public int displayWidth = -1;
    public DrmInitData drmInitData;
    public boolean flagDefault = true;
    public boolean flagForced;
    public boolean hasColorInfo = false;
    public boolean hasContentEncryption;
    public int height = -1;
    private String language = "eng";
    public int maxContentLuminance = 1000;
    public int maxFrameAverageLuminance = 200;
    public float maxMasteringLuminance = -1.0F;
    public float minMasteringLuminance = -1.0F;
    public int nalUnitLengthFieldLength;
    public String name;
    public int number;
    public TrackOutput output;
    public float primaryBChromaticityX = -1.0F;
    public float primaryBChromaticityY = -1.0F;
    public float primaryGChromaticityX = -1.0F;
    public float primaryGChromaticityY = -1.0F;
    public float primaryRChromaticityX = -1.0F;
    public float primaryRChromaticityY = -1.0F;
    public byte[] projectionData = null;
    public int sampleRate = 8000;
    public byte[] sampleStrippedBytes;
    public long seekPreRollNs = 0L;
    public int stereoMode = -1;
    @Nullable
    public MatroskaExtractor.TrueHdSampleRechunker trueHdSampleRechunker;
    public int type;
    public float whitePointChromaticityX = -1.0F;
    public float whitePointChromaticityY = -1.0F;
    public int width = -1;
    
    private Track() {}
    
    private byte[] getHdrStaticInfo()
    {
      if ((primaryRChromaticityX != -1.0F) && (primaryRChromaticityY != -1.0F) && (primaryGChromaticityX != -1.0F) && (primaryGChromaticityY != -1.0F) && (primaryBChromaticityX != -1.0F) && (primaryBChromaticityY != -1.0F) && (whitePointChromaticityX != -1.0F) && (whitePointChromaticityY != -1.0F) && (maxMasteringLuminance != -1.0F) && (minMasteringLuminance != -1.0F))
      {
        byte[] arrayOfByte = new byte[25];
        ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
        localByteBuffer.put((byte)0);
        localByteBuffer.putShort((short)(int)(primaryRChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(primaryRChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(primaryGChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(primaryGChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(primaryBChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(primaryBChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(whitePointChromaticityX * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(whitePointChromaticityY * 50000.0F + 0.5F));
        localByteBuffer.putShort((short)(int)(maxMasteringLuminance + 0.5F));
        localByteBuffer.putShort((short)(int)(minMasteringLuminance + 0.5F));
        localByteBuffer.putShort((short)maxContentLuminance);
        localByteBuffer.putShort((short)maxFrameAverageLuminance);
        return arrayOfByte;
      }
      return null;
    }
    
    private static Pair parseFourCcPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      try
      {
        paramParsableByteArray.skipBytes(16);
        long l = paramParsableByteArray.readLittleEndianUnsignedInt();
        if (l == 1482049860L)
        {
          paramParsableByteArray = new Pair("video/3gpp", null);
          return paramParsableByteArray;
        }
        if (l == 826496599L)
        {
          int i = paramParsableByteArray.getPosition();
          i += 20;
          paramParsableByteArray = data;
          while (i < paramParsableByteArray.length - 4)
          {
            if ((paramParsableByteArray[i] == 0) && (paramParsableByteArray[(i + 1)] == 0) && (paramParsableByteArray[(i + 2)] == 1) && (paramParsableByteArray[(i + 3)] == 15))
            {
              int j = paramParsableByteArray.length;
              paramParsableByteArray = Arrays.copyOfRange(paramParsableByteArray, i, j);
              paramParsableByteArray = new Pair("video/wvc1", Collections.singletonList(paramParsableByteArray));
              return paramParsableByteArray;
            }
            i += 1;
          }
          paramParsableByteArray = new ParserException("Failed to find FourCC VC1 initialization data");
          throw paramParsableByteArray;
        }
        Log.w("MatroskaExtractor", "Unknown FourCC. Setting mimeType to video/x-unknown");
        return new Pair("video/x-unknown", null);
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        for (;;) {}
      }
      throw new ParserException("Error parsing FourCC private data");
    }
    
    private static boolean parseMsAcmCodecPrivate(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      try
      {
        int i = paramParsableByteArray.readLittleEndianUnsignedShort();
        if (i == 1) {
          return true;
        }
        if (i == 65534)
        {
          paramParsableByteArray.setPosition(24);
          long l1 = paramParsableByteArray.readLong();
          long l2 = MatroskaExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits();
          if (l1 == l2)
          {
            l1 = paramParsableByteArray.readLong();
            l2 = MatroskaExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits();
            if (l1 == l2) {
              return true;
            }
          }
          return false;
        }
        return false;
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        for (;;) {}
      }
      throw new ParserException("Error parsing MS/ACM codec private");
    }
    
    private static List parseVorbisCodecPrivate(byte[] paramArrayOfByte)
      throws ParserException
    {
      int i;
      int j;
      int k;
      int m;
      byte[] arrayOfByte1;
      if (paramArrayOfByte[0] == 2)
      {
        i = 1;
        j = 0;
        while (paramArrayOfByte[i] == -1)
        {
          j += 255;
          i += 1;
        }
        k = i + 1;
        m = j + paramArrayOfByte[i];
        i = 0;
        j = k;
        while (paramArrayOfByte[j] == -1)
        {
          i += 255;
          j += 1;
        }
        k = j + 1;
        j = paramArrayOfByte[j];
        if (paramArrayOfByte[k] == 1) {
          arrayOfByte1 = new byte[m];
        }
      }
      try
      {
        System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, m);
        k += m;
        if (paramArrayOfByte[k] == 3)
        {
          i = k + (i + j);
          if (paramArrayOfByte[i] == 5)
          {
            byte[] arrayOfByte2 = new byte[paramArrayOfByte.length - i];
            j = paramArrayOfByte.length;
            System.arraycopy(paramArrayOfByte, i, arrayOfByte2, 0, j - i);
            paramArrayOfByte = new ArrayList(2);
            paramArrayOfByte.add(arrayOfByte1);
            paramArrayOfByte.add(arrayOfByte2);
            return paramArrayOfByte;
          }
          paramArrayOfByte = new ParserException("Error parsing vorbis codec private");
          throw paramArrayOfByte;
        }
        paramArrayOfByte = new ParserException("Error parsing vorbis codec private");
        throw paramArrayOfByte;
      }
      catch (ArrayIndexOutOfBoundsException paramArrayOfByte)
      {
        for (;;) {}
      }
      paramArrayOfByte = new ParserException("Error parsing vorbis codec private");
      throw paramArrayOfByte;
      paramArrayOfByte = new ParserException("Error parsing vorbis codec private");
      throw paramArrayOfByte;
      throw new ParserException("Error parsing vorbis codec private");
    }
    
    public void initializeOutput(ExtractorOutput paramExtractorOutput, int paramInt)
      throws ParserException
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a141 = a140\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
    
    public void outputPendingSampleMetadata()
    {
      if (trueHdSampleRechunker != null) {
        trueHdSampleRechunker.outputPendingSampleMetadata(this);
      }
    }
    
    public void reset()
    {
      if (trueHdSampleRechunker != null) {
        trueHdSampleRechunker.reset();
      }
    }
  }
  
  final class TrueHdSampleRechunker
  {
    private int blockFlags;
    private int chunkSize;
    private boolean foundSyncframe;
    private int sampleCount;
    private final byte[] syncframePrefix = new byte[10];
    private long timeUs;
    
    public TrueHdSampleRechunker() {}
    
    public void outputPendingSampleMetadata(MatroskaExtractor.Track paramTrack)
    {
      if ((foundSyncframe) && (sampleCount > 0))
      {
        output.sampleMetadata(timeUs, blockFlags, chunkSize, 0, cryptoData);
        sampleCount = 0;
      }
    }
    
    public void reset()
    {
      foundSyncframe = false;
    }
    
    public void sampleMetadata(MatroskaExtractor.Track paramTrack, long paramLong)
    {
      if (!foundSyncframe) {
        return;
      }
      int i = sampleCount;
      sampleCount = (i + 1);
      if (i == 0) {
        timeUs = paramLong;
      }
      if (sampleCount < 16) {
        return;
      }
      output.sampleMetadata(timeUs, blockFlags, chunkSize, 0, cryptoData);
      sampleCount = 0;
    }
    
    public void startSample(ExtractorInput paramExtractorInput, int paramInt1, int paramInt2)
      throws IOException, InterruptedException
    {
      if (!foundSyncframe)
      {
        paramExtractorInput.peekFully(syncframePrefix, 0, 10);
        paramExtractorInput.resetPeekPosition();
        if (Ac3Util.parseTrueHdSyncframeAudioSampleCount(syncframePrefix) == 0) {
          return;
        }
        foundSyncframe = true;
        sampleCount = 0;
      }
      if (sampleCount == 0)
      {
        blockFlags = paramInt1;
        chunkSize = 0;
      }
      chunkSize += paramInt2;
    }
  }
}
