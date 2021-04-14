package xyz.themanusia.digitalsignature.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfEntity {
    private String name;
    private String path;
    private int lastPage;
}
