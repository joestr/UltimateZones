package net.dertod2.UltimateZones.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.v1_13_R1.ChatMessageType;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;

/**
 * Better Wrapper for ChatComponents than Canary/Bukkit/BungeeCord has<br />
 * Allows to create Components easily
 * 
 * @author DerTod2
 *
 */
public class ChatComponent {
    private String text;
    private boolean mustTranslate = false;

    private Hover hoverComponent;
    private ChatComponent hoverValue;
    private net.minecraft.server.v1_13_R1.NBTTagCompound hoverCompound;

    private Click clickComponent;
    private String clickValue;

    private ChatComponent before;
    private ChatComponent after;

    private List<ChatColor> formatList = new ArrayList<ChatColor>();

    public ChatComponent() {
        this("", new ChatColor[] {});
    }

    public ChatComponent(String text) {
        this(text, new ChatColor[] {});
    }

    public ChatComponent(Number number) {
        this(number, new ChatColor[] {});
    }

    public ChatComponent(ItemStack itemStack) {
        this(itemStack, new ChatColor[] {});
    }

    public ChatComponent(String text, ChatColor... chatColors) {
        this.text = text;

        for (ChatColor chatColor : chatColors) {
            this.formatList.add(chatColor);
        }
    }

    public ChatComponent(Number number, ChatColor... chatColors) {
        this.text = number.toString();

        for (ChatColor chatColor : chatColors) {
            this.formatList.add(chatColor);
        }
    }

    /**
     * Creates an new ChatComponent with an hover Effect for the ItemStack data and
     * the given Color Codes
     * 
     * @param itemStack
     * @return the new ChatComponent object
     */
    public ChatComponent(ItemStack itemStack, ChatColor... chatColors) {
        this.text(itemStack);
        this.hover(itemStack);

        for (ChatColor chatColor : chatColors) {
            this.formatList.add(chatColor);
        }
    }

    /**
     * Overrides the text inside this ChatComponent with the given text
     * 
     * @param text
     * @return this ChatComponent Object
     */
    public ChatComponent text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Overrides the text inside this ChatComponent with the given text
     * 
     * @param text
     * @return this ChatComponent Object
     */
    public ChatComponent text(Number number) {
        this.text = number.toString();
        return this;
    }

    /**
     * Returns the text inside this ChatComponent
     * 
     * @return
     */
    public String text() {
        return this.text;
    }

    /**
     * Sets the text of this Component to the ItemName of the ItemStack<br />
     * When no name was set the translated name of the stack will be sent.
     * 
     * @param itemStack
     * @return this ChatComponent object
     */
    public ChatComponent text(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        net.minecraft.server.v1_13_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsStack == null) {
            net.minecraft.server.v1_13_R1.Item item = CraftMagicNumbers.getItem(itemStack.getType());
            if (item == null) {
                net.minecraft.server.v1_13_R1.Block block = CraftMagicNumbers.getBlock(itemStack.getType());
                if (block == null) {
                    this.text(itemStack.getType().name().toLowerCase());
                    this.translate(false);
                } else {
                    try {
                        this.text(net.minecraft.server.v1_13_R1.LocaleLanguage.class.newInstance()
                                .a(block.a() + ".name"));
                    } catch (InstantiationException | IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.translate(true);
                }
            } else {
                try {
                    this.text(net.minecraft.server.v1_13_R1.LocaleLanguage.class.newInstance()
                            .a(item.getName() + ".name"));
                } catch (InstantiationException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                this.translate(true);
            }
        } else {
            this.text(itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : nmsStack.getItem().h(nmsStack) + ".name");
            if (!itemMeta.hasDisplayName())
                this.translate(true);
        }

        return this;
    }

    /**
     * Appends the given text to the already set text
     * 
     * @param text
     *            The Text to append
     * @return this ChatComponent Object
     */
    public ChatComponent append(String text) {
        this.text = this.text + text;
        return this;
    }

    /**
     * Appends the given Number to the already set text
     * 
     * @param text
     *            The Text to append
     * @return this ChatComponent Object
     */
    public ChatComponent append(Number number) {
        this.text = this.text + number.toString();
        return this;
    }

    /**
     * Forces an newline at the current position in this ChatComponents text
     * 
     * @return this ChatComponent Object
     */
    public ChatComponent newline() {
        this.text = this.text + "\n";
        return this;
    }

    /**
     * Declares this Component as an Translatable String<br />
     * The Client translates this Component to the Client locale
     * 
     * @param mustTranslate
     * @return This ChatComponent Object
     */
    public ChatComponent translate(boolean mustTranslate) {
        this.mustTranslate = mustTranslate;
        return this;
    }

    /**
     * Enables an hover Action for this ChatComponent when hovering the text with
     * the mouse<br />
     * The Action is defined in {@link Hover}
     * 
     * @param hoverComponent
     *            The Action that will be executed when hovering the text
     * @param value
     *            The Text as an ChatComponent that will be shown or used to render
     *            the Action
     * @return this ChatComponent Object
     */
    public ChatComponent hover(Hover hoverComponent, ChatComponent value) {
        this.hoverComponent = hoverComponent;
        this.hoverValue = value;

        return this;
    }

    /**
     * Displays an Text when hovering the ChatComponent with the mouse
     * 
     * @param text
     *            The Text as an String that will be shown or used to render the
     *            Action
     * @return this ChatComponent Object
     */
    public ChatComponent hover(String text) {
        this.hoverComponent = Hover.ShowText;
        this.hoverValue = new ChatComponent(text);

        return this;
    }

    /**
     * Enables an ShowText Hover Action for this ChatComponent when hovering the
     * text with the mouse<br />
     * The ShowItem Action shows different informations about the given ItemStack
     * 
     * @param itemStack
     *            The ItemStack
     * @return this ChatComponent Object
     */
    public ChatComponent hover(ItemStack itemStack) {
        net.minecraft.server.v1_13_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        if (nmsStack == null) {
            net.minecraft.server.v1_13_R1.Item item = CraftMagicNumbers.getItem(itemStack.getType());
            if (item == null) {
                net.minecraft.server.v1_13_R1.Block block = CraftMagicNumbers.getBlock(itemStack.getType());
                if (block == null) {
                    System.out.println("No NMS Stack Item for " + (itemStack.toString()));
                } else {
                    this.hoverComponent = Hover.ShowItem;
                    this.hoverCompound = new net.minecraft.server.v1_13_R1.ItemStack(block)
                            .save(new net.minecraft.server.v1_13_R1.NBTTagCompound());
                }
            } else {
                this.hoverComponent = Hover.ShowItem;
                this.hoverCompound = new net.minecraft.server.v1_13_R1.ItemStack(item)
                        .save(new net.minecraft.server.v1_13_R1.NBTTagCompound());
            }
        } else {
            this.hoverComponent = Hover.ShowItem;
            this.hoverCompound = nmsStack.save(new net.minecraft.server.v1_13_R1.NBTTagCompound());
        }

        return this;
    }

    /**
     * Enables an click Action for this ChatComponent when clicking the text with
     * the mouse<br />
     * The Action is defined in {@link Click}
     * 
     * @param clickComponent
     *            The Action that will be executed when clicking on the text
     * @param value
     *            The Text that will be used when clicking on the Text (ig. run
     *            Command)
     * @return this ChatComponent Object
     */
    public ChatComponent click(Click clickComponent, String value) {
        this.clickComponent = clickComponent;
        this.clickValue = value;

        return this;
    }

    /**
     * Sets the Style of this ChatComponent Text<br />
     * This can be an Color or other Style Codes defined in {@link ChatColor} except
     * RESET
     * 
     * @param chatFormat
     *            The Style Code to set
     * @return this ChatComponent Object
     */
    public ChatComponent style(ChatColor chatColor) {
        this.formatList.add(chatColor);
        return this;
    }

    /**
     * Sets multiple Styles to this ChatComponents Text<br />
     * This can be an Color or other Style Codes defined in {@link ChatColor} except
     * RESET<br />
     * Its useless to set more than one Color but its possible to use more
     * formatting codes
     * 
     * @param chatColor
     *            The Style Codes to set
     * @return this ChatComponent Object
     */
    public ChatComponent style(ChatColor... chatColors) {
        for (ChatColor chatColor : chatColors) {
            this.formatList.add(chatColor);
        }

        return this;
    }

    public List<ChatColor> styles() {
        return ImmutableList.<ChatColor>copyOf(this.formatList);
    }

    /**
     * Clears all Formattings of this Component
     * 
     * @return this ChatComponent Object
     */
    public ChatComponent clearStyles() {
        this.formatList.clear();
        return this;
    }

    /**
     * Registers an new empty ChatComponent Object after this Object<br />
     * The new Object has its own Text and Style. It can also contain own Hover and
     * Click Actions
     * 
     * @return the new ChatComponent Object
     */
    public ChatComponent then() {
        return then("");
    }

    /**
     * Registers an new ChatComponent Object with the given Text after this
     * Object<br />
     * The new Object has its own Text and Style. It can also contain own Hover and
     * Click Actions
     * 
     * @param text
     *            The Text for the new ChatComponent
     * @return the new ChatComponent Object
     */
    public ChatComponent then(String text) {
        return then(text, new ChatColor[] {});
    }

    /**
     * Registers an new ChatComponent Object with the given Text after this
     * Object<br />
     * The new Object has its own Text and Style. It can also contain own Hover and
     * Click Actions
     * 
     * @param text
     *            The Text for the new ChatComponent
     * @return the new ChatComponent Object
     */
    public ChatComponent then(Number number) {
        return then(number, new ChatColor[] {});
    }

    /**
     * Registers an new ChatComponent Object with the given Text and Style Codes
     * after this Object<br />
     * The new Object has its own Text and Style. It can also contain own Hover and
     * Click Actions
     * 
     * @param text
     * @param chatFormats
     * @return the new ChatComponent Object
     */
    public ChatComponent then(Number number, ChatColor... chatColors) {
        return then(number.toString(), chatColors);
    }

    /**
     * Registers an new ChatComponent Object with the given Text and Style Codes
     * after this Object<br />
     * The new Object has its own Text and Style. It can also contain own Hover and
     * Click Actions
     * 
     * @param text
     * @param chatFormats
     * @return the new ChatComponent Object
     */
    public ChatComponent then(String text, ChatColor... chatColors) {
        ChatComponent chatComponent = new ChatComponent(text);

        this.after = chatComponent;
        chatComponent.before = this;

        if (!chatComponent.formatList.contains(ChatColor.RESET)) {
            chatComponent.formatList.add(ChatColor.RESET);
        }

        for (ChatColor chatColor : chatColors) {
            chatComponent.formatList.add(chatColor);
        }

        return chatComponent;
    }

    public ChatComponent then(ChatComponent chatComponent) {
        this.after = chatComponent;
        chatComponent.before = this;

        if (!chatComponent.formatList.contains(ChatColor.RESET)) {
            chatComponent.formatList.add(ChatColor.RESET);
        }

        return chatComponent;
    }

    /**
     * Adds an new ChatComponent with an hover Effect for the ItemStack data
     * 
     * @param itemStack
     * @return the new ChatComponent object
     */
    public ChatComponent then(ItemStack itemStack) {
        return then(itemStack, new ChatColor[] {});
    }

    /**
     * Adds an new ChatComponent with an hover Effect for the ItemStack data and the
     * given Color Codes
     * 
     * @param itemStack
     * @return the new ChatComponent object
     */
    public ChatComponent then(ItemStack itemStack, ChatColor... chatColors) {
        ChatComponent chatComponent = new ChatComponent();

        chatComponent.text(itemStack);
        chatComponent.hover(itemStack);

        this.after = chatComponent;
        chatComponent.before = this;

        if (!chatComponent.formatList.contains(ChatColor.RESET)) {
            chatComponent.formatList.add(ChatColor.RESET);
        }

        for (ChatColor chatColor : chatColors) {
            chatComponent.formatList.add(chatColor);
        }

        return chatComponent;
    }

    /**
     * Registers an new ChatComponent Object after this Object<br />
     * The new Object uses the style and hover/clicks of the old Component.
     * 
     * @return the new ChatComponent Object
     */
    public ChatComponent attach() {
        return attach("");
    }

    /**
     * Registers an new ChatComponent Object with the given Text after this
     * Object<br />
     * The new Object uses the style and hover/clicks of the old Component.
     * 
     * @param text
     *            The Text for the new ChatComponent
     * @return the new ChatComponent Object
     */
    public ChatComponent attach(String text) {
        return attach(text, new ChatColor[] {});
    }

    /**
     * Registers an new ChatComponent Object with the given Text after this
     * Object<br />
     * The new Object uses the style and hover/clicks of the old Component.
     * 
     * @param text
     *            The Text for the new ChatComponent
     * @return the new ChatComponent Object
     */
    public ChatComponent attach(Number number) {
        return attach(number, new ChatColor[] {});
    }

    /**
     * Registers an new ChatComponent Object with the given Text and Style Codes
     * after this Object<br />
     * The new Object uses the style and hover/clicks of the old Component.
     * 
     * @param text
     * @param chatFormats
     * @return the new ChatComponent Object
     */
    public ChatComponent attach(Number number, ChatColor... chatColors) {
        return attach(number.toString(), chatColors);
    }

    /**
     * Registers an new ChatComponent Object with the given Text and Style Codes
     * after this Object<br />
     * The new Object uses the style and hover/clicks of the old Component.
     * 
     * @param text
     * @param chatFormats
     * @return the new ChatComponent Object
     */
    public ChatComponent attach(String text, ChatColor... chatColors) {
        ChatComponent chatComponent = new ChatComponent(text);

        this.after = chatComponent;
        chatComponent.before = this;

        for (ChatColor chatColor : this.formatList) {
            chatComponent.formatList.add(chatColor);
        }

        for (ChatColor chatColor : chatColors) {
            chatComponent.formatList.add(chatColor);
        }

        chatComponent.hoverComponent = this.hoverComponent;
        chatComponent.hoverValue = this.hoverValue;
        chatComponent.hoverCompound = this.hoverCompound;

        chatComponent.clickComponent = this.clickComponent;
        chatComponent.clickValue = this.clickValue;

        return chatComponent;
    }

    public ChatComponent attach(ItemStack itemStack) {
        return attach(itemStack, new ChatColor[] {});
    }

    /**
     * Adds an new ChatComponent Object after this with the ItemStack text and
     * Hover.<br />
     * The Click Component and Style Codes are merged with the old codes
     * 
     * @param itemStack
     * @param chatColors
     * @return the new ChatComponent Object
     */
    public ChatComponent attach(ItemStack itemStack, ChatColor... chatColors) {
        ChatComponent chatComponent = new ChatComponent();

        chatComponent.text(itemStack);
        chatComponent.hover(itemStack);

        this.after = chatComponent;
        chatComponent.before = this;

        for (ChatColor chatColor : this.formatList) {
            chatComponent.formatList.add(chatColor);
        }

        for (ChatColor chatColor : chatColors) {
            chatComponent.formatList.add(chatColor);
        }

        chatComponent.clickComponent = this.clickComponent;
        chatComponent.clickValue = this.clickValue;

        return chatComponent;
    }

    /**
     * Loops threw all existing ChatComponents referenced with this ChatComponent
     * and returns the last added ChatComponent
     * 
     * @return the last added ChatComponent Object
     */
    public ChatComponent last() {
        ChatComponent last = this.after;

        while (last != null && last.after != null) {
            last = last.after;
        }

        return last != null ? last : this;
    }

    /**
     * Loops threw all existing ChatComponents referenced with this ChatComponent
     * and return the first created ChatComponent
     * 
     * @return the first created ChatComponent Object
     */
    public ChatComponent first() {
        ChatComponent first = this.before;

        while (first != null && first.before != null) {
            first = first.before;
        }

        return first != null ? first : this;
    }

    public ChatComponent next() {
        return this.after;
    }

    public ChatComponent before() {
        return this.before;
    }

    /**
     * Deletes this chatcomponent and all ChatComponents added after this
     * component<br />
     * If this is the first component this method returns null
     * 
     * @return the chatcomponent before this component or null
     */
    public ChatComponent delete() {
        ChatComponent chatComponent = this.before;

        if (chatComponent != null) {
            chatComponent.after = null;
            return chatComponent;
        }

        return null;
    }

    /**
     * Sends the ChatComponent (automatically uses the first component) to the given
     * {@link CommandSender} object.<br />
     * When the Receiver is an Player the ChatComponent itself will be send
     * including all extras.<br />
     * When the Receiver is an console or other objects that don't support
     * ChatComponents the {@link ChatComponent#plain()} Text will be send.
     * 
     * @param player
     *            The Sender that receives the message
     */
    public void to(CommandSender commandSender) {
        ChatComponent first = first();
        if (first == null)
            first = this;

        if (commandSender instanceof Player) {
            ((CraftPlayer) commandSender).getHandle().a(first.server(), true);
        } else {
            commandSender.sendMessage(first.plain());
        }
    }

    /**
     * Converts an JSON Message to an IChatBaseComponent and sends this to an Player
     * or the Console
     * 
     * @param commandSender
     * @param json
     */
    public static void sendParsed(CommandSender commandSender, String json) {
        ChatComponent.sendParsed(commandSender, json, null);
    }

    /**
     * Converts an JSON Message to an IChatBaseComponent and sends this to an Player
     * or the Console<br />
     * Allows to extend the JSON Message with an ChatComponent
     * 
     * @param commandSender
     * @param json
     * @param extended
     */
    public static void sendParsed(CommandSender commandSender, String json, ChatComponent extended) {
        IChatBaseComponent iChatBaseComponent = net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a(json);
        if (extended != null)
            iChatBaseComponent.addSibling(extended.server());

        if (commandSender instanceof Player) {
            ((CraftPlayer) commandSender).getHandle().a(iChatBaseComponent, ChatMessageType.CHAT); // joestr: Fix 
        } else {
            commandSender.sendMessage(iChatBaseComponent.getText());
        }
    }

    /**
     * Converts the ChatComponents to normal Chat Messages include Chat Style Codes
     * but no click and hover Objects<br />
     * This can be used to send this to the console or other objects that don't
     * support ChatComponents
     * 
     * @return
     */
    public String plain() {
        StringBuilder stringBuilder = new StringBuilder();

        ChatComponent chatComponent = first();

        for (ChatColor chatColor : this.formatList)
            stringBuilder.append(chatColor);
        stringBuilder.append(this.text);

        while (chatComponent.after != null) {
            chatComponent = chatComponent.after;

            for (ChatColor chatColor : chatComponent.formatList)
                stringBuilder.append(chatColor);
            stringBuilder.append(chatComponent.text);
        }

        return stringBuilder.toString();
    }

    /**
     * Converts this ChatComponent to an json String<br />
     * Automatically uses the first Component.
     */
    public String json() {
        net.minecraft.server.v1_13_R1.IChatBaseComponent chatComponent = this.server();
        return net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a(chatComponent);
    }

    /**
     * Converts this {@link ChatComponent} Object to an Minecraft
     * {@link net.minecraft.server.v1_8_R3.ChatBaseComponent} Object<br />
     * 
     * @return An new Minecraft Source ChatComponent
     */
    public net.minecraft.server.v1_13_R1.IChatBaseComponent server() {
        net.minecraft.server.v1_13_R1.ChatBaseComponent mainComponent = new net.minecraft.server.v1_13_R1.ChatComponentText(
                "");
        net.minecraft.server.v1_13_R1.IChatBaseComponent workingComponent = null;

        net.minecraft.server.v1_13_R1.ChatBaseComponent clearComponent = new net.minecraft.server.v1_13_R1.ChatComponentText(
                "");
        clearComponent.setChatModifier(new net.minecraft.server.v1_13_R1.ChatModifier().setChatModifier(null));

        // Starts with the first component
        ChatComponent chatComponent = first();
        workingComponent = mainComponent.addSibling(serverComponent(chatComponent));

        while (chatComponent.after != null) {
            chatComponent = chatComponent.after;
            workingComponent = workingComponent.addSibling(serverComponent(chatComponent));
        }

        return mainComponent;
    }

    private net.minecraft.server.v1_13_R1.IChatBaseComponent serverComponent(ChatComponent chatComponent) {
        net.minecraft.server.v1_13_R1.IChatBaseComponent serverComponent = null;

        if (chatComponent.mustTranslate) {
            serverComponent = new net.minecraft.server.v1_13_R1.ChatMessage(chatComponent.text); // Translateable
                                                                                                 // Message
        } else {
            serverComponent = new net.minecraft.server.v1_13_R1.ChatComponentText(chatComponent.text); // Normal message
        }

        net.minecraft.server.v1_13_R1.ChatModifier chatModifier = new net.minecraft.server.v1_13_R1.ChatModifier();

        if (chatComponent.clickComponent != null) {
            chatModifier.setChatClickable(new net.minecraft.server.v1_13_R1.ChatClickable(
                    chatComponent.clickComponent.getNative(), chatComponent.clickValue));
        }

        if (chatComponent.hoverComponent != null) {
            if (chatComponent.hoverComponent == Hover.ShowItem) {
                chatModifier.setChatHoverable(new net.minecraft.server.v1_13_R1.ChatHoverable(
                        chatComponent.hoverComponent.getNative(),
                        new net.minecraft.server.v1_13_R1.ChatComponentText(chatComponent.hoverCompound.toString())));
            } else {
                chatModifier.setChatHoverable(new net.minecraft.server.v1_13_R1.ChatHoverable(
                        chatComponent.hoverComponent.getNative(), chatComponent.hoverValue.server()));
            }
        }

        if (!chatComponent.formatList.isEmpty()) {
            for (ChatColor chatColor : chatComponent.formatList) {
                if (chatColor.isColor()) {
                    if (ChatColor.RESET == chatColor)
                        continue;
                    chatModifier.setColor(net.minecraft.server.v1_13_R1.EnumChatFormat.c(chatColor.name()));
                } else {
                    switch (chatColor) {
                    case BOLD:
                        chatModifier.setBold(true);
                        break;
                    case ITALIC:
                        chatModifier.setItalic(true);
                        break;
                    case MAGIC:
                        chatModifier.setRandom(true);
                        break;
                    case STRIKETHROUGH:
                        chatModifier.setStrikethrough(true);
                        break;
                    case UNDERLINE:
                        chatModifier.setUnderline(true);
                        break;
                    default:
                        break;
                    }
                }
            }
        }

        serverComponent.setChatModifier(chatModifier);
        return serverComponent;
    }

    /**
     * Converts this {@link ChatComponent} Object to an Spigot
     * {@link net.md_5.bungee.api.chat.BaseComponent} Object<br />
     * 
     * @return An new Spigot BaseComponent array with all Objects
     */
    public BaseComponent[] spigot() {
        List<BaseComponent> componentList = new ArrayList<BaseComponent>();

        ChatComponent chatComponent = first();
        while (chatComponent.after != null) {
            chatComponent = chatComponent.after;
            componentList.add(this.spigotComponent(chatComponent));
        }

        return componentList.toArray(new BaseComponent[0]);
    }

    private BaseComponent spigotComponent(ChatComponent chatComponent) {
        BaseComponent baseComponent;

        if (chatComponent.mustTranslate) {
            baseComponent = new TranslatableComponent(chatComponent.text);
        } else {
            baseComponent = new TextComponent(chatComponent.text);
        }

        if (chatComponent.clickComponent != null) {
            baseComponent
                    .setClickEvent(new ClickEvent(chatComponent.clickComponent.getSpigot(), chatComponent.clickValue));
        }

        if (chatComponent.hoverComponent != null) {
            if (chatComponent.hoverComponent == Hover.ShowItem) {
                baseComponent.setHoverEvent(new HoverEvent(chatComponent.hoverComponent.getSpigot(),
                        TextComponent.fromLegacyText(chatComponent.hoverCompound.toString())));
            } else {
                baseComponent.setHoverEvent(
                        new HoverEvent(chatComponent.hoverComponent.getSpigot(), chatComponent.hoverValue.spigot()));
            }
        }

        if (!chatComponent.formatList.isEmpty()) {
            for (ChatColor chatColor : chatComponent.formatList) {
                if (chatColor.isColor()) {
                    if (ChatColor.RESET == chatColor)
                        continue;
                    baseComponent.setColor(net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar()));
                } else {
                    switch (chatColor) {
                    case BOLD:
                        baseComponent.setBold(true);
                        break;
                    case ITALIC:
                        baseComponent.setItalic(true);
                        break;
                    case MAGIC:
                        baseComponent.setObfuscated(true);
                        break;
                    case STRIKETHROUGH:
                        baseComponent.setStrikethrough(true);
                        break;
                    case UNDERLINE:
                        baseComponent.setUnderlined(true);
                        break;
                    default:
                        break;
                    }
                }
            }
        }

        return baseComponent;
    }

    public enum Click {
        OpenFile("open_file", ClickEvent.Action.OPEN_FILE), OpenUrl("open_url", ClickEvent.Action.OPEN_URL), RunCommand(
                "run_command",
                ClickEvent.Action.RUN_COMMAND), SuggestCommand("suggest_command", ClickEvent.Action.SUGGEST_COMMAND),
        /**
         * Currently not supported.
         */
        TwitchUserInfo("twitch_user_info", null),
        /**
         * Currently not supported.
         */
        ChangePage("change_page", null);

        private final String code;
        private final ClickEvent.Action action;

        private Click(String code, ClickEvent.Action action) {
            this.code = code;
            this.action = action;
        }

        public String getCode() {
            return this.code;
        }

        public ClickEvent.Action getSpigot() {
            return this.action;
        }

        public net.minecraft.server.v1_13_R1.ChatClickable.EnumClickAction getNative() {
            return net.minecraft.server.v1_13_R1.ChatClickable.EnumClickAction.a(this.code);
        }
    }

    public enum Hover {
        ShowText("show_text", HoverEvent.Action.SHOW_TEXT), ShowAchievement("show_achievement",
                HoverEvent.Action.SHOW_ACHIEVEMENT), ShowItem("show_item", HoverEvent.Action.SHOW_ITEM),
        /**
         * Currently not supported.
         */
        ShowEntity("show_entity", null);

        private final String code;
        private final HoverEvent.Action action;

        private Hover(String code, HoverEvent.Action action) {
            this.code = code;
            this.action = action;
        }

        public String getCode() {
            return this.code;
        }

        public HoverEvent.Action getSpigot() {
            return this.action;
        }

        public net.minecraft.server.v1_13_R1.ChatHoverable.EnumHoverAction getNative() {
            return net.minecraft.server.v1_13_R1.ChatHoverable.EnumHoverAction.a(this.code);
        }
    }
}