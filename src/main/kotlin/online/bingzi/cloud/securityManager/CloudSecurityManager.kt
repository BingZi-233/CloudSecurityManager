package online.bingzi.cloud.securityManager

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

/**
 * Cloud security manager
 *
 * @constructor Create empty Cloud security manager
 */
object CloudSecurityManager : Plugin() {
    override fun onLoad() {
        info("Cloud Security Manager正在进行预初始化...")
        info("Cloud Security Manager预初始化已完成。")
    }

    override fun onEnable() {
        info("Cloud Security Manager正在进行初始化...")
        info("Cloud Security Manager初始化已完成。")
    }

    override fun onDisable() {
        info("Cloud Security Manager正在进行反初始化...")
        info("Cloud Security Manager反初始化已完成。")
    }
}