package com.google.android.exoplayer2.extractor.ClickListeners;

import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class PsshAtomUtil
{
  private static final String TAG = "PsshAtomUtil";
  
  private PsshAtomUtil() {}
  
  public static byte[] buildPsshAtom(UUID paramUUID, byte[] paramArrayOfByte)
  {
    return buildPsshAtom(paramUUID, null, paramArrayOfByte);
  }
  
  public static byte[] buildPsshAtom(UUID paramUUID, UUID[] paramArrayOfUUID, byte[] paramArrayOfByte)
  {
    int j = 0;
    if (paramArrayOfByte != null) {
      i = paramArrayOfByte.length;
    } else {
      i = 0;
    }
    int k = i + 32;
    int i = k;
    if (paramArrayOfUUID != null) {
      i = k + (paramArrayOfUUID.length * 16 + 4);
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(i);
    localByteBuffer.putInt(i);
    localByteBuffer.putInt(Atom.TYPE_pssh);
    if (paramArrayOfUUID != null) {
      i = 16777216;
    } else {
      i = 0;
    }
    localByteBuffer.putInt(i);
    localByteBuffer.putLong(paramUUID.getMostSignificantBits());
    localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
    if (paramArrayOfUUID != null)
    {
      localByteBuffer.putInt(paramArrayOfUUID.length);
      k = paramArrayOfUUID.length;
      i = j;
      while (i < k)
      {
        paramUUID = paramArrayOfUUID[i];
        localByteBuffer.putLong(paramUUID.getMostSignificantBits());
        localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
        i += 1;
      }
    }
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
    {
      localByteBuffer.putInt(paramArrayOfByte.length);
      localByteBuffer.put(paramArrayOfByte);
    }
    return localByteBuffer.array();
  }
  
  public static boolean isPsshAtom(byte[] paramArrayOfByte)
  {
    return parsePsshAtom(paramArrayOfByte) != null;
  }
  
  private static PsshAtom parsePsshAtom(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte);
    if (paramArrayOfByte.limit() < 32) {
      return null;
    }
    paramArrayOfByte.setPosition(0);
    if (paramArrayOfByte.readInt() != paramArrayOfByte.bytesLeft() + 4) {
      return null;
    }
    if (paramArrayOfByte.readInt() != Atom.TYPE_pssh) {
      return null;
    }
    int i = Atom.parseFullAtomVersion(paramArrayOfByte.readInt());
    if (i > 1)
    {
      paramArrayOfByte = new StringBuilder();
      paramArrayOfByte.append("Unsupported pssh version: ");
      paramArrayOfByte.append(i);
      Log.w("PsshAtomUtil", paramArrayOfByte.toString());
      return null;
    }
    UUID localUUID = new UUID(paramArrayOfByte.readLong(), paramArrayOfByte.readLong());
    if (i == 1) {
      paramArrayOfByte.skipBytes(paramArrayOfByte.readUnsignedIntToInt() * 16);
    }
    int j = paramArrayOfByte.readUnsignedIntToInt();
    if (j != paramArrayOfByte.bytesLeft()) {
      return null;
    }
    byte[] arrayOfByte = new byte[j];
    paramArrayOfByte.readBytes(arrayOfByte, 0, j);
    return new PsshAtom(localUUID, i, arrayOfByte);
  }
  
  public static byte[] parseSchemeSpecificData(byte[] paramArrayOfByte, UUID paramUUID)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    if ((paramUUID != null) && (!paramUUID.equals(uuid)))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UUID mismatch. Expected: ");
      localStringBuilder.append(paramUUID);
      localStringBuilder.append(", got: ");
      localStringBuilder.append(uuid);
      localStringBuilder.append(".");
      Log.w("PsshAtomUtil", localStringBuilder.toString());
      return null;
    }
    return schemeData;
  }
  
  public static UUID parseUuid(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    return uuid;
  }
  
  public static int parseVersion(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return -1;
    }
    return version;
  }
  
  class PsshAtom
  {
    private final byte[] schemeData;
    private final int version;
    
    public PsshAtom(int paramInt, byte[] paramArrayOfByte)
    {
      version = paramInt;
      schemeData = paramArrayOfByte;
    }
  }
}
