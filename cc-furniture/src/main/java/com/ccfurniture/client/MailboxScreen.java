package com.ccfurniture.client;

import com.ccfurniture.mail.MailboxNetworking;
import com.ccfurniture.screen.MailboxScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Mailbox GUI with a text box for this mailbox's address and one for the recipient. */
public class MailboxScreen extends HandledScreen<MailboxScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("cc_furniture", "textures/gui/mailbox.png");

	private TextFieldWidget addressField;
	private TextFieldWidget targetField;

	public MailboxScreen(MailboxScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 176;
		this.backgroundHeight = 204;
	}

	@Override
	protected void init() {
		super.init();
		addressField = new TextFieldWidget(textRenderer, x + 52, y + 16, 82, 14, Text.translatable("gui.cc_furniture.mailbox.address"));
		addressField.setMaxLength(24);
		addressField.setText(handler.getAddress());
		addDrawableChild(addressField);

		targetField = new TextFieldWidget(textRenderer, x + 52, y + 34, 82, 14, Text.translatable("gui.cc_furniture.mailbox.send_to"));
		targetField.setMaxLength(24);
		addDrawableChild(targetField);

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cc_furniture.mailbox.set"),
			b -> sendText(MailboxNetworking.SET_ADDRESS, addressField.getText()))
			.dimensions(x + 138, y + 15, 30, 16).build());
		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cc_furniture.mailbox.send"),
			b -> sendText(MailboxNetworking.SEND, targetField.getText()))
			.dimensions(x + 138, y + 33, 30, 16).build());
	}

	private void sendText(Identifier channel, String text) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(text, 64);
		ClientPlayNetworking.send(channel, buf);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {   // escape
			client.player.closeHandledScreen();
			return true;
		}
		// typing 'e' in a text box must not close the screen
		if (addressField.keyPressed(keyCode, scanCode, modifiers) || addressField.isActive()) return true;
		if (targetField.keyPressed(keyCode, scanCode, modifiers) || targetField.isActive()) return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(textRenderer, title, 8, 5, 0x404040, false);
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.mailbox.address"), 8, 20, 0x404040, false);
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.mailbox.send_to"), 8, 38, 0x404040, false);
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.mailbox.inbox"), 8, 52, 0x404040, false);
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.mailbox.outbox"), 8, 82, 0x404040, false);
		context.drawText(textRenderer, playerInventoryTitle, 8, 111, 0x404040, false);
	}
}
