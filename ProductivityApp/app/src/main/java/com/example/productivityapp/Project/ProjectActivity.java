package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProjectAdapterClass mAdapter;
    private List<ProjectAdapterClass.ProjectItem> projectItems;
    private ActivityProjectBinding binding;
    private FloatingActionButton addProject;
    private TextInputEditText inputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createProjectCardList();
        buildRecyclerView();
        addProject = binding.floatingActionButton;
        inputEditText = binding.textInputEditText;

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.projectStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    String text = inputEditText.getText().toString();

                    if (!text.isEmpty()) {
                        projectItems.add(new ProjectAdapterClass.ProjectItem(R.drawable.img, text));
                        mAdapter.notifyItemInserted(projectItems.size() - 1);
                        inputEditText.setText("");
                        hideKeyboard();
                        // Add a delay of 100ms before hiding the bottom sheet
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }, 100);
                    }



                    return true;
                }
                return false;
            }
        });
    }
    private void createProjectCardList() {
        projectItems = new ArrayList<>();
    }
    
    private void buildRecyclerView() {
        mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mAdapter = new ProjectAdapterClass(projectItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void hideKeyboard (){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}