package xyz.themanusia.signaturepdf.tools;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;

import xyz.themanusia.signaturepdf.ui.Home.PdfEntity;

class FileHelper {

    private static final String TAG = FileHelper.class.getName();
    static void writeToFile(PdfEntity pdfEntity, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(pdfEntity.getName(), Context.MODE_PRIVATE));
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed :", e);
        }
    }
}
