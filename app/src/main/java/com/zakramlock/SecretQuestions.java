package com.zakramlock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zakramlock.config.Config;

public class SecretQuestions extends AppCompatActivity {

    private static final String TAG = SecretQuestions.class.getName();
    private RadioGroup radioQuestionGroup;
    private Button cancel;
    private Button confirm;
    private TextView answer;
    private Config conf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_questions);

        answer = (TextView) findViewById(R.id.answer);
        radioQuestionGroup = (RadioGroup) findViewById(R.id.radioSqList);
        conf = Config.getInstance(getApplicationContext());
        cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        confirm = (Button) findViewById(R.id.button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioQuestionGroup.getCheckedRadioButtonId() == -1){
                        dialog(R.string.empty_radio);
                    return;
                }

                if(!answer.getText().toString().isEmpty()){
                    RadioButton radioButton = (RadioButton)radioQuestionGroup.findViewById(radioQuestionGroup.getCheckedRadioButtonId());
                    String selection = (String) radioButton.getText();
                    conf.setSecureQ(selection);
                    conf.setAnswerQ(answer.getText().toString());
                    setResult(RESULT_OK);
                    finish();
                }else {
                    dialog(R.string.empty_answer);
                }
            }
        });
    }

    private void dialog(int id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog));
        alertDialogBuilder.setTitle(R.string.name_app);
        alertDialogBuilder
                .setMessage(id)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
