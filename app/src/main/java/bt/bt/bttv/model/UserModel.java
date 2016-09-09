package bt.bt.bttv.model;

/**
 * Created by Sachin on 9/10/2016.
 */

public class UserModel {

    /**
     * error : false
     * uid : 14
     * user : {"name":"Bhutan","email":"bttv2016@gmail.com","created_at":"2016-08-10","last_name":"Telecom","mobile":"1234567898","updated_at":"03/07/2016","account_balance":"1010"}
     */

    private boolean error;
    private String uid;
    /**
     * name : Bhutan
     * email : bttv2016@gmail.com
     * created_at : 2016-08-10
     * last_name : Telecom
     * mobile : 1234567898
     * updated_at : 03/07/2016
     * account_balance : 1010
     */

    private UserBean user;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        private String name;
        private String email;
        private String created_at;
        private String last_name;
        private String mobile;
        private String updated_at;
        private String account_balance;

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

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getAccount_balance() {
            return account_balance;
        }

        public void setAccount_balance(String account_balance) {
            this.account_balance = account_balance;
        }
    }
}
