package com.github.quiltservertools.ledger.network.packet.handshake

import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import net.minecraft.network.PacketByteBuf

data class HandshakeS2CPacket(val content: HandshakeContent) : CustomPayload {

    // TODO: ALL OF THIS IDK ABOUT, MIGHT NEED INSIGHT FROM ORIGINAL DEVS

    fun write(buf: PacketByteBuf?) {
        // Ledger information
        // Protocol Version
        buf?.writeInt(content.protocolVersion)

        // Ledger Version
        buf?.writeString(content.ledgerVersion)

        // We tell the client mod how many actions we are writing
        buf?.writeInt(content.actions.size)

        for (action in content.actions) {
            buf?.writeString(action)
        }
    }

    override fun getId() = ID

    companion object {
        val ID: CustomPayload.Id<HandshakeS2CPacket> = CustomPayload.Id(LedgerPacketTypes.HANDSHAKE.id)
        val CODEC: PacketCodec<PacketByteBuf, HandshakeS2CPacket> = CustomPayload.codecOf(
            HandshakeS2CPacket::write
        ) { _: PacketByteBuf? -> TODO() }
    }
}
