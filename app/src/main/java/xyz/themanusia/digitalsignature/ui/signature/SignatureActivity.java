package xyz.themanusia.digitalsignature.ui.signature;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;

import xyz.themanusia.digitalsignature.MainActivity;
import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;
import xyz.themanusia.digitalsignature.databinding.ActivitySignatureBinding;

public class SignatureActivity extends AppCompatActivity {
    private ActivitySignatureBinding binding;
    //    private int save;
    private String title;
    private SignatureViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SignatureViewModel.class);

        binding.btnClear.setEnabled(false);
        binding.btnSave.setEnabled(false);

        binding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                binding.btnClear.setEnabled(true);
                binding.btnSave.setEnabled(true);
            }

            @Override
            public void onClear() {
                binding.btnClear.setEnabled(false);
                binding.btnSave.setEnabled(false);
            }
        });

        binding.btnClear.setOnClickListener(view -> binding.signaturePad.clear());

        binding.btnSave.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save");
//            String[] item = {"PNG", "PDF"};
            final EditText title = new EditText(this);
            builder.setView(title);
            title.setHint("Title");
//            builder.setSingleChoiceItems(item, 0, (dialogInterface, i) -> save = i);

            builder.setPositiveButton("Edit", (dialogInterface, i) -> {
                if (title.length() == 0) {
                    Toast.makeText(this, "Insert Title!", Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                } else {
                    this.title = title.getText().toString().trim();

                    Uri imageCache = saveAsBitmap(binding.signaturePad.getTransparentSignatureBitmap());

                    UCrop.Options option = new UCrop.Options();

                    option.setRootViewBackgroundColor(getResources().getColor(R.color.white));
                    option.setCompressionFormat(Bitmap.CompressFormat.PNG);

                    UCrop.of(imageCache, imageCache)
                            .withOptions(option)
                            .useSourceImageAspectRatio()
                            .start(SignatureActivity.this);

//                    switch (save) {
//                        case 0:
//                            saveAsBitmap(binding.signaturePad.getTransparentSignatureBitmap());
//                            break;
//                        case 1:
//                            saveAsPdf(binding.signaturePad.getTransparentSignatureBitmap());
//                            break;
//                    }
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

            builder.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri output = UCrop.getOutput(data);
                PDFEntity entity = new PDFEntity();
                if (output != null)
                    entity.setPath(output.toString());
                insertToDB(entity);
            }
            Toast.makeText(this, "Success to save file", Toast.LENGTH_SHORT).show();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertToDB(PDFEntity entity) {
        viewModel.insertData(entity);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    private Uri saveAsBitmap(Bitmap signatureBitmap) {
        String path = MainActivity.FOLDER_PATH + File.separator + title + ".png";

        File file = new File(path);
        if (file.exists())
            Toast.makeText(this, "Name already exist", Toast.LENGTH_SHORT).show();
        else
            try {
                FileOutputStream out = new FileOutputStream(file);
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Success to save file", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        return Uri.fromFile(file);
    }

//    private void saveAsPdf(Bitmap transparentSignatureBitmap) {
//        String path = MainActivity.FOLDER_PATH + File.separator + title + ".pdf";
//
//        File file = new File(path);
//        if (file.exists())
//            Toast.makeText(this, "Name already exist", Toast.LENGTH_SHORT).show();
//        else
//            try {
//                Document document = new Document(PageSize.A4.rotate());
//
//                PdfWriter.getInstance(document, new FileOutputStream(path));
//
//                document.open();
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                transparentSignatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                Image image = Image.getInstance(stream.toByteArray());
//
//                document.setMargins(0, 0, 0, 0);
//
//                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                        - document.rightMargin() - 0) / image.getWidth()) * 100;
//                image.scalePercent(scaler);
//                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
//
//
//                document.add(image);
//                document.close();
//                Toast.makeText(this, "Success to save file", Toast.LENGTH_SHORT).show();
//            } catch (IOException | DocumentException e) {
//                Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
//            }
//    }
}