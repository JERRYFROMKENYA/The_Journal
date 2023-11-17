package com.example.thejournal.adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.R;
import com.example.thejournal.data.ImageLoader;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.SpotiySearch.SpotifySearch;

import java.util.List;
public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.SpotifyAdapterHolder> {
public Context context;
public WebView wb;

public ConstraintLayout cly;
public ImageView mSel;

public SpotifySearch sps;


    public SpotifyAdapter(Context c, List<Music> music, WebView wv, ConstraintLayout cl, ImageView ms, SpotifySearch sss) {
        this.context=c;
        this.musicList = music;
        this.wb=wv;
        this.cly=cl;
        this.mSel=ms;
        this.sps=sss;
    }

    @NonNull
    @Override
    public SpotifyAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(music.get(getItemCount()-1));
        return new SpotifyAdapterHolder(context,
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_spotify,
                        parent,
                        false),wb,cly, musicList.get(getItemCount()-1),mSel, sps
        );

    }

    @Override
    public void onBindViewHolder(@NonNull SpotifyAdapterHolder holder, int position) {
        holder.setMusic(musicList.get(position));

    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private List<Music> musicList;
    static class SpotifyAdapterHolder extends RecyclerView.ViewHolder
    {
        public WebView wb;

        public void setMusicURL(String musicURL) {
            this.musicURL = musicURL;
        }

        public String musicURL=new String();
        public ConstraintLayout cly;
        TextView title,artist;
        ImageView image;
        Context ctx;
        Music music;

        ImageView mSelect;

        public Music getPicked() {
            return picked;
        }

        public void setPicked(Music picked) {
            this.picked = picked;
        }

        public Music picked;

        SpotifySearch sps;



        @SuppressLint("RestrictedApi")
        public SpotifyAdapterHolder(Context c, @NonNull View itemView, WebView wv, ConstraintLayout cl, Music m,ImageView ms, SpotifySearch ss) {
            super(itemView);
            ctx=c;
            this.wb=wv;
            this.cly=cl;
            this.music=m;
            Log.d(TAG,""+music);
            mSelect=ms;
            this.sps=ss;


title=itemView.findViewById(R.id.SongTitle);
artist= itemView.findViewById(R.id.ArtistName);
image=itemView.findViewById(R.id.albumart);
TextView tap=itemView.findViewById(R.id.reclickbutton);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
toggleWebView(picked);
tap.setText("Tap and Hold to Use Song");


    }
});

itemView.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {
        sps.finishWithIntent(picked);
        return true;
    }
});


//            textPrompt=itemView.findViewById(R.id.textPrompt);


        }

        public void toggleWebView(Music music){


            // this will enable the javascript.
            wb.getSettings().setJavaScriptEnabled(true);
            wb.loadUrl(picked.getMusicUrl());
            // WebViewClient allows you to handle
            // onPageFinished and override Url loading.
            wb.setWebViewClient(new WebViewClient());
            cly.setVisibility(View.VISIBLE);



            mSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sps.finishWithIntent(picked);
                }
            });


        }



        void setMusic(Music m) {
            title.setText(m.getTitle());
            artist.setText(m.getArtist());
            ImageLoader.getInstance().loadImageNoCache(ctx, m.getImageResource(), image);
            setMusicURL(m.getMusicUrl());
            setPicked(m);



        }

    }
}