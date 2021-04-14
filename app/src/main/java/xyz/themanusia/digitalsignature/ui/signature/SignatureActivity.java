package xyz.themanusia.digitalsignature.ui.signature;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import xyz.themanusia.digitalsignature.MainActivity;
import xyz.themanusia.digitalsignature.databinding.ActivitySignatureBinding;

public class SignatureActivity extends AppCompatActivity {
    private ActivitySignatureBinding binding;
    private int save;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            builder.setTitle("Save As");
            String[] item = {"PNG", "PDF"};
            final EditText title = new EditText(this);
            builder.setView(title);
            title.setHint("Title");
            builder.setSingleChoiceItems(item, 0, (dialogInterface, i) -> save = i);

            builder.setPositiveButton("Edit", (dialogInterface, i) -> {
                if (title.length() == 0) {
                    Toast.makeText(this, "Insert Title!", Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                } else {
                    this.title = title.getText().toString().trim();

                    switch (save) {
                        case 0:
                            saveAsBitmap(binding.signaturePad.getTransparentSignatureBitmap());
                            break;
                        case 1:
                            saveAsPdf(binding.signaturePad.getTransparentSignatureBitmap());
                            break;
                    }
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
            Toast.makeText(this, "Success to save file", Toast.LENGTH_SHORT).show();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
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

    private void saveAsPdf(Bitmap transparentSignatureBitmap) {
        String path = MainActivity.FOLDER_PATH + File.separator + title + ".pdf";

        File file = new File(path);
        if (file.exists())
            Toast.makeText(this, "Name already exist", Toast.LENGTH_SHORT).show();
        else
            try {
                Document document = new Document(PageSize.A4.rotate());

                PdfWriter.getInstance(document, new FileOutputStream(path));

                document.open();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                transparentSignatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());

                document.setMargins(0, 0, 0, 0);

                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);


                document.add(image);
                document.close();
                Toast.makeText(this, "Success to save file", Toast.LENGTH_SHORT).show();
            } catch (IOException | DocumentException e) {
                Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            }
    }
}