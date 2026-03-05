package com.groupdata.base;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@Slf4j
public class BaseService {
    @Autowired
    private DataSource dataSource;

    //static Logger logger = LogManager.getLogger(BaseService.class.getName());


    public JSONObject getBaseData() throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        //        String  tab[]={"对局号","日期","主公","玩家1","忠臣1","玩家2","忠臣2","玩家3",
        //                "反贼1","玩家4","反贼2","玩家5","反贼3","玩家6","反贼4","玩家7","内奸","玩家8","胜利方"};
        //        String  tab[]={"DJH","DATE","ZHU","PLAYER1","ZHONG1","PLAYER2","ZHONG2","PLAYER3",
        //                "FAN1","PLAYER4","FAN2","PLAYER5","FAN3","PLAYER6","FAN4","PLAYER7","NEI","PLAYER8","WINNER"};
        String  tab[]={"ZHU","ZHONG1","ZHONG2","FAN1","FAN2","FAN3","FAN4","NEI"};
        //String win[]={"主忠","反贼","内奸"};

            connection = this.dataSource.getConnection();
            statement = connection.createStatement();

            StringBuilder strSQL=new StringBuilder();
            strSQL.append("select \"DJH\",\"DATE\",\"WINNER\",\"ZHU\",\"PLAYER_1\",\"ZHONG_1\",\"PLAYER_2\",\"ZHONG_2\",\"PLAYER_3\",\"FAN_1\",\"PLAYER_4\",\"FAN_2\",\"PLAYER_5\",\"FAN_3\",\"PLAYER_6\",\"FAN_4\",\"PLAYER_7\",\"NEI\",\"PLAYER_8\"\n");
            strSQL.append("from t_base \n");
            strSQL.append("order by \"DJH\" \n");
            resultSet = statement.executeQuery(strSQL.toString());
            int n=0;
            while (resultSet.next()) {
                JSONObject R=new JSONObject();
                R.put("id",n++);
                int i=1;
                R.put("DJH",resultSet.getInt(i++));
                R.put("DATE",resultSet.getDate(i++));
                int winner=resultSet.getInt(i++);
                R.put("WINNER",winner);
                for(String j:tab)
                {
                    JSONObject Z=new JSONObject();
                    Z.put("ROLE",resultSet.getString(i++));
                    Z.put("PLAYER",resultSet.getString(i++));
                    String style;
                    switch (j)
                    {
                        case "ZHU":
                            style=winner==0?"background: rgb(255, 204, 199);":"";
                            break;
                        case "ZHONG1":
                        case "ZHONG2":
                            style=winner==0?"background: rgb(255, 241, 184);":"";
                            break;
                        case "FAN1":
                        case "FAN2":
                        case "FAN3":
                        case "FAN4":
                            style=winner==1?"background: rgb(217, 247, 190);":"";
                            break;
                        case "NEI":
                            style=winner==2?"background: rgb(186, 231, 255);":"";
                            break;
                        default:
                            style="";
                    }
                    Z.put("STYLE",style);
                    R.put(j, Z);
                }
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


    public JSONObject getPlayerData() throws  SQLException{
        return getPlayerData(false);
    }
    public JSONObject getPlayerData(boolean recent) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        //String  tab[]={"PLAYER","WIN_ZHU","TOTAL_ZHU","WIN_ZHONG","TOTAL_ZHONG","WIN_FAN","TOTAL_FAN","WIN_NEI","TOTAL_NEI","WIN","TOTAL"};
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};
        double over[]= getOverView(recent);

            connection = this.dataSource.getConnection();
            statement = connection.createStatement();
            StringBuilder strSQL=new StringBuilder();
            strSQL.append("select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
            if(recent)
                strSQL.append(" from v_r_player\n");
            else
                strSQL.append(" from v_player\n");
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
    public JSONObject getRoleData() throws  SQLException{
        return getRoleData(false);
    }
    public JSONObject getRoleData(boolean recent) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};

            connection = this.dataSource.getConnection();
            statement = connection.createStatement();
            StringBuilder strSQL=new StringBuilder();
            strSQL.append("select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
            if(recent)
                strSQL.append(" from v_r_role\n");
            else
                strSQL.append(" from v_role\n");
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

    public JSONObject getOverView() throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select f_over()");
        resultSet.next();
        String str=resultSet.getString(1);
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
        JSONObject res=JSONObject.parseObject(str);
        return res;
    }

    public static double[] getOverView(DataSource dataSource) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Map<String,Integer> overView=new HashMap<String,Integer>();
        double over[]=new double[5];

        connection = dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"WINNER\",\"TOTAL\"\n");
        strSQL.append(" from v_over");
        resultSet = statement.executeQuery(strSQL.toString());
        while (resultSet.next()) {
            overView.put(resultSet.getString(1),resultSet.getInt(2));
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

        int sum=overView.getOrDefault("总场次",0);
        if(sum==0) sum=1;
        int zhu=overView.getOrDefault("主忠",0);
        int fan=overView.getOrDefault("反贼",0);
        int nei=overView.getOrDefault("内奸",0);
        over[0]=over[1]=((double) zhu)/sum;
        over[2]=((double) fan)/sum;
        over[3]=((double) nei)/sum;
        over[4]=((double) zhu*3+fan*4+nei)/sum/8;
        return over;
    }

    public ArrayList<JSONObject> getZHU(boolean recent) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"ZHU\",\"TOTAL\",\"WIN\"\n");
        if(recent)
            strSQL.append(" from v_r_zhu\n");
        else
            strSQL.append(" from v_zhu\n");
        strSQL.append("order by \"WIN\" desc\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("id",n++);
            R.put("ZHU",resultSet.getString(1));
            int total=resultSet.getInt(2);
            int win=resultSet.getInt(3);
            double percentage=((double)win)/total;
            R.put("TOTAL",total);
            R.put("WIN",win);
            R.put("PERCENTAGE",percentage);
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
        return res;
    }

    public double[] getOverView(boolean recent) throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Map<String,Integer> overView=new HashMap<String,Integer>();
        double over[]=new double[5];

            connection = this.dataSource.getConnection();
            statement = connection.createStatement();
            StringBuilder strSQL=new StringBuilder();
            strSQL.append("select \"WINNER\",\"TOTAL\"\n");
            if(recent)
                strSQL.append(" from v_r_over");
            else
                strSQL.append(" from v_over");
            resultSet = statement.executeQuery(strSQL.toString());
            while (resultSet.next()) {
                overView.put(resultSet.getString(1),resultSet.getInt(2));
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

        int sum=overView.getOrDefault("总场次",0);
        if(sum==0) sum=1;
        int zhu=overView.getOrDefault("主忠",0);
        int fan=overView.getOrDefault("反贼",0);
        int nei=overView.getOrDefault("内奸",0);
        over[0]=over[1]=((double) zhu)/sum;
        over[2]=((double) fan)/sum;
        over[3]=((double) nei)/sum;
        over[4]=((double) zhu*3+fan*4+nei)/sum/8;
        return over;
    }


    public JSONObject getPlayerList() throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
        strSQL.append(" from v_player\n");
        strSQL.append("order by \"TOTAL\" desc\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("value",resultSet.getString(1));
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

    public JSONObject getRoleList() throws  SQLException{
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<JSONObject> res=new ArrayList<>();
        String  tab[]={"ZHU","ZHONG","FAN","NEI","QUAN"};

        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\"\n");
        strSQL.append(" from v_role\n");
        strSQL.append("order by \"TOTAL\" desc\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int n=0;
        while (resultSet.next()) {
            JSONObject R=new JSONObject();
            R.put("value",resultSet.getString(1));
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

}
