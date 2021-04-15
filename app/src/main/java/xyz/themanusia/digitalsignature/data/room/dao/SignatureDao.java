package xyz.themanusia.digitalsignature.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import xyz.themanusia.digitalsignature.data.room.model.SignatureEntity;

@Dao
public interface SignatureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSignature(SignatureEntity signatureEntity);

    @Query("SELECT * FROM Signature ORDER BY id DESC")
    LiveData<List<SignatureEntity>> getAll();
}
