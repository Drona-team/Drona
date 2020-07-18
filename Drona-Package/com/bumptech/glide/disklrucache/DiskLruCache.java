package com.bumptech.glide.disklrucache;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DiskLruCache
  implements Closeable
{
  static final long ANY_SEQUENCE_NUMBER = -1L;
  private static final String CLEAN = "CLEAN";
  private static final String DIRTY = "DIRTY";
  static final String JOURNAL_FILE = "journal";
  static final String JOURNAL_FILE_BACKUP = "journal.bkp";
  static final String JOURNAL_FILE_TEMP = "journal.tmp";
  static final String MAGIC = "libcore.io.DiskLruCache";
  private static final String READ = "READ";
  private static final String REMOVE = "REMOVE";
  static final String VERSION_1 = "1";
  private final int appVersion;
  private final Callable<Void> cleanupCallable = new Callable()
  {
    public Void call()
      throws Exception
    {
      DiskLruCache localDiskLruCache = DiskLruCache.this;
      try
      {
        if (journalWriter == null) {
          return null;
        }
        DiskLruCache.this.trimToSize();
        if (DiskLruCache.this.journalRebuildRequired())
        {
          DiskLruCache.this.rebuildJournal();
          DiskLruCache.access$502(DiskLruCache.this, 0);
        }
        return null;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  };
  private final File directory;
  final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new DiskLruCacheThreadFactory(null));
  private final File journalFile;
  private final File journalFileBackup;
  private final File journalFileTmp;
  private Writer journalWriter;
  private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
  private long maxSize;
  private long nextSequenceNumber = 0L;
  private int redundantOpCount;
  private long size = 0L;
  private final int valueCount;
  
  private DiskLruCache(File paramFile, int paramInt1, int paramInt2, long paramLong)
  {
    directory = paramFile;
    appVersion = paramInt1;
    journalFile = new File(paramFile, "journal");
    journalFileTmp = new File(paramFile, "journal.tmp");
    journalFileBackup = new File(paramFile, "journal.bkp");
    valueCount = paramInt2;
    maxSize = paramLong;
  }
  
  private void checkNotClosed()
  {
    if (journalWriter != null) {
      return;
    }
    throw new IllegalStateException("cache is closed");
  }
  
  private void completeEdit(Editor paramEditor, boolean paramBoolean)
    throws IOException
  {
    for (;;)
    {
      int j;
      try
      {
        Entry localEntry = entry;
        if (currentEditor == paramEditor)
        {
          int k = 0;
          j = k;
          if (paramBoolean)
          {
            j = k;
            if (!readable)
            {
              int i = 0;
              j = k;
              if (i < valueCount)
              {
                if (written[i] != 0)
                {
                  if (!localEntry.getDirtyFile(i).exists())
                  {
                    paramEditor.abort();
                    return;
                  }
                  i += 1;
                  continue;
                }
                paramEditor.abort();
                paramEditor = new StringBuilder();
                paramEditor.append("Newly created entry didn't create value for index ");
                paramEditor.append(i);
                throw new IllegalStateException(paramEditor.toString());
              }
            }
          }
          long l1;
          if (j < valueCount)
          {
            paramEditor = localEntry.getDirtyFile(j);
            if (paramBoolean)
            {
              if (paramEditor.exists())
              {
                File localFile = localEntry.getCleanFile(j);
                paramEditor.renameTo(localFile);
                l1 = lengths[j];
                long l2 = localFile.length();
                lengths[j] = l2;
                size = (size - l1 + l2);
              }
            }
            else {
              deleteIfExists(paramEditor);
            }
          }
          else
          {
            redundantOpCount += 1;
            Entry.access$802(localEntry, null);
            if ((readable | paramBoolean))
            {
              Entry.access$702(localEntry, true);
              journalWriter.append("CLEAN");
              journalWriter.append(' ');
              journalWriter.append(key);
              journalWriter.append(localEntry.getLengths());
              journalWriter.append('\n');
              if (paramBoolean)
              {
                l1 = nextSequenceNumber;
                nextSequenceNumber = (1L + l1);
                Entry.access$1302(localEntry, l1);
              }
            }
            else
            {
              lruEntries.remove(key);
              journalWriter.append("REMOVE");
              journalWriter.append(' ');
              journalWriter.append(key);
              journalWriter.append('\n');
            }
            journalWriter.flush();
            if ((size > maxSize) || (journalRebuildRequired())) {
              executorService.submit(cleanupCallable);
            }
          }
        }
        else
        {
          throw new IllegalStateException();
        }
      }
      catch (Throwable paramEditor)
      {
        throw paramEditor;
      }
      j += 1;
    }
  }
  
  private static void deleteIfExists(File paramFile)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (paramFile.delete()) {
        return;
      }
      throw new IOException();
    }
  }
  
  private Editor edit(String paramString, long paramLong)
    throws IOException
  {
    try
    {
      checkNotClosed();
      Entry localEntry = (Entry)lruEntries.get(paramString);
      if (paramLong != -1L) {
        if (localEntry != null)
        {
          long l = sequenceNumber;
          if (l == paramLong) {}
        }
        else
        {
          return null;
        }
      }
      if (localEntry == null)
      {
        localEntry = new Entry(paramString, null);
        lruEntries.put(paramString, localEntry);
      }
      else
      {
        localEditor = currentEditor;
        if (localEditor != null) {
          return null;
        }
      }
      Editor localEditor = new Editor(localEntry, null);
      Entry.access$802(localEntry, localEditor);
      journalWriter.append("DIRTY");
      journalWriter.append(' ');
      journalWriter.append(paramString);
      journalWriter.append('\n');
      journalWriter.flush();
      return localEditor;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  private static String inputStreamToString(InputStream paramInputStream)
    throws IOException
  {
    return Util.readFully(new InputStreamReader(paramInputStream, Util.UTF_8));
  }
  
  private boolean journalRebuildRequired()
  {
    return (redundantOpCount >= 2000) && (redundantOpCount >= lruEntries.size());
  }
  
  public static DiskLruCache open(File paramFile, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if (paramLong > 0L)
    {
      if (paramInt2 > 0)
      {
        Object localObject = new File(paramFile, "journal.bkp");
        if (((File)localObject).exists())
        {
          File localFile = new File(paramFile, "journal");
          if (localFile.exists()) {
            ((File)localObject).delete();
          } else {
            renameTo((File)localObject, localFile, false);
          }
        }
        localObject = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
        if (journalFile.exists()) {
          try
          {
            ((DiskLruCache)localObject).readJournal();
            ((DiskLruCache)localObject).processJournal();
            return localObject;
          }
          catch (IOException localIOException)
          {
            PrintStream localPrintStream = System.out;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("DiskLruCache ");
            localStringBuilder.append(paramFile);
            localStringBuilder.append(" is corrupt: ");
            localStringBuilder.append(localIOException.getMessage());
            localStringBuilder.append(", removing");
            localPrintStream.println(localStringBuilder.toString());
            ((DiskLruCache)localObject).delete();
          }
        }
        paramFile.mkdirs();
        paramFile = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
        paramFile.rebuildJournal();
        return paramFile;
      }
      throw new IllegalArgumentException("valueCount <= 0");
    }
    throw new IllegalArgumentException("maxSize <= 0");
  }
  
  private void processJournal()
    throws IOException
  {
    deleteIfExists(journalFileTmp);
    Iterator localIterator = lruEntries.values().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      Editor localEditor = currentEditor;
      int j = 0;
      int i = 0;
      if (localEditor == null)
      {
        while (i < valueCount)
        {
          size += lengths[i];
          i += 1;
        }
      }
      else
      {
        Entry.access$802(localEntry, null);
        i = j;
        while (i < valueCount)
        {
          deleteIfExists(localEntry.getCleanFile(i));
          deleteIfExists(localEntry.getDirtyFile(i));
          i += 1;
        }
        localIterator.remove();
      }
    }
  }
  
  private void readJournal()
    throws IOException
  {
    localStrictLineReader = new StrictLineReader(new FileInputStream(journalFile), Util.US_ASCII);
    for (;;)
    {
      try
      {
        str1 = localStrictLineReader.readLine();
        str2 = localStrictLineReader.readLine();
        localObject = localStrictLineReader.readLine();
        str3 = localStrictLineReader.readLine();
        str4 = localStrictLineReader.readLine();
        bool = "libcore.io.DiskLruCache".equals(str1);
        if (bool)
        {
          bool = "1".equals(str2);
          if (bool)
          {
            bool = Integer.toString(appVersion).equals(localObject);
            if (bool)
            {
              bool = Integer.toString(valueCount).equals(str3);
              if (bool)
              {
                bool = "".equals(str4);
                if (bool) {
                  i = 0;
                }
              }
            }
          }
        }
      }
      catch (Throwable localThrowable)
      {
        String str1;
        String str2;
        Object localObject;
        String str3;
        String str4;
        boolean bool;
        int i;
        int j;
        Util.closeQuietly(localStrictLineReader);
        throw localThrowable;
      }
      try
      {
        readJournalLine(localStrictLineReader.readLine());
        i += 1;
      }
      catch (EOFException localEOFException) {}
    }
    j = lruEntries.size();
    redundantOpCount = (i - j);
    bool = localStrictLineReader.hasUnterminatedLine();
    if (bool) {
      rebuildJournal();
    } else {
      journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFile, true), Util.US_ASCII));
    }
    Util.closeQuietly(localStrictLineReader);
    return;
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("unexpected journal header: [");
    ((StringBuilder)localObject).append(str1);
    ((StringBuilder)localObject).append(", ");
    ((StringBuilder)localObject).append(str2);
    ((StringBuilder)localObject).append(", ");
    ((StringBuilder)localObject).append(str3);
    ((StringBuilder)localObject).append(", ");
    ((StringBuilder)localObject).append(str4);
    ((StringBuilder)localObject).append("]");
    throw new IOException(((StringBuilder)localObject).toString());
  }
  
  private void readJournalLine(String paramString)
    throws IOException
  {
    int i = paramString.indexOf(' ');
    if (i != -1)
    {
      int j = i + 1;
      int k = paramString.indexOf(' ', j);
      if (k == -1)
      {
        localObject3 = paramString.substring(j);
        localObject2 = localObject3;
        localObject1 = localObject2;
        if (i == "REMOVE".length())
        {
          localObject1 = localObject2;
          if (paramString.startsWith("REMOVE")) {
            lruEntries.remove(localObject3);
          }
        }
      }
      else
      {
        localObject1 = paramString.substring(j, k);
      }
      Object localObject3 = (Entry)lruEntries.get(localObject1);
      Object localObject2 = localObject3;
      if (localObject3 == null)
      {
        localObject2 = new Entry((String)localObject1, null);
        lruEntries.put(localObject1, localObject2);
      }
      if ((k != -1) && (i == "CLEAN".length()) && (paramString.startsWith("CLEAN")))
      {
        paramString = paramString.substring(k + 1).split(" ");
        Entry.access$702((Entry)localObject2, true);
        Entry.access$802((Entry)localObject2, null);
        ((Entry)localObject2).setLengths(paramString);
        return;
      }
      if ((k == -1) && (i == "DIRTY".length()) && (paramString.startsWith("DIRTY")))
      {
        Entry.access$802((Entry)localObject2, new Editor((Entry)localObject2, null));
        return;
      }
      if ((k == -1) && (i == "READ".length()) && (paramString.startsWith("READ"))) {
        return;
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("unexpected journal line: ");
      ((StringBuilder)localObject1).append(paramString);
      throw new IOException(((StringBuilder)localObject1).toString());
    }
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("unexpected journal line: ");
    ((StringBuilder)localObject1).append(paramString);
    throw new IOException(((StringBuilder)localObject1).toString());
  }
  
  /* Error */
  private void rebuildJournal()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   6: ifnull +10 -> 16
    //   9: aload_0
    //   10: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   13: invokevirtual 493	java/io/Writer:close	()V
    //   16: new 435	java/io/BufferedWriter
    //   19: dup
    //   20: new 437	java/io/OutputStreamWriter
    //   23: dup
    //   24: new 439	java/io/FileOutputStream
    //   27: dup
    //   28: aload_0
    //   29: getfield 126	com/bumptech/glide/disklrucache/DiskLruCache:journalFileTmp	Ljava/io/File;
    //   32: invokespecial 494	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   35: getstatic 410	com/bumptech/glide/disklrucache/Util:US_ASCII	Ljava/nio/charset/Charset;
    //   38: invokespecial 445	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
    //   41: invokespecial 448	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   44: astore_1
    //   45: aload_1
    //   46: ldc 41
    //   48: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   51: aload_1
    //   52: ldc_w 499
    //   55: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   58: aload_1
    //   59: ldc 48
    //   61: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   64: aload_1
    //   65: ldc_w 499
    //   68: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   71: aload_1
    //   72: aload_0
    //   73: getfield 117	com/bumptech/glide/disklrucache/DiskLruCache:appVersion	I
    //   76: invokestatic 425	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   79: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   82: aload_1
    //   83: ldc_w 499
    //   86: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   89: aload_1
    //   90: aload_0
    //   91: getfield 130	com/bumptech/glide/disklrucache/DiskLruCache:valueCount	I
    //   94: invokestatic 425	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   97: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   100: aload_1
    //   101: ldc_w 499
    //   104: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   107: aload_1
    //   108: ldc_w 499
    //   111: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   114: aload_0
    //   115: getfield 85	com/bumptech/glide/disklrucache/DiskLruCache:lruEntries	Ljava/util/LinkedHashMap;
    //   118: invokevirtual 382	java/util/LinkedHashMap:values	()Ljava/util/Collection;
    //   121: invokeinterface 388 1 0
    //   126: astore_2
    //   127: aload_2
    //   128: invokeinterface 393 1 0
    //   133: ifeq +126 -> 259
    //   136: aload_2
    //   137: invokeinterface 397 1 0
    //   142: checkcast 16	com/bumptech/glide/disklrucache/DiskLruCache$Entry
    //   145: astore_3
    //   146: aload_3
    //   147: invokestatic 197	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$800	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)Lcom/bumptech/glide/disklrucache/DiskLruCache$Editor;
    //   150: ifnull +51 -> 201
    //   153: new 217	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   160: astore 4
    //   162: aload 4
    //   164: ldc_w 501
    //   167: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: aload 4
    //   173: aload_3
    //   174: invokestatic 270	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$1200	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)Ljava/lang/String;
    //   177: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: pop
    //   181: aload 4
    //   183: bipush 10
    //   185: invokevirtual 504	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   188: pop
    //   189: aload_1
    //   190: aload 4
    //   192: invokevirtual 231	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   195: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   198: goto -71 -> 127
    //   201: new 217	java/lang/StringBuilder
    //   204: dup
    //   205: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   208: astore 4
    //   210: aload 4
    //   212: ldc_w 506
    //   215: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: pop
    //   219: aload 4
    //   221: aload_3
    //   222: invokestatic 270	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$1200	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)Ljava/lang/String;
    //   225: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: pop
    //   229: aload 4
    //   231: aload_3
    //   232: invokevirtual 273	com/bumptech/glide/disklrucache/DiskLruCache$Entry:getLengths	()Ljava/lang/String;
    //   235: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   238: pop
    //   239: aload 4
    //   241: bipush 10
    //   243: invokevirtual 504	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   246: pop
    //   247: aload_1
    //   248: aload 4
    //   250: invokevirtual 231	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokevirtual 497	java/io/Writer:write	(Ljava/lang/String;)V
    //   256: goto -129 -> 127
    //   259: aload_1
    //   260: invokevirtual 493	java/io/Writer:close	()V
    //   263: aload_0
    //   264: getfield 124	com/bumptech/glide/disklrucache/DiskLruCache:journalFile	Ljava/io/File;
    //   267: invokevirtual 212	java/io/File:exists	()Z
    //   270: ifeq +15 -> 285
    //   273: aload_0
    //   274: getfield 124	com/bumptech/glide/disklrucache/DiskLruCache:journalFile	Ljava/io/File;
    //   277: aload_0
    //   278: getfield 128	com/bumptech/glide/disklrucache/DiskLruCache:journalFileBackup	Ljava/io/File;
    //   281: iconst_1
    //   282: invokestatic 335	com/bumptech/glide/disklrucache/DiskLruCache:renameTo	(Ljava/io/File;Ljava/io/File;Z)V
    //   285: aload_0
    //   286: getfield 126	com/bumptech/glide/disklrucache/DiskLruCache:journalFileTmp	Ljava/io/File;
    //   289: aload_0
    //   290: getfield 124	com/bumptech/glide/disklrucache/DiskLruCache:journalFile	Ljava/io/File;
    //   293: iconst_0
    //   294: invokestatic 335	com/bumptech/glide/disklrucache/DiskLruCache:renameTo	(Ljava/io/File;Ljava/io/File;Z)V
    //   297: aload_0
    //   298: getfield 128	com/bumptech/glide/disklrucache/DiskLruCache:journalFileBackup	Ljava/io/File;
    //   301: invokevirtual 292	java/io/File:delete	()Z
    //   304: pop
    //   305: aload_0
    //   306: new 435	java/io/BufferedWriter
    //   309: dup
    //   310: new 437	java/io/OutputStreamWriter
    //   313: dup
    //   314: new 439	java/io/FileOutputStream
    //   317: dup
    //   318: aload_0
    //   319: getfield 124	com/bumptech/glide/disklrucache/DiskLruCache:journalFile	Ljava/io/File;
    //   322: iconst_1
    //   323: invokespecial 442	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   326: getstatic 410	com/bumptech/glide/disklrucache/Util:US_ASCII	Ljava/nio/charset/Charset;
    //   329: invokespecial 445	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
    //   332: invokespecial 448	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   335: putfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   338: aload_0
    //   339: monitorexit
    //   340: return
    //   341: astore_2
    //   342: aload_1
    //   343: invokevirtual 493	java/io/Writer:close	()V
    //   346: aload_2
    //   347: athrow
    //   348: astore_1
    //   349: aload_0
    //   350: monitorexit
    //   351: aload_1
    //   352: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	353	0	this	DiskLruCache
    //   44	299	1	localBufferedWriter	BufferedWriter
    //   348	4	1	localThrowable1	Throwable
    //   126	11	2	localIterator	Iterator
    //   341	6	2	localThrowable2	Throwable
    //   145	87	3	localEntry	Entry
    //   160	89	4	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   45	127	341	java/lang/Throwable
    //   127	198	341	java/lang/Throwable
    //   201	256	341	java/lang/Throwable
    //   2	16	348	java/lang/Throwable
    //   16	45	348	java/lang/Throwable
    //   259	285	348	java/lang/Throwable
    //   285	338	348	java/lang/Throwable
    //   342	348	348	java/lang/Throwable
  }
  
  private static void renameTo(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      deleteIfExists(paramFile2);
    }
    if (paramFile1.renameTo(paramFile2)) {
      return;
    }
    throw new IOException();
  }
  
  private void trimToSize()
    throws IOException
  {
    while (size > maxSize) {
      remove((String)((Map.Entry)lruEntries.entrySet().iterator().next()).getKey());
    }
  }
  
  public void close()
    throws IOException
  {
    try
    {
      Object localObject = journalWriter;
      if (localObject == null) {
        return;
      }
      localObject = new ArrayList(lruEntries.values()).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Entry localEntry = (Entry)((Iterator)localObject).next();
        if (currentEditor != null) {
          currentEditor.abort();
        }
      }
      trimToSize();
      journalWriter.close();
      journalWriter = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void delete()
    throws IOException
  {
    close();
    Util.deleteContents(directory);
  }
  
  public Editor edit(String paramString)
    throws IOException
  {
    return edit(paramString, -1L);
  }
  
  public void flush()
    throws IOException
  {
    try
    {
      checkNotClosed();
      trimToSize();
      journalWriter.flush();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Value get(String paramString)
    throws IOException
  {
    try
    {
      checkNotClosed();
      Entry localEntry = (Entry)lruEntries.get(paramString);
      if (localEntry == null) {
        return null;
      }
      boolean bool = readable;
      if (!bool) {
        return null;
      }
      File[] arrayOfFile = cleanFiles;
      int j = arrayOfFile.length;
      int i = 0;
      while (i < j)
      {
        bool = arrayOfFile[i].exists();
        if (!bool) {
          return null;
        }
        i += 1;
      }
      redundantOpCount += 1;
      journalWriter.append("READ");
      journalWriter.append(' ');
      journalWriter.append(paramString);
      journalWriter.append('\n');
      if (journalRebuildRequired()) {
        executorService.submit(cleanupCallable);
      }
      paramString = new Value(paramString, sequenceNumber, cleanFiles, lengths, null);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public File getDirectory()
  {
    return directory;
  }
  
  public long getMaxSize()
  {
    try
    {
      long l = maxSize;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isClosed()
  {
    try
    {
      Writer localWriter = journalWriter;
      boolean bool;
      if (localWriter == null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean remove(String paramString)
    throws IOException
  {
    try
    {
      checkNotClosed();
      Entry localEntry = (Entry)lruEntries.get(paramString);
      int i = 0;
      if ((localEntry != null) && (currentEditor == null))
      {
        while (i < valueCount)
        {
          File localFile = localEntry.getCleanFile(i);
          if ((localFile.exists()) && (!localFile.delete()))
          {
            paramString = new StringBuilder();
            paramString.append("failed to delete ");
            paramString.append(localFile);
            throw new IOException(paramString.toString());
          }
          size -= lengths[i];
          lengths[i] = 0L;
          i += 1;
        }
        redundantOpCount += 1;
        journalWriter.append("REMOVE");
        journalWriter.append(' ');
        journalWriter.append(paramString);
        journalWriter.append('\n');
        lruEntries.remove(paramString);
        if (journalRebuildRequired()) {
          executorService.submit(cleanupCallable);
        }
        return true;
      }
      return false;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void setMaxSize(long paramLong)
  {
    try
    {
      maxSize = paramLong;
      executorService.submit(cleanupCallable);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long size()
  {
    try
    {
      long l = size;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static final class DiskLruCacheThreadFactory
    implements ThreadFactory
  {
    private DiskLruCacheThreadFactory() {}
    
    public Thread newThread(Runnable paramRunnable)
    {
      try
      {
        paramRunnable = new Thread(paramRunnable, "glide-disk-lru-cache-thread");
        paramRunnable.setPriority(1);
        return paramRunnable;
      }
      catch (Throwable paramRunnable)
      {
        throw paramRunnable;
      }
    }
  }
  
  public final class Editor
  {
    private boolean committed;
    private final DiskLruCache.Entry entry;
    private final boolean[] written;
    
    private Editor(DiskLruCache.Entry paramEntry)
    {
      entry = paramEntry;
      if (DiskLruCache.Entry.access$700(paramEntry)) {
        this$1 = null;
      } else {
        this$1 = new boolean[valueCount];
      }
      written = DiskLruCache.this;
    }
    
    private InputStream newInputStream(int paramInt)
      throws IOException
    {
      localDiskLruCache = DiskLruCache.this;
      try
      {
        if (DiskLruCache.Entry.access$800(entry) == this)
        {
          if (!DiskLruCache.Entry.access$700(entry)) {
            return null;
          }
          localObject = entry;
        }
      }
      catch (Throwable localThrowable)
      {
        Object localObject;
        label54:
        throw localThrowable;
      }
      try
      {
        localObject = new FileInputStream(((DiskLruCache.Entry)localObject).getCleanFile(paramInt));
        return localObject;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        break label54;
      }
      return null;
      throw new IllegalStateException();
    }
    
    public void abort()
      throws IOException
    {
      DiskLruCache.this.completeEdit(this, false);
    }
    
    public void abortUnlessCommitted()
    {
      if (!committed) {
        try
        {
          abort();
          return;
        }
        catch (IOException localIOException) {}
      }
    }
    
    public void commit()
      throws IOException
    {
      DiskLruCache.this.completeEdit(this, true);
      committed = true;
    }
    
    public File getFile(int paramInt)
      throws IOException
    {
      DiskLruCache localDiskLruCache = DiskLruCache.this;
      try
      {
        if (DiskLruCache.Entry.access$800(entry) == this)
        {
          if (!DiskLruCache.Entry.access$700(entry)) {
            written[paramInt] = true;
          }
          File localFile = entry.getDirtyFile(paramInt);
          if (!directory.exists()) {
            directory.mkdirs();
          }
          return localFile;
        }
        throw new IllegalStateException();
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      InputStream localInputStream = newInputStream(paramInt);
      if (localInputStream != null) {
        return DiskLruCache.inputStreamToString(localInputStream);
      }
      return null;
    }
    
    public void set(int paramInt, String paramString)
      throws IOException
    {
      Object localObject2 = null;
      try
      {
        Object localObject1 = new OutputStreamWriter(new FileOutputStream(getFile(paramInt)), Util.UTF_8);
        try
        {
          ((Writer)localObject1).write(paramString);
          Util.closeQuietly((Closeable)localObject1);
          return;
        }
        catch (Throwable localThrowable2)
        {
          paramString = (String)localObject1;
          localObject1 = localThrowable2;
        }
        Util.closeQuietly(paramString);
      }
      catch (Throwable localThrowable1)
      {
        paramString = localThrowable2;
      }
      throw localThrowable1;
    }
  }
  
  private final class Entry
  {
    File[] cleanFiles;
    private DiskLruCache.Editor currentEditor;
    File[] dirtyFiles;
    private final String key;
    private final long[] lengths;
    private boolean readable;
    private long sequenceNumber;
    
    private Entry(String paramString)
    {
      key = paramString;
      lengths = new long[valueCount];
      cleanFiles = new File[valueCount];
      dirtyFiles = new File[valueCount];
      paramString = new StringBuilder(paramString);
      paramString.append('.');
      int j = paramString.length();
      int i = 0;
      while (i < valueCount)
      {
        paramString.append(i);
        cleanFiles[i] = new File(directory, paramString.toString());
        paramString.append(".tmp");
        dirtyFiles[i] = new File(directory, paramString.toString());
        paramString.setLength(j);
        i += 1;
      }
    }
    
    private IOException invalidLengths(String[] paramArrayOfString)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("unexpected journal line: ");
      localStringBuilder.append(Arrays.toString(paramArrayOfString));
      throw new IOException(localStringBuilder.toString());
    }
    
    private void setLengths(String[] paramArrayOfString)
      throws IOException
    {
      int i;
      if (paramArrayOfString.length == valueCount) {
        i = 0;
      }
      for (;;)
      {
        long[] arrayOfLong;
        String str;
        if (i < paramArrayOfString.length)
        {
          arrayOfLong = lengths;
          str = paramArrayOfString[i];
        }
        try
        {
          long l = Long.parseLong(str);
          arrayOfLong[i] = l;
          i += 1;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          for (;;) {}
        }
      }
      return;
      throw invalidLengths(paramArrayOfString);
      throw invalidLengths(paramArrayOfString);
    }
    
    public File getCleanFile(int paramInt)
    {
      return cleanFiles[paramInt];
    }
    
    public File getDirtyFile(int paramInt)
    {
      return dirtyFiles[paramInt];
    }
    
    public String getLengths()
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      long[] arrayOfLong = lengths;
      int j = arrayOfLong.length;
      int i = 0;
      while (i < j)
      {
        long l = arrayOfLong[i];
        localStringBuilder.append(' ');
        localStringBuilder.append(l);
        i += 1;
      }
      return localStringBuilder.toString();
    }
  }
  
  public final class Value
  {
    private final File[] files;
    private final String key;
    private final long[] lengths;
    private final long sequenceNumber;
    
    private Value(String paramString, long paramLong, File[] paramArrayOfFile, long[] paramArrayOfLong)
    {
      key = paramString;
      sequenceNumber = paramLong;
      files = paramArrayOfFile;
      lengths = paramArrayOfLong;
    }
    
    public DiskLruCache.Editor edit()
      throws IOException
    {
      return DiskLruCache.this.edit(key, sequenceNumber);
    }
    
    public File getFile(int paramInt)
    {
      return files[paramInt];
    }
    
    public long getLength(int paramInt)
    {
      return lengths[paramInt];
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      return DiskLruCache.inputStreamToString(new FileInputStream(files[paramInt]));
    }
  }
}
