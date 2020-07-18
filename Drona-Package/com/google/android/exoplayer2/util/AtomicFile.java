package com.google.android.exoplayer2.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AtomicFile
{
  private static final String TAG = "AtomicFile";
  private final File backupName;
  private final File baseName;
  
  public AtomicFile(File paramFile)
  {
    baseName = paramFile;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramFile.getPath());
    localStringBuilder.append(".bak");
    backupName = new File(localStringBuilder.toString());
  }
  
  private void restoreBackup()
  {
    if (backupName.exists())
    {
      baseName.delete();
      backupName.renameTo(baseName);
    }
  }
  
  public void delete()
  {
    baseName.delete();
    backupName.delete();
  }
  
  public void endWrite(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.close();
    backupName.delete();
  }
  
  public InputStream openRead()
    throws FileNotFoundException
  {
    restoreBackup();
    return new FileInputStream(baseName);
  }
  
  public OutputStream startWrite()
    throws IOException
  {
    if (baseName.exists()) {
      if (!backupName.exists())
      {
        if (!baseName.renameTo(backupName))
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Couldn't rename file ");
          ((StringBuilder)localObject1).append(baseName);
          ((StringBuilder)localObject1).append(" to backup file ");
          ((StringBuilder)localObject1).append(backupName);
          Log.w("AtomicFile", ((StringBuilder)localObject1).toString());
        }
      }
      else {
        baseName.delete();
      }
    }
    Object localObject1 = baseName;
    try
    {
      localObject1 = new AtomicFileOutputStream((File)localObject1);
      return localObject1;
    }
    catch (FileNotFoundException localFileNotFoundException1)
    {
      Object localObject3 = baseName.getParentFile();
      if ((localObject3 != null) && (((File)localObject3).mkdirs()))
      {
        Object localObject2 = baseName;
        try
        {
          localObject2 = new AtomicFileOutputStream((File)localObject2);
          return localObject2;
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append("Couldn't create ");
          ((StringBuilder)localObject3).append(baseName);
          throw new IOException(((StringBuilder)localObject3).toString(), localFileNotFoundException2);
        }
      }
      localObject3 = new StringBuilder();
      ((StringBuilder)localObject3).append("Couldn't create directory ");
      ((StringBuilder)localObject3).append(baseName);
      throw new IOException(((StringBuilder)localObject3).toString(), localFileNotFoundException2);
    }
  }
  
  private static final class AtomicFileOutputStream
    extends OutputStream
  {
    private boolean closed = false;
    private final FileOutputStream fileOutputStream;
    
    public AtomicFileOutputStream(File paramFile)
      throws FileNotFoundException
    {
      fileOutputStream = new FileOutputStream(paramFile);
    }
    
    public void close()
      throws IOException
    {
      if (closed) {
        return;
      }
      closed = true;
      flush();
      FileOutputStream localFileOutputStream = fileOutputStream;
      try
      {
        localFileOutputStream.getFD().sync();
      }
      catch (IOException localIOException)
      {
        Log.w("AtomicFile", "Failed to sync file descriptor:", localIOException);
      }
      fileOutputStream.close();
    }
    
    public void flush()
      throws IOException
    {
      fileOutputStream.flush();
    }
    
    public void write(int paramInt)
      throws IOException
    {
      fileOutputStream.write(paramInt);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      fileOutputStream.write(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      fileOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
}
