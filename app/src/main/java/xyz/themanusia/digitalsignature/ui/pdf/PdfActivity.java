package xyz.themanusia.digitalsignature.ui.pdf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.databinding.ActivityPdfBinding;
import xyz.themanusia.digitalsignature.databinding.PageDialogBinding;
import xyz.themanusia.digitalsignature.ui.signature.SignatureActivity;

public class PdfActivity extends AppCompatActivity {
    private ActivityPdfBinding binding;
    public static final String PDF_URI = "PDF_URI";
    public static final String SIGNATURE_BITMAP = "SIGNATURE_BITMAP";
    private static final int SIGNATURE_REQUEST_CODE = 42069;
    private Bitmap signatureBitmap;
    private Uri pdfUri;
    private int currentPage;
    private int pageCount;
    private boolean isShow = true;
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null)
            pdfUri = Uri.parse(getIntent().getStringExtra(PDF_URI));
        else {
            Toast.makeText(this, "File doesnt exist", Toast.LENGTH_SHORT).show();
            finish();
        }

        dir = new File(pdfUri.getPath());

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNATURE_REQUEST_CODE)
            if (resultCode == RESULT_OK)
                signatureBitmap = data.getParcelableExtra(SIGNATURE_BITMAP);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        out.setRepeatMode(Animation.REVERSE);

        AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        in.setRepeatMode(Animation.REVERSE);

        binding.topAppBar.setTitle(dir.getName());

        binding.fbPdf.setOnClickListener(view -> {
            Intent signature = new Intent(PdfActivity.this, SignatureActivity.class);
            startActivityForResult(signature, SIGNATURE_REQUEST_CODE);
        });

        binding.pdfView.fromUri(pdfUri)
                .enableSwipe(true)
                .enableDoubletap(false)
                .swipeHorizontal(true)
                .scrollHandle(null)
                .pageFling(true)
                .pageFitPolicy(FitPolicy.BOTH)
                .fitEachPage(true)
                .autoSpacing(true)
                .onPageScroll((page, positionOffset) -> {
                    if (isShow) {
                        binding.tvPage.startAnimation(out);
                        binding.tvPage.setVisibility(View.INVISIBLE);
                        binding.bottomAppBar.animate().translationY(binding.bottomAppBar.getBottom())
                                .setInterpolator(new AccelerateInterpolator()).start();
                        binding.topAppBar.animate().translationY(-binding.topAppBar.getBottom())
                                .setInterpolator(new AccelerateInterpolator()).start();
                        binding.fbPdf.animate().translationY(binding.fbPdf.getBottom())
                                .setInterpolator(new AccelerateInterpolator()).start();
                        isShow = false;
                    }
                })
                .onTap(e -> {
                    if (!isShow) {
                        binding.tvPage.startAnimation(in);
                        binding.tvPage.setVisibility(View.VISIBLE);
                        binding.bottomAppBar.animate().translationY(0)
                                .setInterpolator(new DecelerateInterpolator()).start();
                        binding.topAppBar.animate().translationY(0)
                                .setInterpolator(new DecelerateInterpolator()).start();
                        binding.fbPdf.animate().translationY(0)
                                .setInterpolator(new DecelerateInterpolator()).start();
                        isShow = true;
                    }
                    return true;
                })
                .onPageChange((page, positionOffset) -> {
                    currentPage = page + 1;
                    pageCount = binding.pdfView.getPageCount();
                    String pages = String.format(getString(R.string.page), page + 1, binding.pdfView.getPageCount());
                    binding.tvPage.setText(pages);
                    binding.tvPage.startAnimation(out);
                })
                .load();
        binding.pdfView.setMaxZoom(1);
        binding.pdfView.setMinZoom(1);

        binding.tvPage.setOnClickListener(view -> showDialogPage());
    }

    private void showDialogPage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(PdfActivity.this);
        PageDialogBinding pageBinding = PageDialogBinding.inflate(LayoutInflater.from(getApplicationContext()));
        dialog.setView(pageBinding.getRoot());
        dialog.setPositiveButton("Jump", (dialogInterface, i) -> {
            binding.pdfView.jumpTo(Integer.parseInt(pageBinding.tfPage.getText().toString()));
            dialogInterface.dismiss();
        });

        dialog.show();

        pageBinding.tfPage.setText(String.valueOf(currentPage));
        pageBinding.tvTotal.setText(String.valueOf(pageCount));
        pageBinding.sbPage.setMax(pageCount);
        pageBinding.sbPage.setProgress(currentPage);
        pageBinding.sbPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pageBinding.tfPage.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}