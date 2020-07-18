package androidx.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.FileNotFoundException;

public final class PrintHelper
{
  @SuppressLint({"InlinedApi"})
  public static final int COLOR_MODE_COLOR = 2;
  @SuppressLint({"InlinedApi"})
  public static final int COLOR_MODE_MONOCHROME = 1;
  static final boolean IS_MIN_MARGINS_HANDLING_CORRECT;
  private static final String LOG_TAG = "PrintHelper";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  static final boolean PRINT_ACTIVITY_RESPECTS_ORIENTATION;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  final Object mLock = new Object();
  int mOrientation = 1;
  int mScaleMode = 2;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    boolean bool2 = false;
    if ((i >= 20) && (Build.VERSION.SDK_INT <= 23)) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    PRINT_ACTIVITY_RESPECTS_ORIENTATION = bool1;
    boolean bool1 = bool2;
    if (Build.VERSION.SDK_INT != 23) {
      bool1 = true;
    }
    IS_MIN_MARGINS_HANDLING_CORRECT = bool1;
  }
  
  public PrintHelper(Context paramContext)
  {
    mContext = paramContext;
  }
  
  static Bitmap convertBitmapForColorMode(Bitmap paramBitmap, int paramInt)
  {
    if (paramInt != 1) {
      return paramBitmap;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    ColorMatrix localColorMatrix = new ColorMatrix();
    localColorMatrix.setSaturation(0.0F);
    localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  private static PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
  {
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder().setMediaSize(paramPrintAttributes.getMediaSize()).setResolution(paramPrintAttributes.getResolution()).setMinMargins(paramPrintAttributes.getMinMargins());
    if (paramPrintAttributes.getColorMode() != 0) {
      localBuilder.setColorMode(paramPrintAttributes.getColorMode());
    }
    if ((Build.VERSION.SDK_INT >= 23) && (paramPrintAttributes.getDuplexMode() != 0)) {
      localBuilder.setDuplexMode(paramPrintAttributes.getDuplexMode());
    }
    return localBuilder;
  }
  
  static Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f1 = paramRectF.width();
    float f2 = paramInt1;
    f1 /= f2;
    if (paramInt3 == 2) {
      f1 = Math.max(f1, paramRectF.height() / paramInt2);
    } else {
      f1 = Math.min(f1, paramRectF.height() / paramInt2);
    }
    localMatrix.postScale(f1, f1);
    localMatrix.postTranslate((paramRectF.width() - f2 * f1) / 2.0F, (paramRectF.height() - paramInt2 * f1) / 2.0F);
    return localMatrix;
  }
  
  static boolean isPortrait(Bitmap paramBitmap)
  {
    return paramBitmap.getWidth() <= paramBitmap.getHeight();
  }
  
  /* Error */
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +87 -> 88
    //   4: aload_0
    //   5: getfield 77	androidx/print/PrintHelper:mContext	Landroid/content/Context;
    //   8: ifnull +80 -> 88
    //   11: aconst_null
    //   12: astore_3
    //   13: aload_0
    //   14: getfield 77	androidx/print/PrintHelper:mContext	Landroid/content/Context;
    //   17: invokevirtual 219	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   20: aload_1
    //   21: invokevirtual 225	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   24: astore_1
    //   25: aload_1
    //   26: aconst_null
    //   27: aload_2
    //   28: invokestatic 231	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   31: astore_2
    //   32: aload_1
    //   33: ifnull +65 -> 98
    //   36: aload_1
    //   37: invokevirtual 236	java/io/InputStream:close	()V
    //   40: aload_2
    //   41: areturn
    //   42: astore_1
    //   43: ldc 33
    //   45: ldc -18
    //   47: aload_1
    //   48: invokestatic 244	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   51: pop
    //   52: aload_2
    //   53: areturn
    //   54: astore_3
    //   55: aload_1
    //   56: astore_2
    //   57: aload_3
    //   58: astore_1
    //   59: goto +6 -> 65
    //   62: astore_1
    //   63: aload_3
    //   64: astore_2
    //   65: aload_2
    //   66: ifnull +20 -> 86
    //   69: aload_2
    //   70: invokevirtual 236	java/io/InputStream:close	()V
    //   73: goto +13 -> 86
    //   76: astore_2
    //   77: ldc 33
    //   79: ldc -18
    //   81: aload_2
    //   82: invokestatic 244	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   85: pop
    //   86: aload_1
    //   87: athrow
    //   88: new 246	java/lang/IllegalArgumentException
    //   91: dup
    //   92: ldc -8
    //   94: invokespecial 251	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   97: athrow
    //   98: aload_2
    //   99: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	PrintHelper
    //   0	100	1	paramUri	Uri
    //   0	100	2	paramOptions	BitmapFactory.Options
    //   12	1	3	localObject	Object
    //   54	10	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   36	40	42	java/io/IOException
    //   25	32	54	java/lang/Throwable
    //   13	25	62	java/lang/Throwable
    //   69	73	76	java/io/IOException
  }
  
  public static boolean systemSupportsPrint()
  {
    return Build.VERSION.SDK_INT >= 19;
  }
  
  public int getColorMode()
  {
    return mColorMode;
  }
  
  public int getOrientation()
  {
    if ((Build.VERSION.SDK_INT >= 19) && (mOrientation == 0)) {
      return 1;
    }
    return mOrientation;
  }
  
  public int getScaleMode()
  {
    return mScaleMode;
  }
  
  /* Error */
  Bitmap loadConstrainedBitmap(Uri paramUri)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +217 -> 218
    //   4: aload_0
    //   5: getfield 77	androidx/print/PrintHelper:mContext	Landroid/content/Context;
    //   8: ifnull +210 -> 218
    //   11: new 260	android/graphics/BitmapFactory$Options
    //   14: dup
    //   15: invokespecial 261	android/graphics/BitmapFactory$Options:<init>	()V
    //   18: astore 6
    //   20: aload 6
    //   22: iconst_1
    //   23: putfield 264	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   26: aload_0
    //   27: aload_1
    //   28: aload 6
    //   30: invokespecial 266	androidx/print/PrintHelper:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   33: pop
    //   34: aload 6
    //   36: getfield 269	android/graphics/BitmapFactory$Options:outWidth	I
    //   39: istore 4
    //   41: aload 6
    //   43: getfield 272	android/graphics/BitmapFactory$Options:outHeight	I
    //   46: istore 5
    //   48: iload 4
    //   50: ifle +166 -> 216
    //   53: iload 5
    //   55: ifgt +5 -> 60
    //   58: aconst_null
    //   59: areturn
    //   60: iload 4
    //   62: iload 5
    //   64: invokestatic 275	java/lang/Math:max	(II)I
    //   67: istore_3
    //   68: iconst_1
    //   69: istore_2
    //   70: iload_3
    //   71: sipush 3500
    //   74: if_icmple +14 -> 88
    //   77: iload_3
    //   78: iconst_1
    //   79: iushr
    //   80: istore_3
    //   81: iload_2
    //   82: iconst_1
    //   83: ishl
    //   84: istore_2
    //   85: goto -15 -> 70
    //   88: iload_2
    //   89: ifle +140 -> 229
    //   92: iload 4
    //   94: iload 5
    //   96: invokestatic 277	java/lang/Math:min	(II)I
    //   99: iload_2
    //   100: idiv
    //   101: ifgt +5 -> 106
    //   104: aconst_null
    //   105: areturn
    //   106: aload_0
    //   107: getfield 69	androidx/print/PrintHelper:mLock	Ljava/lang/Object;
    //   110: astore 6
    //   112: aload 6
    //   114: monitorenter
    //   115: aload_0
    //   116: new 260	android/graphics/BitmapFactory$Options
    //   119: dup
    //   120: invokespecial 261	android/graphics/BitmapFactory$Options:<init>	()V
    //   123: putfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   126: aload_0
    //   127: getfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   130: iconst_1
    //   131: putfield 280	android/graphics/BitmapFactory$Options:inMutable	Z
    //   134: aload_0
    //   135: getfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   138: iload_2
    //   139: putfield 283	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   142: aload_0
    //   143: getfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   146: astore 7
    //   148: aload 6
    //   150: monitorexit
    //   151: aload_0
    //   152: aload_1
    //   153: aload 7
    //   155: invokespecial 266	androidx/print/PrintHelper:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   158: astore 6
    //   160: aload_0
    //   161: getfield 69	androidx/print/PrintHelper:mLock	Ljava/lang/Object;
    //   164: astore_1
    //   165: aload_1
    //   166: monitorenter
    //   167: aload_0
    //   168: aconst_null
    //   169: putfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   172: aload_1
    //   173: monitorexit
    //   174: aload 6
    //   176: areturn
    //   177: astore 6
    //   179: aload_1
    //   180: monitorexit
    //   181: aload 6
    //   183: athrow
    //   184: astore 6
    //   186: aload_0
    //   187: getfield 69	androidx/print/PrintHelper:mLock	Ljava/lang/Object;
    //   190: astore_1
    //   191: aload_1
    //   192: monitorenter
    //   193: aload_0
    //   194: aconst_null
    //   195: putfield 67	androidx/print/PrintHelper:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   198: aload_1
    //   199: monitorexit
    //   200: aload 6
    //   202: athrow
    //   203: astore 6
    //   205: aload_1
    //   206: monitorexit
    //   207: aload 6
    //   209: athrow
    //   210: astore_1
    //   211: aload 6
    //   213: monitorexit
    //   214: aload_1
    //   215: athrow
    //   216: aconst_null
    //   217: areturn
    //   218: new 246	java/lang/IllegalArgumentException
    //   221: dup
    //   222: ldc_w 285
    //   225: invokespecial 251	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   228: athrow
    //   229: aconst_null
    //   230: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	PrintHelper
    //   0	231	1	paramUri	Uri
    //   69	70	2	i	int
    //   67	14	3	j	int
    //   39	54	4	k	int
    //   46	49	5	m	int
    //   18	157	6	localObject	Object
    //   177	5	6	localThrowable1	Throwable
    //   184	17	6	localThrowable2	Throwable
    //   203	9	6	localThrowable3	Throwable
    //   146	8	7	localOptions	BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   167	174	177	java/lang/Throwable
    //   179	181	177	java/lang/Throwable
    //   151	160	184	java/lang/Throwable
    //   193	200	203	java/lang/Throwable
    //   205	207	203	java/lang/Throwable
    //   115	151	210	java/lang/Throwable
    //   211	214	210	java/lang/Throwable
  }
  
  public void printBitmap(String paramString, Bitmap paramBitmap)
  {
    printBitmap(paramString, paramBitmap, null);
  }
  
  public void printBitmap(String paramString, Bitmap paramBitmap, OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      if (paramBitmap == null) {
        return;
      }
      PrintManager localPrintManager = (PrintManager)mContext.getSystemService("print");
      if (isPortrait(paramBitmap)) {
        localObject = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
      } else {
        localObject = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
      }
      Object localObject = new PrintAttributes.Builder().setMediaSize((PrintAttributes.MediaSize)localObject).setColorMode(mColorMode).build();
      localPrintManager.print(paramString, new PrintBitmapAdapter(paramString, mScaleMode, paramBitmap, paramOnPrintFinishCallback), (PrintAttributes)localObject);
    }
  }
  
  public void printBitmap(String paramString, Uri paramUri)
    throws FileNotFoundException
  {
    printBitmap(paramString, paramUri, null);
  }
  
  public void printBitmap(String paramString, Uri paramUri, OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    if (Build.VERSION.SDK_INT < 19) {
      return;
    }
    paramUri = new PrintUriAdapter(paramString, paramUri, paramOnPrintFinishCallback, mScaleMode);
    paramOnPrintFinishCallback = (PrintManager)mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(mColorMode);
    if ((mOrientation != 1) && (mOrientation != 0))
    {
      if (mOrientation == 2) {
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }
    else {
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
    }
    paramOnPrintFinishCallback.print(paramString, paramUri, localBuilder.build());
  }
  
  public void setColorMode(int paramInt)
  {
    mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    mScaleMode = paramInt;
  }
  
  void writeBitmap(final PrintAttributes paramPrintAttributes, final int paramInt, final Bitmap paramBitmap, final ParcelFileDescriptor paramParcelFileDescriptor, final CancellationSignal paramCancellationSignal, final PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
  {
    final PrintAttributes localPrintAttributes;
    if (IS_MIN_MARGINS_HANDLING_CORRECT) {
      localPrintAttributes = paramPrintAttributes;
    } else {
      localPrintAttributes = copyAttributes(paramPrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build();
    }
    new AsyncTask()
    {
      /* Error */
      protected Throwable doInBackground(Void... paramAnonymousVarArgs)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 31	androidx/print/PrintHelper$1:val$cancellationSignal	Landroid/os/CancellationSignal;
        //   4: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
        //   7: istore_2
        //   8: iload_2
        //   9: ifeq +5 -> 14
        //   12: aconst_null
        //   13: areturn
        //   14: new 66	android/print/pdf/PrintedPdfDocument
        //   17: dup
        //   18: aload_0
        //   19: getfield 29	androidx/print/PrintHelper$1:this$0	Landroidx/print/PrintHelper;
        //   22: getfield 70	androidx/print/PrintHelper:mContext	Landroid/content/Context;
        //   25: aload_0
        //   26: getfield 33	androidx/print/PrintHelper$1:val$pdfAttributes	Landroid/print/PrintAttributes;
        //   29: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
        //   32: astore 4
        //   34: aload_0
        //   35: getfield 35	androidx/print/PrintHelper$1:val$bitmap	Landroid/graphics/Bitmap;
        //   38: aload_0
        //   39: getfield 33	androidx/print/PrintHelper$1:val$pdfAttributes	Landroid/print/PrintAttributes;
        //   42: invokevirtual 79	android/print/PrintAttributes:getColorMode	()I
        //   45: invokestatic 83	androidx/print/PrintHelper:convertBitmapForColorMode	(Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
        //   48: astore_3
        //   49: aload_0
        //   50: getfield 31	androidx/print/PrintHelper$1:val$cancellationSignal	Landroid/os/CancellationSignal;
        //   53: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +5 -> 63
        //   61: aconst_null
        //   62: areturn
        //   63: aload 4
        //   65: iconst_1
        //   66: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
        //   69: astore 5
        //   71: getstatic 91	androidx/print/PrintHelper:IS_MIN_MARGINS_HANDLING_CORRECT	Z
        //   74: istore_2
        //   75: iload_2
        //   76: ifeq +22 -> 98
        //   79: new 93	android/graphics/RectF
        //   82: dup
        //   83: aload 5
        //   85: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
        //   88: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
        //   91: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
        //   94: astore_1
        //   95: goto +59 -> 154
        //   98: new 66	android/print/pdf/PrintedPdfDocument
        //   101: dup
        //   102: aload_0
        //   103: getfield 29	androidx/print/PrintHelper$1:this$0	Landroidx/print/PrintHelper;
        //   106: getfield 70	androidx/print/PrintHelper:mContext	Landroid/content/Context;
        //   109: aload_0
        //   110: getfield 37	androidx/print/PrintHelper$1:val$attributes	Landroid/print/PrintAttributes;
        //   113: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
        //   116: astore 6
        //   118: aload 6
        //   120: iconst_1
        //   121: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
        //   124: astore 7
        //   126: new 93	android/graphics/RectF
        //   129: dup
        //   130: aload 7
        //   132: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
        //   135: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
        //   138: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
        //   141: astore_1
        //   142: aload 6
        //   144: aload 7
        //   146: invokevirtual 114	android/graphics/pdf/PdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
        //   149: aload 6
        //   151: invokevirtual 117	android/graphics/pdf/PdfDocument:close	()V
        //   154: aload_3
        //   155: invokevirtual 122	android/graphics/Bitmap:getWidth	()I
        //   158: aload_3
        //   159: invokevirtual 125	android/graphics/Bitmap:getHeight	()I
        //   162: aload_1
        //   163: aload_0
        //   164: getfield 39	androidx/print/PrintHelper$1:val$fittingMode	I
        //   167: invokestatic 129	androidx/print/PrintHelper:getMatrix	(IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
        //   170: astore 6
        //   172: getstatic 91	androidx/print/PrintHelper:IS_MIN_MARGINS_HANDLING_CORRECT	Z
        //   175: istore_2
        //   176: iload_2
        //   177: ifeq +6 -> 183
        //   180: goto +27 -> 207
        //   183: aload 6
        //   185: aload_1
        //   186: getfield 133	android/graphics/RectF:left	F
        //   189: aload_1
        //   190: getfield 136	android/graphics/RectF:top	F
        //   193: invokevirtual 142	android/graphics/Matrix:postTranslate	(FF)Z
        //   196: pop
        //   197: aload 5
        //   199: invokevirtual 146	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
        //   202: aload_1
        //   203: invokevirtual 152	android/graphics/Canvas:clipRect	(Landroid/graphics/RectF;)Z
        //   206: pop
        //   207: aload 5
        //   209: invokevirtual 146	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
        //   212: aload_3
        //   213: aload 6
        //   215: aconst_null
        //   216: invokevirtual 156	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
        //   219: aload 4
        //   221: aload 5
        //   223: invokevirtual 114	android/graphics/pdf/PdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
        //   226: aload_0
        //   227: getfield 31	androidx/print/PrintHelper$1:val$cancellationSignal	Landroid/os/CancellationSignal;
        //   230: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
        //   233: istore_2
        //   234: iload_2
        //   235: ifeq +42 -> 277
        //   238: aload 4
        //   240: invokevirtual 117	android/graphics/pdf/PdfDocument:close	()V
        //   243: aload_0
        //   244: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   247: astore_1
        //   248: aload_1
        //   249: ifnull +12 -> 261
        //   252: aload_0
        //   253: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   256: astore_1
        //   257: aload_1
        //   258: invokevirtual 159	android/os/ParcelFileDescriptor:close	()V
        //   261: aload_0
        //   262: getfield 35	androidx/print/PrintHelper$1:val$bitmap	Landroid/graphics/Bitmap;
        //   265: astore_1
        //   266: aload_3
        //   267: aload_1
        //   268: if_acmpeq +129 -> 397
        //   271: aload_3
        //   272: invokevirtual 162	android/graphics/Bitmap:recycle	()V
        //   275: aconst_null
        //   276: areturn
        //   277: aload 4
        //   279: new 164	java/io/FileOutputStream
        //   282: dup
        //   283: aload_0
        //   284: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   287: invokevirtual 168	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
        //   290: invokespecial 171	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
        //   293: invokevirtual 175	android/graphics/pdf/PdfDocument:writeTo	(Ljava/io/OutputStream;)V
        //   296: aload 4
        //   298: invokevirtual 117	android/graphics/pdf/PdfDocument:close	()V
        //   301: aload_0
        //   302: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   305: astore_1
        //   306: aload_1
        //   307: ifnull +12 -> 319
        //   310: aload_0
        //   311: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   314: astore_1
        //   315: aload_1
        //   316: invokevirtual 159	android/os/ParcelFileDescriptor:close	()V
        //   319: aload_0
        //   320: getfield 35	androidx/print/PrintHelper$1:val$bitmap	Landroid/graphics/Bitmap;
        //   323: astore_1
        //   324: aload_3
        //   325: aload_1
        //   326: if_acmpeq +71 -> 397
        //   329: aload_3
        //   330: invokevirtual 162	android/graphics/Bitmap:recycle	()V
        //   333: aconst_null
        //   334: areturn
        //   335: astore_1
        //   336: aload 4
        //   338: invokevirtual 117	android/graphics/pdf/PdfDocument:close	()V
        //   341: aload_0
        //   342: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   345: astore 4
        //   347: aload 4
        //   349: ifnull +14 -> 363
        //   352: aload_0
        //   353: getfield 41	androidx/print/PrintHelper$1:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
        //   356: astore 4
        //   358: aload 4
        //   360: invokevirtual 159	android/os/ParcelFileDescriptor:close	()V
        //   363: aload_0
        //   364: getfield 35	androidx/print/PrintHelper$1:val$bitmap	Landroid/graphics/Bitmap;
        //   367: astore 4
        //   369: aload_3
        //   370: aload 4
        //   372: if_acmpeq +7 -> 379
        //   375: aload_3
        //   376: invokevirtual 162	android/graphics/Bitmap:recycle	()V
        //   379: aload_1
        //   380: athrow
        //   381: astore_1
        //   382: aload_1
        //   383: areturn
        //   384: astore_1
        //   385: goto -124 -> 261
        //   388: astore_1
        //   389: goto -70 -> 319
        //   392: astore 4
        //   394: goto -31 -> 363
        //   397: aconst_null
        //   398: areturn
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	399	0	this	1
        //   0	399	1	paramAnonymousVarArgs	Void[]
        //   7	228	2	bool	boolean
        //   48	328	3	localBitmap	Bitmap
        //   32	339	4	localObject1	Object
        //   392	1	4	localIOException	java.io.IOException
        //   69	153	5	localPage1	android.graphics.pdf.PdfDocument.Page
        //   116	98	6	localObject2	Object
        //   124	21	7	localPage2	android.graphics.pdf.PdfDocument.Page
        // Exception table:
        //   from	to	target	type
        //   63	75	335	java/lang/Throwable
        //   79	95	335	java/lang/Throwable
        //   98	154	335	java/lang/Throwable
        //   154	176	335	java/lang/Throwable
        //   183	207	335	java/lang/Throwable
        //   207	234	335	java/lang/Throwable
        //   277	296	335	java/lang/Throwable
        //   0	8	381	java/lang/Throwable
        //   14	57	381	java/lang/Throwable
        //   238	248	381	java/lang/Throwable
        //   257	261	381	java/lang/Throwable
        //   261	266	381	java/lang/Throwable
        //   271	275	381	java/lang/Throwable
        //   296	306	381	java/lang/Throwable
        //   315	319	381	java/lang/Throwable
        //   319	324	381	java/lang/Throwable
        //   329	333	381	java/lang/Throwable
        //   336	347	381	java/lang/Throwable
        //   358	363	381	java/lang/Throwable
        //   363	369	381	java/lang/Throwable
        //   375	379	381	java/lang/Throwable
        //   379	381	381	java/lang/Throwable
        //   257	261	384	java/io/IOException
        //   315	319	388	java/io/IOException
        //   358	363	392	java/io/IOException
      }
      
      protected void onPostExecute(Throwable paramAnonymousThrowable)
      {
        if (paramCancellationSignal.isCanceled())
        {
          paramWriteResultCallback.onWriteCancelled();
          return;
        }
        if (paramAnonymousThrowable == null)
        {
          paramWriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
          return;
        }
        Log.e("PrintHelper", "Error writing printed content", paramAnonymousThrowable);
        paramWriteResultCallback.onWriteFailed(null);
      }
    }.execute(new Void[0]);
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
  
  @RequiresApi(19)
  private class PrintBitmapAdapter
    extends PrintDocumentAdapter
  {
    private PrintAttributes mAttributes;
    private final Bitmap mBitmap;
    private final PrintHelper.OnPrintFinishCallback mCallback;
    private final int mFittingMode;
    private final String mJobName;
    
    PrintBitmapAdapter(String paramString, int paramInt, Bitmap paramBitmap, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
    {
      mJobName = paramString;
      mFittingMode = paramInt;
      mBitmap = paramBitmap;
      mCallback = paramOnPrintFinishCallback;
    }
    
    public void onFinish()
    {
      if (mCallback != null) {
        mCallback.onFinish();
      }
    }
    
    public void onLayout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle)
    {
      mAttributes = paramPrintAttributes2;
      paramLayoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(mJobName).setContentType(1).setPageCount(1).build(), paramPrintAttributes2.equals(paramPrintAttributes1) ^ true);
    }
    
    public void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
    {
      writeBitmap(mAttributes, mFittingMode, mBitmap, paramParcelFileDescriptor, paramCancellationSignal, paramWriteResultCallback);
    }
  }
  
  @RequiresApi(19)
  private class PrintUriAdapter
    extends PrintDocumentAdapter
  {
    PrintAttributes mAttributes;
    Bitmap mBitmap;
    final PrintHelper.OnPrintFinishCallback mCallback;
    final int mFittingMode;
    final Uri mImageFile;
    final String mJobName;
    AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
    
    PrintUriAdapter(String paramString, Uri paramUri, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback, int paramInt)
    {
      mJobName = paramString;
      mImageFile = paramUri;
      mCallback = paramOnPrintFinishCallback;
      mFittingMode = paramInt;
      mBitmap = null;
    }
    
    void cancelLoad()
    {
      Object localObject = mLock;
      try
      {
        if (mDecodeOptions != null)
        {
          if (Build.VERSION.SDK_INT < 24) {
            mDecodeOptions.requestCancelDecode();
          }
          mDecodeOptions = null;
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void onFinish()
    {
      super.onFinish();
      cancelLoad();
      if (mLoadBitmap != null) {
        mLoadBitmap.cancel(true);
      }
      if (mCallback != null) {
        mCallback.onFinish();
      }
      if (mBitmap != null)
      {
        mBitmap.recycle();
        mBitmap = null;
      }
    }
    
    public void onLayout(final PrintAttributes paramPrintAttributes1, final PrintAttributes paramPrintAttributes2, final CancellationSignal paramCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle)
    {
      try
      {
        mAttributes = paramPrintAttributes2;
        if (paramCancellationSignal.isCanceled())
        {
          paramLayoutResultCallback.onLayoutCancelled();
          return;
        }
        if (mBitmap != null)
        {
          paramLayoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(mJobName).setContentType(1).setPageCount(1).build(), paramPrintAttributes2.equals(paramPrintAttributes1) ^ true);
          return;
        }
        mLoadBitmap = new AsyncTask()
        {
          protected Bitmap doInBackground(Uri... paramAnonymousVarArgs)
          {
            paramAnonymousVarArgs = PrintHelper.this;
            Uri localUri = mImageFile;
            try
            {
              paramAnonymousVarArgs = paramAnonymousVarArgs.loadConstrainedBitmap(localUri);
              return paramAnonymousVarArgs;
            }
            catch (FileNotFoundException paramAnonymousVarArgs)
            {
              for (;;) {}
            }
            return null;
          }
          
          protected void onCancelled(Bitmap paramAnonymousBitmap)
          {
            paramLayoutResultCallback.onLayoutCancelled();
            mLoadBitmap = null;
          }
          
          protected void onPostExecute(Bitmap paramAnonymousBitmap)
          {
            super.onPostExecute(paramAnonymousBitmap);
            Object localObject = paramAnonymousBitmap;
            if (paramAnonymousBitmap != null) {
              if (PrintHelper.PRINT_ACTIVITY_RESPECTS_ORIENTATION)
              {
                localObject = paramAnonymousBitmap;
                if (mOrientation != 0) {}
              }
              else
              {
                try
                {
                  PrintAttributes.MediaSize localMediaSize = mAttributes.getMediaSize();
                  localObject = paramAnonymousBitmap;
                  if (localMediaSize != null)
                  {
                    localObject = paramAnonymousBitmap;
                    if (localMediaSize.isPortrait() != PrintHelper.isPortrait(paramAnonymousBitmap))
                    {
                      localObject = new Matrix();
                      ((Matrix)localObject).postRotate(90.0F);
                      localObject = Bitmap.createBitmap(paramAnonymousBitmap, 0, 0, paramAnonymousBitmap.getWidth(), paramAnonymousBitmap.getHeight(), (Matrix)localObject, true);
                    }
                  }
                }
                catch (Throwable paramAnonymousBitmap)
                {
                  throw paramAnonymousBitmap;
                }
              }
            }
            mBitmap = ((Bitmap)localObject);
            if (localObject != null)
            {
              paramAnonymousBitmap = new PrintDocumentInfo.Builder(mJobName).setContentType(1).setPageCount(1).build();
              boolean bool = paramPrintAttributes2.equals(paramPrintAttributes1);
              paramLayoutResultCallback.onLayoutFinished(paramAnonymousBitmap, true ^ bool);
            }
            else
            {
              paramLayoutResultCallback.onLayoutFailed(null);
            }
            mLoadBitmap = null;
          }
          
          protected void onPreExecute()
          {
            paramCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
            {
              public void onCancel()
              {
                cancelLoad();
                cancel(false);
              }
            });
          }
        }.execute(new Uri[0]);
        return;
      }
      catch (Throwable paramPrintAttributes1)
      {
        throw paramPrintAttributes1;
      }
    }
    
    public void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
    {
      writeBitmap(mAttributes, mFittingMode, mBitmap, paramParcelFileDescriptor, paramCancellationSignal, paramWriteResultCallback);
    }
  }
}
