package com.example.healthy.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Video {
    @SerializedName("items")
    private List<abc> list;

    public List<abc> getList() {
        return list;
    }

    public void setList(List<abc> list) {
        this.list = list;
    }

    public Video(List<abc> list) {
        this.list = list;
    }

    public class abc {
        @SerializedName("snippet")
        private Snippet snippet;

        public Snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
        }

        public abc(Snippet snippet) {
            this.snippet = snippet;
        }
    }

    public class Snippet {
        @SerializedName("title")
        private String title;
        @SerializedName("thumbnails")
        private thumbnails thumbnails;
        @SerializedName("resourceId")
        private resourceId resourceId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Video.thumbnails getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(Video.thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }

        public Video.resourceId getResourceId() {
            return resourceId;
        }

        public void setResourceId(Video.resourceId resourceId) {
            this.resourceId = resourceId;
        }

        public Snippet(String title, Video.thumbnails thumbnails, Video.resourceId resourceId) {
            this.title = title;
            this.thumbnails = thumbnails;
            this.resourceId = resourceId;
        }
    }

    public class thumbnails {
        @SerializedName("high")
        private High high;

        public High getHigh() {
            return high;
        }

        public void setHigh(High high) {
            this.high = high;
        }

        public thumbnails(High high) {
            this.high = high;
        }
    }

    public class High {
        @SerializedName("url")
        private String urlImg;

        public String getUrlImg() {
            return urlImg;
        }

        public void setUrlImg(String urlImg) {
            this.urlImg = urlImg;
        }

        public High(String urlImg) {
            this.urlImg = urlImg;
        }
    }

    public class resourceId {
        @SerializedName("videoId")
        private String videoId;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public resourceId(String videoId) {
            this.videoId = videoId;
        }
    }

}
