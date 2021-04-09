package xyz.themanusia.signaturepdf.ui.Home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import xyz.themanusia.signaturepdf.databinding.PdfItemBinding;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<PdfEntity> pdfEntities = new ArrayList<>();
    private final static String TAG = "HomeAdapter.class";

    public HomeAdapter(List<PdfEntity> pdf) {
        pdfEntities.addAll(pdf);
        Log.e(TAG, "HomeAdapter: "+pdfEntities.isEmpty() );
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

        public void bind(PdfEntity pdfEntity) {
            binding.tvTitle.setText(pdfEntity.getName().trim());
            binding.tvPath.setText(pdfEntity.getPath().trim());

            itemView.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show());
        }
    }
}