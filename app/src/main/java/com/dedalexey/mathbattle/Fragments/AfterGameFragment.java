package com.dedalexey.mathbattle.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dedalexey.mathbattle.R;
import com.dedalexey.mathbattle.model.MenuRow;
import com.dedalexey.mathbattle.model.SessionInfo;
import com.dedalexey.mathbattle.model.Statistics;

import java.util.ArrayList;
import java.util.List;


public class AfterGameFragment extends GameFragment {

    public static final String TAG = AfterGameFragment.class.getSimpleName();

    private EditText mPlayerEditText;
    private ImageButton mSaveButton;
    private View mSaveLayout;
    private List<MenuRow> mMenuRowList = new ArrayList<>();
    private CallBacks mCallBacks;
    private MenuFragment mMenuFragment;
    private StatisticsFragment mStatisticFragment;

    public interface CallBacks {
        void onRepeatClick();

    }


    public static AfterGameFragment newInstance(SessionInfo sessionInfo) {

        AfterGameFragment fragment = new AfterGameFragment();
        fragment.setSessionInfo(sessionInfo);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCallBacksListener();
        mMenuRowList = getMenuRowList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_after_game, container, false);
        mPlayerEditText = view.findViewById(R.id.player_name_edit_text);
        mSaveButton = view.findViewById(R.id.save_button);
        mSaveLayout = view.findViewById(R.id.result_save_view);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = mPlayerEditText.getText().toString();
                if(playerName.length()>0) {
                    mSessionInfo.setPlayerName(playerName);
                }
                Log.d(TAG,"Before Save: "+mSessionInfo.toString());
                Statistics.get(getActivity()).addSessionInfo(mSessionInfo);
                mSaveLayout.setVisibility(View.GONE);
                mStatisticFragment.updateUI();
                closeKeyBoard();
                mStatisticFragment.scrollToPositionOf(mSessionInfo);

            }
        });

        FragmentManager fm = getChildFragmentManager();

        mMenuFragment = (MenuFragment) fm.findFragmentById(R.id.menu_container);

        mStatisticFragment = (StatisticsFragment) fm.findFragmentById(R.id.statistic_container);
        if(mMenuFragment ==null) {
            mMenuFragment = MenuFragment.newInstance(mMenuRowList,R.layout.layout_row_image_text);
            fm.beginTransaction()
                    .replace(R.id.menu_container, mMenuFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        if(mStatisticFragment ==null){
            mStatisticFragment = StatisticsFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.statistic_container, mStatisticFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        if(!Statistics.get(getActivity()).verifyForAdd(mSessionInfo)
                || Statistics.get(getActivity()).find(mSessionInfo)){
            mSaveLayout.setVisibility(View.GONE);
            //mStatisticFragment.scrollToPositionOf(mSessionInfo);

        }



        return view;
    }


    private void initCallBacksListener() {
        Fragment fragment = getParentFragment();
        if ( fragment instanceof CallBacks) {
            mCallBacks = (CallBacks) fragment;
        } else {
            Log.i(TAG,"fragment NOT instanceof CallBacks");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallBacks = null;
    }
    private void closeKeyBoard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public List<MenuRow> getMenuRowList(){
        List<MenuRow> menuRowList = new ArrayList<>();
        MenuRow homeMenuRow = new MenuRow(getString(R.string.home), R.drawable.ic_action_home);
        homeMenuRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        MenuRow repeatMenuRow = new MenuRow(getString(R.string.repeat), R.drawable.ic_action_repeat);
        repeatMenuRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallBacks!=null) {
                    mCallBacks.onRepeatClick();
                    mStatisticFragment.updateUI();
                }
            }
        });


        MenuRow exitMenuRow = new MenuRow(getString(R.string.exit), R.drawable.ic_action_exit);
        exitMenuRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finishAffinity();
            }
        });

        menuRowList.add(homeMenuRow);
        menuRowList.add(repeatMenuRow);
        menuRowList.add(exitMenuRow);
        return menuRowList;
    }
}
