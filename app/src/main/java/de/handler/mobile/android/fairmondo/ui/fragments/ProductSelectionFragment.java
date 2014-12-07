package de.handler.mobile.android.fairmondo.ui.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import de.handler.mobile.android.fairmondo.R;
import de.handler.mobile.android.fairmondo.rest.json.Article;
import de.handler.mobile.android.fairmondo.ui.activities.ProductGalleryActivity;
import de.handler.mobile.android.fairmondo.ui.activities.ProductGalleryActivity_;
import de.handler.mobile.android.fairmondo.ui.adapter.ImageAdapter;

/**
 * Displays all categories for products
 */
@EFragment(R.layout.fragment_product_selection)
public class ProductSelectionFragment extends Fragment implements RecyclerView.OnItemTouchListener {

    public static final String SELECTION_ARRAY_LIST_EXTRA = "selection_string_array_list_extra";

    private ArrayList<Article> mProducts;
    private GestureDetector mGestureDetector;

    @ViewById(R.id.fragment_product_category_gridview)
    RecyclerView recyclerView;

    @ViewById(R.id.fragment_product_selection_empty)
    TextView textViewEmpty;


    @AfterViews
    public void init() {
        if (getArguments() != null) {
            mProducts = getArguments().getParcelableArrayList(SELECTION_ARRAY_LIST_EXTRA);
        }

        // Always set up RecyclerView as otherwise there will be an error
        this.setupRecyclerView();

        if (mProducts != null) {
            textViewEmpty.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        recyclerView.setAdapter(new ImageAdapter(getActivity(), mProducts));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(this);
    }

    @TargetApi(16)
    private void startProductGallery(int position, Bundle bundle) {
        Intent intent = new Intent(getActivity(), ProductGalleryActivity_.class);
        intent.putExtra(ProductGalleryActivity.PAGER_POSITION_EXTRA, position);
        intent.putParcelableArrayListExtra(ProductGalleryActivity.PRODUCT_ARRAY_LIST_EXTRA, mProducts);

        if (Build.VERSION.SDK_INT > 15 && bundle != null) {
            getActivity().startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @TargetApi(16)
    @Override
    public boolean onInterceptTouchEvent(final RecyclerView recyclerView, MotionEvent motionEvent) {
        final View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (childView != null && mGestureDetector.onTouchEvent(motionEvent)) {
            final Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(childView, 0, 0,
                    childView.getWidth(), childView.getHeight()).toBundle();
            if (Build.VERSION.SDK_INT > 15) {
                childView.animate().alpha(0).setDuration(100).withLayer().withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                childView.animate().alpha(1).setDuration(100).withLayer().withEndAction(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                startProductGallery(recyclerView.getChildPosition(childView), bundle);
                                            }
                                        }
                                );
                            }
                        }
                );
            } else {
                startProductGallery(recyclerView.getChildPosition(childView), null);
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }
}