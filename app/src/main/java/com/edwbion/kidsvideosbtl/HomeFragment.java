package com.edwbion.kidsvideosbtl;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize video list and adapter
        videoList = new ArrayList<>();
        loadVideos();  // Gọi phương thức để tải video từ Firestore
        videoAdapter = new VideoAdapter(videoList, getContext());
        videoRecyclerView.setAdapter(videoAdapter);

        return view;
    }

    private void loadVideos() {
        // Khởi tạo FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến collection "videos"
        CollectionReference videoCollection = db.collection("video");

        // Truy vấn tất cả các video từ collection "videos"
        videoCollection.orderBy("title", Query.Direction.ASCENDING)  // Bạn có thể sắp xếp theo tên hoặc theo các trường khác nếu cần
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error getting documents: ", error);
                            return;
                        }

                        // Clear danh sách video cũ trước khi thêm video mới
                        videoList.clear();

                        // Duyệt qua tất cả các tài liệu (document) trong collection "videos"
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String videoUrl = document.getString("videoUrl");

                            // Thêm video vào danh sách
                            videoList.add(new Video(title, description, videoUrl));
                        }

                        // Cập nhật RecyclerView với dữ liệu mới
                        videoAdapter.notifyDataSetChanged();
                    }
                });
    }
}
