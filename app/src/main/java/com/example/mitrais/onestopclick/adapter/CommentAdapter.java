package com.example.mitrais.onestopclick.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Comment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> {
    private Context context;

    public CommentAdapter() {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment comment, @NonNull Comment t1) {
                return comment.getId().equals(t1.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment comment, @NonNull Comment t1) {
                return comment.getEmail().equals(t1.getEmail()) &&
                        comment.getUsername().equals(t1.getUsername()) &&
                        comment.getUserImageUri().equals(t1.getUserImageUri()) &&
                        comment.getContent().equals(t1.getContent()) &&
                        comment.getProductId().equals(t1.getProductId()) &&
                        comment.getDate().equals(t1.getDate());
            }
        });
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int position) {
        if (position != RecyclerView.NO_POSITION) {
            commentViewHolder.bindData(getItem(position));
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_user)
        CustomImageView imgUser;

        @BindView(R.id.txt_username)
        TextView txtUsename;

        @BindView(R.id.rating_bar)
        RatingBar ratingBar;

        @BindView(R.id.txt_date)
        TextView txtDate;

        @BindView(R.id.txt_content)
        TextView txtContent;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(Comment comment) {
            txtUsename.setText(comment.getUsername());
            if (comment.getUserImageUri() == null || !comment.getUserImageUri().isEmpty()) {
                imgUser.loadImageUri(Uri.parse(comment.getUserImageUri()));
            } else {
                imgUser.setImageDrawable(context.getDrawable(R.drawable.skeleton));
            }
            txtContent.setText(comment.getContent());
            String date = DateFormat.format("M/d/yyyy", comment.getDate()).toString();
            txtDate.setText(date);
        }
    }
}
