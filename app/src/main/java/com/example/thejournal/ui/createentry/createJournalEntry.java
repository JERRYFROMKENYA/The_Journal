package com.example.thejournal.ui.createentry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thejournal.R;
import com.example.thejournal.data.ApiHandler;
import com.example.thejournal.models.JournalEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class createJournalEntry extends AppCompatActivity {
    EditText inputEntryTitle,inputSubtitleEntry, inputEntry ;
    ImageView bookmark, imageDone;
    boolean bookmarked=false;
    TextView textDateTime, generateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal_entry);

        ImageView imageBack=findViewById(R.id.imageBack);
        imageDone=findViewById(R.id.imageSave);
        inputEntry=findViewById(R.id.inputEntry);
        inputEntryTitle=findViewById(R.id.inputEntryTitle);
        inputSubtitleEntry=findViewById(R.id.inputEntrySubtitle);
        textDateTime=findViewById(R.id.textDateAndTime);
        generateImage=findViewById(R.id.textGenerate);

        generateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateSuggestions();

            }
        });
        textDateTime.setText(

                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()
                ).format(new Date())
        );
        bookmark=findViewById(R.id.bookmark);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                SaveEntry();
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarked)
                {
                    bookmarked=false;
                    int bookmark_add = getResources().getIdentifier("com.example.thejournal:drawable/ic_bookmark_add", null, null);
                    bookmark.setImageResource(bookmark_add);

                } else if (!bookmarked) {

                    bookmarked=true;
                    int bookmark_remove = getResources().getIdentifier("com.example.thejournal:drawable/baseline_bookmark_remove_24", null, null);
                    bookmark.setImageResource(bookmark_remove);
//                    bookmark.setImageDrawable(Drawable.createFromPath("@drawable/baseline_bookmark_remove_24"));

                }

            }
        });
    }

    private void SaveEntry(){
        if(inputEntryTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Journal Entry " +
                    "Title Can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputSubtitleEntry.getText().toString().trim().isEmpty() &&
                inputEntry.getText().toString().trim().isEmpty() ) {
            Toast.makeText(this, "Please Give This Entry " +
                    "Some Content", Toast.LENGTH_SHORT).show();

        }
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final JournalEntry entry=new JournalEntry();
            entry.setUid(user.getUid());
            entry.setTitle(inputEntryTitle.getText().toString());
            entry.setSubtitle(inputSubtitleEntry.getText().toString());
            entry.setBody(inputEntry.getText().toString());
            entry.setDateCreated(textDateTime.getText().toString());
            entry.setBookmarked(bookmarked);
            //other stuff
            //save
           if(entry.EntryUpload())
           {
               Intent intent=new Intent();
               setResult(RESULT_OK,intent);
               finish();
           }else
           {
               Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show();
           }

        }

    }

    private void generateSuggestions()
    {
        if(inputSubtitleEntry.getText().toString().trim().isEmpty()||inputEntry.getText().toString().trim().isEmpty())
        {
            String promptText=new ApiHandler().generateText(
                    "AIzaSyB5UooCRfiIKJP_xbcmtl-d4k-46t_JH6A",
                    "Generate one " +
                            "random healthy journaling prompt only one result different " +
                            "every time make it is just one sentence nothing extra");
            if(promptText==null)
            {
                inputEntryTitle.setText("What do you want remember about today?");
            }
            else
            {
                inputEntryTitle.setText(promptText);
            }

        }else
        {
            Toast.makeText(this, "Looks Like you're already writing about something", Toast.LENGTH_SHORT).show();
        }

    }
}