package de.handler.mobile.android.fairmondo.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import de.handler.mobile.android.fairmondo.FairmondoApp;
import de.handler.mobile.android.fairmondo.FairmondoApp_;
import de.handler.mobile.android.fairmondo.R;
import de.handler.mobile.android.fairmondo.data.businessobject.Product;
import de.handler.mobile.android.fairmondo.presentation.FormatHelper;
import de.handler.mobile.android.fairmondo.presentation.ProductConstants;
import de.handler.mobile.android.fairmondo.presentation.interfaces.OnRecyclerItemClickListener;
import de.handler.mobile.android.fairmondo.presentation.views.CustomNetworkImageView;


/**
 * Image Adapter used in GridView Fragments.
 */
public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    private final Context mContext;
    private List<Product> mProducts;
    private OnRecyclerItemClickListener<Integer> mOnItemClickListener;

    /**
     * Creates an instance of the ImageAdapter.
     */
    public ListItemAdapter(@NonNull final Context context, @NonNull final List<Product> products) {
        mContext = context;
        mProducts = products;
    }

    /**
     * Enables external changes to the adapter without instantiating it again.
     * @param products the updated lis of items.
     */
    public void updateItems(@NonNull final List<Product> products) {
        mProducts = products;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_list_item, viewGroup, false);
        return new ViewHolder(view, mContext, mProducts);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.bindPosition(position);
        if (mProducts.size() > 0) {
            final Product product = mProducts.get(position);
            if (product != null) {

                // Set image
                String url = product.getTitleImageUrl();
                if (product.getTitleImage() != null && !product.getTitleImage().getOriginalUrl().equals("")) {
                    url = product.getTitleImage().getOriginalUrl();
                }

                final FairmondoApp_ app = (FairmondoApp_) mContext.getApplicationContext();
                this.loadImage(app, viewHolder, url);

                // Set price
                final String price = FormatHelper.formatPrice(product.getPriceCents());
                viewHolder.mTextViewPrice.setText(price + " €");

                // Set text
                final String description = product.getTitle();
                viewHolder.mTextViewTitle.setText(description);

                if (null != product.getTags()) {
                    // Set condition
                    final String condition = product.getTags().getCondition();
                    switch (condition) {
                        case ProductConstants.CONDITION_OLD:
                            viewHolder.mConditionTextView.setVisibility(View.VISIBLE);
                            viewHolder.mConditionTextView.setText(mContext.getString(R.string.text_adapter_image_condition_old));
                            break;
                        case ProductConstants.CONDITION_NEW:
                            viewHolder.mConditionTextView.setVisibility(View.VISIBLE);
                            viewHolder.mConditionTextView.setText(mContext.getString(R.string.text_adapter_image_condition_new));
                            break;
                        default:
                            // nothing is done here
                    }

                    // Make tags visible
                    if (product.getTags().isEcologic()) {
                        viewHolder.mTagEcoTextView.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mTagEcoTextView.setVisibility(View.GONE);
                    }
                    if (product.getTags().isFair()) {
                        viewHolder.mTagFairTextView.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mTagFairTextView.setVisibility(View.GONE);
                    }
                }

            } else {
                viewHolder.mListItem.setVisibility(View.GONE);
            }
        }
    }

    private void loadImage(@NonNull final FairmondoApp app, @NonNull final ListItemAdapter.ViewHolder viewHolder, @Nullable final String url) {
        if (url != null) {
            app.getImageLoader().get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer response, final boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        setImage(viewHolder, response.getBitmap());
                    }
                }
                @Override
                public void onErrorResponse(final VolleyError error) {
                    setImage(viewHolder, BitmapFactory
                            .decodeResource(mContext.getResources(),
                                    R.drawable.fairmondo_small));
                }
            });
        }
    }

    public void setImage(@NonNull final ViewHolder viewHolder, @NonNull final Bitmap bitmap) {
        viewHolder.mProductImageView.setLocalImageBitmap(bitmap);
    }

    @Override
    public long getItemId(final int position) {
        return Long.getLong(mProducts.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    /**
     * Sets a onItemClick listener.
     */
    public void setOnRecyclerItemClickListener(final OnRecyclerItemClickListener<Integer> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CustomNetworkImageView mProductImageView;
        private final TextView mConditionTextView;
        private final TextView mTagFairTextView;
        private final TextView mTagEcoTextView;
        private final TextView mTextViewTitle;
        private final TextView mTextViewPrice;
        private final LinearLayout mListItem;

        private final Context mContext;
        private final List<Product> mProducts;
        private int mPosition;

        public ViewHolder(@NonNull final View view, @NonNull final Context context, @NonNull final List<Product> products) {
            super(view);
            mProductImageView = (CustomNetworkImageView) view.findViewById(R.id.image_adapter_image_view_product);
            mConditionTextView = (TextView) view.findViewById(R.id.image_adapter_text_view_condition);
            mTagFairTextView = (TextView) view.findViewById(R.id.image_adapter_text_view_tag_fair);
            mTagEcoTextView = (TextView) view.findViewById(R.id.image_adapter_text_view_tag_eco);
            mTextViewTitle = (TextView) view.findViewById(R.id.image_adapter_text_view_title);
            mTextViewPrice = (TextView) view.findViewById(R.id.image_adapter_text_view_price);
            mListItem = (LinearLayout) view.findViewById(R.id.image_adapter_list_item);
            mListItem.setOnClickListener(this);
            mContext = context;
            mProducts = products;
        }

        protected void bindPosition(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final View view) {
            mOnItemClickListener.onRecyclerItemClicked(view, mPosition);
        }
    }
}
