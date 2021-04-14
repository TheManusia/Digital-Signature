package xyz.themanusia.digitalsignature.ui.pdf;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.databinding.ActivityPdfBinding;
import xyz.themanusia.digitalsignature.databinding.PageDialogBinding;

public class PdfActivity extends AppCompatActivity {
    private ActivityPdfBinding binding;
    public static final String PDF_URI = "PDF_URI";
    private Uri pdfUri;
    private int currentPage;
    private int pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        out.setRepeatMode(Animation.REVERSE);

        AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        in.setRepeatMode(Animation.REVERSE);

        if (getIntent().getExtras() != null)
            pdfUri = Uri.parse(getIntent().getStringExtra(PDF_URI));
        else {
            Toast.makeText(this, "File doesnt exist", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.clipArt.setVisibility(View.GONE);

        binding.pdfView.fromUri(pdfUri)
                .enableSwipe(true)
                .spacing(8)
                .onPageScroll((page, positionOffset) -> {
                    binding.tvPage.startAnimation(out);
                    binding.tvPage.setVisibility(View.INVISIBLE);
                })
                .onTap(e -> {
                    binding.tvPage.startAnimation(in);
                    binding.tvPage.setVisibility(View.VISIBLE);
                    return true;
                })
                .onPageChange((page, positionOffset) -> {
                    currentPage = page + 1;
                    pageCount = binding.pdfView.getPageCount();
                    String pages = String.format(getString(R.string.page), page + 1, binding.pdfView.getPageCount());
                    binding.tvPage.setText(pages);
                })
                .load();

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