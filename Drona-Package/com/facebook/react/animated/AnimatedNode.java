package com.facebook.react.animated;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import java.util.ArrayList;
import java.util.List;

abstract class AnimatedNode
{
  private static final int DEFAULT_ANIMATED_NODE_CHILD_COUNT = 1;
  public static final int INITIAL_BFS_COLOR = 0;
  int mActiveIncomingNodes = 0;
  int mBFSColor = 0;
  @Nullable
  List<AnimatedNode> mChildren;
  int mTag = -1;
  
  AnimatedNode() {}
  
  public final void addChild(AnimatedNode paramAnimatedNode)
  {
    if (mChildren == null) {
      mChildren = new ArrayList(1);
    }
    ((List)Assertions.assertNotNull(mChildren)).add(paramAnimatedNode);
    paramAnimatedNode.onAttachedToNode(this);
  }
  
  public void onAttachedToNode(AnimatedNode paramAnimatedNode) {}
  
  public void onDetachedFromNode(AnimatedNode paramAnimatedNode) {}
  
  public final void removeChild(AnimatedNode paramAnimatedNode)
  {
    if (mChildren == null) {
      return;
    }
    paramAnimatedNode.onDetachedFromNode(this);
    mChildren.remove(paramAnimatedNode);
  }
  
  public void update() {}
}
