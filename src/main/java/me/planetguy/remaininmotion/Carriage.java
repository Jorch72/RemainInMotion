package me.planetguy.remaininmotion ;

import net.minecraft.util.IIcon;

public class Carriage extends Block
{
	public Carriage ( )
	{
		super ( Configuration . BlockIds . Carriage , wood , CarriageItem . class , HarvestToolTypes . Hatchet ,
			FrameCarriageEntity . class , PlatformCarriageEntity . class , StructureCarriageEntity . class , SupportCarriageEntity . class , TemplateCarriageEntity . class ) ;
	}

	public enum Types
	{
		Frame ,
		Platform ,
		Structure ,
		Support ,
		Template ;

		public double Burden ;

		public IIcon OpenIcon ;
		public IIcon ClosedIcon ;
	}

	public enum Tiers
	{
		foo ;

		double CarriageBurdenFactor ;

		double CargoBurdenFactor ;
	}

	public static IIcon PlaceholderIcon ;

	@Override
	public void AddShowcaseStacks ( java . util . List Showcase )
	{
		for ( Types Type : Types . values ( ) )
		{
			Showcase . add ( CarriageItem . Stack ( Type . ordinal ( ) , 0 ) ) ;
		}
	}

	@Override
	public void registerIcons ( net . minecraft . client . renderer . texture . IconRegister IconRegister )
	{
		for ( Types Type : Types . values ( ) )
		{
			Type . ClosedIcon = Registry . RegisterIcon ( IconRegister , Type . name ( ) + "Carriage_Closed" ) ;

			Type . OpenIcon = Registry . RegisterIcon ( IconRegister , Type . name ( ) + "Carriage_Open" ) ;
		}

		PlaceholderIcon = Registry . RegisterIcon ( IconRegister , "CarriagePlaceholder" ) ;
	}

	@Override
	public IIcon getIcon ( int Side , int Meta )
	{
		try
		{
			return ( Types . values ( ) [ Meta ] . OpenIcon ) ;
		}
		catch ( Throwable Throwable )
		{
			Throwable . printStackTrace ( ) ;

			return ( Blocks . Spectre . getIcon ( 0 , 0 ) ) ;
		}
	}

	@Override
	public IIcon getBlockTexture ( net . minecraft . world . IBlockAccess World , int X , int Y , int Z , int Side )
	{
		try
		{
			CarriageEntity Carriage = ( CarriageEntity ) World . getTileEntity ( X , Y , Z ) ;

			if ( ( Carriage . DecorationId != 0 ) && ( Carriage . SideClosed [ Side ] ) )
			{
				return ( net . minecraft . block . Block . blocksList [ Carriage . DecorationId ] . getIcon ( Side , Carriage . DecorationMeta ) ) ;
			}

			Types Type = Types . values ( ) [ World . getBlockMetadata ( X , Y , Z ) ] ;

			return ( Carriage . SideClosed [ Side ] ? Type . ClosedIcon : Type . OpenIcon ) ;
		}
		catch ( Throwable Throwable )
		{
			Throwable . printStackTrace ( ) ;

			return ( Blocks . Spectre . getIcon ( 0 , 0 ) ) ;
		}
	}

	@Override
	public boolean onBlockActivated ( net . minecraft . world . World World , int X , int Y , int Z , net . minecraft . entity . player . EntityPlayer Player , int Side , float HitX , float HitY , float HitZ )
	{
		if ( World . isRemote )
		{
			return ( false ) ;
		}

		if ( ! ToolItemSet . IsScrewdriverOrEquivalent ( Player . inventory . getCurrentItem ( ) ) )
		{
			return ( false ) ;
		}

		try
		{
			( ( CarriageEntity ) World . getTileEntity ( X , Y , Z ) ) . ToggleSide ( Side , Player . isSneaking ( ) ) ;
		}
		catch ( Throwable Throwable )
		{
			Throwable . printStackTrace ( ) ;

			return ( false ) ;
		}

		return ( true ) ;
	}

	@Override
	public boolean isOpaqueCube ( )
	{
		return ( false ) ;
	}
}
