package com.example.hustzxd.spider;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/5/23.
 * wifi信息的JavaBean，对应Bomb云服务的数据库中的一条记录
 */
public class WifiInfo extends BmobObject {
    private String BuildingName;
    private String RoomName;
    private Integer x;
    private Integer y;
    private List<String> BSSIDs;
    private List<String> SSIDs;
    private List<Integer> RSSIs;

    public List<String> getSSIDs() {
        return SSIDs;
    }

    public void setSSIDs(List<String> SSIDs) {
        this.SSIDs = SSIDs;
    }

    public List<String> getBSSIDs() {
        return BSSIDs;
    }

    public void setBSSIDs(List<String> BSSIDs) {
        this.BSSIDs = BSSIDs;
    }

    public List<Integer> getRSSIs() {
        return RSSIs;
    }

    public void setRSSIs(List<Integer> RSSIs) {
        this.RSSIs = RSSIs;
    }

    public String getRoomName() {
        return RoomName;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }


}
