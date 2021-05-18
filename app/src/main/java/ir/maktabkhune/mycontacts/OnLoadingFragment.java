package ir.maktabkhune.mycontacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class OnLoadingFragment extends Fragment {
    OnBackPressedCallback onBackPressedCallback;
    MainActivity mainActivity;

    public OnLoadingFragment( MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.on_loading_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        mainActivity.getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        onBackPressedCallback.setEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        onBackPressedCallback.setEnabled(false);
    }
}
