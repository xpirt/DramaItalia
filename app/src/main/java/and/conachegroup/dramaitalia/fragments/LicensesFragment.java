package and.conachegroup.dramaitalia.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.License;
import and.conachegroup.dramaitalia.tasks.LicenseTask;
import and.conachegroup.dramaitalia.utils.Utils;

public class LicensesFragment extends Fragment {

    private Context mContext;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mContext = getActivity();

        mRecyclerView = view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        RelativeLayout loading = view.findViewById(R.id.loading);
        RelativeLayout error =  view.findViewById(R.id.error);

        if (mRecyclerView.getAdapter() == null) {
            new LicenseTask(mContext, this, loading, error)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    public void setupListView(List<License> licenseList) {
        LicenseAdapter adapter = new LicenseAdapter(mContext, licenseList);

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    private class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

        private Context mContext;
        private List<License> mLicenseList;

        private LicenseAdapter(Context context, List<License> licenseList) {
            mContext = context;
            mLicenseList = licenseList;
        }

        @Override
        public LicenseAdapter.LicenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.license_list_item, parent,
                    false);
            return new LicenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LicenseAdapter.LicenseViewHolder holder, int position) {
            final License item = getItem(position);

            // set title
            holder.titleTxt.setText(item.getTitle());

            // set genres
            holder.authorTxt.setText(item.getAuthor());

            // set type
            holder.licenseTxt.setText(item.getLicense());

            // set link
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    try {
                        customTabsIntent.launchUrl(mContext, Uri.parse(item.getLink()));
                    } catch (Exception e) {
                        if (!Utils.isValidUrl(item.getLink()))
                            Toast.makeText(mContext, R.string.no_link_found, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }

        public License getItem(int position) {
            return mLicenseList.get(position);
        }

        @Override
        public int getItemCount() {
            return mLicenseList.size();
        }

        class LicenseViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            TextView titleTxt;
            TextView authorTxt;
            TextView licenseTxt;

            LicenseViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.cardView);
                titleTxt = itemView.findViewById(R.id.title);
                authorTxt = itemView.findViewById(R.id.author);
                licenseTxt = itemView.findViewById(R.id.license);
            }
        }
    }
}
