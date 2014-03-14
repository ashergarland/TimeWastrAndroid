package android;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Ryan on 3/12/14.
 */
public class Article implements Parcelable{
    public String title;
    public String published;
    public String link;
    public String content;
    public String picture;
    public String author;
    public int time;
    public boolean favorite;

    public Article (String title, String published, String link, String content, String picture, String author, int time) {
        this.title = title;
        this.published = published;
        this.link = link;
        this.content = content;
        this.picture = picture;
        this.time = time;
        this.author = author;
        this.favorite = false;
    }

    public Article(Parcel in)
    {
        this.title = in.readString();
        this.published = in.readString();
        this.link = in.readString();
        this.content = in.readString();
        this.picture = in.readString();
        this.time = in.readInt();
        this.author = in.readString();
        this.favorite = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(published);
        parcel.writeString(link);
        parcel.writeString(content);
        parcel.writeString(picture);
        parcel.writeString(author);
        parcel.writeInt(time);
    }

    @SuppressWarnings("unchecked")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Article createFromParcel(Parcel in)
        {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int i) {
            return new Article[i];
        }
    };
}