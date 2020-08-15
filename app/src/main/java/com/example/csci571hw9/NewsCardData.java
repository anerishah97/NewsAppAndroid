package com.example.csci571hw9;

import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.xml.datatype.Duration;

public class NewsCardData {

    private String thumbnail;
    private String id;
    private String section;
    private String title;
    private String date;
    private String url;
    public NewsCardData( String id,String thumbnail,String title,  String section, String date, String url){
        this.thumbnail = thumbnail;
        this.id = id;
        this.section = section;
        this.title = title;
        this.date = date;
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        // 1. find current local time
        // 2. Convert given date time to local time
        // 3. Find differece of 1 and 2
        //
        ZoneId losAngeles = ZoneId.of("America/Los_Angeles");
        Instant timestamp = Instant.parse(date);
//        Log.d("localdatetimegiven", date);

        ZonedDateTime articleTime = timestamp.atZone(losAngeles);
        Log.d("localdatetime", articleTime.toString());

        ZonedDateTime currentTimeInLosAngeles = ZonedDateTime.of(LocalDateTime.now(),losAngeles);
        Log.d("localdatetimeCurrent", currentTimeInLosAngeles.toString());

        long diffInSeconds = Math.abs(ChronoUnit.SECONDS.between(articleTime, currentTimeInLosAngeles));
        long diffInMinutes = Math.abs(ChronoUnit.MINUTES.between(articleTime, currentTimeInLosAngeles));
        long diffInHours = Math.abs(ChronoUnit.HOURS.between(articleTime,currentTimeInLosAngeles));
        long diffInDays = Math.abs(ChronoUnit.DAYS.between(articleTime,currentTimeInLosAngeles));
        Log.d("localdatetimee", diffInSeconds +"");

       if(diffInDays>=1){
           return diffInDays+"d ago";
       }
       else if(diffInHours>=1){
           return diffInHours + "h ago";
       }
       else if(diffInMinutes>=1){
           return diffInMinutes + "m ago";
       }
       else{
           return diffInSeconds + "s ago";
       }
    }


    public String getBookmarksDate(){
        String[] month = {"", "Jan", "Feb", "Mar", "Apr", "May", "June", "July","Aug", "Sept", "Oct", "Nov", "Dec"};
        String[] splittedDate = date.split("-");
        String formattedDate = splittedDate[2] + " " + month[Integer.parseInt(splittedDate[1])];
        return  formattedDate;
    }

    public String getSection() {
        String formattedSection = "";
        if(!section.isEmpty())
            formattedSection = section.substring(0,1).toUpperCase() + section.substring(1,section.length());
        return formattedSection;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }
}

