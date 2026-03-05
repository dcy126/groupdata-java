package com.groupdata.player;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@RestController
@CrossOrigin
@RequestMapping("/player")
public class PlayerCtrl {
    @Autowired
    PlayerService service;

    @RequestMapping("/getRoleData")
    public JSONObject getRoleData(String player) throws  SQLException{
        return service.getRoleData(player);
    }

    @RequestMapping("/getPlayerData")
    public JSONObject getPlayerData(String role) throws  SQLException{
        return service.getPlayerData(role);
    }

    @RequestMapping("/getRoleZhu")
    public JSONObject getRoleZhu(String role) throws  SQLException{
        return service.getRoleZhu(role);
    }

    @RequestMapping("/getRoleCompare")
    public JSONObject getRoleCompare(String role_1,String role_2) throws  SQLException{
        return service.getRoleCompare(role_1,role_2);
    }

    @RequestMapping("/getPlayerCompare")
    public JSONObject getPlayerCompare(String player_1,String player_2) throws  SQLException{
        return service.getPlayerCompare(player_1,player_2);
    }


}
