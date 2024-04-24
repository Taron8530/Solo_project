package com.example.solo_project.webrtc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.solo_project.R;

public class CallChoiceFragment extends Fragment {

    private Signaling_Socket socket;
    private OnCall_Choice_ClickListener mListener;
    private TextView failed_Call;
    private TextView accept_Call;
    private TextView calling_Username;
    private String username;
    private String TAG = "CallChoiceFragment";

    public CallChoiceFragment(Signaling_Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    public CallChoiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_call_choice, container, false);
        calling_Username = root.findViewById(R.id.call_choice_username);
        failed_Call = root.findViewById(R.id.call_choice_cancel);
        accept_Call = root.findViewById(R.id.call_choice_accept);
        calling_Username.setText(username + "님에게 전화가 오는중");
        failed_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFailedButtonClicked();
            }
        });
        accept_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAcceptButtonClicked();
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCall_Choice_ClickListener) {
            mListener = (OnCall_Choice_ClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }
}