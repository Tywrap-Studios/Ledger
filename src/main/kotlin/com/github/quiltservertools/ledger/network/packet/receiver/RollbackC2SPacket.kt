package com.github.quiltservertools.ledger.network.packet.receiver

import com.github.quiltservertools.ledger.Ledger
import com.github.quiltservertools.ledger.commands.CommandConsts
import com.github.quiltservertools.ledger.commands.arguments.SearchParamArgument
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import com.github.quiltservertools.ledger.network.packet.Receiver
import com.github.quiltservertools.ledger.network.packet.response.ResponseCodes
import com.github.quiltservertools.ledger.network.packet.response.ResponseContent
import com.github.quiltservertools.ledger.network.packet.response.ResponseS2CPacket
import com.github.quiltservertools.ledger.utility.MessageUtils
import com.github.quiltservertools.ledger.utility.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

/*data class RollbackC2SPacket(val input: String) : CustomPayload {
//
//    override fun getId() = ID
//
//    companion object : ServerPlayNetworking.PlayPayloadHandler<RollbackC2SPacket> {
//        val ID: CustomPayload.Id<RollbackC2SPacket> = CustomPayload.Id(LedgerPacketTypes.ROLLBACK.id)
//        val CODEC: PacketCodec<PacketByteBuf, RollbackC2SPacket> = CustomPayload.codecOf({ _, _ -> TODO() }, {
//            RollbackC2SPacket(it.readString())
//        })
//
//        override fun receive(payload: RollbackC2SPacket, context: ServerPlayNetworking.Context) {
//            val player = context.player()
//            val sender = context.responseSender()
//            if (!Permissions.check(player, "ledger.networking", CommandConsts.PERMISSION_LEVEL) ||
//                !Permissions.check(player, "ledger.commands.purge", CommandConsts.PERMISSION_LEVEL)
//            ) {
//                ResponseS2CPacket.sendResponse(
//                    ResponseContent(LedgerPacketTypes.PURGE.id, ResponseCodes.NO_PERMISSION.code),
//                    sender
//                )
//                return
//            }
//
//            val params = SearchParamArgument.get(payload.input, player.commandSource)
//
//            ResponseS2CPacket.sendResponse(
//                ResponseContent(LedgerPacketTypes.PURGE.id, ResponseCodes.EXECUTING.code),
//                sender
//            )
//
//            Ledger.launch {
//                DatabaseManager.purgeActions(params)
//
//                ResponseS2CPacket.sendResponse(
//                    ResponseContent(LedgerPacketTypes.PURGE.id, ResponseCodes.COMPLETED.code),
//                    sender
//                )
//            }
//        }
//    } */

// TODO: Get this tested
class RollbackC2SPacket : Receiver {
    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        sender: PacketSender
    ) {
        if (!Permissions.check(player, "ledger.networking", CommandConsts.PERMISSION_LEVEL) ||
            !Permissions.check(player, "ledger.commands.rollback", CommandConsts.PERMISSION_LEVEL)
        ) {
            ResponseS2CPacket.sendResponse(
                ResponseContent(LedgerPacketTypes.ROLLBACK.id, ResponseCodes.NO_PERMISSION.code),
                sender
            )
            return
        }

        val source = player.commandSource
        val restore = buf.readBoolean()
        val args = buf.readString()

        val params = SearchParamArgument.get(args, source)

        ResponseS2CPacket.sendResponse(
            ResponseContent(LedgerPacketTypes.ROLLBACK.id, ResponseCodes.EXECUTING.code),
            sender
        )

        Ledger.launch(Dispatchers.IO) {
            MessageUtils.warnBusy(source)
            if (restore) {
                val actions = DatabaseManager.restoreActions(params)

                source.world.launchMain {
                    for (action in actions) {
                        action.restore(source.server)
                        action.rolledBack = false
                    }

                    ResponseS2CPacket.sendResponse(
                        ResponseContent(LedgerPacketTypes.ROLLBACK.id, ResponseCodes.COMPLETED.code),
                        sender
                    )
                }
            } else {
                val actions = DatabaseManager.rollbackActions(params)

                source.world.launchMain {
                    for (action in actions) {
                        action.rollback(source.server)
                        action.rolledBack = true
                    }

                    ResponseS2CPacket.sendResponse(
                        ResponseContent(LedgerPacketTypes.ROLLBACK.id, ResponseCodes.COMPLETED.code),
                        sender
                    )
                }
            }
        }
    }
}
