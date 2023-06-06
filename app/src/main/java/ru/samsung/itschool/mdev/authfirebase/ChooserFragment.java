package ru.samsung.itschool.mdev.authfirebase;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.samsung.itschool.mdev.authfirebase.databinding.FragmentChooserBinding;

public class ChooserFragment extends Fragment {

    private FragmentChooserBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentChooserBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.desc_google_sign_in));
        list.add(getResources().getString(R.string.desc_emailpassword));
        list.add(getString(R.string.desc_phone_auth));
        MyAdapter adapter = new MyAdapter(list,getContext());
        mBinding.list.setAdapter(adapter);
    }
}
