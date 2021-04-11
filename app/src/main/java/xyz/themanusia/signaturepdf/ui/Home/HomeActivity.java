package xyz.themanusia.signaturepdf.ui.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import xyz.themanusia.signaturepdf.R;
import xyz.themanusia.signaturepdf.databinding.ActivityHomeBinding;
import xyz.themanusia.signaturepdf.ui.PdfViewer.PdfActivity;
import xyz.themanusia.signaturepdf.ui.SignaturePad.SignatureActivity;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private final List<PdfEntity> pdfEntityList = new ArrayList<>();
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

        binding.fbOpen.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        });

        binding.fbNew.setOnClickListener(view ->
                startActivity(new Intent(this, SignatureActivity.class)));

        binding.fbTrigger.setOnClickListener(view -> {
            if (isOpen)
                closeFbMenu();
            else
                showFbMenu();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Uri pdfUri = data.getData();
                Intent intent = new Intent(this, PdfActivity.class);
                intent.putExtra(PdfActivity.PDF_URI, pdfUri.toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Can't Open File", Toast.LENGTH_SHORT).show();
            }
        }
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