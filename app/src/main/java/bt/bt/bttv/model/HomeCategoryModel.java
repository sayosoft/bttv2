package bt.bt.bttv.model;

import java.io.Serializable;

/**
 * Created by Sachin on 8/23/2016.
 */
public class HomeCategoryModel implements Serializable {


    /**
     * 0 : 1
     * homepage_id : 1
     * 1 : Popular
     * homepage_title : Popular
     * 2 : The most popular titles on BTTV
     * homepage_subtitle : The most popular titles on BTTV
     * 3 : video
     * content_type : video
     * 4 : 6,8,11,15,16
     * homepage_vod_ids : 6,8,11,15,16
     * 5 : Active
     * homepage_status : Active
     */

    private String homepage_id;
    private String homepage_title;
    private String homepage_subtitle;
    private String content_type;
    private String homepage_vod_ids;
    private String homepage_status;

    public String getHomepage_id() {
        return homepage_id;
    }

    public void setHomepage_id(String homepage_id) {
        this.homepage_id = homepage_id;
    }

    public String getHomepage_title() {
        return homepage_title;
    }

    public void setHomepage_title(String homepage_title) {
        this.homepage_title = homepage_title;
    }

    public String getHomepage_subtitle() {
        return homepage_subtitle;
    }

    public void setHomepage_subtitle(String homepage_subtitle) {
        this.homepage_subtitle = homepage_subtitle;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getHomepage_vod_ids() {
        return homepage_vod_ids;
    }

    public void setHomepage_vod_ids(String homepage_vod_ids) {
        this.homepage_vod_ids = homepage_vod_ids;
    }

    public String getHomepage_status() {
        return homepage_status;
    }

    public void setHomepage_status(String homepage_status) {
        this.homepage_status = homepage_status;
    }
}
