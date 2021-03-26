package com.razchen.look4u.java_classes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Questionnaire implements Serializable {
    private int numOfQuestions;
    private String gender;
    private String Creator_of_the_questionnaire;
    private String category;
    private String id;
    private int ageFrom;
    private int ageTo;
    Map<String, Question> questions;

    public Questionnaire() {
        questions=new HashMap<>();

    }


    public Questionnaire(String creator_of_the_questionnaire,String category,int ageFrom,int ageTo,String gender) {
        this();
        this.category=category;
        this.numOfQuestions =0;
        Creator_of_the_questionnaire = creator_of_the_questionnaire;
        this.ageFrom=ageFrom;
        this.ageTo=ageTo;
        this.gender=gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getAgeFrom() {
        return ageFrom;
    }

    public int getAgeTo() {
        return ageTo;
    }

    public int getNumOfQuestions() {
        return numOfQuestions;
    }

    public String getCreator_of_the_questionnaire() {
        return Creator_of_the_questionnaire;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question)
    {
        questions.put(numOfQuestions+"",question);
        numOfQuestions++;
    }

    public void addQuestionbyIndex(Question question,int index)
    {
        questions.put(index+"",question);
        numOfQuestions++;
    }


    public  void replaceQuestionskeys()
    {
        int index=0;
        Map<String, Question> newQuestions=new HashMap<>();
        for (String key : questions.keySet()) {
            newQuestions.put(index + "", questions.get(key));
            index++;
        }
        questions=newQuestions;
    }
    public void removeLastQuestion()
    {
        questions.remove((numOfQuestions-1)+"");
        numOfQuestions--;
    }
    public void removeQuestionByIndex(int index)
    {
        questions.remove(index+"");
        numOfQuestions--;
    }

    public String getCategory() {
        return category;
    }

    public String getGender() {
        return gender;
    }

    public void setNumOfQuestions(int numOfQuestions) {
        this.numOfQuestions = numOfQuestions;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCreator_of_the_questionnaire(String creator_of_the_questionnaire) {
        Creator_of_the_questionnaire = creator_of_the_questionnaire;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAgeFrom(int ageFrom) {
        this.ageFrom = ageFrom;
    }

    public void setAgeTo(int ageTo) {
        this.ageTo = ageTo;
    }

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
    }
}
