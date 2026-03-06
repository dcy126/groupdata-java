package com.groupdata.player;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin
@RequestMapping("/player")
public class PlayerCtrl {
    @Autowired
    PlayerService service;

    // 同样去掉了抛出不必要的受检异常
    @RequestMapping("/getRoleData")
    public JSONObject getRoleData(String player) {
        return service.getRoleData(player);
    }

    @RequestMapping("/getPlayerData")
    public JSONObject getPlayerData(String role) {
        return service.getPlayerData(role);
    }

    @RequestMapping("/getRoleZhu")
    public JSONObject getRoleZhu(String role) {
        return service.getRoleZhu(role);
    }

    @RequestMapping("/getRoleCompare")
    public JSONObject getRoleCompare(String role_1, String role_2) {
        return service.getRoleCompare(role_1, role_2);
    }

    @RequestMapping("/getPlayerCompare")
    public JSONObject getPlayerCompare(String player_1, String player_2) {
        return service.getPlayerCompare(player_1, player_2);
    }
}