package com.github.quiltservertools.ledger.network.packet.action

import com.github.quiltservertools.ledger.actions.ActionType
import com.github.quiltservertools.ledger.network.packet.LedgerPacket
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/*data class ActionS2CPacket(val content: ActionType) : CustomPayload {
//
//    private fun write(buf: PacketByteBuf?) {
//        // Position
//        buf?.writeBlockPos(content.pos)
//        // Type
//        buf?.writeString(content.identifier)
//        // Dimension
//        buf?.writeIdentifier(content.world)
//        // Objects
//        buf?.writeIdentifier(content.oldObjectIdentifier)
//        buf?.writeIdentifier(content.objectIdentifier)
//        // Source
//        buf?.writeString(content.sourceProfile?.name ?: "@" + content.sourceName)
//        // Epoch second of event, sent as a long
//        buf?.writeLong(content.timestamp.epochSecond)
//        // Has been rolled back?
//        buf?.writeBoolean(content.rolledBack)
//        // NBT
//        buf?.writeString(content.extraData ?: "")
//    }
//
//    override fun getId() = ID
//
//    companion object {
//        val ID: Id<ActionS2CPacket> = Id(LedgerPacketTypes.ACTION.id)
//        val CODEC: PacketCodec<PacketByteBuf, ActionS2CPacket> = CustomPayloadS2CPacket.codecOf(
//            ActionS2CPacket::write
//        ) { _: PacketByteBuf? -> TODO() }
    }*/

// TODO: Get this tested
class ActionS2CPacket : LedgerPacket<ActionType> {
    override val channelId: Identifier = LedgerPacketTypes.ACTION.id
    override var byteBuf: PacketByteBuf = PacketByteBufs.create()

    override fun populate(content: ActionType) {
        // Position
        byteBuf.writeBlockPos(content.pos)
        // Type
        byteBuf.writeString(content.identifier)
        // Dimension
        byteBuf.writeIdentifier(content.world)
        // Objects
        byteBuf.writeIdentifier(content.oldObjectIdentifier)
        byteBuf.writeIdentifier(content.objectIdentifier)
        // Source
        byteBuf.writeString(content.sourceProfile?.name ?: "@" + content.sourceName)
        // Epoch second of event, sent as a long
        byteBuf.writeLong(content.timestamp.epochSecond)
        // Has been rolled back?
        byteBuf.writeBoolean(content.rolledBack)
        // NBT
        byteBuf.writeString(content.extraData ?: "")
    }
}
