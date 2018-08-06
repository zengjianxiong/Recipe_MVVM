package com.recipe_mvvm.zfl.lib_base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/26.
 */

public class RandomUtil
{
    /**
     * 获取列表中随机一个位置数(不包括listSize)
     *
     * @param listSize
     * @return
     */
    public static int random(int listSize)
    {
        Random random = new Random();
        return random.nextInt(listSize);
    }

    /**
     * 获取[min, max]范围内的随机数(包括min和max)
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max)
    {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 返回一个对象列表中的随机对象
     *
     * @param list
     * @return
     */
    public static Object random(Object[] list)
    {
        return list[random(list.length)];
    }

    /**
     * 返回一个对象列表中的随机对象
     *
     * @param list
     * @return
     */
    public static Object random(List<Object> list)
    {
        return list.get(random(list.size()));
    }

    /**
     * 从一个列表中选出randomNums个随机位置,randomNums不可大于list的size
     *
     * @param list
     * @param randomNums
     * @return
     */
    public static List<Integer> random(List list, int randomNums)
    {
        if (randomNums > list.size())
            return null;
        List<Integer> targetRandoms = new ArrayList<>();
        for (int i = 0; i < randomNums; i++)
        {
            Integer targetPosition = random(list.size());
            while (targetRandoms.contains(targetPosition)) {
                targetPosition = random(list.size());
            }
            targetRandoms.add(targetPosition);
        }
        return targetRandoms;
    }

    /**
     * 从一个列表中选出randomNums个随机位置,randomNums不可大于list的size
     * @param list
     * @param randomNums
     * @return
     */
    public static List<Integer> random(Object[] list, int randomNums) {
        if (randomNums > list.length) return null;
        List<Integer> targetRandoms = new ArrayList<>();
        for (int i = 0; i < randomNums; i++)
        {
            Integer targetPosition = random(list.length);
            while (targetRandoms.contains(targetPosition)) {
                targetPosition = random(list.length);
            }
            targetRandoms.add(targetPosition);
        }
        return targetRandoms;
    }

    /**
     * 返回list中随机的randomNums个元素
     * @param list
     * @param randomNums
     * @return
     */
    public static List<Integer> random(int[] list, int randomNums) {
        if (randomNums > list.length) return null;
        List<Integer> targetRandoms = new ArrayList<>();
        for (int i = 0; i < randomNums; i++) {
            Integer targetElement = list[random(list.length)];
            while (targetRandoms.contains(targetElement)) {
                targetElement = list[random(list.length)];
            }
            targetRandoms.add(targetElement);
        }
        return targetRandoms;
    }

}
