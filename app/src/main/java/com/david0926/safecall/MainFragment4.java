package com.david0926.safecall;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.david0926.safecall.databinding.DialogKeywordBinding;
import com.david0926.safecall.databinding.DialogThemeBinding;
import com.david0926.safecall.databinding.FragmentMain4Binding;
import com.david0926.safecall.util.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;

public class MainFragment4 extends Fragment {

    public static MainFragment4 newInstance() {
        return new MainFragment4();
    }

    private Context mContext;
    private FragmentMain4Binding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main4, container, false);

        binding.btnMain4Alert.setOnClickListener(view -> {
            DialogKeywordBinding dialogKeywordBiding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.dialog_keyword, null, false);
            dialogKeywordBiding.setKeyword(SharedPreferenceUtil.getString(mContext, "keyword", ""));

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogKeywordBiding.getRoot());
            AlertDialog dialog = builder.create();

            dialogKeywordBiding.btnDialogsendConfirm.setOnClickListener(view1 -> {
                SharedPreferenceUtil.putString(mContext, "keyword", dialogKeywordBiding.getKeyword());
                dialog.dismiss();
                Toast.makeText(mContext, "성공적으로 저장했습니다!", Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        });

        binding.btnMain4Setting.setOnClickListener(view -> {
            Toast.makeText(mContext, "프로토타입에서는 아직 지원하지 않는 기능입니다.", Toast.LENGTH_SHORT).show();
        });

        binding.btnMain4Theme.setOnClickListener(view -> {
            DialogThemeBinding dialogThemeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.dialog_theme, null, false);

            String[] list = getResources().getStringArray(R.array.call_theme);

            dialogThemeBinding.spinnerDialogtheme.setAdapter(new ArrayAdapter<>(mContext,
                    android.R.layout.simple_spinner_dropdown_item, list));

            if (!SharedPreferenceUtil.getString(mContext, "call_theme", "").equals("")) {
                if (SharedPreferenceUtil.getString(mContext, "call_theme", "").equals("samsung"))
                    dialogThemeBinding.spinnerDialogtheme.setSelection(0);
                else dialogThemeBinding.spinnerDialogtheme.setSelection(1);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogThemeBinding.getRoot());
            AlertDialog dialog = builder.create();

            dialogThemeBinding.btnDialogthemeConfirm.setOnClickListener(view1 -> {
                if (dialogThemeBinding.spinnerDialogtheme.getSelectedItem().equals("테마 1"))
                    SharedPreferenceUtil.putString(mContext, "call_theme", "samsung");
                else
                    SharedPreferenceUtil.putString(mContext, "call_theme", "tcall");

                dialog.dismiss();
            });

            dialog.show();
        });
        return binding.getRoot();
    }
}
