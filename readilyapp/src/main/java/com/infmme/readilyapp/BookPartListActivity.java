package com.infmme.readilyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.Adapter
    .ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.infmme.readilyapp.util.BaseActivity;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of BookParts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookPartDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookPartListActivity extends BaseActivity {

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bookpart_list);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action",
                      Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });
    // Show the Up button in the action bar.
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    View recyclerView = findViewById(R.id.bookpart_list);
    assert recyclerView != null;

    Bundle bundle = getIntent().getExtras();
    TableOfContents toc = (TableOfContents) bundle.getSerializable(
        "TableOfContents");

    assert toc != null;
    setupRecyclerView((RecyclerView) recyclerView, toc);

    if (findViewById(R.id.bookpart_detail_container) != null) {
      // The detail container view will be present only in the
      // large-screen layouts (res/values-w900dp).
      // If this view is present, then the
      // activity should be in two-pane mode.
      mTwoPane = true;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView,
                                 TableOfContents toc) {
    final List<TOCReference> tocReferences = toc.getTocReferences();

    List<ParentPart> parentParts = new ArrayList<>();
    for (int i = 0; i < tocReferences.size(); i++) {
      parentParts.add(new ParentPart(tocReferences.get(i)));
    }
    recyclerView.setAdapter(
        new SimpleItemRecyclerViewAdapter(this, parentParts));
  }

  public class ParentPart implements ParentListItem {

    private List mReferenceList;
    private String mTitle;

    public ParentPart(TOCReference parentReference) {
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

    public String getTitle() {
      return mTitle;
    }
  }

  public class ParentPartViewHolder extends ParentViewHolder {

    private TextView mParentTextView;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public ParentPartViewHolder(View itemView) {
      super(itemView);
      mParentTextView = (TextView) itemView.findViewById(
          R.id.bookpart_item_text_view);
    }

    public void bind(ParentPart parentPart) {
      mParentTextView.setText(parentPart.getTitle());
    }
  }

  public class ChildPartViewHolder extends ChildViewHolder {

    private TextView mChildTextView;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public ChildPartViewHolder(View itemView) {
      super(itemView);
      mChildTextView = (TextView) itemView.findViewById(
          R.id.bookpart_item_text_view);
    }

    public void bind(TOCReference tocReference) {
      mChildTextView.setText(tocReference.getTitle());
    }
  }

  public class SimpleItemRecyclerViewAdapter
      extends
      ExpandableRecyclerAdapter<ParentPartViewHolder, ChildPartViewHolder> {

    private LayoutInflater mInflater;

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
    public SimpleItemRecyclerViewAdapter(
        Context context,
        @NonNull List<? extends ParentListItem> parentItemList) {
      super(parentItemList);
      mInflater = LayoutInflater.from(context);
    }

    @Override
    public ParentPartViewHolder onCreateParentViewHolder(
        ViewGroup parentViewGroup) {
      View parentPartView = mInflater.inflate(
          R.layout.bookpart_item, parentViewGroup, false);
      return new ParentPartViewHolder(parentPartView);
    }

    @Override
    public ChildPartViewHolder onCreateChildViewHolder(
        ViewGroup childViewGroup) {
      View childPartView = mInflater.inflate(
          R.layout.bookpart_item, childViewGroup, false);
      return new ChildPartViewHolder(childPartView);
    }

    @Override
    public void onBindParentViewHolder(
        ParentPartViewHolder parentViewHolder, int position,
        ParentListItem parentListItem) {
      ParentPart parentPart = (ParentPart) parentListItem;
      parentViewHolder.bind(parentPart);
    }

    @Override
    public void onBindChildViewHolder(
        ChildPartViewHolder childViewHolder, int position,
        Object childListItem) {
      TOCReference tocReference = (TOCReference) childListItem;
      childViewHolder.bind(tocReference);
    }

/*
    private final List<DummyContent.DummyItem> mValues;

    public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
      mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.bookpart_list_content, parent,
                                         false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      holder.mItem = mValues.get(position);
      holder.mIdView.setText(mValues.get(position).id);
      holder.mContentView.setText(mValues.get(position).content);

      holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(BookPartDetailFragment.ARG_ITEM_ID,
                                holder.mItem.id);
            BookPartDetailFragment fragment = new BookPartDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.bookpart_detail_container,
                                                fragment)
                                       .commit();
          } else {
            Context context = v.getContext();
            Intent intent = new Intent(context, BookPartDetailActivity.class);
            intent.putExtra(BookPartDetailFragment.ARG_ITEM_ID,
                            holder.mItem.id);

            context.startActivity(intent);
          }
        }
      });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public DummyContent.DummyItem mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.id);
      mContentView = (TextView) view.findViewById(R.id.content);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
*/
  }
}
