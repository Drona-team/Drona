package com.facebook.cache.disk;

import android.os.Environment;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheErrorLogger;
import com.facebook.cache.common.CacheErrorLogger.CacheErrorCategory;
import com.facebook.common.file.FileTree;
import com.facebook.common.file.FileTreeVisitor;
import com.facebook.common.file.FileUtils;
import com.facebook.common.file.FileUtils.CreateDirectoryException;
import com.facebook.common.file.FileUtils.ParentDirNotFoundException;
import com.facebook.common.file.FileUtils.RenameException;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.time.Clock;
import com.facebook.common.time.SystemClock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DefaultDiskStorage
  implements DiskStorage
{
  private static final String CONTENT_FILE_EXTENSION = ".cnt";
  private static final String DEFAULT_DISK_STORAGE_VERSION_PREFIX = "v2";
  private static final int SHARDING_BUCKET_COUNT = 100;
  private static final Class<?> TAG = DefaultDiskStorage.class;
  private static final String TEMP_FILE_EXTENSION = ".tmp";
  static final long TEMP_FILE_LIFETIME_MS = TimeUnit.MINUTES.toMillis(30L);
  private final CacheErrorLogger mCacheErrorLogger;
  private final Clock mClock;
  private final boolean mIsExternal;
  private final File mRootDirectory;
  private final File mVersionDirectory;
  
  public DefaultDiskStorage(File paramFile, int paramInt, CacheErrorLogger paramCacheErrorLogger)
  {
    Preconditions.checkNotNull(paramFile);
    mRootDirectory = paramFile;
    mIsExternal = isExternal(paramFile, paramCacheErrorLogger);
    mVersionDirectory = new File(mRootDirectory, getVersionSubdirectoryName(paramInt));
    mCacheErrorLogger = paramCacheErrorLogger;
    recreateDirectoryIfVersionChanges();
    mClock = SystemClock.get();
  }
  
  private long doRemove(File paramFile)
  {
    if (!paramFile.exists()) {
      return 0L;
    }
    long l = paramFile.length();
    if (paramFile.delete()) {
      return l;
    }
    return -1L;
  }
  
  private DiskStorage.DiskDumpInfoEntry dumpCacheEntry(DiskStorage.Entry paramEntry)
    throws IOException
  {
    EntryImpl localEntryImpl = (EntryImpl)paramEntry;
    String str1 = "";
    byte[] arrayOfByte = localEntryImpl.getResource().read();
    String str2 = typeOfBytes(arrayOfByte);
    paramEntry = str1;
    if (str2.equals("undefined"))
    {
      paramEntry = str1;
      if (arrayOfByte.length >= 4) {
        paramEntry = String.format(null, "0x%02X 0x%02X 0x%02X 0x%02X", new Object[] { Byte.valueOf(arrayOfByte[0]), Byte.valueOf(arrayOfByte[1]), Byte.valueOf(arrayOfByte[2]), Byte.valueOf(arrayOfByte[3]) });
      }
    }
    return new DiskStorage.DiskDumpInfoEntry(localEntryImpl.getResource().getFile().getPath(), str2, (float)localEntryImpl.getSize(), paramEntry);
  }
  
  private static String getFileTypefromExtension(String paramString)
  {
    if (".cnt".equals(paramString)) {
      return ".cnt";
    }
    if (".tmp".equals(paramString)) {
      return ".tmp";
    }
    return null;
  }
  
  private String getFilename(String paramString)
  {
    paramString = new FileInfo(".cnt", paramString, null);
    return paramString.toPath(getSubdirectoryPath(resourceId));
  }
  
  private FileInfo getShardFileInfo(File paramFile)
  {
    FileInfo localFileInfo = FileInfo.fromFile(paramFile);
    if (localFileInfo == null) {
      return null;
    }
    if (getSubdirectory(resourceId).equals(paramFile.getParentFile())) {
      return localFileInfo;
    }
    return null;
  }
  
  private File getSubdirectory(String paramString)
  {
    return new File(getSubdirectoryPath(paramString));
  }
  
  private String getSubdirectoryPath(String paramString)
  {
    int i = Math.abs(paramString.hashCode() % 100);
    paramString = new StringBuilder();
    paramString.append(mVersionDirectory);
    paramString.append(File.separator);
    paramString.append(String.valueOf(i));
    return paramString.toString();
  }
  
  static String getVersionSubdirectoryName(int paramInt)
  {
    return String.format(null, "%s.ols%d.%d", new Object[] { "v2", Integer.valueOf(100), Integer.valueOf(paramInt) });
  }
  
  private static boolean isExternal(File paramFile, CacheErrorLogger paramCacheErrorLogger)
  {
    try
    {
      Object localObject1 = Environment.getExternalStorageDirectory();
      if (localObject1 != null)
      {
        Object localObject2 = ((File)localObject1).toString();
        try
        {
          localObject1 = paramFile.getCanonicalPath();
          paramFile = (File)localObject1;
          try
          {
            boolean bool = ((String)localObject1).contains((CharSequence)localObject2);
            return bool;
          }
          catch (IOException localIOException1) {}
          localObject2 = CacheErrorLogger.CacheErrorCategory.OTHER;
        }
        catch (IOException localIOException2)
        {
          paramFile = null;
        }
        Class localClass = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("failed to read folder to check if external: ");
        localStringBuilder.append(paramFile);
        paramCacheErrorLogger.logError((CacheErrorLogger.CacheErrorCategory)localObject2, localClass, localStringBuilder.toString(), localIOException2);
        return false;
      }
    }
    catch (Exception paramFile)
    {
      paramCacheErrorLogger.logError(CacheErrorLogger.CacheErrorCategory.OTHER, TAG, "failed to get the external storage directory!", paramFile);
    }
    return false;
  }
  
  private void mkdirs(File paramFile, String paramString)
    throws IOException
  {
    try
    {
      FileUtils.mkdirs(paramFile);
      return;
    }
    catch (FileUtils.CreateDirectoryException paramFile)
    {
      mCacheErrorLogger.logError(CacheErrorLogger.CacheErrorCategory.WRITE_CREATE_DIR, TAG, paramString, paramFile);
      throw paramFile;
    }
  }
  
  private boolean query(String paramString, boolean paramBoolean)
  {
    paramString = getContentFileFor(paramString);
    boolean bool = paramString.exists();
    if ((paramBoolean) && (bool)) {
      paramString.setLastModified(mClock.now());
    }
    return bool;
  }
  
  private void recreateDirectoryIfVersionChanges()
  {
    boolean bool = mRootDirectory.exists();
    int i = 1;
    if (bool) {
      if (!mVersionDirectory.exists()) {
        FileTree.deleteRecursively(mRootDirectory);
      } else {
        i = 0;
      }
    }
    if (i != 0)
    {
      Object localObject = mVersionDirectory;
      try
      {
        FileUtils.mkdirs((File)localObject);
        return;
      }
      catch (FileUtils.CreateDirectoryException localCreateDirectoryException)
      {
        CacheErrorLogger.CacheErrorCategory localCacheErrorCategory;
        Class localClass;
        StringBuilder localStringBuilder;
        for (;;) {}
      }
      localObject = mCacheErrorLogger;
      localCacheErrorCategory = CacheErrorLogger.CacheErrorCategory.WRITE_CREATE_DIR;
      localClass = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("version directory could not be created: ");
      localStringBuilder.append(mVersionDirectory);
      ((CacheErrorLogger)localObject).logError(localCacheErrorCategory, localClass, localStringBuilder.toString(), null);
      return;
    }
  }
  
  private String typeOfBytes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length >= 2)
    {
      if ((paramArrayOfByte[0] == -1) && (paramArrayOfByte[1] == -40)) {
        return "jpg";
      }
      if ((paramArrayOfByte[0] == -119) && (paramArrayOfByte[1] == 80)) {
        return "png";
      }
      if ((paramArrayOfByte[0] == 82) && (paramArrayOfByte[1] == 73)) {
        return "webp";
      }
      if ((paramArrayOfByte[0] == 71) && (paramArrayOfByte[1] == 73)) {
        return "gif";
      }
    }
    return "undefined";
  }
  
  public void clearAll()
  {
    FileTree.deleteContents(mRootDirectory);
  }
  
  public boolean contains(String paramString, Object paramObject)
  {
    return query(paramString, false);
  }
  
  File getContentFileFor(String paramString)
  {
    return new File(getFilename(paramString));
  }
  
  public DiskStorage.DiskDumpInfo getDumpInfo()
    throws IOException
  {
    Object localObject = getEntries();
    DiskStorage.DiskDumpInfo localDiskDumpInfo = new DiskStorage.DiskDumpInfo();
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      DiskStorage.DiskDumpInfoEntry localDiskDumpInfoEntry = dumpCacheEntry((DiskStorage.Entry)((Iterator)localObject).next());
      String str = type;
      if (!typeCounts.containsKey(str)) {
        typeCounts.put(str, Integer.valueOf(0));
      }
      typeCounts.put(str, Integer.valueOf(((Integer)typeCounts.get(str)).intValue() + 1));
      entries.add(localDiskDumpInfoEntry);
    }
    return localDiskDumpInfo;
  }
  
  public List getEntries()
    throws IOException
  {
    EntriesCollector localEntriesCollector = new EntriesCollector(null);
    FileTree.walkFileTree(mVersionDirectory, localEntriesCollector);
    return localEntriesCollector.getEntries();
  }
  
  public BinaryResource getResource(String paramString, Object paramObject)
  {
    paramString = getContentFileFor(paramString);
    if (paramString.exists())
    {
      paramString.setLastModified(mClock.now());
      return FileBinaryResource.createOrNull(paramString);
    }
    return null;
  }
  
  public String getStorageName()
  {
    String str = mRootDirectory.getAbsolutePath();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("_");
    localStringBuilder.append(str.substring(str.lastIndexOf('/') + 1, str.length()));
    localStringBuilder.append("_");
    localStringBuilder.append(str.hashCode());
    return localStringBuilder.toString();
  }
  
  public DiskStorage.Inserter insert(String paramString, Object paramObject)
    throws IOException
  {
    paramObject = new FileInfo(".tmp", paramString, null);
    File localFile = getSubdirectory(resourceId);
    if (!localFile.exists()) {
      mkdirs(localFile, "insert");
    }
    try
    {
      paramObject = paramObject.createTempFile(localFile);
      paramString = new InserterImpl(paramString, paramObject);
      return paramString;
    }
    catch (IOException paramString)
    {
      mCacheErrorLogger.logError(CacheErrorLogger.CacheErrorCategory.WRITE_CREATE_TEMPFILE, TAG, "insert", paramString);
      throw paramString;
    }
  }
  
  public boolean isEnabled()
  {
    return true;
  }
  
  public boolean isExternal()
  {
    return mIsExternal;
  }
  
  public void purgeUnexpectedResources()
  {
    FileTree.walkFileTree(mRootDirectory, new PurgingVisitor(null));
  }
  
  public long remove(DiskStorage.Entry paramEntry)
  {
    return doRemove(((EntryImpl)paramEntry).getResource().getFile());
  }
  
  public long remove(String paramString)
  {
    return doRemove(getContentFileFor(paramString));
  }
  
  public boolean touch(String paramString, Object paramObject)
  {
    return query(paramString, true);
  }
  
  private class EntriesCollector
    implements FileTreeVisitor
  {
    private final List<DiskStorage.Entry> result = new ArrayList();
    
    private EntriesCollector() {}
    
    public List getEntries()
    {
      return Collections.unmodifiableList(result);
    }
    
    public void postVisitDirectory(File paramFile) {}
    
    public void preVisitDirectory(File paramFile) {}
    
    public void visitFile(File paramFile)
    {
      DefaultDiskStorage.FileInfo localFileInfo = DefaultDiskStorage.this.getShardFileInfo(paramFile);
      if ((localFileInfo != null) && (type == ".cnt")) {
        result.add(new DefaultDiskStorage.EntryImpl(resourceId, paramFile, null));
      }
    }
  }
  
  @VisibleForTesting
  static class EntryImpl
    implements DiskStorage.Entry
  {
    private final String id;
    private final FileBinaryResource resource;
    private long size;
    private long timestamp;
    
    private EntryImpl(String paramString, File paramFile)
    {
      Preconditions.checkNotNull(paramFile);
      id = ((String)Preconditions.checkNotNull(paramString));
      resource = FileBinaryResource.createOrNull(paramFile);
      size = -1L;
      timestamp = -1L;
    }
    
    public String getId()
    {
      return id;
    }
    
    public FileBinaryResource getResource()
    {
      return resource;
    }
    
    public long getSize()
    {
      if (size < 0L) {
        size = resource.size();
      }
      return size;
    }
    
    public long getTimestamp()
    {
      if (timestamp < 0L) {
        timestamp = resource.getFile().lastModified();
      }
      return timestamp;
    }
  }
  
  private static class FileInfo
  {
    public final String resourceId;
    @DefaultDiskStorage.FileType
    public final String type;
    
    private FileInfo(String paramString1, String paramString2)
    {
      type = paramString1;
      resourceId = paramString2;
    }
    
    public static FileInfo fromFile(File paramFile)
    {
      paramFile = paramFile.getName();
      int i = paramFile.lastIndexOf('.');
      if (i <= 0) {
        return null;
      }
      String str2 = DefaultDiskStorage.getFileTypefromExtension(paramFile.substring(i));
      if (str2 == null) {
        return null;
      }
      String str1 = paramFile.substring(0, i);
      paramFile = str1;
      if (str2.equals(".tmp"))
      {
        i = str1.lastIndexOf('.');
        if (i <= 0) {
          return null;
        }
        paramFile = str1.substring(0, i);
      }
      return new FileInfo(str2, paramFile);
    }
    
    public File createTempFile(File paramFile)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(resourceId);
      localStringBuilder.append(".");
      return File.createTempFile(localStringBuilder.toString(), ".tmp", paramFile);
    }
    
    public String toPath(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append(File.separator);
      localStringBuilder.append(resourceId);
      localStringBuilder.append(type);
      return localStringBuilder.toString();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(type);
      localStringBuilder.append("(");
      localStringBuilder.append(resourceId);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
  }
  
  public static @interface FileType
  {
    public static final String CONTENT = ".cnt";
    public static final String TEMP = ".tmp";
  }
  
  private static class IncompleteFileException
    extends IOException
  {
    public final long actual;
    public final long expected;
    
    public IncompleteFileException(long paramLong1, long paramLong2)
    {
      super();
      expected = paramLong1;
      actual = paramLong2;
    }
  }
  
  @VisibleForTesting
  class InserterImpl
    implements DiskStorage.Inserter
  {
    private final String mResourceId;
    @VisibleForTesting
    final File mTemporaryFile;
    
    public InserterImpl(String paramString, File paramFile)
    {
      mResourceId = paramString;
      mTemporaryFile = paramFile;
    }
    
    public boolean cleanUp()
    {
      return (!mTemporaryFile.exists()) || (mTemporaryFile.delete());
    }
    
    public BinaryResource commit(Object paramObject)
      throws IOException
    {
      paramObject = getContentFileFor(mResourceId);
      File localFile = mTemporaryFile;
      try
      {
        FileUtils.rename(localFile, paramObject);
        if (paramObject.exists()) {
          paramObject.setLastModified(mClock.now());
        }
        return FileBinaryResource.createOrNull(paramObject);
      }
      catch (FileUtils.RenameException localRenameException)
      {
        paramObject = localRenameException.getCause();
        if (paramObject != null)
        {
          if (!(paramObject instanceof FileUtils.ParentDirNotFoundException))
          {
            if ((paramObject instanceof FileNotFoundException)) {
              paramObject = CacheErrorLogger.CacheErrorCategory.WRITE_RENAME_FILE_TEMPFILE_NOT_FOUND;
            } else {
              paramObject = CacheErrorLogger.CacheErrorCategory.WRITE_RENAME_FILE_OTHER;
            }
          }
          else {
            paramObject = CacheErrorLogger.CacheErrorCategory.WRITE_RENAME_FILE_TEMPFILE_PARENT_NOT_FOUND;
          }
        }
        else {
          paramObject = CacheErrorLogger.CacheErrorCategory.WRITE_RENAME_FILE_OTHER;
        }
        mCacheErrorLogger.logError(paramObject, DefaultDiskStorage.TAG, "commit", localRenameException);
        throw localRenameException;
      }
    }
    
    /* Error */
    public void writeData(com.facebook.cache.common.WriterCallback paramWriterCallback, Object paramObject)
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 27	com/facebook/cache/disk/DefaultDiskStorage$InserterImpl:mTemporaryFile	Ljava/io/File;
      //   4: astore_2
      //   5: new 116	java/io/FileOutputStream
      //   8: dup
      //   9: aload_2
      //   10: invokespecial 119	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   13: astore_2
      //   14: new 121	com/facebook/common/internal/CountingOutputStream
      //   17: dup
      //   18: aload_2
      //   19: invokespecial 124	com/facebook/common/internal/CountingOutputStream:<init>	(Ljava/io/OutputStream;)V
      //   22: astore 5
      //   24: aload_1
      //   25: aload 5
      //   27: invokeinterface 129 2 0
      //   32: aload 5
      //   34: invokevirtual 134	java/io/FilterOutputStream:flush	()V
      //   37: aload 5
      //   39: invokevirtual 137	com/facebook/common/internal/CountingOutputStream:getCount	()J
      //   42: lstore_3
      //   43: aload_2
      //   44: invokevirtual 140	java/io/FileOutputStream:close	()V
      //   47: aload_0
      //   48: getfield 27	com/facebook/cache/disk/DefaultDiskStorage$InserterImpl:mTemporaryFile	Ljava/io/File;
      //   51: invokevirtual 143	java/io/File:length	()J
      //   54: lload_3
      //   55: lcmp
      //   56: ifne +4 -> 60
      //   59: return
      //   60: new 145	com/facebook/cache/disk/DefaultDiskStorage$IncompleteFileException
      //   63: dup
      //   64: lload_3
      //   65: aload_0
      //   66: getfield 27	com/facebook/cache/disk/DefaultDiskStorage$InserterImpl:mTemporaryFile	Ljava/io/File;
      //   69: invokevirtual 143	java/io/File:length	()J
      //   72: invokespecial 148	com/facebook/cache/disk/DefaultDiskStorage$IncompleteFileException:<init>	(JJ)V
      //   75: athrow
      //   76: astore_1
      //   77: aload_2
      //   78: invokevirtual 140	java/io/FileOutputStream:close	()V
      //   81: aload_1
      //   82: athrow
      //   83: astore_1
      //   84: aload_0
      //   85: getfield 20	com/facebook/cache/disk/DefaultDiskStorage$InserterImpl:this$0	Lcom/facebook/cache/disk/DefaultDiskStorage;
      //   88: invokestatic 98	com/facebook/cache/disk/DefaultDiskStorage:access$1000	(Lcom/facebook/cache/disk/DefaultDiskStorage;)Lcom/facebook/cache/common/CacheErrorLogger;
      //   91: getstatic 151	com/facebook/cache/common/CacheErrorLogger$CacheErrorCategory:WRITE_UPDATE_FILE_NOT_FOUND	Lcom/facebook/cache/common/CacheErrorLogger$CacheErrorCategory;
      //   94: invokestatic 102	com/facebook/cache/disk/DefaultDiskStorage:access$900	()Ljava/lang/Class;
      //   97: ldc -103
      //   99: aload_1
      //   100: invokeinterface 109 5 0
      //   105: aload_1
      //   106: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	107	0	this	InserterImpl
      //   0	107	1	paramWriterCallback	com.facebook.cache.common.WriterCallback
      //   0	107	2	paramObject	Object
      //   42	23	3	l	long
      //   22	16	5	localCountingOutputStream	com.facebook.common.internal.CountingOutputStream
      // Exception table:
      //   from	to	target	type
      //   14	43	76	java/lang/Throwable
      //   5	14	83	java/io/FileNotFoundException
    }
  }
  
  private class PurgingVisitor
    implements FileTreeVisitor
  {
    private boolean insideBaseDirectory;
    
    private PurgingVisitor() {}
    
    private boolean isExpectedFile(File paramFile)
    {
      DefaultDiskStorage.FileInfo localFileInfo = DefaultDiskStorage.this.getShardFileInfo(paramFile);
      boolean bool = false;
      if (localFileInfo == null) {
        return false;
      }
      if (type == ".tmp") {
        return isRecentFile(paramFile);
      }
      if (type == ".cnt") {
        bool = true;
      }
      Preconditions.checkState(bool);
      return true;
    }
    
    private boolean isRecentFile(File paramFile)
    {
      return paramFile.lastModified() > mClock.now() - DefaultDiskStorage.TEMP_FILE_LIFETIME_MS;
    }
    
    public void postVisitDirectory(File paramFile)
    {
      if ((!mRootDirectory.equals(paramFile)) && (!insideBaseDirectory)) {
        paramFile.delete();
      }
      if ((insideBaseDirectory) && (paramFile.equals(mVersionDirectory))) {
        insideBaseDirectory = false;
      }
    }
    
    public void preVisitDirectory(File paramFile)
    {
      if ((!insideBaseDirectory) && (paramFile.equals(mVersionDirectory))) {
        insideBaseDirectory = true;
      }
    }
    
    public void visitFile(File paramFile)
    {
      if ((!insideBaseDirectory) || (!isExpectedFile(paramFile))) {
        paramFile.delete();
      }
    }
  }
}
