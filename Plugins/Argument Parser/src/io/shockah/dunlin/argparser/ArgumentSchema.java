package io.shockah.dunlin.argparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import io.shockah.dunlin.commands.CommandParseException;

public class ArgumentSchema {
	public static final Pattern ARGUMENT_PATTERN = Pattern.compile("^(.*?)\\: ?(.*)$");
	
	protected final Map<String, ArgumentType> arguments = new HashMap<>();
	protected final boolean hasUnnamedValue;
	
	protected ArgumentSchema(boolean hasUnnamedValue) {
		this.hasUnnamedValue = hasUnnamedValue;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> parse(String input) throws CommandParseException {
		Map<String, Object> args = new HashMap<>();
		String[] lines = input.split("\r?\n");
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.isEmpty())
				continue;
			
			Matcher m = ARGUMENT_PATTERN.matcher(line);
			if (m.find()) {
				String argName = m.group(1).toLowerCase();
				ArgumentType argType = arguments.get(argName);
				if (argType == null)
					throw new CommandParseException(String.format("Invalid argument: %s", line));
				
				StringBuilder valBuilder = new StringBuilder(m.group(2));
				if (argType == ArgumentType.String || argType == ArgumentType.StringList) {
					int j = i + 1;
					while (j < lines.length) {
						String line2 = lines[j];
						if (line2.startsWith("\\")) {
							valBuilder.append('\n');
							valBuilder.append(line2);
						} else {
							break;
						}
						j++;
					}
				}
				
				Object val = argType.parse(valBuilder.toString());
				if (argType.isListType) {
					List<Object> list = (List<Object>)args.get(argName);
					if (list == null) {
						list = new ArrayList<>();
						args.put(argName, list);
					}
					list.add(val);
				} else {
					if (args.containsKey(argName))
						throw new CommandParseException(String.format("Duplicate argument: %s", line));
					args.put(argName, val);
				}
			} else {
				if (hasUnnamedValue)
					args.put("", StringUtils.join(lines, "\n", i, lines.length));
				else
					throw new CommandParseException(String.format("Invalid argument: %s", line));
				break;
			}
		}
		
		return args;
	}
	
	public static class Builder {
		protected final Map<String, ArgumentType> arguments = new HashMap<>();
		protected boolean hasUnnamedValue = false;
		
		public ArgumentSchema build() {
			ArgumentSchema schema = new ArgumentSchema(hasUnnamedValue);
			schema.arguments.putAll(arguments);
			return schema;
		}
		
		public Builder withUnnamedValue(boolean hasUnnamedValue) {
			this.hasUnnamedValue = hasUnnamedValue;
			return this;
		}
		
		public Builder with(String argumentName, ArgumentType argumentType) {
			arguments.put(argumentName.toLowerCase(), argumentType);
			return this;
		}
	}
}