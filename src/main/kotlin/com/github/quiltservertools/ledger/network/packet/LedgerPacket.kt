package com.github.quiltservertools.ledger.network.packet

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface LedgerPacket<T> {
    val channelId: Identifier
    var byteBuf: PacketByteBuf
    fun populate(content: T)
}
