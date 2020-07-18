package androidx.viewpager2.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public final class FragmentViewHolder
  extends RecyclerView.ViewHolder
{
  private FragmentViewHolder(FrameLayout paramFrameLayout)
  {
    super(paramFrameLayout);
  }
  
  static FragmentViewHolder create(ViewGroup paramViewGroup)
  {
    paramViewGroup = new FrameLayout(paramViewGroup.getContext());
    paramViewGroup.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    paramViewGroup.setId(ViewCompat.generateViewId());
    paramViewGroup.setSaveEnabled(false);
    return new FragmentViewHolder(paramViewGroup);
  }
  
  FrameLayout getContainer()
  {
    return (FrameLayout)itemView;
  }
}
