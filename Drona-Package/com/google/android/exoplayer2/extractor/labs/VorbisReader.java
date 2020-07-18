package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.ArrayList;

final class VorbisReader
  extends StreamReader
{
  private VorbisUtil.CommentHeader commentHeader;
  private int previousPacketBlockSize;
  private boolean seenFirstAudioPacket;
  private VorbisUtil.VorbisIdHeader vorbisIdHeader;
  private VorbisSetup vorbisSetup;
  
  VorbisReader() {}
  
  static void appendNumberOfSamples(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    paramParsableByteArray.setLimit(paramParsableByteArray.limit() + 4);
    data[(paramParsableByteArray.limit() - 4)] = ((byte)(int)(paramLong & 0xFF));
    data[(paramParsableByteArray.limit() - 3)] = ((byte)(int)(paramLong >>> 8 & 0xFF));
    data[(paramParsableByteArray.limit() - 2)] = ((byte)(int)(paramLong >>> 16 & 0xFF));
    data[(paramParsableByteArray.limit() - 1)] = ((byte)(int)(paramLong >>> 24 & 0xFF));
  }
  
  private static int decodeBlockSize(byte paramByte, VorbisSetup paramVorbisSetup)
  {
    int i = readBits(paramByte, iLogModes, 1);
    if (!modes[i].blockFlag) {
      return idHeader.blockSize0;
    }
    return idHeader.blockSize1;
  }
  
  static int readBits(byte paramByte, int paramInt1, int paramInt2)
  {
    return paramByte >> paramInt2 & 255 >>> 8 - paramInt1;
  }
  
  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    try
    {
      boolean bool = VorbisUtil.verifyVorbisHeaderCapturePattern(1, paramParsableByteArray, true);
      return bool;
    }
    catch (ParserException paramParsableByteArray)
    {
      for (;;) {}
    }
    return false;
  }
  
  protected void onSeekEnd(long paramLong)
  {
    super.onSeekEnd(paramLong);
    int i = 0;
    boolean bool;
    if (paramLong != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    seenFirstAudioPacket = bool;
    if (vorbisIdHeader != null) {
      i = vorbisIdHeader.blockSize0;
    }
    previousPacketBlockSize = i;
  }
  
  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = data;
    int i = 0;
    if ((arrayOfByte[0] & 0x1) == 1) {
      return -1L;
    }
    int j = decodeBlockSize(data[0], vorbisSetup);
    if (seenFirstAudioPacket) {
      i = (previousPacketBlockSize + j) / 4;
    }
    long l = i;
    appendNumberOfSamples(paramParsableByteArray, l);
    seenFirstAudioPacket = true;
    previousPacketBlockSize = j;
    return l;
  }
  
  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
    throws IOException, InterruptedException
  {
    if (vorbisSetup != null) {
      return false;
    }
    vorbisSetup = readSetupHeaders(paramParsableByteArray);
    if (vorbisSetup == null) {
      return true;
    }
    paramParsableByteArray = new ArrayList();
    paramParsableByteArray.add(vorbisSetup.idHeader.data);
    paramParsableByteArray.add(vorbisSetup.setupHeaderData);
    format = Format.createAudioSampleFormat(null, "audio/vorbis", null, vorbisSetup.idHeader.bitrateNominal, -1, vorbisSetup.idHeader.channels, (int)vorbisSetup.idHeader.sampleRate, paramParsableByteArray, null, 0, null);
    return true;
  }
  
  VorbisSetup readSetupHeaders(ParsableByteArray paramParsableByteArray)
    throws IOException
  {
    if (vorbisIdHeader == null)
    {
      vorbisIdHeader = VorbisUtil.readVorbisIdentificationHeader(paramParsableByteArray);
      return null;
    }
    if (commentHeader == null)
    {
      commentHeader = VorbisUtil.readVorbisCommentHeader(paramParsableByteArray);
      return null;
    }
    byte[] arrayOfByte = new byte[paramParsableByteArray.limit()];
    System.arraycopy(data, 0, arrayOfByte, 0, paramParsableByteArray.limit());
    paramParsableByteArray = VorbisUtil.readVorbisModes(paramParsableByteArray, vorbisIdHeader.channels);
    int i = VorbisUtil.iLog(paramParsableByteArray.length - 1);
    return new VorbisSetup(vorbisIdHeader, commentHeader, arrayOfByte, paramParsableByteArray, i);
  }
  
  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean)
    {
      vorbisSetup = null;
      vorbisIdHeader = null;
      commentHeader = null;
    }
    previousPacketBlockSize = 0;
    seenFirstAudioPacket = false;
  }
  
  final class VorbisSetup
  {
    public final VorbisUtil.CommentHeader commentHeader;
    public final int iLogModes;
    public final VorbisUtil.Mode[] modes;
    public final byte[] setupHeaderData;
    
    public VorbisSetup(VorbisUtil.CommentHeader paramCommentHeader, byte[] paramArrayOfByte, VorbisUtil.Mode[] paramArrayOfMode, int paramInt)
    {
      commentHeader = paramCommentHeader;
      setupHeaderData = paramArrayOfByte;
      modes = paramArrayOfMode;
      iLogModes = paramInt;
    }
  }
}
