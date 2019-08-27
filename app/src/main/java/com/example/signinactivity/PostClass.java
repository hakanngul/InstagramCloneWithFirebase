package com.example.signinactivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class PostClass extends ArrayAdapter<String> {

    private final ArrayList<String> userEmail;
    private final ArrayList<String> userComment;
    private final ArrayList<String> userImage; //dosyayı değilde url yi çekmek için string yaptık
    private final Activity context;

    public PostClass(ArrayList<String> userEmail, ArrayList<String> userComment, ArrayList<String> userImage, Activity context) {
        super(context, R.layout.custom_view, userEmail); // post classı custom view e email üzerinden bağladık
        this.userEmail = userEmail;
        this.userComment = userComment;
        this.userImage = userImage;
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_view,null,true);
        TextView userEmailText = customView.findViewById(R.id.userEmailTextViewCustomView);
        TextView commentText = customView.findViewById(R.id.commentTextViewCustomView);
        ImageView imageView = customView.findViewById(R.id.userImageCustomView);
        userEmailText.setText(userEmail.get(position));
        commentText.setText(userEmail.get(position)+" "+userComment.get(position));

        Picasso.get().load(userImage.get(position)).into(imageView);


        return customView;

      //  return super.getView(position, convertView, parent);
    }
}
