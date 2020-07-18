package com.facebook.soloader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class UnpackingSoSource
  extends DirectorySoSource
{
  private static final String DEPS_FILE_NAME = "dso_deps";
  private static final String LOCK_FILE_NAME = "dso_lock";
  private static final String MANIFEST_FILE_NAME = "dso_manifest";
  private static final byte MANIFEST_VERSION = 1;
  private static final String PAGE_KEY = "fb-UnpackingSoSource";
  private static final byte STATE_CLEAN = 1;
  private static final byte STATE_DIRTY = 0;
  private static final String STATE_FILE_NAME = "dso_state";
  @Nullable
  private String[] mAbis;
  protected final Context mContext;
  @Nullable
  protected String mCorruptedLib;
  private final Map<String, Object> mLibsBeingLoaded = new HashMap();
  
  protected UnpackingSoSource(Context paramContext, File paramFile)
  {
    super(paramFile, 1);
    mContext = paramContext;
  }
  
  protected UnpackingSoSource(Context paramContext, String paramString)
  {
    super(getSoStorePath(paramContext, paramString), 1);
    mContext = paramContext;
  }
  
  private void deleteUnmentionedFiles(Dso[] paramArrayOfDso)
    throws IOException
  {
    String[] arrayOfString = soDirectory.list();
    if (arrayOfString != null)
    {
      int i = 0;
      while (i < arrayOfString.length)
      {
        Object localObject = arrayOfString[i];
        if ((!((String)localObject).equals("dso_state")) && (!((String)localObject).equals("dso_lock")) && (!((String)localObject).equals("dso_deps")) && (!((String)localObject).equals("dso_manifest")))
        {
          int k = 0;
          int j = 0;
          while ((k == 0) && (j < paramArrayOfDso.length))
          {
            if (name.equals(localObject)) {
              k = 1;
            }
            j += 1;
          }
          if (k == 0)
          {
            localObject = new File(soDirectory, (String)localObject);
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("deleting unaccounted-for file ");
            localStringBuilder.append(localObject);
            Log.v("fb-UnpackingSoSource", localStringBuilder.toString());
            SysUtil.dumbDeleteRecursive((File)localObject);
          }
        }
        i += 1;
      }
      return;
    }
    paramArrayOfDso = new StringBuilder();
    paramArrayOfDso.append("unable to list directory ");
    paramArrayOfDso.append(soDirectory);
    throw new IOException(paramArrayOfDso.toString());
  }
  
  private void extractDso(InputDso paramInputDso, byte[] paramArrayOfByte)
    throws IOException
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("extracting DSO ");
    ((StringBuilder)localObject1).append(set.name);
    Log.i("fb-UnpackingSoSource", ((StringBuilder)localObject1).toString());
    if (soDirectory.setWritable(true, true))
    {
      File localFile = new File(soDirectory, set.name);
      RandomAccessFile localRandomAccessFile;
      try
      {
        localObject1 = new RandomAccessFile(localFile, "rw");
      }
      catch (IOException localIOException)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("error overwriting ");
        ((StringBuilder)localObject2).append(localFile);
        ((StringBuilder)localObject2).append(" trying to delete and start over");
        Log.w("fb-UnpackingSoSource", ((StringBuilder)localObject2).toString(), localIOException);
        SysUtil.dumbDeleteRecursive(localFile);
        localRandomAccessFile = new RandomAccessFile(localFile, "rw");
      }
      Object localObject2 = content;
      try
      {
        int i = ((InputStream)localObject2).available();
        if (i > 1)
        {
          localObject2 = localRandomAccessFile.getFD();
          long l = i;
          SysUtil.fallocateIfSupported((FileDescriptor)localObject2, l);
        }
        paramInputDso = content;
        SysUtil.copyBytes(localRandomAccessFile, paramInputDso, Integer.MAX_VALUE, paramArrayOfByte);
        localRandomAccessFile.setLength(localRandomAccessFile.getFilePointer());
        boolean bool = localFile.setExecutable(true, false);
        if (bool)
        {
          localRandomAccessFile.close();
          return;
        }
        paramInputDso = new StringBuilder();
        paramInputDso.append("cannot make file executable: ");
        paramInputDso.append(localFile);
        throw new IOException(paramInputDso.toString());
      }
      catch (Throwable paramInputDso) {}catch (IOException paramInputDso)
      {
        SysUtil.dumbDeleteRecursive(localFile);
        throw paramInputDso;
      }
      localRandomAccessFile.close();
      throw paramInputDso;
    }
    paramInputDso = new StringBuilder();
    paramInputDso.append("cannot make directory writable for us: ");
    paramInputDso.append(soDirectory);
    throw new IOException(paramInputDso.toString());
  }
  
  private Object getLibraryLock(String paramString)
  {
    Map localMap = mLibsBeingLoaded;
    try
    {
      Object localObject2 = mLibsBeingLoaded.get(paramString);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new Object();
        mLibsBeingLoaded.put(paramString, localObject1);
      }
      return localObject1;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public static File getSoStorePath(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getApplicationInfodataDir);
    localStringBuilder.append("/");
    localStringBuilder.append(paramString);
    return new File(localStringBuilder.toString());
  }
  
  /* Error */
  private boolean refreshLocked(final FileLocker paramFileLocker, int paramInt, final byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: new 88	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: getfield 86	com/facebook/soloader/DirectorySoSource:soDirectory	Ljava/io/File;
    //   8: ldc 43
    //   10: invokespecial 104	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   13: astore 12
    //   15: new 156	java/io/RandomAccessFile
    //   18: dup
    //   19: aload 12
    //   21: ldc -98
    //   23: invokespecial 159	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   26: astore 9
    //   28: aload 9
    //   30: invokevirtual 245	java/io/RandomAccessFile:readByte	()B
    //   33: istore 5
    //   35: iload 5
    //   37: istore 4
    //   39: iload 5
    //   41: iconst_1
    //   42: if_icmpeq +96 -> 138
    //   45: new 106	java/lang/StringBuilder
    //   48: dup
    //   49: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   52: astore 10
    //   54: aload 10
    //   56: ldc -9
    //   58: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: pop
    //   62: aload_0
    //   63: getfield 86	com/facebook/soloader/DirectorySoSource:soDirectory	Ljava/io/File;
    //   66: astore 11
    //   68: aload 10
    //   70: aload 11
    //   72: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   75: pop
    //   76: aload 10
    //   78: ldc -7
    //   80: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: pop
    //   84: ldc 37
    //   86: aload 10
    //   88: invokevirtual 120	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   94: pop
    //   95: iconst_0
    //   96: istore 4
    //   98: goto +40 -> 138
    //   101: astore_1
    //   102: goto +6 -> 108
    //   105: astore_3
    //   106: aload_3
    //   107: athrow
    //   108: aload_3
    //   109: ifnull +22 -> 131
    //   112: aload 9
    //   114: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   117: goto +19 -> 136
    //   120: astore 9
    //   122: aload_3
    //   123: aload 9
    //   125: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   128: goto +8 -> 136
    //   131: aload 9
    //   133: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   136: aload_1
    //   137: athrow
    //   138: aload 9
    //   140: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   143: new 88	java/io/File
    //   146: dup
    //   147: aload_0
    //   148: getfield 86	com/facebook/soloader/DirectorySoSource:soDirectory	Ljava/io/File;
    //   151: ldc 25
    //   153: invokespecial 104	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   156: astore 14
    //   158: new 156	java/io/RandomAccessFile
    //   161: dup
    //   162: aload 14
    //   164: ldc -98
    //   166: invokespecial 159	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   169: astore 11
    //   171: aload 11
    //   173: invokevirtual 256	java/io/RandomAccessFile:length	()J
    //   176: l2i
    //   177: newarray byte
    //   179: astore 9
    //   181: aload 11
    //   183: aload 9
    //   185: invokevirtual 260	java/io/RandomAccessFile:read	([B)I
    //   188: istore 6
    //   190: aload 9
    //   192: arraylength
    //   193: istore 7
    //   195: iload 6
    //   197: iload 7
    //   199: if_icmpeq +15 -> 214
    //   202: ldc 37
    //   204: ldc_w 262
    //   207: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   210: pop
    //   211: iconst_0
    //   212: istore 4
    //   214: aload 9
    //   216: aload_3
    //   217: invokestatic 267	java/util/Arrays:equals	([B[B)Z
    //   220: istore 8
    //   222: iload 8
    //   224: ifne +15 -> 239
    //   227: ldc 37
    //   229: ldc_w 269
    //   232: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   235: pop
    //   236: iconst_0
    //   237: istore 4
    //   239: iload 4
    //   241: ifeq +18 -> 259
    //   244: iload_2
    //   245: iconst_2
    //   246: iand
    //   247: ifeq +6 -> 253
    //   250: goto +9 -> 259
    //   253: aconst_null
    //   254: astore 10
    //   256: goto +80 -> 336
    //   259: ldc 37
    //   261: ldc_w 271
    //   264: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   267: pop
    //   268: aload 12
    //   270: iconst_0
    //   271: invokestatic 79	com/facebook/soloader/UnpackingSoSource:writeState	(Ljava/io/File;B)V
    //   274: aload_0
    //   275: invokevirtual 275	com/facebook/soloader/UnpackingSoSource:makeUnpacker	()Lcom/facebook/soloader/UnpackingSoSource$Unpacker;
    //   278: astore 13
    //   280: aload 13
    //   282: invokevirtual 279	com/facebook/soloader/UnpackingSoSource$Unpacker:getDsoManifest	()Lcom/facebook/soloader/UnpackingSoSource$DsoManifest;
    //   285: astore 10
    //   287: aload 10
    //   289: astore 9
    //   291: aload 13
    //   293: invokevirtual 283	com/facebook/soloader/UnpackingSoSource$Unpacker:openDsoIterator	()Lcom/facebook/soloader/UnpackingSoSource$InputDsoIterator;
    //   296: astore 15
    //   298: aload_0
    //   299: iload 4
    //   301: aload 10
    //   303: aload 15
    //   305: invokespecial 287	com/facebook/soloader/UnpackingSoSource:regenerate	(BLcom/facebook/soloader/UnpackingSoSource$DsoManifest;Lcom/facebook/soloader/UnpackingSoSource$InputDsoIterator;)V
    //   308: aload 15
    //   310: ifnull +8 -> 318
    //   313: aload 15
    //   315: invokevirtual 288	com/facebook/soloader/UnpackingSoSource$InputDsoIterator:close	()V
    //   318: aload 9
    //   320: astore 10
    //   322: aload 13
    //   324: ifnull +12 -> 336
    //   327: aload 13
    //   329: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   332: aload 9
    //   334: astore 10
    //   336: aload 11
    //   338: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   341: aload 10
    //   343: ifnonnull +5 -> 348
    //   346: iconst_0
    //   347: ireturn
    //   348: new 6	com/facebook/soloader/UnpackingSoSource$1
    //   351: dup
    //   352: aload_0
    //   353: aload 14
    //   355: aload_3
    //   356: aload 10
    //   358: aload 12
    //   360: aload_1
    //   361: invokespecial 292	com/facebook/soloader/UnpackingSoSource$1:<init>	(Lcom/facebook/soloader/UnpackingSoSource;Ljava/io/File;[BLcom/facebook/soloader/UnpackingSoSource$DsoManifest;Ljava/io/File;Lcom/facebook/soloader/FileLocker;)V
    //   364: astore_1
    //   365: iload_2
    //   366: iconst_1
    //   367: iand
    //   368: ifeq +48 -> 416
    //   371: new 106	java/lang/StringBuilder
    //   374: dup
    //   375: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   378: astore_3
    //   379: aload_3
    //   380: ldc_w 294
    //   383: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: pop
    //   387: aload_3
    //   388: aload_0
    //   389: getfield 86	com/facebook/soloader/DirectorySoSource:soDirectory	Ljava/io/File;
    //   392: invokevirtual 297	java/io/File:getName	()Ljava/lang/String;
    //   395: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   398: pop
    //   399: new 299	java/lang/Thread
    //   402: dup
    //   403: aload_1
    //   404: aload_3
    //   405: invokevirtual 120	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   408: invokespecial 302	java/lang/Thread:<init>	(Ljava/lang/Runnable;Ljava/lang/String;)V
    //   411: invokevirtual 305	java/lang/Thread:start	()V
    //   414: iconst_1
    //   415: ireturn
    //   416: aload_1
    //   417: invokeinterface 310 1 0
    //   422: iconst_1
    //   423: ireturn
    //   424: astore_1
    //   425: aload_1
    //   426: athrow
    //   427: astore_3
    //   428: aload 15
    //   430: ifnull +31 -> 461
    //   433: aload_1
    //   434: ifnull +22 -> 456
    //   437: aload 15
    //   439: invokevirtual 288	com/facebook/soloader/UnpackingSoSource$InputDsoIterator:close	()V
    //   442: goto +19 -> 461
    //   445: astore 9
    //   447: aload_1
    //   448: aload 9
    //   450: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   453: goto +8 -> 461
    //   456: aload 15
    //   458: invokevirtual 288	com/facebook/soloader/UnpackingSoSource$InputDsoIterator:close	()V
    //   461: aload_3
    //   462: athrow
    //   463: astore_1
    //   464: aload_1
    //   465: athrow
    //   466: astore_3
    //   467: aload 13
    //   469: ifnull +31 -> 500
    //   472: aload_1
    //   473: ifnull +22 -> 495
    //   476: aload 13
    //   478: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   481: goto +19 -> 500
    //   484: astore 9
    //   486: aload_1
    //   487: aload 9
    //   489: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   492: goto +8 -> 500
    //   495: aload 13
    //   497: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   500: aload_3
    //   501: athrow
    //   502: astore_1
    //   503: goto +6 -> 509
    //   506: astore_3
    //   507: aload_3
    //   508: athrow
    //   509: aload_3
    //   510: ifnull +22 -> 532
    //   513: aload 11
    //   515: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   518: goto +19 -> 537
    //   521: astore 9
    //   523: aload_3
    //   524: aload 9
    //   526: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   529: goto +8 -> 537
    //   532: aload 11
    //   534: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   537: aload_1
    //   538: athrow
    //   539: astore 10
    //   541: goto -446 -> 95
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	544	0	this	UnpackingSoSource
    //   0	544	1	paramFileLocker	FileLocker
    //   0	544	2	paramInt	int
    //   0	544	3	paramArrayOfByte	byte[]
    //   37	263	4	b1	byte
    //   33	10	5	b2	byte
    //   188	12	6	i	int
    //   193	7	7	j	int
    //   220	3	8	bool	boolean
    //   26	87	9	localRandomAccessFile	RandomAccessFile
    //   120	19	9	localThrowable1	Throwable
    //   179	154	9	localObject1	Object
    //   445	4	9	localThrowable2	Throwable
    //   484	4	9	localThrowable3	Throwable
    //   521	4	9	localThrowable4	Throwable
    //   52	305	10	localObject2	Object
    //   539	1	10	localEOFException	java.io.EOFException
    //   66	467	11	localObject3	Object
    //   13	346	12	localFile1	File
    //   278	218	13	localUnpacker	Unpacker
    //   156	198	14	localFile2	File
    //   296	161	15	localInputDsoIterator	InputDsoIterator
    // Exception table:
    //   from	to	target	type
    //   106	108	101	java/lang/Throwable
    //   28	35	105	java/lang/Throwable
    //   45	62	105	java/lang/Throwable
    //   68	95	105	java/lang/Throwable
    //   112	117	120	java/lang/Throwable
    //   298	308	424	java/lang/Throwable
    //   425	427	427	java/lang/Throwable
    //   437	442	445	java/lang/Throwable
    //   280	287	463	java/lang/Throwable
    //   291	298	463	java/lang/Throwable
    //   313	318	463	java/lang/Throwable
    //   447	453	463	java/lang/Throwable
    //   456	461	463	java/lang/Throwable
    //   461	463	463	java/lang/Throwable
    //   464	466	466	java/lang/Throwable
    //   476	481	484	java/lang/Throwable
    //   507	509	502	java/lang/Throwable
    //   171	195	506	java/lang/Throwable
    //   202	211	506	java/lang/Throwable
    //   214	222	506	java/lang/Throwable
    //   227	236	506	java/lang/Throwable
    //   259	280	506	java/lang/Throwable
    //   327	332	506	java/lang/Throwable
    //   486	492	506	java/lang/Throwable
    //   495	500	506	java/lang/Throwable
    //   500	502	506	java/lang/Throwable
    //   513	518	521	java/lang/Throwable
    //   28	35	539	java/io/EOFException
    //   45	62	539	java/io/EOFException
    //   68	95	539	java/io/EOFException
  }
  
  /* Error */
  private void regenerate(byte paramByte, DsoManifest paramDsoManifest, InputDsoIterator paramInputDsoIterator)
    throws IOException
  {
    // Byte code:
    //   0: new 106	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   7: astore 7
    //   9: aload 7
    //   11: ldc_w 314
    //   14: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: pop
    //   18: aload 7
    //   20: aload_0
    //   21: invokevirtual 318	java/lang/Object:getClass	()Ljava/lang/Class;
    //   24: invokevirtual 321	java/lang/Class:getName	()Ljava/lang/String;
    //   27: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: pop
    //   31: ldc 37
    //   33: aload 7
    //   35: invokevirtual 120	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   38: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   41: pop
    //   42: new 156	java/io/RandomAccessFile
    //   45: dup
    //   46: new 88	java/io/File
    //   49: dup
    //   50: aload_0
    //   51: getfield 86	com/facebook/soloader/DirectorySoSource:soDirectory	Ljava/io/File;
    //   54: ldc 31
    //   56: invokespecial 104	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   59: ldc -98
    //   61: invokespecial 159	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   64: astore 9
    //   66: iload_1
    //   67: iconst_1
    //   68: if_icmpne +34 -> 102
    //   71: aload 9
    //   73: invokestatic 324	com/facebook/soloader/UnpackingSoSource$DsoManifest:read	(Ljava/io/DataInput;)Lcom/facebook/soloader/UnpackingSoSource$DsoManifest;
    //   76: astore 7
    //   78: goto +27 -> 105
    //   81: astore_2
    //   82: goto +287 -> 369
    //   85: astore_3
    //   86: goto +281 -> 367
    //   89: astore 7
    //   91: ldc 37
    //   93: ldc_w 326
    //   96: aload 7
    //   98: invokestatic 328	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   101: pop
    //   102: aconst_null
    //   103: astore 7
    //   105: aload 7
    //   107: astore 8
    //   109: aload 7
    //   111: ifnonnull +16 -> 127
    //   114: new 11	com/facebook/soloader/UnpackingSoSource$DsoManifest
    //   117: dup
    //   118: iconst_0
    //   119: anewarray 8	com/facebook/soloader/UnpackingSoSource$Dso
    //   122: invokespecial 330	com/facebook/soloader/UnpackingSoSource$DsoManifest:<init>	([Lcom/facebook/soloader/UnpackingSoSource$Dso;)V
    //   125: astore 8
    //   127: aload_0
    //   128: aload_2
    //   129: getfield 334	com/facebook/soloader/UnpackingSoSource$DsoManifest:dsos	[Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   132: invokespecial 336	com/facebook/soloader/UnpackingSoSource:deleteUnmentionedFiles	([Lcom/facebook/soloader/UnpackingSoSource$Dso;)V
    //   135: ldc_w 337
    //   138: newarray byte
    //   140: astore 7
    //   142: aload_3
    //   143: invokevirtual 341	com/facebook/soloader/UnpackingSoSource$InputDsoIterator:hasNext	()Z
    //   146: istore 6
    //   148: iload 6
    //   150: ifeq +173 -> 323
    //   153: aload_3
    //   154: invokevirtual 345	com/facebook/soloader/UnpackingSoSource$InputDsoIterator:next	()Lcom/facebook/soloader/UnpackingSoSource$InputDso;
    //   157: astore_2
    //   158: iconst_1
    //   159: istore 4
    //   161: iconst_0
    //   162: istore_1
    //   163: iload 4
    //   165: ifeq +97 -> 262
    //   168: aload 8
    //   170: getfield 334	com/facebook/soloader/UnpackingSoSource$DsoManifest:dsos	[Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   173: arraylength
    //   174: istore 5
    //   176: iload_1
    //   177: iload 5
    //   179: if_icmpge +83 -> 262
    //   182: aload 8
    //   184: getfield 334	com/facebook/soloader/UnpackingSoSource$DsoManifest:dsos	[Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   187: iload_1
    //   188: aaload
    //   189: getfield 101	com/facebook/soloader/UnpackingSoSource$Dso:name	Ljava/lang/String;
    //   192: aload_2
    //   193: getfield 147	com/facebook/soloader/UnpackingSoSource$InputDso:set	Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   196: getfield 101	com/facebook/soloader/UnpackingSoSource$Dso:name	Ljava/lang/String;
    //   199: invokevirtual 98	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   202: istore 6
    //   204: iload 4
    //   206: istore 5
    //   208: iload 6
    //   210: ifeq +37 -> 247
    //   213: aload 8
    //   215: getfield 334	com/facebook/soloader/UnpackingSoSource$DsoManifest:dsos	[Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   218: iload_1
    //   219: aaload
    //   220: getfield 348	com/facebook/soloader/UnpackingSoSource$Dso:hash	Ljava/lang/String;
    //   223: aload_2
    //   224: getfield 147	com/facebook/soloader/UnpackingSoSource$InputDso:set	Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   227: getfield 348	com/facebook/soloader/UnpackingSoSource$Dso:hash	Ljava/lang/String;
    //   230: invokevirtual 98	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   233: istore 6
    //   235: iload 4
    //   237: istore 5
    //   239: iload 6
    //   241: ifeq +6 -> 247
    //   244: iconst_0
    //   245: istore 5
    //   247: iload_1
    //   248: iconst_1
    //   249: iadd
    //   250: istore_1
    //   251: iload 5
    //   253: istore 4
    //   255: goto -92 -> 163
    //   258: astore_3
    //   259: goto +18 -> 277
    //   262: iload 4
    //   264: ifeq +48 -> 312
    //   267: aload_0
    //   268: aload_2
    //   269: aload 7
    //   271: invokespecial 350	com/facebook/soloader/UnpackingSoSource:extractDso	(Lcom/facebook/soloader/UnpackingSoSource$InputDso;[B)V
    //   274: goto +38 -> 312
    //   277: aload_3
    //   278: athrow
    //   279: astore 7
    //   281: aload_2
    //   282: ifnull +27 -> 309
    //   285: aload_3
    //   286: ifnull +19 -> 305
    //   289: aload_2
    //   290: invokevirtual 351	com/facebook/soloader/UnpackingSoSource$InputDso:close	()V
    //   293: goto +16 -> 309
    //   296: astore_2
    //   297: aload_3
    //   298: aload_2
    //   299: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   302: goto +7 -> 309
    //   305: aload_2
    //   306: invokevirtual 351	com/facebook/soloader/UnpackingSoSource$InputDso:close	()V
    //   309: aload 7
    //   311: athrow
    //   312: aload_2
    //   313: ifnull -171 -> 142
    //   316: aload_2
    //   317: invokevirtual 351	com/facebook/soloader/UnpackingSoSource$InputDso:close	()V
    //   320: goto -178 -> 142
    //   323: aload 9
    //   325: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   328: new 106	java/lang/StringBuilder
    //   331: dup
    //   332: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   335: astore_2
    //   336: aload_2
    //   337: ldc_w 353
    //   340: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: pop
    //   344: aload_2
    //   345: aload_0
    //   346: invokevirtual 318	java/lang/Object:getClass	()Ljava/lang/Class;
    //   349: invokevirtual 321	java/lang/Class:getName	()Ljava/lang/String;
    //   352: invokevirtual 113	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: pop
    //   356: ldc 37
    //   358: aload_2
    //   359: invokevirtual 120	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   362: invokestatic 126	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   365: pop
    //   366: return
    //   367: aload_3
    //   368: athrow
    //   369: aload_3
    //   370: ifnull +22 -> 392
    //   373: aload 9
    //   375: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   378: goto +19 -> 397
    //   381: astore 7
    //   383: aload_3
    //   384: aload 7
    //   386: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   389: goto +8 -> 397
    //   392: aload 9
    //   394: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   397: aload_2
    //   398: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	399	0	this	UnpackingSoSource
    //   0	399	1	paramByte	byte
    //   0	399	2	paramDsoManifest	DsoManifest
    //   0	399	3	paramInputDsoIterator	InputDsoIterator
    //   159	104	4	b1	byte
    //   174	78	5	b2	byte
    //   146	94	6	bool	boolean
    //   7	70	7	localObject1	Object
    //   89	8	7	localException	Exception
    //   103	167	7	arrayOfByte	byte[]
    //   279	31	7	localThrowable1	Throwable
    //   381	4	7	localThrowable2	Throwable
    //   107	107	8	localObject2	Object
    //   64	329	9	localRandomAccessFile	RandomAccessFile
    // Exception table:
    //   from	to	target	type
    //   367	369	81	java/lang/Throwable
    //   71	78	85	java/lang/Throwable
    //   91	102	85	java/lang/Throwable
    //   114	127	85	java/lang/Throwable
    //   127	142	85	java/lang/Throwable
    //   142	148	85	java/lang/Throwable
    //   153	158	85	java/lang/Throwable
    //   297	302	85	java/lang/Throwable
    //   305	309	85	java/lang/Throwable
    //   309	312	85	java/lang/Throwable
    //   316	320	85	java/lang/Throwable
    //   71	78	89	java/lang/Exception
    //   168	176	258	java/lang/Throwable
    //   182	204	258	java/lang/Throwable
    //   213	235	258	java/lang/Throwable
    //   267	274	258	java/lang/Throwable
    //   277	279	279	java/lang/Throwable
    //   289	293	296	java/lang/Throwable
    //   373	378	381	java/lang/Throwable
  }
  
  /* Error */
  private static void writeState(File paramFile, byte paramByte)
    throws IOException
  {
    // Byte code:
    //   0: new 156	java/io/RandomAccessFile
    //   3: dup
    //   4: aload_0
    //   5: ldc -98
    //   7: invokespecial 159	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   10: astore_3
    //   11: aload_3
    //   12: lconst_0
    //   13: invokevirtual 356	java/io/RandomAccessFile:seek	(J)V
    //   16: aload_3
    //   17: iload_1
    //   18: invokevirtual 360	java/io/RandomAccessFile:write	(I)V
    //   21: aload_3
    //   22: aload_3
    //   23: invokevirtual 194	java/io/RandomAccessFile:getFilePointer	()J
    //   26: invokevirtual 198	java/io/RandomAccessFile:setLength	(J)V
    //   29: aload_3
    //   30: invokevirtual 181	java/io/RandomAccessFile:getFD	()Ljava/io/FileDescriptor;
    //   33: invokevirtual 365	java/io/FileDescriptor:sync	()V
    //   36: aload_3
    //   37: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   40: return
    //   41: astore_0
    //   42: goto +6 -> 48
    //   45: astore_2
    //   46: aload_2
    //   47: athrow
    //   48: aload_2
    //   49: ifnull +19 -> 68
    //   52: aload_3
    //   53: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   56: goto +16 -> 72
    //   59: astore_3
    //   60: aload_2
    //   61: aload_3
    //   62: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   65: goto +7 -> 72
    //   68: aload_3
    //   69: invokevirtual 204	java/io/RandomAccessFile:close	()V
    //   72: aload_0
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	paramFile	File
    //   0	74	1	paramByte	byte
    //   45	16	2	localThrowable1	Throwable
    //   10	43	3	localRandomAccessFile	RandomAccessFile
    //   59	10	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   46	48	41	java/lang/Throwable
    //   11	36	45	java/lang/Throwable
    //   52	56	59	java/lang/Throwable
  }
  
  /* Error */
  protected byte[] getDepsBlock()
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 373	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore 4
    //   5: aload_0
    //   6: invokevirtual 275	com/facebook/soloader/UnpackingSoSource:makeUnpacker	()Lcom/facebook/soloader/UnpackingSoSource$Unpacker;
    //   9: astore_3
    //   10: aload_3
    //   11: invokevirtual 279	com/facebook/soloader/UnpackingSoSource$Unpacker:getDsoManifest	()Lcom/facebook/soloader/UnpackingSoSource$DsoManifest;
    //   14: getfield 334	com/facebook/soloader/UnpackingSoSource$DsoManifest:dsos	[Lcom/facebook/soloader/UnpackingSoSource$Dso;
    //   17: astore 5
    //   19: aload 4
    //   21: iconst_1
    //   22: invokevirtual 377	android/os/Parcel:writeByte	(B)V
    //   25: aload 4
    //   27: aload 5
    //   29: arraylength
    //   30: invokevirtual 380	android/os/Parcel:writeInt	(I)V
    //   33: iconst_0
    //   34: istore_1
    //   35: aload 5
    //   37: arraylength
    //   38: istore_2
    //   39: iload_1
    //   40: iload_2
    //   41: if_icmpge +34 -> 75
    //   44: aload 4
    //   46: aload 5
    //   48: iload_1
    //   49: aaload
    //   50: getfield 101	com/facebook/soloader/UnpackingSoSource$Dso:name	Ljava/lang/String;
    //   53: invokevirtual 383	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   56: aload 4
    //   58: aload 5
    //   60: iload_1
    //   61: aaload
    //   62: getfield 348	com/facebook/soloader/UnpackingSoSource$Dso:hash	Ljava/lang/String;
    //   65: invokevirtual 383	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   68: iload_1
    //   69: iconst_1
    //   70: iadd
    //   71: istore_1
    //   72: goto -37 -> 35
    //   75: aload_3
    //   76: ifnull +7 -> 83
    //   79: aload_3
    //   80: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   83: aload 4
    //   85: invokevirtual 386	android/os/Parcel:marshall	()[B
    //   88: astore_3
    //   89: aload 4
    //   91: invokevirtual 389	android/os/Parcel:recycle	()V
    //   94: aload_3
    //   95: areturn
    //   96: astore 4
    //   98: goto +8 -> 106
    //   101: astore 5
    //   103: aload 5
    //   105: athrow
    //   106: aload_3
    //   107: ifnull +29 -> 136
    //   110: aload 5
    //   112: ifnull +20 -> 132
    //   115: aload_3
    //   116: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   119: goto +17 -> 136
    //   122: astore_3
    //   123: aload 5
    //   125: aload_3
    //   126: invokevirtual 253	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   129: goto +7 -> 136
    //   132: aload_3
    //   133: invokevirtual 289	com/facebook/soloader/UnpackingSoSource$Unpacker:close	()V
    //   136: aload 4
    //   138: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	UnpackingSoSource
    //   34	38	1	i	int
    //   38	4	2	j	int
    //   9	107	3	localObject	Object
    //   122	11	3	localThrowable1	Throwable
    //   3	87	4	localParcel	android.os.Parcel
    //   96	41	4	localThrowable2	Throwable
    //   17	42	5	arrayOfDso	Dso[]
    //   101	23	5	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   103	106	96	java/lang/Throwable
    //   10	33	101	java/lang/Throwable
    //   35	39	101	java/lang/Throwable
    //   44	68	101	java/lang/Throwable
    //   115	119	122	java/lang/Throwable
  }
  
  public String[] getSoSourceAbis()
  {
    if (mAbis == null) {
      return super.getSoSourceAbis();
    }
    return mAbis;
  }
  
  public int loadLibrary(String paramString, int paramInt, StrictMode.ThreadPolicy paramThreadPolicy)
    throws IOException
  {
    Object localObject = getLibraryLock(paramString);
    try
    {
      paramInt = loadLibraryFrom(paramString, paramInt, soDirectory, paramThreadPolicy);
      return paramInt;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  protected abstract Unpacker makeUnpacker()
    throws IOException;
  
  protected void prepare(int paramInt)
    throws IOException
  {
    SysUtil.mkdirOrThrow(soDirectory);
    Object localObject2 = FileLocker.lock(new File(soDirectory, "dso_lock"));
    Object localObject1 = localObject2;
    try
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("locked dso store ");
      localStringBuilder.append(soDirectory);
      Log.v("fb-UnpackingSoSource", localStringBuilder.toString());
      boolean bool = refreshLocked((FileLocker)localObject2, paramInt, getDepsBlock());
      if (bool)
      {
        localObject1 = null;
      }
      else
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("dso store is up-to-date: ");
        localStringBuilder.append(soDirectory);
        Log.i("fb-UnpackingSoSource", localStringBuilder.toString());
      }
      if (localObject1 != null)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("releasing dso store lock for ");
        ((StringBuilder)localObject2).append(soDirectory);
        Log.v("fb-UnpackingSoSource", ((StringBuilder)localObject2).toString());
        ((FileLocker)localObject1).close();
        return;
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("not releasing dso store lock for ");
      ((StringBuilder)localObject1).append(soDirectory);
      ((StringBuilder)localObject1).append(" (syncer thread started)");
      Log.v("fb-UnpackingSoSource", ((StringBuilder)localObject1).toString());
      return;
    }
    catch (Throwable localThrowable)
    {
      StringBuilder localStringBuilder;
      if (localObject2 != null)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("releasing dso store lock for ");
        localStringBuilder.append(soDirectory);
        Log.v("fb-UnpackingSoSource", localStringBuilder.toString());
        ((FileLocker)localObject2).close();
      }
      else
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("not releasing dso store lock for ");
        ((StringBuilder)localObject2).append(soDirectory);
        ((StringBuilder)localObject2).append(" (syncer thread started)");
        Log.v("fb-UnpackingSoSource", ((StringBuilder)localObject2).toString());
      }
      throw localThrowable;
    }
  }
  
  /* Error */
  protected void prepare(String paramString)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial 400	com/facebook/soloader/UnpackingSoSource:getLibraryLock	(Ljava/lang/String;)Ljava/lang/Object;
    //   7: astore_2
    //   8: aload_2
    //   9: monitorenter
    //   10: aload_0
    //   11: aload_1
    //   12: putfield 431	com/facebook/soloader/UnpackingSoSource:mCorruptedLib	Ljava/lang/String;
    //   15: aload_0
    //   16: iconst_2
    //   17: invokevirtual 433	com/facebook/soloader/UnpackingSoSource:prepare	(I)V
    //   20: aload_2
    //   21: monitorexit
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    //   25: astore_1
    //   26: aload_2
    //   27: monitorexit
    //   28: aload_1
    //   29: athrow
    //   30: astore_1
    //   31: aload_0
    //   32: monitorexit
    //   33: aload_1
    //   34: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	UnpackingSoSource
    //   0	35	1	paramString	String
    //   7	20	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	22	25	java/lang/Throwable
    //   26	28	25	java/lang/Throwable
    //   2	10	30	java/lang/Throwable
    //   28	30	30	java/lang/Throwable
  }
  
  public void setSoSourceAbis(String[] paramArrayOfString)
  {
    mAbis = paramArrayOfString;
  }
  
  public static class Dso
  {
    public final String hash;
    public final String name;
    
    public Dso(String paramString1, String paramString2)
    {
      name = paramString1;
      hash = paramString2;
    }
  }
  
  public static final class DsoManifest
  {
    public final UnpackingSoSource.Dso[] dsos;
    
    public DsoManifest(UnpackingSoSource.Dso[] paramArrayOfDso)
    {
      dsos = paramArrayOfDso;
    }
    
    static final DsoManifest read(DataInput paramDataInput)
      throws IOException
    {
      if (paramDataInput.readByte() == 1)
      {
        int j = paramDataInput.readInt();
        if (j >= 0)
        {
          UnpackingSoSource.Dso[] arrayOfDso = new UnpackingSoSource.Dso[j];
          int i = 0;
          while (i < j)
          {
            arrayOfDso[i] = new UnpackingSoSource.Dso(paramDataInput.readUTF(), paramDataInput.readUTF());
            i += 1;
          }
          return new DsoManifest(arrayOfDso);
        }
        throw new RuntimeException("illegal number of shared libraries");
      }
      throw new RuntimeException("wrong dso manifest version");
    }
    
    public final void write(DataOutput paramDataOutput)
      throws IOException
    {
      paramDataOutput.writeByte(1);
      paramDataOutput.writeInt(dsos.length);
      int i = 0;
      while (i < dsos.length)
      {
        paramDataOutput.writeUTF(dsos[i].name);
        paramDataOutput.writeUTF(dsos[i].hash);
        i += 1;
      }
    }
  }
  
  protected static final class InputDso
    implements Closeable
  {
    public final InputStream content;
    public final UnpackingSoSource.Dso set;
    
    public InputDso(UnpackingSoSource.Dso paramDso, InputStream paramInputStream)
    {
      set = paramDso;
      content = paramInputStream;
    }
    
    public void close()
      throws IOException
    {
      content.close();
    }
  }
  
  protected static abstract class InputDsoIterator
    implements Closeable
  {
    protected InputDsoIterator() {}
    
    public void close()
      throws IOException
    {}
    
    public abstract boolean hasNext();
    
    public abstract UnpackingSoSource.InputDso next()
      throws IOException;
  }
  
  protected static abstract class Unpacker
    implements Closeable
  {
    protected Unpacker() {}
    
    public void close()
      throws IOException
    {}
    
    protected abstract UnpackingSoSource.DsoManifest getDsoManifest()
      throws IOException;
    
    protected abstract UnpackingSoSource.InputDsoIterator openDsoIterator()
      throws IOException;
  }
}
