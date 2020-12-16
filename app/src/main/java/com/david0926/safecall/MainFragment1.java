package com.david0926.safecall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.david0926.safecall.api.RetrofitAPI;
import com.david0926.safecall.databinding.DialogTypeBinding;
import com.david0926.safecall.databinding.FragmentMain1Binding;
import com.david0926.safecall.model.TypeModel;
import com.david0926.safecall.util.TypeCache;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment1 extends Fragment {

    public static MainFragment1 newInstance() {
        return new MainFragment1();
    }

    private List<TypeModel> types = new ArrayList<>();

    private Context mContext;
    private FragmentMain1Binding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main1, container, false);
        binding.setIsCalling(false);

        binding.setSelectedType(TypeCache.getType(mContext));

        binding.btnMain1Call.setOnClickListener(view -> {
            if (TypeCache.getType(mContext).getName().equals("선택해주세요")) {
                Toast.makeText(mContext, "먼저 대화 상대를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.getIsCalling()) return;
            else binding.setIsCalling(true);

            Toast.makeText(mContext, "5초 뒤에 통화가 시작됩니다...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                mContext.sendBroadcast(new Intent("safecall_call"));
                binding.setIsCalling(false);
            }, 5000);
        });

        binding.btnMain1Type.setOnClickListener(view -> {
            if (types.isEmpty()) {
                Toast.makeText(mContext, "아직 대화 상대를 불러오는 중입니다...", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> list = new ArrayList<>();
            for (TypeModel model : types) {
                list.add(model.getName());
            }

            DialogTypeBinding dialogTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.dialog_type, null, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogTypeBinding.getRoot());
            AlertDialog dialog = builder.create();

            dialogTypeBinding.spinnerDialogtype.setAdapter(new ArrayAdapter<>(mContext,
                    android.R.layout.simple_spinner_dropdown_item, list));

            if (!TypeCache.getType(mContext).getName().equals("선택해주세요"))
                dialogTypeBinding.spinnerDialogtype.setSelection(list.indexOf(TypeCache.getType(mContext).getName()));

            dialogTypeBinding.btnDialogtypeConfirm.setOnClickListener(view1 -> {

                TypeModel selectedModel = types.get(dialogTypeBinding.spinnerDialogtype.getSelectedItemPosition());
                binding.setSelectedType(selectedModel);

                TypeCache.setType(mContext, selectedModel);
                dialog.dismiss();
            });

            dialog.show();


        });

        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        Call<ResponseBody> mCallResponse = mRetrofitAPI.getAddress();
        mCallResponse.enqueue(mRetrofitCallBack);


        return binding.getRoot();
    }

    private Callback<ResponseBody> mRetrofitCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                JSONObject responseObject = new JSONObject(response.body().string());
                JSONArray responseList = responseObject.getJSONArray("data");

                Gson gson = new Gson();

                for (int i = 0; i < responseList.length(); i++) {
                    TypeModel model = gson.fromJson(responseList.get(i).toString(), TypeModel.class);
                    types.add(model);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };
}
