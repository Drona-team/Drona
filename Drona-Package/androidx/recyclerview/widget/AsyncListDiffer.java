package androidx.recyclerview.widget;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class AsyncListDiffer<T>
{
  private static final Executor sMainThreadExecutor = new MainThreadExecutor();
  final AsyncDifferConfig<T> mConfig;
  @Nullable
  private List<T> mList;
  private final List<ListListener<T>> mListeners = new CopyOnWriteArrayList();
  Executor mMainThreadExecutor;
  int mMaxScheduledGeneration;
  @NonNull
  private List<T> mReadOnlyList = Collections.emptyList();
  private final ListUpdateCallback mUpdateCallback;
  
  public AsyncListDiffer(ListUpdateCallback paramListUpdateCallback, AsyncDifferConfig paramAsyncDifferConfig)
  {
    mUpdateCallback = paramListUpdateCallback;
    mConfig = paramAsyncDifferConfig;
    if (paramAsyncDifferConfig.getMainThreadExecutor() != null)
    {
      mMainThreadExecutor = paramAsyncDifferConfig.getMainThreadExecutor();
      return;
    }
    mMainThreadExecutor = sMainThreadExecutor;
  }
  
  public AsyncListDiffer(RecyclerView.Adapter paramAdapter, DiffUtil.ItemCallback paramItemCallback)
  {
    this(new AdapterListUpdateCallback(paramAdapter), new AsyncDifferConfig.Builder(paramItemCallback).build());
  }
  
  private void onCurrentListChanged(List paramList, Runnable paramRunnable)
  {
    Iterator localIterator = mListeners.iterator();
    while (localIterator.hasNext()) {
      ((ListListener)localIterator.next()).onCurrentListChanged(paramList, mReadOnlyList);
    }
    if (paramRunnable != null) {
      paramRunnable.run();
    }
  }
  
  public void addListListener(ListListener paramListListener)
  {
    mListeners.add(paramListListener);
  }
  
  public List getCurrentList()
  {
    return mReadOnlyList;
  }
  
  void latchList(List paramList, DiffUtil.DiffResult paramDiffResult, Runnable paramRunnable)
  {
    List localList = mReadOnlyList;
    mList = paramList;
    mReadOnlyList = Collections.unmodifiableList(paramList);
    paramDiffResult.dispatchUpdatesTo(mUpdateCallback);
    onCurrentListChanged(localList, paramRunnable);
  }
  
  public void removeListListener(ListListener paramListListener)
  {
    mListeners.remove(paramListListener);
  }
  
  public void submitList(List paramList)
  {
    submitList(paramList, null);
  }
  
  public void submitList(final List paramList, final Runnable paramRunnable)
  {
    final int i = mMaxScheduledGeneration + 1;
    mMaxScheduledGeneration = i;
    if (paramList == mList)
    {
      if (paramRunnable != null) {
        paramRunnable.run();
      }
    }
    else
    {
      final List localList = mReadOnlyList;
      if (paramList == null)
      {
        i = mList.size();
        mList = null;
        mReadOnlyList = Collections.emptyList();
        mUpdateCallback.onRemoved(0, i);
        onCurrentListChanged(localList, paramRunnable);
        return;
      }
      if (mList == null)
      {
        mList = paramList;
        mReadOnlyList = Collections.unmodifiableList(paramList);
        mUpdateCallback.onInserted(0, paramList.size());
        onCurrentListChanged(localList, paramRunnable);
        return;
      }
      localList = mList;
      mConfig.getBackgroundThreadExecutor().execute(new Runnable()
      {
        public void run()
        {
          final DiffUtil.DiffResult localDiffResult = DiffUtil.calculateDiff(new DiffUtil.Callback()
          {
            public boolean areContentsTheSame(int paramAnonymous2Int1, int paramAnonymous2Int2)
            {
              Object localObject1 = val$oldList.get(paramAnonymous2Int1);
              Object localObject2 = val$newList.get(paramAnonymous2Int2);
              if ((localObject1 != null) && (localObject2 != null)) {
                return mConfig.getDiffCallback().areContentsTheSame(localObject1, localObject2);
              }
              if ((localObject1 == null) && (localObject2 == null)) {
                return true;
              }
              throw new AssertionError();
            }
            
            public boolean areItemsTheSame(int paramAnonymous2Int1, int paramAnonymous2Int2)
            {
              Object localObject1 = val$oldList.get(paramAnonymous2Int1);
              Object localObject2 = val$newList.get(paramAnonymous2Int2);
              if ((localObject1 != null) && (localObject2 != null)) {
                return mConfig.getDiffCallback().areItemsTheSame(localObject1, localObject2);
              }
              return (localObject1 == null) && (localObject2 == null);
            }
            
            public Object getChangePayload(int paramAnonymous2Int1, int paramAnonymous2Int2)
            {
              Object localObject1 = val$oldList.get(paramAnonymous2Int1);
              Object localObject2 = val$newList.get(paramAnonymous2Int2);
              if ((localObject1 != null) && (localObject2 != null)) {
                return mConfig.getDiffCallback().getChangePayload(localObject1, localObject2);
              }
              throw new AssertionError();
            }
            
            public int getNewListSize()
            {
              return val$newList.size();
            }
            
            public int getOldListSize()
            {
              return val$oldList.size();
            }
          });
          mMainThreadExecutor.execute(new Runnable()
          {
            public void run()
            {
              if (mMaxScheduledGeneration == val$runGeneration) {
                latchList(val$newList, localDiffResult, val$commitCallback);
              }
            }
          });
        }
      });
    }
  }
  
  public static abstract interface ListListener<T>
  {
    public abstract void onCurrentListChanged(List paramList1, List paramList2);
  }
  
  private static class MainThreadExecutor
    implements Executor
  {
    final Handler mHandler = new Handler(Looper.getMainLooper());
    
    MainThreadExecutor() {}
    
    public void execute(Runnable paramRunnable)
    {
      mHandler.post(paramRunnable);
    }
  }
}
