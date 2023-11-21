
package com.example.thejournal.adapters;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import static com.example.thejournal.ui.home.HomeActivity.REQUEST_CODE_ADD_INT;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.MainActivity;
import com.example.thejournal.R;
import com.example.thejournal.ui.SpotiySearch.SpotifySearch;
import com.example.thejournal.ui.createentry.createJournalEntry;
import com.example.thejournal.ui.home.HomeActivity;
import com.google.android.gms.common.util.Strings;

import java.util.List;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.PromptViewHolder> {
HomeActivity ha;
    public PromptAdapter(List<String> prompts, HomeActivity homeActivity) {
        this.prompts = prompts;
        this.ha=homeActivity;
    }

    @NonNull
    @Override
    public PromptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PromptViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_prompt,
                        parent,
                        false),ha
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PromptViewHolder holder, int position) {
        holder.setPrompt(prompts.get(position).toString());

    }


    @Override
    public int getItemCount() {
        return prompts.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private List<String> prompts;
    static class PromptViewHolder extends RecyclerView.ViewHolder
    {
        TextView textTitle,textPrompt, textDatetime;
        HomeActivity home;
        String prompt;

        public PromptViewHolder(@NonNull View itemView,HomeActivity homeActivity) {
            super(itemView);
            home=homeActivity;


            textPrompt=itemView.findViewById(R.id.textPrompt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(homeActivity.getApplicationContext(), createJournalEntry.class);
                    intent.putExtra("EntryTitle", prompt);
                    homeActivity.startActivity(intent);
                }
            });


        }

        void setPrompt(String Prompt) {
            textPrompt.setText(Prompt);
            this.prompt=Prompt;
        }
    }
}