package xyz.themanusia.digitalsignature.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import xyz.themanusia.digitalsignature.data.room.AppDatabase;
import xyz.themanusia.digitalsignature.data.room.dao.SignatureDao;
import xyz.themanusia.digitalsignature.data.room.model.SignatureEntity;

public class Repository {
    private final SignatureDao signatureDao;
    private final LiveData<List<SignatureEntity>> signatureEntities;

    public Repository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        signatureDao = appDatabase.signatureDao();
        signatureEntities = signatureDao.getAll();
    }

    public LiveData<List<SignatureEntity>> getAll() {
        return signatureEntities;
    }

    public void insert(SignatureEntity signatureEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> signatureDao.insertSignature(signatureEntity));
    }
}
