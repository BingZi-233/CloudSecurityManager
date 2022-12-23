package online.bingzi.cloud.securityManager.internal.listener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListeningWhitelist
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import online.bingzi.cloud.securityManager.internal.util.TokenBucket
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.platform.util.bukkitPlugin


object NetPackEvent {
    private val plugin: Plugin = bukkitPlugin
    private val windowClickTokenBucket: MutableMap<String, TokenBucket> = mutableMapOf()
    private val setCreativeSlotTokenBucket: MutableMap<String, TokenBucket> = mutableMapOf()

    @SubscribeEvent
    fun onEvent(event: PlayerJoinEvent) {
        val name = event.player.name
        windowClickTokenBucket[name] = TokenBucket(10)
        setCreativeSlotTokenBucket[name] = TokenBucket(10)
    }

    @SubscribeEvent
    fun onEvent(event: PlayerQuitEvent) {
        val name = event.player.name
        windowClickTokenBucket.remove(name)
        setCreativeSlotTokenBucket.remove(name)
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        val packets = mutableListOf<PacketType>().apply {
            this.add(PacketType.Play.Client.WINDOW_CLICK)
            this.add(PacketType.Play.Client.SET_CREATIVE_SLOT)
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(plugin, PacketType.values()) {
            override fun onPacketReceiving(event: PacketEvent) {
                val name = event.player?.name ?: return
                when (event.packetType) {
                    PacketType.Play.Client.WINDOW_CLICK -> {
                        windowClickTokenBucket[name]!!.let {
                            if (!it.tryGetToken()) {
                                warning("玩家 $name 触发攻击检测，已被服务器踢出")
                                submit {
                                    event.player.kickPlayer("§c检测到您的攻击行为，已被服务器踢出")
                                }
                                event.isCancelled = true
                            }
                        }
                    }

                    PacketType.Play.Client.SET_CREATIVE_SLOT -> {
                        setCreativeSlotTokenBucket[name]!!.let {
                            if (!it.tryGetToken()) {
                                warning("玩家 $name 触发攻击检测，已被服务器踢出")
                                submit {
                                    event.player.kickPlayer("§c检测到您的攻击行为，已被服务器踢出")
                                }
                                event.isCancelled = true
                            }
                        }
                    }
                }
            }

            override fun onPacketSending(event: PacketEvent) {

            }

            override fun getReceivingWhitelist(): ListeningWhitelist {
                // 建造白名单
                return ListeningWhitelist.newBuilder()
                    .types(packets)
                    .build()
            }

            override fun getSendingWhitelist(): ListeningWhitelist {
                // 建造白名单
                return ListeningWhitelist.newBuilder()
                    .types(packets)
                    .build()
            }
        })
    }
}