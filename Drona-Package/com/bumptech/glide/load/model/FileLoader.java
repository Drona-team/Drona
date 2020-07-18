package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader<Data>
  implements ModelLoader<File, Data>
{
  private static final String data = "FileLoader";
  private final FileOpener<Data> fileOpener;
  
  public FileLoader(FileOpener paramFileOpener)
  {
    fileOpener = paramFileOpener;
  }
  
  public ModelLoader.LoadData buildLoadData(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramFile), new FileFetcher(paramFile, fileOpener));
  }
  
  public boolean handles(File paramFile)
  {
    return true;
  }
  
  public static class Factory<Data>
    implements ModelLoaderFactory<File, Data>
  {
    private final FileLoader.FileOpener<Data> opener;
    
    public Factory(FileLoader.FileOpener paramFileOpener)
    {
      opener = paramFileOpener;
    }
    
    public final ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new FileLoader(opener);
    }
    
    public final void teardown() {}
  }
  
  public static class FileDescriptorFactory
    extends FileLoader.Factory<ParcelFileDescriptor>
  {
    public FileDescriptorFactory()
    {
      super()
      {
        public void close(ParcelFileDescriptor paramAnonymousParcelFileDescriptor)
          throws IOException
        {
          paramAnonymousParcelFileDescriptor.close();
        }
        
        public Class getDataClass()
        {
          return ParcelFileDescriptor.class;
        }
        
        public ParcelFileDescriptor open(File paramAnonymousFile)
          throws FileNotFoundException
        {
          return ParcelFileDescriptor.open(paramAnonymousFile, 268435456);
        }
      };
    }
  }
  
  private static final class FileFetcher<Data>
    implements DataFetcher<Data>
  {
    private Data data;
    private final File file;
    private final FileLoader.FileOpener<Data> opener;
    
    FileFetcher(File paramFile, FileLoader.FileOpener paramFileOpener)
    {
      file = paramFile;
      opener = paramFileOpener;
    }
    
    public void cancel() {}
    
    public void cleanup()
    {
      if (data != null)
      {
        FileLoader.FileOpener localFileOpener = opener;
        Object localObject = data;
        try
        {
          localFileOpener.close(localObject);
          return;
        }
        catch (IOException localIOException) {}
      }
    }
    
    public Class getDataClass()
    {
      return opener.getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      paramPriority = opener;
      File localFile = file;
      try
      {
        paramPriority = paramPriority.open(localFile);
        data = paramPriority;
        paramDataCallback.onDataReady(data);
        return;
      }
      catch (FileNotFoundException paramPriority)
      {
        if (Log.isLoggable("FileLoader", 3)) {
          Log.d("FileLoader", "Failed to open file", paramPriority);
        }
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static abstract interface FileOpener<Data>
  {
    public abstract void close(Object paramObject)
      throws IOException;
    
    public abstract Class getDataClass();
    
    public abstract Object open(File paramFile)
      throws FileNotFoundException;
  }
  
  public static class StreamFactory
    extends FileLoader.Factory<InputStream>
  {
    public StreamFactory()
    {
      super()
      {
        public void close(InputStream paramAnonymousInputStream)
          throws IOException
        {
          paramAnonymousInputStream.close();
        }
        
        public Class getDataClass()
        {
          return InputStream.class;
        }
        
        public InputStream open(File paramAnonymousFile)
          throws FileNotFoundException
        {
          return new FileInputStream(paramAnonymousFile);
        }
      };
    }
  }
}
