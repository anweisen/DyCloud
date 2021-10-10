package net.anweisen.cloud.driver.player.chat;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChatText implements SerializableObject {

	public static final ChatText NEW_LINE = new ChatText("\n");
	public static final ChatText SPACE = new ChatText(" ");
	public static final ChatText EMPTY = new ChatText("");
	public static final ChatText RESET = new ChatText("§r");

	private String text;

	private ChatClickEvent clickEvent;
	private String click;

	private String hover;

	public ChatText() {
		this("");
	}

	public ChatText(@Nonnull String text) {
		this.text = text;
	}

	public ChatText(@Nullable Object text) {
		this(String.valueOf(text));
	}

	public ChatText(@Nonnull Object... text) {
		this();
		for (Object current : text) {
			appendText(String.valueOf(current));
		}
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(text);
		buffer.writeOptionalEnum(clickEvent);
		buffer.writeOptionalString(click);
		buffer.writeOptionalString(hover);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		text = buffer.readString();
		clickEvent = buffer.readOptionalEnum(ChatClickEvent.class);
		click = buffer.readOptionalString();
		hover = buffer.readOptionalString();
	}

	@Nonnull
	public ChatText setHover(@Nonnull String hover) {
		this.hover = hover;
		return this;
	}

	@Nonnull
	public ChatText setClick(@Nonnull ChatClickEvent event, @Nonnull String value) {
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

	public boolean isEmpty() {
		return text.isEmpty();
	}

	@Override
	public String toString() {
		return "ChatText[text='" + text + "' hover='" + hover + "' click=" + (clickEvent == null ? null : clickEvent + "=:'" + click + "'") + "]";
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

	@Nonnull
	public static String toString(@Nonnull ChatText... text) {
		StringBuilder builder = new StringBuilder();
		for (ChatText current : text) {
			builder.append(current.text);
		}
		return builder.toString();
	}
}
