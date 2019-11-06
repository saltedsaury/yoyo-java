package cn.idachain.finance.batch.common.config;


import cn.idachain.finance.batch.common.util.BaseCacheClient;
import cn.idachain.finance.batch.common.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class MachineIdConfig {
    @Resource
    private BaseCacheClient baseCacheClient;

    /*@Value("${snowflake.datacenter}")
    private Integer dataCenterId;*/

    @Value("${spring.application.name}")
    private String OPLOG_MACHINE_ID_kEY;

    /**
     * 机器id
     */
    public static Integer machineId;
    /**
     * 本地ip地址
     */
    private static String localIp;
    private static TimeUnit timeUnit = TimeUnit.DAYS;

    /**
     * 获取ip地址
     *
     * @return
     * @throws UnknownHostException
     */
    private String getIPAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }

    /**
     * hash机器IP初始化一个机器ID
     */
    @Bean
    public Snowflake initMachineId() throws Exception {
        localIp = getIPAddress();

        Long ip_ = Long.parseLong(localIp.replaceAll("\\.", ""));
        //这里取128,为后续机器Ip调整做准备。
        machineId = ip_.hashCode() % 32;
        //创建一个机器ID
        createMachineId();
        log.info("初始化 machine_id :{}", machineId);

        return new Snowflake(machineId);
    }

    /**
     * 容器销毁前清除注册记录
     */
    @PreDestroy
    public void destroyMachineId() {
        baseCacheClient.delete(OPLOG_MACHINE_ID_kEY + machineId);
    }


    /**
     * 主方法：获取一个机器id
     *
     * @return
     */
    public Integer createMachineId() {
        try {
            //向redis注册，并设置超时时间
            Boolean aBoolean = registerMachine(machineId, localIp);
            //注册成功
            if (aBoolean) {
                //启动一个线程更新超时时间
                updateExpTimeThread();
                //返回机器Id
                return machineId;
            }
            //检查是否被注册满了.不能注册，就直接返回
            if (!checkIfCanRegister()) {
                //注册满了，加一个报警
                return machineId;
            }
            log.info("createMachineId->ip:{},machineId:{}, time:{}", localIp, machineId, new Date());

            //递归调用
            createMachineId();
        } catch (Exception e) {
            getRandomMachineId();
            return machineId;
        }
        getRandomMachineId();
        return machineId;
    }

    /**
     * 检查是否被注册满了
     *
     * @return
     */
    private Boolean checkIfCanRegister() {
        Boolean flag = true;
        //判断0~127这个区间段的机器IP是否被占满
        for (int i = 0; i <= 127; i++) {
            flag = baseCacheClient.hasKey(OPLOG_MACHINE_ID_kEY + i);
            //如果不存在。说明还可以继续注册。直接返回i
            if (!flag) {
                machineId = i;
                break;
            }
        }

        return !flag;
    }

    /**
     * 1.更新超時時間
     * 注意，更新前检查是否存在机器ip占用情况
     */
    private void updateExpTimeThread() {
        //开启一个线程执行定时任务:
        //1.每23小时更新一次超时时间
        new Timer(localIp).schedule(new TimerTask() {
            @Override
            public void run() {
                //检查缓存中的ip与本机ip是否一致, 一致则更新时间，不一致则重新获取一个机器id
                Boolean b = checkIsLocalIp(String.valueOf(machineId));
                if (b) {
                    log.info("更新超时时间 ip:{},machineId:{}, time:{}", localIp, machineId, new Date());
                    baseCacheClient.expire(OPLOG_MACHINE_ID_kEY + machineId, 60 * 60 * 24 * 1000, TimeUnit.MILLISECONDS);
                } else {
                    log.info("重新生成机器ID ip:{},machineId:{}, time:{}", localIp, machineId, new Date());
                    //重新生成机器ID，并且更改雪花中的机器ID
                    getRandomMachineId();
                    //重新生成并注册机器id
                    createMachineId();
                    //更改雪花中的机器ID
                    Snowflake.setNode(machineId);
                    // 结束当前任务
                    log.info("Timer->thread->name:{}", Thread.currentThread().getName());
                    this.cancel();
                }
            }
        }, 10 * 1000, 1000 * 60 * 60 * 23);
    }

    /**
     * 获取1~127随机数
     */
    public void getRandomMachineId() {
        machineId = (int) (Math.random() * 127);
    }

    /**
     * 机器ID顺序获取
     */
    public void incMachineId() {
        if (machineId >= 127) {
            machineId = 0;
        } else {
            machineId += 1;
        }
    }

    /**
     * @param mechineId
     * @return
     */
    private Boolean checkIsLocalIp(String mechineId) {
            String ip = baseCacheClient.getStringValue(OPLOG_MACHINE_ID_kEY + mechineId);
            log.info("checkIsLocalIp->ip:{}", ip);
            return localIp.equals(ip);
    }

    /**
     * 1.注册机器
     * 2.设置超时时间
     *
     * @param machineId 取值为0~127
     * @return
     */
    private Boolean registerMachine(Integer machineId, String localIp) throws Exception {
        return baseCacheClient.addValueNX(OPLOG_MACHINE_ID_kEY + machineId, localIp,60 * 60 * 24);
    }
}
