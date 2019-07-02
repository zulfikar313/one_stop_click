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
import butterknife.OnClick;

public class ProductV2Adapter extends ListAdapter<Product, ProductV2Adapter.ProductV2ViewHolder> {
    private Listener listener;
    private Context context;

    public interface Listener {
        void onItemClicked(Product product);

        void onDeleteClicked(Product product);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ProductV2Adapter() {
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
    public ProductV2ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_v2, viewGroup, false);
        return new ProductV2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductV2ViewHolder productViewHolder, int position) {
        if (position != RecyclerView.NO_POSITION)
            productViewHolder.bind(getItem(position));
    }

    class ProductV2ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_thumbnail)
        CustomImageView imgThumbnail;

        @BindView(R.id.txt_title)
        TextView txtTitle;

        @BindView(R.id.txt_price)
        TextView txtPrice;

        ProductV2ViewHolder(@NonNull View itemView) {
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

        @OnClick(R.id.img_delete)
        void onDeleteClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onDeleteClicked(product);
            }
        }

        public void bind(Product product) {
            if (product.getThumbnailUri() == null || !product.getThumbnailUri().isEmpty()) {
                imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));
            } else {
                imgThumbnail.setImageDrawable(context.getDrawable(R.drawable.skeleton));
            }
            txtTitle.setText(product.getTitle());
            txtPrice.setText("Rp." + product.getPrice());
        }

    }
}
