package xyz.themanusia.digitalsignature.ui.signature;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import xyz.themanusia.digitalsignature.data.Repository;
import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;

public class SignatureViewModel extends AndroidViewModel {
    private final Repository repository;

    public SignatureViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void insertData(PDFEntity pdfEntity) {
        repository.insert(pdfEntity);
    }
}
