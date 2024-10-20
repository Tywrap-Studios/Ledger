package com.github.quiltservertools.ledger.network.packet.response

import com.github.quiltservertools.ledger.network.packet.LedgerPacket
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ResponseS2CPacket(override val channelId: Identifier, override var byteBuf: PacketByteBuf) : LedgerPacket<ResponseContent> {

    fun write(buf: PacketByteBuf?) {
        TODO("Unimplemented in the 1.20.1 v1.3.4 of Ledger, use populate instead.")
    }

/*    companion object {
//        val ID: CustomPayload.Id<ResponseS2CPacket> = CustomPayload.Id(LedgerPacketTypes.RESPONSE.id)
//        val CODEC: PacketCodec<PacketByteBuf, ResponseS2CPacket> = CustomPayload.codecOf(
//            ResponseS2CPacket::write
//        ) { _: PacketByteBuf? -> TODO() }
//
//        fun sendResponse(content: ResponseContent, sender: PacketSender) {
//            sender.sendPacket(ResponseS2CPacket(content))
//        }
//    } */

    companion object {
        private var buf: PacketByteBuf = PacketByteBufs.create()
        private val channel: Identifier = LedgerPacketTypes.RESPONSE.id
        fun sendResponse(content: ResponseContent, sender: PacketSender) {
            val response = ResponseS2CPacket(channel, buf)
            response.populate(content)
            sender.sendPacket(LedgerPacketTypes.RESPONSE.id, response.byteBuf)
        }
    }

    override fun populate(content: ResponseContent) {
        // Packet type, rollback response would be `ledger.rollback`
        byteBuf.writeIdentifier(content.type)
        // Response code
        byteBuf.writeInt(content.response)
    }
}
