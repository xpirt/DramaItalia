package and.conachegroup.dramaitalia.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.LatestCompleted;
import and.conachegroup.dramaitalia.app.LatestDramaFilm;
import and.conachegroup.dramaitalia.tasks.LatestCompletedTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestCompletedFragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<LatestCompleted> mLatestCompletedList;
    private LatestCompletedAdapter mAdapter;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private MenuItem mSortByDateItem;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mContext = getActivity();
        setHasOptionsMenu(true);

        if (getActivity() != null) mSwipeLayout = getActivity().findViewById(R.id.swipe_container);

        mRecyclerView = view.findViewById(R.id.list);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = 0;

                if (recyclerView.getChildCount() != 0) {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop();
                }

                mSwipeLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        RelativeLayout loading = view.findViewById(R.id.loading);
        RelativeLayout error = view.findViewById(R.id.error);

        if (mRecyclerView.getAdapter() == null) {
            new LatestCompletedTask(mContext, this, loading, error)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        mSortByDateItem = menu.findItem(R.id.sort_by_date);
        mSortByDateItem = menu.findItem(R.id.sort_by_date);
        mSortByDateItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(!mSearchView.isIconified()) {
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(true);
                }
                mAdapter.setFilter(Utils.sortLatestById(mLatestCompletedList));
                return true;
            }
        });

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mLatestCompletedList);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<LatestCompleted> filteredLatestCompletedList =
                filter(mLatestCompletedList, newText);
        mAdapter.setFilter(filteredLatestCompletedList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<LatestCompleted> filter(List<LatestCompleted> dramas, String query) {
        query = query.toLowerCase();
        final List<LatestCompleted> filteredLatestCompleted = new ArrayList<>();
        for (LatestCompleted drama : dramas) {
            final String text = drama.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredLatestCompleted.add(drama);
            }
        }
        return filteredLatestCompleted;
    }

    public void setupListView(List<LatestCompleted> latestCompletedList) {
        mAdapter = new LatestCompletedAdapter(mContext, latestCompletedList);
        mLatestCompletedList = latestCompletedList;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByDateItem != null) {
                mSearchItem.setVisible(true);
                mSortByDateItem.setVisible(true);
            }
        }
    }

    private class LatestCompletedAdapter
            extends RecyclerView.Adapter<LatestCompletedAdapter.LatestCompletedViewHolder> {

        private Context mContext;
        private List<? extends LatestDramaFilm> mLatestCompletedList;

        private LatestCompletedAdapter(Context context,
                                       List<? extends LatestDramaFilm> latestCompletedList) {
            mContext = context;
            mLatestCompletedList = latestCompletedList;
        }

        @Override @NonNull
        public LatestCompletedAdapter.LatestCompletedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                                   int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.latest_completed_list_item,
                    parent, false);
            return new LatestCompletedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LatestCompletedAdapter.LatestCompletedViewHolder holder,
                                     int position) {
            final LatestCompleted item = getItem(position);

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set episodes
            String episodesText = getString(R.string.episodes) + item.getEpisodes();
            holder.episodesTxt.setText(episodesText);

            // set type
            holder.typeTxt.setText(item.getType());

            // set fansub
            holder.fansubTxt.setText(item.getFansub());

            // set date
            holder.dateTxt.setText(item.getDate());

            // set download
            Utils.setDownloadDrawable(mContext, holder.downloadImg, item.getDownload());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    try {
                        customTabsIntent.launchUrl(mContext, Uri.parse(item.getDownload()));
                    } catch (Exception e) {
                        if (!Utils.isValidUrl(item.getDownload())) {
                            Toast.makeText(mContext, R.string.no_link_found, Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public LatestCompleted getItem(int position) {
            return (LatestCompleted) mLatestCompletedList.get(position);
        }

        @Override
        public int getItemCount() {
            return mLatestCompletedList.size();
        }

        class LatestCompletedViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            TextView titleTxt;
            TextView episodesTxt;
            TextView typeTxt;
            TextView dateTxt;
            TextView fansubTxt;
            AppCompatImageView downloadImg;

            LatestCompletedViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.cardView);
                titleTxt = itemView.findViewById(R.id.title);
                episodesTxt = itemView.findViewById(R.id.episodes);
                typeTxt = itemView.findViewById(R.id.type);
                dateTxt = itemView.findViewById(R.id.date);
                fansubTxt = itemView.findViewById(R.id.fansub);
                downloadImg = itemView.findViewById(R.id.download);
            }
        }

        public void setFilter(List<? extends LatestDramaFilm> latestCompleted) {
            mAdapter = new LatestCompletedAdapter(mContext, latestCompleted);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
