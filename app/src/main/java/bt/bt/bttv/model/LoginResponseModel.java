package bt.bt.bttv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sachin on 9/10/2016.
 */

public class LoginResponseModel implements Parcelable {

    public static final Parcelable.Creator<LoginResponseModel> CREATOR = new Parcelable.Creator<LoginResponseModel>() {
        @Override
        public LoginResponseModel createFromParcel(Parcel source) {
            return new LoginResponseModel(source);
        }

        @Override
        public LoginResponseModel[] newArray(int size) {
            return new LoginResponseModel[size];
        }
    };
    /**
     * status : success
     * user : {"user_id":"14","name":"Bhutan","email":"bttv2016@gmail.com","created_at":"2016-08-10","last_name":"Telecom","mobile":"9404049694","account_balance":"710","session_id":21,"token":"bfbc37"}
     */

    private String status;
    private String message;
    /**
     * user_id : 14
     * name : Bhutan
     * email : bttv2016@gmail.com
     * created_at : 2016-08-10
     * last_name : Telecom
     * mobile : 9404049694
     * account_balance : 710
     * session_id : 21
     * token : bfbc37
     */

    private UserBean user;

    public LoginResponseModel() {
    }

    protected LoginResponseModel(Parcel in) {
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
        private String user_id;
        private String name;
        private String email;
        private String created_at;
        private String last_name;
        private String mobile;
        private String account_balance;
        private int session_id;
        private String token;

        public UserBean() {
        }

        protected UserBean(Parcel in) {
            this.user_id = in.readString();
            this.name = in.readString();
            this.email = in.readString();
            this.created_at = in.readString();
            this.last_name = in.readString();
            this.mobile = in.readString();
            this.account_balance = in.readString();
            this.session_id = in.readInt();
            this.token = in.readString();
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
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

        public String getAccount_balance() {
            return account_balance;
        }

        public void setAccount_balance(String account_balance) {
            this.account_balance = account_balance;
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
            dest.writeString(this.user_id);
            dest.writeString(this.name);
            dest.writeString(this.email);
            dest.writeString(this.created_at);
            dest.writeString(this.last_name);
            dest.writeString(this.mobile);
            dest.writeString(this.account_balance);
            dest.writeInt(this.session_id);
            dest.writeString(this.token);
        }
    }
}
