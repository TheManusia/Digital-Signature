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
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.util.List;

import lombok.SneakyThrows;
import xyz.themanusia.digitalsignature.MainActivity;
import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.databinding.ActivityPdfBinding;
import xyz.themanusia.digitalsignature.databinding.PageDialogBinding;
import xyz.themanusia.digitalsignature.tools.Tools;
import xyz.themanusia.digitalsignature.ui.motionview.MotionView;
import xyz.themanusia.digitalsignature.ui.motionview.model.ImageEntity;
import xyz.themanusia.digitalsignature.ui.motionview.model.Layer;
import xyz.themanusia.digitalsignature.ui.motionview.model.MotionEntity;
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
                    editorMode(data);
    }

    private void editorMode(Intent data) {
        byte[] signatureByte = data.getByteArrayExtra(SIGNATURE_BITMAP);
        Bitmap signatureBitmap = BitmapFactory.decodeByteArray(signatureByte, 0, signatureByte.length);
        ImageEntity entity = new ImageEntity(new Layer(), signatureBitmap, binding.motionView.getWidth(), binding.motionView.getHeight());
        binding.motionView.post(() -> {
            binding.motionView.addEntityAndPosition(entity);
            binding.motionView.setVisibility(View.VISIBLE);
            binding.bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            binding.bottomAppBar.replaceMenu(R.menu.edit_menu);
            binding.fbPdf.setImageResource(R.drawable.ic_baseline_done_24);
            binding.fbPdf.setOnClickListener(view -> drawImage(binding.motionView.getEntities()));
        });
    }

    @SneakyThrows
    private void drawImage(List<MotionEntity> entities) {
        for (MotionEntity entity : entities) {
            PDDocument doc = PDDocument.load(Tools.getFile(this, pdfUri));
            PDImageXObject image = JPEGFactory.createFromImage(doc, entity.getBitmap());
            float height = image.getHeight() * entity.getLayer().getScale();
            float width = image.getWidth() * entity.getLayer().getScale();
            float x = binding.motionView.selectedBottomLeftX();
            float y = binding.motionView.selectedBottomLeftY();

            String path = MainActivity.CACHE_PATH + File.separator + dir.getName();

            PDPage page = doc.getPage(currentPage - 1);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, false, false);
            contentStream.drawImage(image, x, y, width, height);
            contentStream.close();
            doc.save(MainActivity.CACHE_PATH + File.separator + dir.getName());
            doc.close();

            Log.e(TAG, "drawImage: x= " + x + ", y= " + y);
            Log.e(TAG, "drawImage: image height= " + (image.getHeight() * entity.getLayer().getScale()) + ", width= " + (image.getWidth() * entity.getLayer().getScale()));
            Log.e(TAG, "drawImage: entity height= " + (entity.getHeight() * entity.getLayer().getScale()) + ", width= " + (entity.getWidth() * entity.getLayer().getScale()));
            Log.e(TAG, "drawImage: page height= " + page.getCropBox().getHeight() + ", width= " + page.getCropBox().getWidth());

            loadPdf(Uri.fromFile(new File(path)));

            binding.motionView.deleteEntity(entity);
        }

        binding.motionView.deletedSelectedEntity();
        binding.motionView.setVisibility(View.INVISIBLE);
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

        binding.fbPdf.setOnClickListener(view -> {
            Intent signature = new Intent(PdfActivity.this, SignatureActivity.class);
            startActivityForResult(signature, SIGNATURE_REQUEST_CODE);
        });

        loadPdf(pdfUri);
        binding.pdfView.setMaxZoom(1);
        binding.pdfView.setMinZoom(1);

        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.cancel) {
                binding.motionView.deletedSelectedEntity();
                cancelEditorMode();
                return true;
            } else if (itemId == R.id.reset) {
                binding.motionView.resetSelectedEntityPosition();
                return true;
            }

            return false;
        });

        binding.motionView.setMotionViewCallback(new MotionView.MotionViewCallback() {
            @Override
            public void onEntitySelected(@Nullable MotionEntity entity) {
            }

            @Override
            public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            }
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
                    ViewGroup.LayoutParams layoutParams = binding.motionView.getLayoutParams();
                    layoutParams.height = (int) pageHeight;
                    layoutParams.width = (int) pageWidth;
                    binding.motionView.setLayoutParams(layoutParams);
                    Log.d(TAG, "init: pageHeight= " + pageHeight + " pageWidth= " + pageWidth);
                })
                .load();
    }

    private void cancelEditorMode() {
        binding.bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        binding.bottomAppBar.replaceMenu(R.menu.empty_menu);

        binding.fbPdf.setOnClickListener(view -> {
            Intent signature = new Intent(PdfActivity.this, SignatureActivity.class);
            startActivityForResult(signature, SIGNATURE_REQUEST_CODE);
        });
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