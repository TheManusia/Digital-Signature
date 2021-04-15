package xyz.themanusia.digitalsignature.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;

@Dao
public interface PDFDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPDF(PDFEntity pdfEntity);

    @Query("SELECT * FROM PDF ORDER BY id DESC")
    LiveData<List<PDFEntity>> getAll();
}
