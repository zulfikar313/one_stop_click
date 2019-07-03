package com.example.mitrais.onestopclick.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Product;

import org.apache.commons.text.WordUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {
    private Listener listener;
    private Context context;

    public interface Listener {
        void onItemClicked(Product product);

        void onShareImageClicked(Product product);

        void onAddToCartButtonClicked(Product product);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ProductAdapter() {
        super(new DiffUtil.ItemCallback<Product>() {
            @Override
            public boolean areItemsTheSame(@NonNull Product product, @NonNull Product t1) {
                return product.getId().equals(t1.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Product product, @NonNull Product t1) {
                return product.getTitle().equals(t1.getTitle()) &&
                        product.getDescription().equals(t1.getDescription()) &&
                        product.getType().equals(t1.getType()) &&
                        product.getGenre().equals(t1.getGenre()) &&
                        product.getArtist().equals(t1.getArtist()) &&
                        product.getAuthor().equals(t1.getAuthor()) &&
                        product.getDirector().equals(t1.getDirector()) &&
                        product.getThumbnailUri().equals(t1.getThumbnailUri()) &&
                        product.getMusicUri().equals(t1.getMusicUri()) &&
                        product.getTrailerUri().equals(t1.getTrailerUri()) &&
                        product.getMovieUri().equals(t1.getMovieUri()) &&
                        (product.isInCart() && t1.isInCart()) &&
                        (product.isOwned() && t1.isOwned()) &&
                        product.getPrice() == t1.getPrice();
            }
        });
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        if (position != RecyclerView.NO_POSITION)
            productViewHolder.bind(getItem(position));
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_title)
        TextView txtTitle;

        @BindView(R.id.img_thumbnail)
        CustomImageView imgThumbnail;

        @BindView(R.id.txt_author)
        TextView txtAuthor;

        @BindView(R.id.txt_rating)
        TextView txtRating;

        @BindView(R.id.txt_price)
        TextView txtPrice;

        @BindView(R.id.rating_bar)
        RatingBar ratingBar;

        @BindView(R.id.img_add_to_cart)
        ImageView imgAddtoCart;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    Product product = getItem(position);
                    listener.onItemClicked(product);
                }
            });
        }

        public void bind(Product product) {
            txtTitle.setText(WordUtils.capitalizeFully(product.getTitle()));
            switch (product.getType()) {
                case Constant.PRODUCT_TYPE_MUSIC: {
                    txtAuthor.setText(product.getArtist());
                    break;
                }
                case Constant.PRODUCT_TYPE_MOVIE: {
                    txtAuthor.setText(product.getDirector());
                    break;
                }
                case Constant.PRODUCT_TYPE_BOOK: {
                    txtAuthor.setText(product.getAuthor());
                    break;
                }
                default: {
                    break;
                }
            }

            HashMap<String, Float> ratings = product.getRating();
            float averageRating = 0f;
            int voteNumber = ratings.size();
            if (ratings.size() > 0) {
                float total = 0f;
                for (Float rating : ratings.values()) {
                    total += rating;
                }
                averageRating = total / ratings.size();
                ratingBar.setRating(averageRating);
            }
            txtRating.setText(context.getString(R.string.rate) + ": " + averageRating + "/5 - " + voteNumber + " " + context.getString(R.string.lower_votes));

            txtPrice.setVisibility(product.isOwned() ? View.GONE : View.VISIBLE);
            txtPrice.setText("Rp. " + product.getPrice());
            imgAddtoCart.setVisibility(product.isOwned() ? View.GONE : View.VISIBLE);
            imgAddtoCart.setImageDrawable(context.getDrawable(product.isInCart() ? R.drawable.ic_remove_from_cart : R.drawable.ic_add_to_cart));
            if (product.getThumbnailUri() == null || !product.getThumbnailUri().isEmpty()) {
                imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));
            } else {
                imgThumbnail.setImageDrawable(context.getDrawable(R.drawable.skeleton));
            }
        }

        @OnClick(R.id.img_share)
        void onShareImageClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onShareImageClicked(product);
            }
        }

        @OnClick(R.id.img_add_to_cart)
        void onAddToCartButtonClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onAddToCartButtonClicked(product);
            }
        }
    }
}
