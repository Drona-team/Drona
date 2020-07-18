package androidx.recyclerview.widget;

import java.util.List;

public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder>
  extends RecyclerView.Adapter<VH>
{
  final AsyncListDiffer<T> mDiffer;
  private final AsyncListDiffer.ListListener<T> mListener = new AsyncListDiffer.ListListener()
  {
    public void onCurrentListChanged(List paramAnonymousList1, List paramAnonymousList2)
    {
      ListAdapter.this.onCurrentListChanged(paramAnonymousList1, paramAnonymousList2);
    }
  };
  
  protected ListAdapter(AsyncDifferConfig paramAsyncDifferConfig)
  {
    mDiffer = new AsyncListDiffer(new AdapterListUpdateCallback(this), paramAsyncDifferConfig);
    mDiffer.addListListener(mListener);
  }
  
  protected ListAdapter(DiffUtil.ItemCallback paramItemCallback)
  {
    mDiffer = new AsyncListDiffer(new AdapterListUpdateCallback(this), new AsyncDifferConfig.Builder(paramItemCallback).build());
    mDiffer.addListListener(mListener);
  }
  
  public List getCurrentList()
  {
    return mDiffer.getCurrentList();
  }
  
  protected Object getItem(int paramInt)
  {
    return mDiffer.getCurrentList().get(paramInt);
  }
  
  public int getItemCount()
  {
    return mDiffer.getCurrentList().size();
  }
  
  public void onCurrentListChanged(List paramList1, List paramList2) {}
  
  public void submitList(List paramList)
  {
    mDiffer.submitList(paramList);
  }
  
  public void submitList(List paramList, Runnable paramRunnable)
  {
    mDiffer.submitList(paramList, paramRunnable);
  }
}
