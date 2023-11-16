
package com.example.thejournal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thejournal.R;
import com.google.android.gms.common.util.Strings;

import java.util.List;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.PromptViewHolder> {

    public PromptAdapter(List<String> prompts) {
        this.prompts = prompts;
    }

    @NonNull
    @Override
    public PromptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PromptViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_prompt,
                        parent,
                        false)
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

        public PromptViewHolder(@NonNull View itemView) {
            super(itemView);


            textPrompt=itemView.findViewById(R.id.textPrompt);


        }

        void setPrompt(String Prompt) {
            textPrompt.setText(Prompt);
        }
    }
}