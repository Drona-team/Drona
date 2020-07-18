package com.facebook.react.modules.image;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseArray;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.fresco.ReactNetworkImageRequest;
import com.facebook.react.views.imagehelper.ImageSource;

@ReactModule(name="ImageLoader")
public class ImageLoaderModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener
{
  private static final String ERROR_GET_SIZE_FAILURE = "E_GET_SIZE_FAILURE";
  private static final String ERROR_INVALID_URI = "E_INVALID_URI";
  private static final String ERROR_PREFETCH_FAILURE = "E_PREFETCH_FAILURE";
  public static final String NAME = "ImageLoader";
  private final Object mCallerContext;
  private final Object mEnqueuedRequestMonitor = new Object();
  private final SparseArray<DataSource<Void>> mEnqueuedRequests = new SparseArray();
  
  public ImageLoaderModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    mCallerContext = this;
  }
  
  public ImageLoaderModule(ReactApplicationContext paramReactApplicationContext, Object paramObject)
  {
    super(paramReactApplicationContext);
    mCallerContext = paramObject;
  }
  
  private void registerRequest(int paramInt, DataSource paramDataSource)
  {
    Object localObject = mEnqueuedRequestMonitor;
    try
    {
      mEnqueuedRequests.put(paramInt, paramDataSource);
      return;
    }
    catch (Throwable paramDataSource)
    {
      throw paramDataSource;
    }
  }
  
  private DataSource removeRequest(int paramInt)
  {
    Object localObject = mEnqueuedRequestMonitor;
    try
    {
      DataSource localDataSource = (DataSource)mEnqueuedRequests.get(paramInt);
      mEnqueuedRequests.remove(paramInt);
      return localDataSource;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void abortRequest(int paramInt)
  {
    DataSource localDataSource = removeRequest(paramInt);
    if (localDataSource != null) {
      localDataSource.close();
    }
  }
  
  public String getName()
  {
    return "ImageLoader";
  }
  
  public void getSize(String paramString, final Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty()))
    {
      paramString = ImageRequestBuilder.newBuilderWithSource(new ImageSource(getReactApplicationContext(), paramString).getUri()).build();
      Fresco.getImagePipeline().fetchDecodedImage(paramString, mCallerContext).subscribe(new BaseDataSubscriber()
      {
        protected void onFailureImpl(DataSource paramAnonymousDataSource)
        {
          paramPromise.reject("E_GET_SIZE_FAILURE", paramAnonymousDataSource.getFailureCause());
        }
        
        protected void onNewResultImpl(DataSource paramAnonymousDataSource)
        {
          if (!paramAnonymousDataSource.isFinished()) {
            return;
          }
          paramAnonymousDataSource = (CloseableReference)paramAnonymousDataSource.getResult();
          if (paramAnonymousDataSource != null)
          {
            try
            {
              Object localObject1 = paramAnonymousDataSource.get();
              Object localObject2 = (CloseableImage)localObject1;
              localObject1 = Arguments.createMap();
              ((WritableMap)localObject1).putInt("width", ((ImageInfo)localObject2).getWidth());
              ((WritableMap)localObject1).putInt("height", ((ImageInfo)localObject2).getHeight());
              localObject2 = paramPromise;
              ((Promise)localObject2).resolve(localObject1);
            }
            catch (Throwable localThrowable)
            {
              break label99;
            }
            catch (Exception localException)
            {
              paramPromise.reject("E_GET_SIZE_FAILURE", localException);
            }
            CloseableReference.closeSafely(paramAnonymousDataSource);
            return;
            label99:
            CloseableReference.closeSafely(paramAnonymousDataSource);
            throw localException;
          }
          paramPromise.reject("E_GET_SIZE_FAILURE");
        }
      }, CallerThreadExecutor.getInstance());
      return;
    }
    paramPromise.reject("E_INVALID_URI", "Cannot get the size of an image for an empty URI");
  }
  
  public void getSizeWithHeaders(String paramString, ReadableMap paramReadableMap, final Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty()))
    {
      paramString = ReactNetworkImageRequest.fromBuilderWithHeaders(ImageRequestBuilder.newBuilderWithSource(new ImageSource(getReactApplicationContext(), paramString).getUri()), paramReadableMap);
      Fresco.getImagePipeline().fetchDecodedImage(paramString, mCallerContext).subscribe(new BaseDataSubscriber()
      {
        protected void onFailureImpl(DataSource paramAnonymousDataSource)
        {
          paramPromise.reject("E_GET_SIZE_FAILURE", paramAnonymousDataSource.getFailureCause());
        }
        
        protected void onNewResultImpl(DataSource paramAnonymousDataSource)
        {
          if (!paramAnonymousDataSource.isFinished()) {
            return;
          }
          paramAnonymousDataSource = (CloseableReference)paramAnonymousDataSource.getResult();
          if (paramAnonymousDataSource != null)
          {
            try
            {
              Object localObject1 = paramAnonymousDataSource.get();
              Object localObject2 = (CloseableImage)localObject1;
              localObject1 = Arguments.createMap();
              ((WritableMap)localObject1).putInt("width", ((ImageInfo)localObject2).getWidth());
              ((WritableMap)localObject1).putInt("height", ((ImageInfo)localObject2).getHeight());
              localObject2 = paramPromise;
              ((Promise)localObject2).resolve(localObject1);
            }
            catch (Throwable localThrowable)
            {
              break label99;
            }
            catch (Exception localException)
            {
              paramPromise.reject("E_GET_SIZE_FAILURE", localException);
            }
            CloseableReference.closeSafely(paramAnonymousDataSource);
            return;
            label99:
            CloseableReference.closeSafely(paramAnonymousDataSource);
            throw localException;
          }
          paramPromise.reject("E_GET_SIZE_FAILURE");
        }
      }, CallerThreadExecutor.getInstance());
      return;
    }
    paramPromise.reject("E_INVALID_URI", "Cannot get the size of an image for an empty URI");
  }
  
  public void onHostDestroy()
  {
    Object localObject = mEnqueuedRequestMonitor;
    int i = 0;
    for (;;)
    {
      try
      {
        int j = mEnqueuedRequests.size();
        if (i < j)
        {
          DataSource localDataSource = (DataSource)mEnqueuedRequests.valueAt(i);
          if (localDataSource != null) {
            localDataSource.close();
          }
        }
        else
        {
          mEnqueuedRequests.clear();
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      i += 1;
    }
  }
  
  public void onHostPause() {}
  
  public void onHostResume() {}
  
  public void prefetchImage(String paramString, final int paramInt, final Promise paramPromise)
  {
    if ((paramString != null) && (!paramString.isEmpty()))
    {
      paramString = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramString)).build();
      paramString = Fresco.getImagePipeline().prefetchToDiskCache(paramString, mCallerContext);
      paramPromise = new BaseDataSubscriber()
      {
        protected void onFailureImpl(DataSource paramAnonymousDataSource)
        {
          try
          {
            ImageLoaderModule.this.removeRequest(paramInt);
            paramPromise.reject("E_PREFETCH_FAILURE", paramAnonymousDataSource.getFailureCause());
            paramAnonymousDataSource.close();
            return;
          }
          catch (Throwable localThrowable)
          {
            paramAnonymousDataSource.close();
            throw localThrowable;
          }
        }
        
        protected void onNewResultImpl(DataSource paramAnonymousDataSource)
        {
          if (!paramAnonymousDataSource.isFinished()) {
            return;
          }
          try
          {
            ImageLoaderModule.this.removeRequest(paramInt);
            paramPromise.resolve(Boolean.valueOf(true));
            paramAnonymousDataSource.close();
            return;
          }
          catch (Throwable localThrowable)
          {
            paramAnonymousDataSource.close();
            throw localThrowable;
          }
        }
      };
      registerRequest(paramInt, paramString);
      paramString.subscribe(paramPromise, CallerThreadExecutor.getInstance());
      return;
    }
    paramPromise.reject("E_INVALID_URI", "Cannot prefetch an image for an empty URI");
  }
  
  public void queryCache(final ReadableArray paramReadableArray, final Promise paramPromise)
  {
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = Arguments.createMap();
        ImagePipeline localImagePipeline = Fresco.getImagePipeline();
        int i = 0;
        while (i < paramReadableArray.size())
        {
          String str = paramReadableArray.getString(i);
          Uri localUri = Uri.parse(str);
          if (localImagePipeline.isInBitmapMemoryCache(localUri)) {
            paramAnonymousVarArgs.putString(str, "memory");
          } else if (localImagePipeline.isInDiskCacheSync(localUri)) {
            paramAnonymousVarArgs.putString(str, "disk");
          }
          i += 1;
        }
        paramPromise.resolve(paramAnonymousVarArgs);
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
}
