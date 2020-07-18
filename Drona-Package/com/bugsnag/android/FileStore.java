package com.bugsnag.android;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class FileStore<T extends JsonStream.Streamable>
{
  private final Comparator<File> comparator;
  @NonNull
  protected final Configuration config;
  protected final Delegate delegate;
  final Lock lock = new ReentrantLock();
  private final int maxStoreCount;
  final Collection<File> queuedFiles = new ConcurrentSkipListSet();
  @Nullable
  final String storeDirectory;
  
  FileStore(Configuration paramConfiguration, Context paramContext, String paramString, int paramInt, Comparator paramComparator, Delegate paramDelegate)
  {
    config = paramConfiguration;
    maxStoreCount = paramInt;
    comparator = paramComparator;
    delegate = paramDelegate;
    paramConfiguration = null;
    try
    {
      paramComparator = new StringBuilder();
      paramComparator.append(paramContext.getCacheDir().getAbsolutePath());
      paramComparator.append(paramString);
      paramContext = paramComparator.toString();
      paramString = new File(paramContext);
      paramString.mkdirs();
      boolean bool = paramString.exists();
      if (!bool) {
        Logger.warn("Could not prepare file storage directory");
      } else {
        paramConfiguration = paramContext;
      }
    }
    catch (Exception paramContext)
    {
      Logger.warn("Could not prepare file storage directory", paramContext);
    }
    storeDirectory = paramConfiguration;
  }
  
  void cancelQueuedFiles(Collection paramCollection)
  {
    lock.lock();
    if (paramCollection != null) {
      try
      {
        queuedFiles.removeAll(paramCollection);
      }
      catch (Throwable paramCollection)
      {
        lock.unlock();
        throw paramCollection;
      }
    }
    lock.unlock();
  }
  
  void deleteStoredFiles(Collection paramCollection)
  {
    lock.lock();
    if (paramCollection != null) {
      try
      {
        queuedFiles.removeAll(paramCollection);
        paramCollection = paramCollection.iterator();
        for (;;)
        {
          boolean bool = paramCollection.hasNext();
          if (!bool) {
            break;
          }
          File localFile = (File)paramCollection.next();
          bool = localFile.delete();
          if (!bool) {
            localFile.deleteOnExit();
          }
        }
        lock.unlock();
      }
      catch (Throwable paramCollection)
      {
        lock.unlock();
        throw paramCollection;
      }
    }
  }
  
  void discardOldestFileIfNeeded()
  {
    Object localObject1 = new File(storeDirectory);
    if (((File)localObject1).isDirectory())
    {
      localObject1 = ((File)localObject1).listFiles();
      if ((localObject1 != null) && (localObject1.length >= maxStoreCount))
      {
        Arrays.sort((Object[])localObject1, comparator);
        int i = 0;
        while ((i < localObject1.length) && (localObject1.length >= maxStoreCount))
        {
          Object localObject2 = localObject1[i];
          if (!queuedFiles.contains(localObject2))
          {
            Logger.warn(String.format("Discarding oldest error as stored error limit reached (%s)", new Object[] { localObject2.getPath() }));
            deleteStoredFiles(Collections.singleton(localObject2));
          }
          i += 1;
        }
      }
    }
  }
  
  /* Error */
  void enqueueContentForDelivery(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 95	com/bugsnag/android/FileStore:storeDirectory	Ljava/lang/String;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: aload_1
    //   10: invokevirtual 175	com/bugsnag/android/FileStore:getFilename	(Ljava/lang/Object;)Ljava/lang/String;
    //   13: astore 5
    //   15: aload_0
    //   16: invokevirtual 177	com/bugsnag/android/FileStore:discardOldestFileIfNeeded	()V
    //   19: aload_0
    //   20: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   23: invokeinterface 104 1 0
    //   28: aconst_null
    //   29: astore 4
    //   31: aconst_null
    //   32: astore_3
    //   33: aload_3
    //   34: astore_2
    //   35: new 179	java/io/FileOutputStream
    //   38: dup
    //   39: aload 5
    //   41: invokespecial 180	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   44: astore 6
    //   46: aload_3
    //   47: astore_2
    //   48: new 182	java/io/BufferedWriter
    //   51: dup
    //   52: new 184	java/io/OutputStreamWriter
    //   55: dup
    //   56: aload 6
    //   58: ldc -70
    //   60: invokespecial 189	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   63: invokespecial 192	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   66: astore_3
    //   67: aload_3
    //   68: aload_1
    //   69: invokevirtual 197	java/io/Writer:write	(Ljava/lang/String;)V
    //   72: aload_3
    //   73: invokevirtual 200	java/io/Writer:close	()V
    //   76: goto +120 -> 196
    //   79: astore_1
    //   80: iconst_1
    //   81: anewarray 5	java/lang/Object
    //   84: astore_2
    //   85: aload_2
    //   86: iconst_0
    //   87: aload 5
    //   89: aastore
    //   90: goto +96 -> 186
    //   93: astore_1
    //   94: aload_3
    //   95: astore_2
    //   96: goto +110 -> 206
    //   99: astore_2
    //   100: aload_3
    //   101: astore_1
    //   102: aload_2
    //   103: astore_3
    //   104: goto +11 -> 115
    //   107: astore_1
    //   108: goto +98 -> 206
    //   111: astore_3
    //   112: aload 4
    //   114: astore_1
    //   115: aload_1
    //   116: astore_2
    //   117: new 62	java/io/File
    //   120: dup
    //   121: aload 5
    //   123: invokespecial 76	java/io/File:<init>	(Ljava/lang/String;)V
    //   126: astore 4
    //   128: aload_1
    //   129: astore_2
    //   130: aload_0
    //   131: getfield 51	com/bugsnag/android/FileStore:delegate	Lcom/bugsnag/android/FileStore$Delegate;
    //   134: astore 6
    //   136: aload 6
    //   138: ifnull +19 -> 157
    //   141: aload_1
    //   142: astore_2
    //   143: aload_0
    //   144: getfield 51	com/bugsnag/android/FileStore:delegate	Lcom/bugsnag/android/FileStore$Delegate;
    //   147: aload_3
    //   148: aload 4
    //   150: ldc -54
    //   152: invokeinterface 206 4 0
    //   157: aload_1
    //   158: astore_2
    //   159: aload 4
    //   161: invokestatic 212	com/bugsnag/android/IOUtils:deleteFile	(Ljava/io/File;)V
    //   164: aload_1
    //   165: ifnull +31 -> 196
    //   168: aload_1
    //   169: invokevirtual 200	java/io/Writer:close	()V
    //   172: goto +24 -> 196
    //   175: astore_1
    //   176: iconst_1
    //   177: anewarray 5	java/lang/Object
    //   180: astore_2
    //   181: aload_2
    //   182: iconst_0
    //   183: aload 5
    //   185: aastore
    //   186: ldc -42
    //   188: aload_2
    //   189: invokestatic 162	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   192: aload_1
    //   193: invokestatic 93	com/bugsnag/android/Logger:warn	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   196: aload_0
    //   197: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   200: invokeinterface 113 1 0
    //   205: return
    //   206: aload_2
    //   207: ifnull +29 -> 236
    //   210: aload_2
    //   211: invokevirtual 200	java/io/Writer:close	()V
    //   214: goto +22 -> 236
    //   217: astore_2
    //   218: ldc -42
    //   220: iconst_1
    //   221: anewarray 5	java/lang/Object
    //   224: dup
    //   225: iconst_0
    //   226: aload 5
    //   228: aastore
    //   229: invokestatic 162	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   232: aload_2
    //   233: invokestatic 93	com/bugsnag/android/Logger:warn	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   236: aload_0
    //   237: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   240: invokeinterface 113 1 0
    //   245: aload_1
    //   246: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	247	0	this	FileStore
    //   0	247	1	paramString	String
    //   34	62	2	localObject1	Object
    //   99	4	2	localException1	Exception
    //   116	95	2	localObject2	Object
    //   217	16	2	localException2	Exception
    //   32	72	3	localObject3	Object
    //   111	37	3	localException3	Exception
    //   29	131	4	localFile	File
    //   13	214	5	str	String
    //   44	93	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   72	76	79	java/lang/Exception
    //   67	72	93	java/lang/Throwable
    //   67	72	99	java/lang/Exception
    //   35	46	107	java/lang/Throwable
    //   48	67	107	java/lang/Throwable
    //   117	128	107	java/lang/Throwable
    //   130	136	107	java/lang/Throwable
    //   143	157	107	java/lang/Throwable
    //   159	164	107	java/lang/Throwable
    //   35	46	111	java/lang/Exception
    //   48	67	111	java/lang/Exception
    //   168	172	175	java/lang/Exception
    //   210	214	217	java/lang/Exception
  }
  
  List findStoredFiles()
  {
    lock.lock();
    try
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = storeDirectory;
      if (localObject1 != null)
      {
        localObject1 = new File(storeDirectory);
        boolean bool = ((File)localObject1).exists();
        if (bool)
        {
          bool = ((File)localObject1).isDirectory();
          if (bool)
          {
            localObject1 = ((File)localObject1).listFiles();
            if (localObject1 != null)
            {
              int j = localObject1.length;
              int i = 0;
              while (i < j)
              {
                Object localObject2 = localObject1[i];
                long l = localObject2.length();
                if (l == 0L)
                {
                  bool = localObject2.delete();
                  if (!bool) {
                    localObject2.deleteOnExit();
                  }
                }
                else
                {
                  bool = localObject2.isFile();
                  if (bool)
                  {
                    bool = queuedFiles.contains(localObject2);
                    if (!bool) {
                      localArrayList.add(localObject2);
                    }
                  }
                }
                i += 1;
              }
            }
          }
        }
      }
      queuedFiles.addAll(localArrayList);
      lock.unlock();
      return localArrayList;
    }
    catch (Throwable localThrowable)
    {
      lock.unlock();
      throw localThrowable;
    }
  }
  
  abstract String getFilename(Object paramObject);
  
  /* Error */
  String write(JsonStream.Streamable paramStreamable)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 95	com/bugsnag/android/FileStore:storeDirectory	Ljava/lang/String;
    //   4: ifnonnull +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aload_0
    //   10: invokevirtual 177	com/bugsnag/android/FileStore:discardOldestFileIfNeeded	()V
    //   13: aload_0
    //   14: aload_1
    //   15: invokevirtual 175	com/bugsnag/android/FileStore:getFilename	(Ljava/lang/Object;)Ljava/lang/String;
    //   18: astore 4
    //   20: aload_0
    //   21: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   24: invokeinterface 104 1 0
    //   29: new 179	java/io/FileOutputStream
    //   32: dup
    //   33: aload 4
    //   35: invokespecial 180	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   38: astore_2
    //   39: new 182	java/io/BufferedWriter
    //   42: dup
    //   43: new 184	java/io/OutputStreamWriter
    //   46: dup
    //   47: aload_2
    //   48: ldc -70
    //   50: invokespecial 189	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   53: invokespecial 192	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   56: astore_2
    //   57: new 239	com/bugsnag/android/JsonStream
    //   60: dup
    //   61: aload_2
    //   62: invokespecial 240	com/bugsnag/android/JsonStream:<init>	(Ljava/io/Writer;)V
    //   65: astore_3
    //   66: aload_3
    //   67: astore_2
    //   68: aload_3
    //   69: aload_1
    //   70: invokevirtual 244	com/bugsnag/android/JsonStream:value	(Lcom/bugsnag/android/JsonStream$Streamable;)V
    //   73: aload_3
    //   74: astore_2
    //   75: ldc -10
    //   77: iconst_1
    //   78: anewarray 5	java/lang/Object
    //   81: dup
    //   82: iconst_0
    //   83: aload 4
    //   85: aastore
    //   86: invokestatic 162	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   89: invokestatic 249	com/bugsnag/android/Logger:info	(Ljava/lang/String;)V
    //   92: aload_3
    //   93: invokestatic 253	com/bugsnag/android/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   96: aload_0
    //   97: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   100: invokeinterface 113 1 0
    //   105: aload 4
    //   107: areturn
    //   108: astore_2
    //   109: aload_3
    //   110: astore_1
    //   111: aload_2
    //   112: astore_3
    //   113: goto +20 -> 133
    //   116: astore_2
    //   117: aload_3
    //   118: astore_1
    //   119: aload_2
    //   120: astore_3
    //   121: goto +67 -> 188
    //   124: astore_1
    //   125: aconst_null
    //   126: astore_2
    //   127: goto +86 -> 213
    //   130: astore_3
    //   131: aconst_null
    //   132: astore_1
    //   133: aload_1
    //   134: astore_2
    //   135: new 62	java/io/File
    //   138: dup
    //   139: aload 4
    //   141: invokespecial 76	java/io/File:<init>	(Ljava/lang/String;)V
    //   144: astore 4
    //   146: aload_1
    //   147: astore_2
    //   148: aload_0
    //   149: getfield 51	com/bugsnag/android/FileStore:delegate	Lcom/bugsnag/android/FileStore$Delegate;
    //   152: astore 5
    //   154: aload 5
    //   156: ifnull +19 -> 175
    //   159: aload_1
    //   160: astore_2
    //   161: aload_0
    //   162: getfield 51	com/bugsnag/android/FileStore:delegate	Lcom/bugsnag/android/FileStore$Delegate;
    //   165: aload_3
    //   166: aload 4
    //   168: ldc -1
    //   170: invokeinterface 206 4 0
    //   175: aload_1
    //   176: astore_2
    //   177: aload 4
    //   179: invokestatic 212	com/bugsnag/android/IOUtils:deleteFile	(Ljava/io/File;)V
    //   182: goto +15 -> 197
    //   185: astore_3
    //   186: aconst_null
    //   187: astore_1
    //   188: aload_1
    //   189: astore_2
    //   190: ldc_w 257
    //   193: aload_3
    //   194: invokestatic 93	com/bugsnag/android/Logger:warn	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   197: aload_1
    //   198: invokestatic 253	com/bugsnag/android/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   201: aload_0
    //   202: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   205: invokeinterface 113 1 0
    //   210: aconst_null
    //   211: areturn
    //   212: astore_1
    //   213: aload_2
    //   214: invokestatic 253	com/bugsnag/android/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   217: aload_0
    //   218: getfield 38	com/bugsnag/android/FileStore:lock	Ljava/util/concurrent/locks/Lock;
    //   221: invokeinterface 113 1 0
    //   226: aload_1
    //   227: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	this	FileStore
    //   0	228	1	paramStreamable	JsonStream.Streamable
    //   38	37	2	localObject1	Object
    //   108	4	2	localException1	Exception
    //   116	4	2	localFileNotFoundException1	java.io.FileNotFoundException
    //   126	88	2	localStreamable	JsonStream.Streamable
    //   65	56	3	localObject2	Object
    //   130	36	3	localException2	Exception
    //   185	9	3	localFileNotFoundException2	java.io.FileNotFoundException
    //   18	160	4	localObject3	Object
    //   152	3	5	localDelegate	Delegate
    // Exception table:
    //   from	to	target	type
    //   68	73	108	java/lang/Exception
    //   75	92	108	java/lang/Exception
    //   68	73	116	java/io/FileNotFoundException
    //   75	92	116	java/io/FileNotFoundException
    //   29	39	124	java/lang/Throwable
    //   39	57	124	java/lang/Throwable
    //   57	66	124	java/lang/Throwable
    //   29	39	130	java/lang/Exception
    //   39	57	130	java/lang/Exception
    //   57	66	130	java/lang/Exception
    //   29	39	185	java/io/FileNotFoundException
    //   39	57	185	java/io/FileNotFoundException
    //   57	66	185	java/io/FileNotFoundException
    //   68	73	212	java/lang/Throwable
    //   75	92	212	java/lang/Throwable
    //   135	146	212	java/lang/Throwable
    //   148	154	212	java/lang/Throwable
    //   161	175	212	java/lang/Throwable
    //   177	182	212	java/lang/Throwable
    //   190	197	212	java/lang/Throwable
  }
  
  static abstract interface Delegate
  {
    public abstract void onErrorIOFailure(Exception paramException, File paramFile, String paramString);
  }
}
