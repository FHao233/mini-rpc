package com.fhao.rpc.core.router;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.registy.URL;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.fhao.rpc.core.common.cache.CommonClientCache.*;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:33</p>
 * <p>description:   </p>
 */
public class RandomRouterImpl implements IRouter{
    @Override
    public void refreshRouterArr(Selector selector) {
        //获取服务提供者的数目
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        //提前生成调用先后顺序的随机数组
        int[] result = createRandomIndex(arr.length);
        //生成对应服务集群的每台机器的调用顺序
        for (int i = 0; i < result.length; i++) {
            arr[i] = channelFutureWrappers.get(result[i]);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);
        URL url = new URL();
        url.setServiceName(selector.getProviderServiceName());
        //更新权重
        IROUTER.updateWeight(url);
    }

    private int[] createRandomIndex(int len) {
        int[] arrInt = new int[len];
        Random ra = new Random();
        Arrays.fill(arrInt, -1);
        int index = 0;
        while (index < arrInt.length) {
            int num = ra.nextInt(len);
            //如果数组中不包含这个元素则赋值给数组
            if (!contains(arrInt, num)) {
                arrInt[index++] = num;
            }
        }
        return arrInt;
    }
    public boolean contains(int[] arr, int key) {
        for (int j : arr) {
            if (j == key) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getChannelFutureWrappers());
    }

    @Override
    public void updateWeight(URL url) {
        //服务节点的权重
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(url.getServiceName());
        Integer[] weightArr = createWeightArr(channelFutureWrappers);
        Integer[] finalArr = createRandomArr(weightArr);
        ChannelFutureWrapper[] finalChannelFutureWrappers = new ChannelFutureWrapper[finalArr.length];
        for (int j = 0; j < finalArr.length; j++) {
            finalChannelFutureWrappers[j] = channelFutureWrappers.get(finalArr[j]);
        }
        SERVICE_ROUTER_MAP.put(url.getServiceName(),finalChannelFutureWrappers);
    }
    public static void main(String[] args) {
        List<ChannelFutureWrapper> channelFutureWrappers = new ArrayList<>();
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 100));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 200));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 9300));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 400));
        CONNECT_MAP.put("test",channelFutureWrappers);
        RandomRouterImpl randomRouter = new RandomRouterImpl();
        URL url = new URL();
        url.setServiceName("test");
        randomRouter.updateWeight(url);
        Integer[] r = createWeightArr(channelFutureWrappers);
        System.out.println(r);
    }

    /**
     * weight是指权重，权重值约定好配置是100的整倍数
     *
     */
    private static Integer[] createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers) {
        List<Integer> weightArr = new ArrayList<>();
        for (int k = 0; k < channelFutureWrappers.size(); k++) {
            Integer weight = channelFutureWrappers.get(k).getWeight();
            int c = weight / 100;
            for (int i = 0; i < c; i++) {
                weightArr.add(k);
            }
        }
        Integer[] arr = new Integer[weightArr.size()];
        return weightArr.toArray(arr);
    }
    /**
     * 创建随机乱序数组
     *
     * @param arr
     * @return
     */
    private static Integer[] createRandomArr(Integer[] arr) {
        int total = arr.length;
        Random ra = new Random();
        for (int i = 0; i < total; i++) {
            int j = ra.nextInt(total);
            if (i == j) {
                continue;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }
}
