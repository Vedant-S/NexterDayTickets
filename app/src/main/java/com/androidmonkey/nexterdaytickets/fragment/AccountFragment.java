package com.androidmonkey.nexterdaytickets.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidmonkey.nexterdaytickets.R;
import com.androidmonkey.nexterdaytickets.activity.HomeActivity;
import com.androidmonkey.nexterdaytickets.activity.LoginActivity;

public class AccountFragment extends Fragment {

    //UI Declaration
    TextView textViewFGAccountName,textViewFGAccountEmail;
    Button buttonFGAccountLogOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container, false);
        textViewFGAccountName = view.findViewById(R.id.textViewFGAAccountName);
        textViewFGAccountEmail = view.findViewById(R.id.textViewFGAAccountEmail);
        buttonFGAccountLogOut = view.findViewById(R.id.buttonFGAAccountLogOut);

        final SharedPreferences userDetailsSharedPreferences = getActivity().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        String email = userDetailsSharedPreferences.getString("email", null);
        String name = userDetailsSharedPreferences.getString("name", null);
        textViewFGAccountEmail.setText("Email: "+email);
        textViewFGAccountName.setText("Name: "+name);

        buttonFGAccountLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor logOutEditor = userDetailsSharedPreferences.edit();
                logOutEditor.clear();
                logOutEditor.commit();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}
