package com.example.submission2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.submission2.databinding.FragmentFollowerBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowerFragment extends Fragment {

    private static final String TAG = FollowerFragment.class.getSimpleName();
    private final ArrayList<ModelOther> arrayList = new ArrayList<>();
    private FragmentFollowerBinding binding;
    public static final String GET_DATA = "get data";
    private String data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model model = requireActivity().getIntent().getParcelableExtra(GET_DATA);
        data = model.getUsername();
        Log.i(TAG, "Follower : " + model.getUsername());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFollowerBinding.inflate(inflater, container, false);
        dataLoad(data);
        return binding.getRoot();
    }

    private void dataLoad(String data) {
        showLoading(true);
        AndroidNetworking.get("https://api.github.com/users/" + data + "/followers")
                .addHeaders("Authorization", "token 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")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showLoading(false);
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("login");
                                String type = jsonObject.getString("type");
                                String avatarUrl = jsonObject.getString("avatar_url");
                                Log.e(TAG, "Response = " + name);
                                arrayList.add(new ModelOther(
                                        name,
                                        type,
                                        avatarUrl
//                                        urutan ini harus sama dengan di model gak boleh tertukar urutan nya
                                ));
                            }
                            recyleViewList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        showLoading(false);
                        Log.e(TAG, "onError" + anError);
                    }
                });
    }

    private void showLoading(boolean b) {
        if (b) {
            binding.progressBarFollower.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarFollower.setVisibility(View.GONE);
        }
    }

    private void recyleViewList() {
        binding.rvFollower.setHasFixedSize(true);
        binding.rvFollower.setLayoutManager(new LinearLayoutManager(getContext()));
        ListAdapterFollower list = new ListAdapterFollower(arrayList);
        binding.rvFollower.setAdapter(list);
    }
}