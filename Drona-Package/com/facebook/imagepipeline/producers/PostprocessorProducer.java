package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessorRunner;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class PostprocessorProducer
  implements Producer<CloseableReference<CloseableImage>>
{
  public static final String NAME = "PostprocessorProducer";
  @VisibleForTesting
  static final String POSTPROCESSOR = "Postprocessor";
  private final PlatformBitmapFactory mBitmapFactory;
  private final Executor mExecutor;
  private final Producer<CloseableReference<CloseableImage>> mInputProducer;
  
  public PostprocessorProducer(Producer paramProducer, PlatformBitmapFactory paramPlatformBitmapFactory, Executor paramExecutor)
  {
    mInputProducer = ((Producer)Preconditions.checkNotNull(paramProducer));
    mBitmapFactory = paramPlatformBitmapFactory;
    mExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a7 = a6\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private class PostprocessorConsumer
    extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>>
  {
    @GuardedBy("PostprocessorConsumer.this")
    private boolean mIsClosed;
    @GuardedBy("PostprocessorConsumer.this")
    private boolean mIsDirty = false;
    @GuardedBy("PostprocessorConsumer.this")
    private boolean mIsPostProcessingRunning = false;
    private final ProducerListener mListener;
    private final Postprocessor mPostprocessor;
    private final String mRequestId;
    @Nullable
    @GuardedBy("PostprocessorConsumer.this")
    private CloseableReference<CloseableImage> mSourceImageRef = null;
    @GuardedBy("PostprocessorConsumer.this")
    private int mStatus = 0;
    
    public PostprocessorConsumer(Consumer paramConsumer, ProducerListener paramProducerListener, String paramString, Postprocessor paramPostprocessor, ProducerContext paramProducerContext)
    {
      super();
      mListener = paramProducerListener;
      mRequestId = paramString;
      mPostprocessor = paramPostprocessor;
      paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
      {
        public void onCancellationRequested()
        {
          PostprocessorProducer.PostprocessorConsumer.this.maybeNotifyOnCancellation();
        }
      });
    }
    
    private void clearRunningAndStartIfDirty()
    {
      try
      {
        mIsPostProcessingRunning = false;
        boolean bool = setRunningIfDirtyAndNotRunning();
        if (bool)
        {
          submitPostprocessing();
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private boolean close()
    {
      try
      {
        if (mIsClosed) {
          return false;
        }
        CloseableReference localCloseableReference = mSourceImageRef;
        mSourceImageRef = null;
        mIsClosed = true;
        CloseableReference.closeSafely(localCloseableReference);
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void doPostprocessing(CloseableReference paramCloseableReference, int paramInt)
    {
      Preconditions.checkArgument(CloseableReference.isValid(paramCloseableReference));
      if (!shouldPostprocess((CloseableImage)paramCloseableReference.get()))
      {
        maybeNotifyOnNewResult(paramCloseableReference, paramInt);
        return;
      }
      mListener.onProducerStart(mRequestId, "PostprocessorProducer");
      Object localObject1 = null;
      Object localObject2;
      try
      {
        paramCloseableReference = paramCloseableReference.get();
        paramCloseableReference = (CloseableImage)paramCloseableReference;
        paramCloseableReference = postprocessInternal(paramCloseableReference);
        try
        {
          mListener.onProducerFinishWithSuccess(mRequestId, "PostprocessorProducer", getExtraMap(mListener, mRequestId, mPostprocessor));
          maybeNotifyOnNewResult(paramCloseableReference, paramInt);
          CloseableReference.closeSafely(paramCloseableReference);
          return;
        }
        catch (Throwable localThrowable1) {}
        CloseableReference.closeSafely(paramCloseableReference);
      }
      catch (Throwable localThrowable2)
      {
        paramCloseableReference = localThrowable1;
        localObject2 = localThrowable2;
      }
      catch (Exception paramCloseableReference)
      {
        mListener.onProducerFinishWithFailure(mRequestId, "PostprocessorProducer", paramCloseableReference, getExtraMap(mListener, mRequestId, mPostprocessor));
        maybeNotifyOnFailure(paramCloseableReference);
        CloseableReference.closeSafely(null);
        return;
      }
      throw localObject2;
    }
    
    private Map getExtraMap(ProducerListener paramProducerListener, String paramString, Postprocessor paramPostprocessor)
    {
      if (!paramProducerListener.requiresExtraMap(paramString)) {
        return null;
      }
      return ImmutableMap.of("Postprocessor", paramPostprocessor.getName());
    }
    
    private boolean isClosed()
    {
      try
      {
        boolean bool = mIsClosed;
        return bool;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void maybeNotifyOnCancellation()
    {
      if (close()) {
        getConsumer().onCancellation();
      }
    }
    
    private void maybeNotifyOnFailure(Throwable paramThrowable)
    {
      if (close()) {
        getConsumer().onFailure(paramThrowable);
      }
    }
    
    private void maybeNotifyOnNewResult(CloseableReference paramCloseableReference, int paramInt)
    {
      boolean bool = BaseConsumer.isLast(paramInt);
      if (((!bool) && (!isClosed())) || ((bool) && (close()))) {
        getConsumer().onNewResult(paramCloseableReference, paramInt);
      }
    }
    
    private CloseableReference postprocessInternal(CloseableImage paramCloseableImage)
    {
      CloseableStaticBitmap localCloseableStaticBitmap = (CloseableStaticBitmap)paramCloseableImage;
      Object localObject = localCloseableStaticBitmap.getUnderlyingBitmap();
      localObject = mPostprocessor.process((Bitmap)localObject, mBitmapFactory);
      int i = localCloseableStaticBitmap.getRotationAngle();
      int j = localCloseableStaticBitmap.getExifOrientation();
      try
      {
        paramCloseableImage = CloseableReference.of(new CloseableStaticBitmap((CloseableReference)localObject, paramCloseableImage.getQualityInfo(), i, j));
        CloseableReference.closeSafely((CloseableReference)localObject);
        return paramCloseableImage;
      }
      catch (Throwable paramCloseableImage)
      {
        CloseableReference.closeSafely((CloseableReference)localObject);
        throw paramCloseableImage;
      }
    }
    
    private boolean setRunningIfDirtyAndNotRunning()
    {
      try
      {
        if ((!mIsClosed) && (mIsDirty) && (!mIsPostProcessingRunning) && (CloseableReference.isValid(mSourceImageRef)))
        {
          mIsPostProcessingRunning = true;
          return true;
        }
        return false;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private boolean shouldPostprocess(CloseableImage paramCloseableImage)
    {
      return paramCloseableImage instanceof CloseableStaticBitmap;
    }
    
    private void submitPostprocessing()
    {
      mExecutor.execute(new Runnable()
      {
        public void run()
        {
          PostprocessorProducer.PostprocessorConsumer localPostprocessorConsumer = PostprocessorProducer.PostprocessorConsumer.this;
          try
          {
            CloseableReference localCloseableReference = mSourceImageRef;
            int i = mStatus;
            PostprocessorProducer.PostprocessorConsumer.access$302(PostprocessorProducer.PostprocessorConsumer.this, null);
            PostprocessorProducer.PostprocessorConsumer.access$502(PostprocessorProducer.PostprocessorConsumer.this, false);
            if (CloseableReference.isValid(localCloseableReference)) {
              try
              {
                PostprocessorProducer.PostprocessorConsumer.this.doPostprocessing(localCloseableReference, i);
                CloseableReference.closeSafely(localCloseableReference);
              }
              catch (Throwable localThrowable1)
              {
                CloseableReference.closeSafely(localCloseableReference);
                throw localThrowable1;
              }
            }
            PostprocessorProducer.PostprocessorConsumer.this.clearRunningAndStartIfDirty();
            return;
          }
          catch (Throwable localThrowable2)
          {
            throw localThrowable2;
          }
        }
      });
    }
    
    private void updateSourceImageRef(CloseableReference paramCloseableReference, int paramInt)
    {
      try
      {
        if (mIsClosed) {
          return;
        }
        CloseableReference localCloseableReference = mSourceImageRef;
        mSourceImageRef = CloseableReference.cloneOrNull(paramCloseableReference);
        mStatus = paramInt;
        mIsDirty = true;
        boolean bool = setRunningIfDirtyAndNotRunning();
        CloseableReference.closeSafely(localCloseableReference);
        if (bool)
        {
          submitPostprocessing();
          return;
        }
      }
      catch (Throwable paramCloseableReference)
      {
        throw paramCloseableReference;
      }
    }
    
    protected void onCancellationImpl()
    {
      maybeNotifyOnCancellation();
    }
    
    protected void onFailureImpl(Throwable paramThrowable)
    {
      maybeNotifyOnFailure(paramThrowable);
    }
    
    protected void onNewResultImpl(CloseableReference paramCloseableReference, int paramInt)
    {
      if (!CloseableReference.isValid(paramCloseableReference))
      {
        if (BaseConsumer.isLast(paramInt)) {
          maybeNotifyOnNewResult(null, paramInt);
        }
      }
      else {
        updateSourceImageRef(paramCloseableReference, paramInt);
      }
    }
  }
  
  class RepeatedPostprocessorConsumer
    extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>>
    implements RepeatedPostprocessorRunner
  {
    @GuardedBy("RepeatedPostprocessorConsumer.this")
    private boolean mIsClosed = false;
    @Nullable
    @GuardedBy("RepeatedPostprocessorConsumer.this")
    private CloseableReference<CloseableImage> mSourceImageRef = null;
    
    private RepeatedPostprocessorConsumer(PostprocessorProducer.PostprocessorConsumer paramPostprocessorConsumer, RepeatedPostprocessor paramRepeatedPostprocessor, ProducerContext paramProducerContext)
    {
      super();
      paramRepeatedPostprocessor.setCallback(this);
      paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
      {
        public void onCancellationRequested()
        {
          if (PostprocessorProducer.RepeatedPostprocessorConsumer.this.close()) {
            getConsumer().onCancellation();
          }
        }
      });
    }
    
    private boolean close()
    {
      try
      {
        if (mIsClosed) {
          return false;
        }
        CloseableReference localCloseableReference = mSourceImageRef;
        mSourceImageRef = null;
        mIsClosed = true;
        CloseableReference.closeSafely(localCloseableReference);
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void setSourceImageRef(CloseableReference paramCloseableReference)
    {
      try
      {
        if (mIsClosed) {
          return;
        }
        CloseableReference localCloseableReference = mSourceImageRef;
        mSourceImageRef = CloseableReference.cloneOrNull(paramCloseableReference);
        CloseableReference.closeSafely(localCloseableReference);
        return;
      }
      catch (Throwable paramCloseableReference)
      {
        throw paramCloseableReference;
      }
    }
    
    /* Error */
    private void updateInternal()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 32	com/facebook/imagepipeline/producers/PostprocessorProducer$RepeatedPostprocessorConsumer:mIsClosed	Z
      //   6: ifeq +6 -> 12
      //   9: aload_0
      //   10: monitorexit
      //   11: return
      //   12: aload_0
      //   13: getfield 34	com/facebook/imagepipeline/producers/PostprocessorProducer$RepeatedPostprocessorConsumer:mSourceImageRef	Lcom/facebook/common/references/CloseableReference;
      //   16: invokestatic 72	com/facebook/common/references/CloseableReference:cloneOrNull	(Lcom/facebook/common/references/CloseableReference;)Lcom/facebook/common/references/CloseableReference;
      //   19: astore_1
      //   20: aload_0
      //   21: monitorexit
      //   22: aload_0
      //   23: invokevirtual 78	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   26: aload_1
      //   27: iconst_0
      //   28: invokeinterface 84 3 0
      //   33: aload_1
      //   34: invokestatic 67	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   37: return
      //   38: astore_2
      //   39: aload_1
      //   40: invokestatic 67	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   43: aload_2
      //   44: athrow
      //   45: astore_1
      //   46: aload_0
      //   47: monitorexit
      //   48: aload_1
      //   49: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	50	0	this	RepeatedPostprocessorConsumer
      //   19	21	1	localCloseableReference	CloseableReference
      //   45	4	1	localThrowable1	Throwable
      //   38	6	2	localThrowable2	Throwable
      // Exception table:
      //   from	to	target	type
      //   22	33	38	java/lang/Throwable
      //   2	11	45	java/lang/Throwable
      //   12	22	45	java/lang/Throwable
      //   46	48	45	java/lang/Throwable
    }
    
    protected void onCancellationImpl()
    {
      if (close()) {
        getConsumer().onCancellation();
      }
    }
    
    protected void onFailureImpl(Throwable paramThrowable)
    {
      if (close()) {
        getConsumer().onFailure(paramThrowable);
      }
    }
    
    protected void onNewResultImpl(CloseableReference paramCloseableReference, int paramInt)
    {
      if (BaseConsumer.isNotLast(paramInt)) {
        return;
      }
      setSourceImageRef(paramCloseableReference);
      updateInternal();
    }
    
    public void update()
    {
      try
      {
        updateInternal();
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  class SingleUsePostprocessorConsumer
    extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>>
  {
    private SingleUsePostprocessorConsumer(PostprocessorProducer.PostprocessorConsumer paramPostprocessorConsumer)
    {
      super();
    }
    
    protected void onNewResultImpl(CloseableReference paramCloseableReference, int paramInt)
    {
      if (BaseConsumer.isNotLast(paramInt)) {
        return;
      }
      getConsumer().onNewResult(paramCloseableReference, paramInt);
    }
  }
}
