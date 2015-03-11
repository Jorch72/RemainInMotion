package me.planetguy.remaininmotion.base;

import me.planetguy.remaininmotion.core.RiMConfiguration;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockCamouflageable extends BlockRiM implements ICamouflageable {

    public BlockCamouflageable(Block Template, Class<? extends ItemBlockRiM> BlockItemClass,
                               Class<? extends TileEntityRiM>... TileEntityList) {
        super(Template, BlockItemClass, TileEntityList);

    }

    @Override
    public IIcon getIconCamouflaged(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityCamouflageable) {
        	try {
        		// Try/catch this - it's not unlikely that mods will do an unsafe cast to
        		// their mod's tile entity here.
        		//
        		// included for compatibility with blocks that use position-sensitive but
        		// not TE-sensitive icon logic, like applying biome colour
                if(((TileEntityCamouflageable) te).DecorationMeta != 0) throw new Exception("This method will almost always attempt to get metadata from world, resulting in the wrong texture.");
                return ((TileEntityCamouflageable) te).Decoration.getIcon
                		(world, x, y, z, side);
        	} catch(Exception e) {
                return ((TileEntityCamouflageable) te).Decoration.getIcon(side,
                        ((TileEntityCamouflageable) te).DecorationMeta);
        	}

        } else {
            return null;
        }
    }

    /**
     * Gets the light value of the specified block coords. Args: x, y, z
     */
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityCamouflageable) {
            Block deco = ((TileEntityCamouflageable) te).Decoration;
            if(deco != null)
            {
                return ((Block) deco).getLightValue();
            }
        }
        return super.getLightValue();

    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityCamouflageable) {
            Block deco = ((TileEntityCamouflageable) te).Decoration;
            if(deco != null)
            {
                return ((Block) deco).getLightOpacity();
            }
        }
        return super.getLightOpacity();
    }

    // Make grass and stuff work.
    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        if(!RiMConfiguration.DirtyHacks.experimentalColor) return super.colorMultiplier(world, x, y, z);
        TileEntity te = (TileEntity) world.getTileEntity(x,y,z);

        if (te != null && te instanceof TileEntityCamouflageable) {

            Block deco = ((TileEntityCamouflageable) te).Decoration;
            if(deco != null)
            {
                try {
                    return ((Block) deco).colorMultiplier(world, x, y, z);
                }catch(Exception e){
                    return 0xffffffff;
                }
            }
        }
        return super.colorMultiplier(world, x, y, z);
    }
}
