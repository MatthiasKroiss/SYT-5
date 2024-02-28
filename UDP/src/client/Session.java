package client;


import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Session {
    private String ID;
    @SerializedName("Session ID")
    private int sessionID;
    @SerializedName("Curr HW")
    private int currHW;
    @SerializedName("E start")
    private long eStart;
    @SerializedName("E pres")
    private long ePres;
    @SerializedName("started[s]")
    private long startedSeconds;
    @SerializedName("ended[s]")
    private long endedSeconds;
    private Date started;
    private Date ended;
    private int reason;
    private int timeQ;

    @SerializedName("RFID tag")
    private String rfidTag;

    @SerializedName("RFID class")
    private String rfidClass;

    @SerializedName("Serial")
    private String serial;

    @SerializedName("Sec")
    private int sec;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getCurrHW() {
        return currHW;
    }

    public void setCurrHW(int currHW) {
        this.currHW = currHW;
    }

    public long geteStart() {
        return eStart;
    }

    public void seteStart(long eStart) {
        this.eStart = eStart;
    }

    public long getePres() {
        return ePres;
    }

    public void setePres(long ePres) {
        this.ePres = ePres;
    }

    public long getStartedSeconds() {
        return startedSeconds;
    }

    public void setStartedSeconds(long startedSeconds) {
        this.startedSeconds = startedSeconds;
    }

    public long getEndedSeconds() {
        return endedSeconds;
    }

    public void setEndedSeconds(long endedSeconds) {
        this.endedSeconds = endedSeconds;
    }



    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public int getTimeQ() {
        return timeQ;
    }

    public void setTimeQ(int timeQ) {
        this.timeQ = timeQ;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String rfidTag) {
        this.rfidTag = rfidTag;
    }

    public String getRfidClass() {
        return rfidClass;
    }

    public void setRfidClass(String rfidClass) {
        this.rfidClass = rfidClass;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    @Override
    public String toString() {
        return "Session{" +
                "ID='" + ID + '\'' +
                ", sessionID=" + sessionID +
                ", currHW=" + currHW +
                ", eStart=" + eStart +
                ", ePres=" + ePres +
                ", startedSeconds=" + startedSeconds +
                ", endedSeconds=" + endedSeconds +
                ", started='" + started + '\'' +
                ", ended='" + ended + '\'' +
                ", reason=" + reason +
                ", timeQ=" + timeQ +
                ", rfidTag='" + rfidTag + '\'' +
                ", rfidClass='" + rfidClass + '\'' +
                ", serial='" + serial + '\'' +
                ", sec=" + sec +
                '}';
    }
}