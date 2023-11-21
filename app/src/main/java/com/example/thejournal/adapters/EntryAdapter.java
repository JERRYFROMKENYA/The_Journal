package com.example.thejournal.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.thejournal.MainActivity;
import com.example.thejournal.R;
import com.example.thejournal.models.JournalEntry;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.UpdateView.UpdateView;
import com.example.thejournal.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    static String whiteSpaces="                        " +
            "                                               ";

    HomeActivity ma;
    public EntryAdapter(List<JournalEntry> entries, HomeActivity ma) {
        this.ma=ma;
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_journalentry,
                        parent,
                        false),ma
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        holder.setEntry(entries.get(position));

    }


    @Override
    public int getItemCount() {
        return entries.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private List<JournalEntry> entries;
    static class EntryViewHolder extends RecyclerView.ViewHolder
    {
        TextView textTitle,textSubtitle, textDatetime;
        RecyclerView musicrecyclerView, imagerecyclerview;
        HomeActivity ma;


        public EntryViewHolder(@NonNull View itemView, HomeActivity mac) {
            super(itemView);
            ma=mac;
            musicrecyclerView=itemView.findViewById(R.id.entryMusicRecycler);
            imagerecyclerview=itemView.findViewById(R.id.entryImageRecycler);
            textTitle=itemView.findViewById(R.id.textTitle);
            textSubtitle=itemView.findViewById(R.id.textSubtitle);
            textDatetime=itemView.findViewById(R.id.contdateTime);

        }

        void setEntry(JournalEntry Entry) {

            if (Entry.getMusic() != null) {
//                List<String> images = (Entry.getImages() != null) ? Entry.getImages() : new ArrayList<>();
                MusicAdapter musicAdapter = new MusicAdapter(itemView.getContext(), Entry.getMusic());
                musicrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                musicrecyclerView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
            }
            if(Entry.getImages() !=null)
            {
                System.out.println("This mf"+Entry.getImages());
                ImageAdapter imageAdapter= new ImageAdapter(itemView.getContext(), Entry.getImages());
                imagerecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                imagerecyclerview.setAdapter(imageAdapter);
                imageAdapter.notifyDataSetChanged();
            }
            imagerecyclerview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEntry(Entry.getId());
                }
            });
            musicrecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEntry(Entry.getId());
                }
            });




            textTitle.setText(Entry.getTitle());
            textTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEntry(Entry.getId());
                }
            });
            if(Entry.getSubtitle().trim().isEmpty())
            {
//                textSubtitle.setVisibility(View.GONE);
                if(String.valueOf(Entry.getBody())==null){textSubtitle.setVisibility(View.GONE);}
                else
                {
                    if(String.valueOf(Entry.getBody()).length()<=35)
                    {
                        textSubtitle.setText(String.valueOf(Entry.getBody()));
                    }
                    else {
                        textSubtitle.setText(String.valueOf(Entry.getBody()).substring(0, 35) + " ...");
                    }
                }

            }
            else {
                textSubtitle.setText(Entry.getSubtitle());


            }
            textSubtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEntry(Entry.getId());
                }
            });
            textDatetime.setText(Entry.getDateCreated());
        }
        private void goToEntry(String Id){
         ma.startActivitywithID(Id);
//            startActivity(itemView.getContext().getApplicationContext(),);

        }
    }


}
