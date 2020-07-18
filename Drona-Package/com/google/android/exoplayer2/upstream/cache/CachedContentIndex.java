package com.google.android.exoplayer2.upstream.cache;

import android.util.SparseArray;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

class CachedContentIndex
{
  public static final String FILE_NAME = "cached_content_index.exi";
  private static final int FLAG_ENCRYPTED_INDEX = 1;
  private static final int VERSION = 2;
  private final AtomicFile atomicFile;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private boolean changed;
  private final Cipher cipher;
  private final boolean encrypt;
  private final SparseArray<String> idToKey;
  private final HashMap<String, CachedContent> keyToContent;
  private final SecretKeySpec secretKeySpec;
  
  public CachedContentIndex(File paramFile)
  {
    this(paramFile, null);
  }
  
  public CachedContentIndex(File paramFile, byte[] paramArrayOfByte)
  {
    this(paramFile, paramArrayOfByte, bool);
  }
  
  public CachedContentIndex(File paramFile, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    encrypt = paramBoolean;
    boolean bool = true;
    if (paramArrayOfByte != null)
    {
      if (paramArrayOfByte.length == 16) {
        paramBoolean = bool;
      } else {
        paramBoolean = false;
      }
      Assertions.checkArgument(paramBoolean);
      try
      {
        Cipher localCipher = getCipher();
        cipher = localCipher;
        paramArrayOfByte = new SecretKeySpec(paramArrayOfByte, "AES");
        secretKeySpec = paramArrayOfByte;
      }
      catch (NoSuchAlgorithmException|NoSuchPaddingException paramFile)
      {
        throw new IllegalStateException(paramFile);
      }
    }
    else
    {
      Assertions.checkState(paramBoolean ^ true);
      cipher = null;
      secretKeySpec = null;
    }
    keyToContent = new HashMap();
    idToKey = new SparseArray();
    atomicFile = new AtomicFile(new File(paramFile, "cached_content_index.exi"));
  }
  
  private void add(CachedContent paramCachedContent)
  {
    keyToContent.put(key, paramCachedContent);
    idToKey.put(length, key);
  }
  
  private CachedContent addNew(String paramString)
  {
    paramString = new CachedContent(getNewId(idToKey), paramString);
    add(paramString);
    changed = true;
    return paramString;
  }
  
  private static Cipher getCipher()
    throws NoSuchPaddingException, NoSuchAlgorithmException
  {
    if (Util.SDK_INT == 18) {}
    try
    {
      Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
      return localCipher;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    return Cipher.getInstance("AES/CBC/PKCS5PADDING");
  }
  
  public static int getNewId(SparseArray paramSparseArray)
  {
    int k = paramSparseArray.size();
    int i;
    if (k == 0) {
      i = 0;
    } else {
      i = paramSparseArray.keyAt(k - 1) + 1;
    }
    int j = i;
    if (i < 0)
    {
      i = 0;
      for (;;)
      {
        j = i;
        if (i >= k) {
          break;
        }
        if (i != paramSparseArray.keyAt(i)) {
          return i;
        }
        i += 1;
      }
    }
    return j;
  }
  
  /* Error */
  private boolean readFile()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 98	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lcom/google/android/exoplayer2/util/AtomicFile;
    //   4: astore 7
    //   6: new 167	java/io/BufferedInputStream
    //   9: dup
    //   10: aload 7
    //   12: invokevirtual 171	com/google/android/exoplayer2/util/AtomicFile:openRead	()Ljava/io/InputStream;
    //   15: invokespecial 174	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   18: astore 8
    //   20: new 176	java/io/DataInputStream
    //   23: dup
    //   24: aload 8
    //   26: invokespecial 177	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   29: astore 7
    //   31: aload 7
    //   33: astore 9
    //   35: aload 7
    //   37: invokevirtual 180	java/io/DataInputStream:readInt	()I
    //   40: istore_3
    //   41: iload_3
    //   42: iflt +353 -> 395
    //   45: iload_3
    //   46: iconst_2
    //   47: if_icmple +6 -> 53
    //   50: goto +345 -> 395
    //   53: aload 7
    //   55: astore 9
    //   57: aload 7
    //   59: invokevirtual 180	java/io/DataInputStream:readInt	()I
    //   62: istore_1
    //   63: iload_1
    //   64: iconst_1
    //   65: iand
    //   66: ifeq +152 -> 218
    //   69: aload 7
    //   71: astore 9
    //   73: aload_0
    //   74: getfield 60	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   77: astore 10
    //   79: aload 10
    //   81: ifnonnull +10 -> 91
    //   84: aload 7
    //   86: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   89: iconst_0
    //   90: ireturn
    //   91: aload 7
    //   93: astore 9
    //   95: bipush 16
    //   97: newarray byte
    //   99: astore 10
    //   101: aload 7
    //   103: astore 9
    //   105: aload 7
    //   107: aload 10
    //   109: invokevirtual 188	java/io/DataInputStream:readFully	([B)V
    //   112: aload 7
    //   114: astore 9
    //   116: new 190	javax/crypto/spec/IvParameterSpec
    //   119: dup
    //   120: aload 10
    //   122: invokespecial 192	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   125: astore 10
    //   127: aload_0
    //   128: getfield 60	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   131: astore 11
    //   133: aload_0
    //   134: getfield 69	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:secretKeySpec	Ljavax/crypto/spec/SecretKeySpec;
    //   137: astore 12
    //   139: aload 7
    //   141: astore 9
    //   143: aload 11
    //   145: iconst_2
    //   146: aload 12
    //   148: aload 10
    //   150: invokevirtual 196	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   153: aload_0
    //   154: getfield 60	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   157: astore 11
    //   159: aload 7
    //   161: astore 9
    //   163: aload 7
    //   165: astore 10
    //   167: new 176	java/io/DataInputStream
    //   170: dup
    //   171: new 198	javax/crypto/CipherInputStream
    //   174: dup
    //   175: aload 8
    //   177: aload 11
    //   179: invokespecial 201	javax/crypto/CipherInputStream:<init>	(Ljava/io/InputStream;Ljavax/crypto/Cipher;)V
    //   182: invokespecial 177	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   185: astore 8
    //   187: goto +59 -> 246
    //   190: astore 8
    //   192: aload 7
    //   194: astore 9
    //   196: aload 7
    //   198: astore 10
    //   200: new 71	java/lang/IllegalStateException
    //   203: dup
    //   204: aload 8
    //   206: invokespecial 74	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   209: astore 8
    //   211: aload 7
    //   213: astore 9
    //   215: aload 8
    //   217: athrow
    //   218: aload 7
    //   220: astore 9
    //   222: aload_0
    //   223: getfield 48	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:encrypt	Z
    //   226: istore 6
    //   228: aload 7
    //   230: astore 8
    //   232: iload 6
    //   234: ifeq +12 -> 246
    //   237: aload_0
    //   238: iconst_1
    //   239: putfield 128	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:changed	Z
    //   242: aload 7
    //   244: astore 8
    //   246: aload 8
    //   248: astore 9
    //   250: aload 8
    //   252: astore 10
    //   254: aload 8
    //   256: invokevirtual 180	java/io/DataInputStream:readInt	()I
    //   259: istore 4
    //   261: iconst_0
    //   262: istore_2
    //   263: iconst_0
    //   264: istore_1
    //   265: iload_2
    //   266: iload 4
    //   268: if_icmpge +61 -> 329
    //   271: aload 8
    //   273: astore 9
    //   275: aload 8
    //   277: astore 10
    //   279: iload_3
    //   280: aload 8
    //   282: invokestatic 205	com/google/android/exoplayer2/upstream/cache/CachedContent:readFromStream	(ILjava/io/DataInputStream;)Lcom/google/android/exoplayer2/upstream/cache/CachedContent;
    //   285: astore 7
    //   287: aload 8
    //   289: astore 9
    //   291: aload 8
    //   293: astore 10
    //   295: aload_0
    //   296: aload 7
    //   298: invokespecial 126	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:add	(Lcom/google/android/exoplayer2/upstream/cache/CachedContent;)V
    //   301: aload 8
    //   303: astore 9
    //   305: aload 8
    //   307: astore 10
    //   309: aload 7
    //   311: iload_3
    //   312: invokevirtual 208	com/google/android/exoplayer2/upstream/cache/CachedContent:headerHashCode	(I)I
    //   315: istore 5
    //   317: iload_1
    //   318: iload 5
    //   320: iadd
    //   321: istore_1
    //   322: iload_2
    //   323: iconst_1
    //   324: iadd
    //   325: istore_2
    //   326: goto -61 -> 265
    //   329: aload 8
    //   331: astore 9
    //   333: aload 8
    //   335: astore 10
    //   337: aload 8
    //   339: invokevirtual 180	java/io/DataInputStream:readInt	()I
    //   342: istore_3
    //   343: aload 8
    //   345: astore 9
    //   347: aload 8
    //   349: astore 10
    //   351: aload 8
    //   353: invokevirtual 211	java/io/DataInputStream:read	()I
    //   356: istore_2
    //   357: iload_2
    //   358: iconst_m1
    //   359: if_icmpne +8 -> 367
    //   362: iconst_1
    //   363: istore_2
    //   364: goto +5 -> 369
    //   367: iconst_0
    //   368: istore_2
    //   369: iload_3
    //   370: iload_1
    //   371: if_icmpne +17 -> 388
    //   374: iload_2
    //   375: ifne +6 -> 381
    //   378: goto +10 -> 388
    //   381: aload 8
    //   383: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   386: iconst_1
    //   387: ireturn
    //   388: aload 8
    //   390: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   393: iconst_0
    //   394: ireturn
    //   395: aload 7
    //   397: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   400: iconst_0
    //   401: ireturn
    //   402: astore 7
    //   404: goto +11 -> 415
    //   407: goto +24 -> 431
    //   410: astore 7
    //   412: aconst_null
    //   413: astore 9
    //   415: aload 9
    //   417: ifnull +8 -> 425
    //   420: aload 9
    //   422: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   425: aload 7
    //   427: athrow
    //   428: aconst_null
    //   429: astore 7
    //   431: aload 7
    //   433: ifnull +39 -> 472
    //   436: aload 7
    //   438: invokestatic 184	com/google/android/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   441: iconst_0
    //   442: ireturn
    //   443: astore 7
    //   445: goto -17 -> 428
    //   448: astore 8
    //   450: goto -43 -> 407
    //   453: astore 8
    //   455: goto -48 -> 407
    //   458: astore 8
    //   460: goto -53 -> 407
    //   463: astore 7
    //   465: aload 10
    //   467: astore 7
    //   469: goto -62 -> 407
    //   472: iconst_0
    //   473: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	474	0	this	CachedContentIndex
    //   62	310	1	i	int
    //   262	113	2	j	int
    //   40	332	3	k	int
    //   259	10	4	m	int
    //   315	6	5	n	int
    //   226	7	6	bool	boolean
    //   4	392	7	localObject1	Object
    //   402	1	7	localThrowable1	Throwable
    //   410	16	7	localThrowable2	Throwable
    //   429	8	7	localCloseable	java.io.Closeable
    //   443	1	7	localIOException1	java.io.IOException
    //   463	1	7	localIOException2	java.io.IOException
    //   467	1	7	localObject2	Object
    //   18	168	8	localObject3	Object
    //   190	15	8	localInvalidKeyException	java.security.InvalidKeyException
    //   209	180	8	localObject4	Object
    //   448	1	8	localIOException3	java.io.IOException
    //   453	1	8	localIOException4	java.io.IOException
    //   458	1	8	localIOException5	java.io.IOException
    //   33	388	9	localObject5	Object
    //   77	389	10	localObject6	Object
    //   131	47	11	localCipher	Cipher
    //   137	10	12	localSecretKeySpec	SecretKeySpec
    // Exception table:
    //   from	to	target	type
    //   143	153	190	java/security/InvalidKeyException
    //   143	153	190	java/security/InvalidAlgorithmParameterException
    //   35	41	402	java/lang/Throwable
    //   57	63	402	java/lang/Throwable
    //   73	79	402	java/lang/Throwable
    //   95	101	402	java/lang/Throwable
    //   105	112	402	java/lang/Throwable
    //   116	127	402	java/lang/Throwable
    //   143	153	402	java/lang/Throwable
    //   167	187	402	java/lang/Throwable
    //   200	211	402	java/lang/Throwable
    //   215	218	402	java/lang/Throwable
    //   222	228	402	java/lang/Throwable
    //   254	261	402	java/lang/Throwable
    //   279	287	402	java/lang/Throwable
    //   295	301	402	java/lang/Throwable
    //   309	317	402	java/lang/Throwable
    //   337	343	402	java/lang/Throwable
    //   351	357	402	java/lang/Throwable
    //   0	6	410	java/lang/Throwable
    //   6	20	410	java/lang/Throwable
    //   20	31	410	java/lang/Throwable
    //   6	20	443	java/io/IOException
    //   20	31	443	java/io/IOException
    //   35	41	448	java/io/IOException
    //   57	63	448	java/io/IOException
    //   105	112	453	java/io/IOException
    //   116	127	453	java/io/IOException
    //   143	153	458	java/io/IOException
    //   167	187	463	java/io/IOException
    //   200	211	463	java/io/IOException
    //   254	261	463	java/io/IOException
    //   279	287	463	java/io/IOException
    //   295	301	463	java/io/IOException
    //   309	317	463	java/io/IOException
    //   337	343	463	java/io/IOException
    //   351	357	463	java/io/IOException
  }
  
  private void writeFile()
    throws Cache.CacheException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void applyContentMetadataMutations(String paramString, ContentMetadataMutations paramContentMetadataMutations)
  {
    if (getOrAdd(paramString).applyMetadataMutations(paramContentMetadataMutations)) {
      changed = true;
    }
  }
  
  public int assignIdForKey(String paramString)
  {
    return getOrAddlength;
  }
  
  public CachedContent get(String paramString)
  {
    return (CachedContent)keyToContent.get(paramString);
  }
  
  public Collection getAll()
  {
    return keyToContent.values();
  }
  
  public ContentMetadata getContentMetadata(String paramString)
  {
    paramString = get(paramString);
    if (paramString != null) {
      return paramString.getMetadata();
    }
    return DefaultContentMetadata.EMPTY;
  }
  
  public String getKeyForId(int paramInt)
  {
    return (String)idToKey.get(paramInt);
  }
  
  public Set getKeys()
  {
    return keyToContent.keySet();
  }
  
  public CachedContent getOrAdd(String paramString)
  {
    CachedContent localCachedContent2 = (CachedContent)keyToContent.get(paramString);
    CachedContent localCachedContent1 = localCachedContent2;
    if (localCachedContent2 == null) {
      localCachedContent1 = addNew(paramString);
    }
    return localCachedContent1;
  }
  
  public void load()
  {
    Assertions.checkState(changed ^ true);
    if (!readFile())
    {
      atomicFile.delete();
      keyToContent.clear();
      idToKey.clear();
    }
  }
  
  public void maybeRemove(String paramString)
  {
    CachedContent localCachedContent = (CachedContent)keyToContent.get(paramString);
    if ((localCachedContent != null) && (localCachedContent.isEmpty()) && (!localCachedContent.isLocked()))
    {
      keyToContent.remove(paramString);
      idToKey.remove(length);
      changed = true;
    }
  }
  
  public void removeEmpty()
  {
    String[] arrayOfString = new String[keyToContent.size()];
    keyToContent.keySet().toArray(arrayOfString);
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      maybeRemove(arrayOfString[i]);
      i += 1;
    }
  }
  
  public void store()
    throws Cache.CacheException
  {
    if (!changed) {
      return;
    }
    writeFile();
    changed = false;
  }
}
