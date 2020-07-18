package com.google.android.exoplayer2.extractor.ClickListeners;

import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.AvcConfig;
import com.google.android.exoplayer2.video.HevcConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class AtomParsers
{
  private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 3;
  private static final String TAG = "AtomParsers";
  private static final int TYPE_clcp = Util.getIntegerCodeForString("clcp");
  private static final int TYPE_meta = Util.getIntegerCodeForString("meta");
  private static final int TYPE_sbtl;
  private static final int TYPE_soun;
  private static final int TYPE_subt;
  private static final int TYPE_text;
  private static final int TYPE_vide = Util.getIntegerCodeForString("vide");
  private static final byte[] opusMagic = Util.getUtf8Bytes("OpusHead");
  
  static
  {
    TYPE_soun = Util.getIntegerCodeForString("soun");
    TYPE_text = Util.getIntegerCodeForString("text");
    TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    TYPE_subt = Util.getIntegerCodeForString("subt");
  }
  
  private AtomParsers() {}
  
  private static boolean canApplyEditWithGaplessInfo(long[] paramArrayOfLong, long paramLong1, long paramLong2, long paramLong3)
  {
    int j = paramArrayOfLong.length - 1;
    int i = Util.constrainValue(3, 0, j);
    j = Util.constrainValue(paramArrayOfLong.length - 3, 0, j);
    return (paramArrayOfLong[0] <= paramLong2) && (paramLong2 < paramArrayOfLong[i]) && (paramArrayOfLong[j] < paramLong3) && (paramLong3 <= paramLong1);
  }
  
  private static int findEsdsPosition(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      boolean bool;
      if (j > 0) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      if (paramParsableByteArray.readInt() == Atom.TYPE_esds) {
        return i;
      }
      i += j;
    }
    return -1;
  }
  
  private static void parseAudioSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString, boolean paramBoolean, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt5)
    throws ParserException
  {
    Object localObject1 = paramDrmInitData;
    StsdData localStsdData = paramStsdData;
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    if (paramBoolean)
    {
      k = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
    }
    else
    {
      paramParsableByteArray.skipBytes(8);
      k = 0;
    }
    if ((k != 0) && (k != 1))
    {
      if (k == 2)
      {
        paramParsableByteArray.skipBytes(16);
        j = (int)Math.round(paramParsableByteArray.readDouble());
        i = paramParsableByteArray.readUnsignedIntToInt();
        paramParsableByteArray.skipBytes(20);
      }
    }
    else
    {
      m = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      n = paramParsableByteArray.readUnsignedFixedPoint1616();
      i = m;
      j = n;
      if (k == 1)
      {
        paramParsableByteArray.skipBytes(16);
        j = n;
        i = m;
      }
    }
    int n = paramParsableByteArray.getPosition();
    int i1 = Atom.TYPE_enca;
    int k = paramInt1;
    Object localObject2 = localObject1;
    int m = k;
    if (paramInt1 == i1)
    {
      localObject2 = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3);
      if (localObject2 != null)
      {
        k = ((Integer)first).intValue();
        if (paramDrmInitData == null) {
          localObject1 = null;
        } else {
          localObject1 = paramDrmInitData.copyWithSchemeType(second).schemeType);
        }
        trackEncryptionBoxes[paramInt5] = ((TrackEncryptionBox)second);
      }
      paramParsableByteArray.setPosition(n);
      m = k;
      localObject2 = localObject1;
    }
    localObject1 = localObject2;
    if (m == Atom.TYPE_ac_3) {
      paramDrmInitData = "audio/ac3";
    } else if (m == Atom.TYPE_ec_3) {
      paramDrmInitData = "audio/eac3";
    } else if (m == Atom.TYPE_dtsc) {
      paramDrmInitData = "audio/vnd.dts";
    } else if ((m != Atom.TYPE_dtsh) && (m != Atom.TYPE_dtsl))
    {
      if (m == Atom.TYPE_dtse) {
        paramDrmInitData = "audio/vnd.dts.hd;profile=lbr";
      } else if (m == Atom.TYPE_samr) {
        paramDrmInitData = "audio/3gpp";
      } else if (m == Atom.TYPE_sawb) {
        paramDrmInitData = "audio/amr-wb";
      } else if ((m != Atom.TYPE_lpcm) && (m != Atom.TYPE_sowt))
      {
        if (m == Atom.TYPE__mp3) {
          paramDrmInitData = "audio/mpeg";
        } else if (m == Atom.TYPE_alac) {
          paramDrmInitData = "audio/alac";
        } else if (m == Atom.TYPE_alaw) {
          paramDrmInitData = "audio/g711-alaw";
        } else if (m == Atom.TYPE_ulaw) {
          paramDrmInitData = "audio/g711-mlaw";
        } else if (m == Atom.TYPE_Opus) {
          paramDrmInitData = "audio/opus";
        } else if (m == Atom.TYPE_fLaC) {
          paramDrmInitData = "audio/flac";
        } else {
          paramDrmInitData = null;
        }
      }
      else {
        paramDrmInitData = "audio/raw";
      }
    }
    else {
      paramDrmInitData = "audio/vnd.dts.hd";
    }
    paramInt5 = j;
    int j = n;
    paramInt1 = i;
    paramStsdData = null;
    int i = j;
    while (i - paramInt2 < paramInt3)
    {
      paramParsableByteArray.setPosition(i);
      n = paramParsableByteArray.readInt();
      boolean bool;
      if (n > 0) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      j = paramParsableByteArray.readInt();
      Object localObject3;
      if ((j != Atom.TYPE_esds) && ((!paramBoolean) || (j != Atom.TYPE_wave)))
      {
        if (j == Atom.TYPE_dac3) {
          paramParsableByteArray.setPosition(i + 8);
        }
        for (format = Ac3Util.parseAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, (DrmInitData)localObject1);; format = Ac3Util.parseEAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, (DrmInitData)localObject1))
        {
          j = paramInt5;
          k = paramInt1;
          break label1042;
          if (j != Atom.TYPE_dec3) {
            break;
          }
          paramParsableByteArray.setPosition(i + 8);
        }
        if (j == Atom.TYPE_ddts)
        {
          localObject3 = Integer.toString(paramInt4);
          localObject2 = paramDrmInitData;
          format = Format.createAudioSampleFormat((String)localObject3, paramDrmInitData, null, -1, -1, paramInt1, paramInt5, null, (DrmInitData)localObject1, 0, paramString);
          paramDrmInitData = (DrmInitData)localObject2;
          j = paramInt5;
          k = paramInt1;
        }
        else
        {
          localObject2 = paramDrmInitData;
          if (j == Atom.TYPE_alac)
          {
            paramStsdData = new byte[n];
            paramParsableByteArray.setPosition(i);
            paramParsableByteArray.readBytes(paramStsdData, 0, n);
            paramDrmInitData = (DrmInitData)localObject2;
            j = paramInt5;
            k = paramInt1;
          }
          else
          {
            if (j == Atom.TYPE_dOps)
            {
              j = n - 8;
              paramStsdData = new byte[opusMagic.length + j];
              System.arraycopy(opusMagic, 0, paramStsdData, 0, opusMagic.length);
              paramParsableByteArray.setPosition(i + 8);
              paramParsableByteArray.readBytes(paramStsdData, opusMagic.length, j);
            }
            for (;;)
            {
              paramDrmInitData = (DrmInitData)localObject2;
              j = paramInt5;
              k = paramInt1;
              break;
              paramDrmInitData = (DrmInitData)localObject2;
              j = paramInt5;
              k = paramInt1;
              if (n != Atom.TYPE_dfLa) {
                break;
              }
              j = n - 12;
              paramStsdData = new byte[j];
              paramParsableByteArray.setPosition(i + 12);
              paramParsableByteArray.readBytes(paramStsdData, 0, j);
            }
          }
        }
      }
      else
      {
        if (j == Atom.TYPE_esds) {
          j = i;
        } else {
          j = findEsdsPosition(paramParsableByteArray, i, n);
        }
        m = j;
        j = paramInt5;
        k = paramInt1;
        if (m != -1)
        {
          paramDrmInitData = parseEsdsFromParent(paramParsableByteArray, m);
          localObject2 = (String)first;
          localObject3 = (byte[])second;
          paramDrmInitData = (DrmInitData)localObject2;
          paramStsdData = (StsdData)localObject3;
          j = paramInt5;
          k = paramInt1;
          if ("audio/mp4a-latm".equals(localObject2))
          {
            paramDrmInitData = CodecSpecificDataUtil.parseAacAudioSpecificConfig((byte[])localObject3);
            j = ((Integer)first).intValue();
            k = ((Integer)second).intValue();
            paramDrmInitData = (DrmInitData)localObject2;
            paramStsdData = (StsdData)localObject3;
          }
        }
      }
      label1042:
      i += n;
      paramInt5 = j;
      paramInt1 = k;
    }
    if ((format == null) && (paramDrmInitData != null))
    {
      if ("audio/raw".equals(paramDrmInitData)) {
        paramInt2 = 2;
      } else {
        paramInt2 = -1;
      }
      localObject2 = Integer.toString(paramInt4);
      if (paramStsdData == null) {
        paramParsableByteArray = null;
      } else {
        paramParsableByteArray = Collections.singletonList(paramStsdData);
      }
      format = Format.createAudioSampleFormat((String)localObject2, paramDrmInitData, null, -1, -1, paramInt1, paramInt5, paramInt2, paramParsableByteArray, (DrmInitData)localObject1, 0, paramString);
    }
  }
  
  static Pair parseCommonEncryptionSinfFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    Object localObject2 = null;
    Object localObject1 = null;
    int k = -1;
    int j = 0;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int m = paramParsableByteArray.readInt();
      int n = paramParsableByteArray.readInt();
      Object localObject4;
      Object localObject3;
      if (n == Atom.TYPE_frma)
      {
        localObject4 = Integer.valueOf(paramParsableByteArray.readInt());
        localObject3 = localObject2;
      }
      else if (n == Atom.TYPE_schm)
      {
        paramParsableByteArray.skipBytes(4);
        localObject3 = paramParsableByteArray.readString(4);
        localObject4 = localObject1;
      }
      else
      {
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (n == Atom.TYPE_schi)
        {
          k = i;
          j = m;
          localObject4 = localObject1;
          localObject3 = localObject2;
        }
      }
      i += m;
      localObject2 = localObject3;
      localObject1 = localObject4;
    }
    if ((!"cenc".equals(localObject2)) && (!"cbc1".equals(localObject2)) && (!"cens".equals(localObject2)) && (!"cbcs".equals(localObject2))) {
      return null;
    }
    boolean bool2 = true;
    boolean bool1;
    if (localObject1 != null) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1, "frma atom is mandatory");
    if (k != -1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1, "schi atom is mandatory");
    paramParsableByteArray = parseSchiFromParent(paramParsableByteArray, k, j, localObject2);
    if (paramParsableByteArray != null) {
      bool1 = bool2;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1, "tenc atom is mandatory");
    return Pair.create(localObject1, paramParsableByteArray);
  }
  
  private static Pair parseEdts(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom != null)
    {
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_elst);
      if (paramContainerAtom != null)
      {
        paramContainerAtom = data;
        paramContainerAtom.setPosition(8);
        int j = Atom.parseFullAtomVersion(paramContainerAtom.readInt());
        int k = paramContainerAtom.readUnsignedIntToInt();
        long[] arrayOfLong1 = new long[k];
        long[] arrayOfLong2 = new long[k];
        int i = 0;
        while (i < k)
        {
          long l;
          if (j == 1) {
            l = paramContainerAtom.readUnsignedLongToLong();
          } else {
            l = paramContainerAtom.readUnsignedInt();
          }
          arrayOfLong1[i] = l;
          if (j == 1) {
            l = paramContainerAtom.readLong();
          } else {
            l = paramContainerAtom.readInt();
          }
          arrayOfLong2[i] = l;
          if (paramContainerAtom.readShort() == 1)
          {
            paramContainerAtom.skipBytes(2);
            i += 1;
          }
          else
          {
            throw new IllegalArgumentException("Unsupported media rate.");
          }
        }
        return Pair.create(arrayOfLong1, arrayOfLong2);
      }
    }
    return Pair.create(null, null);
  }
  
  private static Pair parseEsdsFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    paramParsableByteArray.skipBytes(2);
    paramInt = paramParsableByteArray.readUnsignedByte();
    if ((paramInt & 0x80) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    if ((paramInt & 0x40) != 0) {
      paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedShort());
    }
    if ((paramInt & 0x20) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    String str = MimeTypes.getMimeTypeFromMp4ObjectType(paramParsableByteArray.readUnsignedByte());
    if ((!"audio/mpeg".equals(str)) && (!"audio/vnd.dts".equals(str)) && (!"audio/vnd.dts.hd".equals(str)))
    {
      paramParsableByteArray.skipBytes(12);
      paramParsableByteArray.skipBytes(1);
      paramInt = parseExpandableClassSize(paramParsableByteArray);
      byte[] arrayOfByte = new byte[paramInt];
      paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
      return Pair.create(str, arrayOfByte);
    }
    return Pair.create(str, null);
  }
  
  private static int parseExpandableClassSize(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.readUnsignedByte();
    int i = j;
    int k = j & 0x7F;
    j = i;
    for (i = k; (j & 0x80) == 128; i = i << 7 | k & 0x7F)
    {
      k = paramParsableByteArray.readUnsignedByte();
      j = k;
    }
    return i;
  }
  
  private static int parseHdlr(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(16);
    int i = paramParsableByteArray.readInt();
    if (i == TYPE_soun) {
      return 1;
    }
    if (i == TYPE_vide) {
      return 2;
    }
    if ((i != TYPE_text) && (i != TYPE_sbtl) && (i != TYPE_subt) && (i != TYPE_clcp))
    {
      if (i == TYPE_meta) {
        return 4;
      }
      return -1;
    }
    return 3;
  }
  
  private static Metadata parseIlst(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(8);
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      Metadata.Entry localEntry = MetadataUtil.parseIlstElement(paramParsableByteArray);
      if (localEntry != null) {
        localArrayList.add(localEntry);
      }
    }
    if (localArrayList.isEmpty()) {
      return null;
    }
    return new Metadata(localArrayList);
  }
  
  private static Pair parseMdhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (k == 0) {
      i = 8;
    } else {
      i = 16;
    }
    paramParsableByteArray.skipBytes(i);
    long l = paramParsableByteArray.readUnsignedInt();
    int i = j;
    if (k == 0) {
      i = 4;
    }
    paramParsableByteArray.skipBytes(i);
    i = paramParsableByteArray.readUnsignedShort();
    paramParsableByteArray = new StringBuilder();
    paramParsableByteArray.append("");
    paramParsableByteArray.append((char)((i >> 10 & 0x1F) + 96));
    paramParsableByteArray.append((char)((i >> 5 & 0x1F) + 96));
    paramParsableByteArray.append((char)((i & 0x1F) + 96));
    return Pair.create(Long.valueOf(l), paramParsableByteArray.toString());
  }
  
  private static Metadata parseMetaAtom(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(12);
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      int i = paramParsableByteArray.getPosition();
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_ilst)
      {
        paramParsableByteArray.setPosition(i);
        return parseIlst(paramParsableByteArray, i + j);
      }
      paramParsableByteArray.skipBytes(j - 8);
    }
    return null;
  }
  
  private static long parseMvhd(ParsableByteArray paramParsableByteArray)
  {
    int i = 8;
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) != 0) {
      i = 16;
    }
    paramParsableByteArray.skipBytes(i);
    return paramParsableByteArray.readUnsignedInt();
  }
  
  private static float parsePaspFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = paramParsableByteArray.readUnsignedIntToInt();
    int i = paramParsableByteArray.readUnsignedIntToInt();
    return paramInt / i;
  }
  
  private static byte[] parseProjFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_proj) {
        return Arrays.copyOfRange(data, i, j + i);
      }
      i += j;
    }
    return null;
  }
  
  private static Pair parseSampleEntryEncryptionData(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      boolean bool;
      if (j > 0) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      if (paramParsableByteArray.readInt() == Atom.TYPE_sinf)
      {
        Pair localPair = parseCommonEncryptionSinfFromParent(paramParsableByteArray, i, j);
        if (localPair != null) {
          return localPair;
        }
      }
      i += j;
    }
    return null;
  }
  
  private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, String paramString)
  {
    int i = paramInt1 + 8;
    for (;;)
    {
      Object localObject2 = null;
      if (i - paramInt1 >= paramInt2) {
        break;
      }
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_tenc)
      {
        paramInt1 = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
        paramParsableByteArray.skipBytes(1);
        if (paramInt1 == 0)
        {
          paramParsableByteArray.skipBytes(1);
          paramInt2 = 0;
          paramInt1 = 0;
        }
        else
        {
          paramInt2 = paramParsableByteArray.readUnsignedByte();
          paramInt1 = paramInt2 & 0xF;
          paramInt2 = (paramInt2 & 0xF0) >> 4;
        }
        boolean bool;
        if (paramParsableByteArray.readUnsignedByte() == 1) {
          bool = true;
        } else {
          bool = false;
        }
        i = paramParsableByteArray.readUnsignedByte();
        byte[] arrayOfByte = new byte[16];
        paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
        Object localObject1 = localObject2;
        if (bool)
        {
          localObject1 = localObject2;
          if (i == 0)
          {
            j = paramParsableByteArray.readUnsignedByte();
            localObject1 = new byte[j];
            paramParsableByteArray.readBytes((byte[])localObject1, 0, j);
          }
        }
        return new TrackEncryptionBox(bool, paramString, i, arrayOfByte, paramInt2, paramInt1, (byte[])localObject1);
      }
      i += j;
    }
    return null;
  }
  
  public static TrackSampleTable parseStbl(Track paramTrack, Atom.ContainerAtom paramContainerAtom, GaplessInfoHolder paramGaplessInfoHolder)
    throws ParserException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private static StsdData parseStsd(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, String paramString, DrmInitData paramDrmInitData, boolean paramBoolean)
    throws ParserException
  {
    paramParsableByteArray.setPosition(12);
    int j = paramParsableByteArray.readInt();
    StsdData localStsdData = new StsdData(j);
    int i = 0;
    while (i < j)
    {
      int k = paramParsableByteArray.getPosition();
      int m = paramParsableByteArray.readInt();
      boolean bool;
      if (m > 0) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      int n = paramParsableByteArray.readInt();
      if ((n != Atom.TYPE_avc1) && (n != Atom.TYPE_avc3) && (n != Atom.TYPE_encv) && (n != Atom.TYPE_mp4v) && (n != Atom.TYPE_hvc1) && (n != Atom.TYPE_hev1) && (n != Atom.TYPE_s263) && (n != Atom.TYPE_vp08) && (n != Atom.TYPE_vp09))
      {
        if ((n != Atom.TYPE_mp4a) && (n != Atom.TYPE_enca) && (n != Atom.TYPE_ac_3) && (n != Atom.TYPE_ec_3) && (n != Atom.TYPE_dtsc) && (n != Atom.TYPE_dtse) && (n != Atom.TYPE_dtsh) && (n != Atom.TYPE_dtsl) && (n != Atom.TYPE_samr) && (n != Atom.TYPE_sawb) && (n != Atom.TYPE_lpcm) && (n != Atom.TYPE_sowt) && (n != Atom.TYPE__mp3) && (n != Atom.TYPE_alac) && (n != Atom.TYPE_alaw) && (n != Atom.TYPE_ulaw) && (n != Atom.TYPE_Opus) && (n != Atom.TYPE_fLaC))
        {
          if ((n != Atom.TYPE_TTML) && (n != Atom.TYPE_tx3g) && (n != Atom.TYPE_wvtt) && (n != Atom.TYPE_stpp) && (n != Atom.TYPE_c608))
          {
            if (n == Atom.TYPE_camm) {
              format = Format.createSampleFormat(Integer.toString(paramInt1), "application/x-camera-motion", null, -1, null);
            }
          }
          else {
            parseTextSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramString, localStsdData);
          }
        }
        else {
          parseAudioSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramString, paramBoolean, paramDrmInitData, localStsdData, i);
        }
      }
      else {
        parseVideoSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramInt2, paramDrmInitData, localStsdData, i);
      }
      paramParsableByteArray.setPosition(k + m);
      i += 1;
    }
    return localStsdData;
  }
  
  private static void parseTextSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString, StsdData paramStsdData)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    paramInt2 = Atom.TYPE_TTML;
    Object localObject = null;
    long l = Long.MAX_VALUE;
    if (paramInt1 == paramInt2) {
      paramParsableByteArray = "application/ttml+xml";
    }
    for (;;)
    {
      break;
      if (paramInt1 == Atom.TYPE_tx3g)
      {
        paramInt1 = paramInt3 - 8 - 8;
        localObject = new byte[paramInt1];
        paramParsableByteArray.readBytes((byte[])localObject, 0, paramInt1);
        localObject = Collections.singletonList(localObject);
        paramParsableByteArray = "application/x-quicktime-tx3g";
      }
      else if (paramInt1 == Atom.TYPE_wvtt)
      {
        paramParsableByteArray = "application/x-mp4-vtt";
      }
      else if (paramInt1 == Atom.TYPE_stpp)
      {
        paramParsableByteArray = "application/ttml+xml";
        l = 0L;
      }
      else
      {
        if (paramInt1 != Atom.TYPE_c608) {
          break label154;
        }
        paramParsableByteArray = "application/x-mp4-cea-608";
        requiredSampleTransformation = 1;
      }
    }
    format = Format.createTextSampleFormat(Integer.toString(paramInt4), paramParsableByteArray, null, -1, 0, paramString, -1, null, l, (List)localObject);
    return;
    label154:
    throw new IllegalStateException();
  }
  
  private static TkhdData parseTkhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int n = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (n == 0) {
      i = 8;
    } else {
      i = 16;
    }
    paramParsableByteArray.skipBytes(i);
    int m = paramParsableByteArray.readInt();
    paramParsableByteArray.skipBytes(4);
    int i1 = paramParsableByteArray.getPosition();
    int i = j;
    if (n == 0) {
      i = 4;
    }
    int k = 0;
    j = 0;
    while (j < i)
    {
      if (data[(i1 + j)] != -1)
      {
        j = 0;
        break label99;
      }
      j += 1;
    }
    j = 1;
    label99:
    long l2 = -9223372036854775807L;
    long l1;
    if (j != 0)
    {
      paramParsableByteArray.skipBytes(i);
      l1 = l2;
    }
    else
    {
      if (n == 0) {
        l1 = paramParsableByteArray.readUnsignedInt();
      } else {
        l1 = paramParsableByteArray.readUnsignedLongToLong();
      }
      if (l1 == 0L) {
        l1 = l2;
      }
    }
    paramParsableByteArray.skipBytes(16);
    j = paramParsableByteArray.readInt();
    n = paramParsableByteArray.readInt();
    paramParsableByteArray.skipBytes(4);
    i1 = paramParsableByteArray.readInt();
    int i2 = paramParsableByteArray.readInt();
    if ((j == 0) && (n == 65536) && (i1 == -65536) && (i2 == 0))
    {
      i = 90;
    }
    else if ((j == 0) && (n == -65536) && (i1 == 65536) && (i2 == 0))
    {
      i = 270;
    }
    else
    {
      i = k;
      if (j == -65536)
      {
        i = k;
        if (n == 0)
        {
          i = k;
          if (i1 == 0)
          {
            i = k;
            if (i2 == -65536) {
              i = 180;
            }
          }
        }
      }
    }
    return new TkhdData(m, l1, i);
  }
  
  public static Track parseTrak(Atom.ContainerAtom paramContainerAtom, Atom.LeafAtom paramLeafAtom, long paramLong, DrmInitData paramDrmInitData, boolean paramBoolean1, boolean paramBoolean2)
    throws ParserException
  {
    Object localObject = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
    int i = parseHdlr(getLeafAtomOfTypeTYPE_hdlrdata);
    if (i == -1) {
      return null;
    }
    TkhdData localTkhdData = parseTkhd(getLeafAtomOfTypeTYPE_tkhddata);
    long l1 = -9223372036854775807L;
    if (paramLong == -9223372036854775807L) {
      paramLong = duration;
    }
    long l2 = parseMvhd(data);
    if (paramLong == -9223372036854775807L) {}
    for (paramLong = l1;; paramLong = Util.scaleLargeTimestamp(paramLong, 1000000L, l2)) {
      break;
    }
    paramLeafAtom = ((Atom.ContainerAtom)localObject).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
    localObject = parseMdhd(getLeafAtomOfTypeTYPE_mdhddata);
    paramDrmInitData = parseStsd(getLeafAtomOfTypeTYPE_stsddata, id, rotationDegrees, (String)second, paramDrmInitData, paramBoolean2);
    if (!paramBoolean1)
    {
      paramLeafAtom = parseEdts(paramContainerAtom.getContainerAtomOfType(Atom.TYPE_edts));
      paramContainerAtom = (long[])first;
      paramLeafAtom = (long[])second;
    }
    else
    {
      paramContainerAtom = null;
      paramLeafAtom = null;
    }
    if (format == null) {
      return null;
    }
    return new Track(id, i, ((Long)first).longValue(), l2, paramLong, format, requiredSampleTransformation, trackEncryptionBoxes, nalUnitLengthFieldLength, paramContainerAtom, paramLeafAtom);
  }
  
  public static Metadata parseUdta(Atom.LeafAtom paramLeafAtom, boolean paramBoolean)
  {
    if (paramBoolean) {
      return null;
    }
    paramLeafAtom = data;
    paramLeafAtom.setPosition(8);
    while (paramLeafAtom.bytesLeft() >= 8)
    {
      int i = paramLeafAtom.getPosition();
      int j = paramLeafAtom.readInt();
      if (paramLeafAtom.readInt() == Atom.TYPE_meta)
      {
        paramLeafAtom.setPosition(i);
        return parseMetaAtom(paramLeafAtom, i + j);
      }
      paramLeafAtom.skipBytes(j - 8);
    }
    return null;
  }
  
  private static void parseVideoSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt6)
    throws ParserException
  {
    Object localObject1 = paramDrmInitData;
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    paramParsableByteArray.skipBytes(16);
    int n = paramParsableByteArray.readUnsignedShort();
    int i1 = paramParsableByteArray.readUnsignedShort();
    paramParsableByteArray.skipBytes(50);
    int m = paramParsableByteArray.getPosition();
    int k = m;
    int i2 = Atom.TYPE_encv;
    Object localObject6 = null;
    int i = paramInt1;
    Object localObject2 = localObject1;
    int j = i;
    if (paramInt1 == i2)
    {
      localObject2 = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3);
      if (localObject2 != null)
      {
        i = ((Integer)first).intValue();
        if (paramDrmInitData == null) {
          localObject1 = null;
        } else {
          localObject1 = paramDrmInitData.copyWithSchemeType(second).schemeType);
        }
        trackEncryptionBoxes[paramInt6] = ((TrackEncryptionBox)second);
      }
      paramParsableByteArray.setPosition(m);
      j = i;
      localObject2 = localObject1;
    }
    Object localObject4 = null;
    Object localObject3 = null;
    i = 0;
    float f2 = 1.0F;
    m = -1;
    paramInt6 = k;
    while (paramInt6 - paramInt2 < paramInt3)
    {
      paramParsableByteArray.setPosition(paramInt6);
      paramInt1 = paramParsableByteArray.getPosition();
      i2 = paramParsableByteArray.readInt();
      if ((i2 == 0) && (paramParsableByteArray.getPosition() - paramInt2 == paramInt3)) {
        break;
      }
      boolean bool;
      if (i2 > 0) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      int i3 = paramParsableByteArray.readInt();
      Object localObject5;
      float f1;
      if (i3 == Atom.TYPE_avcC)
      {
        if (localObject6 == null) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        localObject4 = "video/avc";
        paramParsableByteArray.setPosition(paramInt1 + 8);
        AvcConfig localAvcConfig = AvcConfig.parse(paramParsableByteArray);
        localObject6 = initializationData;
        nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        paramDrmInitData = (DrmInitData)localObject4;
        localObject1 = localObject6;
        localObject5 = localObject3;
        k = i;
        f1 = f2;
        paramInt1 = m;
        if (i == 0)
        {
          f1 = pixelWidthAspectRatio;
          paramDrmInitData = (DrmInitData)localObject4;
          localObject1 = localObject6;
          localObject5 = localObject3;
          k = i;
          paramInt1 = m;
        }
      }
      else if (i3 == Atom.TYPE_hvcC)
      {
        if (localObject6 == null) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        paramDrmInitData = "video/hevc";
        paramParsableByteArray.setPosition(paramInt1 + 8);
        localObject4 = HevcConfig.parse(paramParsableByteArray);
        localObject1 = initializationData;
        nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        localObject5 = localObject3;
        k = i;
        f1 = f2;
        paramInt1 = m;
      }
      else
      {
        if (i3 == Atom.TYPE_vpcC)
        {
          if (localObject6 == null) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          if (j == Atom.TYPE_vp08) {}
          for (paramDrmInitData = "video/x-vnd.on2.vp8";; paramDrmInitData = "video/x-vnd.on2.vp9")
          {
            localObject1 = localObject4;
            localObject5 = localObject3;
            k = i;
            f1 = f2;
            paramInt1 = m;
            break;
          }
        }
        if (i3 == Atom.TYPE_d263)
        {
          if (localObject6 == null) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          paramDrmInitData = "video/3gpp";
          localObject1 = localObject4;
          localObject5 = localObject3;
          k = i;
          f1 = f2;
          paramInt1 = m;
        }
        else if (i3 == Atom.TYPE_esds)
        {
          if (localObject6 == null) {
            bool = true;
          } else {
            bool = false;
          }
          Assertions.checkState(bool);
          localObject1 = parseEsdsFromParent(paramParsableByteArray, paramInt1);
          paramDrmInitData = (String)first;
          localObject1 = Collections.singletonList(second);
          localObject5 = localObject3;
          k = i;
          f1 = f2;
          paramInt1 = m;
        }
        else if (i3 == Atom.TYPE_pasp)
        {
          f1 = parsePaspFromParent(paramParsableByteArray, paramInt1);
          k = 1;
          paramDrmInitData = (DrmInitData)localObject6;
          localObject1 = localObject4;
          localObject5 = localObject3;
          paramInt1 = m;
        }
        else if (i3 == Atom.TYPE_sv3d)
        {
          localObject5 = parseProjFromParent(paramParsableByteArray, paramInt1, i2);
          paramDrmInitData = (DrmInitData)localObject6;
          localObject1 = localObject4;
          k = i;
          f1 = f2;
          paramInt1 = m;
        }
        else
        {
          paramDrmInitData = (DrmInitData)localObject6;
          localObject1 = localObject4;
          localObject5 = localObject3;
          k = i;
          f1 = f2;
          paramInt1 = m;
          if (i3 == Atom.TYPE_st3d)
          {
            i3 = paramParsableByteArray.readUnsignedByte();
            paramParsableByteArray.skipBytes(3);
            paramDrmInitData = (DrmInitData)localObject6;
            localObject1 = localObject4;
            localObject5 = localObject3;
            k = i;
            f1 = f2;
            paramInt1 = m;
            if (i3 == 0) {
              switch (paramParsableByteArray.readUnsignedByte())
              {
              default: 
                paramDrmInitData = (DrmInitData)localObject6;
                localObject1 = localObject4;
                localObject5 = localObject3;
                k = i;
                f1 = f2;
                paramInt1 = m;
                break;
              case 3: 
                paramInt1 = 3;
                paramDrmInitData = (DrmInitData)localObject6;
                localObject1 = localObject4;
                localObject5 = localObject3;
                k = i;
                f1 = f2;
                break;
              case 2: 
                paramInt1 = 2;
                paramDrmInitData = (DrmInitData)localObject6;
                localObject1 = localObject4;
                localObject5 = localObject3;
                k = i;
                f1 = f2;
                break;
              case 1: 
                paramInt1 = 1;
                paramDrmInitData = (DrmInitData)localObject6;
                localObject1 = localObject4;
                localObject5 = localObject3;
                k = i;
                f1 = f2;
                break;
              case 0: 
                paramInt1 = 0;
                f1 = f2;
                k = i;
                localObject5 = localObject3;
                localObject1 = localObject4;
                paramDrmInitData = (DrmInitData)localObject6;
              }
            }
          }
        }
      }
      paramInt6 += i2;
      localObject6 = paramDrmInitData;
      localObject4 = localObject1;
      localObject3 = localObject5;
      i = k;
      f2 = f1;
      m = paramInt1;
    }
    if (localObject6 == null) {
      return;
    }
    format = Format.createVideoSampleFormat(Integer.toString(paramInt4), (String)localObject6, null, -1, -1, n, i1, -1.0F, (List)localObject4, paramInt5, f2, localObject3, m, null, (DrmInitData)localObject2);
  }
  
  final class ChunkIterator
  {
    private final ParsableByteArray chunkOffsets;
    private final boolean chunkOffsetsAreLongs;
    public int index;
    public final int length;
    private int nextSamplesPerChunkChangeIndex;
    public int numSamples;
    public long offset;
    private int remainingSamplesPerChunkChanges;
    
    public ChunkIterator(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    {
      chunkOffsets = paramParsableByteArray;
      chunkOffsetsAreLongs = paramBoolean;
      paramParsableByteArray.setPosition(12);
      length = paramParsableByteArray.readUnsignedIntToInt();
      setPosition(12);
      remainingSamplesPerChunkChanges = readUnsignedIntToInt();
      int i = readInt();
      paramBoolean = true;
      if (i != 1) {
        paramBoolean = false;
      }
      Assertions.checkState(paramBoolean, "first_chunk must be 1");
      index = -1;
    }
    
    public boolean moveNext()
    {
      int i = index + 1;
      index = i;
      if (i == length) {
        return false;
      }
      long l;
      if (chunkOffsetsAreLongs) {
        l = chunkOffsets.readUnsignedLongToLong();
      } else {
        l = chunkOffsets.readUnsignedInt();
      }
      offset = l;
      if (index == nextSamplesPerChunkChangeIndex)
      {
        numSamples = readUnsignedIntToInt();
        skipBytes(4);
        i = remainingSamplesPerChunkChanges - 1;
        remainingSamplesPerChunkChanges = i;
        if (i > 0) {
          i = readUnsignedIntToInt() - 1;
        } else {
          i = -1;
        }
        nextSamplesPerChunkChangeIndex = i;
      }
      return true;
    }
  }
  
  abstract interface SampleSizeBox
  {
    public abstract int getSampleCount();
    
    public abstract boolean isFixedSampleSize();
    
    public abstract int readNextSampleSize();
  }
  
  final class StsdData
  {
    public static final int STSD_HEADER_SIZE = 8;
    public Format format;
    public int nalUnitLengthFieldLength;
    public int requiredSampleTransformation;
    public final TrackEncryptionBox[] trackEncryptionBoxes;
    
    public StsdData()
    {
      trackEncryptionBoxes = new TrackEncryptionBox[this$1];
      requiredSampleTransformation = 0;
    }
  }
  
  final class StszSampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private final ParsableByteArray data;
    private final int fixedSampleSize;
    private final int sampleCount;
    
    public StszSampleSizeBox()
    {
      data = data;
      data.setPosition(12);
      fixedSampleSize = data.readUnsignedIntToInt();
      sampleCount = data.readUnsignedIntToInt();
    }
    
    public int getSampleCount()
    {
      return sampleCount;
    }
    
    public boolean isFixedSampleSize()
    {
      return fixedSampleSize != 0;
    }
    
    public int readNextSampleSize()
    {
      if (fixedSampleSize == 0) {
        return data.readUnsignedIntToInt();
      }
      return fixedSampleSize;
    }
  }
  
  final class Stz2SampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private int currentByte;
    private final ParsableByteArray data;
    private final int fieldSize;
    private final int sampleCount;
    private int sampleIndex;
    
    public Stz2SampleSizeBox()
    {
      data = data;
      data.setPosition(12);
      fieldSize = (data.readUnsignedIntToInt() & 0xFF);
      sampleCount = data.readUnsignedIntToInt();
    }
    
    public int getSampleCount()
    {
      return sampleCount;
    }
    
    public boolean isFixedSampleSize()
    {
      return false;
    }
    
    public int readNextSampleSize()
    {
      if (fieldSize == 8) {
        return data.readUnsignedByte();
      }
      if (fieldSize == 16) {
        return data.readUnsignedShort();
      }
      int i = sampleIndex;
      sampleIndex = (i + 1);
      if (i % 2 == 0)
      {
        currentByte = data.readUnsignedByte();
        return (currentByte & 0xF0) >> 4;
      }
      return currentByte & 0xF;
    }
  }
  
  final class TkhdData
  {
    private final long duration;
    private final int id;
    private final int rotationDegrees;
    
    public TkhdData(long paramLong, int paramInt)
    {
      id = this$1;
      duration = paramLong;
      rotationDegrees = paramInt;
    }
  }
}
