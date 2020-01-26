package View.MyScrollerPicker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sadam.sadamlibarary.R;

public class MyScrollPickerViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public MyScrollPickerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_content);
    }
}
