package com.example.fashkl.project.VO;

/**
 * Created by ChanYoung on 2016-11-11.
 */

public class TrainScheduleVO {
    private int trainId;
    private String departureInfo;
    private String arrivalInfo;


    public TrainScheduleVO(int trainId, String departureInfo,
                           String arrivalInfo) {

        this.trainId = trainId;
        this.departureInfo = departureInfo;
        this.arrivalInfo = arrivalInfo;

    }

    public int getTrainId() {
        return trainId;
    }
    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public String getDepartureInfo() {
        return departureInfo;
    }
    public void setDepartureInfo(String departureInfo) {
        this.departureInfo = departureInfo;
    }

    public String getArrivalInfo() {
        return arrivalInfo;
    }
    public void setArrivalInfo(String arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
    }
}

