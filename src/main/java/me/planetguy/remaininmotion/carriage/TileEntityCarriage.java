package me.planetguy.remaininmotion.carriage;

import me.planetguy.remaininmotion.motion.CarriageMotionException;
import me.planetguy.remaininmotion.motion.CarriagePackage;
import me.planetguy.remaininmotion.util.transformations.Directions;
import me.planetguy.remaininmotion.api.ConnectabilityState;
import me.planetguy.remaininmotion.api.ICloseable;
import me.planetguy.remaininmotion.api.Moveable;
import me.planetguy.remaininmotion.base.BlockRiM;
import me.planetguy.remaininmotion.base.TileEntityCamouflageable;
import me.planetguy.remaininmotion.util.transformations.ArrayRotator;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityCarriage extends TileEntityCamouflageable implements Moveable, ICloseable {

    public TileEntityCarriage() {
        super();
    }

	@Override
	public boolean canUpdate() {
		return (false);
	}

	public boolean[]	SideClosed	= new boolean[Directions.values().length];

	public void ToggleSide(int Side, boolean Sneaking) {
		if (Sneaking) {
			Side = Directions.values()[Side].opposite().ordinal();
		}

		SideClosed[Side] = !SideClosed[Side];

		Propagate();
	}

	@Override
	public ConnectabilityState isSideClosed(int side) {
		return treatSideAsClosed(side) ? ConnectabilityState.CLOSED : ConnectabilityState.OPEN;
	}

	public boolean treatSideAsClosed(int side) {
		return SideClosed[side];
	}

	@Override
	public void EmitDrops(BlockRiM Block, int Meta) {
		EmitDrop(Block, ItemCarriage.Stack(Meta, net.minecraft.block.Block.getIdFromBlock(getDecoration()),
				getDecorationMeta()));
	}

	@Override
	public void ReadCommonRecord(NBTTagCompound TagCompound) {
		for (int Index = 0; Index < SideClosed.length; Index++) {
			SideClosed[Index] = TagCompound.getBoolean("SideClosed" + Index);
		}

		Decoration = Block.getBlockById(TagCompound.getInteger("DecorationId"));

		DecorationMeta = TagCompound.getInteger("DecorationMeta");
	}

	@Override
	public void WriteCommonRecord(NBTTagCompound TagCompound) {
		for (int Index = 0; Index < SideClosed.length; Index++) {
			TagCompound.setBoolean("SideClosed" + Index, SideClosed[Index]);
		}

		TagCompound.setInteger("DecorationId", Block.getIdFromBlock(Decoration));

		TagCompound.setInteger("DecorationMeta", DecorationMeta);
	}

	@Override
	public abstract void fillPackage(CarriagePackage Package) throws CarriageMotionException;

	@Override
	public void rotateSpecial(ForgeDirection axis) {
		ArrayRotator.rotate(SideClosed, axis);
	}

}
