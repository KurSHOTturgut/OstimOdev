package com.example.formostimuyglamam;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class InstructionsDialog extends Dialog {


    private int instructionPoints=0;

    public InstructionsDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_dialog_layout);

        final AppCompatButton continueBtn=findViewById(R.id.continueBtn);
        final TextView instructionsTV=findViewById(R.id.instructionsTv);

        setInstructionPoint(instructionsTV,"1.Soruları cevaplamak için 10 Dakikanız bulunmaktadır.");



        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setInstructionPoint(TextView instructionTv,String instructionPoint){

        if (instructionPoints==0){
            instructionTv.setText(instructionPoint);
        }
        else{
            instructionTv.setText(instructionTv.getText()+"\n\n"+instructionPoint);
        }
    }
}
