package com.example.formostimuyglamam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {

    //creating quesitons list
    private final List<QuestionList>questionLists=new ArrayList<>();


    private TextView quizTimer;

    private RelativeLayout option1Layout,option2Layout,option3Layout;
    private TextView option1TV,option2TV,option3TV;
    private ImageView option1Icon,option2Icon,option3Icon;


    private TextView questionTV;
    private TextView totalQuestionTV;
    private TextView currentQuestions;



    //creating Database Reference from URL
    private final  DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://otuformuygulamasi-default-rtdb.firebaseio.com/");

    //CountDown Timer for Quiz
    private CountDownTimer countDownTimer;

    //current  questions position
    private int currentQuestionPosition=0;

    //selected option number . Value must be between 1-3(we have 3 options)
    private int selectedOption=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        quizTimer=findViewById(R.id.quizTimer);

        option1Layout=findViewById(R.id.option1Laylout);
        option2Layout=findViewById(R.id.option2Laylout);
        option3Layout=findViewById(R.id.option3Laylout);

        option1TV=findViewById(R.id.option1TV);
        option2TV=findViewById(R.id.option2TV);
        option3TV=findViewById(R.id.option3TV);

        option1Icon=findViewById(R.id.option1Icon);
        option2Icon=findViewById(R.id.option2Icon);
        option3Icon=findViewById(R.id.option3Icon);

        questionTV=findViewById(R.id.questionTV);
        totalQuestionTV=findViewById(R.id.totalQuestionsTV);
        currentQuestions=findViewById(R.id.currentQuestionTV);

        final AppCompatButton nextBtn=findViewById(R.id.nextQuestionBtn);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                       interstitialAd.show(QuestionsActivity.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Toast.makeText(QuestionsActivity.this, "hata reklam gelmedi", Toast.LENGTH_SHORT).show();
                    }
                });

        //show instrucations dialog
        InstructionsDialog instructionsDialog=new InstructionsDialog(QuestionsActivity.this);
        instructionsDialog.setCancelable(false);
        instructionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsDialog.show();

        //getting data(questions and quiz time) from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int getQuizTime=Integer.parseInt(snapshot.child("time\n").getValue(String.class));

                for (DataSnapshot questions : snapshot.child("questions").getChildren()){
                    String getQuestion=questions.child("question").getValue(String.class);
                    String getOption1=questions.child("\n" + "option1\n").getValue(String.class);
                    String getOption2=questions.child("\n" + "option2\n").getValue(String.class);
                    String getOption3=questions.child("\n" + "option3\n").getValue(String.class);

                    int getAnswer=Integer.parseInt(questions.child("\n" + "answer\n").getValue(String.class));

                    //creating questions list object and add details
                    QuestionList questionList=new QuestionList(getQuestion,getOption1,getOption2,getOption3,getAnswer);

                    //adding questionslist object into the list
                    questionList.add(questionList);

                }

                //setting total questions to TextView
                totalQuestionTV.setText("/"+questionLists.size());

                //start quiz time
                startQuizTimer(getQuizTime);

                //select first question by default
                selectQuestion(currentQuestionPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, "Veri tabanına bağlanırken hata ile karışlaşıldı ", Toast.LENGTH_SHORT).show();
            }
        });

        option1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assing 1 as first option is selected
                selectedOption=1;

                //select option
                selectOption(option1Layout,option1Icon);

            }
        });
        option2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assing 2 as second option is selected
                selectedOption=2;

                //select option
                selectOption(option2Layout,option2Icon);
            }
        });
        option3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assing 3 as third option is selected
                selectedOption=3;

                //select option
                selectOption(option3Layout,option3Icon);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if user has select an opiton or not
                if (selectedOption!=0){

                    //set user selected answer
                    questionLists.get(currentQuestionPosition).setUserSelectedAnswer(selectedOption);

                    //reset selected option to default value(0)
                    selectedOption=0;
                    currentQuestionPosition++; //increase current question value to getting next question

                    //check if list more questions
                    if(currentQuestionPosition<questionLists.size()){
                            selectQuestion(currentQuestionPosition); // select question
                    }
                    else{
                        //list has no questions left so finish the quiz
                        countDownTimer.cancel(); //stop countdown timer
                        finishQuiz(); // finish quiz
                    }
                }
                else{
                    Toast.makeText(QuestionsActivity.this, "lütfen bir şık seçiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void finishQuiz(){

        //creating intent to open QuizResult activity
        Intent intent =new Intent(QuestionsActivity.this,QuizResult.class);

        //creating bundle to pass quizQuesitonsList
        Bundle bundle= new Bundle();
        bundle.putSerializable("questions",(Serializable) questionLists);

        //add bundle to intent
        intent.putExtras(bundle);

        //start activity to open QuizResult activity
        startActivity(intent);

        //destroy current acitivty
        finish();
    }

    private void startQuizTimer(int maxTimeInSeconds){
        countDownTimer=new CountDownTimer(maxTimeInSeconds*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long getHour= TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long getMinute=TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long getSecond=TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                String generateTime=String.format(Locale.getDefault(),"%02d:%02d:%02d",getHour,
                        getMinute-TimeUnit.HOURS.toMinutes(getHour),
                        getSecond-TimeUnit.MINUTES.toSeconds(getMinute));

                quizTimer.setText(generateTime);
            }

            @Override
            public void onFinish() {
                // finish quiz when time i finished
                finishQuiz();

            }
        };
        //start timer
        countDownTimer.start();
    }

    private void selectQuestion(int questionListPosition){

        resetOptions();
        //getting question details and set to TextViews
        questionTV.setText(questionLists.get(questionListPosition).getQuestion());
        option1TV.setText(questionLists.get(questionListPosition).getOption1());
        option2TV.setText(questionLists.get(questionListPosition).getOption2());
        option3TV.setText(questionLists.get(questionListPosition).getOption3());

        //setting current question number to TextView
        currentQuestions.setText("Question"+(questionListPosition+1));

    }

    private void resetOptions(){

        option1Layout.setBackgroundResource(R.drawable.round_back_white50_10);
        option2Layout.setBackgroundResource(R.drawable.round_back_white50_10);
        option3Layout.setBackgroundResource(R.drawable.round_back_white50_10);

        option1Icon.setImageResource(R.drawable.round_back_white50_100);
        option2Icon.setImageResource(R.drawable.round_back_white50_100);
        option3Icon.setImageResource(R.drawable.round_back_white50_100);

    }

    private void selectOption(RelativeLayout selectOptionLayout,ImageView selectedOptionIcon){

        //reset options to select new option
        resetOptions();

        selectedOptionIcon.setImageResource(R.drawable.check);
        selectOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option);
    }
}