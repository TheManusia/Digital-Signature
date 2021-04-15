package xyz.themanusia.digitalsignature.data.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "Signature")
public class SignatureEntity {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "path")
    String path;
}
