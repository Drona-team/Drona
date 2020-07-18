package androidx.versionedparcelable;

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;
import androidx.annotation.RestrictTo;
import androidx.collection.ArrayMap;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
class VersionedParcelStream
  extends VersionedParcel
{
  private static final int TYPE_BOOLEAN = 5;
  private static final int TYPE_BOOLEAN_ARRAY = 6;
  private static final int TYPE_DOUBLE = 7;
  private static final int TYPE_DOUBLE_ARRAY = 8;
  private static final int TYPE_FLOAT = 13;
  private static final int TYPE_FLOAT_ARRAY = 14;
  private static final int TYPE_INT = 9;
  private static final int TYPE_INT_ARRAY = 10;
  private static final int TYPE_LONG = 11;
  private static final int TYPE_LONG_ARRAY = 12;
  private static final int TYPE_NULL = 0;
  private static final int TYPE_STRING = 3;
  private static final int TYPE_STRING_ARRAY = 4;
  private static final int TYPE_SUB_BUNDLE = 1;
  private static final int TYPE_SUB_PERSISTABLE_BUNDLE = 2;
  private static final Charset UTF_16 = Charset.forName("UTF-16");
  int mCount = 0;
  private DataInputStream mCurrentInput;
  private DataOutputStream mCurrentOutput;
  private FieldBuffer mFieldBuffer;
  private int mFieldId = -1;
  int mFieldSize = -1;
  private boolean mIgnoreParcelables;
  private final DataInputStream mMasterInput;
  private final DataOutputStream mMasterOutput;
  
  public VersionedParcelStream(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    this(paramInputStream, paramOutputStream, new ArrayMap(), new ArrayMap(), new ArrayMap());
  }
  
  private VersionedParcelStream(InputStream paramInputStream, OutputStream paramOutputStream, ArrayMap paramArrayMap1, ArrayMap paramArrayMap2, ArrayMap paramArrayMap3)
  {
    super(paramArrayMap1, paramArrayMap2, paramArrayMap3);
    paramArrayMap1 = null;
    if (paramInputStream != null) {
      paramInputStream = new DataInputStream(new FilterInputStream(paramInputStream)
      {
        public int read()
          throws IOException
        {
          if ((mFieldSize != -1) && (mCount >= mFieldSize)) {
            throw new IOException();
          }
          int i = super.read();
          VersionedParcelStream localVersionedParcelStream = VersionedParcelStream.this;
          mCount += 1;
          return i;
        }
        
        public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
          throws IOException
        {
          if ((mFieldSize != -1) && (mCount >= mFieldSize)) {
            throw new IOException();
          }
          paramAnonymousInt1 = super.read(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
          if (paramAnonymousInt1 > 0)
          {
            paramAnonymousArrayOfByte = VersionedParcelStream.this;
            mCount += paramAnonymousInt1;
          }
          return paramAnonymousInt1;
        }
        
        public long skip(long paramAnonymousLong)
          throws IOException
        {
          if ((mFieldSize != -1) && (mCount >= mFieldSize)) {
            throw new IOException();
          }
          paramAnonymousLong = super.skip(paramAnonymousLong);
          if (paramAnonymousLong > 0L)
          {
            VersionedParcelStream localVersionedParcelStream = VersionedParcelStream.this;
            mCount += (int)paramAnonymousLong;
          }
          return paramAnonymousLong;
        }
      });
    } else {
      paramInputStream = null;
    }
    mMasterInput = paramInputStream;
    paramInputStream = paramArrayMap1;
    if (paramOutputStream != null) {
      paramInputStream = new DataOutputStream(paramOutputStream);
    }
    mMasterOutput = paramInputStream;
    mCurrentInput = mMasterInput;
    mCurrentOutput = mMasterOutput;
  }
  
  private void readObject(int paramInt, String paramString, Bundle paramBundle)
  {
    switch (paramInt)
    {
    default: 
      paramString = new StringBuilder();
      paramString.append("Unknown type ");
      paramString.append(paramInt);
      throw new RuntimeException(paramString.toString());
    case 14: 
      paramBundle.putFloatArray(paramString, readFloatArray());
      return;
    case 13: 
      paramBundle.putFloat(paramString, readFloat());
      return;
    case 12: 
      paramBundle.putLongArray(paramString, readLongArray());
      return;
    case 11: 
      paramBundle.putLong(paramString, readLong());
      return;
    case 10: 
      paramBundle.putIntArray(paramString, readIntArray());
      return;
    case 9: 
      paramBundle.putInt(paramString, readInt());
      return;
    case 8: 
      paramBundle.putDoubleArray(paramString, readDoubleArray());
      return;
    case 7: 
      paramBundle.putDouble(paramString, readDouble());
      return;
    case 6: 
      paramBundle.putBooleanArray(paramString, readBooleanArray());
      return;
    case 5: 
      paramBundle.putBoolean(paramString, readBoolean());
      return;
    case 4: 
      paramBundle.putStringArray(paramString, (String[])readArray(new String[0]));
      return;
    case 3: 
      paramBundle.putString(paramString, readString());
      return;
    case 2: 
      paramBundle.putBundle(paramString, readBundle());
      return;
    case 1: 
      paramBundle.putBundle(paramString, readBundle());
      return;
    }
    paramBundle.putParcelable(paramString, null);
  }
  
  private void writeObject(Object paramObject)
  {
    if (paramObject == null)
    {
      writeInt(0);
      return;
    }
    if ((paramObject instanceof Bundle))
    {
      writeInt(1);
      writeBundle((Bundle)paramObject);
      return;
    }
    if ((paramObject instanceof String))
    {
      writeInt(3);
      writeString((String)paramObject);
      return;
    }
    if ((paramObject instanceof String[]))
    {
      writeInt(4);
      writeArray((String[])paramObject);
      return;
    }
    if ((paramObject instanceof Boolean))
    {
      writeInt(5);
      writeBoolean(((Boolean)paramObject).booleanValue());
      return;
    }
    if ((paramObject instanceof boolean[]))
    {
      writeInt(6);
      writeBooleanArray((boolean[])paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      writeInt(7);
      writeDouble(((Double)paramObject).doubleValue());
      return;
    }
    if ((paramObject instanceof double[]))
    {
      writeInt(8);
      writeDoubleArray((double[])paramObject);
      return;
    }
    if ((paramObject instanceof Integer))
    {
      writeInt(9);
      writeInt(((Integer)paramObject).intValue());
      return;
    }
    if ((paramObject instanceof int[]))
    {
      writeInt(10);
      writeIntArray((int[])paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      writeInt(11);
      writeLong(((Long)paramObject).longValue());
      return;
    }
    if ((paramObject instanceof long[]))
    {
      writeInt(12);
      writeLongArray((long[])paramObject);
      return;
    }
    if ((paramObject instanceof Float))
    {
      writeInt(13);
      writeFloat(((Float)paramObject).floatValue());
      return;
    }
    if ((paramObject instanceof float[]))
    {
      writeInt(14);
      writeFloatArray((float[])paramObject);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unsupported type ");
    localStringBuilder.append(paramObject.getClass());
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void closeField()
  {
    if (mFieldBuffer != null)
    {
      Object localObject = mFieldBuffer.mOutput;
      try
      {
        int i = ((ByteArrayOutputStream)localObject).size();
        if (i != 0)
        {
          localObject = mFieldBuffer;
          ((FieldBuffer)localObject).flushField();
        }
        mFieldBuffer = null;
        return;
      }
      catch (IOException localIOException)
      {
        throw new VersionedParcel.ParcelException(localIOException);
      }
    }
  }
  
  protected VersionedParcel createSubParcel()
  {
    return new VersionedParcelStream(mCurrentInput, mCurrentOutput, mReadCache, mWriteCache, mParcelizerCache);
  }
  
  public boolean isStream()
  {
    return true;
  }
  
  public boolean readBoolean()
  {
    DataInputStream localDataInputStream = mCurrentInput;
    try
    {
      boolean bool = localDataInputStream.readBoolean();
      return bool;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public Bundle readBundle()
  {
    int j = readInt();
    if (j < 0) {
      return null;
    }
    Bundle localBundle = new Bundle();
    int i = 0;
    while (i < j)
    {
      String str = readString();
      readObject(readInt(), str, localBundle);
      i += 1;
    }
    return localBundle;
  }
  
  public byte[] readByteArray()
  {
    Object localObject = mCurrentInput;
    try
    {
      int i = ((DataInputStream)localObject).readInt();
      if (i > 0)
      {
        localObject = new byte[i];
        DataInputStream localDataInputStream = mCurrentInput;
        localDataInputStream.readFully((byte[])localObject);
        return localObject;
      }
      return null;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  protected CharSequence readCharSequence()
  {
    return null;
  }
  
  public double readDouble()
  {
    DataInputStream localDataInputStream = mCurrentInput;
    try
    {
      double d = localDataInputStream.readDouble();
      return d;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public boolean readField(int paramInt)
  {
    for (;;)
    {
      if (mFieldId == paramInt) {
        return true;
      }
      int i = mFieldId;
      try
      {
        i = String.valueOf(i).compareTo(String.valueOf(paramInt));
        if (i > 0) {
          return false;
        }
        if (mCount < mFieldSize)
        {
          localDataInputStream = mMasterInput;
          long l = mFieldSize - mCount;
          localDataInputStream.skip(l);
        }
        mFieldSize = -1;
        DataInputStream localDataInputStream = mMasterInput;
        int k = localDataInputStream.readInt();
        mCount = 0;
        int j = k & 0xFFFF;
        i = j;
        if (j == 65535)
        {
          localDataInputStream = mMasterInput;
          i = localDataInputStream.readInt();
        }
        mFieldId = (k >> 16 & 0xFFFF);
        mFieldSize = i;
      }
      catch (IOException localIOException) {}
    }
    return false;
  }
  
  public float readFloat()
  {
    DataInputStream localDataInputStream = mCurrentInput;
    try
    {
      float f = localDataInputStream.readFloat();
      return f;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public int readInt()
  {
    DataInputStream localDataInputStream = mCurrentInput;
    try
    {
      int i = localDataInputStream.readInt();
      return i;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public long readLong()
  {
    DataInputStream localDataInputStream = mCurrentInput;
    try
    {
      long l = localDataInputStream.readLong();
      return l;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public Parcelable readParcelable()
  {
    return null;
  }
  
  public String readString()
  {
    Object localObject1 = mCurrentInput;
    try
    {
      int i = ((DataInputStream)localObject1).readInt();
      if (i > 0)
      {
        localObject1 = new byte[i];
        Object localObject2 = mCurrentInput;
        ((DataInputStream)localObject2).readFully((byte[])localObject1);
        localObject2 = UTF_16;
        localObject1 = new String((byte[])localObject1, (Charset)localObject2);
        return localObject1;
      }
      return null;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public IBinder readStrongBinder()
  {
    return null;
  }
  
  public void setOutputField(int paramInt)
  {
    closeField();
    mFieldBuffer = new FieldBuffer(paramInt, mMasterOutput);
    mCurrentOutput = mFieldBuffer.mDataStream;
  }
  
  public void setSerializationFlags(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      mIgnoreParcelables = paramBoolean2;
      return;
    }
    throw new RuntimeException("Serialization of this object is not allowed");
  }
  
  public void writeBoolean(boolean paramBoolean)
  {
    DataOutputStream localDataOutputStream = mCurrentOutput;
    try
    {
      localDataOutputStream.writeBoolean(paramBoolean);
      return;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public void writeBundle(Bundle paramBundle)
  {
    if (paramBundle != null) {
      try
      {
        Object localObject1 = paramBundle.keySet();
        Object localObject2 = mCurrentOutput;
        ((DataOutputStream)localObject2).writeInt(((Set)localObject1).size());
        localObject1 = ((Set)localObject1).iterator();
        for (;;)
        {
          boolean bool = ((Iterator)localObject1).hasNext();
          if (!bool) {
            break;
          }
          localObject2 = ((Iterator)localObject1).next();
          localObject2 = (String)localObject2;
          writeString((String)localObject2);
          writeObject(paramBundle.get((String)localObject2));
        }
        paramBundle = mCurrentOutput;
      }
      catch (IOException paramBundle) {}
    }
    paramBundle.writeInt(-1);
    return;
    throw new VersionedParcel.ParcelException(paramBundle);
  }
  
  public void writeByteArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      DataOutputStream localDataOutputStream = mCurrentOutput;
      int i = paramArrayOfByte.length;
      try
      {
        localDataOutputStream.writeInt(i);
        localDataOutputStream = mCurrentOutput;
        localDataOutputStream.write(paramArrayOfByte);
        return;
      }
      catch (IOException paramArrayOfByte) {}
    }
    else
    {
      paramArrayOfByte = mCurrentOutput;
      paramArrayOfByte.writeInt(-1);
      return;
    }
    throw new VersionedParcel.ParcelException(paramArrayOfByte);
  }
  
  public void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte != null)
    {
      DataOutputStream localDataOutputStream = mCurrentOutput;
      try
      {
        localDataOutputStream.writeInt(paramInt2);
        localDataOutputStream = mCurrentOutput;
        localDataOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      catch (IOException paramArrayOfByte) {}
    }
    else
    {
      paramArrayOfByte = mCurrentOutput;
      paramArrayOfByte.writeInt(-1);
      return;
    }
    throw new VersionedParcel.ParcelException(paramArrayOfByte);
  }
  
  protected void writeCharSequence(CharSequence paramCharSequence)
  {
    if (mIgnoreParcelables) {
      return;
    }
    throw new RuntimeException("CharSequence cannot be written to an OutputStream");
  }
  
  public void writeDouble(double paramDouble)
  {
    DataOutputStream localDataOutputStream = mCurrentOutput;
    try
    {
      localDataOutputStream.writeDouble(paramDouble);
      return;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public void writeFloat(float paramFloat)
  {
    DataOutputStream localDataOutputStream = mCurrentOutput;
    try
    {
      localDataOutputStream.writeFloat(paramFloat);
      return;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public void writeInt(int paramInt)
  {
    DataOutputStream localDataOutputStream = mCurrentOutput;
    try
    {
      localDataOutputStream.writeInt(paramInt);
      return;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public void writeLong(long paramLong)
  {
    DataOutputStream localDataOutputStream = mCurrentOutput;
    try
    {
      localDataOutputStream.writeLong(paramLong);
      return;
    }
    catch (IOException localIOException)
    {
      throw new VersionedParcel.ParcelException(localIOException);
    }
  }
  
  public void writeParcelable(Parcelable paramParcelable)
  {
    if (mIgnoreParcelables) {
      return;
    }
    throw new RuntimeException("Parcelables cannot be written to an OutputStream");
  }
  
  public void writeString(String paramString)
  {
    if (paramString != null)
    {
      Object localObject = UTF_16;
      try
      {
        paramString = paramString.getBytes((Charset)localObject);
        localObject = mCurrentOutput;
        int i = paramString.length;
        ((DataOutputStream)localObject).writeInt(i);
        localObject = mCurrentOutput;
        ((DataOutputStream)localObject).write(paramString);
        return;
      }
      catch (IOException paramString) {}
    }
    else
    {
      paramString = mCurrentOutput;
      paramString.writeInt(-1);
      return;
    }
    throw new VersionedParcel.ParcelException(paramString);
  }
  
  public void writeStrongBinder(IBinder paramIBinder)
  {
    if (mIgnoreParcelables) {
      return;
    }
    throw new RuntimeException("Binders cannot be written to an OutputStream");
  }
  
  public void writeStrongInterface(IInterface paramIInterface)
  {
    if (mIgnoreParcelables) {
      return;
    }
    throw new RuntimeException("Binders cannot be written to an OutputStream");
  }
  
  private static class FieldBuffer
  {
    final DataOutputStream mDataStream = new DataOutputStream(mOutput);
    private final int mFieldId;
    final ByteArrayOutputStream mOutput = new ByteArrayOutputStream();
    private final DataOutputStream mTarget;
    
    FieldBuffer(int paramInt, DataOutputStream paramDataOutputStream)
    {
      mFieldId = paramInt;
      mTarget = paramDataOutputStream;
    }
    
    void flushField()
      throws IOException
    {
      mDataStream.flush();
      int j = mOutput.size();
      int k = mFieldId;
      int i;
      if (j >= 65535) {
        i = 65535;
      } else {
        i = j;
      }
      mTarget.writeInt(k << 16 | i);
      if (j >= 65535) {
        mTarget.writeInt(j);
      }
      mOutput.writeTo(mTarget);
    }
  }
}
