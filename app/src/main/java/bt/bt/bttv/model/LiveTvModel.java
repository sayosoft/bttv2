package bt.bt.bttv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by spoton on 2/9/16.
 */
public class LiveTvModel implements Parcelable {

    /**
     * channel_id : 2
     * channel_type : Video
     * channel_category : 77
     * channel_genres : 27,41,33,84,28
     * channel_name : BBC Live TV News
     * channel_stream_url :
     * channel_poster : bbc_news24_640.jpg
     * channel_language : 2
     * channel_description :
     * channel_order : 0
     * epg_url :
     * epg_id :
     * epg_timeshift :
     * web_price : 50
     * android_price : 25
     * ios_price : 25
     * parental_control : no
     * windows_price : 25
     * channel_memberships : null
     * channel_status : Active
     */

    private String channel_id;
    private String channel_type;
    private String channel_category;
    private String channel_genres;
    private String channel_name;
    private String channel_stream_url;
    private String channel_poster;
    private String channel_language;
    private String channel_description;
    private String channel_order;
    private String epg_url;
    private String epg_id;
    private String epg_timeshift;
    private String web_price;
    private String android_price;
    private String ios_price;
    private String parental_control;
    private String windows_price;
    private String channel_memberships;
    private String channel_status;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }

    public String getChannel_category() {
        return channel_category;
    }

    public void setChannel_category(String channel_category) {
        this.channel_category = channel_category;
    }

    public String getChannel_genres() {
        return channel_genres;
    }

    public void setChannel_genres(String channel_genres) {
        this.channel_genres = channel_genres;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_stream_url() {
        return channel_stream_url;
    }

    public void setChannel_stream_url(String channel_stream_url) {
        this.channel_stream_url = channel_stream_url;
    }

    public String getChannel_poster() {
        return channel_poster;
    }

    public void setChannel_poster(String channel_poster) {
        this.channel_poster = channel_poster;
    }

    public String getChannel_language() {
        return channel_language;
    }

    public void setChannel_language(String channel_language) {
        this.channel_language = channel_language;
    }

    public String getChannel_description() {
        return channel_description;
    }

    public void setChannel_description(String channel_description) {
        this.channel_description = channel_description;
    }

    public String getChannel_order() {
        return channel_order;
    }

    public void setChannel_order(String channel_order) {
        this.channel_order = channel_order;
    }

    public String getEpg_url() {
        return epg_url;
    }

    public void setEpg_url(String epg_url) {
        this.epg_url = epg_url;
    }

    public String getEpg_id() {
        return epg_id;
    }

    public void setEpg_id(String epg_id) {
        this.epg_id = epg_id;
    }

    public String getEpg_timeshift() {
        return epg_timeshift;
    }

    public void setEpg_timeshift(String epg_timeshift) {
        this.epg_timeshift = epg_timeshift;
    }

    public String getWeb_price() {
        return web_price;
    }

    public void setWeb_price(String web_price) {
        this.web_price = web_price;
    }

    public String getAndroid_price() {
        return android_price;
    }

    public void setAndroid_price(String android_price) {
        this.android_price = android_price;
    }

    public String getIos_price() {
        return ios_price;
    }

    public void setIos_price(String ios_price) {
        this.ios_price = ios_price;
    }

    public String getParental_control() {
        return parental_control;
    }

    public void setParental_control(String parental_control) {
        this.parental_control = parental_control;
    }

    public String getWindows_price() {
        return windows_price;
    }

    public void setWindows_price(String windows_price) {
        this.windows_price = windows_price;
    }

    public Object getChannel_memberships() {
        return channel_memberships;
    }

    public void setChannel_memberships(String channel_memberships) {
        this.channel_memberships = channel_memberships;
    }

    public String getChannel_status() {
        return channel_status;
    }

    public void setChannel_status(String channel_status) {
        this.channel_status = channel_status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.channel_id);
        dest.writeString(this.channel_type);
        dest.writeString(this.channel_category);
        dest.writeString(this.channel_genres);
        dest.writeString(this.channel_name);
        dest.writeString(this.channel_stream_url);
        dest.writeString(this.channel_poster);
        dest.writeString(this.channel_language);
        dest.writeString(this.channel_description);
        dest.writeString(this.channel_order);
        dest.writeString(this.epg_url);
        dest.writeString(this.epg_id);
        dest.writeString(this.epg_timeshift);
        dest.writeString(this.web_price);
        dest.writeString(this.android_price);
        dest.writeString(this.ios_price);
        dest.writeString(this.parental_control);
        dest.writeString(this.windows_price);
        dest.writeString(this.channel_memberships);
        dest.writeString(this.channel_status);
    }

    public LiveTvModel() {
    }

    protected LiveTvModel(Parcel in) {
        this.channel_id = in.readString();
        this.channel_type = in.readString();
        this.channel_category = in.readString();
        this.channel_genres = in.readString();
        this.channel_name = in.readString();
        this.channel_stream_url = in.readString();
        this.channel_poster = in.readString();
        this.channel_language = in.readString();
        this.channel_description = in.readString();
        this.channel_order = in.readString();
        this.epg_url = in.readString();
        this.epg_id = in.readString();
        this.epg_timeshift = in.readString();
        this.web_price = in.readString();
        this.android_price = in.readString();
        this.ios_price = in.readString();
        this.parental_control = in.readString();
        this.windows_price = in.readString();
        this.channel_memberships = in.readString();
        this.channel_status = in.readString();
    }

    public static final Parcelable.Creator<LiveTvModel> CREATOR = new Parcelable.Creator<LiveTvModel>() {
        @Override
        public LiveTvModel createFromParcel(Parcel source) {
            return new LiveTvModel(source);
        }

        @Override
        public LiveTvModel[] newArray(int size) {
            return new LiveTvModel[size];
        }
    };
}
