package com.example.fashkl.frbs.VO;

/**
 * Created by ChanYoung on 2016-11-15.
 */

public class ReservationVO extends TrainScheduleVO {

    private int trainNo;
    private String seatNo;
    private int reserveNo;

    public ReservationVO(int trainId, String departureInfo,
                         String arrivalInfo, int trainNo, String seatNo, int reserveNo
                          ) {
        super(trainId, departureInfo, arrivalInfo);
        this.trainNo = trainNo;
        this.seatNo = seatNo;
        this.reserveNo = reserveNo;
    }

    public int getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(int trainNo) {
        this.trainNo = trainNo;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public int getreserveNo() {
        return reserveNo;
    }

    public void setreserveNo(int reserveNo) {
        this.reserveNo = reserveNo;
    }
}
