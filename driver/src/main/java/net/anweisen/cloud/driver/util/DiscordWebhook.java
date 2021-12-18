package net.anweisen.cloud.driver.util;

import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class DiscordWebhook {

	protected String url;
	protected String content;
	protected String username;
	protected String avatarUrl;
	protected boolean tts;
	protected List<EmbedObject> embeds = new ArrayList<>();

	private DiscordWebhook() {
	}

	/**
	 * Constructs a new DiscordWebhook instance
	 *
	 * @param url The webhook URL obtained in Discord
	 */
	public DiscordWebhook(@Nonnull String url) {
		this.url = url;
	}

	public DiscordWebhook(@Nonnull String url, @Nonnull String username) {
		this.url = url;
		this.username = username;
	}

	public DiscordWebhook(@Nonnull String url, @Nonnull String username, @Nonnull String avatarUrl) {
		this.url = url;
		this.username = username;
		this.avatarUrl = avatarUrl;
	}

	public DiscordWebhook(@Nonnull String url, @Nonnull String username, @Nonnull String avatarUrl, @Nonnull String content, @Nonnull List<EmbedObject> embeds, boolean tts) {
		this.url = url;
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.content = content;
		this.embeds = embeds;
		this.tts = tts;
	}

	@Nonnull
	public DiscordWebhook setUrl(@Nonnull String url) {
		this.url = url;
		return this;
	}

	@Nonnull
	public DiscordWebhook setContent(@Nullable String content) {
		this.content = content;
		return this;
	}

	@Nonnull
	public DiscordWebhook setUsername(@Nullable String username) {
		this.username = username;
		return this;
	}

	@Nonnull
	public DiscordWebhook setAvatarUrl(@Nullable String avatarUrl) {
		this.avatarUrl = avatarUrl;
		return this;
	}

	@Nonnull
	public DiscordWebhook setTts(boolean tts) {
		this.tts = tts;
		return this;
	}

	@Nonnull
	public DiscordWebhook addEmbed(EmbedObject embed) {
		this.embeds.add(embed);
		return this;
	}

	public void execute() throws IOException {
		if (content == null && embeds.isEmpty())
			throw new IllegalArgumentException("Set content or add at least one EmbedObject");

		Document json = Documents.newJsonDocument();

		json.set("content", content);
		json.set("username", username);
		json.set("avatar_url", avatarUrl);
		json.set("tts", tts);

		if (!embeds.isEmpty()) {
			List<Document> embedObjects = new ArrayList<>();

			for (EmbedObject embed : embeds) {
				Document jsonEmbed = Documents.newJsonDocument();

				jsonEmbed.set("title", embed.getTitle());
				jsonEmbed.set("description", embed.getDescription());
				jsonEmbed.set("url", embed.getUrl());

				if (embed.getColor() != null) {
					Color color = embed.getColor();
					int rgb = color.getRed();
					rgb = (rgb << 8) + color.getGreen();
					rgb = (rgb << 8) + color.getBlue();

					jsonEmbed.set("color", rgb);
				}


				EmbedObject.Footer footer = embed.getFooter();
				if (footer != null) {
					Document jsonFooter = Documents.newJsonDocument();

					jsonFooter.set("text", footer.getText());
					jsonFooter.set("icon_url", footer.getIconUrl());
					jsonEmbed.set("footer", jsonFooter);
				}

				EmbedObject.Image image = embed.getImage();
				if (image != null) {
					Document jsonImage = Documents.newJsonDocument();

					jsonImage.set("url", image.getUrl());
					jsonEmbed.set("image", jsonImage);
				}

				EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
				if (thumbnail != null) {
					Document jsonThumbnail = Documents.newJsonDocument();

					jsonThumbnail.set("url", thumbnail.getUrl());
					jsonEmbed.set("thumbnail", jsonThumbnail);
				}

				EmbedObject.Author author = embed.getAuthor();
				if (author != null) {
					Document jsonAuthor = Documents.newJsonDocument();

					jsonAuthor.set("name", author.getName());
					jsonAuthor.set("url", author.getUrl());
					jsonAuthor.set("icon_url", author.getIconUrl());
					jsonEmbed.set("author", jsonAuthor);
				}

				List<EmbedObject.Field> fields = embed.getFields();
				List<Document> jsonFields = new ArrayList<>();
				for (EmbedObject.Field field : fields) {
					Document jsonField = Documents.newJsonDocument();

					jsonField.set("name", field.getName());
					jsonField.set("value", field.getValue());
					jsonField.set("inline", field.isInline());

					jsonFields.add(jsonField);
				}

				jsonEmbed.set("fields", jsonFields);
				embedObjects.add(jsonEmbed);
			}

			json.set("embeds", embedObjects);
		}

		URL url = new URL(this.url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setConnectTimeout(2500);
		connection.setReadTimeout(1000);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Content-Type", "application/json");

		OutputStream output = connection.getOutputStream();
		output.write(json.toString().getBytes(StandardCharsets.UTF_8));
		output.flush();
		output.close();

		connection.getInputStream().close();
		connection.disconnect();
	}

	@Nonnull
	public DiscordWebhook replaceEverywhere(@Nonnull CharSequence target, @Nonnull CharSequence replacement) {
		if (content != null) content = content.replace(target, replacement);
		if (username != null) username = username.replace(target, replacement);
		for (EmbedObject embed : embeds) {
			if (embed.author.name != null) embed.author.name = embed.author.name.replace(target, replacement);
			if (embed.description != null) embed.description = embed.description.replace(target, replacement);
			if (embed.title != null) embed.title = embed.title.replace(target, replacement);
			if (embed.footer.text != null) embed.footer.text = embed.footer.text.replace(target, replacement);
			for (EmbedObject.Field field : embed.fields) {
				if (field.name != null) field.name = field.name.replace(target, replacement);
				if (field.value != null) field.value = field.value.replace(target, replacement);
			}
		}
		return this;
	}

	public static class EmbedObject {

		protected String title;
		protected String description;
		protected String url;
		protected Color color;

		protected Footer footer;
		protected Thumbnail thumbnail;
		protected Image image;
		protected Author author;
		protected List<Field> fields = new ArrayList<>();

		public EmbedObject() {
		}

		public EmbedObject(@Nullable String title, @Nullable String description, @Nullable String url, @Nullable Color color,
		                   @Nullable Footer footer, @Nullable Thumbnail thumbnail, @Nullable Image image, @Nullable Author author,
		                   @Nonnull List<Field> fields) {
			this.title = title;
			this.description = description;
			this.url = url;
			this.color = color;
			this.footer = footer;
			this.thumbnail = thumbnail;
			this.image = image;
			this.author = author;
			this.fields = fields;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getUrl() {
			return url;
		}

		public Color getColor() {
			return color;
		}

		public Footer getFooter() {
			return footer;
		}

		public Thumbnail getThumbnail() {
			return thumbnail;
		}

		public Image getImage() {
			return image;
		}

		public Author getAuthor() {
			return author;
		}

		public List<Field> getFields() {
			return fields;
		}

		@Nonnull
		public EmbedObject setTitle(String title) {
			this.title = title;
			return this;
		}

		@Nonnull
		public EmbedObject setDescription(String description) {
			this.description = description;
			return this;
		}

		@Nonnull
		public EmbedObject setUrl(String url) {
			this.url = url;
			return this;
		}

		@Nonnull
		public EmbedObject setColor(Color color) {
			this.color = color;
			return this;
		}

		@Nonnull
		public EmbedObject setFooter(String text, String icon) {
			this.footer = new Footer(text, icon);
			return this;
		}

		@Nonnull
		public EmbedObject setThumbnail(String url) {
			this.thumbnail = new Thumbnail(url);
			return this;
		}

		@Nonnull
		public EmbedObject setImage(String url) {
			this.image = new Image(url);
			return this;
		}

		@Nonnull
		public EmbedObject setAuthor(String name, String url, String icon) {
			this.author = new Author(name, url, icon);
			return this;
		}

		@Nonnull
		public EmbedObject addField(String name, String value, boolean inline) {
			this.fields.add(new Field(name, value, inline));
			return this;
		}

		@Override
		public EmbedObject clone() {
			return new EmbedObject(
				title, description, url, color,
				footer == null ? null : footer.clone(),
				thumbnail == null ? null : thumbnail.clone(),
				image == null ? null : image.clone(),
				author == null ? null : author.clone(),
				DiscordWebhook.clone(fields, Field::clone)
			);
		}

		public static class Footer {

			protected String text;
			protected String iconUrl;

			private Footer() {
			}

			public Footer(String text, String iconUrl) {
				this.text = text;
				this.iconUrl = iconUrl;
			}

			private String getText() {
				return text;
			}

			private String getIconUrl() {
				return iconUrl;
			}

			@Override
			protected Footer clone() {
				return new Footer(text, iconUrl);
			}
		}

		public static class Thumbnail {

			private String url;

			private Thumbnail() {
			}

			public Thumbnail(String url) {
				this.url = url;
			}

			private String getUrl() {
				return url;
			}

			@Override
			protected Thumbnail clone() {
				return new Thumbnail(url);
			}
		}

		public static class Image {

			private String url;

			private Image() {
			}

			public Image(String url) {
				this.url = url;
			}

			private String getUrl() {
				return url;
			}

			@Override
			public Image clone() {
				return new Image(url);
			}
		}

		public static class Author {

			private String name;
			private String url;
			private String iconUrl;

			private Author() {
			}

			public Author(String name, String url, String iconUrl) {
				this.name = name;
				this.url = url;
				this.iconUrl = iconUrl;
			}

			private String getName() {
				return name;
			}

			private String getUrl() {
				return url;
			}

			private String getIconUrl() {
				return iconUrl;
			}

			@Override
			protected Author clone() {
				return new Author(name, url, iconUrl);
			}
		}

		public static class Field {

			private String name;
			private String value;
			private boolean inline;

			private Field() {
			}

			private Field(String name, String value, boolean inline) {
				this.name = name;
				this.value = value;
				this.inline = inline;
			}

			private String getName() {
				return name;
			}

			private String getValue() {
				return value;
			}

			private boolean isInline() {
				return inline;
			}

			@Override
			protected Field clone() {
				return new Field(name, value, inline);
			}
		}
	}

	@Override
	public DiscordWebhook clone() {
		return new DiscordWebhook(url, username, avatarUrl, content, clone(embeds, EmbedObject::clone), tts);
	}

	@Nonnull
	protected static <T> List<T> clone(@Nonnull Collection<T> collection, @Nonnull Function<T, T> cloner) {
		List<T> list = new ArrayList<>(collection.size());
		for (T current : collection) {
			list.add(cloner.apply(current));
		}
		return list;
	}
}
