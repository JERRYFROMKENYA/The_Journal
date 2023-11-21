package com.example.thejournal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.R;
import com.example.thejournal.data.ImageLoader;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.createentry.createJournalEntry;
import com.example.thejournal.ui.home.HomeActivity;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;
    private List<Music> musicList;

    public MusicAdapter(Context c, List<Music> m) {
        this.context = c;
        this.musicList = m;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MusicViewHolder(
                inflater.inflate(R.layout.item_media_container, parent, false), this
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(musicList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView artist;
        private ImageView image, remove;
        MusicAdapter adapter;
        int absPosition;

        MusicViewHolder(@NonNull View itemView, MusicAdapter a) {
            super(itemView);
            title = itemView.findViewById(R.id.mediaTitle);
            artist = itemView.findViewById(R.id.mediaArtist);
            remove = itemView.findViewById(R.id.remove);
            image = itemView.findViewById(R.id.mediaPicture);
            adapter = a;
        }

        @SuppressLint("RestrictedApi")
        void bind(Music music, int pos) {
            absPosition = pos;
            if (!HomeActivity.EDIT_MODE) {
                remove.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                artist.setVisibility(View.GONE);
            }

            Log.d("MediaAdapter", "This happened: " + music.getTitle());
            System.out.println("abs pos" + absPosition);
            title.setText(music.getTitle());
            artist.setText(music.getArtist());
            ImageLoader.getInstance().loadImageNoCache(itemView.getContext(),
                    music.getImageResource(), image);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Use the absolute position to remove the correct item
                    if (absPosition < createJournalEntry.getMusicList().size()) {
                        createJournalEntry.getMusicList().remove(absPosition);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}

