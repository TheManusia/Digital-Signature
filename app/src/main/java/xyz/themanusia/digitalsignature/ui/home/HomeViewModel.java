package xyz.themanusia.digitalsignature.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import xyz.themanusia.digitalsignature.data.Repository;
import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;

public class HomeViewModel extends AndroidViewModel {
    private final LiveData<List<PDFEntity>> pdfEntites;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Repository repository = new Repository(application);
        pdfEntites = repository.getAll();
    }

    public LiveData<List<PDFEntity>> getAll() {
        return pdfEntites;
    }
}
