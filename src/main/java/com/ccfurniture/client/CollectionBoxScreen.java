package com.ccfurniture.client;

import com.ccfurniture.mail.MailboxNetworking;
import com.ccfurniture.screen.CollectionBoxScreenHandler;
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

/** Collection box GUI: a "Send to" text box, a Send button and the 9 slots to drop mail into. */
public class CollectionBoxScreen extends HandledScreen<CollectionBoxScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("cc_furniture", "textures/gui/collection_box.png");

	private TextFieldWidget targetField;

	public CollectionBoxScreen(CollectionBoxScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 176;
		this.backgroundHeight = 160;
	}

	@Override
	protected void init() {
		super.init();
		targetField = new TextFieldWidget(textRenderer, x + 52, y + 16, 82, 14, Text.translatable("gui.cc_furniture.mailbox.send_to"));
		targetField.setMaxLength(24);
		addDrawableChild(targetField);
		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cc_furniture.mailbox.send"), b -> {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeString(targetField.getText(), 64);
			ClientPlayNetworking.send(MailboxNetworking.SEND, buf);
		}).dimensions(x + 138, y + 15, 30, 16).build());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			client.player.closeHandledScreen();
			return true;
		}
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
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.mailbox.send_to"), 8, 20, 0x404040, false);
		context.drawText(textRenderer, Text.translatable("gui.cc_furniture.collection_box.mail"), 8, 35, 0x404040, false);
		context.drawText(textRenderer, playerInventoryTitle, 8, 67, 0x404040, false);
	}
}
