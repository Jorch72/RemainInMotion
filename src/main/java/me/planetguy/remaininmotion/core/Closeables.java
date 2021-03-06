package me.planetguy.remaininmotion.core;

import java.util.ArrayList;

import me.planetguy.remaininmotion.api.ICloseable;
import me.planetguy.remaininmotion.api.ICloseableFactory;
import net.minecraft.tileentity.TileEntity;

public class Closeables {

	private static class ClassEntry {
		Class				targetClass;
		ICloseableFactory	icf;

		public boolean matches(Object o) {
			return o != null && targetClass.isAssignableFrom(o.getClass());
		}
	}

	static ArrayList<ClassEntry>	classesRegistered	= new ArrayList<ClassEntry>();

	public static void register(ICloseableFactory closeable) {
		ClassEntry e = new ClassEntry();
		e.targetClass = closeable.validClass();
		e.icf = closeable;
		classesRegistered.add(e);
	}

	public static ICloseable getCloseable(TileEntity entity) {
		if (entity != null) {
			for (ClassEntry e : classesRegistered) {
				if (e.matches(entity)) { 
					ICloseable closeable=e.icf.retrieve(entity); 
					if(closeable != null)
						return closeable;
				}
			}
		}
		return null;
	}

}
