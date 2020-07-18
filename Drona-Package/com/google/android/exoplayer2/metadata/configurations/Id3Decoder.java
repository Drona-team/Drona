package com.google.android.exoplayer2.metadata.configurations;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Id3Decoder
  implements MetadataDecoder
{
  private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
  private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
  private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
  private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
  private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
  private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
  private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
  private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
  public static final int ID3_HEADER_LENGTH = 10;
  public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
  private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
  private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
  private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
  public static final FramePredicate NO_FRAMES_PREDICATE = -..Lambda.Id3Decoder.7M0gB-IGKaTbyTVX-WCb62bIHyc.INSTANCE;
  private static final String PAGE_KEY = "Id3Decoder";
  @Nullable
  private final FramePredicate framePredicate;
  
  public Id3Decoder()
  {
    this(null);
  }
  
  public Id3Decoder(FramePredicate paramFramePredicate)
  {
    framePredicate = paramFramePredicate;
  }
  
  private static byte[] copyOfRangeIfValid(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= paramInt1) {
      return Util.EMPTY_BYTE_ARRAY;
    }
    return Arrays.copyOfRange(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private static ApicFrame decodeApicFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str2 = getCharsetName(i);
    paramInt1 -= 1;
    byte[] arrayOfByte = new byte[paramInt1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt1);
    String str1;
    if (paramInt2 == 2)
    {
      paramParsableByteArray = new StringBuilder();
      paramParsableByteArray.append("image/");
      paramParsableByteArray.append(Util.toLowerInvariant(new String(arrayOfByte, 0, 3, "ISO-8859-1")));
      str1 = paramParsableByteArray.toString();
      paramParsableByteArray = str1;
      if ("image/jpg".equals(str1)) {
        paramParsableByteArray = "image/jpeg";
      }
      paramInt2 = 2;
    }
    else
    {
      paramInt2 = indexOfZeroByte(arrayOfByte, 0);
      paramInt1 = paramInt2;
      str1 = Util.toLowerInvariant(new String(arrayOfByte, 0, paramInt2, "ISO-8859-1"));
      paramParsableByteArray = str1;
      paramInt2 = paramInt1;
      if (str1.indexOf('/') == -1)
      {
        paramParsableByteArray = new StringBuilder();
        paramParsableByteArray.append("image/");
        paramParsableByteArray.append(str1);
        paramParsableByteArray = paramParsableByteArray.toString();
        paramInt2 = paramInt1;
      }
    }
    paramInt1 = arrayOfByte[(paramInt2 + 1)];
    paramInt2 += 2;
    int j = indexOfEos(arrayOfByte, paramInt2, i);
    return new ApicFrame(paramParsableByteArray, new String(arrayOfByte, paramInt2, j - paramInt2, str2), paramInt1 & 0xFF, copyOfRangeIfValid(arrayOfByte, j + delimiterLength(i), arrayOfByte.length));
  }
  
  private static BinaryFrame decodeBinaryFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new BinaryFrame(paramString, arrayOfByte);
  }
  
  private static ChapterFrame decodeChapterFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, FramePredicate paramFramePredicate)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.getPosition();
    int j = indexOfZeroByte(data, i);
    String str = new String(data, i, j - i, "ISO-8859-1");
    paramParsableByteArray.setPosition(j + 1);
    j = paramParsableByteArray.readInt();
    int k = paramParsableByteArray.readInt();
    long l2 = paramParsableByteArray.readUnsignedInt();
    long l1 = l2;
    if (l2 == 4294967295L) {
      l1 = -1L;
    }
    long l3 = paramParsableByteArray.readUnsignedInt();
    l2 = l3;
    if (l3 == 4294967295L) {
      l2 = -1L;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < i + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3, paramFramePredicate);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterFrame(str, j, k, l1, l2, paramParsableByteArray);
  }
  
  private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, FramePredicate paramFramePredicate)
    throws UnsupportedEncodingException
  {
    int j = paramParsableByteArray.getPosition();
    int i = indexOfZeroByte(data, j);
    String str = new String(data, j, i - j, "ISO-8859-1");
    paramParsableByteArray.setPosition(i + 1);
    int k = paramParsableByteArray.readUnsignedByte();
    i = 0;
    boolean bool1;
    if ((k & 0x2) != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    boolean bool2;
    if ((k & 0x1) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    k = paramParsableByteArray.readUnsignedByte();
    String[] arrayOfString = new String[k];
    while (i < k)
    {
      int m = paramParsableByteArray.getPosition();
      int n = indexOfZeroByte(data, m);
      arrayOfString[i] = new String(data, m, n - m, "ISO-8859-1");
      paramParsableByteArray.setPosition(n + 1);
      i += 1;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < j + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3, paramFramePredicate);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterTocFrame(str, bool1, bool2, arrayOfString, paramParsableByteArray);
  }
  
  private static CommentFrame decodeCommentFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 4) {
      return null;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    Object localObject = new byte[3];
    paramParsableByteArray.readBytes((byte[])localObject, 0, 3);
    localObject = new String((byte[])localObject, 0, 3);
    paramInt -= 4;
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return new CommentFrame((String)localObject, paramParsableByteArray, decodeStringIfValid(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i), str));
  }
  
  private static Id3Frame decodeFrame(int paramInt1, ParsableByteArray paramParsableByteArray, boolean paramBoolean, int paramInt2, FramePredicate paramFramePredicate)
  {
    int i5 = paramParsableByteArray.readUnsignedByte();
    int i6 = paramParsableByteArray.readUnsignedByte();
    int i7 = paramParsableByteArray.readUnsignedByte();
    int i2;
    if (paramInt1 >= 3) {
      i2 = paramParsableByteArray.readUnsignedByte();
    } else {
      i2 = 0;
    }
    int j;
    int i;
    if (paramInt1 == 4)
    {
      j = paramParsableByteArray.readUnsignedIntToInt();
      i = j;
      if (!paramBoolean) {
        i = (j >> 24 & 0xFF) << 21 | j & 0xFF | (j >> 8 & 0xFF) << 7 | (j >> 16 & 0xFF) << 14;
      }
    }
    int i3;
    for (;;)
    {
      i3 = i;
      break;
      if (paramInt1 == 3) {
        i = paramParsableByteArray.readUnsignedIntToInt();
      } else {
        i = paramParsableByteArray.readUnsignedInt24();
      }
    }
    int i1;
    if (paramInt1 >= 3) {
      i1 = paramParsableByteArray.readUnsignedShort();
    } else {
      i1 = 0;
    }
    if ((i5 == 0) && (i6 == 0) && (i7 == 0) && (i2 == 0) && (i3 == 0) && (i1 == 0))
    {
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
    }
    int i4 = paramParsableByteArray.getPosition() + i3;
    if (i4 > paramParsableByteArray.limit())
    {
      Log.w("Id3Decoder", "Frame size exceeds remaining tag data");
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
    }
    if ((paramFramePredicate != null) && (!paramFramePredicate.evaluate(paramInt1, i5, i6, i7, i2)))
    {
      paramParsableByteArray.setPosition(i4);
      return null;
    }
    int k;
    int n;
    int m;
    if (paramInt1 == 3)
    {
      if ((i1 & 0x80) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i1 & 0x40) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      if ((i1 & 0x20) != 0) {
        k = 1;
      } else {
        k = 0;
      }
      n = j;
      i1 = 0;
      m = i;
      j = k;
      k = i;
      i = i1;
    }
    else if (paramInt1 == 4)
    {
      if ((i1 & 0x40) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i1 & 0x8) != 0) {
        k = 1;
      } else {
        k = 0;
      }
      if ((i1 & 0x4) != 0) {
        m = 1;
      } else {
        m = 0;
      }
      if ((i1 & 0x2) != 0) {
        n = 1;
      } else {
        n = 0;
      }
      if ((i1 & 0x1) != 0) {
        i1 = 1;
      } else {
        i1 = 0;
      }
      j = i;
      i = n;
      n = m;
      m = i1;
    }
    else
    {
      k = 0;
      m = 0;
      j = 0;
      i = 0;
      n = 0;
    }
    if ((k == 0) && (n == 0))
    {
      k = i3;
      if (j != 0)
      {
        k = i3 - 1;
        paramParsableByteArray.skipBytes(1);
      }
      j = k;
      if (m != 0)
      {
        j = k - 4;
        paramParsableByteArray.skipBytes(4);
      }
      k = j;
      if (i != 0) {
        k = removeUnsynchronization(paramParsableByteArray, j);
      }
      if ((i5 != 84) || (i6 != 88) || (i7 != 88) || ((paramInt1 != 2) && (i2 != 88))) {}
    }
    try
    {
      try
      {
        paramFramePredicate = decodeTxxxFrame(paramParsableByteArray, k);
      }
      catch (Throwable paramFramePredicate)
      {
        break label1118;
      }
      if (i5 == 84) {
        paramFramePredicate = decodeTextInformationFrame(paramParsableByteArray, k, getFrameId(paramInt1, i5, i6, i7, i2));
      } else if ((i5 == 87) && (i6 == 88) && (i7 == 88) && ((paramInt1 == 2) || (i2 == 88))) {
        paramFramePredicate = decodeWxxxFrame(paramParsableByteArray, k);
      } else if (i5 == 87) {
        paramFramePredicate = decodeUrlLinkFrame(paramParsableByteArray, k, getFrameId(paramInt1, i5, i6, i7, i2));
      } else if ((i5 == 80) && (i6 == 82) && (i7 == 73) && (i2 == 86)) {
        paramFramePredicate = decodePrivFrame(paramParsableByteArray, k);
      } else if ((i5 == 71) && (i6 == 69) && (i7 == 79) && ((i2 == 66) || (paramInt1 == 2))) {
        paramFramePredicate = decodeGeobFrame(paramParsableByteArray, k);
      } else if (paramInt1 == 2 ? (i5 == 80) && (i6 == 73) && (i7 == 67) : (i5 == 65) && (i6 == 80) && (i7 == 73) && (i2 == 67)) {
        paramFramePredicate = decodeApicFrame(paramParsableByteArray, k, paramInt1);
      } else if ((i5 == 67) && (i6 == 79) && (i7 == 77) && ((i2 == 77) || (paramInt1 == 2))) {
        paramFramePredicate = decodeCommentFrame(paramParsableByteArray, k);
      } else if ((i5 == 67) && (i6 == 72) && (i7 == 65) && (i2 == 80)) {
        paramFramePredicate = decodeChapterFrame(paramParsableByteArray, k, paramInt1, paramBoolean, paramInt2, paramFramePredicate);
      } else if ((i5 == 67) && (i6 == 84) && (i7 == 79) && (i2 == 67)) {
        paramFramePredicate = decodeChapterTOCFrame(paramParsableByteArray, k, paramInt1, paramBoolean, paramInt2, paramFramePredicate);
      } else if ((i5 == 77) && (i6 == 76) && (i7 == 76) && (i2 == 84)) {
        paramFramePredicate = decodeMlltFrame(paramParsableByteArray, k);
      } else {
        paramFramePredicate = decodeBinaryFrame(paramParsableByteArray, k, getFrameId(paramInt1, i5, i6, i7, i2));
      }
      if (paramFramePredicate == null)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Failed to decode frame: id=");
        localStringBuilder.append(getFrameId(paramInt1, i5, i6, i7, i2));
        localStringBuilder.append(", frameSize=");
        localStringBuilder.append(k);
        Log.w("Id3Decoder", localStringBuilder.toString());
      }
      paramParsableByteArray.setPosition(i4);
      return paramFramePredicate;
    }
    catch (UnsupportedEncodingException paramFramePredicate)
    {
      for (;;) {}
    }
    Log.w("Id3Decoder", "Unsupported character encoding");
    paramParsableByteArray.setPosition(i4);
    return null;
    label1118:
    paramParsableByteArray.setPosition(i4);
    throw paramFramePredicate;
    Log.w("Id3Decoder", "Skipping unsupported compressed or encrypted frame");
    paramParsableByteArray.setPosition(i4);
    return null;
  }
  
  private static GeobFrame decodeGeobFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    paramInt -= 1;
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, "ISO-8859-1");
    paramInt += 1;
    int j = indexOfEos(arrayOfByte, paramInt, i);
    String str2 = decodeStringIfValid(arrayOfByte, paramInt, j, str1);
    paramInt = j + delimiterLength(i);
    j = indexOfEos(arrayOfByte, paramInt, i);
    return new GeobFrame(paramParsableByteArray, str2, decodeStringIfValid(arrayOfByte, paramInt, j, str1), copyOfRangeIfValid(arrayOfByte, j + delimiterLength(i), arrayOfByte.length));
  }
  
  private static Id3Header decodeHeader(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() < 10)
    {
      Log.w("Id3Decoder", "Data too short to be an ID3 tag");
      return null;
    }
    int i = paramParsableByteArray.readUnsignedInt24();
    if (i != ID3_TAG)
    {
      paramParsableByteArray = new StringBuilder();
      paramParsableByteArray.append("Unexpected first three bytes of ID3 tag header: ");
      paramParsableByteArray.append(i);
      Log.w("Id3Decoder", paramParsableByteArray.toString());
      return null;
    }
    int n = paramParsableByteArray.readUnsignedByte();
    boolean bool = true;
    paramParsableByteArray.skipBytes(1);
    int i1 = paramParsableByteArray.readUnsignedByte();
    int m = paramParsableByteArray.readSynchSafeInt();
    i = m;
    int j;
    if (n == 2)
    {
      if ((i1 & 0x40) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      if (j != 0)
      {
        Log.w("Id3Decoder", "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
        return null;
      }
    }
    else if (n == 3)
    {
      if ((i1 & 0x40) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      if (j != 0)
      {
        i = paramParsableByteArray.readInt();
        paramParsableByteArray.skipBytes(i);
        i = m - (i + 4);
      }
    }
    else
    {
      if (n != 4) {
        break label277;
      }
      int k;
      if ((i1 & 0x40) != 0) {
        k = 1;
      } else {
        k = 0;
      }
      j = i;
      if (k != 0)
      {
        i = paramParsableByteArray.readSynchSafeInt();
        paramParsableByteArray.skipBytes(i - 4);
        j = m - i;
      }
      if ((i1 & 0x10) != 0) {
        k = 1;
      } else {
        k = 0;
      }
      i = j;
      if (k != 0) {
        i = j - 10;
      }
    }
    if ((n >= 4) || ((i1 & 0x80) == 0)) {
      bool = false;
    }
    return new Id3Header(n, bool, i);
    label277:
    paramParsableByteArray = new StringBuilder();
    paramParsableByteArray.append("Skipped ID3 tag with unsupported majorVersion=");
    paramParsableByteArray.append(n);
    Log.w("Id3Decoder", paramParsableByteArray.toString());
    return null;
  }
  
  private static MlltFrame decodeMlltFrame(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.readUnsignedInt24();
    int k = paramParsableByteArray.readUnsignedInt24();
    int m = paramParsableByteArray.readUnsignedByte();
    int n = paramParsableByteArray.readUnsignedByte();
    ParsableBitArray localParsableBitArray = new ParsableBitArray();
    localParsableBitArray.reset(paramParsableByteArray);
    int i1 = (paramInt - 10) * 8 / (m + n);
    paramParsableByteArray = new int[i1];
    int[] arrayOfInt = new int[i1];
    paramInt = 0;
    while (paramInt < i1)
    {
      int i2 = localParsableBitArray.readBits(m);
      int i3 = localParsableBitArray.readBits(n);
      paramParsableByteArray[paramInt] = i2;
      arrayOfInt[paramInt] = i3;
      paramInt += 1;
    }
    return new MlltFrame(i, j, k, paramParsableByteArray, arrayOfInt);
  }
  
  private static PrivFrame decodePrivFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    return new PrivFrame(new String(arrayOfByte, 0, paramInt, "ISO-8859-1"), copyOfRangeIfValid(arrayOfByte, paramInt + 1, arrayOfByte.length));
  }
  
  private static String decodeStringIfValid(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
    throws UnsupportedEncodingException
  {
    if ((paramInt2 > paramInt1) && (paramInt2 <= paramArrayOfByte.length)) {
      return new String(paramArrayOfByte, paramInt1, paramInt2 - paramInt1, paramString);
    }
    return "";
  }
  
  private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    if (paramInt < 1) {
      return null;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    paramInt -= 1;
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new TextInformationFrame(paramString, null, new String(arrayOfByte, 0, indexOfEos(arrayOfByte, 0, i), str));
  }
  
  private static TextInformationFrame decodeTxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 1) {
      return null;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    paramInt -= 1;
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return new TextInformationFrame("TXXX", paramParsableByteArray, decodeStringIfValid(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i), str));
  }
  
  private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new UrlLinkFrame(paramString, null, new String(arrayOfByte, 0, indexOfZeroByte(arrayOfByte, 0), "ISO-8859-1"));
  }
  
  private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 1) {
      return null;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    paramInt -= 1;
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return new UrlLinkFrame("WXXX", paramParsableByteArray, decodeStringIfValid(arrayOfByte, paramInt, indexOfZeroByte(arrayOfByte, paramInt), "ISO-8859-1"));
  }
  
  private static int delimiterLength(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 3)) {
      return 2;
    }
    return 1;
  }
  
  private static String getCharsetName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ISO-8859-1";
    case 3: 
      return "UTF-8";
    case 2: 
      return "UTF-16BE";
    }
    return "UTF-16";
  }
  
  private static String getFrameId(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt1 == 2) {
      return String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
    }
    return String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
  }
  
  private static int indexOfEos(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = indexOfZeroByte(paramArrayOfByte, paramInt1);
    paramInt1 = i;
    if (paramInt2 != 0)
    {
      if (paramInt2 == 3) {
        return i;
      }
      while (paramInt1 < paramArrayOfByte.length - 1)
      {
        if ((paramInt1 % 2 == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 0)) {
          return paramInt1;
        }
        paramInt1 = indexOfZeroByte(paramArrayOfByte, paramInt1 + 1);
      }
      return paramArrayOfByte.length;
    }
    return i;
  }
  
  private static int indexOfZeroByte(byte[] paramArrayOfByte, int paramInt)
  {
    while (paramInt < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[paramInt] == 0) {
        return paramInt;
      }
      paramInt += 1;
    }
    return paramArrayOfByte.length;
  }
  
  private static int removeUnsynchronization(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    byte[] arrayOfByte = data;
    int j = paramParsableByteArray.getPosition();
    for (;;)
    {
      int k = j + 1;
      if (k >= paramInt) {
        break;
      }
      int i = paramInt;
      if ((arrayOfByte[j] & 0xFF) == 255)
      {
        i = paramInt;
        if (arrayOfByte[k] == 0)
        {
          System.arraycopy(arrayOfByte, j + 2, arrayOfByte, k, paramInt - j - 2);
          i = paramInt - 1;
        }
      }
      j = k;
      paramInt = i;
    }
    return paramInt;
  }
  
  private static boolean validateFrames(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int n = paramParsableByteArray.getPosition();
    try
    {
      for (;;)
      {
        int i = paramParsableByteArray.bytesLeft();
        int m = 1;
        if (i < paramInt2) {
          break;
        }
        long l1;
        if (paramInt1 >= 3)
        {
          i = paramParsableByteArray.readInt();
          l1 = paramParsableByteArray.readUnsignedInt();
          k = paramParsableByteArray.readUnsignedShort();
        }
        else
        {
          i = paramParsableByteArray.readUnsignedInt24();
          j = paramParsableByteArray.readUnsignedInt24();
          l1 = j;
          k = 0;
        }
        if ((i == 0) && (l1 == 0L) && (k == 0))
        {
          paramParsableByteArray.setPosition(n);
          return true;
        }
        long l2 = l1;
        if (paramInt1 == 4)
        {
          l2 = l1;
          if (!paramBoolean)
          {
            if ((0x808080 & l1) != 0L)
            {
              paramParsableByteArray.setPosition(n);
              return false;
            }
            l2 = (l1 >> 24 & 0xFF) << 21 | l1 & 0xFF | (l1 >> 8 & 0xFF) << 7 | (l1 >> 16 & 0xFF) << 14;
          }
        }
        if (paramInt1 == 4)
        {
          if ((k & 0x40) != 0) {
            j = 1;
          } else {
            j = 0;
          }
          i = j;
          if ((k & 0x1) == 0) {}
        }
        else
        {
          for (;;)
          {
            k = 1;
            i = j;
            j = k;
            break label266;
            if (paramInt1 != 3) {
              break;
            }
            if ((k & 0x20) != 0) {
              j = 1;
            } else {
              j = 0;
            }
            i = j;
            if ((k & 0x80) == 0) {
              break label263;
            }
          }
          i = 0;
        }
        label263:
        int j = 0;
        label266:
        if (i != 0) {
          i = m;
        } else {
          i = 0;
        }
        int k = i;
        if (j != 0) {
          k = i + 4;
        }
        if (l2 < k)
        {
          paramParsableByteArray.setPosition(n);
          return false;
        }
        i = paramParsableByteArray.bytesLeft();
        if (i < l2)
        {
          paramParsableByteArray.setPosition(n);
          return false;
        }
        i = (int)l2;
        paramParsableByteArray.skipBytes(i);
      }
      paramParsableByteArray.setPosition(n);
      return true;
    }
    catch (Throwable localThrowable)
    {
      paramParsableByteArray.setPosition(n);
      throw localThrowable;
    }
  }
  
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    paramMetadataInputBuffer = data;
    return decode(paramMetadataInputBuffer.array(), paramMetadataInputBuffer.limit());
  }
  
  public Metadata decode(byte[] paramArrayOfByte, int paramInt)
  {
    Object localObject = new ArrayList();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(paramArrayOfByte, paramInt);
    paramArrayOfByte = decodeHeader(localParsableByteArray);
    if (paramArrayOfByte == null) {
      return null;
    }
    int j = localParsableByteArray.getPosition();
    if (majorVersion == 2) {
      paramInt = 6;
    } else {
      paramInt = 10;
    }
    int i = framesSize;
    if (isUnsynchronized) {
      i = removeUnsynchronization(localParsableByteArray, framesSize);
    }
    localParsableByteArray.setLimit(j + i);
    i = majorVersion;
    boolean bool = false;
    if (!validateFrames(localParsableByteArray, i, paramInt, false)) {
      if ((majorVersion == 4) && (validateFrames(localParsableByteArray, 4, paramInt, true)))
      {
        bool = true;
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Failed to validate ID3 tag with majorVersion=");
        ((StringBuilder)localObject).append(majorVersion);
        Log.w("Id3Decoder", ((StringBuilder)localObject).toString());
        return null;
      }
    }
    while (localParsableByteArray.bytesLeft() >= paramInt)
    {
      Id3Frame localId3Frame = decodeFrame(majorVersion, localParsableByteArray, bool, paramInt, framePredicate);
      if (localId3Frame != null) {
        ((List)localObject).add(localId3Frame);
      }
    }
    return new Metadata((List)localObject);
  }
  
  public abstract interface FramePredicate
  {
    public abstract boolean evaluate(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  }
  
  final class Id3Header
  {
    private final int framesSize;
    private final boolean isUnsynchronized;
    private final int majorVersion;
    
    public Id3Header(boolean paramBoolean, int paramInt)
    {
      majorVersion = this$1;
      isUnsynchronized = paramBoolean;
      framesSize = paramInt;
    }
  }
}
