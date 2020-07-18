package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import androidx.appcompat.resources.R.drawable;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.collection.LruCache;
import androidx.collection.SimpleArrayMap;
import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public final class ResourceManagerInternal
{
  private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
  private static final boolean DEBUG = false;
  private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
  private static ResourceManagerInternal INSTANCE;
  private static final String PAGE_KEY = "ResourceManagerInternal";
  private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
  private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
  private ArrayMap<String, InflateDelegate> mDelegates;
  private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable.ConstantState>>> mDrawableCaches = new WeakHashMap(0);
  private boolean mHasCheckedVectorDrawableSetup;
  private ResourceManagerHooks mHooks;
  private SparseArrayCompat<String> mKnownDrawableIdTags;
  private WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists;
  private TypedValue mTypedValue;
  
  public ResourceManagerInternal() {}
  
  private void addDelegate(String paramString, InflateDelegate paramInflateDelegate)
  {
    if (mDelegates == null) {
      mDelegates = new ArrayMap();
    }
    mDelegates.put(paramString, paramInflateDelegate);
  }
  
  private boolean addDrawableToCache(Context paramContext, long paramLong, Drawable paramDrawable)
  {
    try
    {
      Drawable.ConstantState localConstantState = paramDrawable.getConstantState();
      if (localConstantState != null)
      {
        LongSparseArray localLongSparseArray = (LongSparseArray)mDrawableCaches.get(paramContext);
        paramDrawable = localLongSparseArray;
        if (localLongSparseArray == null)
        {
          paramDrawable = new LongSparseArray();
          mDrawableCaches.put(paramContext, paramDrawable);
        }
        paramDrawable.put(paramLong, new WeakReference(localConstantState));
        return true;
      }
      return false;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  private void addTintListToCache(Context paramContext, int paramInt, ColorStateList paramColorStateList)
  {
    if (mTintLists == null) {
      mTintLists = new WeakHashMap();
    }
    SparseArrayCompat localSparseArrayCompat2 = (SparseArrayCompat)mTintLists.get(paramContext);
    SparseArrayCompat localSparseArrayCompat1 = localSparseArrayCompat2;
    if (localSparseArrayCompat2 == null)
    {
      localSparseArrayCompat1 = new SparseArrayCompat();
      mTintLists.put(paramContext, localSparseArrayCompat1);
    }
    localSparseArrayCompat1.append(paramInt, paramColorStateList);
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void checkVectorDrawableSetup(Context paramContext)
  {
    if (mHasCheckedVectorDrawableSetup) {
      return;
    }
    mHasCheckedVectorDrawableSetup = true;
    paramContext = getDrawable(paramContext, R.drawable.abc_vector_test);
    if ((paramContext != null) && (isVectorDrawable(paramContext))) {
      return;
    }
    mHasCheckedVectorDrawableSetup = false;
    throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
  }
  
  private static long createCacheKey(TypedValue paramTypedValue)
  {
    return assetCookie << 32 | data;
  }
  
  private Drawable createDrawableIfNeeded(Context paramContext, int paramInt)
  {
    if (mTypedValue == null) {
      mTypedValue = new TypedValue();
    }
    TypedValue localTypedValue = mTypedValue;
    paramContext.getResources().getValue(paramInt, localTypedValue, true);
    long l = createCacheKey(localTypedValue);
    Drawable localDrawable = getCachedDrawable(paramContext, l);
    if (localDrawable != null) {
      return localDrawable;
    }
    if (mHooks == null) {
      localDrawable = null;
    } else {
      localDrawable = mHooks.createDrawableFor(this, paramContext, paramInt);
    }
    if (localDrawable != null)
    {
      localDrawable.setChangingConfigurations(changingConfigurations);
      addDrawableToCache(paramContext, l, localDrawable);
    }
    return localDrawable;
  }
  
  private static PorterDuffColorFilter createTintFilter(ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int[] paramArrayOfInt)
  {
    if ((paramColorStateList != null) && (paramMode != null)) {
      return getPorterDuffColorFilter(paramColorStateList.getColorForState(paramArrayOfInt, 0), paramMode);
    }
    return null;
  }
  
  public static ResourceManagerInternal get()
  {
    try
    {
      if (INSTANCE == null)
      {
        INSTANCE = new ResourceManagerInternal();
        installDefaultInflateDelegates(INSTANCE);
      }
      ResourceManagerInternal localResourceManagerInternal = INSTANCE;
      return localResourceManagerInternal;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Drawable getCachedDrawable(Context paramContext, long paramLong)
  {
    try
    {
      LongSparseArray localLongSparseArray = (LongSparseArray)mDrawableCaches.get(paramContext);
      if (localLongSparseArray == null) {
        return null;
      }
      Object localObject = (WeakReference)localLongSparseArray.get(paramLong);
      if (localObject != null)
      {
        localObject = (Drawable.ConstantState)((WeakReference)localObject).get();
        if (localObject != null)
        {
          paramContext = ((Drawable.ConstantState)localObject).newDrawable(paramContext.getResources());
          return paramContext;
        }
        localLongSparseArray.delete(paramLong);
      }
      return null;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static PorterDuffColorFilter getPorterDuffColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    try
    {
      PorterDuffColorFilter localPorterDuffColorFilter2 = COLOR_FILTER_CACHE.get(paramInt, paramMode);
      PorterDuffColorFilter localPorterDuffColorFilter1 = localPorterDuffColorFilter2;
      if (localPorterDuffColorFilter2 == null)
      {
        localPorterDuffColorFilter1 = new PorterDuffColorFilter(paramInt, paramMode);
        COLOR_FILTER_CACHE.put(paramInt, paramMode, localPorterDuffColorFilter1);
      }
      return localPorterDuffColorFilter1;
    }
    catch (Throwable paramMode)
    {
      throw paramMode;
    }
  }
  
  private ColorStateList getTintListFromCache(Context paramContext, int paramInt)
  {
    if (mTintLists != null)
    {
      paramContext = (SparseArrayCompat)mTintLists.get(paramContext);
      if (paramContext != null) {
        return (ColorStateList)paramContext.get(paramInt);
      }
    }
    return null;
  }
  
  private static void installDefaultInflateDelegates(ResourceManagerInternal paramResourceManagerInternal)
  {
    if (Build.VERSION.SDK_INT < 24)
    {
      paramResourceManagerInternal.addDelegate("vector", new VdcInflateDelegate());
      paramResourceManagerInternal.addDelegate("animated-vector", new AvdcInflateDelegate());
      paramResourceManagerInternal.addDelegate("animated-selector", new AsldcInflateDelegate());
    }
  }
  
  private static boolean isVectorDrawable(Drawable paramDrawable)
  {
    return ((paramDrawable instanceof VectorDrawableCompat)) || ("android.graphics.drawable.VectorDrawable".equals(paramDrawable.getClass().getName()));
  }
  
  private Drawable loadDrawableFromDelegates(Context paramContext, int paramInt)
  {
    Object localObject3;
    if ((mDelegates != null) && (!mDelegates.isEmpty()))
    {
      if (mKnownDrawableIdTags != null)
      {
        localObject1 = (String)mKnownDrawableIdTags.get(paramInt);
        if ("appcompat_skip_skip".equals(localObject1)) {
          break label412;
        }
        if ((localObject1 != null) && (mDelegates.get(localObject1) == null)) {
          return null;
        }
      }
      else
      {
        mKnownDrawableIdTags = new SparseArrayCompat();
      }
      if (mTypedValue == null) {
        mTypedValue = new TypedValue();
      }
      TypedValue localTypedValue = mTypedValue;
      Object localObject4 = paramContext.getResources();
      ((Resources)localObject4).getValue(paramInt, localTypedValue, true);
      long l = createCacheKey(localTypedValue);
      Object localObject2 = getCachedDrawable(paramContext, l);
      Object localObject1 = localObject2;
      if (localObject2 != null) {
        return localObject2;
      }
      localObject3 = localObject1;
      if (string != null)
      {
        localObject3 = localObject1;
        if (string.toString().endsWith(".xml"))
        {
          localObject3 = localObject1;
          try
          {
            localObject4 = ((Resources)localObject4).getXml(paramInt);
            localObject3 = localObject1;
            AttributeSet localAttributeSet = Xml.asAttributeSet((XmlPullParser)localObject4);
            int i;
            do
            {
              localObject3 = localObject1;
              i = ((XmlPullParser)localObject4).next();
            } while ((i != 2) && (i != 1));
            if (i == 2)
            {
              localObject3 = localObject1;
              localObject2 = ((XmlPullParser)localObject4).getName();
              Object localObject5 = mKnownDrawableIdTags;
              localObject3 = localObject1;
              ((SparseArrayCompat)localObject5).append(paramInt, localObject2);
              localObject5 = mDelegates;
              localObject3 = localObject1;
              localObject2 = ((SimpleArrayMap)localObject5).get(localObject2);
              localObject5 = (InflateDelegate)localObject2;
              localObject2 = localObject1;
              if (localObject5 != null)
              {
                localObject3 = localObject1;
                localObject2 = ((InflateDelegate)localObject5).createFromXmlInner(paramContext, (XmlPullParser)localObject4, localAttributeSet, paramContext.getTheme());
              }
              localObject3 = localObject2;
              if (localObject2 != null)
              {
                i = changingConfigurations;
                localObject3 = localObject2;
                ((Drawable)localObject2).setChangingConfigurations(i);
                localObject3 = localObject2;
                addDrawableToCache(paramContext, l, (Drawable)localObject2);
                localObject3 = localObject2;
              }
            }
            else
            {
              localObject3 = localObject1;
              paramContext = new XmlPullParserException("No start tag found");
              throw paramContext;
            }
          }
          catch (Exception paramContext)
          {
            Log.e("ResourceManagerInternal", "Exception while inflating drawable", paramContext);
          }
        }
      }
      if (localObject3 != null) {
        break label414;
      }
      mKnownDrawableIdTags.append(paramInt, "appcompat_skip_skip");
      return localObject3;
    }
    label412:
    return null;
    label414:
    return localObject3;
  }
  
  private void removeDelegate(String paramString, InflateDelegate paramInflateDelegate)
  {
    if ((mDelegates != null) && (mDelegates.get(paramString) == paramInflateDelegate)) {
      mDelegates.remove(paramString);
    }
  }
  
  private Drawable tintDrawable(Context paramContext, int paramInt, boolean paramBoolean, Drawable paramDrawable)
  {
    Object localObject = getTintList(paramContext, paramInt);
    if (localObject != null)
    {
      paramContext = paramDrawable;
      if (DrawableUtils.canSafelyMutateDrawable(paramDrawable)) {
        paramContext = paramDrawable.mutate();
      }
      paramContext = DrawableCompat.wrap(paramContext);
      DrawableCompat.setTintList(paramContext, (ColorStateList)localObject);
      paramDrawable = getTintMode(paramInt);
      localObject = paramContext;
      if (paramDrawable != null)
      {
        DrawableCompat.setTintMode(paramContext, paramDrawable);
        return paramContext;
      }
    }
    else
    {
      if ((mHooks != null) && (mHooks.tintDrawable(paramContext, paramInt, paramDrawable))) {
        return paramDrawable;
      }
      localObject = paramDrawable;
      if (!tintDrawableUsingColorFilter(paramContext, paramInt, paramDrawable))
      {
        localObject = paramDrawable;
        if (paramBoolean) {
          return null;
        }
      }
    }
    return localObject;
  }
  
  static void tintDrawable(Drawable paramDrawable, TintInfo paramTintInfo, int[] paramArrayOfInt)
  {
    if ((DrawableUtils.canSafelyMutateDrawable(paramDrawable)) && (paramDrawable.mutate() != paramDrawable))
    {
      Log.d("ResourceManagerInternal", "Mutated drawable is not the same instance as the input.");
      return;
    }
    if ((!mHasTintList) && (!mHasTintMode))
    {
      paramDrawable.clearColorFilter();
    }
    else
    {
      ColorStateList localColorStateList;
      if (mHasTintList) {
        localColorStateList = mTintList;
      } else {
        localColorStateList = null;
      }
      if (mHasTintMode) {
        paramTintInfo = mTintMode;
      } else {
        paramTintInfo = DEFAULT_MODE;
      }
      paramDrawable.setColorFilter(createTintFilter(localColorStateList, paramTintInfo, paramArrayOfInt));
    }
    if (Build.VERSION.SDK_INT <= 23) {
      paramDrawable.invalidateSelf();
    }
  }
  
  public Drawable getDrawable(Context paramContext, int paramInt)
  {
    try
    {
      paramContext = getDrawable(paramContext, paramInt, false);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  Drawable getDrawable(Context paramContext, int paramInt, boolean paramBoolean)
  {
    try
    {
      checkVectorDrawableSetup(paramContext);
      Object localObject2 = loadDrawableFromDelegates(paramContext, paramInt);
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = createDrawableIfNeeded(paramContext, paramInt);
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = ContextCompat.getDrawable(paramContext, paramInt);
      }
      localObject1 = localObject2;
      if (localObject2 != null) {
        localObject1 = tintDrawable(paramContext, paramInt, paramBoolean, (Drawable)localObject2);
      }
      if (localObject1 != null) {
        DrawableUtils.fixDrawable((Drawable)localObject1);
      }
      return localObject1;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  ColorStateList getTintList(Context paramContext, int paramInt)
  {
    try
    {
      ColorStateList localColorStateList1 = getTintListFromCache(paramContext, paramInt);
      ColorStateList localColorStateList2 = localColorStateList1;
      if (localColorStateList1 == null)
      {
        if (mHooks == null) {
          localColorStateList1 = null;
        } else {
          localColorStateList1 = mHooks.getTintListForDrawableRes(paramContext, paramInt);
        }
        localColorStateList2 = localColorStateList1;
        if (localColorStateList1 != null)
        {
          addTintListToCache(paramContext, paramInt, localColorStateList1);
          localColorStateList2 = localColorStateList1;
        }
      }
      return localColorStateList2;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  PorterDuff.Mode getTintMode(int paramInt)
  {
    if (mHooks == null) {
      return null;
    }
    return mHooks.getTintModeForDrawableRes(paramInt);
  }
  
  public void onConfigurationChanged(Context paramContext)
  {
    try
    {
      paramContext = (LongSparseArray)mDrawableCaches.get(paramContext);
      if (paramContext != null) {
        paramContext.clear();
      }
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  Drawable onDrawableLoadedFromResources(Context paramContext, VectorEnabledTintResources paramVectorEnabledTintResources, int paramInt)
  {
    try
    {
      Drawable localDrawable2 = loadDrawableFromDelegates(paramContext, paramInt);
      Drawable localDrawable1 = localDrawable2;
      if (localDrawable2 == null) {
        localDrawable1 = paramVectorEnabledTintResources.superGetDrawable(paramInt);
      }
      if (localDrawable1 != null)
      {
        paramContext = tintDrawable(paramContext, paramInt, false, localDrawable1);
        return paramContext;
      }
      return null;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public void setHooks(ResourceManagerHooks paramResourceManagerHooks)
  {
    try
    {
      mHooks = paramResourceManagerHooks;
      return;
    }
    catch (Throwable paramResourceManagerHooks)
    {
      throw paramResourceManagerHooks;
    }
  }
  
  boolean tintDrawableUsingColorFilter(Context paramContext, int paramInt, Drawable paramDrawable)
  {
    return (mHooks != null) && (mHooks.tintDrawableUsingColorFilter(paramContext, paramInt, paramDrawable));
  }
  
  @RequiresApi(11)
  static class AsldcInflateDelegate
    implements ResourceManagerInternal.InflateDelegate
  {
    AsldcInflateDelegate() {}
    
    public Drawable createFromXmlInner(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      try
      {
        paramContext = AnimatedStateListDrawableCompat.createFromXmlInner(paramContext, paramContext.getResources(), paramXmlPullParser, paramAttributeSet, paramTheme);
        return paramContext;
      }
      catch (Exception paramContext)
      {
        Log.e("AsldcInflateDelegate", "Exception while inflating <animated-selector>", paramContext);
      }
      return null;
    }
  }
  
  private static class AvdcInflateDelegate
    implements ResourceManagerInternal.InflateDelegate
  {
    AvdcInflateDelegate() {}
    
    public Drawable createFromXmlInner(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      try
      {
        paramContext = AnimatedVectorDrawableCompat.createFromXmlInner(paramContext, paramContext.getResources(), paramXmlPullParser, paramAttributeSet, paramTheme);
        return paramContext;
      }
      catch (Exception paramContext)
      {
        Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", paramContext);
      }
      return null;
    }
  }
  
  private static class ColorFilterLruCache
    extends LruCache<Integer, PorterDuffColorFilter>
  {
    public ColorFilterLruCache(int paramInt)
    {
      super();
    }
    
    private static int generateCacheKey(int paramInt, PorterDuff.Mode paramMode)
    {
      return (paramInt + 31) * 31 + paramMode.hashCode();
    }
    
    PorterDuffColorFilter get(int paramInt, PorterDuff.Mode paramMode)
    {
      return (PorterDuffColorFilter)get(Integer.valueOf(generateCacheKey(paramInt, paramMode)));
    }
    
    PorterDuffColorFilter put(int paramInt, PorterDuff.Mode paramMode, PorterDuffColorFilter paramPorterDuffColorFilter)
    {
      return (PorterDuffColorFilter)put(Integer.valueOf(generateCacheKey(paramInt, paramMode)), paramPorterDuffColorFilter);
    }
  }
  
  private static abstract interface InflateDelegate
  {
    public abstract Drawable createFromXmlInner(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme);
  }
  
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  static abstract interface ResourceManagerHooks
  {
    public abstract Drawable createDrawableFor(ResourceManagerInternal paramResourceManagerInternal, Context paramContext, int paramInt);
    
    public abstract ColorStateList getTintListForDrawableRes(Context paramContext, int paramInt);
    
    public abstract PorterDuff.Mode getTintModeForDrawableRes(int paramInt);
    
    public abstract boolean tintDrawable(Context paramContext, int paramInt, Drawable paramDrawable);
    
    public abstract boolean tintDrawableUsingColorFilter(Context paramContext, int paramInt, Drawable paramDrawable);
  }
  
  private static class VdcInflateDelegate
    implements ResourceManagerInternal.InflateDelegate
  {
    VdcInflateDelegate() {}
    
    public Drawable createFromXmlInner(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      try
      {
        paramContext = VectorDrawableCompat.createFromXmlInner(paramContext.getResources(), paramXmlPullParser, paramAttributeSet, paramTheme);
        return paramContext;
      }
      catch (Exception paramContext)
      {
        Log.e("VdcInflateDelegate", "Exception while inflating <vector>", paramContext);
      }
      return null;
    }
  }
}
