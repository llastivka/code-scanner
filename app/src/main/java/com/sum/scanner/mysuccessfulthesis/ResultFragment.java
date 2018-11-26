package com.sum.scanner.mysuccessfulthesis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_fragment, container, false);

        TextView resultText = view.findViewById(R.id.result_text);
        resultText.setText("http://www.pja.edu.pl/en/news/druzyna-pjatk-w-finalach-e-sportowych-akademickich-mistrzostw-polski");

        return view;
    }
}
