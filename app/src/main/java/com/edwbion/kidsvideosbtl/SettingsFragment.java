package com.edwbion.kidsvideosbtl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Log để kiểm tra fragment đã được attach
        Log.e("edw","Fragment 2");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Tìm nút Logout trong layout của fragment
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // Xử lý sự kiện click vào nút Logout
        logoutButton.setOnClickListener(v -> {
            // Đăng xuất người dùng khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            // Quay lại màn hình đăng nhập (LoginActivity)
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

            // Đảm bảo người dùng không thể quay lại fragment này sau khi đăng xuất
            getActivity().finish();
        });

        return view;
    }
}
