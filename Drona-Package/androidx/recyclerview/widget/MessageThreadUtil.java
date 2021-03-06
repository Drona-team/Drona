package androidx.recyclerview.widget;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T>
  implements ThreadUtil<T>
{
  MessageThreadUtil() {}
  
  public ThreadUtil.BackgroundCallback getBackgroundProxy(final ThreadUtil.BackgroundCallback paramBackgroundCallback)
  {
    new ThreadUtil.BackgroundCallback()
    {
      static final int LOAD_TILE = 3;
      static final int RECYCLE_TILE = 4;
      static final int REFRESH = 1;
      static final int UPDATE_RANGE = 2;
      private Runnable mBackgroundRunnable = new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            Object localObject = mQueue.next();
            if (localObject == null)
            {
              mBackgroundRunning.set(false);
              return;
            }
            switch (what)
            {
            default: 
              StringBuilder localStringBuilder = new StringBuilder();
              localStringBuilder.append("Unsupported message, what=");
              localStringBuilder.append(what);
              Log.e("ThreadUtil", localStringBuilder.toString());
              break;
            case 4: 
              localObject = (TileList.Tile)data;
              val$callback.recycleTile((TileList.Tile)localObject);
              break;
            case 3: 
              val$callback.loadTile(arg1, arg2);
              break;
            case 2: 
              mQueue.removeMessages(2);
              mQueue.removeMessages(3);
              val$callback.updateRange(arg1, arg2, arg3, arg4, arg5);
              break;
            case 1: 
              mQueue.removeMessages(1);
              val$callback.refresh(arg1);
            }
          }
        }
      };
      AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
      private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();
      
      private void maybeExecuteBackgroundRunnable()
      {
        if (mBackgroundRunning.compareAndSet(false, true)) {
          mExecutor.execute(mBackgroundRunnable);
        }
      }
      
      private void sendMessage(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        mQueue.sendMessage(paramAnonymousSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }
      
      private void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        mQueue.sendMessageAtFrontOfQueue(paramAnonymousSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }
      
      public void loadTile(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramAnonymousInt1, paramAnonymousInt2));
      }
      
      public void recycleTile(TileList.Tile paramAnonymousTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(4, 0, paramAnonymousTile));
      }
      
      public void refresh(int paramAnonymousInt)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramAnonymousInt, null));
      }
      
      public void updateRange(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousInt5, null));
      }
    };
  }
  
  public ThreadUtil.MainThreadCallback getMainThreadProxy(final ThreadUtil.MainThreadCallback paramMainThreadCallback)
  {
    new ThreadUtil.MainThreadCallback()
    {
      static final int ADD_TILE = 2;
      static final int REMOVE_TILE = 3;
      static final int UPDATE_ITEM_COUNT = 1;
      private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
      private Runnable mMainThreadRunnable = new Runnable()
      {
        public void run()
        {
          for (MessageThreadUtil.SyncQueueItem localSyncQueueItem = mQueue.next(); localSyncQueueItem != null; localSyncQueueItem = mQueue.next())
          {
            Object localObject;
            switch (what)
            {
            default: 
              localObject = new StringBuilder();
              ((StringBuilder)localObject).append("Unsupported message, what=");
              ((StringBuilder)localObject).append(what);
              Log.e("ThreadUtil", ((StringBuilder)localObject).toString());
              break;
            case 3: 
              val$callback.removeTile(arg1, arg2);
              break;
            case 2: 
              localObject = (TileList.Tile)data;
              val$callback.addTile(arg1, (TileList.Tile)localObject);
              break;
            case 1: 
              val$callback.updateItemCount(arg1, arg2);
            }
          }
        }
      };
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();
      
      private void sendMessage(MessageThreadUtil.SyncQueueItem paramAnonymousSyncQueueItem)
      {
        mQueue.sendMessage(paramAnonymousSyncQueueItem);
        mMainThreadHandler.post(mMainThreadRunnable);
      }
      
      public void addTile(int paramAnonymousInt, TileList.Tile paramAnonymousTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramAnonymousInt, paramAnonymousTile));
      }
      
      public void removeTile(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramAnonymousInt1, paramAnonymousInt2));
      }
      
      public void updateItemCount(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramAnonymousInt1, paramAnonymousInt2));
      }
    };
  }
  
  static class MessageQueue
  {
    private MessageThreadUtil.SyncQueueItem mRoot;
    
    MessageQueue() {}
    
    MessageThreadUtil.SyncQueueItem next()
    {
      try
      {
        MessageThreadUtil.SyncQueueItem localSyncQueueItem = mRoot;
        if (localSyncQueueItem == null) {
          return null;
        }
        localSyncQueueItem = mRoot;
        mRoot = mRoot.next;
        return localSyncQueueItem;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    void removeMessages(int paramInt)
    {
      try
      {
        Object localObject1;
        while ((mRoot != null) && (mRoot.what == paramInt))
        {
          localObject1 = mRoot;
          mRoot = mRoot.next;
          ((MessageThreadUtil.SyncQueueItem)localObject1).recycle();
        }
        if (mRoot != null)
        {
          Object localObject2 = mRoot;
          MessageThreadUtil.SyncQueueItem localSyncQueueItem;
          for (localObject1 = next; localObject1 != null; localObject1 = localSyncQueueItem)
          {
            localSyncQueueItem = next;
            if (what == paramInt)
            {
              next = localSyncQueueItem;
              ((MessageThreadUtil.SyncQueueItem)localObject1).recycle();
            }
            else
            {
              localObject2 = localObject1;
            }
          }
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    void sendMessage(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      try
      {
        if (mRoot == null)
        {
          mRoot = paramSyncQueueItem;
          return;
        }
        for (MessageThreadUtil.SyncQueueItem localSyncQueueItem = mRoot; next != null; localSyncQueueItem = next) {}
        next = paramSyncQueueItem;
        return;
      }
      catch (Throwable paramSyncQueueItem)
      {
        throw paramSyncQueueItem;
      }
    }
    
    void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      try
      {
        next = mRoot;
        mRoot = paramSyncQueueItem;
        return;
      }
      catch (Throwable paramSyncQueueItem)
      {
        throw paramSyncQueueItem;
      }
    }
  }
  
  static class SyncQueueItem
  {
    private static SyncQueueItem sPool;
    private static final Object sPoolLock = new Object();
    public int arg1;
    public int arg2;
    public int arg3;
    public int arg4;
    public int arg5;
    public Object data;
    SyncQueueItem next;
    public int what;
    
    SyncQueueItem() {}
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3)
    {
      return obtainMessage(paramInt1, paramInt2, paramInt3, 0, 0, 0, null);
    }
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Object paramObject)
    {
      Object localObject = sPoolLock;
      try
      {
        SyncQueueItem localSyncQueueItem;
        if (sPool == null)
        {
          localSyncQueueItem = new SyncQueueItem();
        }
        else
        {
          localSyncQueueItem = sPool;
          sPool = sPoolnext;
          next = null;
        }
        what = paramInt1;
        arg1 = paramInt2;
        arg2 = paramInt3;
        arg3 = paramInt4;
        arg4 = paramInt5;
        arg5 = paramInt6;
        data = paramObject;
        return localSyncQueueItem;
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
    }
    
    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, Object paramObject)
    {
      return obtainMessage(paramInt1, paramInt2, 0, 0, 0, 0, paramObject);
    }
    
    void recycle()
    {
      next = null;
      arg5 = 0;
      arg4 = 0;
      arg3 = 0;
      arg2 = 0;
      arg1 = 0;
      what = 0;
      data = null;
      Object localObject = sPoolLock;
      try
      {
        if (sPool != null) {
          next = sPool;
        }
        sPool = this;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}
