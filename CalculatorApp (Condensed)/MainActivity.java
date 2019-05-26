//ADITYA ABHYANKAR.
//Note: I did order of operations.
//The section of code that seems double spaced
//was copied from my own email which I emailed to myself
//after figuring out the solution on eclipse on my laptop.

//T.L.D.R: It's all my own work!


package com.example.a10010897.calculatorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ArrayList<Button> buttons = new ArrayList<Button>();
    Button equalsButton;
    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons.add((Button)findViewById(R.id.button));
        buttons.add((Button)findViewById(R.id.button2));
        buttons.add((Button)findViewById(R.id.button3));
        buttons.add((Button)findViewById(R.id.button5));
        buttons.add((Button)findViewById(R.id.button6));
        buttons.add((Button)findViewById(R.id.button7));
        buttons.add((Button)findViewById(R.id.button9));
        buttons.add((Button)findViewById(R.id.button10));
        buttons.add((Button)findViewById(R.id.button11));
        buttons.add((Button)findViewById(R.id.button14));


        buttons.add((Button)findViewById(R.id.plusButton));
        buttons.add((Button)findViewById(R.id.minusButton));
        buttons.add((Button)findViewById(R.id.timesButton));
        buttons.add((Button)findViewById(R.id.divideButton));

        buttons.add((Button)findViewById(R.id.clearButton));

        equalsButton = (Button)findViewById(R.id.equalsButton);

        display = (TextView)findViewById(R.id.display);

        for (Button b : buttons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (display.getText().equals("ERROR!"))
                        display.setText("0");

                    if (display.getText().length()<8)
                        display.setText(display.getText() + "" + ((Button)v).getText());

                    if (((Button)v).equals(buttons.get(buttons.size()-1))) {
                        display.setText("0");
                    }
                    else if (display.getText().charAt(0)=='0')
                        display.setText(display.getText().subSequence(1, display.getText().length()));
                }
            });
        }

        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expression = (String) display.getText();
                if (expression.charAt(expression.length()-1)=='+' || expression.charAt(expression.length()-1)=='-' || expression.charAt(expression.length()-1)=='*' || expression.charAt(expression.length()-1)=='/') {
                    display.setText("ERROR!");
                }
                if (expression.charAt(0)=='+' || expression.charAt(0)=='-' || expression.charAt(0)=='*' || expression.charAt(0)=='/')
                    display.setText("ERROR!");

                ArrayList<String> operators = new ArrayList<String>();
                operators.add("/");
                operators.add("*");
                operators.add("+");
                operators.add("-");

                for (String o1 : operators) {
                    for (String o2 : operators) {
                        if (expression.contains(o1 + "" + o2)) {
                            display.setText("ERROR!");
                        }
                    }
                }

                if (!display.getText().equals("ERROR!")) {
                    // Implement code to perform operations here

                    String express = expression, result = "";

                    for (int index=-1;index<express.length()-1;index++) {

                        char c = '|';

                        try {

                            c = express.toCharArray()[index];

                        } catch (Exception e){}

//for (char c : express.toCharArray()) {

                        if (c=='/' || c=='*') {

                            int i=index, j=index;

                            Exception ex = new Exception();

                            do {

                                try {

                                    i--;

//String substr = express.substring(i, index);

                                    Double.parseDouble(express.substring(i, index));

                                } catch (Exception e) {

                                    ex = e;

                                    i++;

                                }

                            } while(!((operators.contains(express.substring(i, i+1))) || (ex instanceof NumberFormatException) || (ex instanceof StringIndexOutOfBoundsException)));


                            if (operators.contains(express.substring(i, i+1))) {

                                i++;

                            }


                            ex = new Exception();


                            do {

                                try {

                                    j++;

                                    String substr = express.substring(index+1, j+1);

                                    Double.parseDouble(express.substring(index+1, j+1));

                                } catch (Exception e) {

                                    ex = e;

                                    j--;

                                }

                            } while(!((operators.contains(express.substring(index+1, j+1))) || (ex instanceof NumberFormatException) || (ex instanceof StringIndexOutOfBoundsException)));


                            if (operators.contains(express.substring(index+1, j+1))) {

                                j--;

                            }


                            try {

//System.out.println(express.substring(i, index) + "      " + express.substring(index, j+1));

                                String delim = express.substring(i, index) + "\\" + express.substring(index, j+1);

                                result = express.split(delim)[0];

                            } catch (Exception e){}


//String answer = performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1));


                            result = result + performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1)) + express.substring(j+1);

                            index = -1 + i + performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1)).length();

                            express = result;

                        }

                    }


                    System.out.println(result);


                    for (int index=-1;index<express.length()-1;index++) {

//index++;

                        char c = '|';

                        try {

                            c = express.toCharArray()[index];

                        } catch (Exception e){}

//for (char c : express.toCharArray()) {

                        if (c=='+' || c=='-') {

                            int i=index, j=index;

                            Exception ex = new Exception();

                            do {

                                try {

                                    i--;

//String substr = express.substring(i, index);

                                    Double.parseDouble(express.substring(i, index));

                                } catch (Exception e) {

                                    ex = e;

                                    i++;

                                }

                            } while(!((operators.contains(express.substring(i, i+1))) || (ex instanceof NumberFormatException) || (ex instanceof StringIndexOutOfBoundsException)));


                            if (operators.contains(express.substring(i, i+1))) {

                                i++;

                            }


                            ex = new Exception();


                            do {

                                try {

                                    j++;

                                    String substr = express.substring(index+1, j+1);

                                    Double.parseDouble(express.substring(index+1, j+1));

                                } catch (Exception e) {

                                    ex = e;

                                    j--;

                                }

                            } while(!((operators.contains(express.substring(index+1, j+1))) || (ex instanceof NumberFormatException) || (ex instanceof StringIndexOutOfBoundsException)));


                            if (operators.contains(express.substring(index+1, j+1))) {

                                j--;

                            }


                            try {

                                String delim = express.substring(i, index) + "\\" + express.substring(index, j+1);

                                result = result.split(delim)[0];

                            } catch (Exception e){}


//String answer = performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1));

                            int operations = 0;


                            for (char q : result.toCharArray())

                                if (operators.contains("" + q))

                                    operations++;


                            if (operations==1)

                                result = "";


                            result = result + performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1)) + express.substring(j+1);

                            index = -1 + i + performOperation(Double.parseDouble(express.substring(i, index)), Double.parseDouble(express.substring(index+1, j+1)), express.substring(index, index+1)).length();

                            express = result;

                        }

                    }


                    display.setText(result);

                }
            }
        });
    }

    public String performOperation(double num1, double num2, String operator) {


        switch (operator) {

            case "+": return "" + (num1+num2);

            case "-": return "" + (num1-num2);

            case "*": return "" + (num1*num2);

            case "/": return "" + (num1/num2);

            default: return "";

        }

    }
}
