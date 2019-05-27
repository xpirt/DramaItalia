package and.conachegroup.dramaitalia.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import and.conachegroup.dramaitalia.R;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /*final AppCompatImageView headerImg = view.findViewById(R.id.headerImg);
        Drawable headerDrawable = Utils.getSeason(getContext());
        if (headerDrawable != null) {
            headerImg.setImageDrawable(headerDrawable);
        }*/

        // just return the current view for now
        return view;
    }
}
