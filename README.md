Messenger
=============

Messenger allows plugin developers to easily create editable language / format files for their users.


Setup
-------
Include Messenger in your project and instantiate it when your plugin loads:

```java
Messenger messenger;

...

public void onEnable() {
    messenger = new Messenger(this);
}
```

By default messenger will create a file in ```plugins/PluginName/messages.yml```. If you wish to change this you may use:

```java
messenger.setFileName("strings.yml");
```

Before using messenger to send any values, you must call ```messaging.load();```. Do this ***after*** you have set any defaults and other options you wish to be present.


Defaults
-------

Set up defaults for your plugin. Defaults are a map where the key is a String that the data will be stored under in YAML, and the value is either a String OR a String List that is the message itself.
When String Lists are used, each new String in the list will be sent as a new line.

In order to make retrieving messages easier, you should use constant String values to keep track of your keys, for instance:

```java
public static final String MESSAGE_NO_PERMISSION = "no-permission";
public static final String MESSAGE_USAGE = "usage";

Map<String, Object> defaults = new HashMap<String, Object>(){{
    put(MESSAGE_NO_PERMISSION, "You do not have permission to use that command");
    put(MESSAGE_USAGE, Arrays.asList(new String[]{"Usage:", "/mycommand <something> [something]"}));
}};
```

You can then set the defaults to be used by the Messenger:

```java
messenger.setDefaults(defaults);
```

The example above would produce a default messages.yml file containing the following:

```yaml
no-permission: 'You do not have permission to use that command'
usage:
- 'Usage:'
- '/mycommand <something> [something]'
```

Sending
-------

Before messages are sent, you must call ```messaging.load();```

Using String constants then allows easy sending of these messages to any CommandSender:

```java
CommandSender cs = ...
messenger.send(MESSAGE_NO_PERMISSION, cs);
```

If you have Objects to use to format the message, you may call it like so:

```java
String value1 = ...
String value2 = ...
CommandSender cs = ...
messenger.send(MESSAGE_NO_PERMISSION, cs, value1, value2);
```

Learn more about message formatting in the section below.

Formatting
-------

All messages are sent with the message prefix in front of them. You can change this prefix to your liking:

```java
messenger.setPrefix(ChatColor.GOLD + "[MyPlugin] " + ChatColor.WHITE);
```

Or you can disable the prefix by setting it to null.

All messages support the use of ```&``` to denote color codes, such as ```&e``` for yellow, where color codes can be found [here](http://ess.khhq.net/mc/).

All messages support the use of [Java's String.format](http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#format%28java.lang.String,%20java.lang.Object...%29) method to insert variable data into messages.

For example, the following message could be included in the plugin:

```java
public static final String MESSAGE_FORMATTED = "message-formatted";

Map<String, Object> defaults = new HashMap<String, Object>(){{
    put(MESSAGE_FORMATTED, "Your name is: %s");
}};
```

The message could then be formatted when a CommandSender sends a command by calling:

```java
CommandSender cs = ...
String name = cs.getName();
messenger.send(MESSAGE_FORMATTED, cs, name);
```

In general, it is good practice to use String variables when formatting so that they can easily be reorganized by a user.

To see more examples of formatting messages, see the example defaults in Messenger.java
