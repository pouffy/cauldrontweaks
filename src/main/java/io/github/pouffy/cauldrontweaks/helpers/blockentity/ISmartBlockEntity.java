package io.github.pouffy.cauldrontweaks.helpers.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ISmartBlockEntity<T extends SmartBlockEntity> extends EntityBlock {

    Class<T> getBlockEntityClass();

    BlockEntityType<? extends T> getBlockEntityType();

    default void withBlockEntityDo(BlockGetter getter, BlockPos pos, Consumer<T> action) {
        getBlockEntityOptional(getter, pos).ifPresent(action);
    }

    default InteractionResult onBlockEntityUse(BlockGetter getter, BlockPos pos, Function<T, InteractionResult> action) {
        return getBlockEntityOptional(getter, pos).map(action).orElse(InteractionResult.PASS);
    }

    default ItemInteractionResult onBlockEntityUseItemOn(BlockGetter getter, BlockPos pos, Function<T, ItemInteractionResult> action) {
        return getBlockEntityOptional(getter, pos).map(action).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    default Optional<T> getBlockEntityOptional(BlockGetter getter, BlockPos pos) {
        return Optional.ofNullable(getBlockEntity(getter, pos));
    }

    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }

    @Override
    default <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
        if (SmartBlockEntity.class.isAssignableFrom(getBlockEntityClass())) return new SmartBlockEntityTicker<>();
        return null;
    }

    default T getBlockEntity(BlockGetter worldIn, BlockPos pos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        Class<T> expectedClass = getBlockEntityClass();

        if (blockEntity == null) return null;
        if (!expectedClass.isInstance(blockEntity)) return null;

        return (T) blockEntity;
    }
}
