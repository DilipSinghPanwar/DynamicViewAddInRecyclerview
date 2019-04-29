package com.example.dynamicview;

import android.net.Uri;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class DynamicViewModel {

    ImageView imageView;
    EditText editText;
    CheckBox checkBox;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    Uri imageUri;

    @Override
    public String toString() {
        return "DynamicViewModel{" +
                "imageView=" + imageView +
                ", editText=" + editText +
                ", checkBox=" + checkBox +
                ", imageUri=" + imageUri +
                '}';
    }
}
