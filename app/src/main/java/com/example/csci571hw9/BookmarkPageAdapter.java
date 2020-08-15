package com.example.csci571hw9;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class BookmarkPageAdapter extends RecyclerView.Adapter<BookmarkPageAdapter.BookmarkPageViewHolder> {
    ArrayList<NewsCardData> arrayList = new ArrayList<>();
    BookmarksFragment bookmarksFragment;
    public BookmarkPageAdapter(ArrayList<NewsCardData> arrayList, BookmarksFragment bookmarksFragment) {
        this.arrayList = arrayList;
        this.bookmarksFragment = bookmarksFragment;

    }

    @NonNull
    @Override
    public BookmarkPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_card_layout, parent, false);

        return new BookmarkPageViewHolder(view);
    }

    @Override
        public void onBindViewHolder(@NonNull final BookmarkPageViewHolder holder, final int position) {
        NewsCardData data = arrayList.get(position);
        if(data.getThumbnail()!=null){
            Picasso.get().load(data.getThumbnail()).fit().centerCrop().into(holder.newsImage);
        }
        holder.newsImage.setTag(position);

        final String articleId = data.getId();
        holder.title.setText(data.getTitle());
        holder.id.setText(data.getId());
        holder.section.setText(data.getSection());
        holder.time.setText(data.getBookmarksDate());
        if(holder.sharedPreferences.contains(data.getId())){
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }
        final SharedPreferences.Editor editor = holder.sharedPreferences.edit();


        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.sharedPreferences.contains(articleId)){
                    arrayList.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,arrayList.size());
                    editor.remove(articleId);
                    editor.commit();
                    if(arrayList.isEmpty()){
                        bookmarksFragment.toggleBookmarksVisibility();
                    }
                    Log.d("removing" , articleId);
                    Toast.makeText(v.getContext(), "\"" + holder.title.getText().toString()+ "\" removed from bookmarks", Toast.LENGTH_LONG).show();
                    holder.bookmark.setImageResource(R.drawable.ic_turned_in_not_black_24dp);
                    //bookmarksFragment.reloadAllBookmarks();
                }

                else
                {
                    //write in sharedpref
                    editor.putString(articleId,"true");
                    editor.commit();
                    Toast.makeText(v.getContext(), "\""+ holder.title.getText().toString()+ "\" added to bookmarks", Toast.LENGTH_LONG).show();
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class BookmarkPageViewHolder extends RecyclerView.ViewHolder {

        SharedPreferences sharedPreferences;
        TextView title, section,time,id, noBookmarks;
        ImageView newsImage, bookmark;
        LinearLayout linearLayout;
        BookmarkPageViewHolder(@NonNull final View itemView) {
            super(itemView);
            sharedPreferences = itemView.getContext().getSharedPreferences("bookmarks", 0);

            itemView.setOnClickListener(new View.OnClickListener() {
                Context context = itemView.getContext();
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailedArticle.class);
                    String currentArticleId = id.getText().toString();
                    intent.putExtra("articleId", currentArticleId);
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.show();

                    TextView titleDialog = dialog.findViewById(R.id.custom_dialog_title);
                    int position = (int) newsImage.getTag();

                    final NewsCardData data = arrayList.get(position);
                    titleDialog.setText(data.getTitle());

                    ImageView imageDialog = dialog.findViewById(R.id.custom_dialog_image);
                    if(data.getThumbnail()!=null){
                        Picasso.get().load(data.getThumbnail()).fit().centerCrop().into(imageDialog);
                    }

                    ImageView twitterShareDialog = dialog.findViewById(R.id.custom_dialog_twitter);
                    twitterShareDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "http://www.twitter.com/intent/tweet?text=Check Out This Link&url=" + data.getUrl() +"&hashtags=CSCI571NewsSearch";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            v.getContext().startActivity(i);
                            ///startActivity(i);
                        }
                    });

                    final ImageView bookmarkShareDialog = dialog.findViewById(R.id.custom_dialog_bookmark);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(sharedPreferences.contains(data.getId())){
                        bookmarkShareDialog.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    }

                    bookmarkShareDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(sharedPreferences.contains(data.getId())){
                                //remove from shared
                                //change color of bookmark
                                editor.remove(data.getId());
                                editor.commit();
                                Toast.makeText(v.getContext(), "\""+data.getTitle()+ "\" removed from bookmarks", Toast.LENGTH_SHORT).show();
                               bookmarkShareDialog.setImageResource(R.drawable.ic_turned_in_not_black_24dp);
                               bookmarksFragment.reloadAllBookmarks();
                               dialog.dismiss();

                               //                                bookmark.setImageResource(R.drawable.ic_turned_in_not_black_24dp);
                            }
                            else
                            {
                                //write in sharedpref
                                editor.putString(data.getId(),"true");
                                editor.commit();
                                Toast.makeText(v.getContext(),"\""+data.getTitle()+ "\" added to bookmarks", Toast.LENGTH_SHORT).show();
                                bookmarkShareDialog.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                bookmarksFragment.reloadAllBookmarks();
                            }
                        }
                    });

                    Log.d("longclicked", position+"");
                    return true;
                }
            });



            bookmark = itemView.findViewById(R.id.bookmark_layout_bookmark);
            id = itemView.findViewById(R.id.bookmark_layout_id);
            title = itemView.findViewById(R.id.bookmark_layout_title);
            section = itemView.findViewById(R.id.bookmark_layout_section);
            time = itemView.findViewById(R.id.bookmark_layout_time);
            newsImage = itemView.findViewById(R.id.bookmark_layout_image);

        }
    }
}
