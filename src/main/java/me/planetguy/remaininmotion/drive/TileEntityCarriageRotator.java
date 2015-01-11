package me.planetguy.remaininmotion.drive;

import scala.actors.threadpool.Arrays;
import me.planetguy.lib.util.Debug;
import me.planetguy.lib.util.Lang;
import me.planetguy.remaininmotion.BlockRecord;
import me.planetguy.remaininmotion.CarriageMotionException;
import me.planetguy.remaininmotion.CarriagePackage;
import me.planetguy.remaininmotion.Directions;
import me.planetguy.remaininmotion.Registry;
import me.planetguy.remaininmotion.CarriageMotionException.ErrorStates;
import me.planetguy.remaininmotion.api.ISpecialMoveBehavior;
import me.planetguy.remaininmotion.core.RiMConfiguration.DirtyHacks;
import me.planetguy.remaininmotion.core.ModRiM;
import me.planetguy.remaininmotion.core.RIMBlocks;
import me.planetguy.remaininmotion.spectre.TileEntityRotativeSpectre;
import me.planetguy.remaininmotion.spectre.BlockSpectre;
import me.planetguy.remaininmotion.util.MultiTypeCarriageUtil;
import me.planetguy.remaininmotion.util.SneakyWorldUtil;
import me.planetguy.remaininmotion.util.WorldUtil;
import me.planetguy.remaininmotion.util.transformations.Rotator;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCarriageRotator extends TileEntityCarriageDrive implements ISpecialMoveBehavior{

	public boolean	alreadyMoving;
	
	private int	axisOfRotationIndex;

	@Override
	public CarriagePackage GeneratePackage(TileEntity carriage, Directions CarriageDirection, Directions MotionDirection)
			throws CarriageMotionException {
		if (!DirtyHacks.allowRotation) { throw new CarriageMotionException(ErrorStates.ROTATION_BANNED); }

		CarriagePackage Package = new CarriagePackage(this, carriage, Directions.Null);

		Package.axis = axisOfRotationIndex;

		Package.blacklistByRotation = true;

		Package.AddBlock(Package.DriveRecord);

		MultiTypeCarriageUtil.fillPackage(Package, carriage);

		for (BlockRecord record : Package.Body) {
			if (axisOfRotationIndex == 0 || axisOfRotationIndex == 1) {
				// TODO collide
			}
		}

		Package.Finalize();

		removeUsedEnergy(Package);

		return (Package);
	}

	@Override
	public boolean Anchored() {
		return false;
	}

	// don't establish placeholders yet - it's very hard to predict where things
	// will go
	@Override
	public void EstablishPlaceholders(CarriagePackage pkg) {
		for (BlockRecord Record : pkg.Body) {
			{
				SneakyWorldUtil.SetBlock(worldObj, Record.X, Record.Y, Record.Z, RIMBlocks.air, 0);
			}
		}
	}

	@Override
	public void EstablishSpectre(CarriagePackage Package) {
		int CarriageX = Package.AnchorRecord.X;
		int CarriageY = Package.AnchorRecord.Y;
		int CarriageZ = Package.AnchorRecord.Z;

		WorldUtil.SetBlock(worldObj, CarriageX, CarriageY, CarriageZ, RIMBlocks.Spectre,
				BlockSpectre.Types.Rotative.ordinal());

		TileEntityRotativeSpectre theEntity = new TileEntityRotativeSpectre();

		theEntity.setAxis(axisOfRotationIndex);

		worldObj.setTileEntity(CarriageX, CarriageY, CarriageZ, theEntity);

		theEntity.Absorb(Package);
	}

	@Override
	public void HandleToolUsage(int side, boolean sneaking) {
		if (sneaking) {
			super.HandleToolUsage(side, true);
		} else {
			axisOfRotationIndex = (axisOfRotationIndex + 1) % 6;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	@Override
	public void WriteCommonRecord(NBTTagCompound tag) {
		tag.setByte("axis", (byte) axisOfRotationIndex);
	}

	@Override
	public void ReadCommonRecord(NBTTagCompound tag) {
		axisOfRotationIndex = tag.getByte("axis");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		try {
			if (drawSideClosed(side)) {
				return BlockCarriageDrive.InactiveIcon;
			} else {
				return icons[axisOfRotationIndex][side];
			}
		} catch (ArrayIndexOutOfBoundsException e) { // testing only
			return Blocks.activator_rail.getIcon(0, 0);
		}
	}

	public static IIcon[][]	icons;

	public static void onRegisterIcons(IIconRegister iconRegister) {
		IIcon pivotCCW = Registry.RegisterIcon(iconRegister, "RotatorArrowCCW");
		IIcon pivotCW = new IconFlipped(pivotCCW, true, false);
		IIcon arrow = Registry.RegisterIcon(iconRegister, "RotatorArrowUp");
		icons = new IIcon[][] { { pivotCW, pivotCCW, arrow, arrow, arrow, arrow },
				{ pivotCCW, pivotCW, arrow, arrow, arrow, arrow }, { arrow, arrow, pivotCW, pivotCCW, arrow, arrow },
				{ arrow, arrow, pivotCCW, pivotCW, arrow, arrow }, { arrow, arrow, arrow, arrow, pivotCW, pivotCCW },
				{ arrow, arrow, arrow, arrow, pivotCCW, pivotCW }, };
	}

	public void setAxis(int axis) {
		axisOfRotationIndex = axis;
	}

	public boolean drawSideClosed(int side) {
		return super.isSideClosed(side);
	}

	@Override
	public void updateEntity() {
		if (!(CarriageDirection != null && (CarriageDirection.ordinal() == axisOfRotationIndex || CarriageDirection.Opposite == axisOfRotationIndex))) {
			CarriageDirection = null;
		}
		super.updateEntity();
	}

	@Override
	public void rotateSpecial(ForgeDirection axis) {
		super.rotateSpecial(axis);
		axisOfRotationIndex = Rotator.newSide(axisOfRotationIndex, axis);
	}

	@Override
	public void onAdded(CarriagePackage pkg, NBTTagCompound tag)
			throws CarriageMotionException {

		// icky hack to stop adding already-added adaptors
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		if (e[10].getClassName().equals(e[12].getClassName())) { return; }

		HandleNeighbourBlockChange();
		BlockRecord record = new BlockRecord(this);
		pkg.AddBlock(record);
		Debug.dbg("Carriage at "+CarriageDirection);
		Debug.dbg("Closed "+Arrays.toString(SideClosed));
		if (!alreadyMoving && this.CarriageDirection != null && this.CarriageDirection != Directions.Null) {
			alreadyMoving = true;
			if (CarriageDirection != null) {
				BlockRecord oldAnchor = pkg.AnchorRecord;
				pkg.AnchorRecord = new BlockRecord(xCoord + CarriageDirection.DeltaX,
						yCoord + CarriageDirection.DeltaY, zCoord + CarriageDirection.DeltaZ);
				pkg.AnchorRecord.Identify(worldObj);
				MultiTypeCarriageUtil.fillPackage(pkg, worldObj.getTileEntity(xCoord + CarriageDirection.DeltaX, yCoord
						+ CarriageDirection.DeltaY, zCoord + CarriageDirection.DeltaZ));
				pkg.AnchorRecord = oldAnchor;

			}
		}
	}

}
