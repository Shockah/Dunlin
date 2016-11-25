package io.shockah.dunlin.complexcommandparser;

import java.util.ArrayList;
import java.util.List;
import io.shockah.dunlin.commands.CommandParseException;

public class ComplexParser {
	public final List<ComplexParserArgument> arguments = new ArrayList<>();
	
	public ComplexParserResult parse(String input) throws CommandParseException {
		ComplexParserResult result = new ComplexParserResult(this);
		List<ComplexParserArgument> arguments = new ArrayList<>(this.arguments);
		while (input != null && !input.isEmpty()) {
			String old = input;
			
			String[] split = input.split("\\s");
			if (split[0].startsWith("-")) {
				for (ComplexParserArgument arg : arguments) {
					String name = split[0].substring(1);
					for (String argName : arg.names) {
						if (name.equalsIgnoreCase(argName)) {
							ComplexParserArgument.ParseResult parseResult = arg.parse(split[0].substring(1), input);
							result.values.add(parseResult);
							input = parseResult.stringLeft;
							if (!arg.multiUse)
								arguments.remove(arg);
							continue;
						}
					}
				}
			}
			
			if (old.equals(input))
				throw new CommandParseException(String.format("Couldn't parse input: '%s'.", input));
		}
		return result;
	}
}