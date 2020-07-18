package androidx.loader.app;

import android.os.Bundle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.content.Loader;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class LoaderManager
{
  public LoaderManager() {}
  
  public static void enableDebugLogging(boolean paramBoolean)
  {
    LoaderManagerImpl.DEBUG = paramBoolean;
  }
  
  public static LoaderManager getInstance(LifecycleOwner paramLifecycleOwner)
  {
    return new LoaderManagerImpl(paramLifecycleOwner, ((ViewModelStoreOwner)paramLifecycleOwner).getViewModelStore());
  }
  
  public abstract void destroyLoader(int paramInt);
  
  public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract Loader getLoader(int paramInt);
  
  public boolean hasRunningLoaders()
  {
    return false;
  }
  
  public abstract Loader initLoader(int paramInt, Bundle paramBundle, LoaderCallbacks paramLoaderCallbacks);
  
  public abstract void markForRedelivery();
  
  public abstract Loader restartLoader(int paramInt, Bundle paramBundle, LoaderCallbacks paramLoaderCallbacks);
  
  public static abstract interface LoaderCallbacks<D>
  {
    public abstract Loader onCreateLoader(int paramInt, Bundle paramBundle);
    
    public abstract void onLoadFinished(Loader paramLoader, Object paramObject);
    
    public abstract void onLoaderReset(Loader paramLoader);
  }
}
