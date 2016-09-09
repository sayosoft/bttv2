package bt.bt.bttv.model;

import java.util.List;

/**
 * Created by Sachin on 9/9/2016.
 */
public class MyPlayListContentModel {


    /**
     * id : 82
     * video_id : 1
     * user_id : 14
     * playlist_id : 54
     * media_type : 2
     * video_title : 3 Idiots
     * video_poster : 3i-poster-32.jpg
     */

    private List<PlaylistBean> playlist;

    public List<PlaylistBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<PlaylistBean> playlist) {
        this.playlist = playlist;
    }

    public static class PlaylistBean {
        private String id;
        private String video_id;
        private String user_id;
        private String playlist_id;
        private String media_type;
        private String video_title;
        private String video_poster;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPlaylist_id() {
            return playlist_id;
        }

        public void setPlaylist_id(String playlist_id) {
            this.playlist_id = playlist_id;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getVideo_title() {
            return video_title;
        }

        public void setVideo_title(String video_title) {
            this.video_title = video_title;
        }

        public String getVideo_poster() {
            return video_poster;
        }

        public void setVideo_poster(String video_poster) {
            this.video_poster = video_poster;
        }
    }
}
