package net.anweisen.cloud.base.console.jline3;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.command.sender.ConsoleCommandSender;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collection;
import java.util.List;

public class JLine3Completer implements Completer {

	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		String buffer = line.line();

		Collection<String> responses = CloudBase.getInstance().getCommandManager().completeCommand(ConsoleCommandSender.INSTANCE, buffer);

		for (String response : responses) {
			candidates.add(new Candidate(response));
		}
	}
}
