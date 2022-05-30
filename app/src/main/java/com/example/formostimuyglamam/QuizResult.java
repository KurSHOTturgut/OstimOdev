package com.example.formostimuyglamam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

public class QuizResult extends AppCompatActivity {

    private  List<QuestionList>questionLists=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        final TextView scoreTV=findViewById(R.id.scoreTV);
        final TextView totalScoreTV=findViewById(R.id.totalScoreTV);
        final TextView correctTV=findViewById(R.id.correctTV);
        final TextView inCorrectTV=findViewById(R.id.inCorrectTV);
        final AppCompatButton shareBtn=findViewById(R.id.shareBtn);
        final AppCompatButton reTakeBtn=findViewById(R.id.reTakeQuizBtn);

        //getting questions list from  QuestionsActivity
        questionLists=(List<QuestionList>) getIntent().getSerializableExtra("question");

        totalScoreTV.setText("/"+questionLists.size());
        scoreTV.setText(getCorrectAnswers()+"");
        correctTV.setText(getCorrectAnswers()+"");
        inCorrectTV.setText(String.valueOf(questionLists.size()-getCorrectAnswers()));

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.show(QuizResult.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Toast.makeText(QuizResult.this, "hata reklam gelmedi", Toast.LENGTH_SHORT).show();
                    }
                });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open share intent
                Intent sendIntent=new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"Skor"+scoreTV.getText());

                Intent shareIntent =Intent.createChooser(sendIntent,"Paylaş");
                startActivity(shareIntent);
            }
        });
        reTakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // re start quiz go to QuestionsActivity
                startActivity(new Intent(QuizResult.this,QuestionsActivity.class));
                finish();
            }
        });

    }

    private int getCorrectAnswers(){
        int correctAnswer=0;
        for (int i=0;i<questionLists.size();i++){
            int getUserSelectedOption=questionLists.get(i).getUserSelectedAnswer();//get user selected option
            int getQuestionAnswer=questionLists.get(i).getAnswer(); //get correct answer for the questions

            // check of user selected answer matches with correct answer
            if(getQuestionAnswer==getUserSelectedOption){
                correctAnswer++;// increase correct answer
            }
        }
        return correctAnswer;
    }
}