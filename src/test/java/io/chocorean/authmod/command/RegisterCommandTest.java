package io.chocorean.authmod.command;

import com.mojang.authlib.GameProfile;
import io.chocorean.authmod.core.DataSourceGuard;
import io.chocorean.authmod.core.GuardInterface;
import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.PlayerInterface;
import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.FileDataSourceStrategy;
import io.chocorean.authmod.event.Handler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterCommandTest {
  private RegisterCommand command;
  private Handler handler;
  private DataSourceStrategyInterface dataSource;
  private GuardInterface guard;
  private EntityPlayerMP sender;


  @BeforeEach
  void init() throws IOException {
    File file = Paths.get(System.getProperty("java.io.tmpdir"), "authmod.csv").toFile();
    Files.deleteIfExists(file.toPath());
    this.handler = new Handler();
    PlayerInterface player = new Player("Batman", "7128022b-9195-490d-9bc8-9b42ebe2a8e3");
    this.sender = mock(EntityPlayerMP.class);

    when(this.sender.getGameProfile()).thenReturn(new GameProfile(UUID.fromString(player.getUuid()), player.getUsername()));
    when(this.sender.getDisplayNameString()).thenReturn(player.getUsername());
    sender.connection = mock(NetHandlerPlayServer.class);
    this.dataSource = new FileDataSourceStrategy(file);
    this.guard = new DataSourceGuard(this.dataSource);
    this.command = new RegisterCommand(this.handler, this.guard);
  }

  @Test
  void testConstructor() {
    assertNotNull(this.command);
  }

  @Test
  void testExecute() {
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {"adminroot", "adminroot"});
    assertTrue(this.handler.isLogged(this.sender));
  }

  @Test
  void testExecuteWrongPassword() {
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {"wrongpass", "wrongpassword"});
    assertFalse(this.handler.isLogged(this.sender));
  }
  @Test
  void testExecuteWrongNumberOfArgs() {
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {});
    assertFalse(this.handler.isLogged(this.sender));
  }

  @Test
  void testExecuteIdentifierRequired() {
    this.guard = new DataSourceGuard(this.dataSource, true);
    this.command = new RegisterCommand(this.handler, this.guard);
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {"elliotalderson@protonmail.ch", "MrRobot", "MrRobot"});
    assertTrue(this.handler.isLogged(this.sender));
  }

  @Test
  void testExecuteIdentifierMissing() {
    this.guard = new DataSourceGuard(this.dataSource, true);
    this.command = new RegisterCommand(this.handler, this.guard);
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {"MrRobot", "MrRobot"});
    assertFalse(this.handler.isLogged(this.sender));
  }

  @Test
  void testExecuteAlreadyLogged() {
    handler.authorizePlayer(sender);
    assertTrue(this.handler.isLogged(this.sender));
    this.command.execute(mock(MinecraftServer.class), sender, new String[] {});
    assertTrue(this.handler.isLogged(this.sender));
  }

  @Test
  void testGetName() {
    assertNotNull(this.command.getName());
  }

  @Test
  void testGetUsage() {
    assertNotNull(this.command.getUsage(mock(ICommandSender.class)));
  }

  @Test
  void testGetAliases() {
    assertNotNull(this.command.getAliases());
  }

  @Test
  void testCheckPermissions() {
    assertTrue(this.command.checkPermission(mock(MinecraftServer.class), mock(ICommandSender.class)));
  }

  @Test
  void testGetTabCompletions() {
    assertNotNull(this.command.getTabCompletions(mock(MinecraftServer.class), mock(ICommandSender.class), new String[] {}, mock(BlockPos.class)));
  }

  @Test
  void testisUsernameIndex() {
    assertTrue(this.command.isUsernameIndex(new String[] {}, 0));
  }

  @Test
  void testCompareTo() {
    assertEquals(0, this.command.compareTo(new RegisterCommand(null, null)));
  }

}
