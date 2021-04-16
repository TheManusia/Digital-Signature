package xyz.themanusia.digitalsignature.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import xyz.themanusia.digitalsignature.databinding.ActivityHomeBinding;
import xyz.themanusia.digitalsignature.ui.signature.SignatureActivity;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    //    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeViewModel viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getAll().observe(this, signatureEntities -> {
            binding.rvSignature.setAdapter(new HomeAdapter(signatureEntities));
            binding.rvSignature.setVisibility(View.VISIBLE);
            binding.rvSignature.setLayoutManager(new LinearLayoutManager(this));
            binding.rvSignature.setHasFixedSize(true);
        });

//        binding.fbOpen.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("application/pdf");
//            startActivityForResult(intent, 1);
//        });

//        binding.fbNew.setOnClickListener(view ->
//                startActivity(new Intent(this, SignatureActivity.class)));

        binding.fbTrigger.setOnClickListener(view -> //{
                startActivity(new Intent(this, SignatureActivity.class)));
//            if (isOpen)
//                closeFbMenu();
//            else
//                showFbMenu();
//      }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (data != null) {
//                Uri pdfUri = data.getData();
//                Intent intent = new Intent(this, PdfActivity.class);
//                intent.putExtra(PdfActivity.PDF_URI, pdfUri.toString());
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, "Can't Open File", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        if (isOpen)
//            closeFbMenu();
//        else
//            super.onBackPressed();
//    }

//    private void showFbMenu() {
//        isOpen = true;
//        binding.fbOpen.animate().translationY(-getResources().getDimension(R.dimen.standard_60)).withStartAction(() -> binding.fbOpen.setVisibility(View.VISIBLE));
//        binding.tvOpen.animate().translationY(-getResources().getDimension(R.dimen.standard_60)).withStartAction(() -> binding.fbNew.setVisibility(View.VISIBLE));
//        binding.fbNew.animate().translationY(-getResources().getDimension(R.dimen.standard_110)).withStartAction(() -> binding.fbNew.setVisibility(View.VISIBLE));
//        binding.tvNew.animate().translationY(-getResources().getDimension(R.dimen.standard_110)).withStartAction(() -> binding.tvNew.setVisibility(View.VISIBLE));
//    }

//    private void closeFbMenu() {
//        isOpen = false;
//        binding.fbNew.animate().translationY(0).withEndAction(() -> binding.fbNew.setVisibility(View.GONE));
//        binding.tvNew.animate().translationY(0).withEndAction(() -> binding.tvNew.setVisibility(View.GONE));
//        binding.fbOpen.animate().translationY(0).withEndAction(() -> binding.fbOpen.setVisibility(View.GONE));
//        binding.tvOpen.animate().translationY(0).withEndAction(() -> binding.tvOpen.setVisibility(View.GONE));
//    }
}