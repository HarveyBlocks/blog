# 解析命令与发送请求

```java
/** 
 * 双引号和单引号内的不被分割, 以逗号和空格分割
 */
private static String[] splitString(String input) {
    List<String> result = new ArrayList<>();
    Pattern pattern = Pattern.compile("[^\\s\",']+|\"[^\"]*\"|'[^']*'");
    Matcher matcher = pattern.matcher(input);
    while (matcher.find()) {
        result.add(matcher.group());
    }
    return result.toArray(new String[0]);
}
```

```java
public class ParseCommand {
    public static int doParse(String[] split, ChannelHandlerContext ctx) {

        try {
            if (split.length<2){
                throw new IllegalArgumentException(Arrays.toString(split));
            }
            split[0] = split[0].toLowerCase();
            split[1] = split[1].toLowerCase();
            Message msg = null;
            switch (split[0]) {
                case "person":
                    msg = parsePersonCommand(split);
                    break;
                case "group":
                    msg = parseGroupCommand(split);
                    break;
                default:
                    throw new UnknownCommandException(split[0]);
            }
            if (msg!=null){
                ctx.writeAndFlush(msg);
                return 1;
            }
        } catch (UnknownCommandException e) {
            System.err.println("unknown command with: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("illegal option counts:" + e.getMessage());
        }
        return 0;
    }

    private static Message parseGroupCommand(String[] split) {
        Message message = null;
        switch (split[1]) {
            case "send":
                if (split.length != 4) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupChatRequestMessage(currentUsername(), split[2], split[3]);
                break;
            case "create":
                if (split.length < 4) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                String[] members = Arrays.copyOfRange(split, 3, split.length);
                // 对重复项会进行合并
                Set<String> memberSet = Arrays.stream(members).collect(Collectors.toSet());
                memberSet.add(currentUsername()); // 当前用户也会加入群聊(自动)
                message = new GroupCreateRequestMessage(split[2], memberSet);
                break;
            case "members":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupMembersRequestMessage(split[2]);
                break;
            case "join":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupJoinRequestMessage(currentUsername(),split[2]);
                break;
            case "quit":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupQuitRequestMessage(currentUsername(),split[2]);
                break;
            default:
                throw new UnknownCommandException(split[1]);
        }
        return message;
    }

    private static Message parsePersonCommand(String[] split) {
        Message message = null;
        switch (split[1]) {
            case "send":
                if (split.length != 4) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new ChatRequestMessage(currentUsername(), split[2], split[3]);
                break;
            case "quit":
                if (split.length != 2) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                break;
            default:
                throw new UnknownCommandException(split[1]);
        }
        return message;
    }

}

class UnknownCommandException extends IllegalArgumentException {
    public UnknownCommandException(String msg) {
        super(msg);
    }
}
```

