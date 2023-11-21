package com.example.thejournal.adapters;
import static androidx.fragment.app.FragmentManager.TAG;

import static com.example.thejournal.ui.home.HomeActivity.EDIT_MODE;

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

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MUSIC = 0;
    private static final int VIEW_TYPE_IMAGE = 1;

    private Context context;
    private List<Music> musicList;
    private List<String> imageList;

    public MediaAdapter(Context c, List<Music> m, List<String> i) {
        this.context = c;
        this.musicList = m;
        this.imageList = i;
    }
public MediaAdapter getMediaAdapter()
{
    return this;
}
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_MUSIC:
                return new MusicViewHolder(
                        inflater.inflate(R.layout.item_media_container, parent, false),getMediaAdapter()
                );
            case VIEW_TYPE_IMAGE:
                return new ImageViewHolder(
                        inflater.inflate(R.layout.item_media_container, parent, false),getMediaAdapter()
                );
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicViewHolder) {
            ((MusicViewHolder) holder).bind(musicList.get(position), position);
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(imageList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        // Use the size of the larger list to determine the item count
        return Math.max(musicList.size(), imageList.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position < musicList.size()) {
            return VIEW_TYPE_MUSIC;
        } else if (position < musicList.size() + imageList.size()) {
            return VIEW_TYPE_IMAGE;
        } else {
            throw new IllegalArgumentException("Invalid position");
        }
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView artist;
        private ImageView image,remove;
        int absPosition;
        MediaAdapter adapter;
        MusicViewHolder(@NonNull View itemView, MediaAdapter a) {
            super(itemView);
            title = itemView.findViewById(R.id.mediaTitle);
            artist = itemView.findViewById(R.id.mediaArtist);
            remove=itemView.findViewById(R.id.remove);
            image = itemView.findViewById(R.id.mediaPicture);
            adapter=a;
        }

        @SuppressLint("RestrictedApi")
        void bind(Music music, int pos) {
            System.out.println("List currently: " + String.valueOf(createJournalEntry.getImageList()));
            absPosition = pos;
            if (!EDIT_MODE) {
                remove.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                artist.setVisibility(View.GONE);
            }

            Log.d(TAG, "This happened: " + String.valueOf(music.getTitle()));
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

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView, logo,remove;
        private TextView title;
        private TextView artist;
        MediaAdapter adapter;

        int absPosition;

        ImageViewHolder(@NonNull View itemView,MediaAdapter a) {

            super(itemView);
            title = itemView.findViewById(R.id.mediaTitle);
            artist = itemView.findViewById(R.id.mediaArtist);
            imageView = itemView.findViewById(R.id.mediaPicture);
            logo=itemView.findViewById(R.id.isSpotify);
            remove=itemView.findViewById(R.id.remove);
            title.setVisibility(View.GONE);
            artist.setVisibility(View.GONE);
            adapter=a;
        }

        void bind(String imageUrl, int pos) {
            absPosition=pos;
            if(!EDIT_MODE)
            {
                remove.setVisibility(View.GONE);
            }
            logo.setVisibility(View.GONE);
            ImageLoader.getInstance().loadImageNoCache(itemView.getContext().getApplicationContext(),
                    imageUrl, imageView);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //remove
                    createJournalEntry.getImageList().remove(pos);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
