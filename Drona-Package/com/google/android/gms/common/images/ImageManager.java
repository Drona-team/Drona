package com.google.android.gms.common.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import androidx.collection.LruCache;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.internal.base.zak;
import com.google.android.gms.internal.base.zap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager
{
  private static final Object zamh = new Object();
  private static HashSet<Uri> zami = new HashSet();
  private static ImageManager zamj;
  private final Context mContext;
  private final Handler mHandler;
  private final ExecutorService zamk;
  private final zaa zaml;
  private final zak zamm;
  private final Map<zaa, ImageReceiver> zamn;
  private final Map<Uri, ImageReceiver> zamo;
  private final Map<Uri, Long> zamp;
  
  private ImageManager(Context paramContext, boolean paramBoolean)
  {
    mContext = paramContext.getApplicationContext();
    mHandler = ((Handler)new zap(Looper.getMainLooper()));
    zamk = Executors.newFixedThreadPool(4);
    zaml = null;
    zamm = new zak();
    zamn = new HashMap();
    zamo = new HashMap();
    zamp = new HashMap();
  }
  
  public static ImageManager create(Context paramContext)
  {
    if (zamj == null) {
      zamj = new ImageManager(paramContext, false);
    }
    return zamj;
  }
  
  private final void flush(Layer paramLayer)
  {
    Asserts.checkMainThread("ImageManager.loadImage() must be called in the main thread");
    new zac(paramLayer).run();
  }
  
  private final Bitmap get(Response paramResponse)
  {
    if (zaml == null) {
      return null;
    }
    return (Bitmap)zaml.get(paramResponse);
  }
  
  public final void loadImage(ImageView paramImageView, int paramInt)
  {
    flush(new Image(paramImageView, paramInt));
  }
  
  public final void loadImage(ImageView paramImageView, Uri paramUri)
  {
    flush(new Image(paramImageView, paramUri));
  }
  
  public final void loadImage(ImageView paramImageView, Uri paramUri, int paramInt)
  {
    paramImageView = new Image(paramImageView, paramUri);
    zamx = paramInt;
    flush(paramImageView);
  }
  
  public final void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
  {
    flush(new Game(paramOnImageLoadedListener, paramUri));
  }
  
  public final void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri, int paramInt)
  {
    paramOnImageLoadedListener = new Game(paramOnImageLoadedListener, paramUri);
    zamx = paramInt;
    flush(paramOnImageLoadedListener);
  }
  
  @KeepName
  private final class ImageReceiver
    extends ResultReceiver
  {
    private final Uri mUri;
    private final ArrayList<zaa> zamq;
    
    ImageReceiver(Uri paramUri)
    {
      super();
      mUri = paramUri;
      zamq = new ArrayList();
    }
    
    public final void Refresh(Layer paramLayer)
    {
      Asserts.checkMainThread("ImageReceiver.addImageRequest() must be called in the main thread");
      zamq.add(paramLayer);
    }
    
    public final void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      paramBundle = (ParcelFileDescriptor)paramBundle.getParcelable("com.google.android.gms.extra.fileDescriptor");
      ImageManager.getExecutor(ImageManager.this).execute(new ImageManager.zab(ImageManager.this, mUri, paramBundle));
    }
    
    public final void remove(Layer paramLayer)
    {
      Asserts.checkMainThread("ImageReceiver.removeImageRequest() must be called in the main thread");
      zamq.remove(paramLayer);
    }
    
    public final void zace()
    {
      Intent localIntent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
      localIntent.putExtra("com.google.android.gms.extras.uri", mUri);
      localIntent.putExtra("com.google.android.gms.extras.resultReceiver", this);
      localIntent.putExtra("com.google.android.gms.extras.priority", 3);
      ImageManager.getContext(ImageManager.this).sendBroadcast(localIntent);
    }
  }
  
  public static abstract interface OnImageLoadedListener
  {
    public abstract void onImageLoaded(Uri paramUri, Drawable paramDrawable, boolean paramBoolean);
  }
  
  private static final class zaa
    extends LruCache<zab, Bitmap>
  {}
  
  private final class zab
    implements Runnable
  {
    private final Uri mUri;
    private final ParcelFileDescriptor zams;
    
    public zab(Uri paramUri, ParcelFileDescriptor paramParcelFileDescriptor)
    {
      mUri = paramUri;
      zams = paramParcelFileDescriptor;
    }
    
    public final void run()
    {
      Asserts.checkNotMainThread("LoadBitmapFromDiskRunnable can't be executed in the main thread");
      Object localObject2 = zams;
      boolean bool = false;
      Object localObject1 = null;
      if (localObject2 != null)
      {
        localObject2 = zams;
        try
        {
          localObject2 = BitmapFactory.decodeFileDescriptor(((ParcelFileDescriptor)localObject2).getFileDescriptor());
          localObject1 = localObject2;
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          String str = String.valueOf(mUri);
          StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 34);
          localStringBuilder.append("OOM while loading bitmap for uri: ");
          localStringBuilder.append(str);
          Log.e("ImageManager", localStringBuilder.toString(), localOutOfMemoryError);
          bool = true;
        }
        ParcelFileDescriptor localParcelFileDescriptor = zams;
        try
        {
          localParcelFileDescriptor.close();
        }
        catch (IOException localIOException)
        {
          Log.e("ImageManager", "closed failed", localIOException);
        }
      }
      else
      {
        localObject1 = null;
        bool = false;
      }
      Object localObject3 = new CountDownLatch(1);
      ImageManager.getHandler(ImageManager.this).post(new ImageManager.zad(ImageManager.this, mUri, (Bitmap)localObject1, bool, (CountDownLatch)localObject3));
      try
      {
        ((CountDownLatch)localObject3).await();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
      localObject1 = String.valueOf(mUri);
      localObject3 = new StringBuilder(String.valueOf(localObject1).length() + 32);
      ((StringBuilder)localObject3).append("Latch interrupted while posting ");
      ((StringBuilder)localObject3).append((String)localObject1);
      Log.w("ImageManager", ((StringBuilder)localObject3).toString());
    }
  }
  
  private final class zac
    implements Runnable
  {
    private final Layer zamt;
    
    public zac(Layer paramLayer)
    {
      zamt = paramLayer;
    }
    
    public final void run()
    {
      Asserts.checkMainThread("LoadImageRunnable must be executed on the main thread");
      Object localObject1 = (ImageManager.ImageReceiver)ImageManager.put(ImageManager.this).get(zamt);
      if (localObject1 != null)
      {
        ImageManager.put(ImageManager.this).remove(zamt);
        ((ImageManager.ImageReceiver)localObject1).remove(zamt);
      }
      Response localResponse = zamt.zamv;
      if (url == null)
      {
        zamt.load(ImageManager.getContext(ImageManager.this), ImageManager.getAccount(ImageManager.this), true);
        return;
      }
      localObject1 = ImageManager.get(ImageManager.this, localResponse);
      if (localObject1 != null)
      {
        zamt.load(ImageManager.getContext(ImageManager.this), (Bitmap)localObject1, true);
        return;
      }
      localObject1 = (Long)ImageManager.access$getHostnames(ImageManager.this).get(url);
      if (localObject1 != null)
      {
        if (SystemClock.elapsedRealtime() - ((Long)localObject1).longValue() < 3600000L)
        {
          zamt.load(ImageManager.getContext(ImageManager.this), ImageManager.getAccount(ImageManager.this), true);
          return;
        }
        ImageManager.access$getHostnames(ImageManager.this).remove(url);
      }
      zamt.load(ImageManager.getContext(ImageManager.this), ImageManager.getAccount(ImageManager.this));
      Object localObject2 = (ImageManager.ImageReceiver)ImageManager.get(ImageManager.this).get(url);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ImageManager.ImageReceiver(ImageManager.this, url);
        ImageManager.get(ImageManager.this).put(url, localObject1);
      }
      ((ImageManager.ImageReceiver)localObject1).Refresh(zamt);
      if (!(zamt instanceof Game)) {
        ImageManager.put(ImageManager.this).put(zamt, localObject1);
      }
      localObject2 = ImageManager.zacc();
      try
      {
        if (!ImageManager.zacd().contains(url))
        {
          ImageManager.zacd().add(url);
          ((ImageManager.ImageReceiver)localObject1).zace();
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  private final class zad
    implements Runnable
  {
    private final Bitmap mBitmap;
    private final Uri mUri;
    private final CountDownLatch zadr;
    private boolean zamu;
    
    public zad(Uri paramUri, Bitmap paramBitmap, boolean paramBoolean, CountDownLatch paramCountDownLatch)
    {
      mUri = paramUri;
      mBitmap = paramBitmap;
      zamu = paramBoolean;
      zadr = paramCountDownLatch;
    }
    
    public final void run()
    {
      Asserts.checkMainThread("OnBitmapLoadedRunnable must be executed in the main thread");
      int i;
      if (mBitmap != null) {
        i = 1;
      } else {
        i = 0;
      }
      if (ImageManager.getCache(ImageManager.this) != null)
      {
        if (zamu)
        {
          ImageManager.getCache(ImageManager.this).evictAll();
          System.gc();
          zamu = false;
          ImageManager.getHandler(ImageManager.this).post(this);
          return;
        }
        if (i != 0) {
          ImageManager.getCache(ImageManager.this).put(new Response(mUri), mBitmap);
        }
      }
      Object localObject = (ImageManager.ImageReceiver)ImageManager.get(ImageManager.this).remove(mUri);
      if (localObject != null)
      {
        localObject = ImageManager.ImageReceiver.access$getFiles((ImageManager.ImageReceiver)localObject);
        int k = ((ArrayList)localObject).size();
        int j = 0;
        while (j < k)
        {
          Layer localLayer = (Layer)((ArrayList)localObject).get(j);
          if (i != 0)
          {
            localLayer.load(ImageManager.getContext(ImageManager.this), mBitmap, false);
          }
          else
          {
            ImageManager.access$getHostnames(ImageManager.this).put(mUri, Long.valueOf(SystemClock.elapsedRealtime()));
            localLayer.load(ImageManager.getContext(ImageManager.this), ImageManager.getAccount(ImageManager.this), false);
          }
          if (!(localLayer instanceof Game)) {
            ImageManager.put(ImageManager.this).remove(localLayer);
          }
          j += 1;
        }
      }
      zadr.countDown();
      localObject = ImageManager.zacc();
      try
      {
        ImageManager.zacd().remove(mUri);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}
