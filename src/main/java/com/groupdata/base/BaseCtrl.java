package com.groupdata.base;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin
@RequestMapping("/base")
public class BaseCtrl {
    @Autowired
    BaseService service;

    // 因为不再使用 JDBC 直接操作，所以去掉了所有 throws SQLException 使得代码更清爽
    @RequestMapping("/getBaseData")
    public JSONObject getBaseData() {
        return service.getBaseData();
    }

    @RequestMapping("/getPlayerData")
    public JSONObject getPlayerData(boolean recent) {
        return service.getPlayerData(recent);
    }

    @RequestMapping("/getRoleData")
    public JSONObject getRoleData(boolean recent) {
        return service.getRoleData(recent);
    }

    @RequestMapping("/getPlayerList")
    public JSONObject getPlayerList() {
        return service.getPlayerList();
    }

    @RequestMapping("/getRoleList")
    public JSONObject getRoleList() {
        return service.getRoleList();
    }

    @RequestMapping("/getOverData")
    public JSONObject getOverData() {
        double[] over = service.getOverView(false);
        JSONObject res = new JSONObject();
        res.put("ZHU1", over[0]);
        res.put("ZHONG1", over[1]);
        res.put("FAN1", over[2]);
        res.put("NEI1", over[3]);
        res.put("QUAN1", over[4]);

        double[] over_r = service.getOverView(true);
        res.put("ZHU2", over_r[0]);
        res.put("ZHONG2", over_r[1]);
        res.put("FAN2", over_r[2]);
        res.put("NEI2", over_r[3]);
        res.put("QUAN2", over_r[4]);
        return res;
    }

    @RequestMapping("/getOverFData")
    public JSONObject getOverFData() {
        JSONObject res = service.getOverView();
        res.put("T_ZHU", service.getZHU(false));
        res.put("T_R_ZHU", service.getZHU(true));

        return res;
    }
}