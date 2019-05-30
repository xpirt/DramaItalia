package and.conachegroup.dramaitalia.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import and.conachegroup.dramaitalia.app.JapanDrama;
import and.conachegroup.dramaitalia.tasks.JapanDramaTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class JapanDramasFragment extends Fragment implements SearchView.OnQueryTextListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<JapanDrama> mJapanDramaList;
    private JapanDramasAdapter mAdapter;
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
            new JapanDramaTask(mContext, this, loading, error)
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
                mAdapter.setFilter(Utils.sortById(mJapanDramaList));
                return true;
            }
        });

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        if (mRecyclerView != null && mSearchView != null && mRecyclerView.getAdapter() != null) {
            mSearchView.clearFocus();
            mAdapter.setFilter(mJapanDramaList);
        }

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<JapanDrama> filteredJapanDramaList = filter(mJapanDramaList, newText);
        mAdapter.setFilter(filteredJapanDramaList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        return false;
    }

    private List<JapanDrama> filter(List<JapanDrama> dramas, String query) {
        query = query.toLowerCase();
        final ArrayList<JapanDrama> filteredJapanDramas = new ArrayList<>();
        for (JapanDrama drama : dramas) {
            final String text = drama.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredJapanDramas.add(drama);
            }
        }
        return filteredJapanDramas;
    }

    public void setupListView(List<JapanDrama> japanDramaList) {
        mAdapter = new JapanDramasAdapter(mContext, japanDramaList);
        mJapanDramaList = japanDramaList;

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            if (mSearchItem != null && mSortByIdItem != null) {
                mSearchItem.setVisible(true);
                mSortByIdItem.setVisible(true);
            }
        }
    }

    private class JapanDramasAdapter
            extends RecyclerView.Adapter<JapanDramasAdapter.JapanDramaViewHolder> {

        private Context mContext;
        private List<? extends DramaFilm> mJapanDramaList;

        private JapanDramasAdapter(Context context, List<? extends DramaFilm> japanDramasList) {
            mContext = context;
            mJapanDramaList = japanDramasList;
        }

        @Override @NonNull
        public JapanDramasAdapter.JapanDramaViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                          int viewType) {
            View view =
                    LayoutInflater.from(mContext).inflate(R.layout.drama_list_item, parent, false);
            return new JapanDramaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JapanDramasAdapter.JapanDramaViewHolder holder, int position) {
            final JapanDrama item = getItem(position);

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
            holder.countryImg.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.japan_flag));
        }

        public JapanDrama getItem(int position) {
            return (JapanDrama) mJapanDramaList.get(position);
        }

        @Override
        public int getItemCount() {
            return mJapanDramaList.size();
        }

        class JapanDramaViewHolder extends RecyclerView.ViewHolder {

            TextView titleTxt;
            TextView fansubTxt;
            TextView typeTxt;
            TextView statusTxt;
            AppCompatImageView countryImg;

            JapanDramaViewHolder(View itemView) {
                super(itemView);

                titleTxt = itemView.findViewById(R.id.title);
                fansubTxt = itemView.findViewById(R.id.fansub);
                typeTxt = itemView.findViewById(R.id.type);
                statusTxt = itemView.findViewById(R.id.status);
                countryImg = itemView.findViewById(R.id.country);
            }
        }

        public void setFilter(List<? extends DramaFilm> japanDramas) {
            mAdapter = new JapanDramasAdapter(mContext, japanDramas);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            notifyDataSetChanged();
        }
    }
}
