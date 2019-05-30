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
import and.conachegroup.dramaitalia.app.Announced;
import and.conachegroup.dramaitalia.app.LatestDramaFilm;
import and.conachegroup.dramaitalia.tasks.LatestAnnouncedTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestAnnouncedFragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Announced> mLatestAnnouncedList;
    private LatestAnnouncedAdapter mAdapter;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private MenuItem mSortByDateItem;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(and.conachegroup.dramaitalia.R.layout.fragment_list, container, false);

        mContext = getActivity();
        setHasOptionsMenu(true);

        if (getActivity() != null) mSwipeLayout = getActivity().findViewById(R.id.swipe_container);

        mRecyclerView = view.findViewById(and.conachegroup.dramaitalia.R.id.list);
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

        RelativeLayout loading = view.findViewById(and.conachegroup.dramaitalia.R.id.loading);
        RelativeLayout error = view.findViewById(and.conachegroup.dramaitalia.R.id.error);

        if (mRecyclerView.getAdapter() == null) {
            new LatestAnnouncedTask(mContext, this, loading, error)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        mSortByDateItem = menu.findItem(and.conachegroup.dramaitalia.R.id.sort_by_date);
        mSortByDateItem = menu.findItem(and.conachegroup.dramaitalia.R.id.sort_by_date);
        mSortByDateItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(!mSearchView.isIconified()) {
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(true);
                }
                mAdapter.setFilter(Utils.sortLatestById(mLatestAnnouncedList));
                return true;
            }
        });

        mSearchItem = menu.findItem(and.conachegroup.dramaitalia.R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mLatestAnnouncedList);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Announced> filteredLatestAnnouncedList =
                filter(mLatestAnnouncedList, newText);
        mAdapter.setFilter(filteredLatestAnnouncedList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<Announced> filter(List<Announced> dramas, String query) {
        query = query.toLowerCase();
        final List<Announced> filteredLatestAnnounced = new ArrayList<>();
        for (Announced drama : dramas) {
            final String text = drama.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredLatestAnnounced.add(drama);
            }
        }
        return filteredLatestAnnounced;
    }

    public void setupListView(List<Announced> latestAnnouncedList) {
        mAdapter = new LatestAnnouncedAdapter(mContext, latestAnnouncedList);
        mLatestAnnouncedList = latestAnnouncedList;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByDateItem != null) {
                mSearchItem.setVisible(true);
                mSortByDateItem.setVisible(true);
            }
        }
    }

    private class LatestAnnouncedAdapter
            extends RecyclerView.Adapter<LatestAnnouncedAdapter.LatestAnnouncedViewHolder> {

        private Context mContext;
        private List<? extends LatestDramaFilm> mLatestAnnouncedList;

        private LatestAnnouncedAdapter(Context context,
                                       List<? extends LatestDramaFilm> latestAnnouncedList) {
            mContext = context;
            mLatestAnnouncedList = latestAnnouncedList;
        }

        @Override @NonNull
        public LatestAnnouncedAdapter.LatestAnnouncedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                                   int viewType) {
            View view = LayoutInflater.from(mContext).inflate(and.conachegroup.dramaitalia.R.layout.announced_list_item,
                    parent, false);
            return new LatestAnnouncedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LatestAnnouncedAdapter.LatestAnnouncedViewHolder holder,
                                     int position) {
            final Announced item = getItem(position);

            // set date
            holder.dateTxt.setText(item.getDate());

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set genres
            String genreText = getString(and.conachegroup.dramaitalia.R.string.genre) + item.getGenre();
            holder.genreTxt.setText(genreText);

            // set type
            holder.typeTxt.setText(item.getType());

            // set fansub
            holder.fansubTxt.setText(item.getFansub());

            // set country
            holder.countryImg.setImageDrawable(Utils.getDrawableFromCountryId(mContext,
                    Utils.getCountryId(item.getCountry())));

            // set link
            if (Utils.isValidUrl(item.getLink())) {
                holder.linkImg.setVisibility(View.VISIBLE);
            } else {
                holder.linkImg.setVisibility(View.GONE);
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    builder.setToolbarColor(ContextCompat.getColor(mContext, and.conachegroup.dramaitalia.R.color.colorPrimary));
                    try {
                        customTabsIntent.launchUrl(mContext, Uri.parse(item.getLink()));
                    } catch (Exception e) {
                        if (!Utils.isValidUrl(item.getLink())) {
                            Toast.makeText(mContext, and.conachegroup.dramaitalia.R.string.no_link_found, Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public Announced getItem(int position) {
            return (Announced) mLatestAnnouncedList.get(position);
        }

        @Override
        public int getItemCount() {
            return mLatestAnnouncedList.size();
        }

        class LatestAnnouncedViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            TextView titleTxt;
            TextView genreTxt;
            TextView typeTxt;
            TextView dateTxt;
            TextView fansubTxt;
            AppCompatImageView linkImg;
            AppCompatImageView countryImg;

            LatestAnnouncedViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(and.conachegroup.dramaitalia.R.id.cardView);
                titleTxt = itemView.findViewById(and.conachegroup.dramaitalia.R.id.title);
                genreTxt = itemView.findViewById(and.conachegroup.dramaitalia.R.id.genre);
                typeTxt = itemView.findViewById(and.conachegroup.dramaitalia.R.id.type);
                dateTxt = itemView.findViewById(and.conachegroup.dramaitalia.R.id.date);
                fansubTxt = itemView.findViewById(and.conachegroup.dramaitalia.R.id.fansub);
                linkImg = itemView.findViewById(and.conachegroup.dramaitalia.R.id.link);
                countryImg = itemView.findViewById(and.conachegroup.dramaitalia.R.id.country);
            }
        }

        public void setFilter(List<? extends LatestDramaFilm> latestAnnounced) {
            mAdapter = new LatestAnnouncedAdapter(mContext, latestAnnounced);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
