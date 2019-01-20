package com.sum.scanner.mysuccessfulthesis.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sum.scanner.mysuccessfulthesis.MainActivity;
import com.sum.scanner.mysuccessfulthesis.R;

public class ResultFragment extends Fragment {

    private Context mContext;
    private Activity mFragmentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_fragment, container, false);

        final String result = ((MainActivity) mFragmentActivity).getResult();
        if (result != null) {
            LinearLayout resultPlaceholder = view.findViewById(R.id.result_placeholder);
            resultPlaceholder.setVisibility(View.VISIBLE);

            TextView resultText = view.findViewById(R.id.result_text);
            resultText.setText(result);

            LinearLayout positiveResultButtons = view.findViewById(R.id.positive_result_buttons);
            positiveResultButtons.setVisibility(View.VISIBLE);

            ImageButton copyBtn = view.findViewById(R.id.copy_button);
            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) mFragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("codeScannerResult", result);
                    clipboard.setPrimaryClip(clip);
                }
            });

            ImageButton shareBtn = view.findViewById(R.id.share_button);
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, result);
                    startActivity(Intent.createChooser(intent, getString(R.string.share)));
                }
            });

            ImageButton browserBtn = view.findViewById(R.id.open_in_browser_button);
            browserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = result;
                    url = (!result.startsWith("http://") && !result.startsWith("https://")) ? "http://" + url : url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });

        } else {
            LinearLayout noResultPlaceholder = view.findViewById(R.id.no_result_placeholder);
            noResultPlaceholder.setVisibility(View.VISIBLE);

            Button tryAgainBtn = view.findViewById(R.id.try_again_result_button);
            tryAgainBtn.setVisibility(View.VISIBLE);
            tryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.code_scanner));
                }
            });

        }

        return view;
    }
}
