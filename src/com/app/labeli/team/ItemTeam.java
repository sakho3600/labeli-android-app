package com.app.labeli.team;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.labeli.member.ItemMember;

public class ItemTeam implements Parcelable{
	
	private ItemMember author;
	private String name;
	private String description;
	private String pictureURL;
	private double created;
	private int status;
	private int type;
	
	public ItemTeam(ItemMember author, String name, String description,
			String pictureURL, double created, int status, int type) {
		super();
		this.author = author;
		this.name = name;
		this.description = description;
		this.pictureURL = pictureURL;
		this.created = created;
		this.status = status;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ItemTeam [author=" + author + ", name=" + name
				+ ", description=" + description + ", pictureURL=" + pictureURL
				+ ", created=" + created + ", status=" + status + ", type="
				+ type + "]";
	}

	public ItemMember getAuthor() {
		return author;
	}

	public void setAuthor(ItemMember author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public double getCreated() {
		return created;
	}

	public void setCreated(double created) {
		this.created = created;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	protected ItemTeam(Parcel in) {
        author = (ItemMember) in.readValue(ItemMember.class.getClassLoader());
        name = in.readString();
        description = in.readString();
        pictureURL = in.readString();
        created = in.readDouble();
        status = in.readInt();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(author);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(pictureURL);
        dest.writeDouble(created);
        dest.writeInt(status);
        dest.writeInt(type);
    }

    public static final Parcelable.Creator<ItemTeam> CREATOR = new Parcelable.Creator<ItemTeam>() {
        @Override
        public ItemTeam createFromParcel(Parcel in) {
            return new ItemTeam(in);
        }

        @Override
        public ItemTeam[] newArray(int size) {
            return new ItemTeam[size];
        }
    };

}
