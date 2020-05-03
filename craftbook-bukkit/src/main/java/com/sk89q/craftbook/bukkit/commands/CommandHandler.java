package com.sk89q.craftbook.bukkit.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldedit.command.argument.BooleanConverter;
import com.sk89q.worldedit.command.argument.VectorConverter;
import com.sk89q.worldedit.command.argument.WorldConverter;
import com.sk89q.worldedit.command.util.SubCommandPermissionCondition;
import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.logging.DynamicStreamHandler;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.gen.CommandRegistration;
import org.enginehub.piston.impl.CommandManagerServiceImpl;
import org.enginehub.piston.inject.InjectedValueStore;
import org.enginehub.piston.inject.MapBackedValueStore;
import org.enginehub.piston.part.SubCommandPart;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandHandler {

    private final CraftBookPlugin plugin;

    private final CommandManagerServiceImpl commandManagerService;
    private final CommandManager commandManager;
    private final InjectedValueStore globalInjectedValues;
    private final DynamicStreamHandler dynamicHandler = new DynamicStreamHandler();
//    private final WorldEditExceptionConverter exceptionConverter;
    private final CommandRegistrationHandler registration;

    CommandHandler(final CraftBookPlugin plugin) {
        checkNotNull(plugin);
        this.plugin = plugin;
//        this.exceptionConverter = new WorldEditExceptionConverter(worldEdit);
        this.commandManagerService = new CommandManagerServiceImpl();
        this.commandManager = commandManagerService.newCommandManager();
        this.globalInjectedValues = MapBackedValueStore.create();
        this.registration = new CommandRegistrationHandler(
                ImmutableList.of());
        // setup separate from main constructor
        // ensures that everything is definitely assigned
        initialize();
    }

    private void initialize() {
        // Set up the commands manager
        registerAlwaysInjectedValues();
        registerArgumentConverters();
        registerAllCommands();
    }

    private void registerArgumentConverters() {
        VectorConverter.register(commandManager);
        BooleanConverter.register(commandManager);
        WorldConverter.register(commandManager);
    }

    private void registerAlwaysInjectedValues() {

    }

    private <CI> void registerSubCommands(String name, List<String> aliases, String desc,
            CommandRegistration<CI> registration, CI instance) {
        registerSubCommands(name, aliases, desc, registration, instance, m -> {});
    }

    private <CI> void registerSubCommands(String name, List<String> aliases, String desc,
            CommandRegistration<CI> registration, CI instance,
            Consumer<CommandManager> additionalConfig) {
        commandManager.register(name, cmd -> {
            cmd.aliases(aliases);
            cmd.description(TextComponent.of(desc));
            cmd.action(Command.Action.NULL_ACTION);

            CommandManager manager = commandManagerService.newCommandManager();
            this.registration.register(
                    manager,
                    registration,
                    instance
            );
            additionalConfig.accept(manager);

            final List<Command> subCommands = manager.getAllCommands().collect(Collectors.toList());
            cmd.addPart(SubCommandPart.builder(TranslatableComponent.of("craftbook.argument.action"),
                    TextComponent.of("Sub-command to run."))
                    .withCommands(subCommands)
                    .required()
                    .build());

            cmd.condition(new SubCommandPermissionCondition.Generator(subCommands).build());
        });
    }

    private void registerAllCommands() {
        registerSubCommands(
                "craftbook",
                ImmutableList.of("cb"),
                "CraftBook commands",
                CraftBookCommandsRegistration.builder(),
                new CraftBookCommands(plugin)
        );
    }
}
