package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.heavn.honesty.R;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class HonestyFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroup;
    private RadioButton myTask,allTask;
    private MyTaskFragment myTaskFragment;
    private AllTaskFragment allTaskFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_honesty,container,false);
        init(view);
        return view;
    }

    private void init(View view){
        myTaskFragment = new MyTaskFragment();
        allTaskFragment = new AllTaskFragment();

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        myTask = view.findViewById(R.id.radioButton_myTask);
        myTask.setChecked(true);
        allTask = view.findViewById(R.id.radioButton_allTask);
        allTask.setChecked(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId){
            case R.id.radioButton_myTask:
                if (myTaskFragment == null) {
                    myTaskFragment = new MyTaskFragment();
                }
                transaction.replace(R.id.honesty_frameLayout, myTaskFragment);
                break;
            case R.id.radioButton_allTask:
                if (allTaskFragment == null) {
                    allTaskFragment = new AllTaskFragment();
                }
                transaction.replace(R.id.honesty_frameLayout, allTaskFragment);
                break;
            default:
                break;
        }
        transaction.commit();// 事务提交
    }
}
