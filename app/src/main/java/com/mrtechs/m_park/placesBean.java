package com.mrtechs.m_park;


class placesBean {
    private String title;
    private String vicinity;
    private String lat;
    private String lng;


    public placesBean()
    {

    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getTitle()
    {
        return this.title;
    }

    public void setLat(String place) {
        this.lat = place;
    }

    public String getLat() {
        return this.lat;
    }


    public void setLng(String place) {
        this.lng = place;
    }

    public String getLng() {
        return this.lng;
    }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getVicinity() {
        return this.vicinity;
    }
}
