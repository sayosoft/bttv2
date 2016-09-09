package bt.bt.bttv.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachin on 8/29/2016.
 */
public class MyPlayListModel implements Parcelable {

    public static final Parcelable.Creator<MyPlayListModel> CREATOR = new Parcelable.Creator<MyPlayListModel>() {
        @Override
        public MyPlayListModel createFromParcel(Parcel source) {
            return new MyPlayListModel(source);
        }

        @Override
        public MyPlayListModel[] newArray(int size) {
            return new MyPlayListModel[size];
        }
    };
    /**
     * playlist_id : 25
     * playlist_name : Test1
     * playlist_user : 14
     */

    private List<ArrayBean> array;

    public MyPlayListModel() {
    }

    protected MyPlayListModel(Parcel in) {
        this.array = new ArrayList<ArrayBean>();
        in.readList(this.array, ArrayBean.class.getClassLoader());
    }

    public List<ArrayBean> getArray() {
        return array;
    }

    public void setArray(List<ArrayBean> array) {
        this.array = array;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.array);
    }

    public static class ArrayBean implements Parcelable {
        public static final Creator<ArrayBean> CREATOR = new Creator<ArrayBean>() {
            @Override
            public ArrayBean createFromParcel(Parcel source) {
                return new ArrayBean(source);
            }

            @Override
            public ArrayBean[] newArray(int size) {
                return new ArrayBean[size];
            }
        };
        private String playlist_id;
        private String playlist_name;
        private String playlist_user;

        public ArrayBean() {
        }

        protected ArrayBean(Parcel in) {
            this.playlist_id = in.readString();
            this.playlist_name = in.readString();
            this.playlist_user = in.readString();
        }

        public String getPlaylist_id() {
            return playlist_id;
        }

        public void setPlaylist_id(String playlist_id) {
            this.playlist_id = playlist_id;
        }

        public String getPlaylist_name() {
            return playlist_name;
        }

        public void setPlaylist_name(String playlist_name) {
            this.playlist_name = playlist_name;
        }

        public String getPlaylist_user() {
            return playlist_user;
        }

        public void setPlaylist_user(String playlist_user) {
            this.playlist_user = playlist_user;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.playlist_id);
            dest.writeString(this.playlist_name);
            dest.writeString(this.playlist_user);
        }
    }
}
