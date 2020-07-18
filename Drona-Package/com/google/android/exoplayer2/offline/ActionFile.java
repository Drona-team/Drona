package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ActionFile
{
  static final int VERSION = 0;
  private final File actionFile;
  private final AtomicFile atomicFile;
  
  public ActionFile(File paramFile)
  {
    actionFile = paramFile;
    atomicFile = new AtomicFile(paramFile);
  }
  
  public DownloadAction[] load(DownloadAction.Deserializer... paramVarArgs)
    throws IOException
  {
    boolean bool = actionFile.exists();
    int i = 0;
    if (!bool) {
      return new DownloadAction[0];
    }
    try
    {
      InputStream localInputStream2 = atomicFile.openRead();
      InputStream localInputStream1 = localInputStream2;
      try
      {
        DataInputStream localDataInputStream = new DataInputStream(localInputStream2);
        int j = localDataInputStream.readInt();
        if (j <= 0)
        {
          j = localDataInputStream.readInt();
          DownloadAction[] arrayOfDownloadAction = new DownloadAction[j];
          while (i < j)
          {
            arrayOfDownloadAction[i] = DownloadAction.deserializeFromStream(paramVarArgs, localDataInputStream);
            i += 1;
          }
          Util.closeQuietly(localInputStream2);
          return arrayOfDownloadAction;
        }
        paramVarArgs = new StringBuilder();
        paramVarArgs.append("Unsupported action file version: ");
        paramVarArgs.append(j);
        throw new IOException(paramVarArgs.toString());
      }
      catch (Throwable paramVarArgs) {}
      Util.closeQuietly(localInputStream1);
    }
    catch (Throwable paramVarArgs)
    {
      localInputStream1 = null;
    }
    throw paramVarArgs;
  }
  
  public void store(DownloadAction... paramVarArgs)
    throws IOException
  {
    try
    {
      Object localObject = new DataOutputStream(atomicFile.startWrite());
      int i = 0;
      try
      {
        ((DataOutputStream)localObject).writeInt(0);
        ((DataOutputStream)localObject).writeInt(paramVarArgs.length);
        int j = paramVarArgs.length;
        while (i < j)
        {
          DownloadAction.serializeToStream(paramVarArgs[i], (OutputStream)localObject);
          i += 1;
        }
        atomicFile.endWrite((OutputStream)localObject);
        Util.closeQuietly(null);
        return;
      }
      catch (Throwable localThrowable2)
      {
        paramVarArgs = (DownloadAction[])localObject;
        localObject = localThrowable2;
      }
      Util.closeQuietly(paramVarArgs);
    }
    catch (Throwable localThrowable1)
    {
      paramVarArgs = null;
    }
    throw localThrowable1;
  }
}
