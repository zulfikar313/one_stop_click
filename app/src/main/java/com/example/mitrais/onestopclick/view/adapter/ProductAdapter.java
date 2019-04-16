package com.example.mitrais.onestopclick.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter to handle product list recycler view
 */
public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {
    private Listener listener;

    public interface Listener {
        void onItemClicked(String productId);

        void onLikeClicked(String productId);

        void onDislikeClicked(String productId);

        void onShareClicked(String productId);
    }

    /**
     * @param listener set adapter listeners
     */
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
                        product.getArtist().equals(t1.getArtist()) &&
                        product.getAuthor().equals(t1.getAuthor()) &&
                        product.getDirector().equals(t1.getDirector()) &&
                        product.getContentUri().equals(t1.getContentUri()) &&
                        product.getContentFilename().equals(t1.getContentFilename()) &&
                        product.getThumbnailUri().equals(t1.getThumbnailUri()) &&
                        product.getThumbnailFilename().equals(t1.getThumbnailFilename()) &&
                        product.getLike() == t1.getLike() &&
                        product.getDislike() == t1.getDislike();
            }
        });
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        productViewHolder.bind(getItem(i));
    }

    /**
     * ProductViewHolder to bind product data to view
     */
    class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_title)
        TextView txtTitle;

        @BindView(R.id.img_thumbnail)
        ImageView imgThumbnail;

        @BindView(R.id.txt_author)
        TextView txtAuthor;

        @BindView(R.id.txt_description)
        TextView txtDescription;

        @BindView(R.id.txt_type)
        TextView txtType;

        @BindView(R.id.txt_like_counter)
        TextView txtLikeCounter;

        @BindView(R.id.txt_dislike_counter)
        TextView txtDislikeCounter;

        /**
         * ProductViewHolder constructor
         *
         * @param itemView recycler item view
         */
        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    Product product = getItem(position);
                    listener.onItemClicked(product.getId());
                }
            });
        }

        /**
         * bind product to item view
         *
         * @param product product object
         */
        public void bind(Product product) {
            txtTitle.setText(product.getTitle());
            txtType.setText(product.getType());
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
            txtDescription.setText(product.getDescription());
            txtLikeCounter.setText(Integer.toString(product.getLike()));
            txtDislikeCounter.setText(Integer.toString(product.getDislike()));
            if (!product.getThumbnailUri().isEmpty())
                Picasso.get().load(product.getThumbnailUri()).placeholder(R.drawable.skeleton).into(imgThumbnail);
        }

        @OnClick(R.id.img_like)
        void onLikeImageClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onLikeClicked(product.getId());
            }
        }

        @OnClick(R.id.img_dislike)
        void onDislikeImageClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onDislikeClicked(product.getId());
            }
        }


        @OnClick(R.id.img_share)
        void onShareImageClicked() {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Product product = getItem(position);
                listener.onShareClicked(product.getId());
            }
        }
    }
}
