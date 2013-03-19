package fr.eurecom.eventspotter.preprocessor;

/**
 Copyright 2007 Kyle Miller.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import fr.eurecom.eventspotter.preprocessor.CSVReader;
import fr.eurecom.eventspotter.preprocessor.MappingStrategy;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvToBean<T> {
    private Map<Class<?>, PropertyEditor> editorMap = null;

    public CsvToBean() {
    }

    public List<T> parse(MappingStrategy<T> mapper, Reader reader) {
        return parse(mapper, new CSVReader(reader));
    }

    public List<T> parse(MappingStrategy<T> mapper, CSVReader csv) {
        try {
            mapper.captureHeader(csv);
            String[] line;
            List<T> list = new ArrayList<T>();
            int count=1;
            while (null != (line = csv.readNext())) {
            	count++;
            	if(count==100)System.gc();
                T obj = processLine(mapper, line);
                list.add(obj); // TODO: (Kyle) null check object
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV!", e);
        }
    }

    protected T processLine(MappingStrategy<T> mapper, String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        T bean = mapper.createBean();
        for (int col = 0; col < line.length; col++) {
            PropertyDescriptor prop = mapper.findDescriptor(col);
            if (null != prop) {
                String value = checkForTrim(line[col], prop);
                Object obj = convertValue(value, prop);
                prop.getWriteMethod().invoke(bean, obj);
            }
        }
        return bean;
    }

    private String checkForTrim(String s, PropertyDescriptor prop) {
        return trimmableProperty(prop) ? s.trim() : s;
    }

    private boolean trimmableProperty(PropertyDescriptor prop) {
        return !prop.getPropertyType().getName().contains("String");
    }

    protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
        PropertyEditor editor = getPropertyEditor(prop);
        Object obj = value;
        if (null != editor) {
            editor.setAsText(value);
            obj = editor.getValue();
        }
        return obj;
    }

    private PropertyEditor getPropertyEditorValue(Class<?> cls) {
        if (editorMap == null) {
            editorMap = new HashMap<Class<?>, PropertyEditor>();
        }

        PropertyEditor editor = editorMap.get(cls);

        if (editor == null) {
            editor = PropertyEditorManager.findEditor(cls);
            addEditorToMap(cls, editor);
        }

        return editor;
    }

    private void addEditorToMap(Class<?> cls, PropertyEditor editor) {
        if (editor != null) {
            editorMap.put(cls, editor);
        }
    }


    /*
     * Attempt to find custom property editor on descriptor first, else try the propery editor manager.
     */
    protected PropertyEditor getPropertyEditor(PropertyDescriptor desc) throws InstantiationException, IllegalAccessException {
        Class<?> cls = desc.getPropertyEditorClass();
        if (null != cls) return (PropertyEditor) cls.newInstance();
        return getPropertyEditorValue(desc.getPropertyType());
    }
   
    public List<Event> myparse(String CSV)
	{
		
		List<Event> Events= new ArrayList<Event>();
		int count=0;
		String a=null,b=null,c=null,d=null,e=null,f=null,g=null,h =null;
		//while(CSV!=null)
		//{
			//System.out.println("here");
			String[] lines = new String[5000];
		    lines = CSV.split("\n");

			for(String line : lines)
			{
				if(line == null)break;
				//System.out.println(line);
				String[] parts = new String[10];

			    parts = line.split(",");
				//System.out.println(parts[2]);
			    
            	count++;
            	if(count==100)System.gc();
				int i = 1;
				for(String part : parts)
				{		
					switch(i)
					{
						case 1: a=part;  break;
						case 2: b=part;  break;
						case 3: c=part;  break;
						case 4: d=part;  break;
						case 5: e=part;  break;
						case 6: f=part;  break;
						case 7: g=part;  break;
						case 8: h=part;  break;				
					}
					i++;
				}
				
				Event obj = new Event(a,b,c,d,e,f,g,h);
				
				//Event obj = new Event(parts[0],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8]);
				Events.add(obj);
				
			}
		//}
		return Events;
		
	}
    /*
    public List<Event> myparse(String CSV)
	{
    	String[] line;
		List<Event> Events= new ArrayList<Event>();
		while (null != (line = csv.readNext()))
		{
			
		}
	}
*/
}
