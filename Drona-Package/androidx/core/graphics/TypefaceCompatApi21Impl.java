package androidx.core.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.content.delay.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import androidx.core.content.delay.FontResourcesParserCompat.FontFileResourceEntry;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
class TypefaceCompatApi21Impl
  extends TypefaceCompatBaseImpl
{
  private static final String ADD_FONT_WEIGHT_STYLE_METHOD = "addFontWeightStyle";
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
  private static final String PAGE_KEY = "TypefaceCompatApi21Impl";
  private static Method sAddFontWeightStyle;
  private static Method sCreateFromFamiliesWithDefault;
  private static Class sFontFamily;
  private static Constructor sFontFamilyCtor;
  private static boolean sHasInitBeenCalled;
  
  TypefaceCompatApi21Impl() {}
  
  private static boolean addFontWeightStyle(Object paramObject, String paramString, int paramInt, boolean paramBoolean)
  {
    init();
    Method localMethod = sAddFontWeightStyle;
    try
    {
      paramObject = localMethod.invoke(paramObject, new Object[] { paramString, Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) });
      paramObject = (Boolean)paramObject;
      paramBoolean = paramObject.booleanValue();
      return paramBoolean;
    }
    catch (IllegalAccessException|InvocationTargetException paramObject)
    {
      throw new RuntimeException(paramObject);
    }
  }
  
  private static Typeface createFromFamiliesWithDefault(Object paramObject)
  {
    init();
    Object localObject = sFontFamily;
    try
    {
      localObject = Array.newInstance((Class)localObject, 1);
      Array.set(localObject, 0, paramObject);
      paramObject = sCreateFromFamiliesWithDefault;
      paramObject = paramObject.invoke(null, new Object[] { localObject });
      return (Typeface)paramObject;
    }
    catch (IllegalAccessException|InvocationTargetException paramObject)
    {
      throw new RuntimeException(paramObject);
    }
  }
  
  private File getFile(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("/proc/self/fd/");
      ((StringBuilder)localObject).append(paramParcelFileDescriptor.getFd());
      paramParcelFileDescriptor = Os.readlink(((StringBuilder)localObject).toString());
      localObject = Os.stat(paramParcelFileDescriptor);
      int i = st_mode;
      boolean bool = OsConstants.S_ISREG(i);
      if (bool)
      {
        paramParcelFileDescriptor = new File(paramParcelFileDescriptor);
        return paramParcelFileDescriptor;
      }
      return null;
    }
    catch (ErrnoException paramParcelFileDescriptor) {}
    return null;
  }
  
  private static void init()
  {
    if (sHasInitBeenCalled) {
      return;
    }
    sHasInitBeenCalled = true;
    Object localObject3 = null;
    Object localObject5;
    Object localObject4;
    Object localObject2;
    try
    {
      Object localObject6 = Class.forName("android.graphics.FontFamily");
      Object localObject1 = localObject6;
      localObject5 = ((Class)localObject6).getConstructor(new Class[0]);
      localObject4 = Integer.TYPE;
      Class localClass = Boolean.TYPE;
      localObject4 = ((Class)localObject6).getMethod("addFontWeightStyle", new Class[] { String.class, localObject4, localClass });
      localObject6 = Array.newInstance((Class)localObject6, 1);
      localObject6 = localObject6.getClass();
      localObject6 = Typeface.class.getMethod("createFromFamiliesWithDefault", new Class[] { localObject6 });
      localObject3 = localObject5;
      localObject5 = localObject6;
    }
    catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException)
    {
      Log.e("TypefaceCompatApi21Impl", localClassNotFoundException.getClass().getName(), localClassNotFoundException);
      localObject5 = null;
      localObject2 = null;
      localObject4 = null;
    }
    sFontFamilyCtor = localObject3;
    sFontFamily = localObject2;
    sAddFontWeightStyle = (Method)localObject4;
    sCreateFromFamiliesWithDefault = (Method)localObject5;
  }
  
  private static Object newFamily()
  {
    init();
    Object localObject = sFontFamilyCtor;
    try
    {
      localObject = ((Constructor)localObject).newInstance(new Object[0]);
      return localObject;
    }
    catch (IllegalAccessException|InstantiationException|InvocationTargetException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }
  
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    Object localObject = newFamily();
    FontResourcesParserCompat.FontFileResourceEntry[] arrayOfFontFileResourceEntry = paramFontFamilyFilesResourceEntry.getEntries();
    int i = arrayOfFontFileResourceEntry.length;
    paramInt = 0;
    for (;;)
    {
      FontResourcesParserCompat.FontFileResourceEntry localFontFileResourceEntry;
      if (paramInt < i)
      {
        localFontFileResourceEntry = arrayOfFontFileResourceEntry[paramInt];
        paramFontFamilyFilesResourceEntry = TypefaceCompatUtil.getTempFile(paramContext);
        if (paramFontFamilyFilesResourceEntry == null) {
          return null;
        }
      }
      try
      {
        bool = TypefaceCompatUtil.copyToFile(paramFontFamilyFilesResourceEntry, paramResources, localFontFileResourceEntry.getResourceId());
        if (!bool)
        {
          paramFontFamilyFilesResourceEntry.delete();
          return null;
        }
      }
      catch (Throwable paramContext)
      {
        try
        {
          boolean bool = addFontWeightStyle(localObject, paramFontFamilyFilesResourceEntry.getPath(), localFontFileResourceEntry.getWeight(), localFontFileResourceEntry.isItalic());
          if (!bool)
          {
            paramFontFamilyFilesResourceEntry.delete();
            return null;
          }
          paramFontFamilyFilesResourceEntry.delete();
          paramInt += 1;
        }
        catch (RuntimeException paramContext)
        {
          for (;;) {}
        }
        paramContext = paramContext;
        paramFontFamilyFilesResourceEntry.delete();
        throw paramContext;
        paramFontFamilyFilesResourceEntry.delete();
        return null;
        return createFromFamiliesWithDefault(localObject);
      }
      catch (RuntimeException paramContext)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  public Typeface createFromFontInfo(Context paramContext, android.os.CancellationSignal paramCancellationSignal, androidx.core.provider.FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +5 -> 8
    //   6: aconst_null
    //   7: areturn
    //   8: aload_0
    //   9: aload_3
    //   10: iload 4
    //   12: invokevirtual 247	androidx/core/graphics/TypefaceCompatBaseImpl:findBestInfo	([Landroidx/core/provider/FontsContractCompat$FontInfo;I)Landroidx/core/provider/FontsContractCompat$FontInfo;
    //   15: astore_3
    //   16: aload_1
    //   17: invokevirtual 253	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   20: astore 6
    //   22: aload 6
    //   24: aload_3
    //   25: invokevirtual 259	androidx/core/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   28: ldc_w 261
    //   31: aload_2
    //   32: invokevirtual 267	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   35: astore_3
    //   36: aload_3
    //   37: ifnonnull +13 -> 50
    //   40: aload_3
    //   41: ifnull +166 -> 207
    //   44: aload_3
    //   45: invokevirtual 270	android/os/ParcelFileDescriptor:close	()V
    //   48: aconst_null
    //   49: areturn
    //   50: aload_0
    //   51: aload_3
    //   52: invokespecial 272	androidx/core/graphics/TypefaceCompatApi21Impl:getFile	(Landroid/os/ParcelFileDescriptor;)Ljava/io/File;
    //   55: astore_2
    //   56: aload_2
    //   57: ifnull +34 -> 91
    //   60: aload_2
    //   61: invokevirtual 275	java/io/File:canRead	()Z
    //   64: istore 5
    //   66: iload 5
    //   68: ifne +6 -> 74
    //   71: goto +20 -> 91
    //   74: aload_2
    //   75: invokestatic 279	android/graphics/Typeface:createFromFile	(Ljava/io/File;)Landroid/graphics/Typeface;
    //   78: astore_2
    //   79: aload_2
    //   80: astore_1
    //   81: aload_3
    //   82: ifnull +127 -> 209
    //   85: aload_3
    //   86: invokevirtual 270	android/os/ParcelFileDescriptor:close	()V
    //   89: aload_2
    //   90: areturn
    //   91: new 281	java/io/FileInputStream
    //   94: dup
    //   95: aload_3
    //   96: invokevirtual 285	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   99: invokespecial 288	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   102: astore 6
    //   104: aload_0
    //   105: aload_1
    //   106: aload 6
    //   108: invokespecial 292	androidx/core/graphics/TypefaceCompatBaseImpl:createFromInputStream	(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
    //   111: astore_2
    //   112: aload 6
    //   114: invokevirtual 293	java/io/FileInputStream:close	()V
    //   117: aload_2
    //   118: astore_1
    //   119: aload_3
    //   120: ifnull +89 -> 209
    //   123: aload_3
    //   124: invokevirtual 270	android/os/ParcelFileDescriptor:close	()V
    //   127: aload_2
    //   128: areturn
    //   129: astore_1
    //   130: aload_1
    //   131: athrow
    //   132: astore_2
    //   133: aload_1
    //   134: ifnull +22 -> 156
    //   137: aload 6
    //   139: invokevirtual 293	java/io/FileInputStream:close	()V
    //   142: goto +19 -> 161
    //   145: astore 6
    //   147: aload_1
    //   148: aload 6
    //   150: invokevirtual 296	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   153: goto +8 -> 161
    //   156: aload 6
    //   158: invokevirtual 293	java/io/FileInputStream:close	()V
    //   161: aload_2
    //   162: athrow
    //   163: astore_1
    //   164: aload_1
    //   165: athrow
    //   166: astore_2
    //   167: aload_3
    //   168: ifnull +27 -> 195
    //   171: aload_1
    //   172: ifnull +19 -> 191
    //   175: aload_3
    //   176: invokevirtual 270	android/os/ParcelFileDescriptor:close	()V
    //   179: goto +16 -> 195
    //   182: astore_3
    //   183: aload_1
    //   184: aload_3
    //   185: invokevirtual 296	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   188: goto +7 -> 195
    //   191: aload_3
    //   192: invokevirtual 270	android/os/ParcelFileDescriptor:close	()V
    //   195: aload_2
    //   196: athrow
    //   197: astore_1
    //   198: aconst_null
    //   199: areturn
    //   200: astore_1
    //   201: aconst_null
    //   202: areturn
    //   203: astore_1
    //   204: aconst_null
    //   205: areturn
    //   206: astore_1
    //   207: aconst_null
    //   208: areturn
    //   209: aload_1
    //   210: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	this	TypefaceCompatApi21Impl
    //   0	211	1	paramContext	Context
    //   0	211	2	paramCancellationSignal	android.os.CancellationSignal
    //   0	211	3	paramArrayOfFontInfo	androidx.core.provider.FontsContractCompat.FontInfo[]
    //   0	211	4	paramInt	int
    //   64	3	5	bool	boolean
    //   20	118	6	localObject	Object
    //   145	12	6	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   104	112	129	java/lang/Throwable
    //   130	132	132	java/lang/Throwable
    //   137	142	145	java/lang/Throwable
    //   50	56	163	java/lang/Throwable
    //   60	66	163	java/lang/Throwable
    //   74	79	163	java/lang/Throwable
    //   91	104	163	java/lang/Throwable
    //   112	117	163	java/lang/Throwable
    //   147	153	163	java/lang/Throwable
    //   156	161	163	java/lang/Throwable
    //   161	163	163	java/lang/Throwable
    //   164	166	166	java/lang/Throwable
    //   175	179	182	java/lang/Throwable
    //   22	36	197	java/io/IOException
    //   44	48	197	java/io/IOException
    //   85	89	200	java/io/IOException
    //   123	127	203	java/io/IOException
    //   183	188	206	java/io/IOException
    //   191	195	206	java/io/IOException
    //   195	197	206	java/io/IOException
  }
}
