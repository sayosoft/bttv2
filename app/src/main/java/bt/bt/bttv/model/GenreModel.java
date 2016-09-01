package bt.bt.bttv.model;

/**
 * Created by sayo1 on 8/31/2016.
 */
public class GenreModel {

    /**
     * genre_id : 48
     * genre_typ : AoD
     * genre_name : Bollywood
     * genre_order :
     * genre_coupon : null
     * genre_status : Active
     */

    private String genre_id;
    private String genre_typ;
    private String genre_name;
    private String genre_order;
    private Object genre_coupon;
    private String genre_status;

    public String getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String genre_id) {
        this.genre_id = genre_id;
    }

    public String getGenre_typ() {
        return genre_typ;
    }

    public void setGenre_typ(String genre_typ) {
        this.genre_typ = genre_typ;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public String getGenre_order() {
        return genre_order;
    }

    public void setGenre_order(String genre_order) {
        this.genre_order = genre_order;
    }

    public Object getGenre_coupon() {
        return genre_coupon;
    }

    public void setGenre_coupon(Object genre_coupon) {
        this.genre_coupon = genre_coupon;
    }

    public String getGenre_status() {
        return genre_status;
    }

    public void setGenre_status(String genre_status) {
        this.genre_status = genre_status;
    }
}
