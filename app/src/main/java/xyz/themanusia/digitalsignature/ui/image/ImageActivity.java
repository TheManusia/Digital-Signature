package xyz.themanusia.digitalsignature.ui.image;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;

import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.databinding.ActivityImageBinding;

public class ImageActivity extends AppCompatActivity {
    public static final String URI_EXTRA = "uri_extra";
    private Uri imageUri;
    private ActivityImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null)
            imageUri = Uri.parse(getIntent().getStringExtra(URI_EXTRA));
        BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
        bigImageView.showImage(imageUri);
    }
}