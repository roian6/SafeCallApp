package com.david0926.safecall;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.david0926.safecall.adapter.TypeAdapter;
import com.david0926.safecall.api.RetrofitAPI;
import com.david0926.safecall.databinding.FragmentMain1Binding;
import com.david0926.safecall.databinding.FragmentMain2Binding;
import com.david0926.safecall.model.TypeModel;
import com.david0926.safecall.util.LinearLayoutManagerWrapper;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment2 extends Fragment {

    public static MainFragment2 newInstance() {
        return new MainFragment2();
    }

    private ObservableArrayList<TypeModel> items = new ObservableArrayList<>();

    private Context mContext;
    private FragmentMain2Binding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main2, container, false);

        LinearLayoutManagerWrapper wrapper = new LinearLayoutManagerWrapper(
                mContext, LinearLayoutManager.VERTICAL, false);
        binding.recyclerMain2.setLayoutManager(wrapper);

        TypeAdapter adapter = new TypeAdapter();
        binding.recyclerMain2.setAdapter(adapter);
        binding.setItems(items);

        adapter.setOnItemClickListener((view, item) -> {});
        adapter.setOnItemLongClickListener((view, item) -> true);

        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        Call<ResponseBody> mCallResponse = mRetrofitAPI.getAddress();
        mCallResponse.enqueue(mRetrofitCallBack);

        return binding.getRoot();
    }

    private Callback<ResponseBody> mRetrofitCallBack =new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                JSONObject responseObject = new JSONObject(response.body().string());
                JSONArray responseList = responseObject.getJSONArray("data");

                Gson gson = new Gson();

                for(int i=0;i<responseList.length();i++){
                    TypeModel model = gson.fromJson(responseList.get(i).toString(), TypeModel.class);
                    items.add(model);
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
