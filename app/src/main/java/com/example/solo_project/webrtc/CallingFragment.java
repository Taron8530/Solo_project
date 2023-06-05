package com.example.solo_project.webrtc;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solo_project.R;

public class CallingFragment extends Fragment{
    private Signaling_Socket socket;
    private OnCallingClickListener mListener;
    private TextView cancel_Call;
    private TextView calling_Username;
    private String username;
    private String TAG = "CallingFragment";
    public CallingFragment(Signaling_Socket socket,String username) {
        this.socket = socket;
        this.username = username;
    }
    public CallingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCallingClickListener) {
            mListener = (OnCallingClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_calling, container, false);
        cancel_Call = root.findViewById(R.id.calling_cancel);
        calling_Username = root.findViewById(R.id.calling_user_name);
        calling_Username.setText(username + "님에게 전화를 거는증");
        cancel_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancelButtonClicked();
            }
        });
        return root;
    }
}