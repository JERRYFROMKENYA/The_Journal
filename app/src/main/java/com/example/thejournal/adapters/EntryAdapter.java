package com.example.thejournal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.R;
import com.example.thejournal.models.JournalEntry;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    public EntryAdapter(List<JournalEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_journalentry,
                        parent,
                        false)
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

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle=itemView.findViewById(R.id.textTitle);
            textSubtitle=itemView.findViewById(R.id.textSubtitle);
            textDatetime=itemView.findViewById(R.id.contdateTime);

        }

         void setEntry(JournalEntry Entry) {
            textTitle.setText(Entry.getTitle());
            if(Entry.getSubtitle().trim().isEmpty())
            {
                textSubtitle.setVisibility(View.GONE);
            }
            else {
                textSubtitle.setText(Entry.getSubtitle());

            }
            textDatetime.setText(Entry.getDateCreated());
        }
    }
}
