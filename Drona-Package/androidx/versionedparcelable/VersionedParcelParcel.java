package androidx.versionedparcelable;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.SparseIntArray;
import androidx.annotation.RestrictTo;
import androidx.collection.ArrayMap;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
class VersionedParcelParcel
  extends VersionedParcel
{
  private static final boolean DEBUG = false;
  private static final String PAGE_KEY = "VersionedParcelParcel";
  private int mCurrentField = -1;
  private final int mEnd;
  private int mFieldId = -1;
  private int mNextRead = 0;
  private final int mOffset;
  private final Parcel mParcel;
  private final SparseIntArray mPositionLookup = new SparseIntArray();
  private final String mPrefix;
  
  VersionedParcelParcel(Parcel paramParcel)
  {
    this(paramParcel, paramParcel.dataPosition(), paramParcel.dataSize(), "", new ArrayMap(), new ArrayMap(), new ArrayMap());
  }
  
  private VersionedParcelParcel(Parcel paramParcel, int paramInt1, int paramInt2, String paramString, ArrayMap paramArrayMap1, ArrayMap paramArrayMap2, ArrayMap paramArrayMap3)
  {
    super(paramArrayMap1, paramArrayMap2, paramArrayMap3);
    mParcel = paramParcel;
    mOffset = paramInt1;
    mEnd = paramInt2;
    mNextRead = mOffset;
    mPrefix = paramString;
  }
  
  public void closeField()
  {
    if (mCurrentField >= 0)
    {
      int i = mPositionLookup.get(mCurrentField);
      int j = mParcel.dataPosition();
      mParcel.setDataPosition(i);
      mParcel.writeInt(j - i);
      mParcel.setDataPosition(j);
    }
  }
  
  protected VersionedParcel createSubParcel()
  {
    Parcel localParcel = mParcel;
    int j = mParcel.dataPosition();
    if (mNextRead == mOffset) {}
    for (int i = mEnd;; i = mNextRead) {
      break;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(mPrefix);
    localStringBuilder.append("  ");
    return new VersionedParcelParcel(localParcel, j, i, localStringBuilder.toString(), mReadCache, mWriteCache, mParcelizerCache);
  }
  
  public boolean readBoolean()
  {
    return mParcel.readInt() != 0;
  }
  
  public Bundle readBundle()
  {
    return mParcel.readBundle(getClass().getClassLoader());
  }
  
  public byte[] readByteArray()
  {
    int i = mParcel.readInt();
    if (i < 0) {
      return null;
    }
    byte[] arrayOfByte = new byte[i];
    mParcel.readByteArray(arrayOfByte);
    return arrayOfByte;
  }
  
  protected CharSequence readCharSequence()
  {
    return (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(mParcel);
  }
  
  public double readDouble()
  {
    return mParcel.readDouble();
  }
  
  public boolean readField(int paramInt)
  {
    while (mNextRead < mEnd)
    {
      if (mFieldId == paramInt) {
        return true;
      }
      if (String.valueOf(mFieldId).compareTo(String.valueOf(paramInt)) > 0) {
        return false;
      }
      mParcel.setDataPosition(mNextRead);
      int i = mParcel.readInt();
      mFieldId = mParcel.readInt();
      mNextRead += i;
    }
    return mFieldId == paramInt;
  }
  
  public float readFloat()
  {
    return mParcel.readFloat();
  }
  
  public int readInt()
  {
    return mParcel.readInt();
  }
  
  public long readLong()
  {
    return mParcel.readLong();
  }
  
  public Parcelable readParcelable()
  {
    return mParcel.readParcelable(getClass().getClassLoader());
  }
  
  public String readString()
  {
    return mParcel.readString();
  }
  
  public IBinder readStrongBinder()
  {
    return mParcel.readStrongBinder();
  }
  
  public void setOutputField(int paramInt)
  {
    closeField();
    mCurrentField = paramInt;
    mPositionLookup.put(paramInt, mParcel.dataPosition());
    writeInt(0);
    writeInt(paramInt);
  }
  
  public void writeBoolean(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void writeBundle(Bundle paramBundle)
  {
    mParcel.writeBundle(paramBundle);
  }
  
  public void writeByteArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      mParcel.writeInt(paramArrayOfByte.length);
      mParcel.writeByteArray(paramArrayOfByte);
      return;
    }
    mParcel.writeInt(-1);
  }
  
  public void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte != null)
    {
      mParcel.writeInt(paramArrayOfByte.length);
      mParcel.writeByteArray(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    mParcel.writeInt(-1);
  }
  
  protected void writeCharSequence(CharSequence paramCharSequence)
  {
    TextUtils.writeToParcel(paramCharSequence, mParcel, 0);
  }
  
  public void writeDouble(double paramDouble)
  {
    mParcel.writeDouble(paramDouble);
  }
  
  public void writeFloat(float paramFloat)
  {
    mParcel.writeFloat(paramFloat);
  }
  
  public void writeInt(int paramInt)
  {
    mParcel.writeInt(paramInt);
  }
  
  public void writeLong(long paramLong)
  {
    mParcel.writeLong(paramLong);
  }
  
  public void writeParcelable(Parcelable paramParcelable)
  {
    mParcel.writeParcelable(paramParcelable, 0);
  }
  
  public void writeString(String paramString)
  {
    mParcel.writeString(paramString);
  }
  
  public void writeStrongBinder(IBinder paramIBinder)
  {
    mParcel.writeStrongBinder(paramIBinder);
  }
  
  public void writeStrongInterface(IInterface paramIInterface)
  {
    mParcel.writeStrongInterface(paramIInterface);
  }
}
