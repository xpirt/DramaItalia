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
import and.conachegroup.dramaitalia.app.DramaFilm;
import and.conachegroup.dramaitalia.app.OtherFilm;
import and.conachegroup.dramaitalia.tasks.OtherFilmTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class OtherFilmsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<OtherFilm> mOtherFilmList;
    private OtherFilmsAdapter mAdapter;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private MenuItem mSortByIdItem;
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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView != null) {
                    int topRowVerticalPosition = 0;

                    if (recyclerView.getChildCount() != 0) {
                        topRowVerticalPosition = recyclerView.getChildAt(0).getTop();
                    }

                    mSwipeLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        RelativeLayout loading = view.findViewById(R.id.loading);
        RelativeLayout error = view.findViewById(R.id.error);

        if (mRecyclerView.getAdapter() == null) {
            new OtherFilmTask(mContext, this, loading, error)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        mSortByIdItem = menu.findItem(R.id.sort_by_alphabet);
        mSortByIdItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(!mSearchView.isIconified()) {
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(true);
                }
                mAdapter.setFilter(Utils.sortById(mOtherFilmList));
                return true;
            }
        });

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mOtherFilmList);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<OtherFilm> filteredOtherFilmList = filter(mOtherFilmList, newText);
        mAdapter.setFilter(filteredOtherFilmList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<OtherFilm> filter(List<OtherFilm> films, String query) {
        query = query.toLowerCase();
        final List<OtherFilm> filteredOtherFilms = new ArrayList<>();
        for (OtherFilm film : films) {
            final String text = film.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredOtherFilms.add(film);
            }
        }
        return filteredOtherFilms;
    }

    public void setupListView(List<OtherFilm> otherFilmList) {
        mAdapter = new OtherFilmsAdapter(mContext, otherFilmList);
        mOtherFilmList = otherFilmList;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByIdItem != null) {
                mSearchItem.setVisible(true);
                mSortByIdItem.setVisible(true);
            }
        }
    }

    private class OtherFilmsAdapter
            extends RecyclerView.Adapter<OtherFilmsAdapter.OtherFilmViewHolder> {

        private Context mContext;
        private List<? extends DramaFilm> mOtherFilmList;

        private OtherFilmsAdapter(Context context, List<? extends DramaFilm> otherFilmsList) {
            mContext = context;
            mOtherFilmList = otherFilmsList;
        }

        @Override
        public OtherFilmsAdapter.OtherFilmViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
            View view =
                    LayoutInflater.from(mContext).inflate(R.layout.film_list_item, parent, false);
            return  new OtherFilmViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OtherFilmsAdapter.OtherFilmViewHolder holder, int position) {
            final OtherFilm item = getItem(position);

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set fansub
            holder.fansubTxt.setText(item.getFansub());

            // set type
            holder.typeTxt.setText(item.getType());

            // set status
            holder.statusTxt.setTextColor(Utils.getStatusColor(mContext,
                    Utils.getStatusId(item.getStatus())));
            holder.statusTxt.setText(item.getStatus());

            // set country
            holder.countryImg.setImageDrawable(Utils.getDrawableFromCountryId(mContext,
                    Utils.getCountryId(item.getCountry())));
        }

        public OtherFilm getItem(int position) {
            return (OtherFilm) mOtherFilmList.get(position);
        }

        @Override
        public int getItemCount() {
            return mOtherFilmList.size();
        }

        class OtherFilmViewHolder extends RecyclerView.ViewHolder {

            protected TextView titleTxt;
            TextView fansubTxt;
            TextView typeTxt;
            TextView statusTxt;
            AppCompatImageView countryImg;

            OtherFilmViewHolder(View itemView) {
                super(itemView);

                titleTxt = itemView.findViewById(R.id.title);
                fansubTxt = itemView.findViewById(R.id.fansub);
                typeTxt = itemView.findViewById(R.id.type);
                statusTxt = itemView.findViewById(R.id.status);
                countryImg = itemView.findViewById(R.id.country);
            }
        }

        public void setFilter(List<? extends DramaFilm> otherFilms) {
            mAdapter = new OtherFilmsAdapter(mContext, otherFilms);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
