package com.example.formostimuyglamam;

public class QuestionList {
    private final String questions,option1,option2,option3;
    private final int answer;
    private int userSelectedAnswer;
    private QuestionList add;

    public QuestionList(String question, String option1, String option2, String option3, int answer) {
        this.questions = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.answer = answer;
        this.userSelectedAnswer=0;

    }

    public String getQuestion() {

        return questions;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public int getAnswer() {
        return answer;
    }

    public int getUserSelectedAnswer() {
        return userSelectedAnswer;
    }

    public void setUserSelectedAnswer(int userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }

    public void add(QuestionList questionList) {
        this.add=questionList;
    }
}
