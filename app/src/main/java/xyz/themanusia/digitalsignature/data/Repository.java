package xyz.themanusia.digitalsignature.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import xyz.themanusia.digitalsignature.data.room.AppDatabase;
import xyz.themanusia.digitalsignature.data.room.dao.PDFDao;
import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;

public class Repository {
    private final PDFDao pdfDao;
    private final LiveData<List<PDFEntity>> pdfEntities;

    public Repository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        pdfDao = appDatabase.pdfDao();
        pdfEntities = pdfDao.getAll();
    }

    public LiveData<List<PDFEntity>> getAll() {
        return pdfEntities;
    }

    public void insert(PDFEntity pdfEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> pdfDao.insertPDF(pdfEntity));
    }
}
