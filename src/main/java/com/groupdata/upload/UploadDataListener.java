package com.groupdata.upload;

import java.sql.SQLException;
import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UploadDataListener implements ReadListener<UploadData> {
    private static final int BATCH_COUNT = 100;
    private List<UploadData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private UploadService service;

    //static Logger logger = LogManager.getLogger(UploadService.class.getName());


    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param service
     */
    public UploadDataListener(UploadService service) {
        this.service = service;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(UploadData data, AnalysisContext context){
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */

    private void saveData(){
        if(cachedDataList.isEmpty()) {
            log.info("未检测到更新数据，存储数据库结束。");
            return;
        }
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        try {
            service.save(cachedDataList);
            log.info("存储数据库成功！");
        } catch (Exception e) {
            log.error("存储数据库失败！", e);
            // 抛出运行时异常，强制中断 EasyExcel 的读取，并触发 Spring 事务回滚
            throw new RuntimeException("数据库保存失败，已终止解析", e);
        }
    }

}
