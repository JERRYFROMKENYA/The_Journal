

package com.example.thejournal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.R;
import com.example.thejournal.data.ImageLoader;
import com.example.thejournal.ui.createentry.createJournalEntry;
import com.example.thejournal.ui.home.HomeActivity;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageList;

    public ImageAdapter(Context c, List<String> i) {
        this.context = c;
        this.imageList = i;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageViewHolder(
                inflater.inflate(R.layout.item_media_container, parent, false), this
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(imageList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView, logo, remove;
        private TextView title;
        private TextView artist;
        ImageAdapter adapter;

        int absPosition;

        ImageViewHolder(@NonNull View itemView, ImageAdapter a) {
            super(itemView);
            title = itemView.findViewById(R.id.mediaTitle);
            artist = itemView.findViewById(R.id.mediaArtist);
            imageView = itemView.findViewById(R.id.mediaPicture);
            logo = itemView.findViewById(R.id.isSpotify);
            remove = itemView.findViewById(R.id.remove);
            title.setVisibility(View.GONE);
            artist.setVisibility(View.GONE);
            adapter = a;
        }

        void bind(String imageUrl, int pos) {
            absPosition = pos;
            if (!HomeActivity.EDIT_MODE) {
                remove.setVisibility(View.GONE);
            }
            logo.setVisibility(View.GONE);
            ImageLoader.getInstance().loadImageNoCache(itemView.getContext().getApplicationContext(),
                    imageUrl, imageView);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove
                    createJournalEntry.getImageList().remove(pos);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}

