package com.verne.kenyfoods;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable{
    private int image;
    private String name;
    private String description;
    private int price;
    private String imageUrl;

    public Food() {
        // Required empty constructor for Firebase
    }

    public Food(int image, String name, String description, int price, String imageUrl) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getImage() {
        return image;}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl(){return imageUrl;}

    protected Food(Parcel in) {
        image = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        imageUrl =in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
}

