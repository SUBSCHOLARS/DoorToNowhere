package com.example.examplemod.doortonowhere;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class DoorToNowhere extends DoorBlock {
    public DoorToNowhere(){
        super(BlockBehaviour.Properties.of(Material.WOOD)
            .strength(3.0f)
            .noOcclusion());
    }
}
