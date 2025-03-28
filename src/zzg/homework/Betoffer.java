package zzg.homework;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class Betoffer {
    //维护一个初始化容量为20的最小堆
    private final PriorityBlockingQueue<Stake> highstakes = new PriorityBlockingQueue<>(20, Comparator.comparing(Stake::getStakeAmount));
    private final ConcurrentHashMap<Integer, Integer> stakes = new ConcurrentHashMap<>();


    public int offer(int customerId, int stake) {
        //维护stakes
        int result = stakes.merge(customerId, stake, Integer::sum);
        Stake newStake = new Stake(customerId, result);
        if(highstakes.size()>=20&&result<highstakes.peek().getStakeAmount()){ //判断堆大小是否大于20，并且金额是否小于堆顶，小于不用更新堆
            //不更新
            return result;
        }else{
            //更新
            //1.直接添加
            //2.出队堆顶再入队
            synchronized (highstakes){
                //添加之前尝试remove
                if(highstakes.remove(newStake)){
                    highstakes.offer(newStake);
                }else{
                    if(highstakes.size()<20){
                        highstakes.offer(newStake);
                    }else{
                        if(result>highstakes.peek().getStakeAmount()){
                            highstakes.poll();
                            highstakes.offer(newStake);
                        }
                    }
                }

            }
        }
        return result;
    }

    public List<Stake> getHighstakes() {
        Stake[] objects = highstakes.toArray(new Stake[0]);
        return Arrays.stream(objects).sorted(Comparator.comparing(Stake::getStakeAmount)).collect(Collectors.toList());
    }
}
