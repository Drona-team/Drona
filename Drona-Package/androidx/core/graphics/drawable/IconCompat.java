package androidx.core.graphics.drawable;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class IconCompat
  extends CustomVersionedParcelable
{
  private static final float ADAPTIVE_ICON_INSET_FACTOR = 0.25F;
  private static final int AMBIENT_SHADOW_ALPHA = 30;
  private static final float BLUR_FACTOR = 0.010416667F;
  static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
  private static final float DEFAULT_VIEW_PORT_SCALE = 0.6666667F;
  private static final String EXTRA_INT1 = "int1";
  private static final String EXTRA_INT2 = "int2";
  private static final String EXTRA_OBJ = "obj";
  private static final String EXTRA_TINT_LIST = "tint_list";
  private static final String EXTRA_TINT_MODE = "tint_mode";
  private static final String EXTRA_TYPE = "type";
  private static final float ICON_DIAMETER_FACTOR = 0.9166667F;
  private static final int KEY_SHADOW_ALPHA = 61;
  private static final float KEY_SHADOW_OFFSET_FACTOR = 0.020833334F;
  private static final String PAGE_KEY = "IconCompat";
  public static final int TYPE_UNKNOWN = -1;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public byte[] mData = null;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public int mInt1 = 0;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public int mInt2 = 0;
  Object mObj1;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public Parcelable mParcelable = null;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public ColorStateList mTintList = null;
  PorterDuff.Mode mTintMode = DEFAULT_TINT_MODE;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public String mTintModeStr = null;
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public int mType = -1;
  
  public IconCompat() {}
  
  private IconCompat(int paramInt)
  {
    mType = paramInt;
  }
  
  public static IconCompat createFromBundle(Bundle paramBundle)
  {
    int i = paramBundle.getInt("type");
    IconCompat localIconCompat = new IconCompat(i);
    mInt1 = paramBundle.getInt("int1");
    mInt2 = paramBundle.getInt("int2");
    if (paramBundle.containsKey("tint_list")) {
      mTintList = ((ColorStateList)paramBundle.getParcelable("tint_list"));
    }
    if (paramBundle.containsKey("tint_mode")) {
      mTintMode = PorterDuff.Mode.valueOf(paramBundle.getString("tint_mode"));
    }
    if (i != -1) {
      switch (i)
      {
      default: 
        paramBundle = new StringBuilder();
        paramBundle.append("Unknown type ");
        paramBundle.append(i);
        Log.w("IconCompat", paramBundle.toString());
        return null;
      case 3: 
        mObj1 = paramBundle.getByteArray("obj");
        return localIconCompat;
      case 2: 
      case 4: 
        mObj1 = paramBundle.getString("obj");
        return localIconCompat;
      }
    }
    mObj1 = paramBundle.getParcelable("obj");
    return localIconCompat;
  }
  
  public static IconCompat createFromIcon(Context paramContext, Icon paramIcon)
  {
    Preconditions.checkNotNull(paramIcon);
    int i = getType(paramIcon);
    if (i != 2)
    {
      if (i != 4)
      {
        paramContext = new IconCompat(-1);
        mObj1 = paramIcon;
        return paramContext;
      }
      return createWithContentUri(getUri(paramIcon));
    }
    String str = getResPackage(paramIcon);
    try
    {
      paramContext = createWithResource(getResources(paramContext, str), str, getResId(paramIcon));
      return paramContext;
    }
    catch (Resources.NotFoundException paramContext)
    {
      for (;;) {}
    }
    throw new IllegalArgumentException("Icon resource cannot be found");
  }
  
  public static IconCompat createFromIcon(Icon paramIcon)
  {
    Preconditions.checkNotNull(paramIcon);
    int i = getType(paramIcon);
    if (i != 2)
    {
      if (i != 4)
      {
        IconCompat localIconCompat = new IconCompat(-1);
        mObj1 = paramIcon;
        return localIconCompat;
      }
      return createWithContentUri(getUri(paramIcon));
    }
    return createWithResource(null, getResPackage(paramIcon), getResId(paramIcon));
  }
  
  static Bitmap createLegacyIconFromAdaptiveIcon(Bitmap paramBitmap, boolean paramBoolean)
  {
    int i = (int)(Math.min(paramBitmap.getWidth(), paramBitmap.getHeight()) * 0.6666667F);
    Bitmap localBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint(3);
    float f1 = i;
    float f2 = 0.5F * f1;
    float f3 = 0.9166667F * f2;
    if (paramBoolean)
    {
      float f4 = 0.010416667F * f1;
      localPaint.setColor(0);
      localPaint.setShadowLayer(f4, 0.0F, f1 * 0.020833334F, 1023410176);
      localCanvas.drawCircle(f2, f2, f3, localPaint);
      localPaint.setShadowLayer(f4, 0.0F, 0.0F, 503316480);
      localCanvas.drawCircle(f2, f2, f3, localPaint);
      localPaint.clearShadowLayer();
    }
    localPaint.setColor(-16777216);
    BitmapShader localBitmapShader = new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Matrix localMatrix = new Matrix();
    localMatrix.setTranslate(-(paramBitmap.getWidth() - i) / 2, -(paramBitmap.getHeight() - i) / 2);
    localBitmapShader.setLocalMatrix(localMatrix);
    localPaint.setShader(localBitmapShader);
    localCanvas.drawCircle(f2, f2, f3, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  public static IconCompat createWithAdaptiveBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap != null)
    {
      IconCompat localIconCompat = new IconCompat(5);
      mObj1 = paramBitmap;
      return localIconCompat;
    }
    throw new IllegalArgumentException("Bitmap must not be null.");
  }
  
  public static IconCompat createWithBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap != null)
    {
      IconCompat localIconCompat = new IconCompat(1);
      mObj1 = paramBitmap;
      return localIconCompat;
    }
    throw new IllegalArgumentException("Bitmap must not be null.");
  }
  
  public static IconCompat createWithContentUri(Uri paramUri)
  {
    if (paramUri != null) {
      return createWithContentUri(paramUri.toString());
    }
    throw new IllegalArgumentException("Uri must not be null.");
  }
  
  public static IconCompat createWithContentUri(String paramString)
  {
    if (paramString != null)
    {
      IconCompat localIconCompat = new IconCompat(4);
      mObj1 = paramString;
      return localIconCompat;
    }
    throw new IllegalArgumentException("Uri must not be null.");
  }
  
  public static IconCompat createWithData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte != null)
    {
      IconCompat localIconCompat = new IconCompat(3);
      mObj1 = paramArrayOfByte;
      mInt1 = paramInt1;
      mInt2 = paramInt2;
      return localIconCompat;
    }
    throw new IllegalArgumentException("Data must not be null.");
  }
  
  public static IconCompat createWithResource(Context paramContext, int paramInt)
  {
    if (paramContext != null) {
      return createWithResource(paramContext.getResources(), paramContext.getPackageName(), paramInt);
    }
    throw new IllegalArgumentException("Context must not be null.");
  }
  
  public static IconCompat createWithResource(Resources paramResources, String paramString, int paramInt)
  {
    IconCompat localIconCompat;
    if (paramString != null) {
      if (paramInt != 0)
      {
        localIconCompat = new IconCompat(2);
        mInt1 = paramInt;
        if (paramResources == null) {}
      }
    }
    try
    {
      paramResources = paramResources.getResourceName(paramInt);
      mObj1 = paramResources;
      return localIconCompat;
    }
    catch (Resources.NotFoundException paramResources)
    {
      for (;;) {}
    }
    throw new IllegalArgumentException("Icon resource cannot be found");
    mObj1 = paramString;
    return localIconCompat;
    throw new IllegalArgumentException("Drawable resource ID must not be 0");
    throw new IllegalArgumentException("Package must not be null.");
  }
  
  private static int getResId(Icon paramIcon)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramIcon.getResId();
    }
    try
    {
      Object localObject = paramIcon.getClass();
      localObject = ((Class)localObject).getMethod("getResId", new Class[0]);
      paramIcon = ((Method)localObject).invoke(paramIcon, new Object[0]);
      paramIcon = (Integer)paramIcon;
      int i = paramIcon.intValue();
      return i;
    }
    catch (NoSuchMethodException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon resource", paramIcon);
      return 0;
    }
    catch (InvocationTargetException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon resource", paramIcon);
      return 0;
    }
    catch (IllegalAccessException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon resource", paramIcon);
    }
    return 0;
  }
  
  private static String getResPackage(Icon paramIcon)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramIcon.getResPackage();
    }
    try
    {
      Object localObject = paramIcon.getClass();
      localObject = ((Class)localObject).getMethod("getResPackage", new Class[0]);
      paramIcon = ((Method)localObject).invoke(paramIcon, new Object[0]);
      return (String)paramIcon;
    }
    catch (NoSuchMethodException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon package", paramIcon);
      return null;
    }
    catch (InvocationTargetException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon package", paramIcon);
      return null;
    }
    catch (IllegalAccessException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon package", paramIcon);
    }
    return null;
  }
  
  private static Resources getResources(Context paramContext, String paramString)
  {
    if ("android".equals(paramString)) {
      return Resources.getSystem();
    }
    paramContext = paramContext.getPackageManager();
    try
    {
      ApplicationInfo localApplicationInfo = paramContext.getApplicationInfo(paramString, 8192);
      if (localApplicationInfo != null)
      {
        paramContext = paramContext.getResourcesForApplication(localApplicationInfo);
        return paramContext;
      }
      return null;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.e("IconCompat", String.format("Unable to find pkg=%s for icon", new Object[] { paramString }), paramContext);
    }
    return null;
  }
  
  private static int getType(Icon paramIcon)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramIcon.getType();
    }
    try
    {
      Object localObject = paramIcon.getClass();
      localObject = ((Class)localObject).getMethod("getType", new Class[0]);
      localObject = ((Method)localObject).invoke(paramIcon, new Object[0]);
      localObject = (Integer)localObject;
      int i = ((Integer)localObject).intValue();
      return i;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to get icon type ");
      localStringBuilder.append(paramIcon);
      Log.e("IconCompat", localStringBuilder.toString(), localNoSuchMethodException);
      return -1;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to get icon type ");
      localStringBuilder.append(paramIcon);
      Log.e("IconCompat", localStringBuilder.toString(), localInvocationTargetException);
      return -1;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to get icon type ");
      localStringBuilder.append(paramIcon);
      Log.e("IconCompat", localStringBuilder.toString(), localIllegalAccessException);
    }
    return -1;
  }
  
  private static Uri getUri(Icon paramIcon)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramIcon.getUri();
    }
    try
    {
      Object localObject = paramIcon.getClass();
      localObject = ((Class)localObject).getMethod("getUri", new Class[0]);
      paramIcon = ((Method)localObject).invoke(paramIcon, new Object[0]);
      return (Uri)paramIcon;
    }
    catch (NoSuchMethodException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon uri", paramIcon);
      return null;
    }
    catch (InvocationTargetException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon uri", paramIcon);
      return null;
    }
    catch (IllegalAccessException paramIcon)
    {
      Log.e("IconCompat", "Unable to get icon uri", paramIcon);
    }
    return null;
  }
  
  private Drawable loadDrawableInner(Context paramContext)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private static String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 5: 
      return "BITMAP_MASKABLE";
    case 4: 
      return "URI";
    case 3: 
      return "DATA";
    case 2: 
      return "RESOURCE";
    }
    return "BITMAP";
  }
  
  public void addToShortcutIntent(Intent paramIntent, Drawable paramDrawable, Context paramContext)
  {
    checkResource(paramContext);
    int i = mType;
    int j;
    if (i != 5)
    {
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Icon type not supported for intent shortcuts");
      case 2: 
        try
        {
          paramContext = paramContext.createPackageContext(getResPackage(), 0);
          if (paramDrawable == null)
          {
            i = mInt1;
            paramIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(paramContext, i));
            return;
          }
          i = mInt1;
          localObject = ContextCompat.getDrawable(paramContext, i);
          i = ((Drawable)localObject).getIntrinsicWidth();
          if (i > 0)
          {
            i = ((Drawable)localObject).getIntrinsicHeight();
            if (i > 0)
            {
              i = ((Drawable)localObject).getIntrinsicWidth();
              j = ((Drawable)localObject).getIntrinsicHeight();
              paramContext = Bitmap.Config.ARGB_8888;
              paramContext = Bitmap.createBitmap(i, j, paramContext);
              break label196;
            }
          }
          paramContext = paramContext.getSystemService("activity");
          paramContext = (ActivityManager)paramContext;
          i = paramContext.getLauncherLargeIconSize();
          paramContext = Bitmap.Config.ARGB_8888;
          paramContext = Bitmap.createBitmap(i, i, paramContext);
          label196:
          ((Drawable)localObject).setBounds(0, 0, paramContext.getWidth(), paramContext.getHeight());
          ((Drawable)localObject).draw(new Canvas(paramContext));
        }
        catch (PackageManager.NameNotFoundException paramIntent)
        {
          paramDrawable = new StringBuilder();
          paramDrawable.append("Can't find package ");
          paramDrawable.append(mObj1);
          throw new IllegalArgumentException(paramDrawable.toString(), paramIntent);
        }
      }
      Object localObject = (Bitmap)mObj1;
      paramContext = (Context)localObject;
      if (paramDrawable != null) {
        paramContext = ((Bitmap)localObject).copy(((Bitmap)localObject).getConfig(), true);
      }
    }
    else
    {
      paramContext = createLegacyIconFromAdaptiveIcon((Bitmap)mObj1, true);
    }
    if (paramDrawable != null)
    {
      i = paramContext.getWidth();
      j = paramContext.getHeight();
      paramDrawable.setBounds(i / 2, j / 2, i, j);
      paramDrawable.draw(new Canvas(paramContext));
    }
    paramIntent.putExtra("android.intent.extra.shortcut.ICON", paramContext);
  }
  
  public void checkResource(Context paramContext)
  {
    if (mType == 2)
    {
      String str3 = (String)mObj1;
      if (!str3.contains(":")) {
        return;
      }
      String str2 = str3.split(":", -1)[1];
      String str1 = str2.split("/", -1)[0];
      str2 = str2.split("/", -1)[1];
      str3 = str3.split(":", -1)[0];
      int i = getResources(paramContext, str3).getIdentifier(str2, str1, str3);
      if (mInt1 != i)
      {
        paramContext = new StringBuilder();
        paramContext.append("Id has changed for ");
        paramContext.append(str3);
        paramContext.append("/");
        paramContext.append(str2);
        Log.i("IconCompat", paramContext.toString());
        mInt1 = i;
      }
    }
  }
  
  public Bitmap getBitmap()
  {
    if ((mType == -1) && (Build.VERSION.SDK_INT >= 23))
    {
      if ((mObj1 instanceof Bitmap)) {
        return (Bitmap)mObj1;
      }
      return null;
    }
    if (mType == 1) {
      return (Bitmap)mObj1;
    }
    if (mType == 5) {
      return createLegacyIconFromAdaptiveIcon((Bitmap)mObj1, true);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("called getBitmap() on ");
    localStringBuilder.append(this);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public int getResId()
  {
    if ((mType == -1) && (Build.VERSION.SDK_INT >= 23)) {
      return getResId((Icon)mObj1);
    }
    if (mType == 2) {
      return mInt1;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("called getResId() on ");
    localStringBuilder.append(this);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public String getResPackage()
  {
    if ((mType == -1) && (Build.VERSION.SDK_INT >= 23)) {
      return getResPackage((Icon)mObj1);
    }
    if (mType == 2) {
      return ((String)mObj1).split(":", -1)[0];
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("called getResPackage() on ");
    localStringBuilder.append(this);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  public int getType()
  {
    if ((mType == -1) && (Build.VERSION.SDK_INT >= 23)) {
      return getType((Icon)mObj1);
    }
    return mType;
  }
  
  public Uri getUri()
  {
    if ((mType == -1) && (Build.VERSION.SDK_INT >= 23)) {
      return getUri((Icon)mObj1);
    }
    return Uri.parse((String)mObj1);
  }
  
  public Drawable loadDrawable(Context paramContext)
  {
    checkResource(paramContext);
    if (Build.VERSION.SDK_INT >= 23) {
      return toIcon().loadDrawable(paramContext);
    }
    paramContext = loadDrawableInner(paramContext);
    if ((paramContext != null) && ((mTintList != null) || (mTintMode != DEFAULT_TINT_MODE)))
    {
      paramContext.mutate();
      DrawableCompat.setTintList(paramContext, mTintList);
      DrawableCompat.setTintMode(paramContext, mTintMode);
    }
    return paramContext;
  }
  
  public void onPostParceling()
  {
    mTintMode = PorterDuff.Mode.valueOf(mTintModeStr);
    int i = mType;
    if (i != -1)
    {
      switch (i)
      {
      default: 
        return;
      case 3: 
        mObj1 = mData;
        return;
      case 2: 
      case 4: 
        mObj1 = new String(mData, Charset.forName("UTF-16"));
        return;
      }
      if (mParcelable != null)
      {
        mObj1 = mParcelable;
        return;
      }
      mObj1 = mData;
      mType = 3;
      mInt1 = 0;
      mInt2 = mData.length;
      return;
    }
    if (mParcelable != null)
    {
      mObj1 = mParcelable;
      return;
    }
    throw new IllegalArgumentException("Invalid icon");
  }
  
  public void onPreParceling(boolean paramBoolean)
  {
    mTintModeStr = mTintMode.name();
    int i = mType;
    if (i != -1)
    {
      switch (i)
      {
      default: 
        return;
      case 4: 
        mData = mObj1.toString().getBytes(Charset.forName("UTF-16"));
        return;
      case 3: 
        mData = ((byte[])mObj1);
        return;
      case 2: 
        mData = ((String)mObj1).getBytes(Charset.forName("UTF-16"));
        return;
      }
      if (paramBoolean)
      {
        Bitmap localBitmap = (Bitmap)mObj1;
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        localBitmap.compress(Bitmap.CompressFormat.PNG, 90, localByteArrayOutputStream);
        mData = localByteArrayOutputStream.toByteArray();
        return;
      }
      mParcelable = ((Parcelable)mObj1);
      return;
    }
    if (!paramBoolean)
    {
      mParcelable = ((Parcelable)mObj1);
      return;
    }
    throw new IllegalArgumentException("Can't serialize Icon created with IconCompat#createFromIcon");
  }
  
  public IconCompat setTint(int paramInt)
  {
    return setTintList(ColorStateList.valueOf(paramInt));
  }
  
  public IconCompat setTintList(ColorStateList paramColorStateList)
  {
    mTintList = paramColorStateList;
    return this;
  }
  
  public IconCompat setTintMode(PorterDuff.Mode paramMode)
  {
    mTintMode = paramMode;
    return this;
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    int i = mType;
    if (i != -1) {
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Invalid icon");
      case 3: 
        localBundle.putByteArray("obj", (byte[])mObj1);
        break;
      case 2: 
      case 4: 
        localBundle.putString("obj", (String)mObj1);
        break;
      case 1: 
      case 5: 
        localBundle.putParcelable("obj", (Bitmap)mObj1);
        break;
      }
    } else {
      localBundle.putParcelable("obj", (Parcelable)mObj1);
    }
    localBundle.putInt("type", mType);
    localBundle.putInt("int1", mInt1);
    localBundle.putInt("int2", mInt2);
    if (mTintList != null) {
      localBundle.putParcelable("tint_list", mTintList);
    }
    if (mTintMode != DEFAULT_TINT_MODE) {
      localBundle.putString("tint_mode", mTintMode.name());
    }
    return localBundle;
  }
  
  public Icon toIcon()
  {
    int i = mType;
    Icon localIcon2;
    if (i != -1)
    {
      Icon localIcon1;
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Unknown type");
      case 5: 
        if (Build.VERSION.SDK_INT >= 26) {
          localIcon1 = Icon.createWithAdaptiveBitmap((Bitmap)mObj1);
        } else {
          localIcon1 = Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon((Bitmap)mObj1, false));
        }
        break;
      case 4: 
        localIcon1 = Icon.createWithContentUri((String)mObj1);
        break;
      case 3: 
        localIcon1 = Icon.createWithData((byte[])mObj1, mInt1, mInt2);
        break;
      case 2: 
        localIcon1 = Icon.createWithResource(getResPackage(), mInt1);
        break;
      case 1: 
        localIcon1 = Icon.createWithBitmap((Bitmap)mObj1);
      }
      if (mTintList != null) {
        localIcon1.setTintList(mTintList);
      }
      localIcon2 = localIcon1;
      if (mTintMode != DEFAULT_TINT_MODE)
      {
        localIcon1.setTintMode(mTintMode);
        return localIcon1;
      }
    }
    else
    {
      localIcon2 = (Icon)mObj1;
    }
    return localIcon2;
  }
  
  public String toString()
  {
    if (mType == -1) {
      return String.valueOf(mObj1);
    }
    StringBuilder localStringBuilder = new StringBuilder("Icon(typ=");
    localStringBuilder.append(typeToString(mType));
    switch (mType)
    {
    default: 
      break;
    case 4: 
      localStringBuilder.append(" uri=");
      localStringBuilder.append(mObj1);
      break;
    case 3: 
      localStringBuilder.append(" len=");
      localStringBuilder.append(mInt1);
      if (mInt2 != 0)
      {
        localStringBuilder.append(" off=");
        localStringBuilder.append(mInt2);
      }
      break;
    case 2: 
      localStringBuilder.append(" pkg=");
      localStringBuilder.append(getResPackage());
      localStringBuilder.append(" id=");
      localStringBuilder.append(String.format("0x%08x", new Object[] { Integer.valueOf(getResId()) }));
      break;
    case 1: 
    case 5: 
      localStringBuilder.append(" size=");
      localStringBuilder.append(((Bitmap)mObj1).getWidth());
      localStringBuilder.append("x");
      localStringBuilder.append(((Bitmap)mObj1).getHeight());
    }
    if (mTintList != null)
    {
      localStringBuilder.append(" tint=");
      localStringBuilder.append(mTintList);
    }
    if (mTintMode != DEFAULT_TINT_MODE)
    {
      localStringBuilder.append(" mode=");
      localStringBuilder.append(mTintMode);
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public static @interface IconType {}
}
