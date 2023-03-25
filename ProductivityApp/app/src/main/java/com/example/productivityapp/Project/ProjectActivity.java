package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.example.productivityapp.databinding.ProjectCardItemBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    private ProjectAdapterClass mAdapter;
    private List<ProjectAdapterClass.ProjectItem> projectItems;
    private ActivityProjectBinding binding;
    private ProjectCardItemBinding cardBinding;
    private FloatingActionButton addProject;
    private TextInputEditText inputEditText;
    private MaterialCardView projectCard;
    private TextView projectTitle;
    private ImageView projectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding for project activity
        binding = ActivityProjectBinding.inflate(getLayoutInflater());

        //binding for project cards
        cardBinding = ProjectCardItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createProjectCardList();
        buildRecyclerView();

        //bind items
        addProject = binding.floatingActionButton;
        inputEditText = binding.textInputEditText;
        projectCard = cardBinding.projectCard;
        projectImage = cardBinding.projectCardImage;
        projectTitle = cardBinding.projectCardTitle;

        //create bottom sheet ans
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.projectStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //display bottom sheet to add project
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Project Card", Toast.LENGTH_SHORT);
                toast.show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //saves project on action send click in input field
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
        RecyclerView mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mAdapter = new ProjectAdapterClass(projectItems, ProjectActivity.this);
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