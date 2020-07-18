package bolts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Task<TResult>
{
  public static final ExecutorService BACKGROUND_EXECUTOR = ;
  private static final Executor IMMEDIATE_EXECUTOR = BoltsExecutors.immediate();
  private static Task<?> TASK_CANCELLED = new Task(true);
  private static Task<Boolean> TASK_FALSE;
  private static Task<?> TASK_NULL;
  private static Task<Boolean> TASK_TRUE;
  public static final Executor UI_THREAD_EXECUTOR = AndroidExecutors.uiThread();
  private static volatile UnobservedExceptionHandler unobservedExceptionHandler;
  private boolean cancelled;
  private boolean complete;
  private List<Continuation<TResult, Void>> continuations = new ArrayList();
  private Exception error;
  private boolean errorHasBeenObserved;
  private final Object lock = new Object();
  private TResult result;
  private UnobservedErrorNotifier unobservedErrorNotifier;
  
  static
  {
    TASK_NULL = new Task(null);
    TASK_TRUE = new Task(Boolean.valueOf(true));
    TASK_FALSE = new Task(Boolean.valueOf(false));
  }
  
  Task() {}
  
  private Task(Object paramObject)
  {
    trySetResult(paramObject);
  }
  
  private Task(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      trySetCancelled();
      return;
    }
    trySetResult(null);
  }
  
  public static Task call(Callable paramCallable)
  {
    return call(paramCallable, IMMEDIATE_EXECUTOR, null);
  }
  
  public static Task call(Callable paramCallable, CancellationToken paramCancellationToken)
  {
    return call(paramCallable, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public static Task call(Callable paramCallable, Executor paramExecutor)
  {
    return call(paramCallable, paramExecutor, null);
  }
  
  public static Task call(final Callable paramCallable, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            localTaskCompletionSource.setCancelled();
            return;
          }
          TaskCompletionSource localTaskCompletionSource = localTaskCompletionSource;
          Callable localCallable = paramCallable;
          try
          {
            localTaskCompletionSource.setResult(localCallable.call());
            return;
          }
          catch (Exception localException)
          {
            localTaskCompletionSource.setError(localException);
            return;
            localTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
    }
    catch (Exception paramCallable)
    {
      localTaskCompletionSource.setError(new ExecutorException(paramCallable));
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static Task callInBackground(Callable paramCallable)
  {
    return call(paramCallable, BACKGROUND_EXECUTOR, null);
  }
  
  public static Task callInBackground(Callable paramCallable, CancellationToken paramCancellationToken)
  {
    return call(paramCallable, BACKGROUND_EXECUTOR, paramCancellationToken);
  }
  
  public static Task cancelled()
  {
    return TASK_CANCELLED;
  }
  
  private static void completeAfterTask(final TaskCompletionSource paramTaskCompletionSource, final Continuation paramContinuation, final Task paramTask, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            paramTaskCompletionSource.setCancelled();
            return;
          }
          Object localObject = paramContinuation;
          Task localTask = paramTask;
          try
          {
            localObject = ((Continuation)localObject).then(localTask);
            localObject = (Task)localObject;
            if (localObject == null)
            {
              localObject = paramTaskCompletionSource;
              ((TaskCompletionSource)localObject).setResult(null);
              return;
            }
            ((Task)localObject).continueWith(new Continuation()
            {
              public Void then(Task paramAnonymous2Task)
              {
                if ((val$ct != null) && (val$ct.isCancellationRequested()))
                {
                  val$tcs.setCancelled();
                  return null;
                }
                if (paramAnonymous2Task.isCancelled())
                {
                  val$tcs.setCancelled();
                  return null;
                }
                if (paramAnonymous2Task.isFaulted())
                {
                  val$tcs.setError(paramAnonymous2Task.getError());
                  return null;
                }
                val$tcs.setResult(paramAnonymous2Task.getResult());
                return null;
              }
            });
            return;
          }
          catch (Exception localException)
          {
            paramTaskCompletionSource.setError(localException);
            return;
            paramTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
      return;
    }
    catch (Exception paramContinuation)
    {
      paramTaskCompletionSource.setError(new ExecutorException(paramContinuation));
    }
  }
  
  private static void completeImmediately(final TaskCompletionSource paramTaskCompletionSource, final Continuation paramContinuation, final Task paramTask, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            paramTaskCompletionSource.setCancelled();
            return;
          }
          Object localObject1 = paramContinuation;
          Object localObject2 = paramTask;
          try
          {
            localObject1 = ((Continuation)localObject1).then((Task)localObject2);
            localObject2 = paramTaskCompletionSource;
            ((TaskCompletionSource)localObject2).setResult(localObject1);
            return;
          }
          catch (Exception localException)
          {
            paramTaskCompletionSource.setError(localException);
            return;
            paramTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
      return;
    }
    catch (Exception paramContinuation)
    {
      paramTaskCompletionSource.setError(new ExecutorException(paramContinuation));
    }
  }
  
  public static TaskCompletionSource create()
  {
    Task localTask = new Task();
    localTask.getClass();
    return new TaskCompletionSource();
  }
  
  public static Task delay(long paramLong)
  {
    return delay(paramLong, BoltsExecutors.scheduled(), null);
  }
  
  public static Task delay(long paramLong, CancellationToken paramCancellationToken)
  {
    return delay(paramLong, BoltsExecutors.scheduled(), paramCancellationToken);
  }
  
  static Task delay(long paramLong, ScheduledExecutorService paramScheduledExecutorService, CancellationToken paramCancellationToken)
  {
    if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
      return cancelled();
    }
    if (paramLong <= 0L) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramScheduledExecutorService = paramScheduledExecutorService.schedule(new Runnable()
    {
      public void run()
      {
        val$tcs.trySetResult(null);
      }
    }, paramLong, TimeUnit.MILLISECONDS);
    if (paramCancellationToken != null) {
      paramCancellationToken.register(new Runnable()
      {
        public void run()
        {
          val$scheduled.cancel(true);
          localTaskCompletionSource.trySetCancelled();
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static Task forError(Exception paramException)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    localTaskCompletionSource.setError(paramException);
    return localTaskCompletionSource.getTask();
  }
  
  public static Task forResult(Object paramObject)
  {
    if (paramObject == null) {
      return TASK_NULL;
    }
    if ((paramObject instanceof Boolean))
    {
      if (((Boolean)paramObject).booleanValue()) {
        return TASK_TRUE;
      }
      return TASK_FALSE;
    }
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    localTaskCompletionSource.setResult(paramObject);
    return localTaskCompletionSource.getTask();
  }
  
  public static UnobservedExceptionHandler getUnobservedExceptionHandler()
  {
    return unobservedExceptionHandler;
  }
  
  private void runContinuations()
  {
    Object localObject = lock;
    try
    {
      Iterator localIterator = continuations.iterator();
      while (localIterator.hasNext())
      {
        Continuation localContinuation = (Continuation)localIterator.next();
        try
        {
          localContinuation.then(this);
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
        catch (RuntimeException localRuntimeException)
        {
          throw localRuntimeException;
        }
      }
      continuations = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static void setUnobservedExceptionHandler(UnobservedExceptionHandler paramUnobservedExceptionHandler)
  {
    unobservedExceptionHandler = paramUnobservedExceptionHandler;
  }
  
  public static Task whenAll(Collection paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    final ArrayList localArrayList = new ArrayList();
    Object localObject = new Object();
    final AtomicInteger localAtomicInteger = new AtomicInteger(paramCollection.size());
    final AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task paramAnonymousTask)
        {
          if (paramAnonymousTask.isFaulted())
          {
            Object localObject = val$errorLock;
            try
            {
              localArrayList.add(paramAnonymousTask.getError());
            }
            catch (Throwable paramAnonymousTask)
            {
              throw paramAnonymousTask;
            }
          }
          if (paramAnonymousTask.isCancelled()) {
            localAtomicBoolean.set(true);
          }
          if (localAtomicInteger.decrementAndGet() == 0)
          {
            if (localArrayList.size() != 0)
            {
              if (localArrayList.size() == 1)
              {
                localTaskCompletionSource.setError((Exception)localArrayList.get(0));
                return null;
              }
              paramAnonymousTask = new AggregateException(String.format("There were %d exceptions.", new Object[] { Integer.valueOf(localArrayList.size()) }), localArrayList);
              localTaskCompletionSource.setError(paramAnonymousTask);
              return null;
            }
            if (localAtomicBoolean.get())
            {
              localTaskCompletionSource.setCancelled();
              return null;
            }
            localTaskCompletionSource.setResult(null);
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static Task whenAllResult(Collection paramCollection)
  {
    whenAll(paramCollection).onSuccess(new Continuation()
    {
      public List then(Task paramAnonymousTask)
        throws Exception
      {
        if (val$tasks.size() == 0) {
          return Collections.emptyList();
        }
        paramAnonymousTask = new ArrayList();
        Iterator localIterator = val$tasks.iterator();
        while (localIterator.hasNext()) {
          paramAnonymousTask.add(((Task)localIterator.next()).getResult());
        }
        return paramAnonymousTask;
      }
    });
  }
  
  public static Task whenAny(Collection paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task paramAnonymousTask)
        {
          if (val$isAnyTaskComplete.compareAndSet(false, true)) {
            localTaskCompletionSource.setResult(paramAnonymousTask);
          } else {
            paramAnonymousTask.getError();
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static Task whenAnyResult(Collection paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task paramAnonymousTask)
        {
          if (val$isAnyTaskComplete.compareAndSet(false, true)) {
            localTaskCompletionSource.setResult(paramAnonymousTask);
          } else {
            paramAnonymousTask.getError();
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public Task cast()
  {
    return this;
  }
  
  public Task continueWhile(Callable paramCallable, Continuation paramContinuation)
  {
    return continueWhile(paramCallable, paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public Task continueWhile(Callable paramCallable, Continuation paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWhile(paramCallable, paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task continueWhile(Callable paramCallable, Continuation paramContinuation, Executor paramExecutor)
  {
    return continueWhile(paramCallable, paramContinuation, paramExecutor, null);
  }
  
  public Task continueWhile(final Callable paramCallable, final Continuation paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final Capture localCapture = new Capture();
    localCapture.execute(new Continuation()
    {
      public Task then(Task paramAnonymousTask)
        throws Exception
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (((Boolean)paramCallable.call()).booleanValue()) {
          return Task.forResult(null).onSuccessTask(paramContinuation, paramExecutor).onSuccessTask((Continuation)localCapture.get(), paramExecutor);
        }
        return Task.forResult(null);
      }
    });
    return makeVoid().continueWithTask((Continuation)localCapture.get(), paramExecutor);
  }
  
  public Task continueWith(Continuation paramContinuation)
  {
    return continueWith(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public Task continueWith(Continuation paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWith(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task continueWith(Continuation paramContinuation, Executor paramExecutor)
  {
    return continueWith(paramContinuation, paramExecutor, null);
  }
  
  public Task continueWith(final Continuation paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    Object localObject = lock;
    try
    {
      boolean bool = isCompleted();
      if (!bool) {
        continuations.add(new Continuation()
        {
          public Void then(Task paramAnonymousTask)
          {
            Task.completeImmediately(localTaskCompletionSource, paramContinuation, paramAnonymousTask, paramExecutor, paramCancellationToken);
            return null;
          }
        });
      }
      if (bool) {
        completeImmediately(localTaskCompletionSource, paramContinuation, this, paramExecutor, paramCancellationToken);
      }
      return localTaskCompletionSource.getTask();
    }
    catch (Throwable paramContinuation)
    {
      throw paramContinuation;
    }
  }
  
  public Task continueWithTask(Continuation paramContinuation)
  {
    return continueWithTask(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public Task continueWithTask(Continuation paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWithTask(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task continueWithTask(Continuation paramContinuation, Executor paramExecutor)
  {
    return continueWithTask(paramContinuation, paramExecutor, null);
  }
  
  public Task continueWithTask(final Continuation paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    Object localObject = lock;
    try
    {
      boolean bool = isCompleted();
      if (!bool) {
        continuations.add(new Continuation()
        {
          public Void then(Task paramAnonymousTask)
          {
            Task.completeAfterTask(localTaskCompletionSource, paramContinuation, paramAnonymousTask, paramExecutor, paramCancellationToken);
            return null;
          }
        });
      }
      if (bool) {
        completeAfterTask(localTaskCompletionSource, paramContinuation, this, paramExecutor, paramCancellationToken);
      }
      return localTaskCompletionSource.getTask();
    }
    catch (Throwable paramContinuation)
    {
      throw paramContinuation;
    }
  }
  
  public Exception getError()
  {
    Object localObject = lock;
    try
    {
      if (error != null)
      {
        errorHasBeenObserved = true;
        if (unobservedErrorNotifier != null)
        {
          unobservedErrorNotifier.setObserved();
          unobservedErrorNotifier = null;
        }
      }
      Exception localException = error;
      return localException;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object getResult()
  {
    Object localObject1 = lock;
    try
    {
      Object localObject2 = result;
      return localObject2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCancelled()
  {
    Object localObject = lock;
    try
    {
      boolean bool = cancelled;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCompleted()
  {
    Object localObject = lock;
    try
    {
      boolean bool = complete;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isFaulted()
  {
    Object localObject = lock;
    for (;;)
    {
      try
      {
        if (getError() != null)
        {
          bool = true;
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public Task makeVoid()
  {
    continueWithTask(new Continuation()
    {
      public Task then(Task paramAnonymousTask)
        throws Exception
      {
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        return Task.forResult(null);
      }
    });
  }
  
  public Task onSuccess(Continuation paramContinuation)
  {
    return onSuccess(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public Task onSuccess(Continuation paramContinuation, CancellationToken paramCancellationToken)
  {
    return onSuccess(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task onSuccess(Continuation paramContinuation, Executor paramExecutor)
  {
    return onSuccess(paramContinuation, paramExecutor, null);
  }
  
  public Task onSuccess(final Continuation paramContinuation, Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    continueWithTask(new Continuation()
    {
      public Task then(Task paramAnonymousTask)
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        return paramAnonymousTask.continueWith(paramContinuation);
      }
    }, paramExecutor);
  }
  
  public Task onSuccessTask(Continuation paramContinuation)
  {
    return onSuccessTask(paramContinuation, IMMEDIATE_EXECUTOR);
  }
  
  public Task onSuccessTask(Continuation paramContinuation, CancellationToken paramCancellationToken)
  {
    return onSuccessTask(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task onSuccessTask(Continuation paramContinuation, Executor paramExecutor)
  {
    return onSuccessTask(paramContinuation, paramExecutor, null);
  }
  
  public Task onSuccessTask(final Continuation paramContinuation, Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    continueWithTask(new Continuation()
    {
      public Task then(Task paramAnonymousTask)
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        return paramAnonymousTask.continueWithTask(paramContinuation);
      }
    }, paramExecutor);
  }
  
  boolean trySetCancelled()
  {
    Object localObject = lock;
    try
    {
      if (complete) {
        return false;
      }
      complete = true;
      cancelled = true;
      lock.notifyAll();
      runContinuations();
      return true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  boolean trySetError(Exception paramException)
  {
    Object localObject = lock;
    try
    {
      if (complete) {
        return false;
      }
      complete = true;
      error = paramException;
      errorHasBeenObserved = false;
      lock.notifyAll();
      runContinuations();
      if ((!errorHasBeenObserved) && (getUnobservedExceptionHandler() != null)) {
        unobservedErrorNotifier = new UnobservedErrorNotifier(this);
      }
      return true;
    }
    catch (Throwable paramException)
    {
      throw paramException;
    }
  }
  
  boolean trySetResult(Object paramObject)
  {
    Object localObject = lock;
    try
    {
      if (complete) {
        return false;
      }
      complete = true;
      result = paramObject;
      lock.notifyAll();
      runContinuations();
      return true;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void waitForCompletion()
    throws InterruptedException
  {
    Object localObject = lock;
    try
    {
      if (!isCompleted()) {
        lock.wait();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean waitForCompletion(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Object localObject = lock;
    try
    {
      if (!isCompleted()) {
        lock.wait(paramTimeUnit.toMillis(paramLong));
      }
      boolean bool = isCompleted();
      return bool;
    }
    catch (Throwable paramTimeUnit)
    {
      throw paramTimeUnit;
    }
  }
  
  public class TaskCompletionSource
    extends TaskCompletionSource<TResult>
  {
    TaskCompletionSource() {}
  }
  
  public static abstract interface UnobservedExceptionHandler
  {
    public abstract void unobservedException(Task paramTask, UnobservedTaskException paramUnobservedTaskException);
  }
}
