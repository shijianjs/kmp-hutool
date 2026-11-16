package io.github.shijianjs.kmphutool.core.date.chinese

import kotlin.Pair

/**
 * Chinese lunar festivals keyed by `(month, day)`.
 *
 * Port of `cn.hutool.core.date.chinese.LunarFestival`. Hutool's multi-value `TableMap` becomes a
 * map from the key to every festival registered for that day.
 *
 * @hutool cn.hutool.core.date.chinese.LunarFestival
 */
object LunarFestival {
    private val L_FTV: Map<Pair<Int, Int>, List<String>> = buildMap {
        fun put(month: Int, day: Int, name: String) {
            val key = Pair(month, day)
            put(key, get(key).orEmpty() + name)
        }

        // 正月
        put(1, 1, "春节")
        put(1, 2, "犬日")
        put(1, 3, "猪日")
        put(1, 4, "羊日")
        put(1, 5, "牛日 破五日")
        put(1, 6, "马日 送穷日")
        put(1, 7, "人日 人胜节")
        put(1, 8, "谷日 八仙日")
        put(1, 9, "天日 九皇会")
        put(1, 10, "地日 石头生日")
        put(1, 12, "火日 老鼠娶媳妇日")
        put(1, 13, "上（试）灯日 关公升天日")
        put(1, 15, "元宵节 上元节")
        put(1, 18, "落灯日")

        // 二月
        put(2, 1, "中和节 太阳生日")
        put(2, 2, "龙抬头")
        put(2, 12, "花朝节")
        put(2, 19, "观世音圣诞")

        // 三月
        put(3, 3, "上巳节")

        // 四月
        put(4, 1, "祭雹神")
        put(4, 4, "文殊菩萨诞辰")
        put(4, 8, "佛诞节")

        // 五月
        put(5, 5, "端午节 端阳节")

        // 六月
        put(6, 6, "晒衣节 姑姑节")
        put(6, 6, "天贶节")
        put(6, 24, "彝族火把节")

        // 七月
        put(7, 7, "七夕")
        put(7, 14, "鬼节(南方)")
        put(7, 15, "盂兰盆节 中元节")
        put(7, 30, "地藏节")

        // 八月
        put(8, 15, "中秋节")

        // 九月
        put(9, 9, "重阳节")

        // 十月
        put(10, 1, "祭祖节")
        put(10, 15, "下元节")

        // 十一月
        put(11, 17, "阿弥陀佛圣诞")

        // 腊月
        put(12, 8, "腊八节")
        put(12, 16, "尾牙")
        put(12, 23, "小年")
        put(12, 30, "除夕")
    }

    /** Festivals on the given lunar date; a 29-day twelfth month maps 29 to 除夕. */
    fun getFestivals(year: Int, month: Int, day: Int): List<String> {
        var actualDay = day
        if (month == 12 && day == 29 && LunarInfo.monthDays(year, month) == 29) {
            actualDay++
        }
        return getFestivals(month, actualDay)
    }

    /** Festivals on the given lunar month/day, without large/small month correction. */
    fun getFestivals(month: Int, day: Int): List<String> = L_FTV[Pair(month, day)].orEmpty()
}
