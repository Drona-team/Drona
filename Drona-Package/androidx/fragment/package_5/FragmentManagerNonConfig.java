package androidx.fragment.package_5;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStore;
import java.util.Collection;
import java.util.Map;

@Deprecated
public class FragmentManagerNonConfig
{
  @Nullable
  private final Map<String, androidx.fragment.app.FragmentManagerNonConfig> mChildNonConfigs;
  @Nullable
  private final Collection<androidx.fragment.app.Fragment> mFragments;
  @Nullable
  private final Map<String, ViewModelStore> mViewModelStores;
  
  FragmentManagerNonConfig(Collection paramCollection, Map paramMap1, Map paramMap2)
  {
    mFragments = paramCollection;
    mChildNonConfigs = paramMap1;
    mViewModelStores = paramMap2;
  }
  
  Map getChildNonConfigs()
  {
    return mChildNonConfigs;
  }
  
  Collection getFragments()
  {
    return mFragments;
  }
  
  Map getViewModelStores()
  {
    return mViewModelStores;
  }
  
  boolean isRetaining(Fragment paramFragment)
  {
    if (mFragments == null) {
      return false;
    }
    return mFragments.contains(paramFragment);
  }
}
