package bt.bt.bttv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by spoton on 24/8/16.
 */
public class PackagesModel implements Parcelable {

    public static final Parcelable.Creator<PackagesModel> CREATOR = new Parcelable.Creator<PackagesModel>() {
        @Override
        public PackagesModel createFromParcel(Parcel source) {
            return new PackagesModel(source);
        }

        @Override
        public PackagesModel[] newArray(int size) {
            return new PackagesModel[size];
        }
    };
    /**
     * package_id : 22
     * package_title : One Day
     * package_type : Individual
     * package_members : null
     * package_vod : 5
     * package_aod : 10
     * package_live_tv : 2
     * package_radio : 2
     * package_price : 10
     * package_discount : null
     * package_duration : Daily
     * package_coupon : null
     * package_status : Active
     */

    private String package_id;
    private String package_title;
    private String package_type;
    private String package_members;
    private String package_vod;
    private String package_aod;
    private String package_live_tv;
    private String package_radio;
    private String package_price;
    private String package_discount;
    private String package_duration;
    private String package_coupon;
    private String package_status;

    public PackagesModel() {
    }

    protected PackagesModel(Parcel in) {
        this.package_id = in.readString();
        this.package_title = in.readString();
        this.package_type = in.readString();
        this.package_members = in.readString();
        this.package_vod = in.readString();
        this.package_aod = in.readString();
        this.package_live_tv = in.readString();
        this.package_radio = in.readString();
        this.package_price = in.readString();
        this.package_discount = in.readString();
        this.package_duration = in.readString();
        this.package_coupon = in.readString();
        this.package_status = in.readString();
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_title() {
        return package_title;
    }

    public void setPackage_title(String package_title) {
        this.package_title = package_title;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }

    public String getPackage_members() {
        return package_members;
    }

    public void setPackage_members(String package_members) {
        this.package_members = package_members;
    }

    public String getPackage_vod() {
        return package_vod;
    }

    public void setPackage_vod(String package_vod) {
        this.package_vod = package_vod;
    }

    public String getPackage_aod() {
        return package_aod;
    }

    public void setPackage_aod(String package_aod) {
        this.package_aod = package_aod;
    }

    public String getPackage_live_tv() {
        return package_live_tv;
    }

    public void setPackage_live_tv(String package_live_tv) {
        this.package_live_tv = package_live_tv;
    }

    public String getPackage_radio() {
        return package_radio;
    }

    public void setPackage_radio(String package_radio) {
        this.package_radio = package_radio;
    }

    public String getPackage_price() {
        return package_price;
    }

    public void setPackage_price(String package_price) {
        this.package_price = package_price;
    }

    public String getPackage_discount() {
        return package_discount;
    }

    public void setPackage_discount(String package_discount) {
        this.package_discount = package_discount;
    }

    public String getPackage_duration() {
        return package_duration;
    }

    public void setPackage_duration(String package_duration) {
        this.package_duration = package_duration;
    }

    public String getPackage_coupon() {
        return package_coupon;
    }

    public void setPackage_coupon(String package_coupon) {
        this.package_coupon = package_coupon;
    }

    public String getPackage_status() {
        return package_status;
    }

    public void setPackage_status(String package_status) {
        this.package_status = package_status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.package_id);
        dest.writeString(this.package_title);
        dest.writeString(this.package_type);
        dest.writeString(this.package_members);
        dest.writeString(this.package_vod);
        dest.writeString(this.package_aod);
        dest.writeString(this.package_live_tv);
        dest.writeString(this.package_radio);
        dest.writeString(this.package_price);
        dest.writeString(this.package_discount);
        dest.writeString(this.package_duration);
        dest.writeString(this.package_coupon);
        dest.writeString(this.package_status);
    }
}
