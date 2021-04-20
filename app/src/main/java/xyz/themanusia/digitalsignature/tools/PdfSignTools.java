package xyz.themanusia.digitalsignature.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfSignTools {
    private final Context context;
    private Bitmap pageImage, drawable;
    private File directory, generatedImagesPath;
    int x = 0, pageNumber = 1, totalNumberOfPages;
    private float finalX, finalY;
    private Uri pdfUri;
    private static final String TAG = PdfSignTools.class.getSimpleName();

    public PdfSignTools(Context context) {
        this.context = context;
    }

    public void setDrawable(Bitmap bitmap) {
        drawable = bitmap;
    }

    public void setPageNumber(int n) {
        pageNumber = n;
    }

    public void setPosition(float x, float y) {
        finalX = x;
        finalY = y;
        addSignOnImage();
    }

    public void setPdfPath(Uri pdfPath) {
        pdfUri = pdfPath;
    }

    public void doIt() {
        pdfToImage();
    }

    private void pdfToImage() {
        FileOutputStream fileOut;
        try {
            File file = new File(pdfUri.getPath());
            PDDocument document = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; document.getNumberOfPages() > i; i++) {
                x++;
                pageImage = renderer.renderImage(i, 1, Bitmap.Config.RGB_565);
                generatedImagesPath = new File(directory, x + ".png");
                fileOut = new FileOutputStream(generatedImagesPath);
                pageImage.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                fileOut.close();
            }
            totalNumberOfPages = x;
            document.close();
            displayGeneratedImage();
        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while rendering file", e);
        }

    }

    private void displayGeneratedImage() {
        generatedImagesPath = new File(directory, "1" + ".png");
        Uri pathUri = Uri.parse(generatedImagesPath.toString());

    }//subtle art of not giving

    private void addSignOnImage() {
        generatedImagesPath = new File(directory, pageNumber + ".png");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(generatedImagesPath.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, pageImage.getWidth(), pageImage.getHeight(), true);
        Bitmap signImage = Bitmap.createScaledBitmap(drawable, 100, 100, false);
        Bitmap resultBitmap = Bitmap.createBitmap(pageImage.getWidth(), pageImage.getHeight(), pageImage.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        // TODO: here is the x and y coordinates comes
        canvas.drawBitmap(signImage, finalX, finalY, new Paint());
        storeImage(resultBitmap);
        drawable.recycle();
        drawable = null;
    }

    private void storeImage(Bitmap image) {
        try {
            FileOutputStream fos = new FileOutputStream(generatedImagesPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}
