package expo.modules.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.unimodules.core.interfaces.InternalModule;
import org.unimodules.interfaces.imageloader.ImageLoader;
import org.unimodules.interfaces.imageloader.ImageLoader.ResultListener;

@Metadata(bv={1, 0, 3}, d1={"\000<\n\002\030\002\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\b\004\n\002\020 \n\002\030\002\n\000\n\002\030\002\n\002\030\002\n\000\n\002\020\016\n\002\020\002\n\000\n\002\030\002\n\002\b\003\030\0002\0020\0012\0020\002B\r\022\006\020\003\032\0020\004?\006\002\020\005J\024\020\b\032\016\022\b\022\006\022\002\b\0030\n\030\0010\tH\026J\026\020\013\032\b\022\004\022\0020\r0\f2\006\020\016\032\0020\017H\026J\030\020\013\032\0020\0202\006\020\016\032\0020\0172\006\020\021\032\0020\022H\026J\030\020\023\032\b\022\004\022\0020\r0\f2\b\b\001\020\016\032\0020\017H\026J\030\020\023\032\0020\0202\006\020\016\032\0020\0172\006\020\021\032\0020\022H\026J\020\020\024\032\0020\0172\006\020\016\032\0020\017H\002R\021\020\003\032\0020\004?\006\b\n\000\032\004\b\006\020\007?\006\025"}, d2={"Lexpo/modules/imageloader/ImageLoaderModule;", "Lorg/unimodules/core/interfaces/InternalModule;", "Lorg/unimodules/interfaces/imageloader/ImageLoader;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "getExportedInterfaces", "", "Ljava/lang/Class;", "loadImageForDisplayFromURL", "Ljava/util/concurrent/Future;", "Landroid/graphics/Bitmap;", "url", "", "", "resultListener", "Lorg/unimodules/interfaces/imageloader/ImageLoader$ResultListener;", "loadImageForManipulationFromURL", "normalizeAssetsUrl", "expo-image-loader_release"}, k=1, mv={1, 1, 15})
public final class ImageLoaderModule
  implements InternalModule, ImageLoader
{
  @NotNull
  private final Context context;
  
  public ImageLoaderModule(Context paramContext)
  {
    context = paramContext;
  }
  
  private final String normalizeAssetsUrl(String paramString)
  {
    Object localObject = paramString;
    if (StringsKt.startsWith$default(paramString, "asset:///", false, 2, null))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("file:///android_asset/");
      ((StringBuilder)localObject).append((String)CollectionsKt.last(StringsKt.split$default((CharSequence)paramString, new String[] { "/" }, false, 0, 6, null)));
      localObject = ((StringBuilder)localObject).toString();
    }
    return localObject;
  }
  
  public final Context getContext()
  {
    return context;
  }
  
  public List getExportedInterfaces()
  {
    return CollectionsKt.listOf(ImageLoader.class);
  }
  
  public Future loadImageForDisplayFromURL(String paramString)
  {
    Intrinsics.checkParameterIsNotNull(paramString, "url");
    SimpleSettableFuture localSimpleSettableFuture = new SimpleSettableFuture();
    loadImageForDisplayFromURL(paramString, (ImageLoader.ResultListener)new ImageLoader.ResultListener()
    {
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        setException((Exception)new ExecutionException(paramAnonymousThrowable));
      }
      
      public void onSuccess(Bitmap paramAnonymousBitmap)
      {
        Intrinsics.checkParameterIsNotNull(paramAnonymousBitmap, "bitmap");
        track(paramAnonymousBitmap);
      }
    });
    return (Future)localSimpleSettableFuture;
  }
  
  public void loadImageForDisplayFromURL(String paramString, ImageLoader.ResultListener paramResultListener)
  {
    Intrinsics.checkParameterIsNotNull(paramString, "url");
    Intrinsics.checkParameterIsNotNull(paramResultListener, "resultListener");
    paramString = ImageRequest.fromUri(paramString);
    Fresco.getImagePipeline().fetchDecodedImage(paramString, context).subscribe((DataSubscriber)new BaseBitmapDataSubscriber()
    {
      protected void onFailureImpl(DataSource paramAnonymousDataSource)
      {
        Intrinsics.checkParameterIsNotNull(paramAnonymousDataSource, "dataSource");
        onFailure(paramAnonymousDataSource.getFailureCause());
      }
      
      protected void onNewResultImpl(Bitmap paramAnonymousBitmap)
      {
        if (paramAnonymousBitmap != null)
        {
          onSuccess(paramAnonymousBitmap);
          return;
        }
        onFailure((Throwable)new Exception("Loaded bitmap is null"));
      }
    }, AsyncTask.THREAD_POOL_EXECUTOR);
  }
  
  public Future loadImageForManipulationFromURL(String paramString)
  {
    Intrinsics.checkParameterIsNotNull(paramString, "url");
    SimpleSettableFuture localSimpleSettableFuture = new SimpleSettableFuture();
    loadImageForManipulationFromURL(paramString, (ImageLoader.ResultListener)new ImageLoader.ResultListener()
    {
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        setException((Exception)new ExecutionException(paramAnonymousThrowable));
      }
      
      public void onSuccess(Bitmap paramAnonymousBitmap)
      {
        Intrinsics.checkParameterIsNotNull(paramAnonymousBitmap, "bitmap");
        track(paramAnonymousBitmap);
      }
    });
    return (Future)localSimpleSettableFuture;
  }
  
  public void loadImageForManipulationFromURL(String paramString, ImageLoader.ResultListener paramResultListener)
  {
    Intrinsics.checkParameterIsNotNull(paramString, "url");
    Intrinsics.checkParameterIsNotNull(paramResultListener, "resultListener");
    paramString = normalizeAssetsUrl(paramString);
    ((RequestBuilder)((RequestBuilder)Glide.with(context).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).load(paramString).into((Target)new SimpleTarget()
    {
      public void onLoadFailed(Drawable paramAnonymousDrawable)
      {
        onFailure((Throwable)new Exception("Loading bitmap failed"));
      }
      
      public void onResourceReady(Bitmap paramAnonymousBitmap, Transition paramAnonymousTransition)
      {
        Intrinsics.checkParameterIsNotNull(paramAnonymousBitmap, "resource");
        onSuccess(paramAnonymousBitmap);
      }
    });
  }
}
