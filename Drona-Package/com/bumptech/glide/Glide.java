package com.bumptech.glide;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import androidx.fragment.package_5.FragmentActivity;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.InputStreamRewinder.Factory;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.engine.prefill.PreFillType.Builder;
import com.bumptech.glide.load.model.AssetUriLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.AssetUriLoader.StreamFactory;
import com.bumptech.glide.load.model.ByteArrayLoader.ByteBufferFactory;
import com.bumptech.glide.load.model.ByteArrayLoader.StreamFactory;
import com.bumptech.glide.load.model.ByteBufferEncoder;
import com.bumptech.glide.load.model.ByteBufferFileLoader.Factory;
import com.bumptech.glide.load.model.DataUrlLoader.StreamFactory;
import com.bumptech.glide.load.model.FileLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.FileLoader.StreamFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.MediaStoreFileLoader.Factory;
import com.bumptech.glide.load.model.ResourceLoader.AssetFileDescriptorFactory;
import com.bumptech.glide.load.model.ResourceLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.ResourceLoader.StreamFactory;
import com.bumptech.glide.load.model.ResourceLoader.UriFactory;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.StringLoader.AssetFileDescriptorFactory;
import com.bumptech.glide.load.model.StringLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.StringLoader.StreamFactory;
import com.bumptech.glide.load.model.UnitModelLoader.Factory;
import com.bumptech.glide.load.model.UriLoader.AssetFileDescriptorFactory;
import com.bumptech.glide.load.model.UriLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.UriLoader.StreamFactory;
import com.bumptech.glide.load.model.UrlUriLoader.StreamFactory;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Factory;
import com.bumptech.glide.load.model.stream.HttpUriLoader.Factory;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader.Factory;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader.Factory;
import com.bumptech.glide.load.model.stream.UrlLoader.StreamFactory;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.ResourceBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.UnitBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder.Factory;
import com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder;
import com.bumptech.glide.load.resource.drawable.UnitDrawableDecoder;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.DrawableBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.manager.RequestManagerRetriever.RequestManagerFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.module.AppliesOptions;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.module.RegistersComponents;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Glide
  implements ComponentCallbacks2
{
  private static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
  private static final String TAG = "Glide";
  private static volatile Glide glide;
  private static volatile boolean isInitializing;
  private final ArrayPool arrayPool;
  private final BitmapPool bitmapPool;
  private final BitmapPreFiller bitmapPreFiller;
  private final ConnectivityMonitorFactory connectivityMonitorFactory;
  private final Engine engine;
  private final GlideContext glideContext;
  private final List<RequestManager> managers = new ArrayList();
  private final MemoryCache memoryCache;
  private MemoryCategory memoryCategory = MemoryCategory.NORMAL;
  private final Registry registry;
  private final RequestManagerRetriever requestManagerRetriever;
  
  Glide(Context paramContext, Engine paramEngine, MemoryCache paramMemoryCache, BitmapPool paramBitmapPool, ArrayPool paramArrayPool, RequestManagerRetriever paramRequestManagerRetriever, ConnectivityMonitorFactory paramConnectivityMonitorFactory, int paramInt, RequestOptions paramRequestOptions, Map paramMap, List paramList, boolean paramBoolean)
  {
    engine = paramEngine;
    bitmapPool = paramBitmapPool;
    arrayPool = paramArrayPool;
    memoryCache = paramMemoryCache;
    requestManagerRetriever = paramRequestManagerRetriever;
    connectivityMonitorFactory = paramConnectivityMonitorFactory;
    bitmapPreFiller = new BitmapPreFiller(paramMemoryCache, paramBitmapPool, (DecodeFormat)paramRequestOptions.getOptions().getOption(Downsampler.DECODE_FORMAT));
    paramMemoryCache = paramContext.getResources();
    registry = new Registry();
    registry.register(new DefaultImageHeaderParser());
    if (Build.VERSION.SDK_INT >= 27) {
      registry.register(new ExifInterfaceImageHeaderParser());
    }
    paramRequestManagerRetriever = registry.getImageHeaderParsers();
    Object localObject = new Downsampler(paramRequestManagerRetriever, paramMemoryCache.getDisplayMetrics(), paramBitmapPool, paramArrayPool);
    paramConnectivityMonitorFactory = new ByteBufferGifDecoder(paramContext, paramRequestManagerRetriever, paramBitmapPool, paramArrayPool);
    ResourceDecoder localResourceDecoder = VideoDecoder.parcel(paramBitmapPool);
    ByteBufferBitmapDecoder localByteBufferBitmapDecoder = new ByteBufferBitmapDecoder((Downsampler)localObject);
    localObject = new StreamBitmapDecoder((Downsampler)localObject, paramArrayPool);
    ResourceDrawableDecoder localResourceDrawableDecoder = new ResourceDrawableDecoder(paramContext);
    ResourceLoader.StreamFactory localStreamFactory = new ResourceLoader.StreamFactory(paramMemoryCache);
    ResourceLoader.UriFactory localUriFactory = new ResourceLoader.UriFactory(paramMemoryCache);
    ResourceLoader.FileDescriptorFactory localFileDescriptorFactory = new ResourceLoader.FileDescriptorFactory(paramMemoryCache);
    ResourceLoader.AssetFileDescriptorFactory localAssetFileDescriptorFactory = new ResourceLoader.AssetFileDescriptorFactory(paramMemoryCache);
    BitmapEncoder localBitmapEncoder = new BitmapEncoder(paramArrayPool);
    BitmapBytesTranscoder localBitmapBytesTranscoder = new BitmapBytesTranscoder();
    GifDrawableBytesTranscoder localGifDrawableBytesTranscoder = new GifDrawableBytesTranscoder();
    ContentResolver localContentResolver = paramContext.getContentResolver();
    registry.append(ByteBuffer.class, new ByteBufferEncoder()).append(InputStream.class, new StreamEncoder(paramArrayPool)).append("Bitmap", ByteBuffer.class, Bitmap.class, localByteBufferBitmapDecoder).append("Bitmap", InputStream.class, Bitmap.class, (ResourceDecoder)localObject).append("Bitmap", ParcelFileDescriptor.class, Bitmap.class, localResourceDecoder).append("Bitmap", AssetFileDescriptor.class, Bitmap.class, VideoDecoder.asset(paramBitmapPool)).append(Bitmap.class, Bitmap.class, UnitModelLoader.Factory.getInstance()).append("Bitmap", Bitmap.class, Bitmap.class, new UnitBitmapDecoder()).append(Bitmap.class, localBitmapEncoder).append("BitmapDrawable", ByteBuffer.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, localByteBufferBitmapDecoder)).append("BitmapDrawable", InputStream.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, (ResourceDecoder)localObject)).append("BitmapDrawable", ParcelFileDescriptor.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, localResourceDecoder)).append(BitmapDrawable.class, new BitmapDrawableEncoder(paramBitmapPool, localBitmapEncoder)).append("Gif", InputStream.class, GifDrawable.class, new StreamGifDecoder(paramRequestManagerRetriever, paramConnectivityMonitorFactory, paramArrayPool)).append("Gif", ByteBuffer.class, GifDrawable.class, paramConnectivityMonitorFactory).append(GifDrawable.class, new GifDrawableEncoder()).append(GifDecoder.class, GifDecoder.class, UnitModelLoader.Factory.getInstance()).append("Bitmap", GifDecoder.class, Bitmap.class, new GifFrameResourceDecoder(paramBitmapPool)).append(Uri.class, Drawable.class, localResourceDrawableDecoder).append(Uri.class, Bitmap.class, new ResourceBitmapDecoder(localResourceDrawableDecoder, paramBitmapPool)).register(new ByteBufferRewinder.Factory()).append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory()).append(File.class, InputStream.class, new FileLoader.StreamFactory()).append(File.class, File.class, new FileDecoder()).append(File.class, ParcelFileDescriptor.class, new FileLoader.FileDescriptorFactory()).append(File.class, File.class, UnitModelLoader.Factory.getInstance()).register(new InputStreamRewinder.Factory(paramArrayPool)).append(Integer.TYPE, InputStream.class, localStreamFactory).append(Integer.TYPE, ParcelFileDescriptor.class, localFileDescriptorFactory).append(Integer.class, InputStream.class, localStreamFactory).append(Integer.class, ParcelFileDescriptor.class, localFileDescriptorFactory).append(Integer.class, Uri.class, localUriFactory).append(Integer.TYPE, AssetFileDescriptor.class, localAssetFileDescriptorFactory).append(Integer.class, AssetFileDescriptor.class, localAssetFileDescriptorFactory).append(Integer.TYPE, Uri.class, localUriFactory).append(String.class, InputStream.class, new DataUrlLoader.StreamFactory()).append(Uri.class, InputStream.class, new DataUrlLoader.StreamFactory()).append(String.class, InputStream.class, new StringLoader.StreamFactory()).append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory()).append(String.class, AssetFileDescriptor.class, new StringLoader.AssetFileDescriptorFactory()).append(Uri.class, InputStream.class, new HttpUriLoader.Factory()).append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory(paramContext.getAssets())).append(Uri.class, ParcelFileDescriptor.class, new AssetUriLoader.FileDescriptorFactory(paramContext.getAssets())).append(Uri.class, InputStream.class, new MediaStoreImageThumbLoader.Factory(paramContext)).append(Uri.class, InputStream.class, new MediaStoreVideoThumbLoader.Factory(paramContext)).append(Uri.class, InputStream.class, new UriLoader.StreamFactory(localContentResolver)).append(Uri.class, ParcelFileDescriptor.class, new UriLoader.FileDescriptorFactory(localContentResolver)).append(Uri.class, AssetFileDescriptor.class, new UriLoader.AssetFileDescriptorFactory(localContentResolver)).append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory()).append(URL.class, InputStream.class, new UrlLoader.StreamFactory()).append(Uri.class, File.class, new MediaStoreFileLoader.Factory(paramContext)).append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory()).append([B.class, ByteBuffer.class, new ByteArrayLoader.ByteBufferFactory()).append([B.class, InputStream.class, new ByteArrayLoader.StreamFactory()).append(Uri.class, Uri.class, UnitModelLoader.Factory.getInstance()).append(Drawable.class, Drawable.class, UnitModelLoader.Factory.getInstance()).append(Drawable.class, Drawable.class, new UnitDrawableDecoder()).register(Bitmap.class, BitmapDrawable.class, new BitmapDrawableTranscoder(paramMemoryCache)).register(Bitmap.class, [B.class, localBitmapBytesTranscoder).register(Drawable.class, [B.class, new DrawableBytesTranscoder(paramBitmapPool, localBitmapBytesTranscoder, localGifDrawableBytesTranscoder)).register(GifDrawable.class, [B.class, localGifDrawableBytesTranscoder);
    paramMemoryCache = new ImageViewTargetFactory();
    glideContext = new GlideContext(paramContext, paramArrayPool, registry, paramMemoryCache, paramRequestOptions, paramMap, paramList, paramEngine, paramBoolean, paramInt);
  }
  
  private static void checkAndInitializeGlide(Context paramContext)
  {
    if (!isInitializing)
    {
      isInitializing = true;
      initializeGlide(paramContext);
      isInitializing = false;
      return;
    }
    throw new IllegalStateException("You cannot call Glide.get() in registerComponents(), use the provided Glide instance instead");
  }
  
  public static Glide get(Context paramContext)
  {
    if (glide == null) {
      try
      {
        if (glide == null) {
          checkAndInitializeGlide(paramContext);
        }
      }
      catch (Throwable paramContext)
      {
        throw paramContext;
      }
    }
    return glide;
  }
  
  private static GeneratedAppGlideModule getAnnotationGeneratedGlideModules()
  {
    try
    {
      try
      {
        Object localObject = Class.forName("com.bumptech.glide.GeneratedAppGlideModuleImpl");
        localObject = ((Class)localObject).getDeclaredConstructor(new Class[0]);
        localObject = ((Constructor)localObject).newInstance(new Object[0]);
        return (GeneratedAppGlideModule)localObject;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throwIncorrectGlideModule(localInvocationTargetException);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throwIncorrectGlideModule(localNoSuchMethodException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throwIncorrectGlideModule(localIllegalAccessException);
      }
      catch (InstantiationException localInstantiationException)
      {
        throwIncorrectGlideModule(localInstantiationException);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;) {}
    }
    if (Log.isLoggable("Glide", 5)) {
      Log.w("Glide", "Failed to find GeneratedAppGlideModule. You should include an annotationProcessor compile dependency on com.github.bumptech.glide:compiler in your application and a @GlideModule annotated AppGlideModule implementation or LibraryGlideModules will be silently ignored");
    }
    return null;
  }
  
  public static File getPhotoCacheDir(Context paramContext)
  {
    return getPhotoCacheDir(paramContext, "image_manager_disk_cache");
  }
  
  public static File getPhotoCacheDir(Context paramContext, String paramString)
  {
    paramContext = paramContext.getCacheDir();
    if (paramContext != null)
    {
      paramContext = new File(paramContext, paramString);
      if (!paramContext.mkdirs())
      {
        if (paramContext.exists())
        {
          if (paramContext.isDirectory()) {
            break label65;
          }
          return null;
        }
      }
      else {
        return paramContext;
      }
    }
    else
    {
      if (!Log.isLoggable("Glide", 6)) {
        break label67;
      }
      Log.e("Glide", "default disk cache dir is null");
    }
    return null;
    label65:
    return paramContext;
    label67:
    return null;
  }
  
  private static RequestManagerRetriever getRetriever(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext, "You cannot start a load on a not yet attached View or a Fragment where getActivity() returns null (which usually occurs when getActivity() is called before the Fragment is attached or after the Fragment is destroyed).");
    return get(paramContext).getRequestManagerRetriever();
  }
  
  public static void init(Context paramContext, GlideBuilder paramGlideBuilder)
  {
    try
    {
      if (glide != null) {
        tearDown();
      }
      initializeGlide(paramContext, paramGlideBuilder);
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static void init(Glide paramGlide)
  {
    try
    {
      if (glide != null) {
        tearDown();
      }
      glide = paramGlide;
      return;
    }
    catch (Throwable paramGlide)
    {
      throw paramGlide;
    }
  }
  
  private static void initializeGlide(Context paramContext)
  {
    initializeGlide(paramContext, new GlideBuilder());
  }
  
  private static void initializeGlide(Context paramContext, GlideBuilder paramGlideBuilder)
  {
    Context localContext = paramContext.getApplicationContext();
    GeneratedAppGlideModule localGeneratedAppGlideModule = getAnnotationGeneratedGlideModules();
    paramContext = Collections.emptyList();
    if ((localGeneratedAppGlideModule == null) || (localGeneratedAppGlideModule.isManifestParsingEnabled())) {
      paramContext = new ManifestParser(localContext).parse();
    }
    Object localObject2;
    Object localObject3;
    if ((localGeneratedAppGlideModule != null) && (!localGeneratedAppGlideModule.getExcludedModuleClasses().isEmpty()))
    {
      localObject1 = localGeneratedAppGlideModule.getExcludedModuleClasses();
      localObject2 = paramContext.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (GlideModule)((Iterator)localObject2).next();
        if (((Set)localObject1).contains(localObject3.getClass()))
        {
          if (Log.isLoggable("Glide", 3))
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("AppGlideModule excludes manifest GlideModule: ");
            localStringBuilder.append(localObject3);
            Log.d("Glide", localStringBuilder.toString());
          }
          ((Iterator)localObject2).remove();
        }
      }
    }
    if (Log.isLoggable("Glide", 3))
    {
      localObject1 = paramContext.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (GlideModule)((Iterator)localObject1).next();
        localObject3 = new StringBuilder();
        ((StringBuilder)localObject3).append("Discovered GlideModule from manifest: ");
        ((StringBuilder)localObject3).append(localObject2.getClass());
        Log.d("Glide", ((StringBuilder)localObject3).toString());
      }
    }
    if (localGeneratedAppGlideModule != null) {
      localObject1 = localGeneratedAppGlideModule.getRequestManagerFactory();
    } else {
      localObject1 = null;
    }
    paramGlideBuilder.setRequestManagerFactory((RequestManagerRetriever.RequestManagerFactory)localObject1);
    Object localObject1 = paramContext.iterator();
    while (((Iterator)localObject1).hasNext()) {
      ((GlideModule)((Iterator)localObject1).next()).applyOptions(localContext, paramGlideBuilder);
    }
    if (localGeneratedAppGlideModule != null) {
      localGeneratedAppGlideModule.applyOptions(localContext, paramGlideBuilder);
    }
    paramGlideBuilder = paramGlideBuilder.build(localContext);
    paramContext = paramContext.iterator();
    while (paramContext.hasNext()) {
      ((GlideModule)paramContext.next()).registerComponents(localContext, paramGlideBuilder, registry);
    }
    if (localGeneratedAppGlideModule != null) {
      localGeneratedAppGlideModule.registerComponents(localContext, paramGlideBuilder, registry);
    }
    localContext.registerComponentCallbacks(paramGlideBuilder);
    glide = paramGlideBuilder;
  }
  
  public static void tearDown()
  {
    try
    {
      if (glide != null)
      {
        glide.getContext().getApplicationContext().unregisterComponentCallbacks(glide);
        glideengine.shutdown();
      }
      glide = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static void throwIncorrectGlideModule(Exception paramException)
  {
    throw new IllegalStateException("GeneratedAppGlideModuleImpl is implemented incorrectly. If you've manually implemented this class, remove your implementation. The Annotation processor will generate a correct implementation.", paramException);
  }
  
  public static RequestManager with(Activity paramActivity)
  {
    return getRetriever(paramActivity).get(paramActivity);
  }
  
  public static RequestManager with(android.app.Fragment paramFragment)
  {
    return getRetriever(paramFragment.getActivity()).get(paramFragment);
  }
  
  public static RequestManager with(Context paramContext)
  {
    return getRetriever(paramContext).get(paramContext);
  }
  
  public static RequestManager with(View paramView)
  {
    return getRetriever(paramView.getContext()).get(paramView);
  }
  
  public static RequestManager with(androidx.fragment.package_5.Fragment paramFragment)
  {
    return getRetriever(paramFragment.getActivity()).get(paramFragment);
  }
  
  public static RequestManager with(FragmentActivity paramFragmentActivity)
  {
    return getRetriever(paramFragmentActivity).get(paramFragmentActivity);
  }
  
  public void clearDiskCache()
  {
    Util.assertBackgroundThread();
    engine.clearDiskCache();
  }
  
  public void clearMemory()
  {
    Util.assertMainThread();
    memoryCache.clearMemory();
    bitmapPool.clearMemory();
    arrayPool.clearMemory();
  }
  
  public ArrayPool getArrayPool()
  {
    return arrayPool;
  }
  
  public BitmapPool getBitmapPool()
  {
    return bitmapPool;
  }
  
  ConnectivityMonitorFactory getConnectivityMonitorFactory()
  {
    return connectivityMonitorFactory;
  }
  
  public Context getContext()
  {
    return glideContext.getBaseContext();
  }
  
  GlideContext getGlideContext()
  {
    return glideContext;
  }
  
  public Registry getRegistry()
  {
    return registry;
  }
  
  public RequestManagerRetriever getRequestManagerRetriever()
  {
    return requestManagerRetriever;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory()
  {
    clearMemory();
  }
  
  public void onTrimMemory(int paramInt)
  {
    trimMemory(paramInt);
  }
  
  public void preFillBitmapPool(PreFillType.Builder... paramVarArgs)
  {
    bitmapPreFiller.preFill(paramVarArgs);
  }
  
  void registerRequestManager(RequestManager paramRequestManager)
  {
    List localList = managers;
    try
    {
      if (!managers.contains(paramRequestManager))
      {
        managers.add(paramRequestManager);
        return;
      }
      throw new IllegalStateException("Cannot register already registered manager");
    }
    catch (Throwable paramRequestManager)
    {
      throw paramRequestManager;
    }
  }
  
  boolean removeFromManagers(Target paramTarget)
  {
    List localList = managers;
    try
    {
      Iterator localIterator = managers.iterator();
      while (localIterator.hasNext()) {
        if (((RequestManager)localIterator.next()).untrack(paramTarget)) {
          return true;
        }
      }
      return false;
    }
    catch (Throwable paramTarget)
    {
      throw paramTarget;
    }
  }
  
  public MemoryCategory setMemoryCategory(MemoryCategory paramMemoryCategory)
  {
    Util.assertMainThread();
    memoryCache.setSizeMultiplier(paramMemoryCategory.getMultiplier());
    bitmapPool.setSizeMultiplier(paramMemoryCategory.getMultiplier());
    MemoryCategory localMemoryCategory = memoryCategory;
    memoryCategory = paramMemoryCategory;
    return localMemoryCategory;
  }
  
  public void trimMemory(int paramInt)
  {
    Util.assertMainThread();
    memoryCache.trimMemory(paramInt);
    bitmapPool.trimMemory(paramInt);
    arrayPool.trimMemory(paramInt);
  }
  
  void unregisterRequestManager(RequestManager paramRequestManager)
  {
    List localList = managers;
    try
    {
      if (managers.contains(paramRequestManager))
      {
        managers.remove(paramRequestManager);
        return;
      }
      throw new IllegalStateException("Cannot unregister not yet registered manager");
    }
    catch (Throwable paramRequestManager)
    {
      throw paramRequestManager;
    }
  }
}
