package fr.clawara.lifesteal.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Data {
	
	private Class<?> clazz;
	
	public Data(Class<?> clazz) {
		this.clazz = clazz;
	} 
	
	public void del(UUID getter) {
		DataType type = DataType.getType(clazz);
		if(DataManager.exist(type, getter.toString())) {
			File f = DataManager.getFile(type, getter.toString());
			if(f != null) {
				f.delete();
			}
		}
	}
	
	public void send(String name, String content) {
		DataType type = DataType.getType(clazz);
		DataManager.updateOrCreateFile(type, name, content);
	}
	

	@SuppressWarnings("deprecation")
	public Object get(String getter) {
		DataType type = DataType.getType(clazz);
		if(DataManager.exist(type, getter)) {
			File f = DataManager.getFile(type, getter);
			try {
				return DataManager.getObject(type, Files.toString(f, Charsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/*public List<Object> getAll() {
		List<Object> objects = new ArrayList<>();
		DataType type = DataType.getType(clazz);
		File folder = DataManager.getFolder(type);
		for(File f : folder.listFiles()) {
			System.out.println(f.getName());
			objects.add(get(f.getName()));
		}
		return objects;
	}*/
	
    public List<Object> getAll() {
        DataType type = DataType.getType(clazz);
        ArrayList<Object> objs = new ArrayList<>();
        for(File f : DataManager.getFolder(type).listFiles()) {
            try {
    			System.out.println(f.getName());
                objs.add(DataManager.getObject(type, Files.toString(f, Charsets.UTF_8)));
            } catch (Exception e) {
    			System.out.println("Error: "+f.getName());
                e.printStackTrace();
            }
        }
        return objs;
    }

}
