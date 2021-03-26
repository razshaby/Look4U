package com.razchen.look4u.server.Boundaries;

import com.razchen.look4u.java_classes.Question;

import java.util.Arrays;

public class QuestionBoundary implements Comparable<QuestionBoundary>{
    private String question;
    private String englishQuestion;
    private String[] answers;
    private int numOfAnswers;
    private String idDB;
    private double similarity = 0;
    private int selectedAnswer;

    public QuestionBoundary() {
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEnglishQuestion() {
        return englishQuestion;
    }

    public void setEnglishQuestion(String englishQuestion) {
        this.englishQuestion = englishQuestion;
    }

    public String[] getAnswers() {
        return answers;
    }




    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public int getNumOfAnswers() {
        return numOfAnswers;
    }

    public void setNumOfAnswers(int numOfAnswers) {
        this.numOfAnswers = numOfAnswers;
    }

    public Question toEntityQuestion()
    {
        Question questionEntity = new Question();
        for (String answer:answers) {
            questionEntity.addAnswer(answer);
        }
        questionEntity.setQuestion(this.question);
        questionEntity.setIdDB(this.idDB);
        questionEntity.setSelectedAnswer(selectedAnswer+"");
        return  questionEntity;
    }

    public String getIdDB() {
        return idDB;
    }

    public void setIdDB(String idDB) {
        this.idDB = idDB;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    @Override
    public String toString() {
        return "QuestionBoundary{" +
                "question='" + question + '\'' +
                ", englishQuestion='" + englishQuestion + '\'' +
                ", answers=" + Arrays.toString(answers) +
                ", numOfAnswers=" + numOfAnswers +
                '}';
    }

    @Override
    public int compareTo(QuestionBoundary questionBoundary) {
        return this.question.compareTo(questionBoundary.getQuestion());
    }
}
