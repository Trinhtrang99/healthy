package com.example.healthy.model;

public class Question {
   private String question;

   private Answer answerA;
   private Answer answerB;
   private Answer answerC;
   private Answer answerD;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Answer getAnswerA() {
        return answerA;
    }

    public void setAnswerA(Answer answerA) {
        this.answerA = answerA;
    }

    public Answer getAnswerB() {
        return answerB;
    }

    public void setAnswerB(Answer answerB) {
        this.answerB = answerB;
    }

    public Answer getAnswerC() {
        return answerC;
    }

    public void setAnswerC(Answer answerC) {
        this.answerC = answerC;
    }

    public Answer getAnswerD() {
        return answerD;
    }

    public void setAnswerD(Answer answerD) {
        this.answerD = answerD;
    }

    public Question(String question, Answer answerA, Answer answerB, Answer answerC, Answer answerD) {
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
    }

    public class Answer{
        private Boolean ans;
        private String nameAns;

        public Boolean getAns() {
            return ans;
        }

        public void setAns(Boolean ans) {
            this.ans = ans;
        }

        public String getNameAns() {
            return nameAns;
        }

        public void setNameAns(String nameAns) {
            this.nameAns = nameAns;
        }

        public Answer(Boolean ans, String nameAns) {
            this.ans = ans;
            this.nameAns = nameAns;
        }
    }
}
