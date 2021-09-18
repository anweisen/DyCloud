package net.anweisen.cloud.driver.player.chat;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChatText implements SerializableObject {

	private String text;

	private ChatClickEvent clickEvent;
	private String click;

	private String hover;

	private ChatText() {
	}

	public ChatText(@Nonnull String text) {
		this.text = text;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(text);
		buffer.writeOptionalEnumConstant(clickEvent);
		buffer.writeOptionalString(click);
		buffer.writeOptionalString(hover);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		text = buffer.readString();
		clickEvent = buffer.readOptionalEnumConstant(ChatClickEvent.class);
		click = buffer.readOptionalString();
		hover = buffer.readOptionalString();
	}

	@Nonnull
	public ChatText addHover(@Nonnull String hover) {
		this.hover = hover;
		return this;
	}

	@Nonnull
	public ChatText addClick(@Nonnull ChatClickEvent event, @Nonnull String value) {
		this.clickEvent = event;
		this.click = value;
		return this;
	}

	@Nonnull
	public ChatText appendText(@Nonnull String text) {
		return setText(this.text + text);
	}

	@Nonnull
	public ChatText setText(@Nonnull String text) {
		Preconditions.checkNotNull(text, "");
		this.text = text;
		return this;
	}

	@Nonnull
	public String getText() {
		return text;
	}

	@Nullable
	public String getHover() {
		return hover;
	}

	@Nullable
	public ChatClickEvent getClickEvent() {
		return clickEvent;
	}

	@Nullable
	public String getClick() {
		return click;
	}

	@Override
	public String toString() {
		return "ChatText[text='" + text + "' hover='" + hover + "' click=" + clickEvent + "='" + click + "']";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChatText chatText = (ChatText) o;
		return clickEvent == chatText.clickEvent
			&& Objects.equals(text, chatText.text)
			&& Objects.equals(click, chatText.click)
			&& Objects.equals(hover, chatText.hover);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, clickEvent, click, hover);
	}
}
