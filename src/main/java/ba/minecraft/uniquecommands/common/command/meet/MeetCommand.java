package ba.minecraft.uniquecommands.common.command.meet;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class MeetCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("meet")
						.executes(
							(context) -> {
								CommandSourceStack source = context.getSource();
								return meet(source);
							}
						)
					);
			
	}
	
	private static int meet(CommandSourceStack source) throws CommandSyntaxException {
		
		// Get reference to a player that has typed the command => summoner.
		ServerPlayer summoner = source.getPlayerOrException();
		
		// Get game profile for summoner.
		GameProfile summonerGameProfile = summoner.getGameProfile();
		
		// Get unique ID of summoner.
		UUID summonerId = summonerGameProfile.getId();
		
		// Get position of summoner.
		BlockPos playerPos = summoner.blockPosition();
		
		// Get world where summoner is currently at.
		ServerLevel level = summoner.serverLevel();

		// Get coordinates of summoner.
		int x = playerPos.getX();
		int y = playerPos.getY();
		int z = playerPos.getZ();
		
		// Get reference to a Minecraft server instance.
		MinecraftServer server = source.getServer();
		
		// Get list of players currently on the server.
		PlayerList playerList = server.getPlayerList();
		
		// Get array of players on the server.
		List<ServerPlayer> players = playerList.getPlayers();
		
		// IF: Player is the only one on the server.
		if(players.size() == 1) {
			
			MutableComponent message = Component.literal(
				"You are alone on the server. Maybe get some friends to join you?"
			);
				
			// Send error message.
			source.sendFailure(message);
			
			return -1;
		}
		
		int summonCount = 0;
		
		// Iterate through all players.
		for(ServerPlayer player : players) {
			
			// Get game profile of player.
			GameProfile playerGameProfile = player.getGameProfile();
			
			// Get unique ID of player.
			UUID playerId = playerGameProfile.getId();

			// IF: player is not summoner.
			if(playerId != summonerId) {
				// Get current rotation of player.
				float yaw = player.getYRot();
				float pitch = player.getXRot();
				
				// Teleport player to summoner location.
				player.teleportTo(level, x, y, z, yaw, pitch);
				
				summonCount++;
			}
			
		}
		
		// Create message to be displayed in console.		
		MutableComponent message = Component.literal(
			summonCount + " players has been summoned to your location."
		);
		
		// Send message to console.
		source.sendSuccess(() -> message, true);
		
		return 1;
	}
}
