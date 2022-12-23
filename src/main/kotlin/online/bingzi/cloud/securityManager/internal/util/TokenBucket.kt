package online.bingzi.cloud.securityManager.internal.util

class TokenBucket(
    // 令牌桶容量
    private val MAX_CAPACITY: Int = 20,
) {
    private val REFILL_INTERVAL = 1000L // 每次补充令牌的时间间隔
    private var bucket: Int = 0 // 令牌桶，每个元素代表一个令牌
    private var lastBucketFillTime: Long = System.currentTimeMillis() // 上一次令牌桶填充的时间

    init {
        fillBucket() // 初始化令牌桶
    }

    /**
     * 从桶中读取一个令牌。如果桶子里没有令牌，则从队列中读取并将其添加到桶子中
     */
    private fun getToken(): Int {
        if (lastBucketFillTime + REFILL_INTERVAL < System.currentTimeMillis()) {
            fillBucket()
        }
        return if (bucket > 0) {
            --bucket
        } else {
            fillBucket()
            getToken()
        }
    }

    /**
     * 向令牌桶中添加新令牌
     */
    private fun fillBucket() {
        bucket = MAX_CAPACITY
        lastBucketFillTime += REFILL_INTERVAL
    }

    /**
     * 尝试获取令牌。如果成功获得令牌，则返回true，否则返回false
     */
    fun tryGetToken(): Boolean {
        return getToken() > 0
    }
}