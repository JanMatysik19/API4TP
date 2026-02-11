package com.example.api4tp.UI_Helpers;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.function.Function;

public class MainSearch {
    private final ConstraintLayout main;
    private final SearchView searchSv;

    private final ConstraintSet collapsedCs;
    private final ConstraintSet expandedCs;
    private final int collapsedWidth;
    private final int expandedWidth;

    private final ViewGroup.LayoutParams searchSvParams;

    public MainSearch(ConstraintLayout main, CardView searchCv, SearchView searchSv, DisplayMetrics metrics) {
        this.main = main;
        this.searchSv = searchSv;

        collapsedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, metrics);
        expandedWidth = ViewGroup.LayoutParams.MATCH_PARENT;

        searchSvParams = searchSv.getLayoutParams();
        collapsedCs = initCollapsed();
        expandedCs = initExpanded(searchCv);

        searchSv.setOnSearchClickListener(this::searchClickHandler);
        searchSv.setOnCloseListener(this::searchCloseHandler);
    }

    private ConstraintSet initCollapsed() {
        var set = new ConstraintSet();
        set.clone(main);
        return set;
    }

    private ConstraintSet initExpanded(CardView searchCv) {
        var set = new ConstraintSet();
        set.clone(main);
        set.connect(searchCv.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 15);
        set.connect(searchCv.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 15);
        set.constrainWidth(searchCv.getId(), ConstraintSet.MATCH_CONSTRAINT);
        return set;
    }

    private void searchClickHandler(View v) {
        expandedCs.applyTo(main);
        searchSvParams.width = expandedWidth;
    }

    private boolean searchCloseHandler() {
        collapsedCs.applyTo(main);
        searchSvParams.width = collapsedWidth;
        return false;
    }

    public void clearFocus() {
        searchSv.clearFocus();
    }

    public void setQuerySubmitHandler(Function<String, Boolean> handler) {
        searchSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return handler.apply(query);
            }
        });
    }

    public void setSearchCloseHandler(Runnable handler) {
        searchSv.setOnCloseListener(() -> {
            handler.run();
            return searchCloseHandler();
        });
    }
}
