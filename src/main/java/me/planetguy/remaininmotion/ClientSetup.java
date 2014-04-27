package me.planetguy.remaininmotion ;

<<<<<<< HEAD:src/me/planetguy/remaininmotion/ClientSetup.java
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.microblock.MicroblockClass;
import codechicken.microblock.MicroblockClassRegistry;
import codechicken.microblock.MicroMaterialRegistry.IMicroMaterial;
import codechicken.multipart.JItemMultiPart;
=======
import me.planetguy.remaininmotion.base.TileEntity;
>>>>>>> 98ce934fa8adaa9996508250d82fa78ffb003353:src/main/java/me/planetguy/remaininmotion/ClientSetup.java

public class ClientSetup extends ClientSetupProxy
{
	public void RegisterTileEntityRenderer ( TileEntityRenderer Renderer , Class < ? extends TileEntity > ... EntityClasses )
	{
		for ( Class < ? extends TileEntity > EntityClass : EntityClasses )
		{
			cpw . mods . fml . client . registry . ClientRegistry . bindTileEntitySpecialRenderer ( EntityClass , Renderer ) ;
		}
	}

	@Override
	public void Execute ( )
	{
		RegisterTileEntityRenderer ( new MotiveSpectreRenderer ( ) , MotiveSpectreEntity . class ) ;

		RegisterTileEntityRenderer ( new TeleportativeSpectreRenderer ( ) , TeleportativeSpectreEntity . class ) ;

		new CarriageRenderer ( ) ;

		new CarriageDriveRenderer ( ) ;
		
		/*
		IItemRenderer renderer=new IItemRenderer(){

			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type) {
				return true;
			}

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type,
					ItemStack item, ItemRendererHelper helper) {
				return false;
			}

			@Override
			public void renderItem(ItemRenderType t, ItemStack item,
					Object... data) {
				GL11.glPushMatrix();
				if(t == ItemRenderType.ENTITY)
					GL11.glScaled(0.5, 0.5, 0.5);
				if(t == ItemRenderType.INVENTORY || t == ItemRenderType.ENTITY)
					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

				IMicroMaterial material = MicroMaterialRegistry.getMaterial("planks");
				MicroblockClass mcrClass = MicroblockClassRegistry.getMicroClass(item.getItemDamage());
				if(material==null || mcrClass == null)
					return;

				CCRenderState.reset();
				TextureUtils.bindAtlas(0);
				CCRenderState.useNormals(true);
				CCRenderState.useModelColours(true);
				CCRenderState.pullLightmap();
				CCRenderState.startDrawing(7);
				FMPCarriage part=(FMPCarriage) Items.hollowCarriage.newPart(item, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.worldObj, null, 0, null);
				part.renderStatic(new Vector3(0.5, 0.5, 0.5).subtract(part.getBounds().center()), null, 0);
				CCRenderState.draw();
				GL11.glPopMatrix();			
			}

		};
		
        MinecraftForgeClient.registerItemRenderer(Items.hollowCarriageId, renderer);
        */
	}
}