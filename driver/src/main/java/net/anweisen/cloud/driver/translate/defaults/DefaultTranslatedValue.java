package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.player.chat.ChatClickReaction;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.translate.LanguageSection;
import net.anweisen.cloud.driver.translate.TranslatedValue;
import net.anweisen.utilities.common.misc.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see TranslatedValue
 */
public class DefaultTranslatedValue implements TranslatedValue, LoggingApiUser {

	private final LanguageSection section;
	private final String name;

	private final List<String> value;

	public DefaultTranslatedValue(@Nonnull LanguageSection section, @Nonnull String name, @Nonnull List<String> value) {
		this.section = section;
		this.name = name;
		this.value = value;
	}

	@Nonnull
	@Override
	public LanguageSection getSection() {
		return section;
	}

	@Nonnull
	@Override
	public String getFullName() {
		return section.getId() + "." + name;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	public List<String> getValue() {
		return value;
	}

	@Nonnull
	@Override
	public String asString(@Nonnull Object... args) {
		String string = value.size() == 1 ? value.get(0) : StringUtils.getIterableAsString(value, "\n", Function.identity());
		return formatString(string);
	}

	@Nonnull
	@Override
	public String[] asArray(@Nonnull Object... args) {
		String[] array = new String[value.size()];
		for (int i = 0; i < value.size(); i++) {
			array[i] = formatString(value.get(i), args);
		}
		return array;
	}

	@Nonnull
	@Override
	public List<String> asList(@Nonnull Object... args) {
		List<String> list = new ArrayList<>(value.size());
		for (String line : value) {
			list.add(formatString(line, args));
		}
		return list;
	}

	@Nonnull
	@Override
	public ChatText[] asText(@Nonnull Object... args) {
		String[] lines = asArray(args);
		List<ChatText> result = new ArrayList<>();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (i > 0) result.add(ChatText.NEW_LINE);
			formatChat(line, result);
		}

		return result.toArray(new ChatText[0]);
	}

	/**
	 * Splits the given sequence and converts it into ChatText objects and adds it to the output.
	 * See the class description for syntax.
	 */
	protected void formatChat(@Nonnull String sequence, @Nonnull List<ChatText> output) {
		char start = '(', // start embed
			 end = ')', // end embed
			 link = ':'; // link event type and event content

		int phase = 0; // 0:NONE 1:TEXT 2:SWITCH 3:TYPE 4:CONTENT
		StringBuilder argument = new StringBuilder();
		StringBuilder type = new StringBuilder();
		StringBuilder content = new StringBuilder();
		StringBuilder text = new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (c == start) {
				if (phase == 0) {
					if (text.length() > 0) {
						output.add(new ChatText(text));
						text.setLength(0);
					}
					phase = 1;
				} else if (phase == 2) {
					phase = 3;
				} else {
					output.add(new ChatText(start, argument));
					argument.setLength(0);
					phase = 1;
				}

				continue;
			}
			if (c == end) {
				if (phase == 1) {
					phase = 2;
					continue;
				}
				if (phase == 4) {
					output.add(new ChatText(argument).setClick(ChatClickReaction.getByShortcut(type.toString()), content.toString()));
					argument.setLength(0);
					type.setLength(0);
					content.setLength(0);
					phase = 0;
					continue;
				}
			}
			if (phase == 1) {
				argument.append(c);
				continue;
			}
			if (phase == 3) {
				if (c == link) {
					phase = 4;
					continue;
				}

				type.append(c);
				continue;
			}
			if (phase == 4) {
				content.append(c);
				continue;
			}
			if (phase == 2) {
				output.add(new ChatText(start, argument, end, c));
				argument.setLength(0);
				phase = 0;
				continue;
			}

			text.append(c);
		}

		if (text.length() > 0) output.add(new ChatText(text));
	}

	@Nonnull
	protected String formatString(@Nonnull String sequence, @Nonnull Object... args) {
		char start = '{', // start argument
			 end = '}', // end argument
			 trans = '$'; // marker for translations

		boolean markedTrans = false, inArgument = false;
		StringBuilder argument = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (c == trans && !inArgument) {
				markedTrans = true;
				continue;
			}
			if (c == end && inArgument) {
				inArgument = false;
				if (markedTrans) {
					String translation = argument.toString();
					if (translation.equals(getName()))      throw new IllegalStateException("Cannot use itself");
					if (translation.equals(getFullName()))  throw new IllegalStateException("Cannot use itself");

					final TranslatedValue value;
					if (getSection().hasValue(translation)) {
						value = getSection().getValue(translation);
					} else {
						value = getSection().getParent().getValue(translation);
					}

					builder.append(value.asString(args));
				} else {
					try {
						int arg = Integer.parseInt(argument.toString());
						Object current = args[arg];
						Object replacement =
							  current instanceof Supplier ? ((Supplier<?>)current).get()
							: current instanceof Callable ? ((Callable<?>)current).call()
							: current;
						builder.append(replacement);
					} catch (NumberFormatException | IndexOutOfBoundsException ex) {
						warn("Invalid argument index '{}' in {}", argument, this);
						builder.append(start).append(argument).append(end);
					} catch (Exception ex) {
						warn("Error while getting argument index '{}'", ex);
						builder.append(start).append(ex.getClass().getName()).append(end);
					}
				}

				argument = new StringBuilder();
				continue;
			}
			if (c == start && !inArgument) {
				inArgument = true;
				continue;
			}
			if (inArgument) {
				argument.append(c);
				continue;
			}
			if (markedTrans) {
				argument.append(trans);
				continue;
			}
			builder.append(c);
		}
		if (argument.length() > 0) builder.append(start).append(argument);
		return builder.toString();
	}

	@Override
	public String toString() {
		return "TranslatedValue[name=" + section.getId() + "." + name + " language=" + section.getParent().getId() + " value=" + value + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultTranslatedValue that = (DefaultTranslatedValue) o;
		return Objects.equals(section, that.section) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(section, name);
	}
}
