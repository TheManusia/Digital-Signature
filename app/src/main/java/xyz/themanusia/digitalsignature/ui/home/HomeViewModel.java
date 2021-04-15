package xyz.themanusia.digitalsignature.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import xyz.themanusia.digitalsignature.data.Repository;
import xyz.themanusia.digitalsignature.data.room.model.SignatureEntity;

public class HomeViewModel extends AndroidViewModel {
    private final LiveData<List<SignatureEntity>> signatureEntities;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Repository repository = new Repository(application);
        signatureEntities = repository.getAll();
    }

    public LiveData<List<SignatureEntity>> getAll() {
        return signatureEntities;
    }
}
