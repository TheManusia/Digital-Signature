package xyz.themanusia.signaturepdf.ui.Home;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import xyz.themanusia.signaturepdf.R;
import xyz.themanusia.signaturepdf.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private List<PdfEntity> pdfEntityList = new ArrayList<>();
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvPdf.setAdapter(new HomeAdapter(pdfEntityList));
        binding.rvPdf.setVisibility(View.VISIBLE);
        binding.rvPdf.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPdf.setHasFixedSize(true);

        binding.fbTrigger.setOnClickListener(view -> {
            if (isOpen)
                closeFbMenu();
            else
                showFbMenu();
        });
    }

    @Override
    public void onBackPressed() {
        if (isOpen)
            closeFbMenu();
        else
            super.onBackPressed();
    }

    private void showFbMenu() {
        isOpen = true;
        binding.fbOpen.setVisibility(View.VISIBLE);
        binding.tvOpen.setVisibility(View.VISIBLE);
        binding.fbNew.setVisibility(View.VISIBLE);
        binding.tvNew.setVisibility(View.VISIBLE);
        binding.fbOpen.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        binding.fbNew.animate().translationY(-getResources().getDimension(R.dimen.standard_110));
        binding.tvOpen.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        binding.tvNew.animate().translationY(-getResources().getDimension(R.dimen.standard_110));
    }

    private void closeFbMenu() {
        isOpen = false;
        binding.fbNew.animate().translationY(0).withEndAction(() -> binding.fbNew.setVisibility(View.GONE));
        binding.fbOpen.animate().translationY(0).withEndAction(() -> binding.fbOpen.setVisibility(View.GONE));
        binding.tvNew.animate().translationY(0).withEndAction(() -> binding.tvNew.setVisibility(View.GONE));
        binding.tvOpen.animate().translationY(0).withEndAction(() -> binding.tvOpen.setVisibility(View.GONE));
    }
}