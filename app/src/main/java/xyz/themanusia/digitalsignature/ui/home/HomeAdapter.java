package xyz.themanusia.digitalsignature.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xyz.themanusia.digitalsignature.R;
import xyz.themanusia.digitalsignature.data.room.model.PDFEntity;
import xyz.themanusia.digitalsignature.databinding.PdfItemBinding;
import xyz.themanusia.digitalsignature.tools.Tools;
import xyz.themanusia.digitalsignature.ui.image.ImageActivity;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final List<PDFEntity> pdfEntities = new ArrayList<>();
    private final static String TAG = "HomeAdapter.class";

    public HomeAdapter(List<PDFEntity> pdf) {
        pdfEntities.addAll(pdf);
        Log.e(TAG, "HomeAdapter: " + pdfEntities.isEmpty());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PdfItemBinding itemBinding = PdfItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pdfEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return pdfEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PdfItemBinding binding;

        public ViewHolder(@NonNull PdfItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(PDFEntity pdfEntity) {

            Uri uri = Uri.parse(pdfEntity.getPath());
            File dir = new File(uri.getPath());
            if (dir.exists()) {
                long lastTime = dir.lastModified();
                String dateString = DateFormat.format("dd MMMM yyyy hh:mm a", new Date(lastTime)).toString();
                String name = dir.getName();
                long size = dir.length();

                binding.tvTitle.setText(name);
                binding.tvInfo.setText(String.format(binding.getRoot().getContext().getResources().getString(R.string.path),
                        Tools.readableFileSize(size),
                        dateString
                ));

                Glide.with(binding.getRoot())
                        .load(uri)
                        .apply(new RequestOptions().override(64, 64))
                        .into(binding.imageView);

                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(itemView.getContext(), ImageActivity.class);
                    intent.putExtra(ImageActivity.URI_EXTRA, uri.toString());
                    itemView.getContext().startActivity(intent);
                });
            } else {
                binding.tvTitle.setText(R.string.file_not_found);
                binding.tvInfo.setText(uri.getPath());
            }
        }
    }
}