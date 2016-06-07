package com.infmme.readilyapp.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.Adapter
    .ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.storable.AbstractTocReference;

import java.util.List;

/**
 * Created with love, by infm dated on 6/5/16.
 */

public class BookNavigationAdapter
    extends
    ExpandableRecyclerAdapter<BookNavigationAdapter.ParentPartViewHolder,
        BookNavigationAdapter.ChildPartViewHolder> {


  private LayoutInflater mInflater;
  private OnItemClickListener mOnItemClickListener;

  /**
   * Primary constructor. Sets up {@link #mParentItemList} and
   * {@link #mItemList}.
   * <p>
   * Changes to {@link #mParentItemList} should be made through add/remove
   * methods in
   * {@link ExpandableRecyclerAdapter}
   *
   * @param parentItemList List of all {@link ParentListItem} objects to be
   *                       displayed in the RecyclerView that this
   *                       adapter is linked to
   */
  public BookNavigationAdapter(
      Context context,
      OnItemClickListener onItemClickListener,
      @NonNull List<? extends ParentListItem> parentItemList) {
    super(parentItemList);
    mInflater = LayoutInflater.from(context);
    mOnItemClickListener = onItemClickListener;
  }

  @Override
  public ParentPartViewHolder onCreateParentViewHolder(
      ViewGroup parentViewGroup) {
    View parentPartView = mInflater.inflate(
        R.layout.bookpart_item_parent, parentViewGroup, false);
    return new ParentPartViewHolder(parentPartView);
  }

  @Override
  public ChildPartViewHolder onCreateChildViewHolder(
      ViewGroup childViewGroup) {
    View childPartView = mInflater.inflate(
        R.layout.bookpart_item_child, childViewGroup, false);
    return new ChildPartViewHolder(childPartView);
  }

  @Override
  public void onBindParentViewHolder(
      ParentPartViewHolder parentViewHolder, int position,
      ParentListItem parentListItem) {
    final ParentPart parentPart = (ParentPart) parentListItem;
    parentViewHolder.bind(parentPart);

    // If it doesn't have any children - it's actually a child
    if (!parentPart.hasChildren()) {
      parentViewHolder.mContainerView.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mOnItemClickListener
                  .onBookPartClicked(v, parentPart.getParentReference());
            }
          });
    } else {
      parentViewHolder.setDefaultOnClickListener();
    }
  }

  @Override
  public void onBindChildViewHolder(
      ChildPartViewHolder childViewHolder, int position,
      Object childListItem) {
    final AbstractTocReference tocReference = (AbstractTocReference)
        childListItem;
    childViewHolder.bind(tocReference);
    childViewHolder.mContainerView.setClickable(true);
    childViewHolder.mContainerView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mOnItemClickListener.onBookPartClicked(v, tocReference);
          }
        });
  }

  public static class ParentPart implements ParentListItem {

    private AbstractTocReference mParentReference;
    private List mReferenceList;
    private String mTitle;

    public ParentPart(AbstractTocReference parentReference) {
      mParentReference = parentReference;
      mReferenceList = parentReference.getChildren();
      mTitle = parentReference.getTitle();
    }

    @Override
    public List<?> getChildItemList() {
      return mReferenceList;
    }

    @Override
    public boolean isInitiallyExpanded() {
      return false;
    }

    public AbstractTocReference getParentReference() {
      return mParentReference;
    }

    public String getTitle() {
      return mTitle;
    }

    public boolean hasChildren() {
      return mReferenceList != null && !mReferenceList.isEmpty();
    }
  }

  public class ParentPartViewHolder extends ParentViewHolder {

    private View mContainerView;
    private TextView mParentTextView;
    private ImageView mImageView;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public ParentPartViewHolder(View itemView) {
      super(itemView);
      mContainerView = itemView;
      mParentTextView = (TextView) itemView.findViewById(
          R.id.bookpart_parent_item_text_view);
      mImageView = (ImageView) itemView.findViewById(
          R.id.bookpart_parent_item_image_button);
      setDefaultOnClickListener();
    }

    public void bind(ParentPart parentPart) {
      mParentTextView.setText(parentPart.getTitle());
      if (parentPart.hasChildren()) {
        mImageView.setVisibility(View.VISIBLE);
      } else {
        mImageView.setVisibility(View.GONE);
      }
    }

    public void setDefaultOnClickListener() {
      mContainerView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Context c = v.getContext();
          Drawable d;
          if (isExpanded()) {
            collapseView();
            d = ContextCompat
                .getDrawable(c, R.drawable.ic_expand_more_black_36dp);
          } else {
            expandView();
            d = ContextCompat
                .getDrawable(c, R.drawable.ic_expand_less_black_36dp);
          }
          mImageView.setImageDrawable(d);
        }
      });
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
      return false;
    }
  }

  public class ChildPartViewHolder extends ChildViewHolder {

    private View mContainerView;
    private TextView mChildTextView;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public ChildPartViewHolder(View itemView) {
      super(itemView);
      mContainerView = itemView;
      mChildTextView = (TextView) itemView.findViewById(
          R.id.bookpart_child_item_text_view);
    }

    public void bind(AbstractTocReference tocReference) {
      mChildTextView.setText(tocReference.getTitle());
    }
  }

  public interface OnItemClickListener {
    void onBookPartClicked(View v, AbstractTocReference tocReference);
  }
}
