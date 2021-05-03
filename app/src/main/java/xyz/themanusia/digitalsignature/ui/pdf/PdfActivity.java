package xyz.themanusia.digitalsignature.ui.pdf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.shockwave.pdfium.util.SizeF;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;

import lombok.SneakyThrows;
import xyz.themanusia.digitalsignature.MainActivity;
import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.databinding.ActivityPdfBinding;
import xyz.themanusia.digitalsignature.databinding.PageDialogBinding;
import xyz.themanusia.digitalsignature.tools.Tools;
import xyz.themanusia.digitalsignature.ui.signature.SignatureActivity;

public class PdfActivity extends AppCompatActivity {
    private ActivityPdfBinding binding;
    public static final String PDF_URI = "PDF_URI";
    public static final String SIGNATURE_BITMAP = "SIGNATURE_BITMAP";
    private static final int SIGNATURE_REQUEST_CODE = 42069;
    private Uri pdfUri;
    private int currentPage;
    private int pageCount;
    private boolean isShow = true;
    private File dir;
    private static final String TAG = PdfActivity.class.getSimpleName();
    private boolean isEditing = false;

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
                if (data != null)
                    drawImage(data);
    }

    private void editorMode() {
        binding.drawView.setVisibility(View.VISIBLE);
        binding.bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        binding.bottomAppBar.replaceMenu(R.menu.edit_menu);
        binding.fbPdf.setImageResource(R.drawable.ic_baseline_done_24);
        isEditing = true;
        binding.fbPdf.setOnClickListener(view -> getSignature());
    }

    private void getSignature() {
        Intent signature = new Intent(PdfActivity.this, SignatureActivity.class);
        startActivityForResult(signature, SIGNATURE_REQUEST_CODE);
    }

    @SneakyThrows
    private void drawImage(Intent data) {
        byte[] signatureByte = data.getByteArrayExtra(SIGNATURE_BITMAP);
        Bitmap signatureBitmap = BitmapFactory.decodeByteArray(signatureByte, 0, signatureByte.length);

        PDDocument doc = PDDocument.load(Tools.getFile(this, pdfUri));
        PDImageXObject image = JPEGFactory.createFromImage(doc, signatureBitmap);
        float rectHeight = binding.drawView.getRectHeight();
        float rectWidth = binding.drawView.getRectWidth();
        float imageHeight = signatureBitmap.getHeight();
        float imageWidth = signatureBitmap.getWidth();

        float widthRatio = rectWidth / imageWidth;
        float heightRatio = rectHeight / imageHeight;
        float ratio = Math.min(widthRatio, heightRatio);

        float height = rectHeight * ratio;
        float width = rectWidth * ratio;

        PDPage page = doc.getPage(currentPage - 1);
        PDRectangle pageBounds = page.getMediaBox();
        float x = binding.drawView.getRectX() * binding.drawView.getWidth() / pageBounds.getWidth();
        float y = (binding.drawView.getHeight() - binding.drawView.getRectY()) * binding.drawView.getHeight() / pageBounds.getHeight();

        Log.d(TAG, "drawImage: x= " + x + ", y= " + y);
        Log.d(TAG, "drawImage: width= " + width + ", height= " + height);

        String path = MainActivity.CACHE_PATH + File.separator + dir.getName();

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, false, false);
        contentStream.drawImage(image, binding.drawView.getRectX(),
                (binding.drawView.getHeight() - binding.drawView.getRectY()), width, height);
        contentStream.close();
        doc.save(path);
        doc.close();

        loadPdf(Uri.fromFile(new File(path)));
        cancelEditorMode();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {

        binding.topAppBar.setTitle(dir.getName());

        binding.fbPdf.setOnClickListener(view -> editorMode());

        loadPdf(pdfUri);
        binding.pdfView.setMaxZoom(1);
        binding.pdfView.setMinZoom(1);

        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.cancel) {

                cancelEditorMode();
                return true;
            } else if (itemId == R.id.reset) {
                binding.drawView.clear();
                return true;
            }

            return false;
        });

        binding.tvPage.setOnClickListener(view -> showDialogPage());
    }

    private void loadPdf(Uri pdf) {
        AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        out.setRepeatMode(Animation.REVERSE);

        AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        in.setRepeatMode(Animation.REVERSE);

        binding.pdfView.fromUri(pdf)
                .enableSwipe(true)
                .enableDoubletap(false)
                .swipeHorizontal(true)
                .scrollHandle(null)
                .pageFling(true)
                .pageFitPolicy(FitPolicy.BOTH)
                .fitEachPage(true)
                .autoSpacing(true)
                .onPageScroll((page, positionOffset) -> {
                    if (!isEditing) {
                        if (isShow) {
                            binding.tvPage.startAnimation(out);
                            binding.tvPage.setVisibility(View.INVISIBLE);
                            binding.bottomAppBar.animate().translationY(binding.bottomAppBar.getBottom())
                                    .setInterpolator(new AccelerateInterpolator()).start();
                            binding.topAppBar.animate().translationY(-binding.topAppBar.getBottom())
                                    .setInterpolator(new AccelerateInterpolator()).withEndAction(() ->
                                    binding.topAppBar.setVisibility(View.GONE));
                            binding.fbPdf.animate().translationY(binding.fbPdf.getBottom())
                                    .setInterpolator(new AccelerateInterpolator()).start();
                            isShow = false;
                        }
                    }
                })
                .onTap(e -> {
                    Log.d(TAG, "init: pdfY= " + e.getY() + " pdfX= " + e.getX());
                    if (!isShow) {
                        binding.tvPage.startAnimation(in);
                        binding.tvPage.setVisibility(View.VISIBLE);
                        binding.bottomAppBar.animate().translationY(0)
                                .setInterpolator(new DecelerateInterpolator()).start();
                        binding.topAppBar.animate().translationY(0)
                                .setInterpolator(new DecelerateInterpolator()).withStartAction(() ->
                                binding.topAppBar.setVisibility(View.VISIBLE));
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

                    SizeF size = binding.pdfView.getPageSize(page);
                    float pageHeight = size.getHeight();
                    float pageWidth = size.getWidth();
                    ViewGroup.LayoutParams layoutParams = binding.drawView.getLayoutParams();
                    layoutParams.height = (int) pageHeight;
                    layoutParams.width = (int) pageWidth;
                    binding.drawView.setLayoutParams(layoutParams);
                    Log.d(TAG, "init: pageHeight= " + pageHeight + " pageWidth= " + pageWidth);
                })
                .load();
    }

    private void cancelEditorMode() {
        binding.bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        binding.bottomAppBar.replaceMenu(R.menu.empty_menu);
        binding.drawView.setVisibility(View.GONE);
        binding.drawView.clear();

        binding.fbPdf.setOnClickListener(view -> editorMode());

        isEditing = false;
        binding.fbPdf.setImageResource(R.drawable.ic_baseline_add_24);
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