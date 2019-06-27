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
import android.widget.TextView;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Product;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductV3Adapter extends ListAdapter<Product, ProductV3Adapter.ProductV3ViewHolder> {
    private Listener listener;
    private Context context;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ProductV3Adapter() {
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
                        product.getLike() == t1.getLike() &&
                        product.getDislike() == t1.getDislike() &&
                        (product.isLiked() && t1.isLiked()) &&
                        (product.isDisliked() && t1.isDisliked()) &&
                        (product.isInCart() && t1.isInCart()) &&
                        (product.isOwned() && t1.isOwned()) &&
                        product.getPrice() == t1.getPrice();
            }
        });
    }

    @NonNull
    @Override
    public ProductV3ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_v3, viewGroup, false);
        return new ProductV3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductV3ViewHolder productV3ViewHolder, int position) {
        if (position != RecyclerView.NO_POSITION)
            productV3ViewHolder.bind(getItem(position));
    }

    public interface Listener {
        void onItemClicked(Product product);
    }

    class ProductV3ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_thumbnail)
        CustomImageView imgThumbnail;

        @BindView(R.id.txt_title)
        TextView txtTitle;

        @BindView(R.id.txt_type)
        TextView txtType;

        ProductV3ViewHolder(@NonNull View itemView) {
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
            if (product.getThumbnailUri() == null || !product.getThumbnailUri().isEmpty()) {
                imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));
            } else {
                imgThumbnail.setImageDrawable(context.getDrawable(R.drawable.skeleton));
            }
            txtTitle.setText(product.getTitle());
            txtType.setText(product.getType());
        }
    }
}
