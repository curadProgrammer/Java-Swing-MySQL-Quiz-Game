package screens;

import constants.CommonConstants;
import database.Answer;
import database.Category;
import database.JDBC;
import database.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class QuizScreenGui extends JFrame implements ActionListener{
    private JLabel scoreLabel;
    private JTextArea questionTextArea;
    private JButton[] answerButtons;
    private JButton nextButton;

    // current quiz category
    private Category category;

    // question based on category
    private ArrayList<Question> questions;
    private Question currentQuestion;
    private int currentQuestionNumber;
    private int numOfQuestions;
    private int score;
    private boolean firstChoiceMade;

    public QuizScreenGui(Category category, int numOfQuestions){
        super("Quiz Game");
        setSize(400, 565);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(CommonConstants.LIGHT_BLUE);

        answerButtons = new JButton[4];
        this.category = category;

        // load the questions based of category;
        questions = JDBC.getQuestions(category);

        // adjust number of questions to choose the min between the user's input and the total number of
        // questions in the database
        this.numOfQuestions = Math.min(numOfQuestions, questions.size());

        // load the answers for each question
        for(Question question : questions){
            ArrayList<Answer> answers = JDBC.getAnswers(question);
            question.setAnswers(answers);
        }

        // load current question
        currentQuestion = questions.get(currentQuestionNumber);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // topic label
        JLabel topicLabel = new JLabel("Topic: " + category.getCategoryName());
        topicLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topicLabel.setBounds(15, 15 ,250, 20);
        topicLabel.setForeground(CommonConstants.BRIGHT_YELLOW);
        add(topicLabel);

        // score label
        scoreLabel = new JLabel("Score: " + score + "/" + numOfQuestions);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBounds(270, 15, 96, 20);
        scoreLabel.setForeground(CommonConstants.BRIGHT_YELLOW);
        add(scoreLabel);

        // question text area
        questionTextArea = new JTextArea(currentQuestion.getQuestionText());
        questionTextArea.setFont(new Font("Arial", Font.BOLD, 32));
        questionTextArea.setBounds(15, 50, 350, 91);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setEditable(false);
        questionTextArea.setForeground(CommonConstants.BRIGHT_YELLOW);
        questionTextArea.setBackground(CommonConstants.LIGHT_BLUE);
        add(questionTextArea);

        addAnswerComponents();

        // return to title
        JButton returnToTitleButton = new JButton("Return to Title");
        returnToTitleButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnToTitleButton.setBounds(60, 420, 262, 35);
        returnToTitleButton.setBackground(CommonConstants.BRIGHT_YELLOW);
        returnToTitleButton.setForeground(CommonConstants.LIGHT_BLUE);
        returnToTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load title screen
                TitleScreenGui titleScreenGui = new TitleScreenGui();
                titleScreenGui.setLocationRelativeTo(QuizScreenGui.this);

                // dispose of this screen
                QuizScreenGui.this.dispose();

                // display title screen
                titleScreenGui.setVisible(true);
            }
        });
        add(returnToTitleButton);

        // next button
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBounds(240, 470, 80, 35);
        nextButton.setBackground(CommonConstants.BRIGHT_YELLOW);
        nextButton.setForeground(CommonConstants.LIGHT_BLUE);
        nextButton.setVisible(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // hide next button
                nextButton.setVisible(false);

                // reset first choice flag
                firstChoiceMade = false;

                // update current question to the next question
                currentQuestion = questions.get(++currentQuestionNumber);
                questionTextArea.setText(currentQuestion.getQuestionText());

                // reset and update the answer buttons
                for(int i = 0; i < currentQuestion.getAnswers().size(); i++){
                    Answer answer = currentQuestion.getAnswers().get(i);

                    // reset background color for button
                    answerButtons[i].setBackground(Color.WHITE);

                    // update answer text
                    answerButtons[i].setText(answer.getAnswerText());
                }
            }
        });
        add(nextButton);
    }

    private void addAnswerComponents(){
        // apply a 60px vertical space between each answer button
        int verticalSpacing = 60;

        for(int i = 0; i < currentQuestion.getAnswers().size(); i++){
            Answer answer = currentQuestion.getAnswers().get(i);

            JButton answerButton = new JButton(answer.getAnswerText());
            answerButton.setBounds(60, 180 + (i * verticalSpacing), 262, 45);
            answerButton.setFont(new Font("Arial", Font.BOLD, 18));
            answerButton.setHorizontalAlignment(SwingConstants.LEFT);
            answerButton.setBackground(Color.WHITE);
            answerButton.setForeground(CommonConstants.DARK_BLUE);
            answerButton.addActionListener(this);
            answerButtons[i] = answerButton;
            add(answerButtons[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton answerButton = (JButton) e.getSource();

        // find correct answer
        Answer correctAnswer = null;
        for(Answer answer : currentQuestion.getAnswers()){
            if(answer.isCorrect()) {
                correctAnswer = answer;
                break;
            }
        }

        if(answerButton.getText().equals(correctAnswer.getAnswerText())){
            // user chose the right answer

            // change button to green
            answerButton.setBackground(CommonConstants.LIGHT_GREEN);

            // increase score only if it was the first choice
            if(!firstChoiceMade){
                scoreLabel.setText("Score: " + (++score) + "/" + numOfQuestions);
            }

            // check to see if it was the last question
            if(currentQuestionNumber == numOfQuestions - 1){
                // display final results
                JOptionPane.showMessageDialog(QuizScreenGui.this,
                        "You're final score is " + score + "/" + numOfQuestions);
            }else{
                // make next button visible
                nextButton.setVisible(true);
            }
        }else{
            // make button red to indicate incorrect choice
            answerButton.setBackground(CommonConstants.LIGHT_RED);
        }

        firstChoiceMade = true;
    }
}










