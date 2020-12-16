package com.david0926.safecall.util;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;


import com.david0926.safecall.R;
import com.david0926.safecall.adapter.ChatAdapter;
import com.david0926.safecall.adapter.TypeAdapter;
import com.david0926.safecall.model.ChatModel;
import com.david0926.safecall.model.TypeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BindingOptions {

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingAdapter("typeItem")
    public static void bindTypeItem(RecyclerView recyclerView, ObservableArrayList<TypeModel> items) {
        TypeAdapter adapter = (TypeAdapter) recyclerView.getAdapter();
        if (adapter != null) adapter.setItem(items);
    }

    @BindingAdapter("chatItem")
    public static void bindChatItem(RecyclerView recyclerView, ObservableArrayList<ChatModel> items) {
        ChatAdapter adapter = (ChatAdapter) recyclerView.getAdapter();
        if (adapter != null) adapter.setItem(items);
    }
}
