package com.ccfurniture.mail;

import com.ccfurniture.CCFurniture;
import com.ccfurniture.screen.CollectionBoxScreenHandler;
import com.ccfurniture.screen.MailboxScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

/** Client -> server packets sent by the mailbox GUI. Both just carry one text field. */
public final class MailboxNetworking {
	public static final Identifier SET_ADDRESS = CCFurniture.id("mailbox_set_address");
	public static final Identifier SEND = CCFurniture.id("mailbox_send");

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(SET_ADDRESS, (server, player, handler, buf, responseSender) -> {
			String text = buf.readString(64);
			server.execute(() -> {
				if (player.currentScreenHandler instanceof MailboxScreenHandler m && m.be != null) {
					m.be.playerSetAddress(player, text);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(SEND, (server, player, handler, buf, responseSender) -> {
			String text = buf.readString(64);
			server.execute(() -> {
				if (player.currentScreenHandler instanceof CollectionBoxScreenHandler c && c.be != null) {
					c.be.playerSend(player, text);
					return;
				}
				if (player.currentScreenHandler instanceof MailboxScreenHandler m && m.be != null) {
					m.be.playerSend(player, text);
				}
			});
		});
	}

	private MailboxNetworking() {}
}
