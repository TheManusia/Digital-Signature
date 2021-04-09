package xyz.themanusia.signaturepdf.ui.PdfViewer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import xyz.themanusia.signaturepdf.databinding.ActivityPdfBinding;

public class PdfActivity extends AppCompatActivity {
    private ActivityPdfBinding binding;
    public static final String PDF_URI = "PDF_URI";
    private Uri pdfUri;

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

        binding.pdfView.fromUri(pdfUri)
                .enableSwipe(true)
                .load();

    }
}