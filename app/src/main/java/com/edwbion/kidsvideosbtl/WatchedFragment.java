package com.edwbion.kidsvideosbtl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WatchedFragment extends Fragment {

    private RecyclerView watchedRecyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> watchedVideoList;
    private FirebaseFirestore firestore;
    private String userId;

    public WatchedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("edw", "Fragment 2");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watched, container, false);

        // Initialize Firestore and get the current user ID
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Lấy userId của người dùng hiện tại

        // Initialize RecyclerView
        watchedRecyclerView = view.findViewById(R.id.videoRecyclerView);
        watchedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize video list and adapter
        watchedVideoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(watchedVideoList, getContext());
        watchedRecyclerView.setAdapter(videoAdapter);

        // Load watched videos for the current user
        loadWatchedVideos();

        return view;
    }

    private void loadWatchedVideos() {
        // Lấy userId của người dùng hiện tại
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Khởi tạo FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến collection "watched" của người dùng
        CollectionReference watchedCollection = db.collection("users")
                .document(userId)
                .collection("watched");

        // Truy vấn tất cả các video đã xem của người dùng
        watchedCollection.orderBy("title", Query.Direction.ASCENDING)  // Sắp xếp theo tên video
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error getting documents: ", error);
                            return;
                        }

                        // Clear danh sách video cũ trước khi thêm video mới
                        watchedVideoList.clear();

                        // Duyệt qua tất cả các tài liệu (document) trong collection "watched"
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String videoUrl = document.getString("videoUrl");

                            // Thêm video vào danh sách đã xem
                            watchedVideoList.add(new Video(title, description, videoUrl));
                        }

                        // Cập nhật RecyclerView với dữ liệu mới
                        videoAdapter.notifyDataSetChanged();
                    }
                });
    }
}
