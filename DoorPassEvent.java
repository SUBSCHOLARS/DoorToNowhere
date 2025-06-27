package com.example.examplemod.doortonowhere;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;

@Mod.EventBusSubscriber
public class DoorPassEvent {
    private static final HashMap<BlockPos, BlockPos> cachedTeleportPositions = new HashMap<>();
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level;
        BlockPos playerPos = player.blockPosition();

        if (level.getBlockState(playerPos).getBlock() instanceof DoorToNowhere) {
            BlockPos teleportPos = cachedTeleportPositions.computeIfAbsent(playerPos, pos -> calculateSafePosition(level, 250));
            player.teleportTo(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());

            level.playSound(null, teleportPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!level.isClientSide) {
                player.sendMessage(new TextComponent("あなたは何処へだって行ける"), player.getUUID());
            }
        }
    }

    private static BlockPos calculateSafePosition(Level level, int range) {
        BlockPos randomPos;
        do {
            randomPos = getRandomPosition(level, range);
        } while (!isSafePosition(level, randomPos));//getRandomPositionの返り値がisSafePositionをtrueにするまで回す
        return randomPos;
    }

    private static BlockPos getRandomPosition(Level level, int range) {
        int x = random.nextInt(range * 2) - range;
        int z = random.nextInt(range * 2) - range;
        int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY();
        return new BlockPos(x, y, z);
    }

    private static boolean isSafePosition(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() &&//プレイヤーがブロックに埋もれない
                level.getBlockState(pos.above()).isAir() &&//頭上が空気ブロック
                !level.getBlockState(pos.below()).isAir() &&//床が空気ブロックではない
                !level.getBlockState(pos.below()).is(Blocks.LAVA) &&//床が溶岩ブロックではない
                !level.getBlockState(pos.below()).is(Blocks.WATER);//床が水ブロックではない
    }
}

