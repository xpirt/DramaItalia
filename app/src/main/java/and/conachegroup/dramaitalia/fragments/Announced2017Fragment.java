package and.conachegroup.dramaitalia.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import and.conachegroup.dramaitalia.tasks.Announced2017Task;
import and.conachegroup.dramaitalia.utils.Utils;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Announced2017Fragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Announced> mAnnounced2017List;
    private Announced2017Adapter mAdapter;
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
            new Announced2017Task(this, loading, error)
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
                mAdapter.setFilter(Utils.sortLatestById(mAnnounced2017List));
                return true;
            }
        });

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mAnnounced2017List);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Announced> filteredAnnounced2017List =
                filter(mAnnounced2017List, newText);
        mAdapter.setFilter(filteredAnnounced2017List);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<Announced> filter(List<Announced> dramas, String query) {
        query = query.toLowerCase();
        final List<Announced> filteredAnnounced2017 = new ArrayList<>();
        for (Announced drama : dramas) {
            final String text = drama.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredAnnounced2017.add(drama);
            }
        }
        return filteredAnnounced2017;
    }

    public void setupListView(List<Announced> announced2017List) {
        mAdapter = new Announced2017Adapter(mContext, announced2017List);
        mAnnounced2017List = announced2017List;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByDateItem != null) {
                mSearchItem.setVisible(true);
                mSortByDateItem.setVisible(true);
            }
        }
    }

    private class Announced2017Adapter
            extends RecyclerView.Adapter<Announced2017Adapter.Announced2017ViewHolder> {

        private Context mContext;
        private List<? extends LatestDramaFilm> mAnnounced2017List;

        private Announced2017Adapter(Context context,
                                       List<? extends LatestDramaFilm> announced2017List) {
            mContext = context;
            mAnnounced2017List = announced2017List;
        }

        @Override @NonNull
        public Announced2017Adapter.Announced2017ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                                   int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.announced_list_item,
                    parent, false);
            return new Announced2017ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Announced2017Adapter.Announced2017ViewHolder holder,
                                     int position) {
            final Announced item = getItem(position);

            // set date
            holder.dateTxt.setText(item.getDate());

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set genres
            String genreText = getString(R.string.genre) + item.getGenre();
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
                    builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    try {
                        customTabsIntent.launchUrl(mContext, Uri.parse(item.getLink()));
                    } catch (Exception e) {
                        if (!Utils.isValidUrl(item.getLink())) {
                            Toast.makeText(mContext, R.string.no_link_found, Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public Announced getItem(int position) {
            return (Announced) mAnnounced2017List.get(position);
        }

        @Override
        public int getItemCount() {
            return mAnnounced2017List.size();
        }

        class Announced2017ViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            TextView titleTxt;
            TextView genreTxt;
            TextView typeTxt;
            TextView dateTxt;
            TextView fansubTxt;
            AppCompatImageView linkImg;
            AppCompatImageView countryImg;

            Announced2017ViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.cardView);
                titleTxt = itemView.findViewById(R.id.title);
                genreTxt = itemView.findViewById(R.id.genre);
                typeTxt = itemView.findViewById(R.id.type);
                dateTxt = itemView.findViewById(R.id.date);
                fansubTxt = itemView.findViewById(R.id.fansub);
                linkImg = itemView.findViewById(R.id.link);
                countryImg = itemView.findViewById(R.id.country);
            }
        }

        public void setFilter(List<? extends LatestDramaFilm> announced2017) {
            mAdapter = new Announced2017Adapter(mContext, announced2017);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
