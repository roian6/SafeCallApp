package com.david0926.safecall;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.david0926.safecall.util.SharedPreferenceUtil;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class LandingFragment extends Fragment implements ISlideBackgroundColorHolder {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    static LandingFragment newInstance(int layoutResId) {
        LandingFragment fragment = new LandingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutResId, container, false);
        if (layoutResId == R.layout.activity_landing3) {
            Button btnLand3Permission = v.findViewById(R.id.btn_land3_permission);
            btnLand3Permission.setOnClickListener(view->{
                TedPermission.with(v.getContext())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                SharedPreferenceUtil.putBoolean(getContext(),
                                        "permission_granted", true);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage("서비스를 이용하려면 면저 권한에 동의해야 합니다. \n" +
                                "설정->권한에서 권한을 설정할 수 있습니다.")
                        .setPermissions(Manifest.permission.RECORD_AUDIO)
                        .check();
            });
        }
        return v;
    }

    @Override
    public int getDefaultBackgroundColor() {
        Context context = getContext();
        if(layoutResId==R.layout.activity_landing1&&context!=null)
            return ContextCompat.getColor(context, R.color.materialDarkBlack);
        else
            return Color.WHITE;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        View v = getView();
        if(v!=null) v.setBackgroundColor(backgroundColor);
    }
}
