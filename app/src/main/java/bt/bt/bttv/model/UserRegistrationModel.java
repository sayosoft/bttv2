package bt.bt.bttv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sachin on 9/28/2016.
 */
public class UserRegistrationModel implements Parcelable {


    public static final Parcelable.Creator<UserRegistrationModel> CREATOR = new Parcelable.Creator<UserRegistrationModel>() {
        @Override
        public UserRegistrationModel createFromParcel(Parcel source) {
            return new UserRegistrationModel(source);
        }

        @Override
        public UserRegistrationModel[] newArray(int size) {
            return new UserRegistrationModel[size];
        }
    };
    /**
     * status : success
     * user : {"user_id":149,"name":"sachin","email":"patilganesh6200@gmail.com","last_name":"mandhare","mobile":"8857930909","session_id":19,"token":"1de528"}
     */

    private String status;
    private String message;
    /**
     * user_id : 149
     * name : sachin
     * email : patilganesh6200@gmail.com
     * last_name : mandhare
     * mobile : 8857930909
     * <p>
     * session_id : 19
     * token : 1de528
     */

    private UserBean user;

    public UserRegistrationModel() {
    }

    protected UserRegistrationModel(Parcel in) {
        this.status = in.readString();
        this.message = in.readString();
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.message);
        dest.writeParcelable(this.user, flags);
    }

    public static class UserBean implements Parcelable {
        public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
            @Override
            public UserBean createFromParcel(Parcel source) {
                return new UserBean(source);
            }

            @Override
            public UserBean[] newArray(int size) {
                return new UserBean[size];
            }
        };
        private int user_id;
        private String name;
        private String email;
        private String last_name;
        private String mobile;
        private int session_id;
        private String token;

        public UserBean() {
        }

        protected UserBean(Parcel in) {
            this.user_id = in.readInt();
            this.name = in.readString();
            this.email = in.readString();
            this.last_name = in.readString();
            this.mobile = in.readString();
            this.session_id = in.readInt();
            this.token = in.readString();
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getSession_id() {
            return session_id;
        }

        public void setSession_id(int session_id) {
            this.session_id = session_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.user_id);
            dest.writeString(this.name);
            dest.writeString(this.email);
            dest.writeString(this.last_name);
            dest.writeString(this.mobile);
            dest.writeInt(this.session_id);
            dest.writeString(this.token);
        }
    }
}
