package com.example.fragmentor.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class ArticleCategoriesFragment
        extends Fragment
{

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onCategorySelected(int categoryIndex) {
            // Do nothing
        }
    };

    private Callbacks selectionCallback = sDummyCallbacks;

    public interface Callbacks {
        public void onCategorySelected(int categoryIndex);
    }

    public ArticleCategoriesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_categories, container, false);
        if (rootView == null) {
            return null;
        }

        // Show the dummy content as text in a TextView.
        Spinner categoriesSpinner = (Spinner) rootView.findViewById(R.id.spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.agiNewsFeedsNames)
        );

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(dataAdapter);

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionCallback.onCategorySelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            selectionCallback = (Callbacks) activity;
        }
    }

    @Override
    public void onDetach() {
        selectionCallback = sDummyCallbacks;
        super.onDetach();
    }
}
