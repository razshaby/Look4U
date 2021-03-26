package com.razchen.look4u.java_classes;

import com.razchen.look4u.server.Boundaries.QuestionBoundary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Question implements Serializable {
    private String selectedAnswer;
    private String question;
    private String englishQuestion;
   private Map<String,String> answers;
    private int numOfAnswers;
    private String idDB;

    public Question() {
        answers=new HashMap<>();
        numOfAnswers=0;
    }


    public void removeLastAnswer()
    {
        if(answers.size()!=0)
        {
            numOfAnswers--;
            answers.remove(numOfAnswers);
        }
    }

    public void addAnswer(String answer)
    {
        answers.put(numOfAnswers+"",answer);
        numOfAnswers++;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public String getEnglishQuestion() {
        return englishQuestion;
    }

    public void setEnglishQuestion(String englishQuestion) {
        this.englishQuestion = englishQuestion;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public int getNumOfAnswers() {
        return numOfAnswers;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public void setNumOfAnswers(int numOfAnswers) {
        this.numOfAnswers = numOfAnswers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
    public String getIdDB() {
        return idDB;
    }

    public void setIdDB(String idDB) {
        this.idDB = idDB;
    }
    public QuestionBoundary toBoundary()
    {
        String[] answersArray= new String[answers.size()];

        QuestionBoundary questionBoundary = new QuestionBoundary();
        questionBoundary.setQuestion(question);
        questionBoundary.setSelectedAnswer(Integer.parseInt(selectedAnswer));
        int i=0;
        for (String answer : answers.values())
            answersArray[i++]=answer;
        questionBoundary.setAnswers(answersArray);
        questionBoundary.setNumOfAnswers(i);
        questionBoundary.setIdDB(idDB);
        return questionBoundary;
    }
}
