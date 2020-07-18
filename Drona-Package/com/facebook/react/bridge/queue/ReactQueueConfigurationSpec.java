package com.facebook.react.bridge.queue;

import android.os.Build.VERSION;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;

public class ReactQueueConfigurationSpec
{
  private static final long LEGACY_STACK_SIZE_BYTES = 2000000L;
  private final MessageQueueThreadSpec mJSQueueThreadSpec;
  private final MessageQueueThreadSpec mNativeModulesQueueThreadSpec;
  
  private ReactQueueConfigurationSpec(MessageQueueThreadSpec paramMessageQueueThreadSpec1, MessageQueueThreadSpec paramMessageQueueThreadSpec2)
  {
    mNativeModulesQueueThreadSpec = paramMessageQueueThreadSpec1;
    mJSQueueThreadSpec = paramMessageQueueThreadSpec2;
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static ReactQueueConfigurationSpec createDefault()
  {
    MessageQueueThreadSpec localMessageQueueThreadSpec;
    if (Build.VERSION.SDK_INT < 21) {
      localMessageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules", 2000000L);
    } else {
      localMessageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules");
    }
    return builder().setJSQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec("js")).setNativeModulesQueueThreadSpec(localMessageQueueThreadSpec).build();
  }
  
  public MessageQueueThreadSpec getJSQueueThreadSpec()
  {
    return mJSQueueThreadSpec;
  }
  
  public MessageQueueThreadSpec getNativeModulesQueueThreadSpec()
  {
    return mNativeModulesQueueThreadSpec;
  }
  
  public static class Builder
  {
    @Nullable
    private MessageQueueThreadSpec mJSQueueSpec;
    @Nullable
    private MessageQueueThreadSpec mNativeModulesQueueSpec;
    
    public Builder() {}
    
    public ReactQueueConfigurationSpec build()
    {
      return new ReactQueueConfigurationSpec((MessageQueueThreadSpec)Assertions.assertNotNull(mNativeModulesQueueSpec), (MessageQueueThreadSpec)Assertions.assertNotNull(mJSQueueSpec), null);
    }
    
    public Builder setJSQueueThreadSpec(MessageQueueThreadSpec paramMessageQueueThreadSpec)
    {
      boolean bool;
      if (mJSQueueSpec == null) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.assertCondition(bool, "Setting JS queue multiple times!");
      mJSQueueSpec = paramMessageQueueThreadSpec;
      return this;
    }
    
    public Builder setNativeModulesQueueThreadSpec(MessageQueueThreadSpec paramMessageQueueThreadSpec)
    {
      boolean bool;
      if (mNativeModulesQueueSpec == null) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.assertCondition(bool, "Setting native modules queue spec multiple times!");
      mNativeModulesQueueSpec = paramMessageQueueThreadSpec;
      return this;
    }
  }
}