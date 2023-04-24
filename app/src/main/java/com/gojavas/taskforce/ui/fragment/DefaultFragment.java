package com.gojavas.taskforce.ui.fragment;

/**
 * Created by GJS280 on 21/4/2015.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gojavas.taskforce.R;

public class DefaultFragment extends Fragment {

//    private RecyclerView mRecyclerView;
//
//    private DocketListAdapter mAdapter;
//    private ArrayList<DrsEntity> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_default,container,false);

        registerViews(v);
        return v;
    }

    private void registerViews(View view) {
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.default_recyclerview);
//        mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//
//        mAdapter = new DocketListAdapter(mList);
//        mRecyclerView.setAdapter(mAdapter);
    }
}
