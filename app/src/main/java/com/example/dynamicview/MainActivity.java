package com.example.dynamicview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 2;
    Button plush_icon;
    Button submit_icon;
    RecyclerView daynamic_recycleView;

    ArrayList<DynamicViewModel> daynamicViewModelArrayList = new ArrayList<>();
    AnatalListAdapter anatalListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit_icon = findViewById(R.id.submit_icon);
        plush_icon = findViewById(R.id.plush_icon);
        daynamic_recycleView = findViewById(R.id.daynamic_recycleView);

        plush_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daynamicViewModelArrayList.add(new DynamicViewModel());
                if (daynamic_recycleView.getAdapter() == null) {
                    anatalListAdapter = new AnatalListAdapter(daynamicViewModelArrayList);
                    daynamic_recycleView.setHasFixedSize(true);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    daynamic_recycleView.setLayoutManager(mLayoutManager);
                    daynamic_recycleView.setNestedScrollingEnabled(false);
                    daynamic_recycleView.setAdapter(anatalListAdapter);
                } else {
                    anatalListAdapter.refreshView(daynamicViewModelArrayList);
                }

            }
        });

        submit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < daynamicViewModelArrayList.size(); i++) {

                    System.out.println("Position: "+i+" , CheckBox: "+daynamicViewModelArrayList.get(i).getCheckBox().isChecked()+"\n Edittext: "
                            + daynamicViewModelArrayList.get(i).getEditText().getText()+"\n imageURl: "+
                            daynamicViewModelArrayList.get(i).getImageUri().toString());
                }
            }
        });
    }

    public class AnatalListAdapter extends RecyclerView.Adapter<AnatalListAdapter.ViewHolder> {
        private ArrayList<DynamicViewModel> listdata;

        AnatalListAdapter(ArrayList<DynamicViewModel> listdata) {
            this.listdata = listdata;
        }

        void refreshView(ArrayList<DynamicViewModel> count) {
            this.listdata = count;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.multiple_view_layout, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//            daynamicEdittextModels.get(position).setWattEdittext(holder.et_watt_antalenheder);
//            daynamicEdittextModels.get(position).setFxSportEdittext(holder.et_spots_antalenheder);
            DynamicViewModel daynamicEdittextModel = new DynamicViewModel();
            daynamicEdittextModel.setCheckBox(holder.checkbox_layout);
            daynamicEdittextModel.setEditText(holder.edittext_layout);
            daynamicEdittextModel.setImageView(holder.imageView_layout);
            daynamicViewModelArrayList.set(position, daynamicEdittextModel);

            holder.imageView_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                    selectPosition = position;
                    imageView = holder.imageView_layout;
                }
            });
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView_layout;
            EditText edittext_layout;
            CheckBox checkbox_layout;


            ViewHolder(View itemView) {
                super(itemView);
                imageView_layout = itemView.findViewById(R.id.imageView_layout);
                edittext_layout = itemView.findViewById(R.id.edittext_layout);
                checkbox_layout = itemView.findViewById(R.id.checkbox_layout);

            }
        }
    }

    int selectPosition;
    String userChoosenTask;
    ImageView imageView;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkPermission(MainActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                for (int i = 0; i < daynamicViewModelArrayList.size(); i++) {
                    if (i == selectPosition) {
                        DynamicViewModel daynamicViewModel = daynamicViewModelArrayList.get(i);
                        daynamicViewModel.setImageUri(data.getData());
                        daynamicViewModelArrayList.set(i, daynamicViewModel);
                    }
                }
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA) {
            for (int i = 0; i < daynamicViewModelArrayList.size(); i++) {
                if (i == selectPosition) {
                    DynamicViewModel daynamicViewModel = daynamicViewModelArrayList.get(i);
                    daynamicViewModel.setImageUri(data.getData());
                    daynamicViewModelArrayList.set(i, daynamicViewModel);
                }
            }
            onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(thumbnail);
    }
}
