package com.facebook.soloader;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ExoSoSource
  extends UnpackingSoSource
{
  public ExoSoSource(Context paramContext, String paramString)
  {
    super(paramContext, paramString);
  }
  
  protected UnpackingSoSource.Unpacker makeUnpacker()
    throws IOException
  {
    return new ExoUnpacker(this);
  }
  
  private final class ExoUnpacker
    extends UnpackingSoSource.Unpacker
  {
    private final ExoSoSource.FileDso[] mDsos;
    
    ExoUnpacker(UnpackingSoSource paramUnpackingSoSource)
      throws IOException
    {
      this$1 = mContext;
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("/data/local/tmp/exopackage/");
      ((StringBuilder)localObject1).append(getPackageName());
      ((StringBuilder)localObject1).append("/native-libs/");
      File localFile1 = new File(((StringBuilder)localObject1).toString());
      ArrayList localArrayList = new ArrayList();
      LinkedHashSet localLinkedHashSet = new LinkedHashSet();
      String[] arrayOfString = SysUtil.getSupportedAbis();
      int k = arrayOfString.length;
      int i = 0;
      while (i < k)
      {
        this$1 = arrayOfString[i];
        File localFile2 = new File(localFile1, ExoSoSource.this);
        if (localFile2.isDirectory())
        {
          localLinkedHashSet.add(ExoSoSource.this);
          this$1 = new File(localFile2, "metadata.txt");
          if (isFile()) {
            this$1 = new FileReader(ExoSoSource.this);
          }
        }
        try
        {
          localObject1 = new BufferedReader(ExoSoSource.this);
          label177:
          int j = 0;
          try
          {
            String str = ((BufferedReader)localObject1).readLine();
            if (str != null)
            {
              int m = str.length();
              if (m == 0) {
                break label177;
              }
              m = str.indexOf(' ');
              if (m != -1)
              {
                Object localObject2 = new StringBuilder();
                ((StringBuilder)localObject2).append(str.substring(0, m));
                ((StringBuilder)localObject2).append(".so");
                localObject2 = ((StringBuilder)localObject2).toString();
                int n = localArrayList.size();
                while (j < n)
                {
                  boolean bool = getname.equals(localObject2);
                  if (bool)
                  {
                    j = 1;
                    break label317;
                  }
                  j += 1;
                }
                j = 0;
                label317:
                if (j != 0) {
                  break label177;
                }
                for (;;)
                {
                  str = str.substring(m + 1);
                  localArrayList.add(new ExoSoSource.FileDso((String)localObject2, str, new File(localFile2, str)));
                }
              }
              paramUnpackingSoSource = new StringBuilder();
              paramUnpackingSoSource.append("illegal line in exopackage metadata: [");
              paramUnpackingSoSource.append(str);
              paramUnpackingSoSource.append("]");
              throw new RuntimeException(paramUnpackingSoSource.toString());
            }
            ((BufferedReader)localObject1).close();
            close();
            i += 1;
          }
          catch (Throwable paramUnpackingSoSource)
          {
            try
            {
              throw paramUnpackingSoSource;
            }
            catch (Throwable localThrowable3)
            {
              if (paramUnpackingSoSource != null) {
                try
                {
                  ((BufferedReader)localObject1).close();
                }
                catch (Throwable localThrowable1)
                {
                  paramUnpackingSoSource.addSuppressed(localThrowable1);
                }
              } else {
                localThrowable1.close();
              }
              throw localThrowable3;
            }
          }
          paramUnpackingSoSource.setSoSourceAbis((String[])localLinkedHashSet.toArray(new String[localLinkedHashSet.size()]));
        }
        catch (Throwable paramUnpackingSoSource)
        {
          try
          {
            throw paramUnpackingSoSource;
          }
          catch (Throwable localThrowable2)
          {
            if (paramUnpackingSoSource != null) {
              try
              {
                close();
              }
              catch (Throwable this$1)
              {
                paramUnpackingSoSource.addSuppressed(ExoSoSource.this);
              }
            } else {
              close();
            }
            throw localThrowable2;
          }
        }
      }
      mDsos = ((ExoSoSource.FileDso[])localArrayList.toArray(new ExoSoSource.FileDso[localArrayList.size()]));
    }
    
    protected UnpackingSoSource.DsoManifest getDsoManifest()
      throws IOException
    {
      return new UnpackingSoSource.DsoManifest(mDsos);
    }
    
    protected UnpackingSoSource.InputDsoIterator openDsoIterator()
      throws IOException
    {
      return new FileBackedInputDsoIterator(null);
    }
    
    private final class FileBackedInputDsoIterator
      extends UnpackingSoSource.InputDsoIterator
    {
      private int mCurrentDso;
      
      private FileBackedInputDsoIterator() {}
      
      public boolean hasNext()
      {
        return mCurrentDso < mDsos.length;
      }
      
      public UnpackingSoSource.InputDso next()
        throws IOException
      {
        Object localObject1 = mDsos;
        int i = mCurrentDso;
        mCurrentDso = (i + 1);
        Object localObject2 = localObject1[i];
        localObject1 = new FileInputStream(backingFile);
        try
        {
          localObject2 = new UnpackingSoSource.InputDso((UnpackingSoSource.Dso)localObject2, (InputStream)localObject1);
          return localObject2;
        }
        catch (Throwable localThrowable)
        {
          ((FileInputStream)localObject1).close();
          throw localThrowable;
        }
      }
    }
  }
  
  private static final class FileDso
    extends UnpackingSoSource.Dso
  {
    final File backingFile;
    
    FileDso(String paramString1, String paramString2, File paramFile)
    {
      super(paramString2);
      backingFile = paramFile;
    }
  }
}