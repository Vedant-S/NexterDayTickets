package com.androidmonkey.nexterdaytickets.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.androidmonkey.nexterdaytickets.R;
import com.androidmonkey.nexterdaytickets.activity.AddEventActivity;
import com.github.ybq.android.spinkit.SpinKitView;

public class EventsFragment extends Fragment {

    //UI Declarations
    RecyclerView recyclerViewEvents;
    SpinKitView spinKitEventProgressBar;
    RelativeLayout relativeLayoutLoadingFailed;
    ImageView imageViewFGEAddEvent,imageViewFGERefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events,container, false);
        relativeLayoutLoadingFailed = view.findViewById(R.id.relativeLayoutLoadingFailed);
        recyclerViewEvents =view.findViewById(R.id.recyclerViewEvents);
        spinKitEventProgressBar = view.findViewById(R.id.spinKitEventProgressBar);
        imageViewFGEAddEvent = view.findViewById(R.id.imageViewFGEAddEvent);
        imageViewFGERefresh = view.findViewById(R.id.imageViewFGERefresh);
        imageViewFGEAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddEventActivity.class));
            }
        });
        imageViewFGERefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


}
