package com.groupdata.player;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.groupdata.base.BaseService;

@Repository
@Transactional
@Slf4j
public class PlayerService {
    @Autowired
    private DataSource dataSource;

    public JSONObject getRoleData(String c_player) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
        strSQL.append(" from f_f_role('");
        strSQL.append(c_player);
        strSQL.append("')\n");
        strSQL.append("order by \"TOTAL\" desc\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("id",n++);
            int i=1;
            R.put("ROLE",resultSet.getString(i++));
            double weight=0.0;
            int w=0;
            for(String j:tab)
            {
                int win=resultSet.getInt(i++);
                int total=resultSet.getInt(i++);
                double percentage=((double)win)/total;
                if(!Double.isNaN(percentage))
                    switch (j)
                    {
                        case "ZHU":
                        case "NEI":
                            weight+=percentage;
                            w+=1;
                            break;
                        case "ZHONG":
                            weight+=2*percentage;
                            w+=2;
                            break;
                        case "FAN":
                            weight+=4*percentage;
                            w+=4;
                            break;
                        default:
                    }
                JSONObject Z=new JSONObject();
                Z.put("WIN",win);
                Z.put("TOTAL",total);
                Z.put("PERCENTAGE",percentage);
                R.put(j,Z);
            }
            R.put("WEIGHT",weight/w);
            R.put("TOTAL",resultSet.getInt(i-1));
            res.add(R);
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }

        JSONObject obj=new JSONObject();
        obj.put("results",res);
        return obj;
    }


    public JSONObject getPlayerData(String c_role) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};
        double over[]= BaseService.getOverView(this.dataSource);

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
        strSQL.append(" from f_f_player('");
        strSQL.append(c_role);
        strSQL.append("')\n");
        strSQL.append("order by \"TOTAL\" desc\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("id",n++);
            int i=1;
            R.put("PLAYER",resultSet.getString(i++));
            for(int j=0;j<5;j++)
            {
                int win=resultSet.getInt(i++);
                int total=resultSet.getInt(i++);
                double percentage=((double)win)/total;
                JSONObject Z=new JSONObject();
                Z.put("WIN",win);
                Z.put("TOTAL",total);
                Z.put("PERCENTAGE",percentage);
                Z.put("RED",percentage>over[j]);
                R.put(tab[j],Z);
            }
            R.put("TOTAL",resultSet.getInt(i-1));
            res.add(R);
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }

        JSONObject obj=new JSONObject();
        obj.put("results",res);
        return obj;
    }

    public JSONObject getPlayerRank() throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};
        double over[]= BaseService.getOverView(this.dataSource);

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"DJH\",\"DATE\",\"PLAYER_1\",\"PLAYER_2\",\"PLAYER_3\",\"PLAYER_4\",\"PLAYER_5\",\"PLAYER_6\",\"PLAYER_7\",\"PLAYER_8\",\"WIN\"\n");
        strSQL.append(" from t_base\n");
        strSQL.append("order by \"DATE\" \n");
        resultSet = statement.executeQuery(strSQL.toString());
        String date="";
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("id",n++);
            int i=1;
            R.put("PLAYER",resultSet.getString(i++));
            for(int j=0;j<5;j++)
            {
                int win=resultSet.getInt(i++);
                int total=resultSet.getInt(i++);
                double percentage=((double)win)/total;
                JSONObject Z=new JSONObject();
                Z.put("WIN",win);
                Z.put("TOTAL",total);
                Z.put("PERCENTAGE",percentage);
                Z.put("RED",percentage>over[j]);
                R.put(tab[j],Z);
            }
            R.put("TOTAL",resultSet.getInt(i-1));
            res.add(R);
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }

        JSONObject obj=new JSONObject();
        obj.put("results",res);
        return obj;
    }


    public JSONObject getRoleZhu(String c_role) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"ZHONG","FAN","NEI","QUAN"};
        double over[]= BaseService.getOverView(this.dataSource);

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"ROLE\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
        strSQL.append(" from f_zhu_role('");
        strSQL.append(c_role);
        strSQL.append("')\n");
        strSQL.append("order by \"TOTAL\" \n");
        resultSet = statement.executeQuery(strSQL.toString());
        String date="";
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("id",n++);
            int i=1;
            R.put("ROLE",resultSet.getString(i++));
            for(int j=0;j<4;j++)
            {
                int win=resultSet.getInt(i++);
                int total=resultSet.getInt(i++);
                double percentage=((double)win)/total;
                JSONObject Z=new JSONObject();
                Z.put("WIN",win);
                Z.put("TOTAL",total);
                Z.put("PERCENTAGE",percentage);
                Z.put("RED",percentage>over[j]);
                R.put(tab[j],Z);
            }
            R.put("TOTAL",resultSet.getInt(i-1));
            res.add(R);
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }

        JSONObject obj=new JSONObject();
        obj.put("results",res);
        return obj;
    }

    public JSONObject getRoleCompare(String c_role_1,String c_role_2) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"","ROLE1_WIN","ROLE2_WIN","TWO_WIN",""};
        double over[]= BaseService.getOverView(this.dataSource);

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"DESCRIBE\",\"COUNT\"\n");
        strSQL.append(" from f_compare_role('");
        strSQL.append(c_role_1);
        strSQL.append("','");
        strSQL.append(c_role_2);
        strSQL.append("')\n");
        strSQL.append("order by \"DESCRIBE\" \n");
        resultSet = statement.executeQuery(strSQL.toString());
        String date="";
        int sum_butong=0;
        int sum_tong=0;
        JSONObject R=new JSONObject();
        int i=0;
        while (resultSet.next()) {
            int count=resultSet.getInt(2);
            if(i<3)
                sum_butong+=count;
            else if (i<5)
                sum_tong+=count;
            if(!tab[i].isEmpty())
                R.put(tab[i],count);
            i++;
        }
        R.put("SUM_BUTONG",sum_butong);
        R.put("SUM_TONG",sum_tong);
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
        return R;
    }

    public JSONObject getPlayerCompare(String c_player_1,String c_player_2) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"","ROLE1_WIN","ROLE2_WIN","TWO_WIN",""};
        double over[]= BaseService.getOverView(this.dataSource);

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"DESCRIBE\",\"COUNT\"\n");
        strSQL.append(" from f_compare('");
        strSQL.append(c_player_1);
        strSQL.append("','");
        strSQL.append(c_player_2);
        strSQL.append("')\n");
        strSQL.append("order by \"DESCRIBE\" \n");
        resultSet = statement.executeQuery(strSQL.toString());
        String date="";
        int sum_butong=0;
        int sum_tong=0;
        JSONObject R=new JSONObject();
        int i=0;
        while (resultSet.next()) {
            int count=resultSet.getInt(2);
            if(i<3)
                sum_butong+=count;
            else if (i<5)
                sum_tong+=count;
            if(!tab[i].isEmpty())
                R.put(tab[i],count);
            i++;
        }
        R.put("SUM_BUTONG",sum_butong);
        R.put("SUM_TONG",sum_tong);
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
        return R;
    }

}
