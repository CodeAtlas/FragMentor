package com.example.fragmentor.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.fragmentor.app.databinding.ArticleCategoriesFragmentBinding;
import com.example.fragmentor.app.util.TrackingAnalyticsUtils;

public class ArticleCategoriesFragment
        extends Fragment {

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onCategorySelected(int categoryIndex) {
            // Do nothing
        }
    };

    private Callbacks selectionCallback = sDummyCallbacks;
    private ArrayAdapter<String> categoryAdapter;

    public interface Callbacks {
        void onCategorySelected(int categoryIndex);
    }

    public ArticleCategoriesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArticleCategoriesFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_article_categories, container, false);

        categoryAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.agiNewsFeedsNames)
        );

        categoryAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        binding.spinner.setAdapter(categoryAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Tracking category selected
                TrackingAnalyticsUtils.sendEvent(getActivity(),
                        TrackingAnalyticsUtils.CAT_UI_ACTION,
                        TrackingAnalyticsUtils.ACT_CATEGORY_SELECTED,
                        categoryAdapter.getItem(position));

                selectionCallback.onCategorySelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            selectionCallback = (Callbacks) context;
        }
    }

    @Override
    public void onDetach() {
        selectionCallback = sDummyCallbacks;
        super.onDetach();
    }
}
