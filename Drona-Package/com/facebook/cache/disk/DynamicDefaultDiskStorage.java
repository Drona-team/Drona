package com.facebook.cache.disk;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheErrorLogger;
import com.facebook.cache.common.CacheErrorLogger.CacheErrorCategory;
import com.facebook.common.file.FileTree;
import com.facebook.common.file.FileUtils;
import com.facebook.common.file.FileUtils.CreateDirectoryException;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nullable;

public class DynamicDefaultDiskStorage
  implements DiskStorage
{
  private static final Class<?> TAG = DynamicDefaultDiskStorage.class;
  private final String mBaseDirectoryName;
  private final Supplier<File> mBaseDirectoryPathSupplier;
  private final CacheErrorLogger mCacheErrorLogger;
  @VisibleForTesting
  volatile State mCurrentState;
  private final int mVersion;
  
  public DynamicDefaultDiskStorage(int paramInt, Supplier paramSupplier, String paramString, CacheErrorLogger paramCacheErrorLogger)
  {
    mVersion = paramInt;
    mCacheErrorLogger = paramCacheErrorLogger;
    mBaseDirectoryPathSupplier = paramSupplier;
    mBaseDirectoryName = paramString;
    mCurrentState = new State(null, null);
  }
  
  private void createStorage()
    throws IOException
  {
    File localFile = new File((File)mBaseDirectoryPathSupplier.getFolder(), mBaseDirectoryName);
    createRootDirectoryIfNecessary(localFile);
    mCurrentState = new State(localFile, new DefaultDiskStorage(localFile, mVersion, mCacheErrorLogger));
  }
  
  private boolean shouldCreateNewStorage()
  {
    State localState = mCurrentState;
    return (delegate == null) || (rootDirectory == null) || (!rootDirectory.exists());
  }
  
  public void clearAll()
    throws IOException
  {
    get().clearAll();
  }
  
  public boolean contains(String paramString, Object paramObject)
    throws IOException
  {
    return get().contains(paramString, paramObject);
  }
  
  void createRootDirectoryIfNecessary(File paramFile)
    throws IOException
  {
    try
    {
      FileUtils.mkdirs(paramFile);
      FLog.d(TAG, "Created cache directory %s", paramFile.getAbsolutePath());
      return;
    }
    catch (FileUtils.CreateDirectoryException paramFile)
    {
      mCacheErrorLogger.logError(CacheErrorLogger.CacheErrorCategory.WRITE_CREATE_DIR, TAG, "createRootDirectoryIfNecessary", paramFile);
      throw paramFile;
    }
  }
  
  void deleteOldStorageIfNecessary()
  {
    if ((mCurrentState.delegate != null) && (mCurrentState.rootDirectory != null)) {
      FileTree.deleteRecursively(mCurrentState.rootDirectory);
    }
  }
  
  DiskStorage get()
    throws IOException
  {
    try
    {
      if (shouldCreateNewStorage())
      {
        deleteOldStorageIfNecessary();
        createStorage();
      }
      DiskStorage localDiskStorage = (DiskStorage)Preconditions.checkNotNull(mCurrentState.delegate);
      return localDiskStorage;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DiskStorage.DiskDumpInfo getDumpInfo()
    throws IOException
  {
    return get().getDumpInfo();
  }
  
  public Collection getEntries()
    throws IOException
  {
    return get().getEntries();
  }
  
  public BinaryResource getResource(String paramString, Object paramObject)
    throws IOException
  {
    return get().getResource(paramString, paramObject);
  }
  
  public String getStorageName()
  {
    try
    {
      String str = get().getStorageName();
      return str;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return "";
  }
  
  public DiskStorage.Inserter insert(String paramString, Object paramObject)
    throws IOException
  {
    return get().insert(paramString, paramObject);
  }
  
  public boolean isEnabled()
  {
    try
    {
      boolean bool = get().isEnabled();
      return bool;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return false;
  }
  
  public boolean isExternal()
  {
    try
    {
      boolean bool = get().isExternal();
      return bool;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return false;
  }
  
  public void purgeUnexpectedResources()
  {
    try
    {
      get().purgeUnexpectedResources();
      return;
    }
    catch (IOException localIOException)
    {
      FLog.e(TAG, "purgeUnexpectedResources", localIOException);
    }
  }
  
  public long remove(DiskStorage.Entry paramEntry)
    throws IOException
  {
    return get().remove(paramEntry);
  }
  
  public long remove(String paramString)
    throws IOException
  {
    return get().remove(paramString);
  }
  
  public boolean touch(String paramString, Object paramObject)
    throws IOException
  {
    return get().touch(paramString, paramObject);
  }
  
  @VisibleForTesting
  static class State
  {
    @Nullable
    public final DiskStorage delegate;
    @Nullable
    public final File rootDirectory;
    
    State(File paramFile, DiskStorage paramDiskStorage)
    {
      delegate = paramDiskStorage;
      rootDirectory = paramFile;
    }
  }
}
