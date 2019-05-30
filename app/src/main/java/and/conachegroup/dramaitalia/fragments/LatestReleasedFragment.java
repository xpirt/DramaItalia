package and.conachegroup.dramaitalia.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatImageView;
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

import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.LatestDramaFilm;
import and.conachegroup.dramaitalia.app.LatestReleased;
import and.conachegroup.dramaitalia.tasks.LatestReleasedTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestReleasedFragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<LatestReleased> mLatestReleasedList;
    private LatestReleasedAdapter mAdapter;
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
            new LatestReleasedTask(mContext, this, loading, error)
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
                mAdapter.setFilter(Utils.sortLatestById(mLatestReleasedList));
                return true;
            }
        });

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mLatestReleasedList);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<LatestReleased> filteredLatestReleasedList =
                filter(mLatestReleasedList, newText);
        mAdapter.setFilter(filteredLatestReleasedList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<LatestReleased> filter(List<LatestReleased> dramas, String query) {
        query = query.toLowerCase();
        final List<LatestReleased> filteredLatestReleased = new ArrayList<>();
        for (LatestReleased drama : dramas) {
            final String text = drama.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredLatestReleased.add(drama);
            }
        }
        return filteredLatestReleased;
    }

    public void setupListView(List<LatestReleased> latestReleasedList) {
        mAdapter = new LatestReleasedAdapter(mContext, latestReleasedList);
        mLatestReleasedList = latestReleasedList;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByDateItem != null) {
                mSearchItem.setVisible(true);
                mSortByDateItem.setVisible(true);
            }
        }
    }

    private class LatestReleasedAdapter
            extends RecyclerView.Adapter<LatestReleasedAdapter.LatestReleasedViewHolder> {

        private Context mContext;
        private List<? extends LatestDramaFilm> mLatestReleasedList;

        private LatestReleasedAdapter(Context context,
                                      List<? extends LatestDramaFilm> latestReleasedList) {
            mContext = context;
            mLatestReleasedList = latestReleasedList;
        }

        @Override @NonNull
        public LatestReleasedAdapter.LatestReleasedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                                 int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.latest_released_list_item,
                    parent, false);
            return new LatestReleasedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LatestReleasedAdapter.LatestReleasedViewHolder holder,
                                     int position) {
            final LatestReleased item = getItem(position);

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set episode
            String episodeText = getString(R.string.episode) + item.getEpisode();
            holder.episodeTxt.setText(episodeText);

            // set fansub
            holder.fansubTxt.setText(item.getFansub());

            // set date
            holder.dateTxt.setText(item.getDate());

            // set country
            holder.countryImg.setImageDrawable(Utils.getDrawableFromCountryId(mContext,
                    Utils.getCountryId(item.getCountry())));
        }

        public LatestReleased getItem(int position) {
            return (LatestReleased) mLatestReleasedList.get(position);
        }

        @Override
        public int getItemCount() {
            return mLatestReleasedList.size();
        }

        class LatestReleasedViewHolder extends RecyclerView.ViewHolder {

            TextView titleTxt;
            TextView episodeTxt;
            TextView dateTxt;
            TextView fansubTxt;
            AppCompatImageView countryImg;

            LatestReleasedViewHolder(View itemView) {
                super(itemView);

                titleTxt = itemView.findViewById(R.id.title);
                episodeTxt = itemView.findViewById(R.id.episode);
                dateTxt = itemView.findViewById(R.id.date);
                fansubTxt = itemView.findViewById(R.id.fansub);
                countryImg = itemView.findViewById(R.id.country);
            }
        }

        public void setFilter(List<? extends LatestDramaFilm> latestReleased) {
            mAdapter = new LatestReleasedAdapter(mContext, latestReleased);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
